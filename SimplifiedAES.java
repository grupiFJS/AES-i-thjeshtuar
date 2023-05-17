public class SimplifiedAES {

    private final String[][] SBOX = {
            { "0110", "1011", "0000", "0100" },
            { "0111", "1110", "0010", "1111" },
            { "1001", "1000", "1010", "1100" },
            { "0011", "0001", "0101", "1101" }
    };

    private final String[][] invSBOX = {
            { "0010", "1101", "0110", "1100" },
            { "0011", "1110", "0000", "0100" },
            { "1001", "1000", "1010", "0001" },
            { "1011", "1111", "0101", "0111" }
    };

    private final String[][] MDSM = {
            { "0001", "0001" },
            { "0001", "0010" }
    };

    private final String[] RC = { "0001", "0010", "0100" };

    private final String[][] invMDSM = {
            { "1111", "1110" },
            { "1110", "1110" }
    };

    public Pair<String, String> AES16(String bits, String key) {
        String[][] P = makeMatrix(bits);
        String[][] K = makeMatrix(key);
        P = addRoundKey(P, K);
        for (int i = 0; i < 3; i++) {
            sBox(P);
            shiftRows(P);
            if (i != 2) {
                P = mixColumns(P);
            }
            K = roundKey(K, i);
            P = addRoundKey(P, K);
        }
        String cypherText = P[0][0] + P[1][0] + P[0][1] + P[1][1];
        String lastKey = K[0][0] + K[1][0] + K[0][1] + K[1][1];
        return new Pair<String, String>(cypherText, lastKey);
    }

    public Pair<String, String> invAES16(String bits, String key) {
        String[][] P = makeMatrix(bits);
        String[][] K = makeMatrix(key);
        for (int i = 0; i < 3; i++) {
            P = addRoundKey(P, K);
            if (i != 0) {
                P = invMixColumns(P);
            }
            shiftRows(P);
            invSBox(P);
            K = invRoundKey(K, 2 - i);
        }
        P = addRoundKey(P, K);
        String plainText = P[0][0] + P[1][0] + P[0][1] + P[1][1];
        String firstKey = K[0][0] + K[1][0] + K[0][1] + K[1][1];
        return new Pair<String, String>(plainText, firstKey);
    }

    public String[][] makeMatrix(String s) {
        String[][] R = {
                { s.substring(0, 4), s.substring(8, 12) },
                { s.substring(4, 8), s.substring(12, 16) }
        };
        return R;
    }

    public String[][] addRoundKey(String[][] A, String[][] B) {
        String[][] R = new String[A.length][A[0].length];
        for (int i = 0; i < R.length; i++) {
            for (int j = 0; j < R.length; j++) {
                String x = xor(A[i][j], B[i][j]);
                R[i][j] = x;
            }
        }
        return R;
    }

    public void sBox(String[][] P) {
        for (int i = 0; i < P.length; i++) {
            for (int j = 0; j < P.length; j++) {
                int row = Integer.parseInt(P[i][j].substring(0, 2), 2);
                int column = Integer.parseInt(P[i][j].substring(2, 4), 2);
                P[i][j] = SBOX[row][column];
            }
        }
    }

    public void shiftRows(String[][] P) {
        String temp = P[1][1];
        P[1][1] = P[1][0];
        P[1][0] = temp;
    }

    // Addition and xor are the same thing in GF(2^n).
    public String xor(String a, String b) {
        int ai = Integer.parseInt(a, 2);
        int bi = Integer.parseInt(b, 2);
        String r = Integer.toBinaryString(ai ^ bi);
        while (r.length() < 4) {
            r = "0" + r;
        }
        return r;
    }

    public int binToInt(String bits) {
        return Integer.parseInt(bits, 2);
    }

    // A method to multiply two polynomials in GF(2^4).
    public static String multiply(String a, String b) {
        int A = Integer.parseInt(a, 2);
        int B = Integer.parseInt(b, 2);
        int result = 0;
        for (int i = 0; i < 4; i++) {
            if ((B & (1 << i)) != 0) {
                result ^= A << i;
            }
        }
        for (int i = 6; i >= 4; i--) {
            if ((result & (1 << i)) != 0) {
                result ^= 0b10011 << (i - 4);
            }
        }
        String output = Integer.toBinaryString(result);
        while (output.length() < 4) {
            output = "0" + output;
        }
        return output;
    }

    public String[][] mixColumns(String[][] P) {
        String[][] R = new String[P.length][P[0].length];
        for (int i = 0; i < P.length; i++) {
            for (int j = 0; j < MDSM[0].length; j++) {
                String m1 = multiply(MDSM[i][0], P[0][j]);
                String m2 = multiply(MDSM[i][1], P[1][j]);
                R[i][j] = xor(m1, m2);
            }
        }
        return R;
    }

    public String[][] roundKey(String[][] K, int round) {
        String[][] newK = new String[K.length][K[0].length];
        String ki = g(K[K.length - 1][K[0].length - 1], round);
        for (int i = 0; i < newK.length; i++) {
            for (int j = 0; j < newK[0].length; j++) {
                if (i == 0 && j == 0) {
                    newK[0][0] = xor(K[0][0], ki);
                    ki = newK[0][0];
                    continue;
                }
                newK[j][i] = xor(K[j][i], ki);
                ki = newK[j][i];
            }
        }
        return newK;
    }

    public String[][] invRoundKey(String[][] K, int round) {
        String[][] newK = new String[K.length][K[0].length];
        newK[1][1] = xor(K[1][1], K[0][1]);
        newK[0][1] = xor(K[0][1], K[1][0]);
        newK[1][0] = xor(K[1][0], K[0][0]);
        newK[0][0] = xor(K[0][0], g(newK[1][1], round));
        return newK;
    }

    public String g(String b, int round) {
        String s = b.substring(1, 4) + b.charAt(0);
        String x = SBOX[binToInt(s.substring(0, 2))][binToInt(s.substring(2, 4))];
        String r = xor(x, RC[round]);
        return r;
    }

    public String[][] invMixColumns(String[][] P) {
        String[][] R = new String[P.length][P[0].length];
        for (int i = 0; i < P.length; i++) {
            for (int j = 0; j < invMDSM[0].length; j++) {
                String m1 = multiply(invMDSM[i][0], P[0][j]);
                String m2 = multiply(invMDSM[i][1], P[1][j]);
                R[i][j] = xor(m1, m2);
            }
        }
        return R;
    }

    public void invSBox(String[][] P) {
        for (int i = 0; i < P.length; i++) {
            for (int j = 0; j < P.length; j++) {
                int row = Integer.parseInt(P[i][j].substring(0, 2), 2);
                int column = Integer.parseInt(P[i][j].substring(2, 4), 2);
                P[i][j] = invSBOX[row][column];
            }
        }
    }

    public static String encrypt(String mes) {
        SimplifiedAES simp = new SimplifiedAES();

        Pair<String, String> P = simp.AES16(mes, "0101010101010101");
        return P.first;
    }

    public static void main(String[] args) {
        System.out.println(encrypt("0101010101010101"));
    }
}