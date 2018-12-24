package photonics.wg.bend;

import mathLib.func.ArrayFunc;
import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils.FindMinimum;
import mathLib.util.MathUtils;
import photonics.wg.bend.nature.LossModel;

public class Optimum90BezierBend {

	public static void main(String[] args){
		double R = 5 ; // in micron
		// sweep B to find minimum loss
		double[] B = MathUtils.linspace(0.1, 0.8, 1000) ;
		double[] lossdB = new double[B.length] ;

		LossModel model = new LossModel() ;

		for(int i=0; i<B.length; i++) {
			BezierCurve90 bezier = new BezierCurve90(R, B[i]) ;
			BendLossCalculate lossCalc = new BendLossCalculate(model, bezier) ;
			lossdB[i] = lossCalc.getLossDB(0.0, 1.0) ;
		}

		System.out.println("Min Loss (dB) = " + FindMinimum.getValue(lossdB));

		int minIndex = FindMinimum.getIndex(lossdB) ;
		System.out.println("B = " + B[minIndex]);

		double Bopt = B[minIndex] ;
		BezierCurve90 optBend = new BezierCurve90(R, Bopt) ;
		double[] t = MathUtils.linspace(0.0, 1.0, 200) ;
		double[] x = ArrayFunc.apply(s -> optBend.getX(s) , t) ;
		double[] y = ArrayFunc.apply(s -> optBend.getY(s), t) ;
		double[] curvature = ArrayFunc.apply(s -> 1.0/optBend.getRadiusOfCurvature(s), t) ;
//		double[] length = ArrayFunc.apply(s -> optBend.getLength(0, s), t) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, y);
		fig.renderPlot();
		fig.xlabel("X (um)");
		fig.ylabel("Y (um)");
		fig.run(true);

		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(x, curvature, "r");
		fig1.renderPlot();
		fig1.xlabel("S (um)");
		fig1.ylabel("Curvature (um^(-1)");
		fig1.run(true);
	}

}
