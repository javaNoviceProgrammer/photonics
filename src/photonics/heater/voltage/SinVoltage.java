package photonics.heater.voltage;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SinVoltage extends AbstractVoltage {

	double tStart_usec, vAmp, freq_kHz, vDC ;
	
	public SinVoltage(
			@ParamName(name="Start time (usec)") double tStart_usec,
			@ParamName(name="Amplitude Voltage (V)") double vAmp,
			@ParamName(name="Frequency (kHz)") double freq_kHz
			){
		this.tStart_usec = tStart_usec ;
		this.vAmp = vAmp ;
		this.vDC = 0 ;
		this.freq_kHz = freq_kHz ;
	}
	
	public SinVoltage(
			@ParamName(name="Start time (usec)") double tStart_usec,
			@ParamName(name="DC Voltage Bias (V)") double vDC,
			@ParamName(name="Amplitude Voltage (V)") double vAmp,
			@ParamName(name="Frequency (kHz)") double freq_kHz
			){
		this.tStart_usec = tStart_usec ;
		this.vAmp = vAmp ;
		this.vDC = vDC ;
		this.freq_kHz = freq_kHz ;
	}
	
	@Override
	public double getVoltage(double t_usec) {
		double v = vDC + vAmp * Math.sin(2*Math.PI*freq_kHz*1e3 * t_usec*1e-6) ;
		if(t_usec < tStart_usec){return 0 ;}
		else{return v ;}
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("tSart (usec)", tStart_usec+"") ;
		map.put("vAmp (V)", vAmp+"") ;
		map.put("DC voltage (V)", vDC+"") ;
		map.put("freq (kHz)", freq_kHz+"") ;
		map.put("VoltageName", "SinVoltage") ;
		return map;
	}

}
