package chat.client;

import chat.client.net.SocksSocketImplFactory;
import chat.client.net.SocksSocket;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * ********Chat Client*************
 */
public class ChatClient extends Frame implements Serializable, KeyListener,
        ActionListener, CommonSettings, Runnable {

    public static void main(String[] args) {
        ChatClient cw = new ChatClient();
    }

    //Frame
    private Image ImgLogo, ImgBanner;
    private Dimension dimension;
    private MediaTracker tracker;
    private Label InformationLabel;
    private Image[] IconArray;
    private MessageCanvas messageCanvas;
    private ScrollView MessageScrollView;
    private TapPanel tapPanel;
    private TextField txtMessage;
    private Button CmdSend, CmdExit;
    private Font textFont;
    protected PrivateChat[] privatewindow;
    protected int count, iconCount, PrivateWindowCount;
    private InformationDialog dialog;
    private Toolkit toolkit;
    private MenuItem loginItem, disconnectItem, seperatorItem;
    private MenuItem quitItem, aboutItem;
    private String chatLogo, bannerName;
    private StringBuffer stringBuffer;
    private ChatClient chatClient;
    private String appletStatus;

    //For future Network class 
    private String userName, serverData, roomList, splitString;
    private String userRoom, serverName, proxyHost;
    private int serverPort, proxyPort, totalUserCount;
    private boolean startFlag, isProxy;
    private Socket socket;
//    private DataInputStream in;
    private BufferedReader in;
    private DataOutputStream out;

    private Thread thread;
    private StringTokenizer tokenizer;

    ChatClient() {
        /**
         * ********Getting all the Parameters**********
         */
        userName = "";
        userRoom = "";
        roomList = "";
        isProxy = false;
        chatLogo = "images/logo.gif";
        bannerName = "images/defaultbanner.gif";
        iconCount = 21;

        toolkit = Toolkit.getDefaultToolkit();
        if (toolkit.getScreenSize().getWidth() > 778) {
            setSize(778, 575);
        } else {
            setSize((int) toolkit.getScreenSize().getWidth(), (int) toolkit.getScreenSize().getHeight() - 20);
        }
        setResizable(false);
        dimension = getSize();
        setLayout(new BorderLayout());

        setTitle(PRODUCT_NAME);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                disconnectChat();
                System.exit(0);
            }
        });

        /**
         * ***Loading menubar **********
         */
        MenuBar menuBar = new MenuBar();
        Menu loginMenu = new Menu("Login");
        loginItem = new MenuItem("Login");
        loginItem.addActionListener(this);
        disconnectItem = new MenuItem("Disconnect");
        disconnectItem.addActionListener(this);
        seperatorItem = new MenuItem("-");
        quitItem = new MenuItem("Quit");
        quitItem.addActionListener(this);
        loginMenu.add(loginItem);
        loginMenu.add(disconnectItem);
        loginMenu.add(seperatorItem);
        loginMenu.add(quitItem);

        Menu aboutMenu = new Menu("Help ");
        aboutItem = new MenuItem("About " + PRODUCT_NAME);
        aboutItem.addActionListener(this);
        aboutMenu.add(aboutItem);

        menuBar.add(loginMenu);
        menuBar.add(aboutMenu);
        setMenuBar(menuBar);
        /**
         * ********Loading Images********
         */
        tracker = new MediaTracker(this);
        int ImageCount = 0;
        ImgLogo = toolkit.getImage(chatLogo);
        tracker.addImage(ImgLogo, ImageCount);
        ImageCount++;
        ImgBanner = toolkit.getImage(bannerName);
        tracker.addImage(ImgBanner, ImageCount);
        ImageCount++;

        /**
         * ********Loading Icons....**********
         */
        IconArray = new Image[iconCount];
        for (count = 0; count < iconCount; count++) {
            IconArray[count] = toolkit.getImage("icons/photo" + count + ".gif");
            tracker.addImage(IconArray[count], ImageCount);
            ImageCount++;
        }

        /**
         * *******Initialize Private Window *********
         */
        privatewindow = new PrivateChat[MAX_PRIVATE_WINDOW];
        PrivateWindowCount = 0;

        try {
            setAppletStatus("Loading Images and Icons.....");
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setIconImage(toolkit.getImage("images/logo.gif"));
        setAppletStatus("");
        /**
         * ********Initializing all the Components********
         */
        initializeAppletComponents();
    }

    /**
     * *****Initialize all the Applet Components*******
     */
    private void initializeAppletComponents() {
        /**
         * *****Common Settings**********
         */
        setBackground(BACKGROUND);
        Font font = new Font("Dialog", Font.BOLD, 11);
        textFont = new Font("Dialog", 0, 11);
        setFont(font);

        /**
         * *********Top Panel Coding************
         */
        Panel TopPanel = new Panel(new BorderLayout());
        TopPanel.setBackground(TOP_PANEL_BACKGROUND);
        Panel LogoPanel = new ImagePanel(this, ImgLogo);
        TopPanel.add("East", LogoPanel);
        Panel BannerPanel = new ImagePanel(this, ImgBanner);
        TopPanel.add("West", BannerPanel);
        add("North", TopPanel);

        /**
         * ***********Information Label Panel Coding************
         */
        Panel CenterPanel = new Panel(new BorderLayout());
        Panel InformationPanel = new Panel(new BorderLayout());
        InformationPanel.setBackground(INFORMATION_PANEL_BACKGROUND);
        InformationLabel = new Label();
        InformationLabel.setAlignment(1);
        updateInformationLabel();
        InformationLabel.setForeground(LABEL_TEXT_COLOR);
        InformationPanel.add("Center", InformationLabel);
        CenterPanel.add("North", InformationPanel);

        /**
         * *******Message Canvas and SSTAB Coding*******
         */
        Panel MessagePanel = new Panel(new BorderLayout());
        messageCanvas = new MessageCanvas(this);
        MessageScrollView = new ScrollView(messageCanvas, true, true, TAPPANEL_CANVAS_WIDTH, TAPPANEL_CANVAS_HEIGHT, SCROLL_BAR_SIZE);
        messageCanvas.scrollview = MessageScrollView;
        MessagePanel.add("Center", MessageScrollView);

        tapPanel = new TapPanel(this);

        MessagePanel.add("East", tapPanel);
        CenterPanel.add("Center", MessagePanel);

        /**
         * *******Input Panel Coding Starts..********
         */
        Panel InputPanel = new Panel(new BorderLayout());
        Panel TextBoxPanel = new Panel(new BorderLayout());
        Label LblGeneral = new Label("General Message!");
        txtMessage = new TextField();
        txtMessage.addKeyListener(this);
        txtMessage.setFont(textFont);
        CmdSend = new CustomButton(this, "Send Message!");
        CmdSend.addActionListener(this);
        TextBoxPanel.add("West", LblGeneral);
        TextBoxPanel.add("Center", txtMessage);
        TextBoxPanel.add("East", CmdSend);
        InputPanel.add("Center", TextBoxPanel);

        Panel InputButtonPanel = new Panel(new BorderLayout());
        CmdExit = new CustomButton(this, "Exit Chat");
        CmdExit.addActionListener(this);
        InputButtonPanel.add("Center", CmdExit);
        InputPanel.add("East", InputButtonPanel);

        Panel EmptyPanel = new Panel();
        InputPanel.add("South", EmptyPanel);
        CenterPanel.add("South", InputPanel);
        add("Center", CenterPanel);

        disableAll();
        loginToChat();
    }

    private void loginToChat() {
        /**
         * ******* Open the Dialog ********
         */
        dialog = new InformationDialog(this);
        if (dialog.isConnect) {
            userName = dialog.getTxtUserName();
            //UserRoom 	= dialog.roomChoice.getSelectedItem();
            serverName = dialog.getTxtServerName();
            serverPort = Integer.parseInt(dialog.getTxtServerPort());
            if (dialog.isProxyCheckBox()) {
                isProxy = true;
                proxyHost = dialog.getTxtProxyHost();
                proxyPort = Integer.parseInt(dialog.getTxtProxyPort());
            } else {
                isProxy = false;
            }
            connectToServer();
        }
    }

    private void sendMessageToServer(String message) {
        try {
            out.writeBytes(message + "\r\n");
        } catch (IOException ie) {
            quitConnection(QUIT_TYPE_DEFAULT);
            ie.printStackTrace();
        }
    }

    /**
     * *******Function to Destroy all the Objects*******
     */
    private void quitConnection(int QuitType) {
        if (socket != null) {
            try {
                if (QuitType == QUIT_TYPE_DEFAULT) {
                    sendMessageToServer("QUIT " + userName + "~" + userRoom);
                }
                if (QuitType == QUIT_TYPE_KICK) {
                    sendMessageToServer("KICK " + userName + "~" + userRoom);
                }
                socket.close();
                socket = null;
                tapPanel.UserCanvas.ClearAll();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
        if (thread != null) {
            thread.interrupt();//stop();
            thread = null;
        }
        disableAll();
        startFlag = false;
        setAppletStatus("ADMIN: CONNECTION TO THE SERVER CLOSED.");
    }

    /**
     * ******Implements the Thread ***************
     */
    @Override
    public void run() {
        while (thread != null) {
            try {
                serverData = in.readLine();
                /**
                 * ******* LIST UserName;UserName; RFC Coding**********
                 */
                if (serverData.startsWith("LIST")) {
                    tokenizer = new StringTokenizer(serverData.substring(5), ";");
                    /**
                     * ******Update the Information Label ********
                     */
                    totalUserCount = tokenizer.countTokens();
                    updateInformationLabel();
                    /**
                     * ********Add User Item into User Canvas ********
                     */
                    tapPanel.UserCanvas.ClearAll();
                    while (tokenizer.hasMoreTokens()) {
                        tapPanel.UserCanvas.AddListItemToMessageObject(tokenizer.nextToken());
                    }

                    messageCanvas.ClearAll();
                    messageCanvas.addMessageToMessageObject("Welcome To The " + userRoom + " Room!", MESSAGE_TYPE_JOIN);
                }

                /**
                 * *******Room Rfc *******
                 */
                if (serverData.startsWith("ROOM")) {
                    /**
                     * ******** Loading Room List in to Room Canvas
                     * *************
                     */
                    tokenizer = new StringTokenizer(serverData.substring(5), ";");
                    userRoom = tokenizer.nextToken();
                    updateInformationLabel();
                    /**
                     * ********Add User Item into User Canvas ********
                     */
                    tapPanel.RoomCanvas.ClearAll();
                    tapPanel.RoomCanvas.AddListItemToMessageObject(userRoom);
                    while (tokenizer.hasMoreTokens()) {
                        tapPanel.RoomCanvas.AddListItemToMessageObject(tokenizer.nextToken());
                    }
                }

                /**
                 * ******** ADD RFC ********
                 */
                if (serverData.startsWith("ADD")) {
                    /**
                     * ******Update the Information Label ********
                     */
                    totalUserCount++;
                    updateInformationLabel();

                    /**
                     * ********Add User Item into User Canvas ********
                     */
                    splitString = serverData.substring(5);
                    enablePrivateWindow(splitString);
                    tapPanel.UserCanvas.AddListItemToMessageObject(splitString);
                    messageCanvas.addMessageToMessageObject(splitString + " joins chat...", MESSAGE_TYPE_JOIN);
                }

                /**
                 * *******If User Name Already Exists *********
                 */
                if (serverData.startsWith("EXIS")) {
                    messageCanvas.addMessageToMessageObject(" User Name Already Exists... Try Again With Some Other Name!", MESSAGE_TYPE_ADMIN);
                    thread = null;
                    quitConnection(QUIT_TYPE_NULL);
                }

                /**
                 * ****** REMOVE User RFC Coding *********
                 */
                if (serverData.startsWith("REMO")) {
                    splitString = serverData.substring(5);

                    tapPanel.UserCanvas.RemoveListItem(splitString);
                    removeUserFromPrivateChat(splitString);
                    messageCanvas.addMessageToMessageObject(splitString + " has been logged Out from Chat!", MESSAGE_TYPE_LEAVE);

                    /**
                     * ***Update the Information Label *******
                     */
                    totalUserCount--;
                    updateInformationLabel();

                }

                /**
                 * ****** MESS RFC Coding Starts *********
                 */
                if (serverData.startsWith("MESS")) {
                    /**
                     * ** Chk whether ignored user ********
                     */
                    if (!(tapPanel.UserCanvas.IsIgnoredUser(serverData.substring(5, serverData.indexOf(":"))))) {
                        messageCanvas.addMessageToMessageObject(serverData.substring(5), MESSAGE_TYPE_DEFAULT);
                    }
                }

                /**
                 * *** KICK RFC Starts **********
                 */
                if (serverData.startsWith("KICK")) {
                    messageCanvas.addMessageToMessageObject("You are Kicked Out From Chat for flooding the message!", MESSAGE_TYPE_ADMIN);
                    thread = null;
                    quitConnection(QUIT_TYPE_KICK);
                }

                /**
                 * *** INKI RFC (Information about kicked off User ********
                 */
                if (serverData.startsWith("INKI")) {
                    splitString = serverData.substring(5);
                    tapPanel.UserCanvas.RemoveListItem(splitString);
                    removeUserFromPrivateChat(splitString);
                    messageCanvas.addMessageToMessageObject(splitString + " has been kicked Out from Chat by the Administrator!", MESSAGE_TYPE_ADMIN);

                    /**
                     * ***Update the Information Label *******
                     */
                    totalUserCount--;
                    updateInformationLabel();
                }

                /**
                 * *** Change Room RFC *********
                 */
                if (serverData.startsWith("CHRO")) {
                    userRoom = serverData.substring(5);
                }

                /**
                 * ******** Join Room RFC ************
                 */
                if (serverData.startsWith("JORO")) {
                    splitString = serverData.substring(5);
                    tapPanel.UserCanvas.AddListItemToMessageObject(splitString);
                    /**
                     * ***Update the Information Label *******
                     */
                    totalUserCount++;
                    updateInformationLabel();

                    messageCanvas.addMessageToMessageObject(splitString + " joins chat...", MESSAGE_TYPE_JOIN);
                }

                /**
                 * *********Leave Room RFC *********
                 */
                if (serverData.startsWith("LERO")) {
                    splitString = serverData.substring(5, serverData.indexOf("~"));
                    tapPanel.UserCanvas.RemoveListItem(splitString);
                    messageCanvas.addMessageToMessageObject(splitString + " has leaves " + userRoom + " Room and join into " + serverData.substring(serverData.indexOf("~") + 1) + " Room", MESSAGE_TYPE_ADMIN);

                    /**
                     * ***Update the Information Label *******
                     */
                    totalUserCount--;
                    updateInformationLabel();
                }

                /**
                 * ******** Room Count RFC *******
                 */
                if (serverData.startsWith("ROCO")) {
                    splitString = serverData.substring(5, serverData.indexOf("~"));
                    tapPanel.TxtUserCount.setText("Total Users in " + splitString + " : " + serverData.substring(serverData.indexOf("~") + 1));
                }

                /**
                 * ***** Private Message RFC *********
                 */
                if (serverData.startsWith("PRIV")) {
                    splitString = serverData.substring(5, serverData.indexOf(":"));
                    /**
                     * ** Check whether ignored user ********
                     */
                    if (!(tapPanel.UserCanvas.IsIgnoredUser(splitString))) {
                        boolean PrivateFlag = false;
                        for (count = 0; count < PrivateWindowCount; count++) {
                            if (privatewindow[count].UserName.equals(splitString)) {
                                privatewindow[count].addMessageToMessageCanvas(serverData.substring(5));
                                privatewindow[count].setVisible(true);
                                privatewindow[count].requestFocus();
                                PrivateFlag = true;
                                break;
                            }
                        }

                        if (!(PrivateFlag)) {
                            if (PrivateWindowCount >= MAX_PRIVATE_WINDOW) {
                                messageCanvas.addMessageToMessageObject("You are Exceeding private window limit! So you may lose some message from your friends!", MESSAGE_TYPE_ADMIN);
                            } else {
                                privatewindow[PrivateWindowCount++] = new PrivateChat(this, splitString);
                                privatewindow[PrivateWindowCount - 1].addMessageToMessageCanvas(serverData.substring(5));
                                privatewindow[PrivateWindowCount - 1].setVisible(true);
                                privatewindow[PrivateWindowCount - 1].requestFocus();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                messageCanvas.addMessageToMessageObject(e.getMessage(), MESSAGE_TYPE_ADMIN);
                quitConnection(QUIT_TYPE_DEFAULT);
                e.printStackTrace();
            }
        }
    }

    /**
     * *******Button Events ****
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(CmdSend)) {
            if (!(txtMessage.getText().trim().equals(""))) {
                sendMessage();
            }
        }

        if ((evt.getSource().equals(CmdExit)) || (evt.getSource().equals(quitItem))) {
            disconnectChat();
            System.exit(0);
        }

        if (evt.getSource().equals(loginItem)) {
            loginToChat();
        }

        if (evt.getSource().equals(disconnectItem)) {
            disconnectChat();
        }
        if (evt.getSource().equals(aboutItem)) {
//            MessageBox messagebox = new MessageBox(this, false);
//            messagebox.AddMessage("~~13 " + PRODUCT_NAME);
//            messagebox.AddMessage("Developed By...");
//            messagebox.AddMessage(COMPANY_NAME);

            JOptionPane.showMessageDialog(this, PRODUCT_NAME + "\n Developed By...\n" + COMPANY_NAME,
                    "About Us", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/photo13.gif"));
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

    /**
     * ********Setting the appletStatus*******
     */
    private void setAppletStatus(String Message) {
        if (messageCanvas != null) {
            messageCanvas.addMessageToMessageObject(Message, MESSAGE_TYPE_ADMIN);
        }
    }

    public TapPanel getTapPanel() {
        return tapPanel;
    }

    public Font getTextFont() {
        return textFont;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * *******Function To Update the Information Label****
     */
    private void updateInformationLabel() {
        stringBuffer = new StringBuffer();
        stringBuffer.append("User Name: ");
        stringBuffer.append(userName);
        stringBuffer.append("       ");
        stringBuffer.append("Room Name: ");
        stringBuffer.append(userRoom);
        stringBuffer.append("       ");
        stringBuffer.append("No. Of Users: ");
        stringBuffer.append(totalUserCount);
        stringBuffer.append("       ");
        InformationLabel.setText(stringBuffer.toString());
    }

    public MessageCanvas getMessageCanvas() {
        return messageCanvas;
    }

    public int getIconCount() {
        return iconCount;
    }

    public Image getIcon(int index) {
        return IconArray[index];
    }

    /**
     * *** Function To Disable All Components *******
     */
    private void disableAll() {
        txtMessage.setEnabled(false);
        CmdSend.setEnabled(false);
        tapPanel.setEnabled(false);
        disconnectItem.setEnabled(false);
        loginItem.setEnabled(true);
        userName = "";
        userRoom = "";
        totalUserCount = 0;
    }

    /**
     * *** Function To Enable All Components *******
     */
    private void enableAll() {
        txtMessage.setEnabled(true);
        CmdSend.setEnabled(true);
        tapPanel.setEnabled(true);
        disconnectItem.setEnabled(true);
        loginItem.setEnabled(false);
    }

    /**
     * *****Disconnect Chat *******
     */
    private void disconnectChat() {
        if (socket != null) {
            messageCanvas.addMessageToMessageObject("CONNECTION TO THE SERVER CLOSED", MESSAGE_TYPE_ADMIN);
            quitConnection(QUIT_TYPE_DEFAULT);
        }
    }

    /**
     * *** Enable the Private Chat when the End User logged out***
     */
    private void enablePrivateWindow(String ToUserName) {
        for (count = 0; count < PrivateWindowCount; count++) {
            if (privatewindow[count].UserName.equals(ToUserName)) {
                privatewindow[count].getMessageCanvas().addMessageToMessageObject(ToUserName + " is Currently Online!", MESSAGE_TYPE_ADMIN);
                privatewindow[count].enableAll();
                return;
            }
        }
    }

    /**
     * *** Disable the Private Chat when the End User logged out***
     */
    private void removeUserFromPrivateChat(String ToUserName) {
        for (count = 0; count < PrivateWindowCount; count++) {
            if (privatewindow[count].UserName.equals(ToUserName)) {
                privatewindow[count].getMessageCanvas().addMessageToMessageObject(ToUserName + " is Currently Offline!", MESSAGE_TYPE_ADMIN);
                privatewindow[count].disableAll();
                return;
            }
        }
    }

    /**
     * *****Function To Send Private Message To Server **********
     */
    protected void sentPrivateMessageToServer(String Message, String ToUserName) {
        sendMessageToServer("PRIV " + ToUserName + "~"
                + userName + ": " + Message);
    }

    /**
     * ***** Function To Remove Private Window **************
     */
    protected void removePrivateWindow(String ToUserName) {
        int m_UserIndex = 0;
        for (count = 0; count < PrivateWindowCount; count++) {
            m_UserIndex++;
            if (privatewindow[count].UserName.equals(ToUserName)) {
                break;
            }
        }
        for (int m_iLoop = m_UserIndex; m_iLoop < PrivateWindowCount; m_iLoop++) {
            privatewindow[m_iLoop] = privatewindow[m_iLoop + 1];
        }

        PrivateWindowCount--;
    }

    /**
     * ******* Function to Change Room ******
     */
    protected void changeRoom() {
        if (tapPanel.RoomCanvas.SelectedUser.equals("")) {
            messageCanvas.addMessageToMessageObject("Invalid Room Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }

        if (tapPanel.RoomCanvas.SelectedUser.equals(userRoom)) {
            messageCanvas.addMessageToMessageObject("You are already in that ROOM!", MESSAGE_TYPE_ADMIN);
            return;
        }

        sendMessageToServer("CHRO " + userName + "~"
                + tapPanel.RoomCanvas.SelectedUser);
    }

    /**
     * ***** Function to Send a RFC for Get a Room User Count *******
     */
    protected void getRoomUserCount(String RoomName) {
        sendMessageToServer("ROCO " + RoomName);
    }

    /**
     * ****** Function to Set the Image Name into Text Field ***********
     */
    protected void addImageToTextField(String imageName) {
        if (txtMessage.getText() == null || txtMessage.getText().equals("")) {
            txtMessage.setText("~~" + imageName + " ");
        } else {
            txtMessage.setText(txtMessage.getText() + " "
                    + "~~" + imageName + " ");
        }
    }

    private void connectToServer() {
        /**
         * *********Initialize the Socket******
         */
        messageCanvas.ClearAll();
        messageCanvas.addMessageToMessageObject("Connecting To Server... Please Wait...", MESSAGE_TYPE_ADMIN);
        /**
         * *********Initialize the Socket******
         */
        try {
            if (isProxy) {
                /**
                 * *******Proxy**********
                 */
                SocksSocketImplFactory factory
                        = new SocksSocketImplFactory(proxyHost, proxyPort);
                SocksSocket.setSocketImplFactory(factory);
                socket = new SocksSocket(serverName, serverPort);
                socket.setSoTimeout(0);
            } else {
                /**
                 * *****Not Proxy********
                 */
                socket = new Socket(serverName, serverPort);
            }
            out = new DataOutputStream(socket.getOutputStream());
            sendMessageToServer("HELO " + userName);
//            in = new DataInputStream(socket.getInputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            /**
             * *********Send HELO To Server *********
             */
            startFlag = true;
            thread = new Thread(this);
            thread.start();
            enableAll();
        } catch (IOException ie) {
            quitConnection(QUIT_TYPE_NULL);
            ie.printStackTrace();
        }
    }

    /**
     * ****** Function To Send MESS Rfc to Server ************
     */
    private void sendMessage() {
        /**
         * ******Sending a Message To Server ********
         */
        sendMessageToServer("MESS " + userRoom + "~" + userName + ": "
                + txtMessage.getText());
        messageCanvas.addMessageToMessageObject(userName + ": "
                + txtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        txtMessage.setText("");
        txtMessage.requestFocus();
    }
}
