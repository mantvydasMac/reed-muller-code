import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Arrays;

public class ImagePage extends JPanel {

    public ReedMullerEncoder encoder;
    public FastDecoder decoder;
    public Channel channel;

    BufferedImage image;

    BufferedImage imageNotEncoded;
    BufferedImage imageEncoded;

    JPanel imagePanel;
    JPanel resultEncodedPanel;
    JPanel resultNotEncodedPanel;

    public ImagePage(ReedMullerEncoder encoder, FastDecoder decoder, Channel channel) {
        super();

        this.encoder = encoder;
        this.decoder = decoder;
        this.channel = channel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        // open image row
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton openButton = new JButton("Open BMP");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Bitmap Images (*.bmp)", "bmp"
        ));



        openButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + file.getAbsolutePath());

                // Load the BMP here
                loadBmp(file);
            }
        });

        row1.add(openButton);
        add(row1);



        imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        add(imagePanel);


        // send row
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton sendButton = new JButton("Send Image");
        sendButton.addActionListener(e -> {
            if(image != null) {
                byte[] header = BMPBinaryConverter.getImageHeaderBytes(image);
                byte[] pixels = BMPBinaryConverter.getImagePixelBytes(image);

                BitVector vector = BMPBinaryConverter.imagePixelsToBinary(pixels);

//                if(vector == null) {return;}
                BitVector encoded = encoder.encodeMessage(vector);

                BitVector receivedNotEncoded = channel.send(vector);
                BitVector receivedEncoded = channel.send(encoded);

                BitVector decoded = decoder.decodeMessage(receivedEncoded);

                imageNotEncoded = BMPBinaryConverter.binaryAndHeaderToImage(receivedNotEncoded, header);
                imageEncoded = BMPBinaryConverter.binaryAndHeaderToImage(decoded, header);

                setImage(resultNotEncodedPanel, imageNotEncoded, 4);
                setImage(resultEncodedPanel, imageEncoded, 4);
            }
        });

        row2.add(sendButton);
        add(row2);

        //result images rowe
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultEncodedPanel = new JPanel();
        resultEncodedPanel.setLayout(new BorderLayout());

        resultNotEncodedPanel = new JPanel();
        resultNotEncodedPanel.setLayout(new BorderLayout());

        row3.add(resultEncodedPanel);
        row3.add(resultNotEncodedPanel);
        add(row3);
    }

    private void loadBmp(File file) {
        try {
            image = ImageIO.read(file);

            if (image == null) {
                JOptionPane.showMessageDialog(this, "Invalid BMP");
                return;
            }

            setImage(imagePanel, image, 4);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setImage(JPanel panel, BufferedImage image, double scale) {
        Image scaled;
        if(scale > 0)
        {
            scaled = image.getScaledInstance(
                    (int) (image.getWidth() * scale),
                    (int) (image.getHeight() * scale),
                    Image.SCALE_SMOOTH
            );
        }
        else {
            scaled = image;
        }

        panel.removeAll();
        panel.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }
}
