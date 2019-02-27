package photonics.wg.bend;

import flanagan.integration.RungeKutta;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.func.ArrayFunc;
import mathLib.ode.intf.DerivnFunction1D;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils;
import mathLib.util.MathUtils;

public class OptimalSbend {
	
	public static void main(String[] args) {
		double H = 20 ;
		double V = 40 ;
		
		double b = 2.49 ;
		double xi = (3.0*b-1.0)/(2.0*b) ;
		
		//******* solving for A
		
		RealRootFunction funcA = new RealRootFunction() {

			@Override
			public double function(double A) {

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

				rk.setStepSize(1e-5);
				rk.setInitialValueOfX(0);
				rk.setFinalValueOfX(H/2.0);
				rk.setInitialValueOfY(new double[] {0, 0});
				double[] yz = rk.fourthOrder(func) ;
				return yz[0] - 0.5*V ;
			}
		};

		RealRoot root = new RealRoot() ;
		double A = root.bisect(funcA, 0, 1) ;
		System.out.println("A = " + A);
		
		//************ now calculating the bend

		double[] xx = MathUtils.linspace(0, H/2.0, 1000) ;
		double[] yy = new double[xx.length] ;
		double[] yyprime = new double[xx.length] ;

		for(int i=0; i<xx.length; i++) {

			RungeKutta rk = new RungeKutta() ;
			DerivnFunction1D func = new DerivnFunction1D() {

				@Override
				public double[] derivn(double x, double[] yy) {
					// z = y'
//					double y = yy[0] ;
					double z = yy[1] ;
					double yprime = z ;
					double zprime = A * Math.pow(1+z*z, xi) ;
					return new double[] {yprime, zprime};
				}
			};

			rk.setStepSize(1e-4);
			rk.setInitialValueOfX(0);
			rk.setFinalValueOfX(xx[i]);
			rk.setInitialValueOfY(new double[] {0, 0});
			double[] yz = rk.fourthOrder(func) ;
			yy[i] = yz[0] ;
			yyprime[i] = yz[1] ;
		}

//		MatlabChart fig1 = new MatlabChart() ;
//		fig1.plot(xx, yy, "r");
//		fig1.renderPlot();
//		fig1.run(true);
//
//
//		MatlabChart fig2 = new MatlabChart() ;
//		fig2.plot(xx, yyprime, "k");
//		fig2.renderPlot();
//		fig2.run(true);
		
		double[] curvature = ArrayFunc.apply(t -> A/Math.pow(1+t*t, 1.5), yyprime) ;
		
		//************* finding the other half of y(x)
		double[] ytilde = ArrayFunc.apply(t -> V - t, yy) ;
		double[] xtilde = ArrayFunc.apply(t -> H - t, xx) ;

		double[] xtot = ArrayUtils.concat(xx, xtilde) ;
		double[] ytot = ArrayUtils.concat(yy, ytilde) ;
		
		double[] ctot = ArrayUtils.concat(curvature, curvature) ;

//		MatlabChart fig3 = new MatlabChart() ;
//		fig3.plot(xtot, ytot, "m");
//		fig3.renderPlot();
//		fig3.run(true);
//		fig3.markerON();
//		
//		MatlabChart fig4 = new MatlabChart() ;
//		fig4.plot(xtot, ctot, "g");
//		fig4.renderPlot();
//		fig4.run(true);
		
		
		Sbend bend = new Sbend(20, 40) ;
		double[] t = MathUtils.linspace(0, 1, 1000) ;
		double[] x = ArrayFunc.apply(s -> bend.getX(s), t) ;
		double[] y = ArrayFunc.apply(s -> bend.getY(s), t) ;
		
		MatlabChart fig5 = new MatlabChart() ;
		fig5.plot(x, y, "b");
		fig5.plot(xtot, ytot, "r");
		fig5.renderPlot();
		fig5.xlabel("X (um)");
		fig5.ylabel("Y (um)");
		fig5.run(true);
		
		double[] curvature1 = ArrayFunc.apply(s -> 1.0/bend.getRadiusOfCurvature(s), t) ;
		
		MatlabChart fig6 = new MatlabChart() ;
		fig6.plot(x, curvature1, "b");
		fig6.plot(xtot, ctot, "r");
		fig6.renderPlot();
		fig6.xlabel("X (um)");
		fig6.ylabel("Curvature (1/um)");
		fig6.run(true);
		
	}

}
