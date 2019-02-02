
import chat.client.CommonSettings;
import chat.client.ImagePanel;
import chat.client.MessageCanvas;
import chat.client.PrivateChat;
import chat.client.ScrollView;
import chat.client.TapPanel;
import java.awt.Panel;
import java.awt.Label;
import java.awt.Image;
import java.awt.TextField;
import java.awt.Frame;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;
import java.io.Serializable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;
import java.awt.Toolkit;
import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.MenuItem;

/**
 * ********Chat Client*************
 */
public class ChatClientOld extends Frame implements Serializable, Runnable, KeyListener, ActionListener, CommonSettings {

    private String userName, userRoom, serverName, appletStatus, chatLogo, bannerName, proxyHost, serverData, roomList, splitString;
    private int serverPort, proxyPort, iconCount, totalUserCount, count;
    private boolean startFlag, isProxy;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private StringBuffer stringBuffer;
    private Thread thread;
    private StringTokenizer tokenizer;

    //Frame
    private Image ImgLogo, ImgBanner;
    private Color[] colorMap;
    private Dimension dimension;
    private MediaTracker tracker;
    private Label InformationLabel;
    private Image[] IconArray;
    private MessageCanvas messageCanvas;
    private ScrollView MessageScrollView;
    private TapPanel tapPanel;
    private TextField TxtMessage;
    private Button CmdSend, CmdExit;
    private Font textFont;
    protected PrivateChat[] privatewindow;
    protected int PrivateWindowCount;
    private InformationDialog dialog;
    private Toolkit toolkit;
    private MenuItem loginItem;
    private MenuItem disconnectItem;
    private MenuItem seperatorItem;
    private MenuItem quitItem, aboutItem;

    ChatClientOld() {
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
         * ********Getting all the Parameteres**********
         */
        userName = "";
        userRoom = "";
        iconCount = 21;
        chatLogo = "images/logo.gif";
        bannerName = "images/defaultbanner.gif";
        roomList = "";
        isProxy = false;
        /**
         * *******Assigning Global Colors************
         */
        colorMap = new Color[MAX_COLOR];
        /**
         * *****Backgorund********
         */
        colorMap[0] = new Color(224, 236, 254);
        /**
         * *****Information Panel Background********
         */
        colorMap[1] = new Color(255, 153, 0);
        /**
         * *****Button Foreground********
         */
        colorMap[2] = Color.black;
        /**
         * *****Button Background*************
         */
        colorMap[3] = new Color(224, 236, 254);
        /**
         * *****sstab button***************
         */
        colorMap[4] = new Color(255, 153, 0);
        /**
         * *****message canvas********
         */
        colorMap[5] = Color.black;
        /**
         * *****Top Panel Background********
         */
        colorMap[6] = Color.yellow;
        /**
         * *****Label Text Colors********
         */
        colorMap[7] = Color.white;

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

    public static void main(String args[]) {
        ChatClientOld mainFrame = new ChatClientOld();
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

    public String getUserName() {
        return userName;
    }

    private void connectToServer() {
        /**
         * *********Initialize the Socket******
         */
        messageCanvas.ClearAll();
        messageCanvas.AddMessageToMessageObject("Connecting To Server... Please Wait...", MESSAGE_TYPE_ADMIN);
        try {

            if (isProxy) {
                /**
                 * *******Proxy**********
                 */
                SocksSocketImplFactory factory = new SocksSocketImplFactory(proxyHost, proxyPort);
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
            in = new DataInputStream(socket.getInputStream());
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

    private void sendMessageToServer(String Message) {
        try {
            out.writeBytes(Message + "\r\n");
        } catch (IOException ie) {
            quitConnection(QUIT_TYPE_DEFAULT);
            ie.printStackTrace();
        }
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
        TxtMessage = new TextField();
        TxtMessage.addKeyListener(this);
        TxtMessage.setFont(textFont);
        CmdSend = new CustomButton(this, "Send Message!");
        CmdSend.addActionListener(this);
        TextBoxPanel.add("West", LblGeneral);
        TextBoxPanel.add("Center", TxtMessage);
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
        if (dialog.IsConnect == true) {
            userName = dialog.TxtUserName.getText();
            //UserRoom 	= dialog.roomchoice.getSelectedItem();
            serverName = dialog.TxtServerName.getText();
            serverPort = Integer.parseInt(dialog.TxtServerPort.getText());
            if (dialog.IsProxyCheckBox.getState() == true) {
                isProxy = true;
                proxyHost = dialog.TxtProxyHost.getText();
                proxyPort = Integer.parseInt(dialog.TxtProxyPort.getText());
            } else {
                isProxy = false;
            }
            connectToServer();
        }
    }

    /**
     * *******Button Events ****
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(CmdSend)) {
            if (!(TxtMessage.getText().trim().equals(""))) {
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
            MessageBox messagebox = new MessageBox(this, false);
            messagebox.AddMessage("~~13 " + PRODUCT_NAME);
            messagebox.AddMessage("Developed By...");
            messagebox.AddMessage(COMPANY_NAME);

        }

    }

    /**
     * ******* Key Listener Event ************
     */
    public void keyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() == 10) && (!(TxtMessage.getText().trim().equals("")))) {
            sendMessage();
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    /**
     * ****** Function To Send MESS Rfc to Server ************
     */
    private void sendMessage() {
        /**
         * ******Sending a Message To Server ********
         */
        sendMessageToServer("MESS " + userRoom + "~" + userName + ": " + TxtMessage.getText());
        messageCanvas.AddMessageToMessageObject(userName + ": " + TxtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        TxtMessage.setText("");
        TxtMessage.requestFocus();
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

    /**
     * ******Implements the Thread ***************
     */
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
                    messageCanvas.AddMessageToMessageObject("Welcome To The " + userRoom + " Room!", MESSAGE_TYPE_JOIN);
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
                    messageCanvas.AddMessageToMessageObject(splitString + " joins chat...", MESSAGE_TYPE_JOIN);
                }

                /**
                 * *******If User Name Already Exists *********
                 */
                if (serverData.startsWith("EXIS")) {
                    messageCanvas.AddMessageToMessageObject(" User Name Already Exists... Try Again With Some Other Name!", MESSAGE_TYPE_ADMIN);
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
                    messageCanvas.AddMessageToMessageObject(splitString + " has been logged Out from Chat!", MESSAGE_TYPE_LEAVE);

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
                        messageCanvas.AddMessageToMessageObject(serverData.substring(5), MESSAGE_TYPE_DEFAULT);
                    }
                }

                /**
                 * *** KICK RFC Starts **********
                 */
                if (serverData.startsWith("KICK")) {
                    messageCanvas.AddMessageToMessageObject("You are Kicked Out From Chat for flooding the message!", MESSAGE_TYPE_ADMIN);
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
                    messageCanvas.AddMessageToMessageObject(splitString + " has been kicked Out from Chat by the Administrator!", MESSAGE_TYPE_ADMIN);

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

                    messageCanvas.AddMessageToMessageObject(splitString + " joins chat...", MESSAGE_TYPE_JOIN);
                }

                /**
                 * *********Leave Room RFC *********
                 */
                if (serverData.startsWith("LERO")) {
                    splitString = serverData.substring(5, serverData.indexOf("~"));
                    tapPanel.UserCanvas.RemoveListItem(splitString);
                    messageCanvas.AddMessageToMessageObject(splitString + " has leaves " + userRoom + " Room and join into " + serverData.substring(serverData.indexOf("~") + 1) + " Room", MESSAGE_TYPE_ADMIN);

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
                     * ** Chk whether ignored user ********
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
                                messageCanvas.AddMessageToMessageObject("You are Exceeding private window limit! So you may lose some message from your friends!", MESSAGE_TYPE_ADMIN);
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
                messageCanvas.AddMessageToMessageObject(e.getMessage(), MESSAGE_TYPE_ADMIN);
                quitConnection(QUIT_TYPE_DEFAULT);
                e.printStackTrace();
            }
        }
    }

    /**
     * *** Enable the Private Chat when the End User logged out***
     */
    private void enablePrivateWindow(String ToUserName) {
        for (count = 0; count < PrivateWindowCount; count++) {
            if (privatewindow[count].UserName.equals(ToUserName)) {
                privatewindow[count].getMessageCanvas().AddMessageToMessageObject(ToUserName + " is Currently Online!", MESSAGE_TYPE_ADMIN);
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
                privatewindow[count].getMessageCanvas().AddMessageToMessageObject(ToUserName + " is Currently Offline!", MESSAGE_TYPE_ADMIN);
                privatewindow[count].disableAll();
                return;
            }
        }
    }

    /**
     * *****Function To Send Private Message To Server **********
     */
    protected void sentPrivateMessageToServer(String Message, String ToUserName) {
        sendMessageToServer("PRIV " + ToUserName + "~" + userName + ": " + Message);
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
            messageCanvas.AddMessageToMessageObject("Invalid Room Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }

        if (tapPanel.RoomCanvas.SelectedUser.equals(userRoom)) {
            messageCanvas.AddMessageToMessageObject("You are already in that ROOM!", MESSAGE_TYPE_ADMIN);
            return;
        }

        sendMessageToServer("CHRO " + userName + "~" + tapPanel.RoomCanvas.SelectedUser);
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
    protected void addImageToTextField(String ImageName) {
        if (TxtMessage.getText() == null || TxtMessage.getText().equals("")) {
            TxtMessage.setText("~~" + ImageName + " ");
        } else {
            TxtMessage.setText(TxtMessage.getText() + " " + "~~" + ImageName + " ");
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
     * *** Function To Disable All Components *******
     */
    private void disableAll() {
        TxtMessage.setEnabled(false);
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
        TxtMessage.setEnabled(true);
        CmdSend.setEnabled(true);
        tapPanel.setEnabled(true);
        disconnectItem.setEnabled(true);
        loginItem.setEnabled(false);
    }

    /**
     * *****Diconnect Chat *******
     */
    private void disconnectChat() {
        if (socket != null) {
            messageCanvas.AddMessageToMessageObject("CONNECTION TO THE SERVER CLOSED", MESSAGE_TYPE_ADMIN);
            quitConnection(QUIT_TYPE_DEFAULT);
        }
    }

    /**
     * ********Setting the appletStatus*******
     */
    private void setAppletStatus(String Message) {
        if (messageCanvas != null) {
            messageCanvas.AddMessageToMessageObject(Message, MESSAGE_TYPE_ADMIN);
        }
    }

    public TapPanel getTapPanel() {
        return tapPanel;
    }

    public Font getTextFont() {
        return textFont;
    }
}
