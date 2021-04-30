package chat.client;

import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

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
    public synchronized Component getListCellRendererComponent(
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
        String name = icon.getDescription();
        //System.out.println(name);
        setIcon(icon);
        if (icon != null) {
            setText(name);
            setFont(list.getFont());
        } else {
            setUhOhText(name + " (no image available)", list.getFont());
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
