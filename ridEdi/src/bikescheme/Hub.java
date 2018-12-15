/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  
 * Hub system.
 *
 * 
 * @author pbj
 *
 */
public class Hub implements AddDStationObserver {
    public static final Logger logger = Logger.getLogger("bikescheme");

    private HubTerminal terminal;
    private HubDisplay display;
    private Map<String,DStation> dockingStationMap;
    private Map<String,User> userMap;
    private Map<String,Bike> bikeMap;
    private Map<Bike,BikeHire> currentBikeHires;
    
    /**
     * 
     * Construct a hub system with an operator terminal, a wall display 
     * and connections to a number of docking stations (initially 0). 
     * 
     * Schedule update of the hub wall display every 5 minutes with
     * docking station occupancy data.
     * 
     * @param instanceName
     */
    public Hub() {

        // Construct and make connections with interface devices
    	Clock.createInstance();
        terminal = new HubTerminal("ht");
        terminal.setObserver(this);
        display = new HubDisplay("hd");
        
        dockingStationMap = new HashMap<String,DStation>();
        userMap =  new HashMap<String,User>();
        bikeMap =  new HashMap<String,Bike>();
        currentBikeHires =  new HashMap<Bike,BikeHire>();
        
        // Schedule timed notification for generating updates of 
        // hub display. 

        // The idiom of an anonymous class is used here, to make it easy
        // for hub code to process multiple timed notification, if needed.
         
        Clock.getInstance().scheduleNotification(
                new TimedNotificationObserver() {

                    
                    @Override
                    public void processTimedNotification() {
                        logger.fine("");
                        List<String> occupancyData = showOccupancy();
                        display.showOccupancy(occupancyData);
                    }

                },
                Clock.getStartDate(), 
                0, 
                5);

    }

    public void setDistributor(EventDistributor d) {
        
        // The clock device is connected to the EventDistributor here, even
        // though the clock object is not constructed here, 
        // as no distributor is available to the Clock constructor.
        Clock.getInstance().addDistributorLinks(d);
        terminal.addDistributorLinks(d);
    }
    
    public void setCollector(EventCollector c) {
        display.setCollector(c); 
        terminal.setCollector(c);
    }
    

    /**
     * 
     */
    @Override
    public void addDStation(
            String instanceName, 
            int eastPos, 
            int northPos,
            int numPoints) {
        logger.info("Docking Station " + instanceName + " added");
        
        DStation newDStation = 
                new DStation(this, instanceName, eastPos, northPos, numPoints);
        dockingStationMap.put(instanceName, newDStation);
        
        // Now connect up DStation to event distributor and collector.
        
        EventDistributor d = terminal.getDistributor();
        EventCollector c = display.getCollector();
        
        newDStation.setDistributor(d);
        newDStation.setCollector(c);
        
        
    }
    
    public List<String> showOccupancy(){
    	
    	List<String> output = new ArrayList<String>();
    	
    	for(DStation dst : dockingStationMap.values()){
    		output.add(dst.getInstanceName());
    		output.add(String.valueOf(dst.getEastPos()));
    		output.add(String.valueOf(dst.getNorthPos()));
    		output.add(dst.occupancy());
    		output.add(String.valueOf(dst.occupiedPoints()));
    		output.add(String.valueOf(dst.getNumPoints()));
    	}
    	
    	return output;
    }
    
    public DStation getDStation(String instanceName) {
        return dockingStationMap.get(instanceName);
    }
    
    public User identifyUser(String keyID){
        return userMap.get(keyID);
    }
    
    public boolean isRegisteredBike(String bikeID){
        if (bikeMap.containsKey(bikeID)) return true;
        else return false;
    }
    
    public Bike identifyBike(String bikeID){
        return bikeMap.get(bikeID);
    }
    
    public void registerUser(
            String personalDetails,
            String bankDetails,
            String keyID){    
        logger.info("User registered: " + personalDetails);
        User newUser = new User(personalDetails, bankDetails, keyID);
        userMap.put(keyID, newUser);
    }
    
    public void addBike(String bikeID){    
        logger.info("New bike added");
        Bike newBike = new Bike(bikeID);
        bikeMap.put(bikeID, newBike);
    }
    
    public void startBikeHire(Date startTime, DStation startStation, User user, Bike bike){
        logger.info("New bike hire added");
        BikeHire newHire = new BikeHire(startTime, startStation, user, bike);
    	currentBikeHires.put(bike, newHire);
        user.setCurrentHire(newHire);
        bike.setBikeHire(newHire);
    }
    
    public void endBikeHire(Date endTime, DStation endStation, Bike bike){
        BikeHire hire = currentBikeHires.get(bike);
        int duration = Clock.minutesBetween(hire.getStartTime(), endTime);
        hire.setDuration(duration);
        User user = hire.getUser();      
        currentBikeHires.remove(bike);
        user.endBikeHire(endTime, endStation);
        bike.endHire();
    }
    
    public List<String> generateActivity(String keyID){
        User user = userMap.get(keyID);
        List<String> activityData = user.getActivity();
        return activityData;
    }

    public boolean canHire(String keyID) {
        User user = identifyUser(keyID);
        if (!user.hasCurrentHire()) return true;
        else return false;
    }
}