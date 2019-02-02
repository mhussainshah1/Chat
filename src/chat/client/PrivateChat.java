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

public class PrivateChat extends Frame implements CommonSettings, KeyListener, 
        ActionListener {
    public static void main(String[] args) {
        
    }
    
    private ChatClient chatClient;
    protected String userName;
    private TextField txtMessage;
    private boolean emotionFlag;

    private Button cmdSend, cmdClose, cmdIgnore, cmdClear, cmdEmoticons;
    private EmotionCanvas emotionCanvas;
    private ScrollView emotionScrollView, messageScrollView;
    private MessageCanvas messageCanvas;//Replace with JtextPane
    private Panel emotionPanel;


    PrivateChat(ChatClient parent, String toUserName) {
        chatClient = parent;
        userName = toUserName;
        setTitle("Private Chat With " + userName);
        Image IconImage = Toolkit.getDefaultToolkit().getImage("images/logo.gif");
        setIconImage(IconImage);
        setBackground(BACKGROUND);
        setFont(chatClient.getFont());
        emotionFlag = false;
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
        Label LblConversation = new Label("Conversation With " + userName);
        LblConversation.setForeground(MESSAGE_CANVAS);
        LblConversation.setBounds(5, 30, 400, 20);
        add(LblConversation);

        Panel MessagePanel = new Panel(new BorderLayout());
        messageCanvas = new MessageCanvas(chatClient);
        messageScrollView = new ScrollView(messageCanvas, true, true,
                TAPPANEL_CANVAS_WIDTH, TAPPANEL_CANVAS_HEIGHT, SCROLL_BAR_SIZE);
        messageCanvas.scrollview = messageScrollView;
        MessagePanel.add("Center", messageScrollView);
        MessagePanel.setBounds(5, 50, 400, 200);
        add(MessagePanel);

        txtMessage = new TextField();
        txtMessage.addKeyListener(this);
        txtMessage.setFont(chatClient.getTextFont());
        txtMessage.setBounds(5, 260, 320, 20);
        add(txtMessage);

        cmdSend = new CustomButton(chatClient, "Send");
        cmdSend.addActionListener(this);
        cmdSend.setBounds(335, 260, 70, 20);
        add(cmdSend);

        cmdClear = new CustomButton(chatClient, "Clear");
        cmdClear.addActionListener(this);
        cmdClear.setBounds(5, 290, 80, 20);

        cmdIgnore = new CustomButton(chatClient, "Ignore User");
        cmdIgnore.addActionListener(this);
        cmdIgnore.setBounds(105, 290, 80, 20);

        cmdClose = new CustomButton(chatClient, "Close");
        cmdClose.addActionListener(this);
        cmdClose.setBounds(205, 290, 80, 20);

        cmdEmoticons = new CustomButton(chatClient, "Emoticons");
        cmdEmoticons.addActionListener(this);
        cmdEmoticons.setBounds(305, 290, 80, 20);

        add(cmdClear);
        add(cmdIgnore);
        add(cmdClose);
        add(cmdEmoticons);

        emotionPanel = new Panel(new BorderLayout());
        emotionCanvas = new EmotionCanvas(chatClient, this);
        emotionScrollView = new ScrollView(emotionCanvas, true, true,
                EMOTION_CANVAS_WIDTH, EMOTION_CANVAS_HEIGHT, SCROLL_BAR_SIZE);
        emotionCanvas.scrollview = emotionScrollView;
        /**
         * ********Add Icons into MessageObject ********
         */
        emotionCanvas.AddIconsToMessageObject();
        emotionPanel.add("Center", emotionScrollView);
        emotionPanel.setVisible(false);
        emotionPanel.setBounds(5, 320,
                EMOTION_CANVAS_WIDTH, EMOTION_CANVAS_HEIGHT);
        add(emotionPanel);

        setSize(PRIVATE_WINDOW_WIDTH, PRIVATE_WINDOW_HEIGHT);
        setResizable(false);
        setVisible(true);
        this.requestFocus();
    }

    /**
     * @param evt *********Action Listener coding *********
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(cmdSend)) {
            /**
             * ****** Send Message ********
             */
            if (!(txtMessage.getText().trim().equals(""))) {
                sendMessage();
            }
        }

        /**
         * ***Close Button Event *******
         */
        if (evt.getSource().equals(cmdClose)) {
            exitPrivateWindow();
        }

        /**
         * *******Clear Button Event *******
         */
        if (evt.getSource().equals(cmdClear)) {
            messageCanvas.ClearAll();
        }

        /**
         * *** Ignore Action Event *******
         */
        if (evt.getSource().equals(cmdIgnore)) {
            if (evt.getActionCommand().equals("Ignore User")) {
                chatClient.getTapPanel().UserCanvas.ignoreUser(true, userName);
                messageCanvas.addMessageToMessageObject(userName + " has been ignored!", MESSAGE_TYPE_ADMIN);
                cmdIgnore.setLabel("Allow User");
            } else {
                messageCanvas.addMessageToMessageObject(userName + " has been removed from ignored list!", MESSAGE_TYPE_ADMIN);
                chatClient.getTapPanel().UserCanvas.ignoreUser(false, userName);
                cmdIgnore.setLabel("Ignore User");
            }
        }

        /**
         * *** Emoticons Action Event *******
         */
        if (evt.getSource().equals(cmdEmoticons)) {
            if (emotionFlag) {
                emotionFlag = false;
                emotionPanel.setVisible(false);
                setSize(PRIVATE_WINDOW_WIDTH, PRIVATE_WINDOW_HEIGHT);
            } else {
                emotionFlag = true;
                emotionPanel.setVisible(true);
                setSize(PRIVATE_WINDOW_WIDTH, PRIVATE_WINDOW_HEIGHT + EMOTION_CANVAS_HEIGHT);
            }
        }

    }

    /**
     * ******* Key Listener Event ************
     */
    @Override
    public void keyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() == 10) && (!(txtMessage.getText().trim().equals("")))) {
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
        messageCanvas.addMessageToMessageObject(chatClient.getUserName() + ": " + txtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        chatClient.sentPrivateMessageToServer(txtMessage.getText(), userName);
        txtMessage.setText("");
        txtMessage.requestFocus();
    }

    /**
     * ****** Function to Set the Image Name into Text Field ***********
     * @param imageName
     */
    protected void addImageToTextField(String imageName) {
        if (txtMessage.getText() == null || txtMessage.getText().equals("")) {
            txtMessage.setText("~~" + imageName + " ");
        } else {
            txtMessage.setText(txtMessage.getText() + " " + "~~" + imageName + " ");
        }
    }

    /**
     * *******Function to Add a Message To Messagecanvas ********
     */
    protected void addMessageToMessageCanvas(String message) {
        messageCanvas.addMessageToMessageObject(message, MESSAGE_TYPE_DEFAULT);
    }

    protected void disableAll() {
        txtMessage.setEnabled(false);
        cmdSend.setEnabled(false);
    }

    protected void enableAll() {
        txtMessage.setEnabled(true);
        cmdSend.setEnabled(true);
    }

    /**
     * **** Exit from Private Chat
     */
    private void exitPrivateWindow() {
        chatClient.removePrivateWindow(userName);
        setVisible(false);
    }

}
