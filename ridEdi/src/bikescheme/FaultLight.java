package bikescheme;

import java.util.ArrayList;
import java.util.List;

/* 
 * Red Fault Light output device.
 */

public class FaultLight extends AbstractOutputDevice{
    
    public FaultLight(String instanceName) {
        super(instanceName);
    }
    
    public void flash() {
        logger.fine(getInstanceName());
        
        String deviceClass = "FaultLight";
        String deviceInstance = getInstanceName();
        String messageName = "flashed";
        List<String> valueList = new ArrayList<String>();
 
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                valueList));
        
    }
}