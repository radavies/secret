package secret;

public class Main {

    public static void main(String[] args) {

        String key = "";
        String imagePath = "";
        String messagePath = "";
        String outputPath = "";
        boolean write = true;

        for(String item : args) {
            String[] keyValueItem = item.split("=");
            if (keyValueItem[0].equalsIgnoreCase("key")) {
                key = keyValueItem[1];
            } else if (keyValueItem[0].equalsIgnoreCase("image")) {
                imagePath = keyValueItem[1];
            } else if (keyValueItem[0].equalsIgnoreCase("message")) {
                messagePath = keyValueItem[1];
            }
            else if (keyValueItem[0].equalsIgnoreCase("output")) {
                outputPath = keyValueItem[1];
            }
            else if (keyValueItem[0].equalsIgnoreCase("job")) {
                write = keyValueItem[1].equalsIgnoreCase("write");
            }
        }

        if(write){
            Writer writer = new Writer(key, imagePath, messagePath, outputPath);
            writer.write();
        }
        else{
            Reader reader = new Reader(key, imagePath, outputPath);
            reader.read();
        }
    }
}
