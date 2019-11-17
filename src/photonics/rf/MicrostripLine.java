package photonics.rf;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName; 

public class MicrostripLine {
	
	double w, t, h, epsr ;
	
	public MicrostripLine(
			@ParamName(name="Width of metal (m)") double w,
			@ParamName(name="Thickness of metal (m)") double t,
			@ParamName(name="Thickness of substrate (m)") double h,
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
		double epsEff = getEpsEff() ;
		double q0 = pow(t/h, 2.0) + pow((1.0/PI)/(w/t+1.1), 2.0) ;
		double dW = t/PI*log(4*E/q0) ;
		double q1 = (1.0+1.0/epsEff)/2.0 ;
		double dWprime = dW*q1 ;
		double wprime = w + dWprime ;
		double a0 = 120.0*PI/2.0/sqrt(2.0)/PI/sqrt(epsr+1.0) ;
		double a1 = 4.0*h/wprime ;
		double a2 = (14+8.0/epsEff)/11.0 * 4*h/wprime ;
		double a3 = sqrt(pow(a2, 2.0) + PI*PI*q1) ;
		double Z0 = a0 * log(1.0 + a1 * (a2+a3)) ;
		return Z0 ;
	}
	
	public double getEpsEff() {
		if(w/h < 1) {
			double a0 = (epsr+1)/2.0 ;
			double a1 = (epsr-1)/2.0 ;
			double a2 = 1.0/sqrt(1+12.0*h/w) ;
			double a3 = 0.04 * pow(1.0-w/h, 2.0) ;
			return a0 + a1 * (a2 + a3) ;
		}
		else {
			double a0 = (epsr+1)/2.0 ;
			double a1 = (epsr-1)/2.0 ;
			double a2 = 1.0/sqrt(1+12.0*h/w) ;
			return a0 + a1 * a2 ;
		}
	}
	
	public double getNeff() {
		return sqrt(getEpsEff()) ;
	}
	
	
}
