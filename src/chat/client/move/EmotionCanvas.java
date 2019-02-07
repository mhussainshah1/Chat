package chat.client.move;

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
    ChatClient chatClient;
    ArrayList<MessageObject> iconArray;
    int count, xOffset, yOffset;
    MessageObject messageObject;
    ScrollView scrollView;
    String selectedImage;
    PrivateChat privateChat;

    /**
     * ********Constructor Of Image Canvas ************
     */
    EmotionCanvas(ChatClient parent, PrivateChat parentPrivate) {
        chatClient = parent;
        privateChat = parentPrivate;
        dimension = getSize();//size();
        iconArray = new ArrayList<>();
        setBackground(BACKGROUND);
        setFont(chatClient.getTextFont());
    }

    protected void addIconsToMessageObject() {
        int StartX = IMAGE_CANVAS_START_POSITION;
        int StartY = IMAGE_CANVAS_START_POSITION;
        for (count = 1; count <= chatClient.getIconCount(); count++) {
            messageObject = new MessageObject();
            messageObject.message = (count - 1) + "";
            messageObject.startX = StartX;
            messageObject.startY = StartY;
            messageObject.isImage = true;
            messageObject.width = DEFAULT_ICON_WIDTH;
            messageObject.height = DEFAULT_ICON_HEIGHT;
            iconArray.add(messageObject);
            if (count % 6 == 0) {
                StartX = IMAGE_CANVAS_START_POSITION;
                StartY += DEFAULT_ICON_HEIGHT + DEFAULT_IMAGE_CANVAS_SPACE;
            } else {
                StartX += DEFAULT_ICON_WIDTH + DEFAULT_IMAGE_CANVAS_SPACE;
            }
        }

        scrollView.setValues(dimension.width, StartY);
        scrollView.setScrollPos(1, 1);
        scrollView.setScrollSteps(2, 1, DEFAULT_SCROLLING_HEIGHT);
        repaint();
    }

    private void paintFrame(Graphics graphics) {
        int m_iconListSize = iconArray.size();
        for (count = 0; count < m_iconListSize; count++) {
            messageObject = iconArray.get(count);
            if ((messageObject.startY + messageObject.height) >= yOffset) {
                paintImagesIntoCanvas(graphics, messageObject);
            }
        }
    }

    private void paintImagesIntoCanvas(Graphics graphics, MessageObject messageObject) {
        int m_StartY = messageObject.startY - yOffset;
        if (this.messageObject.message.equals(selectedImage)) {
            graphics.draw3DRect(messageObject.startX - 2, m_StartY - 2, DEFAULT_ICON_WIDTH + 2, DEFAULT_ICON_HEIGHT + 2, true);
        }
        graphics.drawImage(chatClient.getIcon(Integer.parseInt(messageObject.message)), messageObject.startX, m_StartY, DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT, this);
        graphics.setColor(Color.black);
        graphics.drawString(ICON_NAME + messageObject.message, messageObject.startX - 1, m_StartY + DEFAULT_ICON_HEIGHT + 10);
    }

    @Override
    public boolean handleEvent(Event event) {
        if (event.id == 1001 && event.arg == scrollView) {
            if (event.modifiers == 1) {
                xOffset = event.key;
            } else {
                yOffset = event.key;
            }
            repaint();
            return true;
        } else {
            return super.handleEvent(event);
        }
    }

    @Override
    public boolean mouseEnter(Event event, int i, int j) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        return true;
    }

    @Override
    public boolean mouseExit(Event event, int i, int j) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        return true;
    }

    @Override
    public boolean mouseMove(Event event, int i, int j) {
        int CurrentY = j + yOffset;
        int m_iconListSize = iconArray.size();
        for (count = 0; count < m_iconListSize; count++) {
            messageObject = iconArray.get(count);
            if ((CurrentY <= messageObject.startY + messageObject.height) && (i <= messageObject.startX + messageObject.width)) {
                selectedImage = messageObject.message;
                repaint();
                break;
            }
            selectedImage = null;
        }
        return true;
    }

    @Override
    public boolean mouseDown(Event event, int i, int j) {
        int CurrentY = j + yOffset;
        int m_iconListSize = iconArray.size();
        for (count = 0; count < m_iconListSize; count++) {
            messageObject = iconArray.get(count);
            if ((CurrentY <= messageObject.startY + messageObject.height) && (i <= messageObject.startX + messageObject.width)) {
                privateChat.addImageToTextField(messageObject.message);
                break;
            }
        }
        return true;
    }

    @Override
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
        paintFrame(offGraphics);

        /**
         * **************** Paint the image onto the screen************
         */
        graphics.drawImage(offImage, 0, 0, null);
    }

    @Override
    public void update(Graphics graphics) {
        paint(graphics);
    }

}
