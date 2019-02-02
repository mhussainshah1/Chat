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
            messageobject.Message = (count - 1) + "";
            messageobject.StartX = StartX;
            messageobject.StartY = StartY;
            messageobject.IsImage = true;
            messageobject.Width = DEFAULT_ICON_WIDTH;
            messageobject.Height = DEFAULT_ICON_HEIGHT;
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
            if ((messageobject.StartY + messageobject.Height) >= YOffset) {
                PaintImagesIntoCanvas(graphics, messageobject);
            }
        }
    }

    private void PaintImagesIntoCanvas(Graphics graphics, MessageObject messageObject) {
        int m_StartY = messageObject.StartY - YOffset;
        if (messageobject.Message.equals(SelectedImage)) {
            graphics.draw3DRect(messageObject.StartX - 2, m_StartY - 2, DEFAULT_ICON_WIDTH + 2, DEFAULT_ICON_HEIGHT + 2, true);
        }
        graphics.drawImage(chatclient.getIcon(Integer.parseInt(messageObject.Message)), messageObject.StartX, m_StartY, DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT, this);
        graphics.setColor(Color.black);
        graphics.drawString(ICON_NAME + messageObject.Message, messageObject.StartX - 1, m_StartY + DEFAULT_ICON_HEIGHT + 10);
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
            if ((CurrentY <= messageobject.StartY + messageobject.Height) && (i <= messageobject.StartX + messageobject.Width)) {
                SelectedImage = messageobject.Message;
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
            if ((CurrentY <= messageobject.StartY + messageobject.Height) && (i <= messageobject.StartX + messageobject.Width)) {
                privatechat.addImageToTextField(messageobject.Message);
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
