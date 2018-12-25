package photonics.wg.bend;

import photonics.wg.bend.nature.LossModel;
import static java.lang.Math.* ;

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

public class OptimalClothoid90deg {
	public static void main(String[] args) {
		LossModel lossModel = new LossModel() ;
		double R0 = 10 ;
		double r = 1.0/0.7 ;
		double theta = PI/2.0 * r/(1+2*r) ;

		//******** solve for sEnd & beta
		RealRootFunction funcSend = new RealRootFunction() {
			@Override
			public double function(double s) {
				double beta = sqrt((PI/4.0)/(1+2.0*r))/s ;
				double Rmin = s * 2.0/PI * (1+2*r) ;
				Integral1D xIntegral = new Integral1D(t -> cos(beta*beta*t*t), 0, s) ;
				double xEnd = xIntegral.getIntegral() ;
				Integral1D yIntegral = new Integral1D(t -> sin(beta*beta*t*t), 0, s) ;
				double yEnd = yIntegral.getIntegral() ;
				double equation = (xEnd + yEnd) - (R0 - sqrt(2.0)*Rmin*sin(theta)) ;
				return equation;
			}
		};

		RealRoot rootSend = new RealRoot() ;
		double sEnd = rootSend.bisect(funcSend, 1e-2, 2*R0) ;
		System.out.println("sEnd = " + sEnd);

		double beta = sqrt((PI/4.0)/(1+2.0*r))/sEnd ;
		double Rmin = sEnd * 2.0/PI * (1+2*r) ;

		//******* finding the loss
		double a = lossModel.getA() ;
		double b = lossModel.getB() ;
		double lossdB = 2*a*pow(2*beta*beta, b)*pow(sEnd, b+1)*(1.0/(b+1.0) + r) * 1e-4 ;
		System.out.println("loss (dB) = " + lossdB);

		//******* finding the bend
//		double[] s1 = MathUtils.linspace(0.0, sEnd, 100) ;
//		double[] x1 = ArrayFunc.apply(z -> (new Integral1D(t -> cos(beta*beta*t*t), 0, z)).getIntegral(), s1) ;
//		double[] y1 = ArrayFunc.apply(z -> (new Integral1D(t -> sin(beta*beta*t*t), 0, z)).getIntegral(), s1) ;
//
//		Integral1D xIntegral = new Integral1D(t -> cos(beta*beta*t*t), 0, sEnd) ;
//		double xEnd = xIntegral.getIntegral() ;
//		System.out.println("xEnd = " + xEnd);
//		Integral1D yIntegral = new Integral1D(t -> sin(beta*beta*t*t), 0, sEnd) ;
//		double yEnd = yIntegral.getIntegral() ;
//		System.out.println("yEnd = " + yEnd);
//
//		double[] angle = MathUtils.linspace(-PI/4.0-theta, -PI/4, 20) ;
//		double[] x2 = ArrayFunc.apply(t -> xEnd + Rmin*(cos(t)-cos(-PI/4.0-theta)), angle) ;
//		double[] y2 = ArrayFunc.apply(t -> yEnd + Rmin*(sin(t)-sin(-PI/4.0-theta)), angle) ;
//
//		double[] x = ArrayUtils.concat(x1, x2) ;
//		double[] y = ArrayUtils.concat(y1, y2) ;
//
//		double[] xtilde = ArrayFunc.apply(t -> R0-t, y) ;
//		double[] ytilde = ArrayFunc.apply(t -> R0-t, x) ;
//		double[] xx = ArrayUtils.concat(x, xtilde) ;
//		double[] yy = ArrayUtils.concat(y, ytilde) ;
//
//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(xx, yy);
//		fig.renderPlot();
//		fig.markerON();
//		fig.setFigLineWidth(0, 0f);
//		fig.run(true);
//
//
//		//***************** finding the curvature
//		CubicSpline interpolateY = new CubicSpline(xx, yy) ;
//		RealFunction yprime = t -> Richardson.deriv(z -> interpolateY.interpolate(z), t, 1e-5, 2) ;
//		RealFunction ydoubleprime = t -> Richardson.deriv2(z -> interpolateY.interpolate(z), t, 1e-5, 2) ;
//
//		RealFunction radius = t -> Math.pow(1+yprime.evaluate(t)*yprime.evaluate(t), 1.5)/Math.abs(ydoubleprime.evaluate(t)) ;
//
//		MatlabChart fig2 = new MatlabChart() ;
//		fig2.plot(xx, ArrayFunc.apply(t->1/radius.evaluate(t), xx), "r");
//		fig2.renderPlot();
//		fig2.run(true);
//
//		IntegralFunction funcLength = t -> Math.sqrt(1+yprime.evaluate(t)*yprime.evaluate(t)) ;
//		double[] xtot = MathUtils.linspace(1e-8, R0, 100) ;
//		double[] ss = ArrayFunc.apply(z -> (new Integral1D(funcLength, 1e-8, z)).getIntegral(), xtot) ;
//		double[] cc = ArrayFunc.apply(z -> 1/radius.evaluate(z), xtot) ;
//
//		MatlabChart fig3 = new MatlabChart() ;
//		fig3.plot(ss, cc, "k");
//		fig3.renderPlot();
//		fig3.run(true);

	}
}
