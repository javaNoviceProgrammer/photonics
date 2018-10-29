package photonics.wg.bend;

import mathLib.func.ArrayFunc;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils.FindMinimum;
import mathLib.util.MathUtils;

public class Optimum180EulerBend {

	public static void main(String[] args){
		double R = 5 ; // in micron
		// sweep d to find minimum loss
		double[] B = MathUtils.linspace(0.1, 0.8, 1000) ;
		double[] lossdB = new double[B.length] ;

		double a = 2096.3 ;
		double b = 2.9123 ;
		LossModel model = new LossModel(a, b, 0.0) ;

		for(int i=0; i<B.length; i++) {
			BezierCurve90 bezier = new BezierCurve90(R, B[i]) ;
			BendLossCalculate lossCalc = new BendLossCalculate(model, bezier) ;
			lossdB[i] = lossCalc.getLossDB(0.0, 1.0) ;
		}

		System.out.println(FindMinimum.getValue(lossdB));

		int minIndex = FindMinimum.getIndex(lossdB) ;
		System.out.println(B[minIndex]);

		double dOpt = B[minIndex] ;
		BezierCurve90 optBend = new BezierCurve90(R, dOpt) ;
		double[] t = MathUtils.linspace(0.0, 1.0, 200) ;
		double[] x = ArrayFunc.apply(s -> optBend.getX(s) , t) ;
		double[] y = ArrayFunc.apply(s -> optBend.getY(s), t) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.RenderPlot();
		fig.markerON();
		fig.setFigLineWidth(0, 0f);
		fig.xlabel("X (um)");
		fig.ylabel("Y (um)");
		fig.run(true);
	}

}
