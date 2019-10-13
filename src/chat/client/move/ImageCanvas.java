package chat.client.move;

import chat.client.MessageObject;
import java.awt.Dimension;
import java.awt.Canvas;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.Cursor;

public class ImageCanvas extends Canvas implements CommonSettings {

    Dimension offDimension, dimension;
    Image offImage;
    Graphics offGraphics;
    ChatClient chatclient;
    ArrayList<MessageObject> IconArray;
    int count, XOffset, YOffset;
    MessageObject messageObject;
    ScrollView scrollview;
    String selectedImage;

    ImageCanvas(ChatClient Parent) {
        chatclient = Parent;
        dimension = this.getSize();
        IconArray = new ArrayList<>();
    }

    protected void AddIconsToMessageObject() {
        int startX = IMAGE_CANVAS_START_POSITION;
        int startY = IMAGE_CANVAS_START_POSITION;
        for (count = 1; count <= chatclient.getIconCount(); count++) {
            messageObject = new MessageObject();
            messageObject.setMessage ((count - 1) + "");
            messageObject.setStartX(startX);
            messageObject.setStartY(startY);
            messageObject.setImage(true);
            messageObject.setWidth(DEFAULT_ICON_WIDTH);
            messageObject.setHeight(DEFAULT_ICON_HEIGHT);
            IconArray.add(messageObject);
            if (count % 3 == 0) {
                startX = IMAGE_CANVAS_START_POSITION;
                startY += DEFAULT_ICON_HEIGHT + DEFAULT_IMAGE_CANVAS_SPACE;
            } else {
                startX += DEFAULT_ICON_WIDTH + DEFAULT_IMAGE_CANVAS_SPACE;
            }
        }

        scrollview.setValues(dimension.width, startY);
        scrollview.setScrollPos(1, 1);
        scrollview.setScrollSteps(2, 1, DEFAULT_SCROLLING_HEIGHT);
        repaint();
    }

    private void PaintFrame(Graphics graphics) {
        int m_iconListSize = IconArray.size();
        for (count = 0; count < m_iconListSize; count++) {
            messageObject = IconArray.get(count);
            if ((messageObject.startY + messageObject.height) >= YOffset) {
                PaintImagesIntoCanvas(graphics, messageObject);
            }
        }
    }

    private void PaintImagesIntoCanvas(Graphics graphics, MessageObject messageObject) {
        int m_StartY = messageObject.startY - YOffset;
        if (this.messageObject.message.equals(selectedImage)) {
            graphics.draw3DRect(messageObject.startX - 2, m_StartY - 2, DEFAULT_ICON_WIDTH + 2, DEFAULT_ICON_HEIGHT + 2, true);
        }
        graphics.drawImage(chatclient.getIcon(Integer.parseInt(messageObject.message)), messageObject.startX, m_StartY, DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT, this);
        graphics.setColor(Color.black);
        graphics.drawString(ICON_NAME + messageObject.message, messageObject.startX - 1, m_StartY + DEFAULT_ICON_HEIGHT + 10);
    }

    public boolean handleEvent(Event event) {
        if (event.id == 1001 && event.arg == scrollview) {
            if (event.modifiers == 1) {
                XOffset = event.key;
            } else {
                YOffset = event.key;
            }
            repaint();
            return true;
        } else {
            return super.handleEvent(event);
        }
    }

    public boolean mouseEnter(Event event, int i, int j) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        return true;
    }

    public boolean mouseExit(Event event, int i, int j) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        return true;
    }

    public boolean mouseMove(Event event, int i, int j) {
        int CurrentY = j + YOffset;
        int m_iconListSize = IconArray.size();
        for (count = 0; count < m_iconListSize; count++) {
            messageObject = IconArray.get(count);
            if ((CurrentY <= messageObject.startY + messageObject.height) && (i <= messageObject.startX + messageObject.width)) {
                selectedImage = messageObject.message;
                repaint();
                break;
            }
            selectedImage = null;
        }
        return true;
    }

    public boolean mouseDown(Event event, int i, int j) {
        int CurrentY = j + YOffset;
        int m_iconListSize = IconArray.size();
        for (count = 0; count < m_iconListSize; count++) {
            messageObject = IconArray.get(count);
            if ((CurrentY <= messageObject.startY + messageObject.height) && (i <= messageObject.startX + messageObject.width)) {
                chatclient.addImageToTextField(messageObject.message);
                break;
            }
        }
        return true;
    }

    public void paint(Graphics graphics) {
        /**
         * ***********Double Buffering*************
         */
        dimension = size();

        /**
         * ********* Create the offscreen graphics context*************
         */
        if ((offGraphics == null) || (dimension.width != offDimension.width) || (dimension.height != offDimension.height)) {
            offDimension = dimension;
            offImage = createImage(dimension.width, dimension.height);
            offGraphics = offImage.getGraphics();
        }

        /**
         * ******* Erase the previous image********
         */
        offGraphics.setColor(Color.white);
        offGraphics.fillRect(0, 0, dimension.width, dimension.height);

        /**
         * ************* Paint the frame into the image****************
         */
        PaintFrame(offGraphics);

        /**
         * **************** Paint the image onto the screen************
         */
        graphics.drawImage(offImage, 0, 0, null);
    }

    public void update(Graphics graphics) {
        paint(graphics);
    }

}
