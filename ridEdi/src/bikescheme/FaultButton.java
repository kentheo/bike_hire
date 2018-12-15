package bikescheme;

import java.util.ArrayList;
import java.util.List;
/* 
 * Button input device for reporting fault
 */
public class FaultButton extends AbstractInputDevice{
    
    private FaultButtonObserver observer;
    private FaultLight faultLight;
    
    public FaultButton(String instanceName) {
        super(instanceName);   
        faultLight = new FaultLight(instanceName + ".fl");
    }
    
    public void setObserver(FaultButtonObserver o) {
        observer = o;
    }
    
    public void buttonPressed(){
        logger.fine(getInstanceName());
        faultLight.flash();
        observer.buttonPressed();
        
    }
    
    public void receiveEvent(Event e) {
        
        if (e.getMessageName().equals("pressButton") 
                && e.getMessageArgs().size() == 0) {
            
            buttonPressed();
            
        } else {
            super.receiveEvent(e);
        }
    }

}