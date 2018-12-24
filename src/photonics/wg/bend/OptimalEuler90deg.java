package photonics.wg.bend;

import flanagan.integration.IntegralFunction;
import flanagan.interpolation.CubicSpline;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.func.ArrayFunc;
import mathLib.func.intf.RealFunction;
import mathLib.integral.Integral1D;
import mathLib.ode.Richardson;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils;
import mathLib.util.MathUtils;
import photonics.wg.bend.nature.LossModel;
import static java.lang.Math.* ;

public class OptimalEuler90deg {
	public static void main(String[] args) {
		LossModel lossModel = new LossModel() ;
		double R0 = 5 ;

		// step 1: find smid, beta, Rmin
		RealRootFunction funcSmid = new RealRootFunction() {

			@Override
			public double function(double smid) {
				double beta = sqrt(PI/4.0)/smid ;
				Integral1D xIntegral = new Integral1D(t -> cos(beta*beta*t*t), 0, smid) ;
				double x = xIntegral.getIntegral() ;
				Integral1D yIntegral = new Integral1D(t -> sin(beta*beta*t*t), 0, smid) ;
				double y = yIntegral.getIntegral() ;
				return (x + y - R0) ;
			}
		};

		RealRoot rootSmid = new RealRoot() ;
		double smid = rootSmid.bisect(funcSmid, 0.0, 4*R0) ;
		System.out.println("smid = " + smid);

		double beta = sqrt(PI/4.0)/smid ;

		Integral1D xIntegral = new Integral1D(t -> cos(beta*beta*t*t), 0, smid) ;
		double xmid = xIntegral.getIntegral() ;
		System.out.println("xmid = " + xmid);

		Integral1D yIntegral = new Integral1D(t -> sin(beta*beta*t*t), 0, smid) ;
		double ymid = yIntegral.getIntegral() ;
		System.out.println("ymid = " + ymid);

		//*************** find loss
		double a = lossModel.getA() ;
		double b = lossModel.getB() ;
		double lossdB = 2*a*pow(2*beta*beta, b)*pow(smid, b+1)/(b+1.0)*1e-4 ;
		System.out.println("loss (dB) = " + lossdB);

		//**************** find curve
//		double[] s = MathUtils.linspace(0.0, smid, 100) ;
//		double[] x = ArrayFunc.apply(z -> (new Integral1D(t -> cos(beta*beta*t*t), 0, z)).getIntegral(), s) ;
//		double[] y = ArrayFunc.apply(z -> (new Integral1D(t -> sin(beta*beta*t*t), 0, z)).getIntegral(), s) ;
//		double[] xtilde = ArrayFunc.apply(t -> R0-t, y) ;
//		double[] ytilde = ArrayFunc.apply(t -> R0-t, x) ;
//		double[] xx = ArrayUtils.concat(x, xtilde) ;
//		double[] yy = ArrayUtils.concat(y, ytilde) ;
//
//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(xx, yy);
//		fig.renderPlot();
//		fig.run(true);

		//***************** finding the curvature
//		CubicSpline interpolateY = new CubicSpline(xx, yy) ;
//		RealFunction yprime = t -> Richardson.deriv(z -> interpolateY.interpolate(z), t, 1e-5, 1) ;
//		RealFunction ydoubleprime = t -> Richardson.deriv2(z -> interpolateY.interpolate(z), t, 1e-5, 1) ;
//
//		RealFunction radius = t -> Math.pow(1+yprime.evaluate(t)*yprime.evaluate(t), 1.5)/Math.abs(ydoubleprime.evaluate(t)) ;
//
//		MatlabChart fig2 = new MatlabChart() ;
//		fig2.plot(xx, ArrayFunc.apply(t->1/radius.evaluate(t), xx), "r");
//		fig2.renderPlot();
//		fig2.run(true);
//
//		IntegralFunction funcLength = t -> Math.sqrt(1+yprime.evaluate(t)*yprime.evaluate(t)) ;
//		double[] x1 = MathUtils.linspace(1e-8, R0, 100) ;
//		double[] ss = ArrayFunc.apply(z -> (new Integral1D(funcLength, 1e-8, z)).getIntegral(), x1) ;
//		double[] cc = ArrayFunc.apply(z -> 1/radius.evaluate(z), x1) ;
//
//		MatlabChart fig3 = new MatlabChart() ;
//		fig3.plot(ss, cc, "k");
//		fig3.renderPlot();
//		fig3.run(true);

	}
}
