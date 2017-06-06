package secret;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class Encryption {

    public byte[] encrypt(String message, String key){
        try {

            //TODO: write a method that pads the key to 128 bits
            key = "Bar12345Bar12345"; // 128 bit key
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            System.out.println(new String(encrypted));
            return encrypted;
        }
        catch (Exception ex){
            return null;
        }
    }

    public String decrypt(byte[] message, String key){
        try {

            //TODO: write a method that pads the key to 128 bits
            key = "Bar12345Bar12345"; // 128 bit key
            // decrypt the text
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(message));
            System.out.println(decrypted);
            return decrypted;
        }
        catch (Exception ex){
            return null;
        }
    }

}
