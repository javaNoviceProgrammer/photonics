package photonics.wg.bend;

import mathLib.fitting.interpol.LinearInterpolation1D;
import mathLib.func.ArrayFunc;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

public class OptimalLossFunc {

	double[] b = { 1.0000, 1.0909, 1.1818, 1.2727, 1.3636, 1.4545, 1.5455, 1.6364, 1.7273, 1.8182, 1.9091, 2.0000,
			2.0909, 2.1818, 2.2727, 2.3636, 2.4545, 2.5455, 2.6364, 2.7273, 2.8182, 2.9091, 3.0000, 3.0909, 3.1818,
			3.2727, 3.3636, 3.4545, 3.5455, 3.6364, 3.7273, 3.8182, 3.9091, 4.0000, 4.0909, 4.1818, 4.2727, 4.3636,
			4.4545, 4.5455, 4.6364, 4.7273, 4.8182, 4.9091, 5.0000, 5.0909, 5.1818, 5.2727, 5.3636, 5.4545, 5.5455,
			5.6364, 5.7273, 5.8182, 5.9091, 6.0000, 6.0909, 6.1818, 6.2727, 6.3636, 6.4545, 6.5455, 6.6364, 6.7273,
			6.8182, 6.9091, 7.0000, 7.0909, 7.1818, 7.2727, 7.3636, 7.4545, 7.5455, 7.6364, 7.7273, 7.8182, 7.9091,
			8.0000, 8.0909, 8.1818, 8.2727, 8.3636, 8.4545, 8.5455, 8.6364, 8.7273, 8.8182, 8.9091, 9.0000, 9.0909,
			9.1818, 9.2727, 9.3636, 9.4545, 9.5455, 9.6364, 9.7273, 9.8182, 9.9091, 10.0000 };
	double[] func = { 1.5698, 1.4861, 1.4240, 1.3761, 1.3380, 1.3069, 1.2812, 1.2594, 1.2407, 1.2246, 1.2105, 1.1981,
			1.1871, 1.1772, 1.1684, 1.1603, 1.1530, 1.1464, 1.1403, 1.1347, 1.1295, 1.1247, 1.1202, 1.1161, 1.1122,
			1.1086, 1.1052, 1.1020, 1.0990, 1.0962, 1.0935, 1.0910, 1.0886, 1.0864, 1.0842, 1.0821, 1.0802, 1.0783,
			1.0766, 1.0749, 1.0732, 1.0717, 1.0702, 1.0688, 1.0674, 1.0661, 1.0648, 1.0636, 1.0624, 1.0613, 1.0602,
			1.0591, 1.0581, 1.0571, 1.0562, 1.0552, 1.0544, 1.0535, 1.0527, 1.0518, 1.0511, 1.0503, 1.0496, 1.0488,
			1.0481, 1.0475, 1.0468, 1.0462, 1.0455, 1.0449, 1.0443, 1.0438, 1.0432, 1.0427, 1.0421, 1.0416, 1.0411,
			1.0406, 1.0401, 1.0397, 1.0392, 1.0387, 1.0383, 1.0379, 1.0375, 1.0370, 1.0366, 1.0363, 1.0359, 1.0355,
			1.0351, 1.0348, 1.0344, 1.0341, 1.0337, 1.0334, 1.0331, 1.0327, 1.0324, 1.0321 };

	LinearInterpolation1D interpolation = new LinearInterpolation1D(b, func);

	public double getValue(double x) {
		return interpolation.interpolate(x);
	}

	public static void main(String[] args) {
		OptimalLossFunc loss = new OptimalLossFunc() ;
		MatlabChart fig = new MatlabChart() ;
		double[] b = MathUtils.linspace(1, 10, 100) ;
		double[] val = ArrayFunc.apply(t -> loss.getValue(t), b) ;
		fig.plot(b, val);
		fig.renderPlot();
		fig.run(true);
	}
}
