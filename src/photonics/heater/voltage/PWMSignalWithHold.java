package photonics.heater.voltage;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class PWMSignalWithHold extends AbstractVoltage {

	double vStart, vEnd, tWidth_usec, tPeriod_usec, comFactor ;
	int numPeriods ;
	
	public PWMSignalWithHold(
			@ParamName(name="Pulse Width (usec)") double tWidth_usec,
			@ParamName(name="Pulse Period (usec)") double tPeriod_usec,
			@ParamName(name="Compression Factor of Period (0<x<1)") double comFactor,
			@ParamName(name="Number of Periods") int numPeriods,
			@ParamName(name="Start Voltage (V)") double vStart,
			@ParamName(name="End Voltage (V)") double vEnd
			){
		this.tWidth_usec = tWidth_usec ;
		this.tPeriod_usec = tPeriod_usec ;
		this.comFactor = comFactor ;
		this.numPeriods = numPeriods ;
		this.vStart = vStart ;
		this.vEnd = vEnd ;
	}
	// create the PWM signal and hold it.
	@Override
	public double getVoltage(double t_usec) {
		double V = 0 ;
		double riseEdge = 0  ;
		double fallEdge = 0 ;
		int N = numPeriods ;
		for(int i=0; i<N; i++){
			riseEdge = 0 ;
			fallEdge = 0 ;
			for(int j=0; j<i; j++){
				riseEdge += tPeriod_usec*Math.pow(comFactor, j) ; 
			}
			fallEdge = riseEdge + tWidth_usec*Math.pow(comFactor, i)  ; 
			// need to include the compression of period into the 
			PulseVoltage pulse = new PulseVoltage(riseEdge, fallEdge, vStart, vEnd) ;
			V += pulse.getVoltage(t_usec) ;
		}
		double D = tWidth_usec/tPeriod_usec ;
		PulseTrainVoltage pulseHold = new PulseTrainVoltage(D, 0.01, 1000, vStart, vEnd) ;
		double Ts = fallEdge + Math.pow(comFactor, N) *(tPeriod_usec - tWidth_usec) ;
		V += pulseHold.getVoltage(t_usec-Ts) ;
		return V ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("tWidth (usec)", tWidth_usec+"") ;
		map.put("tPeriod (usec)", tPeriod_usec+"") ;
		map.put("Compression Factor", comFactor+"") ;
		map.put("Number of Periods", numPeriods+"") ;
		map.put("vStart (V)", vStart+"") ;
		map.put("vEnd (V)", vEnd+"") ;
		map.put("VoltageName", "PWMSignalWithHold") ;
		return map;
	}

}
