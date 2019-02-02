package extra;

import java.awt.Panel;
import java.awt.Dimension;

class CustomPanel extends Panel {

    private Dimension dimension;

    public CustomPanel(int i, int j) {
        dimension = new Dimension(i, j);
        setSize(dimension);//resize(dimension);
        validate();
    }

    @Override
    public Dimension minimumSize() {
        return dimension;
    }

    @Override
    public Dimension preferredSize() {
        return getSize();//size();
    }

}
