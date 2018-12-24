package photonics.wg.bend.nature;

import flanagan.integration.RungeKutta;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.ode.intf.DerivnFunction1D;

public class OptimalBend90deg {

	double R0, x0, A ;
	LossModel lossModel ;

	public OptimalBend90deg(
			double R0,
			LossModel lossModel
			) {
		this.R0 = R0 ;
		this.lossModel = lossModel ;
	}

	public double getX0() {
		double b = lossModel.getB();
		SpecialFunc lossFunc = new SpecialFunc() ;
		double xi = lossFunc.getXi(b) ;
		double a1 = lossFunc.getValueAtInf(b) ;
		double a2 = lossFunc.getValueAtMinusOne(b) ;

		RealRootFunction funcX0 = new RealRootFunction() {

			@Override
			public double function(double x) {
				double A = (a1-a2)/(R0 - x) ;

				RungeKutta rk = new RungeKutta() ;
				DerivnFunction1D func = new DerivnFunction1D() {

					@Override
					public double[] derivn(double x, double[] yy) {
						// z = y'
//						double y = yy[0] ;
						double z = yy[1] ;
						double yprime = z ;
						double zprime = A * Math.pow(1+z*z, xi) ;
						return new double[] {yprime, zprime};
					}
				};

				rk.setStepSize(1e-4);
				rk.setInitialValueOfX(x);
				rk.setFinalValueOfX(R0);
				rk.setInitialValueOfY(new double[] {R0-x, 1});
				double[] yz = rk.fourthOrder(func) ;
				return yz[0]-R0 ;
			}
		};

		RealRoot root = new RealRoot() ;
		this.x0 = root.bisect(funcX0, 0, R0) ;
		return x0 ;
	}

	public double getA() {
		if(x0 <= 0.0)
			x0 = getX0() ;
		double b = lossModel.getB();
		SpecialFunc lossFunc = new SpecialFunc() ;
		double a1 = lossFunc.getValueAtInf(b) ;
		double a2 = lossFunc.getValueAtMinusOne(b) ;
		this.A = (a1-a2)/(R0-x0) ;
		return A ;
	}

	public double getLossdB() {
		double a = lossModel.getA() ;
		double b = lossModel.getB() ;
		if(A<=0)
			this.A = getA() ;
		double lossdB = a*Math.pow(A, b)*2.0*(R0-x0)*1e-4 ;
		return lossdB ;
	}

	public String getName() {
		return "Optimal90deg" ;
	}


	// for test
	public static void main(String[] args) {
		OptimalBend90deg bend = new OptimalBend90deg(5, new LossModel()) ;
		System.out.println(bend.getX0());
		System.out.println(bend.getA());
		System.out.println(bend.getLossdB());
	}

}
