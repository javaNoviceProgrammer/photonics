package photonics.lithium;

import static java.lang.Math.PI;
import static java.lang.Math.signum;
import static java.lang.Math.sin;

import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

public class FillFunction {
	
	int n ;
	double[] a ;
	
	public FillFunction(int n) {
		this.n = n ;
		a = new double[n] ;
	}
	
	private double getIntegral(int k) {
		double x1 = (k-1)*2*PI/n ;
		double x2 = k*2*PI/n ;
		double t2 = 3.0/4.0 * sin(x2) + 1.0/12.0 * sin(3*x2) ;
		double t1 = 3.0/4.0 * sin(x1) + 1.0/12.0 * sin(3*x1) ;
		return t2-t1 ;
	}
	
	private void calculateCoeffs() {
		for(int k=0; k<n; k++)
			a[k] = -signum(getIntegral(k+1)) ;
		double[] index = MathUtils.linspace(-PI, PI, n) ;
		MatlabChart fig = new MatlabChart() ;
		fig.plot(index, a);
		fig.renderPlot();
		fig.markerON();
		fig.run(true) ;
	}
	
	public static void main(String[] args) {
		FillFunction f1 = new FillFunction(1000) ;
		f1.calculateCoeffs();
	}

}
