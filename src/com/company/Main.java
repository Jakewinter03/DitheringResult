package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    //Grayscale values
    private static final double GS_RED = 0.299;
    private static final double GS_GREEN = 0.587;
    private static final double GS_BLUE = 0.114;
    private static BufferedImage img;

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        img = null;
        boolean isValid = false;
        String fileName = "";

        //import image to dither
        while (!isValid) {
            System.out.println("Please input a file path to dither:");

            fileName = in.next();

            try {
                img = ImageIO.read(new File(fileName));
                isValid = true;
            } catch (IOException e) {
                System.out.println("Not a valid file path.");
            }
        }


        //threshold image into grayscale.  This code largely subsidized by http://stackoverflow.com/questions/18710560/how-to-convert-colors-to-grayscale-in-java-with-just-the-java-io-library
        for (int i = 0; i < img.getHeight(); i ++){
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int red= color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                red = green = blue = (int)(red * GS_RED + green * GS_GREEN + blue *GS_BLUE);
                color = new Color(red, green, blue);
                int rgb = color.getRGB();

                img.setRGB(j, i, rgb);
            }
        }

        //dither image
        for (int i = 0; i < img.getHeight(); i ++){
            for (int j = 0; j < img.getWidth(); j++){
                Color color;
                int oldPixel = img.getRGB(j, i);
                color = new Color(oldPixel);
                oldPixel = color.getRed();  // Pulls the current value for red, which is the same for green and blue.

                //Determine whether pixel should be black = 0 or white = 255.
                int newPixel;
                if (oldPixel < 128) {
                    newPixel = 0;
                } else {
                    newPixel = 255;
                }
                int errorValue = oldPixel - newPixel;

                //Store w/b color to cast into image.
                color = new Color(newPixel, newPixel, newPixel);
                newPixel = color.getRGB();
                img.setRGB(j, i, newPixel);

                dithOffset(j + 1, i, errorValue, 7);
                dithOffset(j - 1, i + 1, errorValue, 3);
                dithOffset(j, i + 1, errorValue, 5);
                dithOffset(j + 1, i + 1, errorValue, 1);

            }
        }

        // Write new image to file.
        String newFileName = fileName.substring(0, fileName.length() -4) + "_edited";
        String fileType = fileName.substring(fileName.length() - 3);

        try{
            ImageIO.write(img, fileType, new File(newFileName + "." + fileType));
        } catch (IOException e ) {
            System.out.println("Could not write to file");
        }


    }

    private static void dithOffset(int x, int y, int errorValue, int factor) {
        if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight()){
            Color color = new Color(img.getRGB(x, y));
            int value = color.getRed();  //rgb values are the same so simply using Red for reference.
            value += (errorValue * factor/16);
            if (value >= 0 && value < 256){
                color = new Color(value, value, value);
            }
            img.setRGB(x, y, color.getRGB());
        }

    }
}
