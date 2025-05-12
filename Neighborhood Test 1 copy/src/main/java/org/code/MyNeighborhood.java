package org.code;

import javax.swing.JFrame;

import org.code.neighborhood.Painter;
import org.code.neighborhood.gui.PainterVisualizer;

public class MyNeighborhood {

    public static void main(String[] args) {
        Painter myPainter = new Painter(0,0,"east",12);

        // Launch GUI
        PainterVisualizer visualizer = new PainterVisualizer(myPainter);
        myPainter.setVisualizer(visualizer);
        JFrame frame = new JFrame("Neighborhood Visualizer");
        frame.add(visualizer);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        //move and paint with your painter

    }

    
    
}
