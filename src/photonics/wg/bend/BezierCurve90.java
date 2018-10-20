package photonics.wg.bend;

import java.util.HashMap;

public class BezierCurve90 extends AbstractCurveModel {

	double x0, x1, x2, x3 ;
	double y0, y1, y2, y3 ;
	double R, B ;

	public BezierCurve90(
			double R,
			double B
			) {
		this.R = R ;
		this.B = B ;
		x0 = 0 ;
		x1 = R*(1-B) ;
		x2 = R ;
		x3 = R ;
		y0 = 0 ;
		y1 = 0 ;
		y2 = R*B ;
		y3 = R ;
	}

	@Override
	public double getX(double t) {
		double x = (1-t)*(1-t)*(1-t)*x0 + 3*(1-t)*(1-t)*t*x1 + 3*(1-t)*t*t*x2 + t*t*t*x3 ;
		return x;
	}

	@Override
	public double getXPrime(double t) {
		double xPrime = 3*(1-t)*(1-t)*(x1-x0)+ 6*(1-t)*t*(x2-x1) + 3*t*t*(x3-x2) ;
		return xPrime;
	}

	@Override
	public double getXDoublePrime(double t) {
		double xDoublePrime = 6*(1-t)*(x2-2*x1+x0) + 6*t*(x3-2*x2+x1) ;
		return xDoublePrime;
	}

	@Override
	public double getY(double t) {
		double y = (1-t)*(1-t)*(1-t)*y0 + 3*(1-t)*(1-t)*t*y1 + 3*(1-t)*t*t*y2 + t*t*t*y3 ;
		return y;
	}

	@Override
	public double getYPrime(double t) {
		double yPrime = 3*(1-t)*(1-t)*(y1-y0)+ 6*(1-t)*t*(y2-y1) + 3*t*t*(y3-y2) ;
		return yPrime;
	}

	@Override
	public double getYDoublePrime(double t) {
		double yDoublePrime = 6*(1-t)*(y2-2*y1+y0) + 6*t*(y3-2*y2+y1) ;
		return yDoublePrime;
	}

	@Override
	public String getName() {
		return "Bezier";
	}

	@Override
	public HashMap<String, String> getAllParameters() {
		HashMap<String, String> map = new HashMap<>() ;
		map.put("R", R+"") ;
		map.put("B", B+"") ;
		return map;
	}

}
