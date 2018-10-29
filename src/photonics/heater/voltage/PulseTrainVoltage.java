package photonics.heater.voltage;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class PulseTrainVoltage extends AbstractVoltage {

	double vStart, vEnd, tWidth_usec, tPeriod_usec, dutyCycle ;
	double freq_MHz ;
	int numPeriods ;
	
/*	public PulseTrainVoltage(
			@ParamName(name="Pulse Width (usec)") double tWidth_usec,
			@ParamName(name="Pulse Period (usec)") double tPeriod_usec,
			@ParamName(name="Number of Periods") int numPeriods,
			@ParamName(name="Start Voltage (V)") double vStart,
			@ParamName(name="End Voltage (V)") double vEnd
			){
		this.tWidth_usec = tWidth_usec ;
		this.tPeriod_usec = tPeriod_usec ;
		this.numPeriods = numPeriods ;
		this.vStart = vStart ;
		this.vEnd = vEnd ;
	}*/
	
	public PulseTrainVoltage(
			@ParamName(name="Duty Cycle ") double dutyCycle,
			@ParamName(name="Pulse Period (usec)") double tPeriod_usec,
			@ParamName(name="Number of Periods") int numPeriods,
			@ParamName(name="Start Voltage (V)") double vStart,
			@ParamName(name="End Voltage (V)") double vEnd
			){
		this.dutyCycle = dutyCycle ;
		this.tWidth_usec = tPeriod_usec * dutyCycle ;
		this.tPeriod_usec = tPeriod_usec ;
		this.numPeriods = numPeriods ;
		this.vStart = vStart ;
		this.vEnd = vEnd ;
		this.freq_MHz = 1/tPeriod_usec ;
	}
	
	@Override
	public double getVoltage(double t_usec) {
		double V = 0 ;
		int N = numPeriods ;
		for(int i=0; i<N; i++){
			PulseVoltage pulse = new PulseVoltage(i*tPeriod_usec, i*tPeriod_usec + tWidth_usec, vStart, vEnd) ;
			V += pulse.getVoltage(t_usec) ;
		}
		return V ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("Duty Cycle", dutyCycle+"") ;
		map.put("tWidth (usec)", tWidth_usec+"") ;
		map.put("tPeriod (usec)", tPeriod_usec+"") ;
		map.put("Number of Periods", numPeriods+"") ;
		map.put("vStart (V)", vStart+"") ;
		map.put("vEnd (V)", vEnd+"") ;
		map.put("VoltageName", "PulseTrainVoltage") ;
		map.put("freq (MHz)", freq_MHz+"") ;
		return map;
	}

}
