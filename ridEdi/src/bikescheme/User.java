package bikescheme;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class User {
    public static final Logger logger = Logger.getLogger("bikescheme");
    
    private String personalDetails;
    private String bankDetails;
    private String keyID;
    private boolean hasCurrentHire = false;
    private BikeHire currentHire;
    private List<BikeHire> dailyHires;
    
    public User (
            String personalDetails,
            String bankDetails,
            String keyID) {
        
        this.personalDetails = personalDetails;
        this.bankDetails = bankDetails;
        
        dailyHires = new ArrayList<BikeHire>();
        
            }
    
    public String getKey(){
        return this.keyID;
    }
    
    public void setCurrentHire(BikeHire hire){
        logger.info("Current hire set: User");
        this.hasCurrentHire = true;
        currentHire = hire;
    }
    
    public boolean hasCurrentHire(){
        return this.hasCurrentHire;
    }
    
    public BikeHire getCurrentHire(){
    	return currentHire;
    }
    
    public void endBikeHire(Date endTime, DStation endStation){
        logger.info("Current hire ended: User");
        this.hasCurrentHire = false;
        currentHire.endBikeHire(endTime, endStation);
        dailyHires.add(currentHire);
        currentHire = null;
    }
    
    public List<String> getActivity(){
        List<String> output = new ArrayList<String>();
        for(BikeHire hire : dailyHires){
        	output.add(Clock.format(hire.getStartTime()));
        	output.add(hire.getStartStation().getInstanceName());
        	output.add(hire.getEndStation().getInstanceName());
        	output.add(String.valueOf(hire.hireDuration()));
        }
        return output;
    }
    

}