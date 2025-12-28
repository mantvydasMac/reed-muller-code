import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class BMPBinaryConverter {

    private static int getHeaderEndIndex()
    {
        return 54;
    }


    /// Paverčia nuotraukos pikselių duomenis į bitų vektorių
    /// pixels - baitų masyvas, laikantis 24 bitų formato BMP nuotraukos pikselių duomenis
    /// Gražina:
    /// pixels paverstą į bitų vektorių
    public static BitVector imagePixelsToBinary(byte[] pixels) {
        BitVector output = new BitVector();

        for (byte pixel : pixels) {
            for (int j = 7; j >= 0; j--) {
                output.add(((pixel >> j) & 1) == 1);
            }
        }

        return output;
    }

    /// Iš nuotraukos failo ištraukia antraštės baitus
    /// img - nuotrauka
    /// Gražina:
    /// Baitų masyvą, kuriame yra nuotraukos antraštės duomenys
    public static byte[] getImageHeaderBytes(BufferedImage img) {
        if (img == null) {return null;}
        int headerEndIndex = getHeaderEndIndex();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ImageIO.write(img, "bmp", baos);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        byte[] imageBytes = baos.toByteArray();

        return Arrays.copyOfRange(imageBytes, 0, headerEndIndex);
    }

    /// Iš nuotraukos failo ištraukia pikselių duomenų baitus
    /// img - nuotrauka
    /// Gražina:
    /// Baitų masyvą, kuriame yra pikselių duomenys
    public static byte[] getImagePixelBytes(BufferedImage img) {
        if (img == null) {return null;}
        int headerEndIndex = getHeaderEndIndex();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ImageIO.write(img, "bmp", baos);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        byte[] imageBytes = baos.toByteArray();

        return Arrays.copyOfRange(imageBytes, headerEndIndex, imageBytes.length);
    }

    /// Paverčia bitų vektorių, laikantį pikselių duomenis, į baitų masyvą ir prijungia prie nuotraukos antraštės
    /// vector - bitų vektorius, laikantis pikselių duomenis
    /// header - baitų masyvas, laikantis nuotraukos antraštę
    /// Gražina:
    /// Nuotraukos failą, sudarytą prie antraštės prijungus pikselių duomenis
    public static BufferedImage binaryAndHeaderToImage(BitVector vector, byte[] header) {

        int arrLength = vector.length() / 8;
        byte[] pixels = new byte[arrLength];

        for (int i = 0; i < arrLength; i++) {
            byte value = 0;
            for (int j = 0; j < 8; j++) {
                if (vector.get(i * 8 + j)) {
                    value |= (byte) (1 << (7 - j));
                }
            }
            pixels[i] = value;
        }

        byte[] imageBytes = new byte[header.length + pixels.length];
        System.arraycopy(header, 0, imageBytes, 0, header.length);
        System.arraycopy(pixels, 0, imageBytes, header.length, pixels.length);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new IOException("Failed to decode BMP image");
            }
            return image;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
