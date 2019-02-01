package chat.client;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PrivateChat extends Frame implements CommonSettings, KeyListener, ActionListener {

    private ChatClient chatclient;
    protected String UserName;
    private TextField TxtMessage;
    private boolean EmotionFlag;

    private Button CmdSend, CmdClose, CmdIgnore, CmdClear, CmdEmoticons;
    private EmotionCanvas emotioncanvas;
    private ScrollView EmotionScrollView, MessageScrollView;
    private MessageCanvas messageCanvas;//Replace with JtextPane
    private Panel EmotionPanel;

    PrivateChat(ChatClient Parent, String ToUserName) {
        chatclient = Parent;
        UserName = ToUserName;
        setTitle("Private Chat With " + UserName);
        Image IconImage = Toolkit.getDefaultToolkit().getImage("images/logo.gif");
        setIconImage(IconImage);
        setBackground(BACKGROUND);
        setFont(chatclient.getFont());
        EmotionFlag = false;
        initializeComponents();
        /**
         * **Window Closing Event ****
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                exitPrivateWindow();
            }
        });
    }

    public MessageCanvas getMessageCanvas() {
        return messageCanvas;
    }

    /**
     * ***** Initialize All Components *********
     */
    private void initializeComponents() {
        setLayout(null);
        Label LblConversation = new Label("Conversation With " + UserName);
        LblConversation.setForeground(MESSAGE_CANVAS);
        LblConversation.setBounds(5, 30, 400, 20);
        add(LblConversation);

        Panel MessagePanel = new Panel(new BorderLayout());
        messageCanvas = new MessageCanvas(chatclient);
        MessageScrollView = new ScrollView(messageCanvas, true, true, 
                TAPPANEL_CANVAS_WIDTH, TAPPANEL_CANVAS_HEIGHT, SCROLL_BAR_SIZE);
        messageCanvas.scrollview = MessageScrollView;
        MessagePanel.add("Center", MessageScrollView);
        MessagePanel.setBounds(5, 50, 400, 200);
        add(MessagePanel);

        TxtMessage = new TextField();
        TxtMessage.addKeyListener(this);
        TxtMessage.setFont(chatclient.getTextFont());
        TxtMessage.setBounds(5, 260, 320, 20);
        add(TxtMessage);

        CmdSend = new CustomButton(chatclient, "Send");
        CmdSend.addActionListener(this);
        CmdSend.setBounds(335, 260, 70, 20);
        add(CmdSend);

        CmdClear = new CustomButton(chatclient, "Clear");
        CmdClear.addActionListener(this);
        CmdClear.setBounds(5, 290, 80, 20);

        CmdIgnore = new CustomButton(chatclient, "Ignore User");
        CmdIgnore.addActionListener(this);
        CmdIgnore.setBounds(105, 290, 80, 20);

        CmdClose = new CustomButton(chatclient, "Close");
        CmdClose.addActionListener(this);
        CmdClose.setBounds(205, 290, 80, 20);

        CmdEmoticons = new CustomButton(chatclient, "Emoticons");
        CmdEmoticons.addActionListener(this);
        CmdEmoticons.setBounds(305, 290, 80, 20);

        add(CmdClear);
        add(CmdIgnore);
        add(CmdClose);
        add(CmdEmoticons);

        EmotionPanel = new Panel(new BorderLayout());
        emotioncanvas = new EmotionCanvas(chatclient, this);
        EmotionScrollView = new ScrollView(emotioncanvas, true, true, 
                EMOTION_CANVAS_WIDTH, EMOTION_CANVAS_HEIGHT, SCROLL_BAR_SIZE);
        emotioncanvas.scrollview = EmotionScrollView;
        /**
         * ********Add Icons into MessageObject ********
         */
        emotioncanvas.AddIconsToMessageObject();
        EmotionPanel.add("Center", EmotionScrollView);
        EmotionPanel.setVisible(false);
        EmotionPanel.setBounds(5, 320, 
                EMOTION_CANVAS_WIDTH, EMOTION_CANVAS_HEIGHT);
        add(EmotionPanel);

        setSize(PRIVATE_WINDOW_WIDTH, PRIVATE_WINDOW_HEIGHT);
        setResizable(false);
        setVisible(true);
        this.requestFocus();
    }

    /**
     * @param evt
     * *********Action Listener coding *********
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(CmdSend)) {
            /**
             * ****** Send Message ********
             */
            if (!(TxtMessage.getText().trim().equals(""))) {
                sendMessage();
            }
        }

        /**
         * ***Close Button Event *******
         */
        if (evt.getSource().equals(CmdClose)) {
            exitPrivateWindow();
        }

        /**
         * *******Clear Button Event *******
         */
        if (evt.getSource().equals(CmdClear)) {
            messageCanvas.ClearAll();
        }

        /**
         * *** Ignore Action Event *******
         */
        if (evt.getSource().equals(CmdIgnore)) {
            if (evt.getActionCommand().equals("Ignore User")) {
                chatclient.getTapPanel().UserCanvas.IgnoreUser(true, UserName);
                messageCanvas.addMessageToMessageObject(UserName + " has been ignored!", MESSAGE_TYPE_ADMIN);
                CmdIgnore.setLabel("Allow User");
            } else {
                messageCanvas.addMessageToMessageObject(UserName + " has been removed from ignored list!", MESSAGE_TYPE_ADMIN);
                chatclient.getTapPanel().UserCanvas.IgnoreUser(false, UserName);
                CmdIgnore.setLabel("Ignore User");
            }
        }

        /**
         * *** Emoticons Action Event *******
         */
        if (evt.getSource().equals(CmdEmoticons)) {
            if (EmotionFlag) {
                EmotionFlag = false;
                EmotionPanel.setVisible(false);
                setSize(PRIVATE_WINDOW_WIDTH, PRIVATE_WINDOW_HEIGHT);
            } else {
                EmotionFlag = true;
                EmotionPanel.setVisible(true);
                setSize(PRIVATE_WINDOW_WIDTH, PRIVATE_WINDOW_HEIGHT + EMOTION_CANVAS_HEIGHT);
            }
        }

    }

    /**
     * ******* Key Listener Event ************
     */
    @Override
    public void keyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() == 10) && (!(TxtMessage.getText().trim().equals("")))) {
            sendMessage();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void sendMessage() {
        messageCanvas.addMessageToMessageObject(chatclient.getUserName() + ": " + TxtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        chatclient.sentPrivateMessageToServer(TxtMessage.getText(), UserName);
        TxtMessage.setText("");
        TxtMessage.requestFocus();
    }

    /**
     * ****** Function to Set the Image Name into Text Field ***********
     */
    protected void addImageToTextField(String ImageName) {
        if (TxtMessage.getText() == null || TxtMessage.getText().equals("")) {
            TxtMessage.setText("~~" + ImageName + " ");
        } else {
            TxtMessage.setText(TxtMessage.getText() + " " + "~~" + ImageName + " ");
        }
    }

    /**
     * *******Function to Add a Message To Messagecanvas ********
     */
    protected void addMessageToMessageCanvas(String Message) {
        messageCanvas.addMessageToMessageObject(Message, MESSAGE_TYPE_DEFAULT);
    }

    protected void disableAll() {
        TxtMessage.setEnabled(false);
        CmdSend.setEnabled(false);
    }

    protected void enableAll() {
        TxtMessage.setEnabled(true);
        CmdSend.setEnabled(true);
    }

    /**
     * **** Exit from Private Chat
     */
    private void exitPrivateWindow() {
        chatclient.removePrivateWindow(UserName);
        setVisible(false);
    }

}
