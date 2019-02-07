package chat.client.move;

import java.awt.Dimension;
import java.awt.Canvas;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.StringTokenizer;

public class MessageCanvas extends Canvas implements CommonSettings {

    Dimension offDimension, dimension;
    Image offImage;
    Graphics offGraphics;
    ChatClient chatclient;
    ArrayList<MessageObject> MessageArray;
    int count, XOffset, YOffset, HorizantalSpace;
    MessageObject messageobject;
    ScrollView scrollview;
    FontMetrics fontmetrics;
    int TotalWidth, MessageCount, TotalHeight;
    Font UserNameFont, TextFont;
    StringTokenizer tokenizer;
    String TokenString;

    /**
     * ********Constructor Of Image Canvas ************
     */
    MessageCanvas(ChatClient Parent) {
        chatclient = Parent;
        /**
         * *** Waiting for Loading Chat	Client *********
         */
        dimension = chatclient.getSize();
        MessageArray = new ArrayList<>();
        MessageCount = 0;
        TotalWidth = 0;
        HorizantalSpace = 2;
        UserNameFont = chatclient.getFont();
        TextFont = chatclient.getTextFont();
        setFont(chatclient.getFont());
        fontmetrics = chatclient.getFontMetrics(chatclient.getFont());
    }

    /**
     * **** Function To Clear All the Item From ListArray ********
     */
    protected void ClearAll() {
        MessageArray.clear();
        TotalWidth = 0;
        TotalHeight = 0;
        MessageCount = 0;
        scrollview.setValues(TotalWidth, TotalHeight);
    }

    protected void addMessageToMessageObject(String Message, int MessageType) {
        String m_Message = "";
        tokenizer = new StringTokenizer(Message, " ");
        while (tokenizer.hasMoreTokens()) {
            TokenString = tokenizer.nextToken();
            if (fontmetrics.stringWidth(m_Message + TokenString) < dimension.width) {
                m_Message = m_Message + TokenString + " ";
            } else {
                AddMessage(m_Message, MessageType);
                m_Message = "";
            }
        }

        AddMessage(m_Message, MessageType);
    }

    private void AddMessage(String Message, int MessageType) {
        int m_startY = DEFAULT_MESSAGE_CANVAS_POSITION;
        if (MessageArray.size() > 0) {
            messageobject = MessageArray.get(MessageArray.size() - 1);
            m_startY = messageobject.startY + messageobject.height;
        }

        messageobject = new MessageObject();
        messageobject.message = Message;
        messageobject.startY = m_startY;
        messageobject.MessageType = MessageType;
        /**
         * *****Is Image True********
         */
        if (Message.indexOf("~~") >= 0) {
            messageobject.isImage = true;
            messageobject.width = DEFAULT_ICON_WIDTH;
            messageobject.height = DEFAULT_ICON_HEIGHT;
        } else {
            messageobject.isImage = false;
            messageobject.width = fontmetrics.stringWidth(Message);
            messageobject.height = fontmetrics.getHeight() + fontmetrics.getDescent();
        }
        MessageArray.add(messageobject);
        MessageCount++;
        TotalWidth = Math.max(TotalWidth, messageobject.width);
        TotalHeight = m_startY + messageobject.height;
        scrollview.setValues(TotalWidth, TotalHeight);

        int m_Height = TotalHeight - YOffset;
        if (m_Height > dimension.height) {
            YOffset = TotalHeight - dimension.height;
        }
        scrollview.setScrollPos(2, 2);
        scrollview.setScrollSteps(2, 1, DEFAULT_SCROLLING_HEIGHT);
        repaint();
    }

    private void PaintFrame(Graphics graphics) {
        if (MessageCount < 1) {
            return;
        }
        int m_YPos = YOffset + dimension.height;
        int m_StartPos = 0;
        int m_listArraySize = MessageArray.size();
        for (count = 0; count < MessageCount && m_StartPos < m_YPos; count++) {
            if (m_listArraySize < count) {
                return;
            }
            messageobject = MessageArray.get(count);
            if (messageobject.startY >= YOffset) {
                PaintMessageIntoCanvas(graphics, messageobject);
                m_StartPos = messageobject.startY;
            }
        }

        if (count < MessageCount) {
            messageobject = MessageArray.get(count);
            PaintMessageIntoCanvas(graphics, messageobject);
        }
    }

    private void PaintMessageIntoCanvas(Graphics graphics, MessageObject messageObject) {
        graphics.setColor(Color.black);
        int m_YPos = messageobject.startY - YOffset;
        int m_XPos = 5 - XOffset;
        int CustomWidth = 0;
        String Message = messageobject.message;
        /**
         * ***********Print The User Name in UserName Font *************
         */
        if (Message.indexOf(":") >= 0) {
            graphics.setFont(UserNameFont);
            chatclient.getGraphics().setFont(UserNameFont);
            fontmetrics = chatclient.getGraphics().getFontMetrics();
            String m_UserName = Message.substring(0, Message.indexOf(":") + 1);
            graphics.drawString(m_UserName, m_XPos + CustomWidth, m_YPos);
            CustomWidth += fontmetrics.stringWidth(m_UserName) + HorizantalSpace;
            Message = Message.substring(Message.indexOf(":") + 1);
        }
        /**
         * *******Set the Text Font *********
         */
        chatclient.getGraphics().setFont(TextFont);
        graphics.setFont(TextFont);
        fontmetrics = chatclient.getGraphics().getFontMetrics();

        /**
         * ********Print Image Area*******
         */
        if (messageobject.isImage) {
            tokenizer = new StringTokenizer(Message, " ");
            while (tokenizer.hasMoreTokens()) {
                TokenString = tokenizer.nextToken();
                if (TokenString.indexOf("~~") >= 0) {
                    /**
                     * ******If its a Proper Image************
                     */
                    try {
                        int m_ImageIndex = Integer.parseInt(TokenString.substring(2));
                        if ((m_ImageIndex >= 0) && (m_ImageIndex < chatclient.getIconCount())) {
                            graphics.drawImage(chatclient.getIcon(m_ImageIndex), m_XPos + CustomWidth, m_YPos - 15, messageobject.width, messageobject.height, this);
                            CustomWidth += messageobject.width + HorizantalSpace;

                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    graphics.drawString(TokenString, m_XPos + CustomWidth, m_YPos);
                    CustomWidth += fontmetrics.stringWidth(TokenString) + HorizantalSpace;
                }
                if (TotalWidth < m_XPos + CustomWidth) {
                    TotalWidth = m_XPos + CustomWidth;
                    scrollview.setValues(TotalWidth, TotalHeight);
                }
            }

        } /**
         * **Not a Image*******
         */
        else {

            switch (messageobject.MessageType) {
                case MESSAGE_TYPE_DEFAULT: {
                    graphics.setColor(Color.black);
                    break;
                }
                case MESSAGE_TYPE_JOIN: {
                    graphics.setColor(Color.blue);
                    break;
                }
                case MESSAGE_TYPE_LEAVE: {
                    graphics.setColor(Color.red);
                    break;
                }
                case MESSAGE_TYPE_ADMIN: {
                    graphics.setColor(Color.gray);
                    break;
                }
            }
            graphics.drawString(Message, m_XPos + CustomWidth, m_YPos);
        }

        graphics.setFont(UserNameFont);
        chatclient.getGraphics().setFont(UserNameFont);
        fontmetrics = chatclient.getGraphics().getFontMetrics();
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
