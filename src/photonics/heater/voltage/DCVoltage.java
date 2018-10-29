package photonics.heater.voltage;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class DCVoltage extends AbstractVoltage {

	double vDC ;
	
	public DCVoltage(
			@ParamName(name="DC voltage (V)") double vDC
			){
		this.vDC = vDC ;
	}
	
	@Override
	public double getVoltage(double t_usec) {
		return vDC ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("vDC", vDC+"") ;
		map.put("VoltageName", "DCVoltage") ;
		return map ;
	}

}
