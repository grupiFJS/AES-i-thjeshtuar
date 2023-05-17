public class CBC implements Crypto {

    BinaryWords bw;
    SimplifiedAES aes;
    String IV;

    public CBC(BinaryWords bw, SimplifiedAES aes, String IV) {
        this.bw = bw;
        this.aes = aes;
        this.IV = bw.toBinary(IV);
    }

    public Pair<String, String> encrypt(String plainText, String secretKey) {
        String binaryText = bw.toBinary(plainText);
        String key = bw.toBinary(secretKey);
        
        String block = binaryText.substring(0, 16);
        block = xor(block, IV);
        Pair<String, String> encrypted = aes.AES16(block, key);
        String encryptedBinaryText = encrypted.first;
        for (int i = 1; i < binaryText.length() / 16; i++) {
            encryptedBinaryText += aes.AES16(xor(encryptedBinaryText.substring((i-1)*16, i*16), binaryText.substring(i * 16, (i + 1) * 16)), key).first;
        }
        encrypted.first = bw.toString(encryptedBinaryText);
        encrypted.second = bw.toString(encrypted.second);
        return encrypted;
    }

    public Pair<String, String> decrypt(String cypherText, String secretKey) {
        String binaryText = bw.toBinary(cypherText);
        String key = bw.toBinary(secretKey);

        String block = binaryText.substring(0, 16);
        Pair<String, String> decrypted = aes.invAES16(block, key);
        block = decrypted.first;
        block = xor(block, IV);
        String decryptedBinaryText = block;
        for (int i = 1; i < binaryText.length() / 16; i++) {
            decryptedBinaryText += xor(binaryText.substring((i-1)*16, i*16), aes.invAES16(binaryText.substring(i * 16, (i + 1) * 16), key).first);
        }
        decrypted.first = bw.toString(decryptedBinaryText);
        decrypted.second = bw.toString(decrypted.second);
        return decrypted;
    }

    private String xor(String a, String b){
        int x = Integer.parseInt(a,2);
        int y = Integer.parseInt(b,2);
        String result = Integer.toBinaryString(x^y);
        while(result.length()<16){
            result = "0" + result;
        }
        return result;
    }

}