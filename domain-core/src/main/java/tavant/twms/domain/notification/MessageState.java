package tavant.twms.domain.notification;

import java.util.HashMap;
import java.util.Map;


public enum MessageState {
	SENT("Sent"),
	PENDING("Pending"),	
	FAILED("Failed"),
    CANCELLED("Cancelled");
	
	private static Map<String,MessageState> messageStatesMap=new HashMap<String,MessageState>();
    static{
    	for(MessageState aState : MessageState.values())
    		messageStatesMap.put(aState.getState(), aState);
    }
    
    private String state;
	
    private MessageState(String state) {    	
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    @Override
    public String toString() {
        return this.state;
    }

    public static MessageState getMessageState(String stateString){
    	if(!messageStatesMap.containsKey(stateString))
    		throw new RuntimeException("No Message State is defined for string["+stateString+"]");
    	return messageStatesMap.get(stateString);
    }	

}
