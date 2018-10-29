package photonics.heater.voltage;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class BoostPWMVoltage extends AbstractVoltage {
	double vStart, vEnd, tWidthInitial_usec, tPeriod_usec, comFactor ;
	int numPeriods ;
	
	public BoostPWMVoltage(
			@ParamName(name="Initial Pulse Width (usec)") double tWidthInitial_usec,
			@ParamName(name="Pulse Period (usec)") double tPeriod_usec,
			@ParamName(name="Number of Periods") int numPeriods,
			@ParamName(name="Start Voltage (V)") double vStart,
			@ParamName(name="End Voltage (V)") double vEnd
			){
		this.tWidthInitial_usec = tWidthInitial_usec ;
		this.tPeriod_usec = tPeriod_usec ;
		this.numPeriods = numPeriods ;
		this.vStart = vStart ;
		this.vEnd = vEnd ;
	}
	
	@Override
	public double getVoltage(double t_usec) {
		double V = 0 ;
		PulseVoltage pulse = new PulseVoltage(0, tWidthInitial_usec, vStart, vEnd) ; // this is the initial pulse
		V += pulse.getVoltage(t_usec) ;
		double T0 = tWidthInitial_usec + tPeriod_usec/2 ;
		for(int i=0; i<numPeriods; i++){
			double T = T0 + tPeriod_usec * i ;
			pulse = new PulseVoltage(T, T + tPeriod_usec/2, vStart, vEnd) ;
			V += pulse.getVoltage(t_usec) ;
		}
		return V ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("tWidth Initial (usec)", tWidthInitial_usec+"") ;
		map.put("tPeriod (usec)", tPeriod_usec+"") ;
		map.put("Number of Periods", numPeriods+"") ;
		map.put("vStart (V)", vStart+"") ;
		map.put("vEnd (V)", vEnd+"") ;
		map.put("VoltageName", "BoostPWMSignal") ;
		return map;
	}

}
