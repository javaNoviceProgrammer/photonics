package photonics.heater.voltage;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class StepVoltagePreEmphasis extends AbstractVoltage {

	double vStart, vEnd, tStep_usec, tOvershoot_usec, vOvershoot ;
	
	public StepVoltagePreEmphasis(
			@ParamName(name="Step time (usec)") double tStep_usec,
			@ParamName(name="Start Voltage (V)") double vStart,
			@ParamName(name="End Voltage (V)") double vEnd,
			@ParamName(name="Pre-Emphasis Amplitude") double vOvershoot,
			@ParamName(name="Pre-Emphasis Duration (usec)") double tOvershoot_usec
			){
		this.tStep_usec = tStep_usec ;
		this.vStart = vStart ;
		this.vEnd = vEnd ;
		this.vOvershoot = vOvershoot ;
		this.tOvershoot_usec = tOvershoot_usec ;
	}
	
	@Override
	public double getVoltage(double t_usec) {
		if(t_usec < tStep_usec){
			return vStart ;
		}
		else if(t_usec < tStep_usec + tOvershoot_usec ){
			return vOvershoot ;
		}
		else{
			return vEnd ;
		}
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("tStep (usec)", tStep_usec+"") ;
		map.put("tOvershoot (usec)", tOvershoot_usec+"") ;
		map.put("vStart (V)", vStart+"") ;
		map.put("vEnd (V)", vEnd+"") ;
		map.put("Overshoot (V)", (vOvershoot-vEnd)+"") ;
		map.put("VoltageName", "StepVoltagePreEmphasis") ;
		return map;
	}

}
