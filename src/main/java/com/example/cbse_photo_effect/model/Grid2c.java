package com.example.cbse_photo_effect.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Grid2c {
    private final int startX, startY;
    private final int width, height;
    private BufferedImage gridImage;
    int threshold = 30;

    public Grid2c(int startX, int startY, int width, int height, BufferedImage sourceImage) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.gridImage = sourceImage.getSubimage(startX, startY, width, height);
    }

    public void embedData(String data) {
        // Embedding logic
        //If data = '1', then R = highest, else G = highest
        if (data.equals("1")){
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get the pixel's RGB value (ignoring alpha)
                    int rgb = gridImage.getRGB(x, y);

                    // Extract the individual color components (R, G, B)
                    int red   = (rgb >> 16) & 0xff;
                    int green = (rgb >> 8) & 0xff;
                    int blue  = rgb & 0xff;

                    // use Math.min or Math.max to clamp or cap the value
                    red = Math.min(255, Math.max(blue, green) + threshold);
                    if(green==255) {
                        green = green - threshold;
                    }
                    blue = (int)(Math.random() * 254);

                    // Reassemble the RGB value (ignoring alpha), only for 24-bit image
                    int newRgb = (red << 16) | (green << 8) | blue;

                    // Set the modified RGB value back into the grid image
                    gridImage.setRGB(x, y, newRgb);
                }
            }
        }else{ //ensure G = highest, imply 0 embedded
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get the pixel's RGB value (ignoring alpha)
                    int rgb = gridImage.getRGB(x, y);

                    // Extract the individual color components (R, G, B)
                    int red   = (rgb >> 16) & 0xff;
                    int green = (rgb >> 8) & 0xff;
                    int blue  = rgb & 0xff;

                    // use Math.min or Math.max to clamp or cap the value
                    green = Math.min(255, Math.max(red, blue) + threshold);
                    if(red==255) {
                        red = red - threshold;
                    }
                    blue = (int)(Math.random() * 254);

                    // Reassemble the RGB value (ignoring alpha), only for 24-bit image
                    int newRgb = (red << 16) | (green << 8) | blue;

                    // Set the modified RGB value back into the grid image
                    gridImage.setRGB(x, y, newRgb);
                }
            }
        }

        makeOuterPixelsWhite();
    }

    public void makeOuterPixelsWhite() {
        int whiteRGB = 0xFFFFFF; // RGB value for white color (no alpha)

        // Set top and bottom rows to white
        for (int x = 0; x < width; x++) {
            gridImage.setRGB(x, 0, whiteRGB);            // Top row
            gridImage.setRGB(x, height - 1, whiteRGB);   // Bottom row
        }

        // Set left and right columns to white
        for (int y = 0; y < height; y++) {
            gridImage.setRGB(0, y, whiteRGB);            // Left column
            gridImage.setRGB(width - 1, y, whiteRGB);    // Right column
        }
    }

    public String extractData() {
        // Extraction logic
        // Safe margin, Majority conventional
        int marginX = determineSafeMargin(width);
        int marginY = determineSafeMargin(height);
        int majorityCount = (width*height)/4;

        // Center coordinates
        int centerX = width/2;
        int centerY = height/2;

        Random random = new Random();
        ArrayList<String> extract = new ArrayList<String>();

        // Include center pixel
        addDataFromPixel(extract, centerX, centerY);

        for (int i = 0; i < majorityCount; i++) {
            // Generate random (x, y) coordinates within the grid
            int randomX = marginX + random.nextInt(width - marginX - marginX);
            int randomY = marginY + random.nextInt(height - marginY - marginY);

            addDataFromPixel(extract, randomX, randomY);
        }

        // Count the occurrences of "1" and "0"
        int countOnes = 0;
        int countZeros = 0;
        for (String value : extract) {
            if (value.equals("1")) {
                countOnes++;
            } else {
                countZeros++;
            }
        }

        // Return the value that occurs the most
        return (countOnes >= countZeros) ? "1" : "0";
    }

    private void addDataFromPixel(ArrayList<String> extract, int x, int y){
        // Put extraction result from pixel into consideration
        int rgb = gridImage.getRGB(x, y);
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;

        if (red > green){
            extract.add("1");
        }else{
            extract.add("0");
        }
    }

    private int determineSafeMargin(int length){
        return (int)(Math.ceil(length*0.1));
    }

    public BufferedImage getGridImage() {
        return gridImage;
    }
}
