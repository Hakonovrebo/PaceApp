import javax.swing.*;
import java.awt.*;

public class PaceApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PaceApp::createAndShow);
    }

    private static void createAndShow() {
        JFrame frame = new JFrame("Pace <-> km/t");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField paceField = new JTextField(10);
        JTextField kmhField  = new JTextField(10);

        JLabel kmhOut  = new JLabel(" ");
        JLabel paceOut = new JLabel(" ");

        // Pace -> km/t
        paceField.addActionListener(e -> {
            try {
                double kmh = PaceConverter.paceToKmh(paceField.getText());
                kmhOut.setText(String.format("%.2f km/t", kmh));
            } catch (Exception ex) {
                kmhOut.setText("Ugyldig pace");
            }
        });

        // km/t -> Pace
        kmhField.addActionListener(e -> {
            try {
                String pace = PaceConverter.kmhToPace(kmhField.getText());
                paceOut.setText(pace + " per km");
            } catch (Exception ex) {
                paceOut.setText("Ugyldig fart");
            }
        });

        JPanel left = new JPanel(new GridLayout(0,1,6,6));
        left.setBorder(BorderFactory.createTitledBorder("Pace (mm.ss)"));
        left.add(paceField);
        left.add(kmhOut);

        JPanel right = new JPanel(new GridLayout(0,1,6,6));
        right.setBorder(BorderFactory.createTitledBorder("Fart (km/t)"));
        right.add(kmhField);
        right.add(paceOut);

        JPanel root = new JPanel(new GridLayout(1,2,10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        root.add(left);
        root.add(right);

        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
