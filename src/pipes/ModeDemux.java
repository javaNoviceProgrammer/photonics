package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class ModeDemux {
	
	public double demuxILdB, demuxXtalkdB ;
	
	public ModeDemux(
			@ParamName(name="Mode Demux Loss (dB)") double demuxILdB,
			@ParamName(name="Mode Demux Crosstalk (dB)") double demuxXtalkdB
			) {
		this.demuxILdB = demuxILdB ;
		this.demuxXtalkdB = demuxXtalkdB ;
	}
	
	public double getPenaltydB() {
		double xtalk = Math.pow(10.0, demuxXtalkdB/10.0) ;
		double qBER = 7.0 ;
		double penalty = demuxILdB + (-10.0 * Math.log10(1 - 0.5 * xtalk * qBER)) ;
		return penalty ;
	}
	
	public double getPenaltydB(double omadB) {
		double xtalk = Math.pow(10.0, demuxXtalkdB/10.0) ;
		double qBER = 7.0 ;
		double er = Math.pow(10.0, omadB/10.0) ;
		double penalty = demuxILdB + (-10.0 * Math.log10(1 - 0.5 * xtalk * qBER * (er+1)/(er-1))) ;
		return penalty ;
	}

	public Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("Mode Demux Loss (dB)", demuxILdB+"") ;
		map.put("Mode Demux Crosstalk (dB)", demuxXtalkdB+"") ;
		return map ;
	}
}
