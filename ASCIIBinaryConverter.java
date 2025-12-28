

public class ASCIIBinaryConverter {

    /// Paverčia ASCII formato teksto eilutę į bitų vektorių
    /// text - verčiama teksto eilutė
    /// Gražina:
    /// Bitų vektorių, kuris yra paversta text eilutė pagal ASCII kodavimą
    public static BitVector convertASCIIToBinary(String text) {
        BitVector bits = new BitVector();

        for (char c : text.toCharArray()) {
            for (int b = 7; b >= 0; b--) {
                bits.add(((c >> b) & 1) == 1);
            }
        }

        return bits;
    }

    /// Paverčia bitų vektorių, gautą po dekodavimo į ASCII formato teksto eilutę
    /// binary - dekoduotas bitų vektorius
    /// Gražina:
    /// Teksto eilutę
    public static String convertBinaryToASCII(BitVector binary) {
        StringBuilder output = new StringBuilder();

        int stringLength = binary.length() / 8;
        byte value;
        for(int i = 0; i < stringLength; i++){
            value = 0;
            for(int j = 0; j < 8; j++){
                if (binary.get(i * 8 + j)) {
                    value |= (byte) (1 << (7 - j));
                }
            }
            output.append((char) value);
        }

        return output.toString();
    }
}
