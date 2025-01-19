package com.example.cbse_photo_effect.service;

import com.example.cbse_photo_effect.model.Grid2c;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {
    int gridCols = 32;
    int gridRows = 32;
    public String embedDataIntoImage(String data, byte[] imageBytes) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        image = resizeImage(image, nearestMultiple(gridCols, image.getWidth()),nearestMultiple(gridRows, image.getHeight()));
        List<Grid2c> grids = splitImageIntoGrids(image, gridRows, gridCols); // Example grid size

        int index = 0;
        for (Grid2c grid : grids) {
            if (index < data.length()) {
                grid.embedData(String.valueOf(data.charAt(index)));
                index++;
            }
        }

        // Save the modified image to the filesystem
        String outputPath = "output/embedded-image.png";
        File outputFile = new File(outputPath);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs(); // Create directories if they don't exist
        }
        ImageIO.write(image, "png", outputFile);

        return outputFile.getAbsolutePath();
    }

    public String extractDataFromImage(byte[] imageBytes) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        List<Grid2c> grids = splitImageIntoGrids(image, gridRows, gridCols); // Example grid size

        StringBuilder extractedData = new StringBuilder();
        for (Grid2c grid : grids) {
            extractedData.append(grid.extractData());
        }

        return extractedData.toString();
    }

    private List<Grid2c> splitImageIntoGrids(BufferedImage image, int rows, int cols) {
        List<Grid2c> grids = new ArrayList<>();
        int gridWidth = image.getWidth() / cols;
        int gridHeight = image.getHeight() / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grids.add(new Grid2c(col * gridWidth, row * gridHeight, gridWidth, gridHeight, image));
            }
        }
        return grids;
    }

    public static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight){
        // Create a new image with the desired dimensions
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType());

        // Draw the original image into the new image with scaling
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose(); // Free up system resources

        return resizedImage;
    }

    public int nearestMultiple(int n, int pixels){
        if (pixels == 0){
            return n;
        }else {
            int result = (int)(Math.ceil((double)pixels/n)*n);
            System.out.println("Nearest Multiple: "+result);
            return result;
        }
    }
}
