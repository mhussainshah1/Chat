
import chat.client.ChatClient;
import chat.client.CommonSettings;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Properties;
import java.util.Scanner;

public class InformationDialog extends Dialog implements ActionListener, CommonSettings {
    private ChatClient chatClient;
    protected TextField txtUserName, txtServerName, txtServerPort, txtProxyHost, txtProxyPort;
    protected Button cmdOk, cmdCancel;
    protected Choice roomChoice;
    protected Checkbox isProxyCheckBox;
    protected boolean isConnect;
    private Properties properties;
    
    public InformationDialog(ChatClient Parent) {
        super(Parent, PRODUCT_NAME + " - Login", true);
        chatClient = Parent;
        setFont(chatClient.getTextFont());
        setLayout(new BorderLayout());
        isConnect = false;
        
        properties = this.getProperties();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });

        Panel buttonPanel = new Panel(new GridLayout(7, 2, 15, 30));
        buttonPanel.setBackground(BUTTON_BACKGROUND);

        Label lblUserName = new Label("Nick Name: ");
        txtUserName = new TextField(properties.getProperty("TurtleUserName"));
        buttonPanel.add(lblUserName);
        buttonPanel.add(txtUserName);

        Label lblServerName = new Label("Server Name: ");

        txtServerName = new TextField();
        if (properties.getProperty("TurtleServerName") != null) {
            txtServerName.setText(properties.getProperty("TurtleServerName"));
        } else {
            txtServerName.setText("localhost");
        }

        buttonPanel.add(lblServerName);
        buttonPanel.add(txtServerName);

        Label lblServerPort = new Label("Server Port: ");
        txtServerPort = new TextField();
        if (properties.getProperty("TurtleServerPort") != null) {
            txtServerPort.setText(properties.getProperty("TurtleServerPort"));
        } else {
            txtServerPort.setText(Integer.toString(PORT_NUMBER));
        }

        buttonPanel.add(lblServerPort);
        buttonPanel.add(txtServerPort);

        Label LblProxy = new Label("Proxy :");
        isProxyCheckBox = new Checkbox();

        isProxyCheckBox.setState(Boolean.parseBoolean(properties.getProperty("TurtleProxyState")));

        buttonPanel.add(LblProxy);
        buttonPanel.add(isProxyCheckBox);

        Label LblProxyHost = new Label("Proxy Host (Socks): ");
        txtProxyHost = new TextField();
        txtProxyHost.setText(properties.getProperty("TurtleProxyHost"));
        buttonPanel.add(LblProxyHost);
        buttonPanel.add(txtProxyHost);

        Label LblProxyPort = new Label("Proxy Port (Socks): ");
        txtProxyPort = new TextField();
        txtProxyPort.setText(properties.getProperty("TurtleProxyPort"));
        buttonPanel.add(LblProxyPort);
        buttonPanel.add(txtProxyPort);

        cmdOk = new Button("Connect");
        cmdOk.addActionListener(this);
        cmdCancel = new Button("Quit");
        cmdCancel.addActionListener(this);
        buttonPanel.add(cmdOk);
        buttonPanel.add(cmdCancel);

        add("Center", buttonPanel);

        Panel EmptyNorthPanel = new Panel();
        EmptyNorthPanel.setBackground(BUTTON_BACKGROUND);
        add("North", EmptyNorthPanel);

        Panel EmptySouthPanel = new Panel();
        EmptySouthPanel.setBackground(BUTTON_BACKGROUND);
        add("South", EmptySouthPanel);

        Panel EmptyEastPanel = new Panel();
        EmptyEastPanel.setBackground(BUTTON_BACKGROUND);
        add("East", EmptyEastPanel);

        Panel EmptyWestPanel = new Panel();
        EmptyWestPanel.setBackground(BUTTON_BACKGROUND);
        add("West", EmptyWestPanel);

        setSize(250, 400);
        chatClient.setVisible(true);//show();
        setVisible(true);
    }

    private Properties getProperties() {
        Properties propertiesLocal = new Properties();
        try {
            File x = new File("data.properties");
            if(x.exists()){
               InputStream inputstream = this.getClass().getResourceAsStream("data.properties");
               //this.getClass().getClassLoader().getResourceAsStream("data.properties");
               propertiesLocal.load(inputstream);
               inputstream.close();
            } else{
                Formatter f = new Formatter(x);
                f.format("TurtleUserName=%s\r\n","amir");
                f.format("TurtleServerName=%s\r\n","");
                f.format("TurtleProxyPort=%s\r\n","");
                f.format("TurtleServerPort=%d\r\n", PORT_NUMBER);
                f.format("TurtleProxyHost=%s\r\n","");
                f.format("TurtleProxyState=%s",false);
                f.close();

                Scanner sc = new Scanner(x);
                while (sc.hasNext()) {
                    System.out.println(sc.next());
                }
                sc.close();
            }            
        } catch (java.io.IOException | java.lang.NullPointerException exc) {
            exc.printStackTrace();
        } finally {
            return propertiesLocal;
        }        
    }

    private void setProperties(Properties properties) {
        this.properties = properties;
        try {
            FileOutputStream fout = new FileOutputStream(new File("data.properties"));
            if (isProxyCheckBox.getState() == true) {
                properties.setProperty("TurtleProxyState", "true");
            } else {
                properties.setProperty("TurtleProxyState", "false");
            }
            properties.setProperty("TurtleUserName", txtUserName.getText());
            properties.setProperty("TurtleServerName", txtServerName.getText());
            properties.setProperty("TurtleServerPort", txtServerPort.getText());
            properties.setProperty("TurtleProxyHost", txtProxyHost.getText());
            properties.setProperty("TurtleProxyPort", txtProxyPort.getText());
            properties.store(fout, PRODUCT_NAME);//save(fout,PRODUCT_NAME);
            dispose();
        } catch (java.io.IOException exc) {
            exc.printStackTrace();
        }
    }   
    
    /**
     * ****** Action Event Coding Starts *************
     * @param evt
     */
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(cmdOk)) {
            isConnect = true;
            setProperties(properties);
        }

        if (evt.getSource().equals(cmdCancel)) {
            isConnect = false;
            dispose();
        }
    }
}
