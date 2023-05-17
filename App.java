import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) {
        BinaryWords bw = new BinaryWords();
        SimplifiedAES aes = new SimplifiedAES();
        // Crypto c = new NoBlockCypher(bw, aes);
        Crypto c = new CBC(bw, aes, "ef");

        Path plainTextPath = Paths.get("output", "plainText.txt");
        Path cypherPath = Paths.get("output", "cypherText.txt");
        Path decypherPath = Paths.get("output", "decrypherText.txt");
        String secretKey = "vg";

        Charset encoding = StandardCharsets.UTF_8;
        String textString = "";
        try {
            textString = new String(Files.readAllBytes(plainTextPath), encoding);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        Pair<String, String> encrypted = c.encrypt(textString, secretKey);
        System.out.println("Encrypted text: \n" + encrypted.first + "\n");
        try {
            Files.writeString(cypherPath, encrypted.first, encoding);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        Pair<String, String> decrypted = c.decrypt(encrypted.first, encrypted.second);
        System.out.println("Decrypted text: \n" + decrypted.first + "\n");
        try {
            Files.writeString(decypherPath, decrypted.first, encoding);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }
}