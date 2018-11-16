package photonics.rf;

import static java.lang.Math.*;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;

public class CoplanarWgLine {
	
	double w, t, h, epsr, s ;
	
	public CoplanarWgLine(
			@ParamName(name="Width of metal (m)") double w,
			@ParamName(name="Thickness of metal (m)") double t,
			@ParamName(name="Thickness of substrate (m)") double h,
			@ParamName(name="Separation (m)") double s,
			@ParamName(name="Relative permittivity of substrate") double epsr
			) {
		this.w = w ;
		this.t = t ;
		this.h = h ;
		this.epsr = epsr ;
		this.s = s ;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<>() ;
		map.put("w", w+"") ;
		map.put("h", h+"") ;
		map.put("t", t+"") ;
		map.put("s", s+"") ;
		map.put("epsr", epsr+"") ;
		return map ;
	}
	
	private double getKKprimeRatio(double k) {
		double kprime = sqrt(1-k*k) ;
		if(0<= k && k <= 1.0/sqrt(2.0)) {
			double arg = 2.0*(1.0 + sqrt(kprime))/(1.0 - sqrt(kprime)) ;
			return PI/log(arg) ;
		}
		else if(1.0/sqrt(2.0) <= k && k <= 1.0) {
			double arg = 2.0*(1.0 + sqrt(k))/(1.0 - sqrt(k)) ;
			return log(arg)/PI ;
		}
		else {
			throw new IllegalArgumentException("k must be between 0 and 1") ;
		}
	}
	
	private double getK1() {
		if(t < 1e-7)
			return w/(w+2*s) ;
		double delta = 1.25 * t/w * (1.0 + log(4.0*PI*w/t)) ;
		double se = s - delta ;
		double we = w + delta ;
		double k1 = we/(we + 2*se) ;
		return k1 ;
	}
	
	private double getK2() {
		double a0 = PI*w/(4.0*h) ;
		double a1 = PI*(w+2*s)/(4.0*h) ;
		double k2 = sinh(a0)/sinh(a1) ;
		return k2 ;
	}
	
	@SuppressWarnings("unused")
	private double getK3() {
		double a0 = PI*w/(4.0*h) ;
		double a1 = PI*(w+2*s)/(4.0*h) ;
		double k2 = tanh(a0)/tanh(a1) ;
		return k2 ;
	}
	
	private double getFillingFactor() {
		double k1 = getK1() ;
		double k2 = getK2() ;
		double a0 = getKKprimeRatio(k2) ;
		double a1 = getKKprimeRatio(k1) ;
		double q = 0.5 * a0/a1 ;
		return q ;
	}

	public double getImpedance() {
		double a0 = 30.0*PI/sqrt(getEpsEff()) ;
		double a1 = getKKprimeRatio(getK1()) ;
		double Z0 = a0/a1 ;
		return Z0 ;
	}
	
	public double getEpsEff() {
		double q = getFillingFactor() ;
		double eps0 = 1.0 + q * (epsr - 1.0) ;
		double a1 = 0.7 * (eps0-1)*t/s ;
		double a2 = getKKprimeRatio(getK1()) + 0.7 * t/s ;
		double epsEff = eps0 - a1/a2 ;
		return epsEff ;
	}
	
	public double getNeff() {
		return sqrt(getEpsEff()) ;
	}
	
}
