import Interfaces.SimpleDocumentListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class BinaryPage extends JPanel {


    public ReedMullerEncoder encoder;
    public FastDecoder decoder;
    public Channel channel;

    String input;
    BitVector byteInput;
    BitVector encoded;
    BitVector received;
    BitVector decoded;

    boolean canEncode = false;

    JLabel dimensionLabel;
    JTextPane receivedTextPane;
    StyledDocument doc;
    SimpleAttributeSet normal;
    SimpleAttributeSet underline;

    JTextArea encodedInput;
    JTextField inputField;
    JTextField decodedMsg;
    JTextArea receivedEditableMsg;

    public BinaryPage(ReedMullerEncoder encoder, FastDecoder decoder, Channel channel) {
        super();

        this.encoder = encoder;
        this.decoder = decoder;
        this.channel = channel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // stack rows vertically

        // dimension row
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dimensionLabel = new JLabel("Required dimension: " + encoder.getDimension());

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            clear();
        });

        row1.add(dimensionLabel);
        row1.add(clearButton);
        add(row1);

        // input row
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        inputField = new JTextField(30);
        inputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        Border normalBorder = inputField.getBorder();
        Border errorBorder = BorderFactory.createLineBorder(Color.RED);

        inputField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            String value = inputField.getText();
            boolean correctInput = true;

            try{
                byteInput = parseBitVector(value);
            }
            catch(NumberFormatException ex){
                correctInput = false;
            }

            if (value.length() != encoder.getDimension() || !correctInput) {
                inputField.setBorder(errorBorder);
                canEncode = false;
            } else {
                inputField.setBorder(normalBorder);
                canEncode = true;
            }
            input = value;
        });

        JLabel baseLabel = new JLabel("Base input");

        row2.add(inputField);
        row2.add(baseLabel);
        add(row2);

        // encode button row
        JPanel rowEncode = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton encodeButton = new JButton("Encode");
        encodeButton.addActionListener(e -> {
            if(canEncode) {
                encoded = encoder.encodeMessage(byteInput);
                encodedInput.setText(formatBitVector(encoded, " "));
            }
            else {
                encoded = null;
            }
        });

        rowEncode.add(encodeButton);
        add(rowEncode);

        // encoded row
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel encodedLabel = new JLabel("Encoded");

        encodedInput = new JTextArea(1, 60);
        encodedInput.setEditable(false);
        encodedInput.setLineWrap(false);
        encodedInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(
                encodedInput,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        row3.add(scroll);
        row3.add(encodedLabel);
        add(row3);

        // send row
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            if(encoded != null) {
                received = channel.send(encoded);
                try {
                    fillReceivedPane(encoded, received);
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
                receivedEditableMsg.setText(formatBitVector(received, ""));

                decoded = decoder.decodeMessage(received);

                decodedMsg.setText(formatBitVector(decoded, " "));

            }
            else {
                received = null;
            }
        });

        row4.add(sendButton);
        add(row4);



        // editable received row
        JPanel editableReceivedRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        receivedEditableMsg = new JTextArea(1, 60);
        receivedEditableMsg.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        receivedEditableMsg.setLineWrap(false);
        receivedEditableMsg.setWrapStyleWord(false);

        JScrollPane receivedScroll = new JScrollPane(
                receivedEditableMsg,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        receivedEditableMsg.getDocument().addDocumentListener(
                (SimpleDocumentListener) e -> {

                    String value = receivedEditableMsg.getText();
                    boolean correctInput = true;

                    try {
                        received = parseBitVector(value);
                    } catch (NumberFormatException ex) {
                        correctInput = false;
                    }

                    if (value.length() != encoder.getLength() || !correctInput) {
                        receivedScroll.setBorder(errorBorder);
                        receivedTextPane.setBorder(errorBorder);
                        decodedMsg.setBorder(errorBorder);
                    } else {
                        receivedScroll.setBorder(normalBorder);
                        receivedTextPane.setBorder(normalBorder);
                        decodedMsg.setBorder(normalBorder);

                        try {
                            fillReceivedPane(encoded, received);
                        } catch (BadLocationException ex) {
                            throw new RuntimeException(ex);
                        }

                        decoded = decoder.decodeMessage(received);
                        decodedMsg.setText(formatBitVector(decoded, " "));
                    }
                }
        );

        JLabel receivedEditableLabel = new JLabel("Received");

        editableReceivedRow.add(receivedScroll);
        editableReceivedRow.add(receivedEditableLabel);
        add(editableReceivedRow);

        // received row
        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        receivedTextPane = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return false;
            }
        };
        receivedTextPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        doc = receivedTextPane.getStyledDocument();

        normal = new SimpleAttributeSet();
        StyleConstants.setUnderline(normal, false);

        underline = new SimpleAttributeSet();
        StyleConstants.setUnderline(underline, true);


        receivedTextPane.setEditable(false);
        JLabel receivedLabel = new JLabel("Comparison");


        JScrollPane scrollPane = new JScrollPane(receivedTextPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setPreferredSize(new Dimension(500, 80));

        row5.add(scrollPane);
        row5.add(receivedLabel);
        add(row5);


        // decoded row
        JPanel row6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        decodedMsg = new JTextField(30);
        decodedMsg.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        decodedMsg.setEditable(false);
        JLabel decodedLabel = new JLabel("Decoded");

        row6.add(decodedMsg);
        row6.add(decodedLabel);
        add(row6);
    }

    private void fillReceivedPane(BitVector encoded, BitVector received) throws BadLocationException {
        clearText(receivedTextPane);

        doc.insertString(doc.getLength(), formatBitVector(encoded, " "), normal);
        doc.insertString(doc.getLength(), "\n", normal);

        for(int i = 0; i < received.length(); i++){
            if(received.get(i) == encoded.get(i)){
                doc.insertString(doc.getLength(), (received.get(i) ? 1 : 0) + " ", normal);
            }
            else {
                doc.insertString(doc.getLength(), String.valueOf((received.get(i) ? 1 : 0)), underline);
                doc.insertString(doc.getLength(), " ", normal);
            }
        }
    }

    private String formatBitVector(BitVector bits, String separator)
    {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < bits.length(); i++) {
            str.append(bits.get(i) ? 1 : 0).append(separator);
        }

        return str.toString();
    }

    private BitVector parseBitVector(String str)
    {
        BitVector bits = new BitVector(str.length());

        for (int i = 0; i < str.length(); i++) {
            String c = str.substring(i, i + 1);
            byte b = Byte.parseByte(c);

            if(b != 1 && b != 0)
            {
                throw new NumberFormatException();
            }
            bits.set(i, b != 0);
        }

        return bits;
    }

    private void clearText(JTextPane pane) {
        StyledDocument doc = pane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ignored) {}
    }

    public void update()
    {
        dimensionLabel.setText("Required dimension: " + encoder.getDimension());

        inputField.setText(input);

        encodedInput.setText("");
        decodedMsg.setText("");
        receivedEditableMsg.setText("");
        clearText(receivedTextPane);
    }

    private void clear()
    {
        dimensionLabel.setText("Required dimension: " + encoder.getDimension());
        inputField.setText("");
        encodedInput.setText("");
        decodedMsg.setText("");
        receivedEditableMsg.setText("");
        clearText(receivedTextPane);

        encoded = null;
        decoded = null;
        received = null;
        byteInput = null;
        input = null;
    }
}
