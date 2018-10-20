package photonics.wg.bend;

import mathLib.util.ArrayUtils.FindMinimum;
import mathLib.func.ArrayFunc;
import mathLib.util.MathUtils;
import plotter.chart.MatlabChart;

public class Optimum90BezierBend {

	public static void main(String[] args){
		double R = 5 ; // in micron
		// sweep d to find minimum loss
		double[] d = MathUtils.linspace(2.0, 10.0, 1000) ;
		double[] lossdB = new double[d.length] ;

		double a = 2096.3 ;
		double b = 2.9123 ;
		LossModel model = new LossModel(a, b, 0.0) ;

		for(int i=0; i<d.length; i++) {
			BezierCurve180 bezier = new BezierCurve180(R, d[i]) ;
			BendLossCalculate lossCalc = new BendLossCalculate(model, bezier) ;
			lossdB[i] = lossCalc.getLossDB(0.0, 1.0) ;
		}

//		System.out.println(FindMinimum.getValue(lossdB));

		int minIndex = FindMinimum.getIndex(lossdB) ;
//		System.out.println(d[minIndex]);

		double dOpt = d[minIndex] ;
		BezierCurve180 optBend = new BezierCurve180(R, dOpt) ;
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
