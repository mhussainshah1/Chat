package chat.server;

import static chat.server.CommonSetting.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Properties;
import java.util.Scanner;

public class ChatServer implements Serializable, Runnable{

    private static final long serialVersionUID = 1L;
    private Properties properties;
    private ServerSocket serverSocket;
    private Socket socket;
    private ArrayList<Client> clientList;
    private ArrayList<String> messageList;
    private Thread thread;
    private DataOutputStream out;
    private Client client;
    private String roomList;
    private ChatCommunication chatCommunication;

    public void startServer() {
        properties = getProperties();
        //Initialize the Server Socket
        try {
            roomList = "";
            if (properties.getProperty("roomlist") != null) {
                roomList = properties.getProperty("roomlist");
            }
            int portNo = PORT_NUMBER.getNumber();
            if (properties.getProperty("portno") != null) {
                portNo = Integer.parseInt(properties.getProperty("portno"));
            }
            serverSocket = new ServerSocket(portNo);
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        //Initialize the Array List
        clientList = new ArrayList<>();
        messageList = new ArrayList<>();

        //Initialize the thread
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Function To Add a New Client in to the Server List
     *
     * @param clientSocket
     * @param userName
     */
    protected void addUser(Socket clientSocket, String userName) {
        //If User name Exists return
        if (isUserExists(userName)) {
            sendMessageToClient(clientSocket, "EXIS");
            return;
        }
        //Send a Room List
        sendMessageToClient(clientSocket, "ROOM " + roomList);

        //Send the New User Detail into All Other Users
        String addRFC = "ADD  " + userName;
        StringBuilder stringbuffer = new StringBuilder("LIST ");
        for (Client user : clientList) {
            //Check the Room Name
            if (user.getClientRoomName().equals(ROOM_NAME.getRoomName())) {
                sendMessageToClient(user.getClientSocket(), addRFC);
                stringbuffer.append(user.getClientUserName());
                stringbuffer.append(";");
            }
        }
        //Add a user in to array list
        client = new Client(clientSocket, userName, ROOM_NAME.getRoomName());
        clientList.add(client);

        //Sending the Complete User List to the New User
        stringbuffer.append(userName);
        stringbuffer.append(";");
        sendMessageToClient(clientSocket, stringbuffer.toString());
    }

    /**
     * Function To Change the Room
     *
     * @param clientSocket
     * @param userName
     * @param newRoomName
     */
    public void changeRoom(Socket clientSocket, String userName, String newRoomName) {
        int clientIndex = clientList.indexOf(userName);//getIndexOf(userName);
        if (clientIndex >= 0) {
            //Update the Old Room to New Room and send the RFC 
            Client tempClient = clientList.get(clientIndex);
            String oldRoomName = tempClient.getClientRoomName();
            tempClient.setClientRoomName(newRoomName);
            clientList.set(clientIndex, tempClient);
            sendMessageToClient(clientSocket, "CHRO " + newRoomName);

            //Send all the Users list of that particular room to that client socket 
            StringBuffer stringbuffer = new StringBuffer("LIST ");
            for (Client user : clientList) {
                //Check the Room Name
                if (user.getClientRoomName().equals(newRoomName)) {
                    stringbuffer.append(user.getClientUserName());
                    stringbuffer.append(";");
                }
            }
            sendMessageToClient(clientSocket, stringbuffer.toString());

            //Inform to Old Room and New Room Users
            String oldRoomRFC = "LERO " + userName + "~" + newRoomName;
            String newRoomRFC = "JORO " + userName;
            for (Client user : clientList) {
                if (user.getClientRoomName().equals(oldRoomName)) {
                    sendMessageToClient(user.getClientSocket(), oldRoomRFC);
                }
                if ((user.getClientRoomName().equals(newRoomName))
                        && (!(user.getClientUserName().equals(userName)))) {
                    sendMessageToClient(user.getClientSocket(), newRoomRFC);
                }
            }
        }
    }

    //Function to Destroy the Objects
    public void stopServer() {
        if (thread != null) {
            thread.interrupt();//stop();
            thread = null;
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        clientList = null;
        messageList = null;
    }

    //Function To Get the Object Of Given User Name
    private Client getClient(String userName) {
        for (Client user : clientList) {
            if ( user.getClientUserName().equalsIgnoreCase(userName)) {
                return user;
            }
        }
        return null;
    }

    //Loading Properties File
    private Properties getProperties() {
        //Getting the Property Value From Property File
        Properties propertiesLocal = new Properties();
        try {
            File x = new File("server.properties");
            if (x.exists()) {
                try (InputStream inputstream = this.getClass().getResourceAsStream("server.properties")) {
                    //this.getClass().getClassLoader().getResourceAsStream("server.properties");
                    propertiesLocal.load(inputstream);
                }
            } else {
                Formatter f = new Formatter(x);
                f.format("portno=%d\r\n", PORT_NUMBER);
                f.format("roomlist=%s;%s;%s;%s;", "General", "Teen", "Music", "Party");
                f.close();

                Scanner sc = new Scanner(x);
                while (sc.hasNext()) {
                    System.out.println(sc.next());
                }
                sc.close();

                propertiesLocal.setProperty("portno", Integer.toString(PORT_NUMBER.getNumber()));
                propertiesLocal.setProperty("roomlist", "General;Teen;Music;Party");
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            return (propertiesLocal);
        }
    }

    //Function to Get Remote User Address
    protected void getRemoteUserAddress(Socket clientSocket, String toUserName, String fromUserName) {
        client = getClient(toUserName);
        if (client != null) {
            sendMessageToClient(client.getClientSocket(),
                    "REIP " + fromUserName + "~" + clientSocket.getInetAddress().getHostAddress());
        }
    }

    //Function to get the User Count in the Room
    protected void getUserCount(Socket clientSocket, String roomName) {
        int userCount = 0;
        for (Client user : clientList) {
            if (user.getClientRoomName().equals(roomName)) {
                userCount++;
            }
        }
        sendMessageToClient(clientSocket, "ROCO " + roomName + "~" + userCount);
    }

    //Function To Check whether the Username is Already Exists
    private boolean isUserExists(String userName) {
        return getClient(userName) != null;
    }

    /**
     * Function to Quit Video Chat
     *
     * @param toUserName
     */
    protected void quitVideoChat(String toUserName) {
        client = getClient(toUserName);
        if (client != null) {
            sendMessageToClient(client.getClientSocket(), "QUVC");
        }
    }

    /**
     * Function To Quit Voice Chat
     *
     * @param fromUserName
     * @param toUserName
     */
    protected void quitVoiceChat(String fromUserName, String toUserName) {
        System.out.println(fromUserName + "-->" + toUserName);
        client = getClient(toUserName);
        if (client != null) {
            sendMessageToClient(client.getClientSocket(), "QVCT " + fromUserName + "~" + toUserName);
        }
    }

    /**
     * Function To Reject The Request For Voice Chat
     *
     * @param fromUserName
     * @param toUserName
     */
    protected void rejectCall(String fromUserName, String toUserName) {
        client = getClient(toUserName);
        if (client != null) {
            sendMessageToClient(client.getClientSocket(), "REJC " + fromUserName + "~" + toUserName);
        }

    }

    /**
     * Function to Remove User From Server
     *
     * @param userName
     * @param roomName
     * @param removeType
     */
    public void removeUser(String userName, String roomName, CommonSetting removeType) {
        Client removeClient = getClient(userName);
        if (removeClient != null) {
            clientList.remove(removeClient);
            clientList.trimToSize();
            String removeRFC = null;
            if (removeType.equals(REMOVE_USER)) {
                removeRFC = "REMO " + userName;
            }
            if (removeType == KICK_USER) {
                removeRFC = "INKI " + userName;
            }
            //Send a REMO RFC to all other Users
            for (Client user : clientList) {
                if (user.getClientRoomName().equals(roomName)) {
                    sendMessageToClient(user.getClientSocket(), removeRFC);
                }
            }
        }
    }

    /**
     * Remove User When Exception Occurs
     *
     * @param clientSocket
     */
    protected void removeUserWhenException(Socket clientSocket) {
        String removeUserName, removeRoomName;
        for (Client removeClient : clientList) {
            if (removeClient.getClientSocket().equals(clientSocket)) {
                removeUserName = removeClient.getClientUserName();
                removeRoomName = removeClient.getClientRoomName();
                clientList.remove(removeClient);
                clientList.trimToSize();
                String removeRFC = "REMO " + removeUserName;
                //Send a REMO RFC to all other Users
                for (Client user : clientList) {
                    if (user.getClientRoomName().equals(removeRoomName)) {
                        sendMessageToClient(user.getClientSocket(), removeRFC);
                    }
                }
            }
        }
    }

    /**
     * Function To Request User For Voice Chat
     *
     * @param clientSocket
     * @param fromUserName
     * @param toUserName
     */
    protected void requestForVoiceChat(Socket clientSocket, String fromUserName, String toUserName) {
        client = getClient(toUserName);
        if (client != null) {
            sendMessageToClient(client.getClientSocket(), 
                    "REQU "
                    + getClient(fromUserName).getClientSocket().getInetAddress().getHostAddress() 
                    + "~" + fromUserName);
        }
    }

    //Thread Implementation
    @Override
    public void run() {
        //Accepting all the client connections and create a separate thread
        while (thread != null) {
            try {
                //Accepting the Server Connections
                socket = serverSocket.accept();
                //Create a Separate Thread for that each client
                chatCommunication = new ChatCommunication(this, socket);

                Thread.sleep(THREAD_SLEEP_TIME.getNumber());
            } catch (InterruptedException | IOException ie) {
                stopServer();
                ie.printStackTrace();
            }
        }
    }

    //Function to Send General Message
    protected void sendGeneralMessage(Socket clientSocket, String message, String userName, String roomName) {
        boolean floodFlag = false;
        messageList.add(userName);
        if (messageList.size() > MAX_MESSAGE.getNumber()) {
            messageList.remove(0);
            messageList.trimToSize();

            //Check Whether the User is flooding the message
            String firstMessage = messageList.get(0);
            for(String msg:messageList){
                 if (msg.equals(firstMessage)) {
                    floodFlag = true;
                } else {
                    floodFlag = false;
                    break;
                }
            }
        }

        //Sending a General Message to All the Users
        String messageRFC = "MESS " + userName + ":" + message;
        for (Client user : clientList) {
            if ((user.getClientRoomName().equals(roomName)) && (!(user.getClientUserName().equals(userName)))) {
                sendMessageToClient(user.getClientSocket(), messageRFC);
            }
        }

        //Kick Off the User If he/she flooding the message
        if (floodFlag) {
            sendMessageToClient(clientSocket, "KICK ");
            messageList.clear();
        }

    }

    //Function To Send a Message to Client
    private void sendMessageToClient(Socket clientSocket, String message) {
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            out.write((message + "\r\n").getBytes());
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Function To Send Private Message
     *
     * @param message
     * @param toUserName
     */
    protected void sendPrivateMessage(String message, String toUserName) {
        client = getClient(toUserName);
        if (client != null) {
            sendMessageToClient(client.getClientSocket(), "PRIV " + message);
        }

    }

    /**
     * Function to Get Remote User Address
     *
     * @param clientSocket
     * @param toUserName
     * @param fromUserName
     */
    protected void sendRemoteUserAddress(Socket clientSocket, String toUserName, String fromUserName) {
        client = getClient(fromUserName);
        if (client != null) {
            sendMessageToClient(client.getClientSocket(),
                    "AEIP " + toUserName + "~" + clientSocket.getInetAddress().getHostAddress());
        }
    }

    /**
     * Function To Send User IP For Voice Chat
     *
     * @param clientSocket
     * @param fromUserName
     * @param toUserName
     */
    protected void sendUserIP(Socket clientSocket, String fromUserName, String toUserName) {
        client = getClient(toUserName);
        if (clientSocket != null) {
            sendMessageToClient(client.getClientSocket(), "ADDR "
                    + getClient(fromUserName).getClientSocket().getInetAddress().getHostAddress() + "~" + fromUserName);
        }
    }
}