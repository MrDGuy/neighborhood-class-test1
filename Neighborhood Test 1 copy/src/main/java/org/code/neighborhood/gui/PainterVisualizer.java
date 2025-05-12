package org.code.neighborhood.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.code.neighborhood.Painter;
import org.code.neighborhood.support.GridSquare;
import org.code.neighborhood.support.NeighborhoodRuntimeException;

public class PainterVisualizer extends JPanel {
    private final int tileSize = 32;
    private final int gridWidth;
    private final int gridHeight;

    private org.code.neighborhood.Painter painter;
    private BufferedImage painterImage;
    private BufferedImage backgroundImage;
    private Map<Integer, BufferedImage> tileImages = new HashMap<>();
    private Map<String, BufferedImage> painterImages = new HashMap<>();

    public PainterVisualizer(Painter painter) {
        this.painter = painter;
        this.gridWidth = painter.getGrid().getWidth();
        this.gridHeight = painter.getGrid().getHeight();

        try {
            backgroundImage = ImageIO.read(new File("resources/background.png"));
        } catch (IOException e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }

        try {
            painterImage = ImageIO.read(new File("resources/painter.png"));
        } catch (IOException e) {
            System.err.println("Failed to load painter image: " + e.getMessage());
        }

        try {
            BufferedImage spriteSheet = ImageIO.read(new File("resources/sprite_sheet.png"));
            int tilesPerRow = spriteSheet.getWidth() / tileSize;
            int tilesPerCol = spriteSheet.getHeight() / tileSize;

            int assetId = 0;
            for (int y = 0; y < tilesPerCol; y++) {
                for (int x = 0; x < tilesPerRow; x++) {
                    BufferedImage tile = spriteSheet.getSubimage(
                        x * tileSize, y * tileSize, tileSize, tileSize
                    );
                    tileImages.put(assetId, tile);
                    assetId++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        
        
        loadPainterImages();
        setPreferredSize(new Dimension(tileSize * gridWidth, tileSize * gridHeight));
    }

    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (backgroundImage != null) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
    }

    for (int y = 0; y < gridHeight; y++) {
        for (int x = 0; x < gridWidth; x++) {
            GridSquare square = null;

            try {
                square = painter.getGrid().getSquare(x, y);
            } catch (NeighborhoodRuntimeException e) {
                // Log only if it's out of bounds — should not happen if grid is set up properly
                System.err.println("Invalid coordinate at (" + x + "," + y + ")");
                continue;
            }

            // Draw tile image if available
            BufferedImage tile = tileImages.get(square.getAssetID());
            if (tile != null) {
                g.drawImage(tile, x * tileSize, y * tileSize, tileSize, tileSize, null);
            } else {
                // Fallback color if no tile image found
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }

            // Draw paint overlay if painted
            if (square.getColor() != null) {
                g.setColor(square.getColor());
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }

            // // OPTIONAL: draw a transparent overlay on walls
            // if (!square.isPassable()) {
            //     g.setColor(new Color(0, 0, 0, 100)); // translucent black
            //     g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            // }
        }
    }

        // Draw the painter
        String facing = painter.getDirection(); // e.g. "NORTH"
        BufferedImage painterSprite = painterImages.get(facing);
        if (painterSprite != null) {
            g.drawImage(painterSprite, painter.getX() * tileSize, painter.getY() * tileSize, tileSize, tileSize, null);
        }
    }

    public void updatePainter(Painter painter) {
        this.painter = painter;
        repaint();
    }

    private void loadPainterImages() {
        try {
            BufferedImage base = ImageIO.read(new File("resources/painter.png"));
            painterImages.put("east", base); // default
    
            painterImages.put("north", rotateImage(base, -90));
            painterImages.put("south", rotateImage(base, 90));
            painterImages.put("west", rotateImage(base, 180));
        } catch (IOException e) {
            System.err.println("Painter image load failed: " + e.getMessage());
        }
    }

    private BufferedImage rotateImage(BufferedImage img, double angleDegrees) {
        int size = tileSize; // force square canvas
        BufferedImage rotated = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
    
        AffineTransform at = new AffineTransform();
        at.translate(size / 2.0, size / 2.0);
        at.rotate(Math.toRadians(angleDegrees));
        at.translate(-img.getWidth() / 2.0, -img.getHeight() / 2.0);
    
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
    
        return rotated;
    }
}