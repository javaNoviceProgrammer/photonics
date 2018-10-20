package photonics.wg.bend;

import java.util.HashMap;

public class EulerCurve extends AbstractCurveModel {

	double A, B ;

	public EulerCurve(double R) {
		this.A = R ;
		this.B = R ;
	}

	@Override
	public double getX(double t) {
		return A*Math.cos(t);
	}

	@Override
	public double getXPrime(double t) {
		return -A*Math.sin(t);
	}

	@Override
	public double getXDoublePrime(double t) {
		return -A*Math.cos(t);
	}

	@Override
	public double getY(double t) {
		return B*Math.sin(t);
	}

	@Override
	public double getYPrime(double t) {
		return B*Math.cos(t);
	}

	@Override
	public double getYDoublePrime(double t) {
		return -B*Math.sin(t);
	}

	@Override
	public String getName() {
		return "Circle";
	}

	@Override
	public HashMap<String, String> getAllParameters() {
		HashMap<String, String> map = new HashMap<String, String>() ;
		map.put("R", A+"") ;
		return map;
	}


}
