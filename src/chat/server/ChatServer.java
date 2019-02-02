package chat.server;

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

public class ChatServer implements Serializable, Runnable, CommonSettings {

    private static final long serialVersionUID = 1L;
    private Properties properties;
    private ServerSocket serverSocket;
    private Socket socket;
    private ArrayList<ClientObject> userArrayList;
    private ArrayList<String> messageArrayList;
    private Thread thread;
    private DataOutputStream out;
    private int count;
    private ClientObject clientObject;
    private String roomList;
    private ChatCommunication chatCommunication;

    public void startServer() {
        properties = getProperties();
        /**
         * ******* Initialize the Server Socket ********
         */
        try {
            roomList = "";
            if (properties.getProperty("roomlist") != null) {
                roomList = properties.getProperty("roomlist");
            }
            int portNo = PORT_NUMBER;
            if (properties.getProperty("portno") != null) {
                portNo = Integer.parseInt(properties.getProperty("portno"));
            }
            serverSocket = new ServerSocket(portNo);
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        /**
         * ****** Initialize the Array List *********
         */
        userArrayList = new ArrayList<>();
        messageArrayList = new ArrayList<>();

        /**
         * ****** Initialize the thread ************
         */
        thread = new Thread(this);
        thread.start();
    }

    /**
     * ****** Function To Add a New Client in to the Server List
     *
     *********
     * @param clientSocket
     * @param userName
     */
    protected void addUser(Socket clientSocket, String userName) {
        /**
         * * If User name Exists return *
         */
        if (isUserExists(userName)) {
            sendMessageToClient(clientSocket, "EXIS");
            return;
        }
        /**
         * ****** Send a Room List *******
         */
        sendMessageToClient(clientSocket, "ROOM " + roomList);

        /**
         * ****** Send the New User Detail into All Other Users ***
         */
        String addRFC = "ADD  " + userName;
        StringBuffer stringbuffer = new StringBuffer("LIST ");
        for (ClientObject user : userArrayList) {
            /**
             * * Check the Room Name ****
             */
            if (user.getClientRoomName().equals(ROOM_NAME)) {
                sendMessageToClient(user.getClientSocket(), addRFC);
                stringbuffer.append(user.getClientUserName());
                stringbuffer.append(";");
            }
        }
        /**
         * *** Add a user in to array list **
         */
        clientObject = new ClientObject(clientSocket, userName, ROOM_NAME);
        userArrayList.add(clientObject);

        /**
         * ****** Sending the Complete User List to the New User **********
         */
        stringbuffer.append(userName);
        stringbuffer.append(";");
        sendMessageToClient(clientSocket, stringbuffer.toString());
    }

    /**
     * ******* Function To Change the Room
     *
     ****************
     * @param clientSocket
     * @param userName
     * @param newRoomName
     */
    public void changeRoom(Socket clientSocket, String userName, String newRoomName) {
        int clientIndex = getIndexOf(userName);
        if (clientIndex >= 0) {
            /**
             * ******
             * Update the Old Room to New Room and send the RFC ********
             */
            ClientObject TempClientObject = userArrayList.get(clientIndex);
            String oldRoomName = TempClientObject.getClientRoomName();
            TempClientObject.setClientRoomName(newRoomName);
            userArrayList.set(clientIndex, TempClientObject);
            sendMessageToClient(clientSocket, "CHRO " + newRoomName);

            /**
             * **
             * Send all the Users list of that particular room to that client
             * socket **
             */
            StringBuffer stringbuffer = new StringBuffer("LIST ");
            for (ClientObject user : userArrayList) {
                /**
                 * * Check the Room Name ****
                 */
                if (user.getClientRoomName().equals(newRoomName)) {
                    stringbuffer.append(user.getClientUserName());
                    stringbuffer.append(";");
                }
            }
            sendMessageToClient(clientSocket, stringbuffer.toString());

            /**
             * ******** Inform to Old Room and New Room Users *********
             */
            String oldRoomRFC = "LERO " + userName + "~" + newRoomName;
            String newRoomRFC = "JORO " + userName;
            for (ClientObject user : userArrayList) {
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

    /**
     * ********* Function to Destroy the Objects **********
     */
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
        userArrayList = null;
        messageArrayList = null;
    }

    /**
     * ******* Function To Get the Object Of Given User Name ********
     */
    private ClientObject getClientObject(String userName) {
        for (ClientObject user : userArrayList) {
            if (user.getClientUserName().equalsIgnoreCase(userName)) {
                return user;
            }
        }
        return null;
    }

    /**
     * ******* Loading Properties File ******************
     */
    private Properties getProperties() {
        /**
         * Getting the Property Value From Property File
         */
        Properties propertiesLocal = new Properties();
        try {
            File x = new File("server.properties");
            if (x.exists()) {
                InputStream inputstream = this.getClass().getResourceAsStream("server.properties");
                //this.getClass().getClassLoader().getResourceAsStream("server.properties");
                propertiesLocal.load(inputstream);
                inputstream.close();
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

                propertiesLocal.setProperty("portno", Integer.toString(PORT_NUMBER));
                propertiesLocal.setProperty("roomlist", "General;Teen;Music;Party");
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            return (propertiesLocal);
        }
    }

    /**
     * ********* Function to get the Index of specified User Name *******
     */
    //NO NEED THIS FUNCTION
    // USE indexOf() in ArrayList
    private int getIndexOf(String userName) {
        for (ClientObject user : userArrayList) {
            if (user.getClientUserName().equalsIgnoreCase(userName)) {
                return userArrayList.indexOf(user);
            }
        }
        return -1;
    }

    /**
     * ********* Function to Get Remote User Address *****************
     */
    protected void getRemoteUserAddress(Socket clientSocket, String toUserName, String fromUserName) {
        clientObject = getClientObject(toUserName);
        if (clientObject != null) {
            sendMessageToClient(clientObject.getClientSocket(),
                    "REIP " + fromUserName + "~" + clientSocket.getInetAddress().getHostAddress());
        }

    }

    /**
     * ******* Function to get the User Count in the Room **********
     */
    protected void getUserCount(Socket clientSocket, String roomName) {
        int userCount = 0;
        for (ClientObject user : userArrayList) {
            if (user.getClientRoomName().equals(roomName)) {
                userCount++;
            }
        }
        sendMessageToClient(clientSocket, "ROCO " + roomName + "~" + userCount);
    }

    /**
     * *** Function To Check whether the Username is Already Exists *********
     */
    private boolean isUserExists(String userName) {
        if (getClientObject(userName) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ********* Function to Quit Video Chat
     *
     *****************
     * @param toUserName
     */
    protected void quitVideoChat(String toUserName) {
        clientObject = getClientObject(toUserName);
        if (clientObject != null) {
            sendMessageToClient(clientObject.getClientSocket(), "QUVC");
        }
    }

    /**
     * *********** Function To Quit Voice Chat
     *
     ************
     * @param fromUserName
     * @param toUserName
     */
    protected void quitVoiceChat(String fromUserName, String toUserName) {
        System.out.println(fromUserName + "-->" + toUserName);
        clientObject = getClientObject(toUserName);
        if (clientObject != null) {
            sendMessageToClient(clientObject.getClientSocket(), "QVCT " + fromUserName + "~" + toUserName);
        }

    }

    /**
     * *********** Function To Reject The Request For Voice Chat
     *
     ************
     * @param fromUserName
     * @param toUserName
     */
    protected void rejectCall(String fromUserName, String toUserName) {
        clientObject = getClientObject(toUserName);
        if (clientObject != null) {
            sendMessageToClient(clientObject.getClientSocket(), "REJC " + fromUserName + "~" + toUserName);
        }

    }

    /**
     * ******** Function to Remove User From Server
     *
     *************
     * @param userName
     * @param roomName
     * @param removeType
     */
    public void removeUser(String userName, String roomName, int removeType) {
        ClientObject removeclientobject = getClientObject(userName);
        if (removeclientobject != null) {
            userArrayList.remove(removeclientobject);
            userArrayList.trimToSize();
            String removeRFC = null;
            if (removeType == REMOVE_USER) {
                removeRFC = "REMO " + userName;
            }
            if (removeType == KICK_USER) {
                removeRFC = "INKI " + userName;
            }
            /**
             * *** Send a REMO RFC to all other Users ***
             */
            for (ClientObject user : userArrayList) {
                if (user.getClientRoomName().equals(roomName)) {
                    sendMessageToClient(user.getClientSocket(), removeRFC);
                }
            }
        }
    }

    /**
     * ******** Remove User When Exception Occurs
     *
     *************
     * @param clientSocket
     */
    protected void removeUserWhenException(Socket clientSocket) {
        String removeUserName, removeRoomName;
        for (ClientObject removeClientObject : userArrayList) {
            if (removeClientObject.getClientSocket().equals(clientSocket)) {
                removeUserName = removeClientObject.getClientUserName();
                removeRoomName = removeClientObject.getClientRoomName();
                userArrayList.remove(removeClientObject);
                userArrayList.trimToSize();
                String removeRFC = "REMO " + removeUserName;
                /**
                 * *** Send a REMO RFC to all other Users ***
                 */
                for (ClientObject user : userArrayList) {
                    if (user.getClientRoomName().equals(removeRoomName)) {
                        sendMessageToClient(user.getClientSocket(), removeRFC);
                    }
                }
            }
        }
    }

    /**
     * *********** Function To Request User For Voice Chat
     *
     ************
     * @param clientSocket
     * @param fromUserName
     * @param toUserName
     */
    protected void requestForVoiceChat(Socket clientSocket, String fromUserName, String toUserName) {
        clientObject = getClientObject(toUserName);
        if (clientObject != null) {
            sendMessageToClient(clientObject.getClientSocket(), "REQU "
                    + getClientObject(fromUserName).getClientSocket().getInetAddress().getHostAddress() + "~" + fromUserName);
        }

    }

    /**
     * *********** Thread Implementation **************
     */
    @Override
    public void run() {
        /**
         * Accepting all the client connections and create a separate thread
         */
        while (thread != null) {
            try {
                /**
                 * ****** Accepting the Server Connections **********
                 */
                socket = serverSocket.accept();
                /**
                 * Create a Separate Thread for that each client ************
                 */
                chatCommunication = new ChatCommunication(this, socket);

                Thread.sleep(THREAD_SLEEP_TIME);
            } catch (InterruptedException | IOException ie) {
                stopServer();
                ie.printStackTrace();
            }
        }
    }

    /**
     * ****** Function to Send General Message **************
     */
    protected void sendGeneralMessage(Socket clientSocket, String message, String userName, String roomName) {
        boolean floodFlag = false;
        messageArrayList.add(userName);
        if (messageArrayList.size() > MAX_MESSAGE) {
            messageArrayList.remove(0);
            messageArrayList.trimToSize();

            /**
             * ******* Check Whether the User is flooding the message ********
             */
            String firstMessage = messageArrayList.get(0);
            for (count = 1; count < messageArrayList.size(); count++) {
                if (messageArrayList.get(count).equals(firstMessage)) {
                    floodFlag = true;
                } else {
                    floodFlag = false;
                    break;
                }
            }
        }

        /**
         * ****** Sending a General Message to All the Users ******
         */
        String messageRFC = "MESS " + userName + ":" + message;
        for (ClientObject user : userArrayList) {
            if ((user.getClientRoomName().equals(roomName)) && (!(user.getClientUserName().equals(userName)))) {
                sendMessageToClient(user.getClientSocket(), messageRFC);
            }
        }

        /**
         * ****** Kick Off the User If he/she flooding the message *******
         */
        if (floodFlag) {
            sendMessageToClient(clientSocket, "KICK ");
            messageArrayList.clear();
        }

    }

    /**
     * *** Function To Send a Message to Client *********
     */
    private void sendMessageToClient(Socket clientSocket, String message) {
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            out.write((message + "\r\n").getBytes());
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * *********** Function To Send Private Message
     *
     ************
     * @param message
     * @param toUserName
     */
    protected void sendPrivateMessage(String message, String toUserName) {
        clientObject = getClientObject(toUserName);
        if (clientObject != null) {
            sendMessageToClient(clientObject.getClientSocket(), "PRIV " + message);
        }

    }

    /**
     * ********* Function to Get Remote User Address
     *
     *****************
     * @param clientSocket
     * @param toUserName
     * @param fromUserName
     */
    protected void sendRemoteUserAddress(Socket clientSocket, String toUserName, String fromUserName) {
        clientObject = getClientObject(fromUserName);
        if (clientObject != null) {
            sendMessageToClient(clientObject.getClientSocket(),
                    "AEIP " + toUserName + "~" + clientSocket.getInetAddress().getHostAddress());
        }
    }

    /**
     * *********** Function To Send User IP For Voice Chat
     *
     ************
     * @param clientSocket
     * @param fromUserName
     * @param toUserName
     */
    protected void sendUserIP(Socket clientSocket, String fromUserName, String toUserName) {
        clientObject = getClientObject(toUserName);
        if (clientSocket != null) {
            sendMessageToClient(clientObject.getClientSocket(), "ADDR "
                    + getClientObject(fromUserName).getClientSocket().getInetAddress().getHostAddress() + "~" + fromUserName);
        }
    }
}
