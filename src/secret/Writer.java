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

            String message = fileHelper.getMessage(messagePath);
            byte[] messageBytes = encryption.encrypt(message, key);

            if(messageBytes == null){
                throw new RuntimeException("Could not encrypt the message.");
            }

            byte[] imageBytes = fileHelper.getImageData(imagePath);

            if (imageBytes == null || imageBytes.length < 1) {
                throw new RuntimeException("Image file not found or image was empty.");
            }

            ImageInformation imageInformation = byteHelper.getImageInformation(imageBytes);

            if (!byteHelper.isThereSpaceToHideTheMessage(messageBytes, imageInformation)) {
                throw new RuntimeException("Image file not large enough to contain message.");
            }

            byte[] outputBytes = byteHelper.hideMessageInImage(messageBytes, imageBytes, imageInformation);

            if(!fileHelper.writeBytesToFile(outputBytes, outputPath)){
                throw new RuntimeException("Error writing the output image file.");
            }


        } catch (Exception ex) {
            System.out.println("----------");
            System.out.println(String.format("Something went wrong - %s", ex.getMessage()));
            System.out.println("----------");
        }

    }
}
