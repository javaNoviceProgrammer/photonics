package photonics.heater.voltage;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class PulseVoltage extends AbstractVoltage {

	double vStart, vEnd, tRiseEdge_usec, tFallEdge_usec ;
	
	public PulseVoltage(
			@ParamName(name="Rising-Edge time (usec)") double tRiseEdge_usec,
			@ParamName(name="Falling-Edge time (usec)") double tFallEdge_usec,
			@ParamName(name="Start Voltage (V)") double vStart,
			@ParamName(name="End Voltage (V)") double vEnd
			){
		this.tRiseEdge_usec = tRiseEdge_usec ;
		this.tFallEdge_usec = tFallEdge_usec ;
		this.vStart = vStart ;
		this.vEnd = vEnd ;
	}
	
	@Override
	public double getVoltage(double t_usec) {
		if(t_usec < tRiseEdge_usec || t_usec > tFallEdge_usec){
			return vStart ;
		}
		else{
			return vEnd ;
		}
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("tRiseEdge (usec)", tRiseEdge_usec+"") ;
		map.put("tFallEdge (usec)", tFallEdge_usec+"") ;
		map.put("vStart (V)", vStart+"") ;
		map.put("vEnd (V)", vEnd+"") ;
		map.put("VoltageName", "PulseVoltage") ;
		return map;
	}

}
