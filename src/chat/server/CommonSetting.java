package chat.server;

/**
 * <pre>
 * public interface CommonSettings {
    //By Default
    //public static final //By Default
    int THREAD_SLEEP_TIME = 1500;
    int KICK_USER = 0;
    int REMOVE_USER = 1;
    int MAX_MESSAGE = 30;
    int PORT_NUMBER = 1436;
    String ROOM_NAME = "General";
}
* </pre>
 */
public enum CommonSetting {
    KICK_USER(0),
    REMOVE_USER(1),
    THREAD_SLEEP_TIME(1500),
    MAX_MESSAGE(30),
    PORT_NUMBER(1436),
    ROOM_NAME("General");
    
    private int number;
    private String roomName;
    
    private CommonSetting(){
        
    }
    
    private CommonSetting(int number){
        this.number=number;
    }
    
   
    private CommonSetting(String string){
        this.roomName =string;
    }

    public int getNumber(){
        return number;
    }
    
    public String getRoomName() {
        return roomName;
    }
    
}