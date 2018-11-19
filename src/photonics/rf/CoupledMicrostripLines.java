package photonics.rf;

import static java.lang.Math.*;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;

public class CoupledMicrostripLines {

	double w, t, s, h, epsr, freq ;
	double u, g, fn ;
	
	public CoupledMicrostripLines(
			@ParamName(name="Width of metal (m)") double w,
			@ParamName(name="Thickness of metal (m)") double t,
			@ParamName(name="Separation (m)") double s,
			@ParamName(name="Thickness of substrate (m)") double h,
			@ParamName(name="Relative permittivity of substrate") double epsr,
			@ParamName(name="RF frequency (Hz)") double freq
			) {
		this.w = w ;
		this.t = t ;
		this.s = s ;
		this.h = h ;
		this.epsr = epsr ;
		this.freq = freq ;
		calculateNormalizedParams();
	}
	
	private void calculateNormalizedParams() {
		u = w/h ;
		g = s/h ;
		fn = freq/1e9 * h/1e-3 ;
		if(u < 0.1 || u > 10 || g < 0.1 || g > 10 || epsr < 1 || epsr > 18)
			System.err.println("Parameters outside the valid range");
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<>() ;
		map.put("w", w+"") ;
		map.put("h", h+"") ;
		map.put("s", s+"") ;
		map.put("t", t+"") ;
		map.put("epsr", epsr+"") ;
		map.put("freq (hz)", freq+"") ;
		return map ;
	}
	
	public double getImpedanceEvenStatic() {
		MicrostripLine sl = new MicrostripLine(w, 1e-9, h, epsr) ;
		double eps0 = sl.getEpsEff() ;
		double zl0 = sl.getImpedance() ;
		double epse0 = getEpsEffEvenStatic() ;
		double Q1 = 0.8695*pow(u, 0.194) ;
		double Q2 = 1 + 0.7519 * g + 0.189 * pow(g, 2.31) ;
		double Q3 = 0.1975 + pow(16.6+pow(8.4/g, 6), -0.387) + 1.0/241.0 * log(pow(g, 10)/(1+pow(g/3.4, 10))) ;
		double Q4 = Q1/Q2 * 2.0/(exp(-g) * pow(u, Q3) + (2-exp(-g))*pow(u, -Q3)) ;
		double ZLe0 = sqrt(eps0/epse0) * zl0/(1 - zl0/377.0 * sqrt(eps0)*Q4) ;
		return ZLe0 ;
	}
	
//	public double getImpedanceEven() {
//
//	}
	
	public double getImpedanceOddStatic() {
		MicrostripLine sl = new MicrostripLine(w, 1e-9, h, epsr) ;
		double eps0 = sl.getEpsEff() ;
		double zl0 = sl.getImpedance() ;
		double epso0 = getEpsEffOddStatic() ;
		double Q1 = 0.8695*pow(u, 0.194) ;
		double Q2 = 1 + 0.7519 * g + 0.189 * pow(g, 2.31) ;
		double Q3 = 0.1975 + pow(16.6+pow(8.4/g, 6), -0.387) + 1.0/241.0 * log(pow(g, 10)/(1+pow(g/3.4, 10))) ;
		double Q4 = Q1/Q2 * 2.0/(exp(-g) * pow(u, Q3) + (2-exp(-g))*pow(u, -Q3)) ;
		double Q5 = 1.794 + 1.14 * log(1 + 0.638/(g+0.517*pow(g, 2.43))) ;
		double Q6 = 0.2305 + 1.0/281.3 * log(pow(g, 10)/(1+pow(g/5.8, 10))) + 1.0/5.1 * log(1 + 0.598 * pow(g, 1.154)) ;
		double Q7 = (10+190*pow(g, 2))/(1+82.3*pow(g, 3)) ;
		double Q8 = exp(-6.5 - 0.95 * log(g) - pow(g/0.15, 5.0)) ;
		double Q9 = log(Q7) * (Q8 + 1.0/16.5) ;
		double Q10 = Q4 - Q5/Q2 * pow(u, Q6*pow(u, -Q9)) ;
		double ZLo0 = sqrt(eps0/epso0) * zl0/(1 - zl0/377.0 * sqrt(eps0)*Q10) ;
		return ZLo0 ;
	}
	
//	public double getImpedanceOdd() {
//
//	}
	
	public double getEpsEffEvenStatic() {
		double v = u * (20+g*g)/(10+g*g) + g * exp(-g) ;
		double arg1 = pow(v, 4.0) + pow(v/52.0, 2.0) ;
		double ae = 1 + 1.0/49.0 * log(arg1) + 1.0/18.7 * log(1+pow(v/18.1, 3.0)) ;
		double be = 0.564 * pow((epsr-0.9)/(epsr+3), 0.053) ;
		double epse0 = 0.5*(epsr+1) + 0.5 * (epsr-1)*pow(1+10/v, -ae*be) ;
		return epse0 ;
	}
	
	public double getEpsEffEven() {
		double epse0 = getEpsEffEvenStatic() ;
		double P1 = 0.27488 + u*(0.6315 + 0.525/pow(1+0.0157*fn, 20)) - 0.065683 * exp(-8.7513*u) ;
		double P2 = 0.33622*(1-exp(-0.03442*epsr)) ;
		double P3 = 0.0363 * exp(-4.6*u)*(1-exp(-pow(fn/38.7, 4.97))) ;
		double P4 = 1 + 2.751 *(1-exp(-pow(epsr/15.916, 8))) ;
		double P5 = 0.334 * exp(-3.3*pow(epsr/15.0, 3.0)) + 0.746 ;
		double P6 = P5 * exp(-pow(fn/18.0, 0.368)) ;
		double P7 = 1 + 4.069*P6*pow(g, 0.479)*exp(-1.347*pow(g, 0.595)-0.17*pow(g, 2.5)) ;
		double Fe = P1 * P2*pow(fn*(P3*P4+0.1844*P7), 1.5763) ;
		double epsef = epsr - (epsr-epse0)/(1.0 + Fe) ;
		return epsef ;
	}
	
	public double getEpsEffOddStatic() {
		MicrostripLine sl = new MicrostripLine(w, 0, h, epsr) ;
		double eps0 = sl.getEpsEff() ;
		double ao = 0.7287 * (eps0 - 0.5 * (epsr+1)) * (1 - exp(-0.179)*u) ;
		double bo = 0.747 * epsr/(0.15 + epsr) ;
		double co = bo - (bo - 0.207) * exp(-0.414*u) ;
		double d0 = 0.593 + 0.694 * exp(-0.562 * u) ;
		double epso0 = (0.5 * (epsr+1) + ao - eps0) * exp(-co*pow(g, d0)) + eps0 ;
		return epso0 ;
	}
	
	public double getEpsEffOdd() {
		double epso0 = getEpsEffOddStatic() ;
		double P1 = 0.27488 + u*(0.6315 + 0.525/pow(1+0.0157*fn, 20)) - 0.065683 * exp(-8.7513*u) ;
		double P2 = 0.33622*(1-exp(-0.03442*epsr)) ;
		double P3 = 0.0363 * exp(-4.6*u)*(1-exp(-pow(fn/38.7, 4.97))) ;
		double P4 = 1 + 2.751 *(1-exp(-pow(epsr/15.916, 8))) ;
		double P8 = 0.7168 * (1.0 + 1.076/(1+0.0576*(epsr-1))) ;
		double P9 = P8 - 0.7913 * (1-exp(-pow(fn/20.0, 1.424))) * atan(2.481 * pow(epsr/8.0, 0.946)) ;
		double P10 = 0.242 * pow(epsr-1, 0.55) ;
		double P11 = 0.6366 * (exp(-0.3401*fn)-1) * atan(1.263*pow(u/3.0, 1.629)) ;
		double P12 = P9 + (1-P9)/(1+1.183*pow(u, 1.376)) ;
		double P13 = 1.695 * P10/(0.414 + 1.605 * P10) ;
		double P14 = 0.8928 + 0.1072 * (1 - exp(-0.42 * pow(fn/20.0, 3.215))) ;
		double P15 = abs(1 - 0.8928 *(1+P11) * exp(-P13 * pow(g, 1.092)) * P12/P14) ;
		double Fo = P1 * P2*pow(fn*(P3*P4 + 0.1844)*P15, 1.5763) ;
		double epsof = epsr - (epsr-epso0)/(1.0 + Fo) ;
		return epsof ;
	}
	
	public double getNeffEven() {
		return sqrt(getEpsEffEven()) ;
	}
	
	public double getNeffOdd() {
		return sqrt(getEpsEffOdd()) ;
	}
	
}
