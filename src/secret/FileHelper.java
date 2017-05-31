package secret;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileHelper {

    public byte[] getImageData(String imagePath){
        try {
            Path path = Paths.get(imagePath);
            return Files.readAllBytes(path);
        }
        catch (IOException ex){
            return null;
        }
    }

    public String getMessage(String messagePath){
        ArrayList<String> message = readMessageFile(messagePath);

        if(message == null || message.size() < 1){
            throw new RuntimeException("Message file not found or message was empty.");
        }

        int counter = 1;
        StringBuilder messageText = new StringBuilder();
        for(String line : message){
            messageText.append(line);

            if(counter < message.size()){
                messageText.append("\n\r");
            }

            counter++;
        }
        return messageText.toString();
    }

    public boolean writeBytesToFile(byte[] bytesToWrite, String filePath){
        try(FileOutputStream fileOutputStream = new FileOutputStream(filePath)){
            fileOutputStream.write(bytesToWrite);
            return true;
        }
        catch (IOException ex){
            return false;
        }

    }

    private ArrayList<String> readMessageFile(String messagePath) {
        ArrayList<String> builder = new ArrayList<>();
        Path path = Paths.get(messagePath);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.add(line);
            }
            return builder;
        }
        catch (IOException ex) {
            return null;
        }

    }

}
