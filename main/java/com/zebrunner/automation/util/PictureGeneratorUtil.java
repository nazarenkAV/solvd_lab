package com.zebrunner.automation.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PictureGeneratorUtil {

    // Generate picture with size that maximally close to user inputted size, and return path to it.
    // pictureWeight = 54 + (side * side * 3) + (side * (side % 4)) - checked only on square bmp images
    public static String generateByWeight(double sizeInMegabytes) {
        Random random = new Random();

        double inputSize = sizeInMegabytes * 1024000;

        // Implied square picture side in pixels
        double side = Math.floor(Math.sqrt((inputSize - 54) / 3));

        for (int i = -2; i < 3; i++) {
            side += i;
            double size = 54 + (side * side * 3) + (side * (side % 4));
            if (size > inputSize) {
                side -= 1;
                break;
            }
            side -= i;
        }

        // Cast image side from double to int
        int imageSide = (int) side;
        BufferedImage image = new BufferedImage(imageSide, imageSide, BufferedImage.TYPE_INT_RGB);

        // Number of coloured rectangles on picture
        for (int x = 0; x < 7; x++) {

            // Rectangle width
            int rectangleWidth = random.nextInt((imageSide / 2) - 2);

            // Pixels between left side of image and left side of rectangle
            int rectangleLeftIndent = random.nextInt(imageSide - rectangleWidth);

            // Rectangle height
            int rectangleHeight = random.nextInt((imageSide / 2) - 2);

            // Pixels between upper side of image and upper side of rectangle
            int rectangleTopIndent = random.nextInt(imageSide - rectangleHeight);

            int newColour = random.nextInt(16777215 * 2) - 16777215;

            // Painting rectangles
            for (int i = rectangleLeftIndent; i < rectangleLeftIndent + rectangleWidth; i++) {
                for (int j = rectangleTopIndent; j < rectangleTopIndent + rectangleHeight; j++) {
                    image.setRGB(i, j, newColour);
                }
            }
        }

        // Writing image to file
        try {
            String path = "src/test/resources/testImage.jpg";
            ImageIO.write(image, "bmp", new File(path));
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    /**
     * Default path: <p>
     * <b>"src/test/resources/testImage.jpg"</b>
     */
    public static void deleteInDefaultPath() {
        File file = new File("src/test/resources/testImage.jpg");
        file.delete();
    }

}
