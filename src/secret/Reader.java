package secret;


import java.util.Arrays;

public class Reader {

    private final String key;
    private final String imagePath;
    private final String outputPath;
    private final FileHelper fileHelper;
    private final ByteHelper byteHelper;

    public Reader(String key, String imagePath, String outputPath){

        this.key = key;
        this.imagePath = imagePath;
        this.outputPath = outputPath;

        fileHelper = new FileHelper();
        byteHelper = new ByteHelper();

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

        //debug
        System.out.println(Arrays.toString(messageBytes));
        String test = new String(messageBytes);
        System.out.print(test);
    }

}
