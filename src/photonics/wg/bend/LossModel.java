package photonics.wg.bend;

import static java.lang.Math.pow;

public class LossModel {

	// loss model is in dB/cm
	double a, b, c ;

	public LossModel(double a, double b, double c) {
		this.a = a ;
		this.b = b ;
		this.c = c ;
	}

	public LossModel(){
		this.a = 2096.3 ;
		this.b = 2.9123 ;
		this.c = 0 ;
	}

	public double getAlpha(double R){
		double alpha = a/pow(R, b) + c ;
		return alpha ;
	}

}
