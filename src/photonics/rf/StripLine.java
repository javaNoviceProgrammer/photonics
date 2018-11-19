package photonics.rf;

import static java.lang.Math.*;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;

public class StripLine {
	
	double w, t, h, epsr ;
	
	public enum Method {
		Simple,
		Complex,
		Combined
	}
	
	Method method ;
	
	public StripLine(
			@ParamName(name="Width of metal (m)") double w,
			@ParamName(name="Thickness of metal (m)") double t,
			@ParamName(name="Distance of metal to top/bottom (m)") double h,
			@ParamName(name="Relative permittivity of substrate") double epsr,
			@ParamName(name="Method") Method method
			) {
		this.w = w ;
		this.t = t ;
		this.h = h ;
		this.epsr = epsr ;
		this.method = method ;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<>() ;
		map.put("w", w+"") ;
		map.put("h", h+"") ;
		map.put("t", t+"") ;
		map.put("epsr", epsr+"") ;
		map.put("Method", method.name()) ;
		return map ;
	}

	public double getImpedance() {
		double Z0 = 0.0 ;
		switch (method) {
		case Simple:
			double a0 = 60.0/sqrt(epsr) ;
			double a1 = 1.9*(2*h+t)/(0.8*w + t) ;
			Z0 = a0 * log(a1) ;
			break;
		case Complex:
			double b0 = 94.15/sqrt(epsr) ;
			double d = 2*h + t ;
			double K = 1.0/(1.0 - t/d) ;
			double b1 = w/d * K ;
			double b2 = (2*K*log(K+1)-(K-1)*log(K*K-1))/PI ;
			Z0 = b0/(b1 + b2) ;
			break ;
		case Combined:
			StripLine sl1 = new StripLine(w, t, h, epsr, Method.Simple) ;
			StripLine sl2 = new StripLine(w, t, h, epsr, Method.Complex) ;
			Z0 = max(sl1.getImpedance(), sl2.getImpedance()) ;
		default:
			break;
		}
		return Z0 ;
	}
	
	public double getEpsEff() {
		return epsr ;
	}
	
	public double getNeff() {
		return sqrt(getEpsEff()) ;
	}

}
