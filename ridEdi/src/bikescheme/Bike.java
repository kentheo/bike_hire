package bikescheme;

import java.util.logging.Logger;

public class Bike {
    public static final Logger logger = Logger.getLogger("bikescheme");
    
    private String bikeID;
    private BikeHire currentHire;
    private boolean faulty;
    private boolean isHired = false;
    
    public Bike (String bikeID) {
        this.bikeID = bikeID;
        this.faulty = false;
            }
    
    public void setBikeHire(BikeHire hire){
        logger.info("Current hire set: Bike");
        this.currentHire = hire;
        this.isHired = true;
    }
    
    public void endHire(){
        logger.info("Current hire ended: Bike");
        this.currentHire = null;
    	this.isHired = false;
    }
    
    public BikeHire getBikeHire(){
    	return this.currentHire;
    }
    
    public void setIsFaulty(){
        this.faulty = true;
    }
    
    public boolean getIsFaulty(){
        return this.faulty;
    }
}