package secret;


import java.util.Arrays;

public class Reader {

    private final String key;
    private final String imagePath;
    private final String outputPath;
    private final FileHelper fileHelper;
    private final ByteHelper byteHelper;
    private final Encryption encryption;

    public Reader(String key, String imagePath, String outputPath){

        this.key = key;
        this.imagePath = imagePath;
        this.outputPath = outputPath;

        fileHelper = new FileHelper();
        byteHelper = new ByteHelper();
        encryption = new Encryption();

        if(this.key.equalsIgnoreCase("")
                || this.imagePath.equalsIgnoreCase("")
                || this.outputPath.equalsIgnoreCase("")){
            throw new IllegalArgumentException("Key, image path and output path must not be empty.");
        }
    }

    public void read(){

        try {

            byte[] imageBytes = fileHelper.getImageData(imagePath);

            if (imageBytes == null || imageBytes.length < 1) {
                throw new RuntimeException("Image file not found or image was empty.");
            }

            ImageInformation imageInformation = byteHelper.getImageInformation(imageBytes);

            byte[] messageBytes = byteHelper.readMessageFromImage(imageBytes, imageInformation);

            byte[] decryptedMessage = encryption.decrypt(messageBytes, key);

            if (decryptedMessage == null) {
                throw new RuntimeException("Could not decrypt the message.");
            }

            if (!fileHelper.writeBytesToFile(decryptedMessage, outputPath)) {
                throw new RuntimeException("Error writing the output message file.");
            }

        } catch (Exception ex) {
            System.out.println("----------");
            System.out.println(String.format("Something went wrong - %s", ex.getMessage()));
            System.out.println("----------");
        }
    }

}
