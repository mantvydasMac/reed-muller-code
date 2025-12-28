import Exceptions.InputDimensionIncorrectException;

public class ReedMullerEncoder {

    private final int r = 1;
    private int m;

    public void setM(int m) {
        this.m = m;
        generatorMatrix = calculateGeneratorMatrix(r, this.m);
    }

    public int getM() {
        return m;
    }

    public int getLength()
    {
        return (int) Math.pow(2, m);
    }
    public int getDimension()
    {
        return 1 + m;
    }

    private Matrix generatorMatrix;
    public Matrix getGeneratorMatrix()
    {
        return generatorMatrix;
    }

    public ReedMullerEncoder(int m) {
        this.m = m;
        this.generatorMatrix = calculateGeneratorMatrix(r, m);
    }

    /// Užkoduoja bet kokio ilgio bitų vektorių
    /// message - žinutė, bitų vektorius kuris bus užkoduojamas
    /// Gražina:
    /// Bitų vektorių, kuris yra užkoduota message žinutė
    public BitVector encodeMessage(BitVector message) {
        BitVector encodedMessage = new BitVector();

        int numPartitions = (int) Math.ceil((double) message.length() / getDimension());

        BitVector part;
        for (int i = 0; i < numPartitions; i++) {
            part = new BitVector(getDimension());
            for(int j = 0; j < getDimension(); j++) {
                if((i * getDimension()) + j < message.length()) {
                    part.set(j, message.get((i * getDimension()) + j));
                }
                else {
                    part.set(j, false);
                }
            }
            encodedMessage = BitVector.concatenate(encodedMessage, encode(part));
        }

        return encodedMessage;
    }

    /// Užkoduoja reikalaujamo ilgio (1 + m) bitų vektorių
    /// vector - užkoduojamas vektorius
    /// Gražina:
    /// Užkoduotą bitų vektorių, kurio ilgis 2^m
    public BitVector encode(BitVector vector) {
        if(vector.length() != getDimension()) throw new InputDimensionIncorrectException("Vector length does not match dimension. Passed: " + vector.length());

        BitVector encodedVector = new BitVector(getLength());

        for(int i = 0; i < getDimension(); i++) {
            int[] row = generatorMatrix.arr[i];

            if(vector.get(i)) {
                for(int j = 0; j < getLength(); j++) {
                    encodedVector.set(j, encodedVector.get(j) ^ (row[j] != 0));
                }
            }
        }

        return encodedVector;
    }

    /// Apskaičiuoja generavimo matricą
    /// r, m - kodo parametrai
    /// Gražina:
    /// Generavimo matricą, apskaičiuotą pagal duotus r, m parametrus
    Matrix calculateGeneratorMatrix(int r, int m) {
        if(r > 0 && m > r){
            Matrix matrix = calculateGeneratorMatrix(r, m-1);
            matrix.concatRight(matrix);

            Matrix matrixBottom = calculateGeneratorMatrix(r-1, m-1);
            matrixBottom.concatLeft(new Matrix(matrixBottom.height(), matrixBottom.width()));

            matrix.concatBottom(matrixBottom);

            return matrix;
        } else if (r == 0 && m > 0) {
            return new Matrix(1, (int) Math.pow(2, m), 1);
        } else if (r == m && m > 0) {
            Matrix matrix = calculateGeneratorMatrix(r-1, m);

            Matrix matrix2 = new Matrix(1, (int) Math.pow(2, m));
            matrix2.arr[0][matrix2.width()-1] = 1;

            matrix.concatBottom(matrix2);

            return matrix;
        }

        return null;
    }


}
