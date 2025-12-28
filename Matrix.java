import Exceptions.MatrixDimensionIncorrectException;

public class Matrix {

    int[][] arr;


    public Matrix(int rows, int cols) {
        arr = new int[rows][cols];
    }

    public Matrix(int rows, int cols, int value) {
        arr = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                arr[i][j] = value;
            }
        }
    }

    public Matrix(int[][] arr) {
        this.arr = arr;
    }

    /// Gražina matricos aukštį
    public int height(){
        return arr.length;
    }

    /// Gražina matricos ilgį
    public int width(){
        return arr[0].length;
    }

    /// Sukuria tam tikro dydžio vienetinę matricą
    /// size - matricos dydis (ilgis ir aukštis)
    /// Gražina:
    /// Vienetinę matricą size ilgio ir aukščio
    public static Matrix identity(int size) {
        Matrix matrix = new Matrix(size, size);

        for (int i = 0; i < size; i++) {
            matrix.arr[i][i] = 1;
        }

        return matrix;
    }

    /// Gražina Hadamardo matricą
    public static Matrix hadamard() {
        return new Matrix(new int[][]{
                {1, 1},
                {1, -1}
        });
    }

    /// Sukuria matricą pagal formulę:  I_{2^{m-i}} ⊗ H ⊗ I_{2^{i-1}},
    /// kur I - vienetinę matrica, H - hadamardo matrica, ⊗ - operatorius reikšiantis Krònekerio sandaugą
    /// i - transformacijos indeksas
    /// m - kodo parametras
    public static Matrix H(int i, int m) {
        return Matrix.kroneckerProduct(Matrix.kroneckerProduct(Matrix.identity((int) Math.pow(2, m-i)), Matrix.hadamard()), Matrix.identity((int) Math.pow(2, i-1)));
    }

    /// Sudaugina dvi matricas pagal Krònekerio sandaugą
    /// m1, m2 - dauginamos matricos
    /// Gražina:
    /// Dviejų matricų Krònekerio sandaugą
    public static Matrix kroneckerProduct(Matrix m1, Matrix m2) {

        Matrix product = null;
        Matrix rowProduct;

        for(int i = 0; i < m1.height(); i++) {
            rowProduct = null;
            for(int j = 0; j < m1.width(); j++) {
                if(rowProduct == null) {
                    rowProduct = Matrix.multiply(m1.arr[i][j], m2);
                }
                else {
                    rowProduct.concatRight(Matrix.multiply(m1.arr[i][j], m2));
                }
            }
            if(product == null) {
                product = rowProduct;
            }
            else {
                product.concatBottom(rowProduct);
            }
        }
        return product;
    }

    /// Daugina natūralųjį skaičių su matrica
    /// x - natūralusis skaičius
    /// m - matrica
    /// Gražina:
    /// Matricą m padaugintą iš skaičiaus x
    public static Matrix multiply(int x, Matrix m) {
        Matrix matrix = Matrix.copy(m);

        for(int i = 0; i < matrix.height(); i++) {
            for(int j = 0; j < matrix.width(); j++) {
                matrix.arr[i][j] *= x;
            }
        }

        return matrix;
    }

    /// Daugina vektorių su matrica
    /// vector - vektorius sudarytas iš natūraliųjų skaičių
    /// m - matrica
    /// Gražina:
    /// Matricą m sudaugintą su vektoriu vector
    public static int[] multiply(int[] vector, Matrix m) {
        int[] output = new int[m.height()];

        for (int i = 0; i < m.height(); i++) {
            for(int j = 0; j < m.width(); j++) {
                output[i] += vector[j] * m.arr[i][j];
            }
        }

        return output;
    }


    /// Sukuria matricos kopiją
    /// m - kopijuojama matrica
    /// Gražina:
    /// m matricos kopiją
    public static Matrix copy(Matrix m) {
        Matrix newMatrix = new Matrix(m.width(), m.height());

        for(int i = 0; i < newMatrix.height(); i++) {
            for(int j = 0; j < newMatrix.width(); j++) {
                newMatrix.arr[i][j] = m.arr[i][j];
            }
        }

        return newMatrix;
    }

    // Concatenation

    /// Matricos apačioje prijungia kitą matricą
    /// matrix - prijungiama matrica
    public void concatBottom(Matrix matrix) {
        if(width() != matrix.width()) throw new MatrixDimensionIncorrectException("Cannot concatenate matrix. Widths are not equal.");

        int[][] newArr = new int[height() + matrix.height()][width()];

        int i = 0;
        for (; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                newArr[i][j] = arr[i][j];
            }
        }
        int offset = i;

        i = 0;
        for (; i < matrix.height(); i++) {
            for (int j = 0; j < matrix.width(); j++) {
                newArr[i+offset][j] = matrix.arr[i][j];
            }
        }

        arr = newArr;
    }

    /// Matricos viršuje prijungia kitą matricą
    /// matrix - prijungiama matrica
    public void concatTop(Matrix matrix) {
        if(width() != matrix.width()) throw new MatrixDimensionIncorrectException("Cannot concatenate matrix. Widths are not equal.");

        int[][] newArr = new int[height() + matrix.height()][width()];

        int i = 0;
        for (; i < matrix.height(); i++) {
            for (int j = 0; j < matrix.width(); j++) {
                newArr[i][j] = matrix.arr[i][j];
            }
        }
        int offset = i;

        i = 0;
        for (; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                newArr[i+offset][j] = arr[i][j];
            }
        }

        arr = newArr;
    }

    /// Matricos dešinėje prijungia kitą matricą
    /// matrix - prijungiama matrica
    public void concatRight(Matrix matrix) {
        if(height() != matrix.height()) throw new MatrixDimensionIncorrectException("Cannot concatenate matrix. Heights are not equal.");

        int[][] newArr = new int[height()][width() + matrix.width()];

        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                newArr[i][j] = arr[i][j];
            }
        }
        int offset = width();

        for (int i = 0; i < matrix.height(); i++) {
            for (int j = 0; j < matrix.width(); j++) {
                newArr[i][j+offset] = matrix.arr[i][j];
            }
        }

        arr = newArr;
    }

    /// Matricos kairėje prijungia kitą matricą
    /// matrix - prijungiama matrica
    public void concatLeft(Matrix matrix) {
        if(height() != matrix.height()) throw new MatrixDimensionIncorrectException("Cannot concatenate matrix. Heights are not equal.");

        int[][] newArr = new int[height()][width() + matrix.width()];

        for (int i = 0; i < matrix.height(); i++) {
            for (int j = 0; j < matrix.width(); j++) {
                newArr[i][j] = matrix.arr[i][j];
            }
        }
        int offset = matrix.width();

        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                newArr[i][j+offset] = arr[i][j];
            }
        }
        arr = newArr;
    }



}
