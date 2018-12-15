/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 *  
 * Docking Station.
 * 
 * @author pbj
 *
 */
public class DStation implements StartRegObserver, KeyInsertionObserver,
	ViewActivityObserver, BikeDockingObserver{
    public static final Logger logger = Logger.getLogger("bikescheme");
    
    private Hub hub;
    
    private String instanceName;
    private int eastPos;
    private int northPos;
    private int numPoints;
    
    private DSTouchScreen touchScreen;
    private CardReader cardReader; 
    private KeyIssuer keyIssuer;
    private List<DPoint> dockingPoints;
    private KeyReader keyReader;
 
    /**
     * 
     * Construct a Docking Station object with touch screen, card reader
     * and key issuer interface devices and a connection to a number of
     * docking points.
     * 
     * If the instance name is <foo>, then the Docking Points are named
     * <foo>.1 ... <foo>.<numPoints> . 
     * 
     * @param instanceName
     */
    public DStation(
            Hub hub,
            String instanceName,
            int eastPos,
            int northPos,
            int numPoints) {
        
     // Construct and make connections with interface devices
        
        this.hub = hub;
        this.instanceName = instanceName;
        this.eastPos = eastPos;
        this.northPos = northPos;
        this.numPoints = numPoints;
        
        touchScreen = new DSTouchScreen(instanceName + ".ts");
        touchScreen.setObserver(this);
        touchScreen.setViewActivityObserver(this);
        touchScreen.setDockingObserver(this);
        
        cardReader = new CardReader(instanceName + ".cr");
        keyIssuer = new KeyIssuer(instanceName + ".ki");
        keyReader = new KeyReader(instanceName + ".kr");
        keyReader.setObserver(this);
        
        dockingPoints = new ArrayList<DPoint>();
        
        for (int i = 1; i <= numPoints; i++) {
            DPoint dp = new DPoint(this, instanceName + "." + i, i - 1);
            dockingPoints.add(dp);
        }
    }
       
    void setDistributor(EventDistributor d) {
        touchScreen.addDistributorLinks(d); 
        cardReader.addDistributorLinks(d);
        keyReader.addDistributorLinks(d);
        for (DPoint dp : dockingPoints) {
            dp.setDistributor(d);
        }
    }
    
    void setCollector(EventCollector c) {
        touchScreen.setCollector(c);
        cardReader.setCollector(c);
        keyIssuer.setCollector(c);
        for (DPoint dp : dockingPoints) {
            dp.setCollector(c);
        }
    }

    public String getInstanceName() {
        return instanceName;
    }
    
    public int getEastPos() {
        return eastPos;
    }
    
    public int getNorthPos() {
        return northPos;
    }
    
    public int getNumPoints(){
    	return this.numPoints;
    }
    
    public int occupiedPoints(){
    	int counter = 0;
    	for(DPoint point : dockingPoints){
    		if(point.hasBike()) counter++;
    	}
    	return counter;
    }
    
    public String occupancy(){
    	double occupancy = (double)this.occupiedPoints()/this.numPoints;
    	if(occupancy >= 0.8) return "HIGH";
    	else if (occupancy <= 0.2) return "LOW";
    	else return "OK";
    }
    
    /** 
     * Implementation of docking station functionality for 
     * "register user" use case.
     * 
     * Method called on docking station receiving a "start registration"
     * triggering input event at the touch screen.
     * 
     * @param personalInfo
     */
    public void startRegReceived(String personalInfo) {
        logger.info("Starting registration on instance " + getInstanceName());
        
        String authCode = cardReader.readCard();
        String keyID = keyIssuer.issueKey();
        
        hub.registerUser(personalInfo, authCode, keyID);
    }
    

    /** 
     * Implementation of docking station functionality for 
     * "view user activity" use case.
     * 
     * Method called on docking station receiving a "view activity"
     * triggering input event at the touch screen.
     * 
     * @param personalInfo
     */

    
    public void viewActivityReceived() {
        logger.info("Starting viewActivity on instance " + getInstanceName());
        
        touchScreen.requestKey();
        String keyID = keyReader.waitForKeyInsertion();
        User user = hub.identifyUser(keyID);
        List<String> activityData = user.getActivity();
        
        /** 
         * Generate dummy display of user activity.
         
       
        String[] activityArray = 
                // "HireTime","HireDS","ReturnDS","Duration (min)"
                {  "  09:30", "    A","      B","           24",
                   "  17:10","     B","       A","            32" };
		
        List<String> activityData = Arrays.asList(activityArray);
        */
        
        touchScreen.displayActivity(activityData);
    }
    
    public void noBikeError(){
    	touchScreen.noBikeError();
    }

    public void startBikeHire(String keyID, Bike bike) {
        Date currentTime = Clock.getInstance().getDateAndTime();
        User user = hub.identifyUser(keyID);
        hub.startBikeHire(currentTime, this, user, bike);
    }

    public void bikeDocked(String bikeID) {
        if (hub.isRegisteredBike(bikeID)){
            Bike bike = hub.identifyBike(bikeID);
            Date currentDate = Clock.getInstance().getDateAndTime();
            hub.endBikeHire(currentDate, this, bike);
            
        }
        
        else {
            hub.addBike(bikeID);
            
        }
    }

    public Hub getHub() {
        return hub;
    }

    @Override
    public void keyInserted(String keyId) {
        // TODO Auto-generated method stub
        
    }
}