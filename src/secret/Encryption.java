package secret;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class Encryption {

    public byte[] encrypt(String message, String key){
        try {

            key = padOrTrimKey(key);

            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(message.getBytes());

            return encrypted;
        }
        catch (Exception ex){
            return null;
        }
    }

    public byte[] decrypt(byte[] message, String key){
        try {

            key = padOrTrimKey(key);

            // decrypt the text
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decrypted = cipher.doFinal(message);

            return decrypted;
        }
        catch (Exception ex){
            return null;
        }
    }

    private String padOrTrimKey(String key){
        if(key.length() > 16){
            key = key.substring(0, 16);
        }
        else if(key.length() < 16){
            int counter = 0;
            StringBuilder newKey = new StringBuilder();
            newKey.append(key);
            while (newKey.length() < 16){
                newKey.append(key.charAt(counter));
                counter++;

                if(counter >= key.length()){
                    counter = 0;
                }
            }
            key = newKey.toString();
        }
        return key;
    }

}
