package chat.client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

public class ListViewCanvas extends Canvas implements CommonSettings {

    Dimension offDimension, dimension;
    Image offImage;
    Graphics offGraphics;
    ChatClient chatClient;
    ArrayList<MessageObject> listArray;
    int count, xOffset, yOffset;
    MessageObject messageObject;
    ScrollView scrollView;
    FontMetrics fontmetrics;
    int canvasType, totalWidth, totalHeight;
    protected String selectedUser;

    /**
     * ********Constructor Of Image Canvas ************
     */
    ListViewCanvas(ChatClient parent, int canvasType) {
        chatClient = parent;
        dimension = getSize();
        listArray = new ArrayList<>();
        selectedUser = "";
        canvasType = canvasType;
        setFont(chatClient.getFont());
        fontmetrics = chatClient.getFontMetrics(chatClient.getFont());
    }

    protected void addListItemToMessageObject(String ListItem) {
        int m_startY = DEFAULT_LIST_CANVAS_POSITION;
        if (listArray.size() > 0) {
            messageObject = listArray.get(listArray.size() - 1);
            m_startY = messageObject.startY + DEFAULT_LIST_CANVAS_INCREMENT;
        }
        messageObject = new MessageObject();
        messageObject.message = ListItem;
        messageObject.startY = m_startY;
        messageObject.selected = false;
        messageObject.width = fontmetrics.stringWidth(ListItem) + DEFAULT_LIST_CANVAS_INCREMENT;
        listArray.add(messageObject);
        totalWidth = Math.max(totalWidth, messageObject.width);
        scrollView.setValues(totalWidth, m_startY + DEFAULT_LIST_CANVAS_HEIGHT);
        scrollView.setScrollPos(1, 1);
        scrollView.setScrollSteps(2, 1, DEFAULT_SCROLLING_HEIGHT);
        repaint();
    }

    /**
     * ***** Function To Clear All the Item From listArray ********
     */
    protected void clearAll() {
        listArray.clear();
        totalWidth = 0;
        totalHeight = 0;
        scrollView.setValues(totalWidth, totalHeight);
    }

    /**
     * ******Function To Get the Index of Give message from List Array ********
     */
    private int getIndexOf(String message) {
        int m_listSize = listArray.size();
        for (count = 0; count < m_listSize; count++) {
            messageObject = listArray.get(count);
            if (messageObject.message.equals(message)) {
                return count;
            }
        }

        return -1;

    }

    protected void ignoreUser(boolean isIgnore, String ignoreUserName) {
        int m_listIndex = getIndexOf(ignoreUserName);
        if (m_listIndex >= 0) {
            messageObject = listArray.get(m_listIndex);
            messageObject.isIgnored = isIgnore;
            listArray.set(m_listIndex, messageObject);

            if (isIgnore) {
                chatClient.getTapPanel().cmdIgnoreUser.setLabel("Allow User");
                chatClient.getMessageCanvas().addMessageToMessageObject(ignoreUserName + " has been ignored!", MESSAGE_TYPE_LEAVE);
            } else {
                chatClient.getTapPanel().cmdIgnoreUser.setLabel("Ignore User");
                chatClient.getMessageCanvas().addMessageToMessageObject(ignoreUserName + " has been romoved from ignored list!", MESSAGE_TYPE_JOIN);
            }
        }
    }

    /**
     * ********Set or Remove Ignore List from Array *******
     * @param isIgnore
     */
    protected void ignoreUser(boolean isIgnore) {
        if (selectedUser.equals("")) {
            chatClient.getMessageCanvas().addMessageToMessageObject("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedUser.equals(chatClient.getUserName())) {
            chatClient.getMessageCanvas().addMessageToMessageObject("You can not ignored yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }

        ignoreUser(isIgnore, selectedUser);

    }

    protected void SendDirectMessage() {
        if (selectedUser.equals("")) {
            chatClient.getMessageCanvas().addMessageToMessageObject("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedUser.equals(chatClient.getUserName())) {
            chatClient.getMessageCanvas().addMessageToMessageObject("You can not chat with yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }

        CreatePrivateWindow();
    }

    /**
     * ******** Check Whether the User ignored or not ********
     * @param userName
     * @return 
     */
    protected boolean isIgnoredUser(String userName) {
        int m_listIndex = getIndexOf(userName);
        if (m_listIndex >= 0) {
            messageObject = listArray.get(m_listIndex);
            return messageObject.isIgnored;
        }

        /**
         * **By Default***
         */
        return false;

    }

    /**
     * ******** Function To Remove the Given Item From the List Array *******
     * @param listItem
     */
    protected void removeListItem(String listItem) {
        int ListIndex = getIndexOf(listItem);
        if (ListIndex >= 0) {
            messageObject = listArray.get(ListIndex);
            int m_StartY = messageObject.startY;
            listArray.remove(ListIndex);
            int m_listSize = listArray.size();
            int m_nextStartY;
            for (count = ListIndex; count < m_listSize; count++) {
                messageObject = listArray.get(count);
                m_nextStartY = messageObject.startY;
                messageObject.startY = m_StartY;
                m_StartY = m_nextStartY;
            }

        }
        repaint();
    }

    private void paintFrame(Graphics graphics) {
        int m_listArraySize = listArray.size();
        for (count = 0; count < m_listArraySize; count++) {
            messageObject = listArray.get(count);
            if ((messageObject.startY + messageObject.height) >= yOffset) {
                paintListItemIntoCanvas(graphics, messageObject);
            }
        }
    }

    private void paintListItemIntoCanvas(Graphics graphics, MessageObject messageObject) {
        int m_StartY = messageObject.startY - yOffset;
        int m_imageIndex = ROOM_CANVAS_ICON;
        switch (canvasType) {
            case USER_CANVAS: {
                if (this.messageObject.isIgnored == true) {
                    m_imageIndex = USER_CANVAS_IGNORE_ICON;
                } else {
                    m_imageIndex = USER_CANVAS_NORMAL_ICON;
                }
                break;
            }
        }
        graphics.drawImage(chatClient.getIcon(m_imageIndex), 5 - xOffset, m_StartY, DEFAULT_LIST_CANVAS_HEIGHT, DEFAULT_LIST_CANVAS_HEIGHT, this);
        if (this.messageObject.selected) {
            graphics.setColor(Color.blue);
            graphics.fillRect(5 - xOffset + DEFAULT_LIST_CANVAS_HEIGHT, m_StartY, totalWidth, DEFAULT_LIST_CANVAS_INCREMENT);
            graphics.setColor(Color.white);
            graphics.drawString(messageObject.message, 5 - xOffset + DEFAULT_LIST_CANVAS_INCREMENT, m_StartY + DEFAULT_LIST_CANVAS_HEIGHT);
        } else {
            graphics.setColor(Color.white);
            graphics.fillRect(5 - xOffset + DEFAULT_LIST_CANVAS_HEIGHT, m_StartY, totalWidth, DEFAULT_LIST_CANVAS_INCREMENT);
            graphics.setColor(Color.black);
            graphics.drawString(messageObject.message, 5 - xOffset + DEFAULT_LIST_CANVAS_INCREMENT, m_StartY + DEFAULT_LIST_CANVAS_HEIGHT);
        }
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
    public boolean mouseDown(Event event, int i, int j) {
        int CurrentY = j + yOffset;
        int m_listArraySize = listArray.size();
        boolean SelectedFlag = false;
        chatClient.getTapPanel().txtUserCount.setText("");
        chatClient.getTapPanel().cmdIgnoreUser.setLabel("Ignore User");
        for (count = 0; count < m_listArraySize; count++) {
            messageObject = listArray.get(count);
            if ((CurrentY >= messageObject.startY) && (CurrentY <= (messageObject.startY + DEFAULT_LIST_CANVAS_HEIGHT))) {
                messageObject.selected = true;
                selectedUser = messageObject.message;
                SelectedFlag = true;

                if (canvasType == ROOM_CANVAS) {
                    chatClient.getRoomUserCount(selectedUser);
                }

                if (canvasType == USER_CANVAS) {
                    if (isIgnoredUser(selectedUser)) {
                        chatClient.getTapPanel().cmdIgnoreUser.setLabel("Allow User");
                    } else {
                        chatClient.getTapPanel().cmdIgnoreUser.setLabel("Ignore User");
                    }
                }
            } else {
                messageObject.selected = false;
            }
        }
        repaint();
        if ((!SelectedFlag)) {
            selectedUser = "";
        }

        if ((event.clickCount == 2) && (canvasType == USER_CANVAS) && (!(selectedUser.equals(""))) && (!(selectedUser.equals(chatClient.getUserName())))) {
            CreatePrivateWindow();
        }

        return true;
    }

    private void CreatePrivateWindow() {
        /**
         * ** Chk whether ignored user ********
         */
        if (!(isIgnoredUser(selectedUser))) {
            boolean PrivateFlag = false;
            for (count = 0; count < chatClient.privateWindowCount; count++) {
                if (chatClient.privateWindows[count].userName.equals(selectedUser)) {
                    chatClient.privateWindows[count].show();
                    chatClient.privateWindows[count].requestFocus();
                    PrivateFlag = true;
                    break;
                }
            }

            if (!(PrivateFlag)) {
                if (chatClient.privateWindowCount >= MAX_PRIVATE_WINDOW) {
                    chatClient.getMessageCanvas().addMessageToMessageObject("You are Exceeding private window limit! So you may lose some message from your friends!", MESSAGE_TYPE_ADMIN);
                } else {
                    chatClient.privateWindows[chatClient.privateWindowCount++] = new PrivateChat(chatClient, selectedUser);
                    chatClient.privateWindows[chatClient.privateWindowCount - 1].show();
                    chatClient.privateWindows[chatClient.privateWindowCount - 1].requestFocus();
                }
            }

        }
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
