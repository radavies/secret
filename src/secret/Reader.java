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
        byte[] imageBytes = fileHelper.getImageData(imagePath);

        if (imageBytes == null || imageBytes.length < 1) {
            throw new RuntimeException("Image file not found or image was empty.");
        }

        ImageInformation imageInformation = byteHelper.getImageInformation(imageBytes);

        byte[] messageBytes = byteHelper.readMessageFromImage(imageBytes, imageInformation);
        //TODO: add error handling around the crypto
        String message = encryption.decrypt(messageBytes, key);

        //TODO: write the message to file

        //debug
        System.out.print(message);
    }

}
