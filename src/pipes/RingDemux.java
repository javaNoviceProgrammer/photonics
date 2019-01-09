package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class RingDemux extends AbstractDemux {

	double ildB, bandwidthGHz ;
	
	public RingDemux(
			@ParamName(name="Insertion Loss (dB)") double ildB,
			@ParamName(name="Demux Bandwidth (GHz)") double bandwidthGHz
			) {
		this.ildB = ildB ;
		this.bandwidthGHz = bandwidthGHz ;
	}
	
	@Override
	public double getILdB() {
		return ildB ;
	}
	
	@Override
	public double getTruncationPenaltydB(double rateGbps) {
		double bwFactor = bandwidthGHz/(2.0*rateGbps) ;
		double trancFactor = 1- (1-Math.exp(-2*Math.PI*bwFactor))/(2*Math.PI*bwFactor) ;
		return -10*Math.log10(trancFactor);
	}

	@Override
	public double getXtalkPenaltydB(double channelSpacingGHz, double omadB) {
		double factor = 2.0*channelSpacingGHz/bandwidthGHz ;
		double xtalk1 = 2.0 * 1.0/(1+Math.pow(factor, 2.0)) ;
		double xtalk2 = 2.0 * 1.0/(1+Math.pow(2.0*factor, 2.0)) ;
		double xtalk = xtalk1 + xtalk2 ;
		double er = Math.pow(10.0, omadB/10.0) ;
		double qBER = 7.0 ;
		double penalty = -10.0 * Math.log10(1 - 0.5 * xtalk * qBER * (er+1)/(er-1)) ;
		return penalty ;
	}

	@Override
	public double getPenaltydB(double rateGbps, double channelSpacingGHz, double omadB) {
		return getILdB() + getTruncationPenaltydB(rateGbps) +  getXtalkPenaltydB(channelSpacingGHz, omadB) ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("Demux BW (GHz)", bandwidthGHz+"") ;
		return map ;
	}





}
