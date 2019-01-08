package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SimpleModulator extends AbstractModulator {

	public double ildB, omadB, er ;
	public double effPmPerVolt, capfF ;
	
	public SimpleModulator(
			@ParamName(name="Insertion Loss (dB)") double ildB,
			@ParamName(name="OMA (dB)") double omadB,
			@ParamName(name="Modulation Efficiency (pm/V)") double effPmPerVolt,
			@ParamName(name="Modulator Capacitance (fF)") double capfF
			) {
		this.ildB = ildB ;
		this.omadB = omadB ;
		this.er = Math.pow(10.0, omadB/10.0) ;
		this.effPmPerVolt = effPmPerVolt ;
		this.capfF = capfF ;
	}
	
	@Override
	public Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
//		map.put("Modulator IL (dB)", ildB+"") ;
//		map.put("Modulator OMA (dB)", omadB+"") ;
		map.put("Modulator Efficiency (pm/V)", effPmPerVolt+"") ;
		map.put("Modulator Capacitance (fF)", capfF+"") ;
		return map ;
	}

	@Override
	public double getILdB() {
		return ildB;
	}

	@Override
	public double getOMAdB() {
		return omadB;
	}

	@Override
	public double getOMApenaltydB() {
		return 10*Math.log10((er+1.0)/(er-1.0));
	}

	@Override
	public double getOOKpenlatydB() {
		return 10*Math.log10(2.0*er/(er+1.0));
	}

	@Override
	public double getTotalPenaltydB() {
		return getILdB()+getOMApenaltydB()+getOOKpenlatydB();
	}

	@Override
	public double getCapfF() {
		return capfF;
	}

	@Override
	public double getEOeff() {
		return effPmPerVolt;
	}

}
