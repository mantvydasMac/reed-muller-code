import java.util.BitSet;

public class BitVector {
    private final BitSet bits;
    private int length;

    public BitVector() {
        this.bits = new BitSet();
        this.length = 0;
    }

    public BitVector(int length) {
        this.bits = new BitSet(length);
        this.length = length;
    }

    public BitVector(int[] vector) {
        this.bits = new BitSet();
        this.length = 0;

        for (int v : vector) {
            add(v != 0);
        }
    }

    public boolean get(int i) {
        return bits.get(i);
    }

    public void set(int i, boolean value) {
        if(i >= length) {
            length = i+1;
        }
        bits.set(i, value);
    }

    public void add(boolean value) {
        bits.set(length++, value);
    }

    public int length() {
        return length;
    }

    public BitSet raw() {
        return bits;
    }

    public void print() {
        for(int i = 0; i < length; i++) {
            System.out.print(bits.get(i) ? "1 " : "0 ");
        }
    }

    /// Sujungia du bitų vektorius
    /// a, b - jungiami vektoriai
    /// Gražina:
    /// Naują bitų vektorių, kurio reikšmės yra prie a prijungta b
    public static BitVector concatenate(BitVector a, BitVector b) {
        BitVector c = new BitVector(a.length() + b.length());

        boolean aLarger = a.length() > b.length();

        for(int i = 0; i < (aLarger ? a.length() : b.length()); i++) {
            if(i < a.length()) {
                c.set(i, a.get(i));
            }
            if(i < b.length()) {
                c.set(i + a.length(), b.get(i));
            }
        }

        return c;
    }
}
