package photonics.heater.impulse;

import java.util.Map;

public abstract class AbstractImpulseResponse {

	public abstract double getTimeResponse(double t_usec) ;
	public abstract double getNormalizedImpulseResponse(double t_usec) ;
	public abstract double[] getTimeResponse(double[] t_usec) ;
	public abstract double[] getNormalizedImpulseResponse(double[] t_usec) ;
	public abstract double getFreqResponse(double freqHz) ;
	public abstract double getFreqResponsedB(double freqHz) ;

	public abstract double getf3dBKHz() ;
	public abstract Map<String, String> getAllParameters() ;
}
