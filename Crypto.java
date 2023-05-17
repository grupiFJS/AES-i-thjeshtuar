public interface Crypto {

    public Pair<String, String> encrypt(String plainText, String secretKey);

    public Pair<String, String> decrypt(String cypherText, String secretKey);
     
}
