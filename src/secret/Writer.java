package secret;

import java.util.Arrays;

public class Writer {

    private final String key;
    private final String imagePath;
    private final String messagePath;
    private final String outputPath;
    private final FileHelper fileHelper;
    private final ByteHelper byteHelper;
    private final Encryption encryption;

    public Writer(String key, String imagePath, String messagePath, String outputPath){

        this.key = key;
        this.imagePath = imagePath;
        this.messagePath = messagePath;
        this.outputPath = outputPath;

        fileHelper = new FileHelper();
        byteHelper = new ByteHelper();
        encryption = new Encryption();

        if(this.key.equalsIgnoreCase("")
                || this.imagePath.equalsIgnoreCase("")
                || this.messagePath.equalsIgnoreCase("")
                || this.outputPath.equalsIgnoreCase("")){
            throw new IllegalArgumentException("Key, image path, output path and message path must not be empty.");
        }
    }

    public void write() {

        try {

            //TODO: add error handling around the crypto

            String message = fileHelper.getMessage(messagePath);
            byte[] messageBytes = encryption.encrypt(message, key);
            //byte[] messageBytes = byteHelper.convertMessageToBytes(message);

            //debug
            System.out.println(String.format("Message is %d bytes long.", messageBytes.length));
            System.out.println(Arrays.toString(messageBytes));
            String test = new String(messageBytes);
            System.out.print(test);

            byte[] imageBytes = fileHelper.getImageData(imagePath);

            if (imageBytes == null || imageBytes.length < 1) {
                throw new RuntimeException("Image file not found or image was empty.");
            }

            ImageInformation imageInformation = byteHelper.getImageInformation(imageBytes);

            if (!byteHelper.isThereSpaceToHideTheMessage(messageBytes, imageInformation)) {
                throw new RuntimeException("Image file not large enough to contain message.");
            }

            byteHelper.printImageMarkerLocationsJpg(imageBytes);

            byte[] outputBytes = byteHelper.hideMessageInImage(messageBytes, imageBytes, imageInformation);

            byteHelper.printImageLocationComparisonJpg(imageBytes, outputBytes);

            fileHelper.writeBytesToFile(outputBytes, outputPath);


        } catch (Exception ex) {
            System.out.println("----------");
            System.out.println(String.format("Something went wrong - %s", ex.getMessage()));
            System.out.println("----------");
        }

    }
}
