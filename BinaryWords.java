class BinaryWords {
   
   public static String toBinary(String s){
      String binaryString = "";
      for (int i = 0; i < s.length(); i++) {
         int asciiValue = (int) s.charAt(i);
         String binaryValue = Integer.toBinaryString(asciiValue);
         while (binaryValue.length() < 8) {
            binaryValue = "0" + binaryValue;
         }
         binaryString += binaryValue;
      }
      while (binaryString.length() % 16 != 0) {
         binaryString = "0" + binaryString;
      }
      return binaryString;
   }

   public static String toString(String bits){
      String s = "";
      for (int i = 0; i < bits.length(); i += 8) {
         String eightBitChunk = bits.substring(i, i + 8);
         int decimalValue = Integer.parseInt(eightBitChunk, 2);
         s += (char) decimalValue;
      }
      return s;
   }
}