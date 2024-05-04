
import com.formdev.flatlaf.FlatDarculaLaf;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to project RUdra Chat Client CLI.");
        new JFXPanel();

        // Create a new JFrame for the loading screen
        FlatDarculaLaf.setup();
        JFrame loadingFrame = new JFrame("Loading...");
        loadingFrame.setUndecorated(true);
        loadingFrame.setSize(572, 368);
        loadingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setResizable(false);

        URL iconURL = Main.class.getResource("/Resources/Icon.png");
        ImageIcon icon1 = new ImageIcon(iconURL);
        loadingFrame.setIconImage(icon1.getImage());

        // Create a custom JPanel that draws a circle of dots
        LoadingPanel loadingPanel = new LoadingPanel();

        JPanel roundedPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D g2d) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                }
            }
        };
        roundedPanel.setLayout(new BorderLayout());
        roundedPanel.add(loadingPanel, BorderLayout.CENTER);

        // Add the custom JPanel to the loading screen
        loadingFrame.add(roundedPanel);

        // Display the loading screen
        loadingFrame.setVisible(true);

        // Create a Timer that will animate the circle of dots
        Timer animationTimer = new Timer(50, e -> loadingPanel.nextDot());
        animationTimer.start();

        // Create a Timer that will display the login frame after 5 seconds
        Timer timer = new Timer(5000, e -> {
            // Stop the animation
            animationTimer.stop();

            // Hide the loading screen
            loadingFrame.setVisible(false);
            loadingFrame.dispose();

            // Display the MainMenu
            Platform.runLater(MainMenu::createMenu);
        });

        // Start the Timer
        timer.setRepeats(false);
        timer.start();
    }
}

class LoadingPanel extends JPanel {
    private int dotIndex = 0;
    private Image backgroundImg;

    public LoadingPanel() {
        try {
            URL backgroundURL = getClass().getResource("/Resources/BackgroundLoad.png");
            assert backgroundURL != null;
            backgroundImg = ImageIO.read(backgroundURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);

        g.setColor(Color.WHITE);
        for (int i = 0; i < 12; i++) {
            int x = (int) (Math.sin(i * Math.PI / 6) * 30 + getWidth() - 50); // reduced radius
            int y = (int) (Math.cos(i * Math.PI / 6) * 30 + getHeight() - 50); // reduced radius
            if (i == dotIndex) {
                g.fillOval(x, y, 6, 6); // reduced dot size
            } else {
                g.drawOval(x, y, 6, 6); // reduced dot size
            }
        }
    }

    public void nextDot() {
        dotIndex = (dotIndex + 1) % 12;
        repaint();
    }
}