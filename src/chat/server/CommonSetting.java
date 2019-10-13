package chat.server;

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