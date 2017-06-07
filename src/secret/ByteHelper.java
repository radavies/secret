package secret;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class ByteHelper {


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

        //write the message length
        byte[] messageLengthBytes = ByteBuffer.allocate(4).putInt(messageBytes.length).array();

        for(byte lengthByte : messageLengthBytes) {
            int[] newPositions = writeByteIntoOutput(lengthByte, output, outputByteCounter, outputBitCounter, imageInformation);
            outputByteCounter = newPositions[0];
            outputBitCounter = newPositions[1];
        }

        //write the message
        for (byte bite : messageBytes) {
            int[] newPositions = writeByteIntoOutput(bite, output, outputByteCounter, outputBitCounter, imageInformation);
            outputByteCounter = newPositions[0];
            outputBitCounter = newPositions[1];
        }

        return output;
    }

    public byte[] readMessageFromImage(byte[] imageBytes, ImageInformation imageInformation){

        ArrayList<Byte> messageStorage = new ArrayList<>();

        int imageByteCounter = imageInformation.getEndLocation() -1;
        int imageBitCounter = 0;

        //read the message length
        byte[] messageLengthBytes = new byte[4];
        for(int lengthByteCounter = 0; lengthByteCounter < 4; lengthByteCounter++){

            ByteReadPosition byteReadPosition = readByte(imageBytes,imageInformation, imageByteCounter, imageBitCounter);
            imageByteCounter = byteReadPosition.imageBytePosition;
            imageBitCounter = byteReadPosition.imageBitPosition;
            messageLengthBytes[lengthByteCounter] = byteReadPosition.readByte;
        }

        int messageLength = convertMessageLengthByteArrayToInt(messageLengthBytes);

        //read the message
        while(messageStorage.size() < messageLength){
            ByteReadPosition byteReadPosition = readByte(imageBytes,imageInformation, imageByteCounter, imageBitCounter);
            imageByteCounter = byteReadPosition.imageBytePosition;
            imageBitCounter = byteReadPosition.imageBitPosition;
            messageStorage.add(byteReadPosition.readByte);
        }

        //convert to byte array
        Byte[] bigByteStorage = new Byte[messageStorage.size()];
        bigByteStorage = messageStorage.toArray(bigByteStorage);
        byte[] output = new byte[messageStorage.size()];

        for(int counter = 0; counter < bigByteStorage.length; counter++){
            output[counter] = bigByteStorage[counter];
        }

        return output;
    }

    public byte[] convertMessageToBytes(String message) {
        return message.getBytes();
    }

    public boolean isThereSpaceToHideTheMessage(byte[] messageBytes, ImageInformation imageInformation) {
        return getNumberOfBytesRequiredToHideMessage(messageBytes) < imageInformation.availableBytes();
    }

    private ByteReadPosition readByte(byte[] imageBytes, ImageInformation imageInformation, int imageByteCounter, int imageBitCounter){

        byte outputByte = 0x00;
        byte imageByte = imageBytes[imageByteCounter];

        for (int counter = 0; counter <= 7; counter++) {
            if(isBitSet(imageByte, imageBitCounter)){
                //set a bit in the new byte
                outputByte = (byte) (outputByte | (1 << counter));
            }

            //get a new byte from the image if we need to
            if (imageBitCounter == 1) {
                imageByteCounter = getNextAvailableImageByte(imageInformation, imageByteCounter);
                imageByte = imageBytes[imageByteCounter];
                imageBitCounter = 0;
            }
            else {
                imageBitCounter++;
            }
        }

        return new ByteReadPosition(outputByte, imageByteCounter, imageBitCounter);
    }


    private int convertMessageLengthByteArrayToInt(byte[] messageLengthBytes){
        int value = (messageLengthBytes[0] << (Byte.SIZE * 3));
        value |= (messageLengthBytes[1] & 0xFF) << (Byte.SIZE * 2);
        value |= (messageLengthBytes[2] & 0xFF) << (Byte.SIZE);
        value |= (messageLengthBytes[3] & 0xFF);
        return value;
    }

    private int[] writeByteIntoOutput(byte byteToWrite, byte[] output,
                                      int outputByteCounter, int outputBitCounter,
                                      ImageInformation imageInformation){

        for (int counter = 0; counter <= 7; counter++) {
            if (isBitSet(byteToWrite, counter)) {
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

            if (outputBitCounter == 1) {
                outputByteCounter = getNextAvailableImageByte(imageInformation, outputByteCounter);
                outputBitCounter = 0;
            }
            else {
                outputBitCounter++;
            }
        }
        return new int[]{outputByteCounter, outputBitCounter};
    }

    private int getNextAvailableImageByte(ImageInformation imageInformation, int currentByteLocation){
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

    private int getNumberOfBytesRequiredToHideMessage(byte[] messageBytes) {
        //x4 as each byte will require 4 to hide it
        //+4 as we need 4 bytes to store the message length (32bit int)
        return (messageBytes.length * 4) + 4;
    }
}
