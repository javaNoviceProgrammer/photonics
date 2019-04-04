package photonics.wg.bend;

import static java.lang.Math.sqrt;
import static mathLib.func.GammaFunc.gamma;

import flanagan.interpolation.CubicSpline;
import mathLib.func.ArrayFunc;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

/**
 * This function calculates the values of h(f;xi) = f*HG2F1(0.5,xi;1.5,-f^2)
 * at f=1 and f=inf for values of b.
 *
 * @author meisam
 *
 */

public class SpecialFunc {

	double[] bVal = { 1.0000, 1.4737, 1.9474, 2.4211, 2.8947, 3.3684, 3.8421, 4.3158, 4.7895, 5.2632, 5.7368, 6.2105,
			6.6842, 7.1579, 7.6316, 8.1053, 8.5789, 9.0526, 9.5263, 10.0000 };

	double[] funcVal = { 0.7854, 0.7585, 0.7454, 0.7376, 0.7324, 0.7287, 0.7260, 0.7239, 0.7222, 0.7208, 0.7197, 0.7187,
			0.7179, 0.7171, 0.7165, 0.7159, 0.7155, 0.7150, 0.7146, 0.7143 };

	CubicSpline interpolate = null ;

	public double getXi(double b) {
		double xi = (3.0 * b - 1.0) / (2.0 * b);
		return xi;
	}

	public double getValueAtInf(double b) {
		double xi = getXi(b);
		double infVal = sqrt(Math.PI) / 2.0 * gamma(xi - 0.5) / gamma(xi);
		return infVal;
	}

	public double getValueAtMinusOne(double b) {
		if(interpolate == null)
			interpolate = new CubicSpline(bVal, funcVal) ;
		return interpolate.interpolate(b) ;
	}

	public static void main(String[] args) {
		SpecialFunc func = new SpecialFunc() ;
		MatlabChart fig = new MatlabChart() ;
		double[] b = MathUtils.linspace(1, 10, 1000) ;
		double[] vals = ArrayFunc.apply(t -> func.getValueAtMinusOne(t), b) ;
		fig.plot(b, vals);
		fig.renderPlot();
		fig.run(true);
	}

}
