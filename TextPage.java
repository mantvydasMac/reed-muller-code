import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TextPage extends JPanel {

    public ReedMullerEncoder encoder;
    public FastDecoder decoder;
    public Channel channel;

    String input;
    BitVector binaryInput;
    BitVector encoded;
    BitVector receivedNotEncoded;
    BitVector receivedEncoded;
    BitVector decoded;

    JTextArea inputArea;
    JTextArea noEncArea;
    JTextArea encArea;


    public TextPage(ReedMullerEncoder encoder, FastDecoder decoder, Channel channel) {
        super();

        this.encoder = encoder;
        this.decoder = decoder;
        this.channel = channel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        // input row
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel inputLabel = new JLabel("Input:");

        inputArea = new JTextArea(6, 40);
        inputArea.setLineWrap(true);

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        row1.add(inputLabel);
        row1.add(inputScroll);
        add(row1);



        // send row
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            input = inputArea.getText();

            binaryInput = ASCIIBinaryConverter.convertASCIIToBinary(input);

            encoded = encoder.encodeMessage(binaryInput);

            receivedNotEncoded = channel.send(binaryInput);
            receivedEncoded = channel.send(encoded);

            decoded = decoder.decodeMessage(receivedEncoded);

            noEncArea.setText(ASCIIBinaryConverter.convertBinaryToASCII(receivedNotEncoded));
            encArea.setText(ASCIIBinaryConverter.convertBinaryToASCII(decoded));
        });

        row2.add(sendButton);
        add(row2);


        // no encode results
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel outputLabel1 = new JLabel("No encoding:  ");
        noEncArea = new JTextArea(6, 40);
        noEncArea.setEditable(false);
        noEncArea.setLineWrap(true);

        JScrollPane noEncScroll = new JScrollPane(noEncArea);
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        row3.add(outputLabel1);
        row3.add(noEncScroll);
        add(row3);

        // with encode results
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel outputLabel2 = new JLabel("With encoding:");
        encArea = new JTextArea(6, 40);
        encArea.setEditable(false);
        encArea.setLineWrap(true);

        JScrollPane encScroll = new JScrollPane(encArea);
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        row4.add(outputLabel2);
        row4.add(encScroll);
        add(row4);
    }
}
