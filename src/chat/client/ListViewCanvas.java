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
    ChatClient chatclient;
    ArrayList<MessageObject> ListArray;
    int count, XOffset, YOffset;
    MessageObject messageobject;
    ScrollView scrollview;
    FontMetrics fontmetrics;
    int CanvasType, TotalWidth, TotalHeight;
    protected String SelectedUser;

    /**
     * ********Constructor Of Image Canvas ************
     */
    ListViewCanvas(ChatClient Parent, int canvastype) {
        chatclient = Parent;
        dimension = getSize();
        ListArray = new ArrayList();
        SelectedUser = "";
        CanvasType = canvastype;
        setFont(chatclient.getFont());
        fontmetrics = chatclient.getFontMetrics(chatclient.getFont());
    }

    protected void AddListItemToMessageObject(String ListItem) {
        int m_startY = DEFAULT_LIST_CANVAS_POSITION;
        if (ListArray.size() > 0) {
            messageobject = ListArray.get(ListArray.size() - 1);
            m_startY = messageobject.startY + DEFAULT_LIST_CANVAS_INCREMENT;
        }
        messageobject = new MessageObject();
        messageobject.message = ListItem;
        messageobject.startY = m_startY;
        messageobject.selected = false;
        messageobject.width = fontmetrics.stringWidth(ListItem) + DEFAULT_LIST_CANVAS_INCREMENT;
        ListArray.add(messageobject);
        TotalWidth = Math.max(TotalWidth, messageobject.width);
        scrollview.setValues(TotalWidth, m_startY + DEFAULT_LIST_CANVAS_HEIGHT);
        scrollview.setScrollPos(1, 1);
        scrollview.setScrollSteps(2, 1, DEFAULT_SCROLLING_HEIGHT);
        repaint();
    }

    /**
     * **** Function To Clear All the Item From ListArray ********
     */
    protected void ClearAll() {
        ListArray.clear();
        TotalWidth = 0;
        TotalHeight = 0;
        scrollview.setValues(TotalWidth, TotalHeight);
    }

    /**
     * ******Function To Get the Index of Give message from List Array ********
     */
    private int GetIndexOf(String Message) {
        int m_listSize = ListArray.size();
        for (count = 0; count < m_listSize; count++) {
            messageobject = ListArray.get(count);
            if (messageobject.message.equals(Message)) {
                return count;
            }
        }

        return -1;

    }

    protected void IgnoreUser(boolean IsIgnore, String IgnoreUserName) {
        int m_listIndex = GetIndexOf(IgnoreUserName);
        if (m_listIndex >= 0) {
            messageobject = ListArray.get(m_listIndex);
            messageobject.isIgnored = IsIgnore;
            ListArray.set(m_listIndex, messageobject);

            if (IsIgnore) {
                chatclient.getTapPanel().CmdIgnoreUser.setLabel("Allow User");
                chatclient.getMessageCanvas().addMessageToMessageObject(IgnoreUserName + " has been ignored!", MESSAGE_TYPE_LEAVE);
            } else {
                chatclient.getTapPanel().CmdIgnoreUser.setLabel("Ignore User");
                chatclient.getMessageCanvas().addMessageToMessageObject(IgnoreUserName + " has been romoved from ignored list!", MESSAGE_TYPE_JOIN);
            }
        }
    }

    /**
     * ********Set or Remove Ignore List from Array *******
     */
    protected void IgnoreUser(boolean IsIgnore) {
        if (SelectedUser.equals("")) {
            chatclient.getMessageCanvas().addMessageToMessageObject("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (SelectedUser.equals(chatclient.getUserName())) {
            chatclient.getMessageCanvas().addMessageToMessageObject("You can not ignored yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }

        IgnoreUser(IsIgnore, SelectedUser);

    }

    protected void SendDirectMessage() {
        if (SelectedUser.equals("")) {
            chatclient.getMessageCanvas().addMessageToMessageObject("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (SelectedUser.equals(chatclient.getUserName())) {
            chatclient.getMessageCanvas().addMessageToMessageObject("You can not chat with yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }

        CreatePrivateWindow();
    }

    /**
     * ******** Check Whether the User ignored or not ********
     */
    protected boolean IsIgnoredUser(String UserName) {
        int m_listIndex = GetIndexOf(UserName);
        if (m_listIndex >= 0) {
            messageobject = ListArray.get(m_listIndex);
            return messageobject.isIgnored;
        }

        /**
         * **By Fefault***
         */
        return false;

    }

    /**
     * ******** Function To Remove the Given Item From the List Array *******
     */
    protected void RemoveListItem(String ListItem) {
        int ListIndex = GetIndexOf(ListItem);
        if (ListIndex >= 0) {
            messageobject = ListArray.get(ListIndex);
            int m_StartY = messageobject.startY;
            ListArray.remove(ListIndex);
            int m_listSize = ListArray.size();
            int m_nextStartY;
            for (count = ListIndex; count < m_listSize; count++) {
                messageobject = ListArray.get(count);
                m_nextStartY = messageobject.startY;
                messageobject.startY = m_StartY;
                m_StartY = m_nextStartY;
            }

        }
        repaint();
    }

    private void PaintFrame(Graphics graphics) {
        int m_listArraySize = ListArray.size();
        for (count = 0; count < m_listArraySize; count++) {
            messageobject = ListArray.get(count);
            if ((messageobject.startY + messageobject.height) >= YOffset) {
                PaintListItemIntoCanvas(graphics, messageobject);
            }
        }
    }

    private void PaintListItemIntoCanvas(Graphics graphics, MessageObject messageObject) {
        int m_StartY = messageObject.startY - YOffset;
        int m_imageIndex = ROOM_CANVAS_ICON;
        switch (CanvasType) {
            case USER_CANVAS: {
                if (messageobject.isIgnored == true) {
                    m_imageIndex = USER_CANVAS_IGNORE_ICON;
                } else {
                    m_imageIndex = USER_CANVAS_NORMAL_ICON;
                }
                break;
            }
        }
        graphics.drawImage(chatclient.getIcon(m_imageIndex), 5 - XOffset, m_StartY, DEFAULT_LIST_CANVAS_HEIGHT, DEFAULT_LIST_CANVAS_HEIGHT, this);
        if (messageobject.selected) {
            graphics.setColor(Color.blue);
            graphics.fillRect(5 - XOffset + DEFAULT_LIST_CANVAS_HEIGHT, m_StartY, TotalWidth, DEFAULT_LIST_CANVAS_INCREMENT);
            graphics.setColor(Color.white);
            graphics.drawString(messageObject.message, 5 - XOffset + DEFAULT_LIST_CANVAS_INCREMENT, m_StartY + DEFAULT_LIST_CANVAS_HEIGHT);
        } else {
            graphics.setColor(Color.white);
            graphics.fillRect(5 - XOffset + DEFAULT_LIST_CANVAS_HEIGHT, m_StartY, TotalWidth, DEFAULT_LIST_CANVAS_INCREMENT);
            graphics.setColor(Color.black);
            graphics.drawString(messageObject.message, 5 - XOffset + DEFAULT_LIST_CANVAS_INCREMENT, m_StartY + DEFAULT_LIST_CANVAS_HEIGHT);
        }
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

    public boolean mouseDown(Event event, int i, int j) {
        int CurrentY = j + YOffset;
        int m_listArraySize = ListArray.size();
        boolean SelectedFlag = false;
        chatclient.getTapPanel().TxtUserCount.setText("");
        chatclient.getTapPanel().CmdIgnoreUser.setLabel("Ignore User");
        for (count = 0; count < m_listArraySize; count++) {
            messageobject = ListArray.get(count);
            if ((CurrentY >= messageobject.startY) && (CurrentY <= (messageobject.startY + DEFAULT_LIST_CANVAS_HEIGHT))) {
                messageobject.selected = true;
                SelectedUser = messageobject.message;
                SelectedFlag = true;

                if (CanvasType == ROOM_CANVAS) {
                    chatclient.getRoomUserCount(SelectedUser);
                }

                if (CanvasType == USER_CANVAS) {
                    if (IsIgnoredUser(SelectedUser)) {
                        chatclient.getTapPanel().CmdIgnoreUser.setLabel("Allow User");
                    } else {
                        chatclient.getTapPanel().CmdIgnoreUser.setLabel("Ignore User");
                    }
                }
            } else {
                messageobject.selected = false;
            }
        }
        repaint();
        if ((!SelectedFlag)) {
            SelectedUser = "";
        }

        if ((event.clickCount == 2) && (CanvasType == USER_CANVAS) && (!(SelectedUser.equals(""))) && (!(SelectedUser.equals(chatclient.getUserName())))) {
            CreatePrivateWindow();
        }

        return true;
    }

    private void CreatePrivateWindow() {
        /**
         * ** Chk whether ignored user ********
         */
        if (!(IsIgnoredUser(SelectedUser))) {
            boolean PrivateFlag = false;
            for (count = 0; count < chatclient.PrivateWindowCount; count++) {
                if (chatclient.privatewindow[count].UserName.equals(SelectedUser)) {
                    chatclient.privatewindow[count].show();
                    chatclient.privatewindow[count].requestFocus();
                    PrivateFlag = true;
                    break;
                }
            }

            if (!(PrivateFlag)) {
                if (chatclient.PrivateWindowCount >= MAX_PRIVATE_WINDOW) {
                    chatclient.getMessageCanvas().addMessageToMessageObject("You are Exceeding private window limit! So you may lose some message from your friends!", MESSAGE_TYPE_ADMIN);
                } else {
                    chatclient.privatewindow[chatclient.PrivateWindowCount++] = new PrivateChat(chatclient, SelectedUser);
                    chatclient.privatewindow[chatclient.PrivateWindowCount - 1].show();
                    chatclient.privatewindow[chatclient.PrivateWindowCount - 1].requestFocus();
                }
            }

        }
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
