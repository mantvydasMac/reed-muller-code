import Interfaces.SimpleDocumentListener;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;

public class Main {
    public static final int DEFAULT_REPEAT_TIMES = 50;

    public static void main(String[] args) {
        if(args.length > 0 && args[0].equals("exp")) {
            runExperiments(args);
        }
        else {
            runGUI();
        }
    }

    static void runExperiments(String[] args)
    {
        System.out.println("Running experiments.");
        int repeatTimes;
        try{
            repeatTimes = Integer.parseInt(args[1]);
            if(repeatTimes <= 0) throw new Exception("Invalid repeat times");
            System.out.println("Repeat number: " + repeatTimes);
        } catch (Exception ignored) {
            System.out.println("Invalid or no repeat number argument given. Using default value: " + DEFAULT_REPEAT_TIMES);
            repeatTimes = DEFAULT_REPEAT_TIMES;
        }
        System.out.println();

        accuracyTimeBasedOnParameterExperiment();
        accuracyBasedOnErrorProbabilityExperiment(repeatTimes);

        System.out.println();
        try{
            System.out.println("Press any button to close.");
            System.in.read();
        } catch(java.io.IOException ignored) {}

    }


    static void accuracyTimeBasedOnParameterExperiment() {
        System.out.println("== Accuracy and time based on m parameter ==");

        int m = 1;
        double p = 0.2;

        System.out.println("== error probability = " + p + "               ==");

        Channel channel = new Channel(p);
        ReedMullerEncoder encoder = new ReedMullerEncoder(m);
        FastDecoder decoder = new FastDecoder(m);

        String text = "This is a test text.";
        BitVector testVector = ASCIIBinaryConverter.convertASCIIToBinary(text);

        try (PrintWriter out = new PrintWriter(new FileWriter("rm_accuracy_time_m.csv"))) {

            out.println("m,elapsedMs,accuracy");

            for (; m <= 10; ++m) {

                long start = System.nanoTime();

                encoder.setM(m);
                decoder.m = m;

                BitVector decoded = decoder.decodeMessage(channel.send(encoder.encodeMessage(testVector)));

                long end = System.nanoTime();
                long elapsedNs = end - start;
                double elapsedMs = elapsedNs / 1_000_000.0;

                double accuracy = 100 * checkBitVectorAccuracy(testVector, decoded);

                System.out.printf(
                        "m = %d | %.2f ms | Accuracy: %.3f%n",
                        m, elapsedMs, accuracy
                );

                out.printf(
                        "%d,%.6f,%.6f%n",
                        m, elapsedMs, accuracy
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to write experiment results", e);
        }
    }

    static void accuracyBasedOnErrorProbabilityExperiment(int repeatTimes)
    {
        System.out.println("== Accuracy based on error probability ==");

        int m = 6;
        double p = 0.0125;

        System.out.println("== m = " + m + "                               ==");

        Channel channel = new Channel(p);
        ReedMullerEncoder encoder = new ReedMullerEncoder(m);
        FastDecoder decoder = new FastDecoder(m);

        String text = "This is a test text.";
        BitVector testVector = ASCIIBinaryConverter.convertASCIIToBinary(text);


        try (PrintWriter out = new PrintWriter(new FileWriter("rm_accuracy_error_probability.csv"))) {

            out.println("p,accuracy");

            double accuracySum;

            for (; p <= 1.01; p = p + 0.0125) {
                channel.errorProbability = p;

                accuracySum = 0;
                for (int i = 0; i < repeatTimes; ++i) {
                    BitVector decoded = decoder.decodeMessage(channel.send(encoder.encodeMessage(testVector)));

                    double accuracy = 100 * checkBitVectorAccuracy(testVector, decoded);

                    accuracySum += accuracy;
                }
                System.out.printf(
                        "p = %.4f | Average accuracy: %.3f%n",
                        p, accuracySum / repeatTimes
                );
                out.printf(
                        "%f,%.6f%n",
                        p, accuracySum / repeatTimes
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write experiment results", e);
        }
    }

    static double checkBitVectorAccuracy(BitVector v1, BitVector v2)
    {
        double correct = 0;

        for(int i = 0; i < v1.length(); i++)
        {
            if(v1.get(i) == v2.get(i)) correct++;
        }

        return correct/v1.length();
    }

    static void runGUI()
    {
        AtomicInteger m = new AtomicInteger(3);
        AtomicReference<Double> errorProbability = new AtomicReference<>(0.05);

        Channel channel = new Channel(errorProbability.get());
        ReedMullerEncoder encoder = new ReedMullerEncoder(m.get());
        FastDecoder decoder = new FastDecoder(m.get());


        JFrame frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        frame.setLayout(new BorderLayout());

        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.X_AXIS));
        tabPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        frame.add(tabPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        CardLayout cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        BinaryPage binaryPage = new BinaryPage(encoder, decoder, channel);

        TextPage textPage = new TextPage(encoder, decoder, channel);

        ImagePage imagePage = new ImagePage(encoder, decoder, channel);

        mainPanel.add(binaryPage, "binary");
        mainPanel.add(textPage, "text");
        mainPanel.add(imagePage, "image");

        // tab buttons
        JButton button1 = new JButton("Binary");
        button1.addActionListener(e -> cardLayout.show(mainPanel, "binary"));
        tabPanel.add(button1);
        JButton button2 = new JButton("Text");
        button2.addActionListener(e -> cardLayout.show(mainPanel, "text"));
        tabPanel.add(button2);
        JButton button3 = new JButton("Image");
        button3.addActionListener(e -> cardLayout.show(mainPanel, "image"));
        tabPanel.add(button3);

        // code parameters
        JLabel mLabel = new JLabel("m = ");
        tabPanel.add(mLabel);

        JTextField mTextField = new JTextField(5);
        mTextField.setMaximumSize(mTextField.getPreferredSize());
        mTextField.setText(String.valueOf(m.get()));

        mTextField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            try {
                int value = Integer.parseInt(mTextField.getText());
                m.set(value);
                encoder.setM(value);
                decoder.m = value;
                binaryPage.update();
            } catch (Exception ignored) {}
        });
        tabPanel.add(mTextField);

        JLabel pLabel = new JLabel("Error probability = ");
        tabPanel.add(pLabel);

        JTextField pTextField = new JTextField(5);
        pTextField.setMaximumSize(pTextField.getPreferredSize());
        pTextField.setText(String.valueOf(errorProbability.get()));
        pTextField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            try {
                double value = Double.parseDouble(pTextField.getText());
                errorProbability.set(value);
                channel.errorProbability = errorProbability.get();
                binaryPage.update();
            } catch (Exception ignored) {}
        });
        tabPanel.add(pTextField);


        frame.setVisible(true);
    }
}
