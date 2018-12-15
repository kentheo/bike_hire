package bikescheme;

import java.util.Date;

public class BikeHire {
    
    private Date startTime;
    private Date endTime;
    private DStation startStation;
    private DStation endStation;
    private Bike bike;
    private User user;
    private int duration;
    private boolean completed;
   
    
    public BikeHire(Date startTime, DStation startStation, User user, Bike bike) {
    	this.startTime = startTime;
        this.startStation = startStation;
        this.bike = bike;
        this.user = user;
	}
    
    public User getUser(){
    	return user;
    }

	public void endBikeHire(Date endTime, DStation endStation){
		this.endTime = endTime;
		this.endStation = endStation;
        this.completed = true;
    }
    
    public Date getStartTime(){
    	
    	return this.startTime;
    }

    public Date getEndTime(){
    	return this.endTime;
    }
    
    public DStation getStartStation(){
    	return this.startStation;
    }
    
    public DStation getEndStation(){
    	return this.endStation;
    }
    
    public void setDuration(int duration){
    	this.duration = duration;
    }
    
    public int hireDuration(){
    	return duration;
    }
}