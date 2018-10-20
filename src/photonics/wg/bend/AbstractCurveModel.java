package photonics.wg.bend;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import java.util.HashMap;

import flanagan.integration.IntegralFunction;
import mathLib.integral.Integral1D;

public abstract class AbstractCurveModel {

	public abstract double getX(double t) ;
	public abstract double getXPrime(double t) ;
	public abstract double getXDoublePrime(double t) ;

	public abstract double getY(double t) ;
	public abstract double getYPrime(double t) ;
	public abstract double getYDoublePrime(double t) ;

	public abstract String getName() ;

	public abstract HashMap<String, String> getAllParameters() ;

	public double getDS(double t) {
		return Math.sqrt(getXPrime(t)*getXPrime(t)+getYPrime(t)*getYPrime(t));
	}

	public double getLength(double tStart, double tEnd) {
		IntegralFunction func = new IntegralFunction() {
			public double function(double t) {
				return getDS(t);
			}
		};
		Integral1D integral = new Integral1D(func, tStart, tEnd) ;
		return (integral.getIntegral()*1e-4); // length in cm instead of micron
	}

	public double getRadiusOfCurvature(double t){
		double num = pow(getXPrime(t)*getXPrime(t)+getYPrime(t)*getYPrime(t), 1.5) ;
		double denom = abs(getXPrime(t)*getYDoublePrime(t)-getYPrime(t)*getXDoublePrime(t)) ;
		return (num/denom) ;
	}

}
