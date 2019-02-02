package chat.client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

public class EmotionCanvas extends Canvas implements CommonSettings {

    Dimension offDimension, dimension;
    Image offImage;
    Graphics offGraphics;
    ChatClient chatclient;
    ArrayList<MessageObject> IconArray;
    int count, XOffset, YOffset;
    MessageObject messageobject;
    ScrollView scrollview;
    String SelectedImage;
    PrivateChat privatechat;

    /**
     * ********Constructor Of Image Canvas ************
     */
    EmotionCanvas(ChatClient Parent, PrivateChat ParentPrivate) {
        chatclient = Parent;
        privatechat = ParentPrivate;
        dimension = size();
        IconArray = new ArrayList<>();
        setBackground(BACKGROUND);
        setFont(chatclient.getTextFont());
    }

    protected void AddIconsToMessageObject() {
        int StartX = IMAGE_CANVAS_START_POSITION;
        int StartY = IMAGE_CANVAS_START_POSITION;
        for (count = 1; count <= chatclient.getIconCount(); count++) {
            messageobject = new MessageObject();
            messageobject.message = (count - 1) + "";
            messageobject.startX = StartX;
            messageobject.startY = StartY;
            messageobject.isImage = true;
            messageobject.width = DEFAULT_ICON_WIDTH;
            messageobject.height = DEFAULT_ICON_HEIGHT;
            IconArray.add(messageobject);
            if (count % 6 == 0) {
                StartX = IMAGE_CANVAS_START_POSITION;
                StartY += DEFAULT_ICON_HEIGHT + DEFAULT_IMAGE_CANVAS_SPACE;
            } else {
                StartX += DEFAULT_ICON_WIDTH + DEFAULT_IMAGE_CANVAS_SPACE;
            }
        }

        scrollview.setValues(dimension.width, StartY);
        scrollview.setScrollPos(1, 1);
        scrollview.setScrollSteps(2, 1, DEFAULT_SCROLLING_HEIGHT);
        repaint();
    }

    private void PaintFrame(Graphics graphics) {
        int m_iconListSize = IconArray.size();
        for (count = 0; count < m_iconListSize; count++) {
            messageobject = IconArray.get(count);
            if ((messageobject.startY + messageobject.height) >= YOffset) {
                PaintImagesIntoCanvas(graphics, messageobject);
            }
        }
    }

    private void PaintImagesIntoCanvas(Graphics graphics, MessageObject messageObject) {
        int m_StartY = messageObject.startY - YOffset;
        if (messageobject.message.equals(SelectedImage)) {
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
            messageobject = IconArray.get(count);
            if ((CurrentY <= messageobject.startY + messageobject.height) && (i <= messageobject.startX + messageobject.width)) {
                SelectedImage = messageobject.message;
                repaint();
                break;
            }
            SelectedImage = null;
        }
        return true;
    }

    public boolean mouseDown(Event event, int i, int j) {
        int CurrentY = j + YOffset;
        int m_iconListSize = IconArray.size();
        for (count = 0; count < m_iconListSize; count++) {
            messageobject = IconArray.get(count);
            if ((CurrentY <= messageobject.startY + messageobject.height) && (i <= messageobject.startX + messageobject.width)) {
                privatechat.addImageToTextField(messageobject.message);
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
