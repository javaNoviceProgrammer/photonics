package photonics.wg.bend;

import flanagan.integration.DerivFunction;
import flanagan.integration.IntegralFunction;
import flanagan.integration.RungeKutta;
import flanagan.interpolation.LinearInterpolation;
import mathLib.integral.Integral1D;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

public class Optimum180DegreeBend {

	public static void main(String[] args){
		double R = 5 ; // in micron
		OptimalLossFunc lossFunc = new OptimalLossFunc() ;
		double b = 2.9123 ;
		double p = (3*b-1)/(2*b) ;

		double A = lossFunc.getValue(b)/R ;

		DerivFunction dFunction = new DerivFunction() {
			public double deriv(double x, double f) {
				return A*Math.pow(1+f*f, p);
			}
		};
		RungeKutta integration = new RungeKutta() ;
		integration.setInitialValueOfX(0);
		integration.setInitialValueOfY(0);

		integration.setStepSize(1e-5*R);

		double[] x = MathUtils.Arrays.concat(MathUtils.linspace(0, 0.98*R, 100), MathUtils.linspace(0.98*R, R, 300))  ;
		double[] f = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			integration.setFinalValueOfX(x[i]);
			f[i] = integration.fourthOrder(dFunction) ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, f);
		fig.RenderPlot();
		fig.xlabel("x (um)");
		fig.ylabel("f(x)");
		fig.run(true);

		// now calculate y
		LinearInterpolation interpolation = new LinearInterpolation(x, f) ;
		IntegralFunction func = new IntegralFunction() {
			public double function(double x) {
				return interpolation.interpolate(x);
			}
		};

		double[] y = new double[x.length] ;

		for(int i=0; i<x.length ; i++){
			Integral1D yIntegral = new Integral1D(func, 0, x[i]) ;
			y[i] = yIntegral.getIntegral() ;
		}

		double[] xx = MathUtils.Arrays.concat(MathUtils.Arrays.times(x, -1), x) ;
		double[] yy = MathUtils.Arrays.concat(y, y) ;

		MatlabChart fig1= new MatlabChart() ;
		fig1.plot(xx, yy, "r");
		fig1.RenderPlot();
		fig1.xlabel("x (um)");
		fig1.ylabel("y(x)");
		fig1.run(true);

		// find radius of curvature
		double[] radius = new double[x.length] ;
		for(int i=0; i<x.length; i++){
			radius[i] = 1/A * Math.pow(1+f[i]*f[i], 0.5/b) ;
		}

		MatlabChart fig2= new MatlabChart() ;
		fig2.plot(x, radius, "g");
		fig2.RenderPlot();
		fig2.xlabel("x (um)");
		fig2.ylabel("radius of curvature (um)");
		fig2.run(true);


	}

}
