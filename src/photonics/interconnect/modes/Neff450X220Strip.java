package photonics.interconnect.modes;

public class Neff450X220Strip implements Neff {

	double A2 = -2.6821e-7,
		   A1 = -0.00048639,
		   A0 = 3.7488 ;

	@Override
	public double evaluate(double lambdaNm) {
		return A2*lambdaNm*lambdaNm + A1*lambdaNm + A0 ;
	}

}
