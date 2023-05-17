public class NoBlockCypher implements Crypto {

    BinaryWords bw;
    SimplifiedAES aes;

    public NoBlockCypher(BinaryWords bw, SimplifiedAES aes){
        this.bw = bw;
        this.aes = aes;
    }

    public Pair<String, String> encrypt(String plainText, String secretKey) {
        String binaryText = bw.toBinary(plainText);
        String key = bw.toBinary(secretKey);
        Pair<String, String> encrypted = aes.AES16(binaryText.substring(0, 16), key);
        String encryptedBinaryText = "";
        for (int i = 0; i < binaryText.length()/16; i++) {
            encryptedBinaryText += aes.AES16(binaryText.substring(i*16, (i+1)*16), key).first;
        }
        encrypted.first = bw.toString(encryptedBinaryText);
        encrypted.second = bw.toString(encrypted.second);
        return encrypted;
    }

    public Pair<String, String> decrypt(String cypherText, String secretKey) {
        String binaryText = bw.toBinary(cypherText);
        String key = bw.toBinary(secretKey);
        Pair<String, String> decrypted = aes.invAES16(binaryText.substring(0, 16), key);
        String decryptedBinaryText = "";
        for (int i = 0; i < binaryText.length()/16; i++) {
            decryptedBinaryText += aes.invAES16(binaryText.substring(i*16, (i+1)*16), key).first;
        }
        decrypted.first = bw.toString(decryptedBinaryText);
        decrypted.second = bw.toString(decrypted.second);
        return decrypted;
    }
}