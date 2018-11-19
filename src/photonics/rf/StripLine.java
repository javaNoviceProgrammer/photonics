package photonics.rf;

import static java.lang.Math.log;
import static java.lang.Math.sqrt;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;

public class StripLine {
	
	double w, t, h, epsr ;
	
	public StripLine(
			@ParamName(name="Width of metal (m)") double w,
			@ParamName(name="Thickness of metal (m)") double t,
			@ParamName(name="Distance of metal to top/bottom (m)") double h,
			@ParamName(name="Relative permittivity of substrate") double epsr
			) {
		this.w = w ;
		this.t = t ;
		this.h = h ;
		this.epsr = epsr ;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<>() ;
		map.put("w", w+"") ;
		map.put("h", h+"") ;
		map.put("t", t+"") ;
		map.put("epsr", epsr+"") ;
		return map ;
	}

	public double getImpedance() {
		double a0 = 60.0/sqrt(epsr) ;
		double a1 = 1.9*(2*h+t)/(0.8*w + t) ;
		double Z0 = a0 * log(a1) ;
		return Z0 ;
	}
	
	public double getEpsEff() {
		return epsr ;
	}
	
	public double getNeff() {
		return sqrt(getEpsEff()) ;
	}

}
