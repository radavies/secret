package secret;


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

    }

}
