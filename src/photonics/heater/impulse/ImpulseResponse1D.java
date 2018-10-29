package photonics.heater.impulse;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.util.MathUtils;
import photonics.heater.struct.HeaterWgCrossSection;

public class ImpulseResponse1D extends AbstractImpulseResponse {

	double xi_sio2 = 8.7e-7 ;
	double f0_hz, f3dB_kHz ;
	HeaterWgCrossSection crossSection ;
	
	public ImpulseResponse1D(
			@ParamName(name="Cross Section") HeaterWgCrossSection crossSection
			){
		this.crossSection = crossSection ;
		double d_um = crossSection.getDistanceMicron() ;
		f0_hz = xi_sio2 /(Math.PI * d_um*1e-6*d_um*1e-6) ;
		double f3dB_hz = f0_hz * (Math.log(2) * Math.log(2)) ;
		f3dB_kHz = f3dB_hz/1e3 ;
	}
	
	public ImpulseResponse1D(
			@ParamName(name="f0 (kHz)") double f0_kHz
			){
		f0_hz = f0_kHz * 1e3 ;
		double f3dB_hz = f0_hz * (Math.log(2) * Math.log(2)) ;
		f3dB_kHz = f3dB_hz/1e3 ;
		double d_um = 1e6 * Math.sqrt((xi_sio2/Math.PI)/f0_hz) ;
		this.crossSection = new HeaterWgCrossSection(5, 0.2, d_um, 100) ;
	}
	
	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put("xi_sio2", xi_sio2+"") ;
		map.put("f3dB (kHz)", f3dB_kHz+"") ;
		map.put("model name", "1D model") ;
		map.putAll(crossSection.getAllParameters()) ;
		return map ;
	}
	
	public double getf3dBKHz(){
		return f3dB_kHz ;
	}
	
	public double getTimeResponse(double t_usec){
		if(t_usec <= 0){
			return 0 ;
		}
		else{
			double arg1 = 4*Math.PI*Math.PI*f0_hz*(t_usec*1e-6)*(t_usec*1e-6)*(t_usec*1e-6) ;
			double arg2 = -1/(4*Math.PI*f0_hz*(t_usec*1e-6)) ;
			double arg3 = Math.exp(arg2) ;
			double response = 1/Math.sqrt(arg1) * arg3 ;
			return response ;
		}

	}
	
	public double[] getTimeResponse(double[] t_usec){
		int N = t_usec.length ;
		double[] response = new double[N] ;
		for(int i=0; i<N; i++){
			response[i] = getTimeResponse(t_usec[i]) ;
		}
		return response ;
	}
	
	private double getPeakValue(){
		double tPeak_usec = 1e6 * 1/(6*Math.PI*f0_hz) ;
		return getTimeResponse(tPeak_usec) ;
	}
	
	public double getNormalizedImpulseResponse(double t_usec){	
		return getTimeResponse(t_usec)/getPeakValue() ;
	}
	
	public double[] getNormalizedImpulseResponse(double[] t_usec){
		double[] response = getTimeResponse(t_usec) ;
		return MathUtils.Arrays.times(response, 1/getPeakValue()) ;
	}
	
	public double getFreqResponse(double freqHz){
		double func = Math.exp(-Math.sqrt(freqHz/f0_hz)) ;
		return func ;
	}
	
	public double getFreqResponsedB(double freqHz){
		double val = getFreqResponse(freqHz) ;
		return 10*Math.log10(val) ;
	}

	
}
