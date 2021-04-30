package help;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class ListDemo extends JPanel {

    private List<ImageIcon> images;
    private List<String> userNames;
    private DefaultListModel<ImageIcon> listModel;

    /*
     * Despite its use of EmptyBorder, this panel makes a fine content
     * pane because the empty border just increases the panel's size
     * and is "painted" on top of the panel's normal background.  In
     * other words, the JPanel fills its entire background if it's
     * opaque (which it is by default); adding a border doesn't change
     * that.
     */
    public ListDemo() {
        super(new BorderLayout());

        //Load the user images and create a list of names.
        images = new ArrayList<>();
        userNames = Arrays.asList("Amir", "Ali", "Mo", "Cat", "Sam");
        for (String userName : userNames) {
            images.add(createImageIcon("images/icon.gif", userName));
        }
        listModel = new DefaultListModel<>();
        listModel.addAll(images);

        //Create the List.
        JList<ImageIcon> userList = new JList<>(listModel);
        userList.setCellRenderer(new ListRenderer());
       
        //Lay out the demo.
        add(userList, BorderLayout.PAGE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * @param path
     * @param description
     * @return 
     */
    protected static ImageIcon createImageIcon(String path, String description) {
        URL imgURL = ListDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ListDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new ListDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    class ListRenderer extends JLabel
            implements ListCellRenderer<ImageIcon> {

        private Font uhOhFont;

        public ListRenderer() {
            setOpaque(true);
        }

        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        @Override
        public Component getListCellRendererComponent(
                JList<? extends ImageIcon> list,
                ImageIcon icon,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            //Set the icon and text.  If icon was null, say so.
            String userName = userNames.get(index);
            setIcon(icon);
            if (icon != null) {
                setText(userName);
                setFont(list.getFont());
            } else {
                setUhOhText(userName + " (no image available)", list.getFont());
            }
            return this;
        }

        //Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { //lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            setFont(uhOhFont);
            setText(uhOhText);
        }
    }
}
