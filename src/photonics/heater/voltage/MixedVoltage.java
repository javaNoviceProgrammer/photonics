package photonics.heater.voltage;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class MixedVoltage extends AbstractVoltage {

	// This class takes in an array of voltages and mix them together
	
	AbstractVoltage[] voltage ;
	
	public MixedVoltage(
			@ParamName(name="Choose Voltage") AbstractVoltage[] voltage
			){
		this.voltage = voltage ;
	}
	
	
	@Override
	public double getVoltage(double t_usec) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("VoltageName", "BoostPWMSignal") ;
		return map;
	}

}
