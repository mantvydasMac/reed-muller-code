import Exceptions.MessageLengthIncorrectException;

public class FastDecoder {


    public int m;

    public FastDecoder(int m) {
        this.m = m;
    }

    /// Dekoduoja bet kokio ilgio užkoduotą žinutę
    /// encodedMessage - užkoduotos žinutės bitų vektorius
    /// Gražina:
    /// Bitų vektorių, kuris yra dekoduota encodedMessage žinutė
    public BitVector decodeMessage(BitVector encodedMessage) {
        BitVector message = new BitVector();

        int partitionLength = (int) Math.pow(2, m);

        int numPartitions = (int) Math.ceil((double) encodedMessage.length() / partitionLength);

        int[] part;
        for (int i = 0; i < numPartitions; i++) {
            part = new int[partitionLength];
            for(int j = 0; j < partitionLength; j++) {
                part[j] = encodedMessage.get((i * partitionLength) + j) ? 1 : -1;
            }
            message = BitVector.concatenate(message, decode(part));
        }

        return message;
    }

    /// Dekoduoja 2^m ilgio bitų vektorių
    /// w - sveikų skaičių masyvas, reprezentuojantis bitų vektorių, kuriame 0 reikšmės pakeistos į -1
    /// Gražina:
    /// Dekoduotą bitų vektorių
    public BitVector decode(int[] w) {
        if(w.length != Math.pow(2, m)) throw new MessageLengthIncorrectException("Message length is incorrect. Must be: " + Math.pow(2, m));

        int[] wn = Matrix.multiply(w, Matrix.H(1, m));

        for(int i = 2; i <= m; i++) {
            wn = Matrix.multiply(wn, Matrix.H(i, m));
        }

        int largest = 0;
        int largestIndex = 0;
        for(int i = 0; i < wn.length; i++) {
            if(Math.abs(wn[i]) > Math.abs(largest)) {
                largest = wn[i];
                largestIndex = i;
            }
        }

        BitVector vj = v(largestIndex);
        BitVector x = new BitVector();
        x.add(largest > 0);

        return BitVector.concatenate(x, vj);
    }

    /// Paverčia duotą sveiką skaičių į m ilgio bitų vektorių, išdėstytą taip, kad žemiausi bitai yra pradžioje
    /// j - sveikas skaičius, verčiamas į bitų vektorių
    /// Gražina:
    /// "Apsuktą" m ilgio bitų vektorių
    private BitVector v(int j) {
        BitVector output = new BitVector(m);

        for(int i = 0; i < m; i++) {
            output.set(i, ((j >> i) & 1) == 1);
        }

        return output;
    }

}
