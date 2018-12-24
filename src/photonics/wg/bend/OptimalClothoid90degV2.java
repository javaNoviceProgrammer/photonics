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

public class OptimalClothoid90degV2 {

	double R0 ;

	public OptimalClothoid90degV2(
			double R0
			) {
		this.R0 = R0 ;
	}

	public double getLossdB(double r) {
		LossModel lossModel = new LossModel() ;
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

		double beta = sqrt((PI/4.0)/(1+2.0*r))/sEnd ;
		double Rmin = sEnd * 2.0/PI * (1+2*r) ;

		//******* finding the loss
		double a = lossModel.getA() ;
		double b = lossModel.getB() ;
		double lossdB = 2*a*pow(2*beta*beta, b)*pow(sEnd, b+1)*(1.0/(b+1.0) + r) * 1e-4 ;
		return lossdB ;
	}

	// test
	public static void main(String[] args) {
		double R0 = 3 ;
		OptimalClothoid90degV2 bend = new OptimalClothoid90degV2(R0) ;
		double[] r = MathUtils.linspace(0, 100, 100) ;
		double[] lossdB = ArrayFunc.apply(t -> bend.getLossdB(t), r) ;
		LossModel lossModel = new LossModel() ;
		double lossCircular = lossModel.getAlpha(5)*PI*R0*1e-4/2.0 + lossModel.getAlphaM(R0)*2 ;
		double[] lossConst = ArrayFunc.apply(t -> lossCircular, r) ;
		double[] lossRatio = ArrayFunc.apply(t->t/lossCircular, lossdB) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(r, lossdB, "b");
		fig.plot(r, lossConst, "r");
		fig.renderPlot();
		fig.run(true);
	}
}
