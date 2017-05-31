package secret;

import java.awt.*;
import java.util.Arrays;

public class ByteHelper {

    //http://www.catonmat.net/blog/low-level-bit-hacks-you-absolutely-must-know/

    //byte b = 0x7;
    //System.out.println(b);
    //System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
    //b = (byte) (b | (1 << 6));
    //System.out.println(b);
    //System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
    //b = (byte) (b | (1 << 7));
    //System.out.println(b);
    //System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
    //b = (byte) (b & ~(1 << 0));
    //System.out.println(b);
    //System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
    //b = (byte) (b & ~(1 << 1));
    //System.out.println(b);
    //System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
    //System.out.println("----");

    public ImageInformation getImageInformation(byte[] imageBytes){
        ImageInformation imageInformation = new ImageInformation();

        byte previousByte = 0x00;
        boolean firstStartOfScan = true;

        for (int counter = 0; counter < imageBytes.length; counter++) {

            byte b = imageBytes[counter];

            if (previousByte == (byte) 0xFF && b == (byte) 0xDA) {
                //Start Of Scan
                if(firstStartOfScan){
                    //Ignore everything before this point
                    //+1 so the start is the first byte after the marker
                    imageInformation.setStartLocation(counter + 1);
                    firstStartOfScan = false;
                }else {
                    //Ignore this byte and the previous one
                    //which covers both bytes in the marker
                    imageInformation.addIgnoreLocation(counter - 1);
                    imageInformation.addIgnoreLocation(counter);
                }
            }
            else if (previousByte == (byte) 0xFF && b == (byte) 0xD9) {
                //EOI
                //Ignore everything after this point
                //including the marker (hence the -1)
                imageInformation.setEndLocation(counter-1);
            }

            previousByte = b;
        }

        return imageInformation;
    }

    public byte[] hideMessageInImage(byte[] messageBytes, byte[] imageBytes, ImageInformation imageInformation) {

        //We start at the end and work backward
        int outputByteCounter = imageInformation.getEndLocation() -1;
        int outputBitCounter = 0;

        byte[] output = Arrays.copyOf(imageBytes, imageBytes.length);

        for (byte bite : messageBytes) {
            for (int counter = 0; counter < 7; counter++) {
                if (isBitSet(bite, counter)) {
                    //set a bit in the output
                    byte outByte = output[outputByteCounter];
                    outByte = (byte) (outByte | (1 << outputBitCounter));
                    output[outputByteCounter] = outByte;

                } else {
                    //unset a bit in the output
                    byte outByte = output[outputByteCounter];
                    outByte = (byte) (outByte & ~(1 << outputBitCounter));
                    output[outputByteCounter] = outByte;
                }

                outputBitCounter++;
                if (outputBitCounter == 1) {
                    outputByteCounter = getNextAvailableOutputByte(imageInformation, outputByteCounter);
                    outputBitCounter = 0;
                }
            }
        }

        // Debug
        int differenceCount = 0;
        for (int x = 0; x < output.length; x++) {
            if (output[x] != imageBytes[x]) {
                System.out.println(String.format("Different - %d", x));
                differenceCount++;
            }

        }
        System.out.println(String.format("There was %d different bytes", differenceCount));

        return output;
    }

    private int getNextAvailableOutputByte(ImageInformation imageInformation, int currentByteLocation){
        //Start a the end and move backward
        currentByteLocation--;

        while(!imageInformation.isByteAvailable(currentByteLocation)){
            currentByteLocation--;
        }
        return currentByteLocation;
    }

    private boolean isBitSet(byte bite, int bitPosition) {
        return (bite & (1 << bitPosition)) != 0;
    }


    public byte[] convertMessageToBytes(String message) {
        return message.getBytes();
    }

    public boolean isThereSpaceToHideTheMessage(byte[] messageBytes, ImageInformation imageInformation) {
        return getNumberOfBytesRequiredToHideMessage(messageBytes) < imageInformation.availableBytes();
    }

    private int getNumberOfBytesRequiredToHideMessage(byte[] messageBytes) {
        return messageBytes.length * 4;
    }


    // DEBUG METHODS

    public void printImageMarkerLocationsJpg(byte[] imageBytes) {

        System.out.println(String.format("Image is %d bytes long", imageBytes.length));

        byte previousByte = 0x00;
        int counter = 0;
        int markersFound = 0;
        int onBytesFound = 0;

        for (byte b : imageBytes) {
            if (previousByte == (byte) 0xFF && b == (byte) 0xD8) {
                System.out.println("Start Of Image");
                System.out.println("Found at byte: " + counter);

                markersFound++;

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xC0) {
                System.out.println("Start Of Image (baseline DCT)");
                System.out.println("Found at byte: " + counter);

                markersFound++;

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xC2) {
                System.out.println("Start Of Image (progressive DCT)");
                System.out.println("Found at byte: " + counter);

                markersFound++;

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xDA) {
                System.out.println("Start Of Scan");
                System.out.println("Found at byte: " + counter);

                markersFound++;

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xE1) {
                System.out.println("End Of Scan");
                System.out.println("Found at byte: " + counter);

                markersFound++;

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xD9) {
                System.out.println("EOI");
                System.out.println("Found at byte: " + counter);

                markersFound++;
            }

            if (b == (byte) 0xFF) {
                onBytesFound++;
            }

            previousByte = b;
            counter++;
        }

        System.out.println(String.format("Found %d files markers: ", markersFound));
        System.out.println(String.format("Found %d on bytes: ", onBytesFound));
    }

//    public void getImageMarkerLocationsBmp(byte[] imageBytes) {
//
//        System.out.println(String.format("Image is %d bytes long", imageBytes.length));
//
//        byte previousByte = 0x00;
//        int counter = 0;
//        for (byte b : imageBytes) {
//            if (previousByte == (byte) 0xFF && b == (byte) 0xD8) {
//                System.out.println("Start Of Image");
//                System.out.println("Found at byte: " + counter);
//            } else if (previousByte == (byte) 0xFF && b == (byte) 0xC0) {
//                System.out.println("Start Of Image (baseline DCT)");
//                System.out.println("Found at byte: " + counter);
//            } else if (previousByte == (byte) 0xFF && b == (byte) 0xC2) {
//                System.out.println("Start Of Image (progressive DCT)");
//                System.out.println("Found at byte: " + counter);
//            }
//
//            previousByte = b;
//            counter++;
//        }
//    }

    public void printImageLocationComparisonJpg(byte[] imageBytes, byte[] outputBytes) {

        System.out.println(String.format("Image is %d bytes long", imageBytes.length));

        byte previousByte = 0x00;
        byte previousOutputByte = 0x00;

        for (int counter = 0; counter < imageBytes.length; counter++) {

            byte b = imageBytes[counter];
            byte ob = outputBytes[counter];

            if (previousByte == (byte) 0xFF && b == (byte) 0xD8) {
                System.out.println("Start Of Image");
                System.out.println("Found at byte: " + counter);

                if (previousOutputByte != (byte) 0xFF && ob != (byte) 0xD8) {
                    System.out.println("OUTPUT MARKER MISSING!");
                }

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xC0) {
                System.out.println("Start Of Image (baseline DCT)");
                System.out.println("Found at byte: " + counter);

                if (previousOutputByte != (byte) 0xFF && ob != (byte) 0xC0) {
                    System.out.println("OUTPUT MARKER MISSING!");
                }

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xC2) {
                System.out.println("Start Of Image (progressive DCT)");
                System.out.println("Found at byte: " + counter);

                if (previousOutputByte != (byte) 0xFF && ob != (byte) 0xC2) {
                    System.out.println("OUTPUT MARKER MISSING!");
                }

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xDA) {
                System.out.println("Start Of Scan");
                System.out.println("Found at byte: " + counter);

                if (previousOutputByte != (byte) 0xFF && ob != (byte) 0xDA) {
                    System.out.println("OUTPUT MARKER MISSING!");
                }

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xE1) {
                System.out.println("End Of Scan");
                System.out.println("Found at byte: " + counter);

                if (previousOutputByte != (byte) 0xFF && ob != (byte) 0xE1) {
                    System.out.println("OUTPUT MARKER MISSING!");
                }

            } else if (previousByte == (byte) 0xFF && b == (byte) 0xD9) {
                System.out.println("EOI");
                System.out.println("Found at byte: " + counter);

                if (previousOutputByte != (byte) 0xFF && ob != (byte) 0xD9) {
                    System.out.println("OUTPUT MARKER MISSING!");
                }

            }

            previousByte = b;
            previousOutputByte = ob;
        }
    }
}
