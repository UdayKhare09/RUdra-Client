package ChatApp;

import javax.swing.*;
import java.awt.*;

public class ImageFrame extends JFrame {
    public ImageFrame (ImageIcon image, String sender) {
        setTitle("Image from " + sender);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width - 100; // Subtract 100 pixels for margin
        int screenHeight = screenSize.height - 100; // Subtract 100 pixels for margin

        int imageWidth = image.getIconWidth();
        int imageHeight = image.getIconHeight();

        if (imageWidth > screenWidth || imageHeight > screenHeight) {
            double widthScale = (double) screenWidth / imageWidth;
            double heightScale = (double) screenHeight / imageHeight;
            double scale = Math.min(widthScale, heightScale);

            imageWidth = (int) (imageWidth * scale);
            imageHeight = (int) (imageHeight * scale);

            Image scaledImage = image.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
            image = new ImageIcon(scaledImage);
        }

        JLabel label = new JLabel(image);
        add(label);
        pack();
        setVisible(true);
    }
}