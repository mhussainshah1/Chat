/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FrameWithIcon extends JFrame {

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                FrameWithIcon myFrame = new FrameWithIcon();
                myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                myFrame.setTitle("Frame with Icon");
                myFrame.setLayout(new BorderLayout());
                myFrame.setIconImage(loadImageIcon("images/icon.gif").getImage());
                Dimension size = new Dimension(250, 100);
                JPanel panel = new JPanel();
                panel.setPreferredSize(size);
                myFrame.add(panel, BorderLayout.LINE_START);
                myFrame.setVisible(true);
                myFrame.pack();
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    private static ImageIcon loadImageIcon(String path) {
        URL imgURL = FrameWithIcon.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
