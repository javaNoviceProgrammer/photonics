package photonics.wg.bend.nature;

import static java.lang.Math.pow;

public class LossModel {

	// loss model is in dB/cm
	double a, b, c ;
	double am, bm ;

	public LossModel(double a, double b, double c) {
		this.a = a ;
		this.b = b ;
		this.c = c ;
		this.am = 0.1315 ;
		this.bm = 2.37 ;
	}

	public LossModel(){
		this.a = 181.98 ;
		this.b = 2.49 ;
		this.c = 0 ;
		this.am = 0.1315 ;
		this.bm = 2.37 ;
	}

	public double getAlpha(double R){
		double alpha = a/pow(R, b) + c ;
		return alpha ;
	}

	public double getAlphaM(double R){
		double alphaM = am*Math.pow(R, -bm) ;
		return alphaM ;
	}

	public double getA() {
		return a ;
	}

	public double getB() {
		return b ;
	}

	public double getC() {
		return c ;
	}

	public double getAm() {
		return am ;
	}

	public double getBm() {
		return bm ;
	}

}
