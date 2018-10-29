package photonics.heater.voltage;

import java.util.Map;

public abstract class AbstractVoltage {

	public abstract double getVoltage(double t_usec) ;
	
	public abstract Map<String, String> getAllParameters() ;
}
