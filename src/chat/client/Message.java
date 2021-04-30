package chat.client;
//This class is updated

public class Message {
    
    private String text;//null
//    private int startX;
//    private int startY;
    private int width;
    private int height;//0
    private boolean image;
    private boolean selected;
    private boolean ignore; //false
    private int type;//0

    public Message() {
    }

    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    public int getStartX() {
//        return startX;
//    }
//
//    public void setStartX(int startX) {
//        this.startX = startX;
//    }
//
//    public int getStartY() {
//        return startY;
//    }
//
//    public void setStartY(int startY) {
//        this.startY = startY;
//    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }    
}
