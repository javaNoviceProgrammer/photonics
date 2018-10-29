package photonics.wg.bend;

import mathLib.plot.MatlabChart;
import mathLib.util.ArrayUtils.FindMinimum;
import mathLib.util.MathUtils;

public class Optimum180BezierBend {

	public static void main(String[] args){
		double R = 3 ; // in micron
		// sweep d to find minimum loss
		double[] d = MathUtils.linspace(2.0, 10.0, 1000) ;
		double[] lossdB = new double[d.length] ;

		double a = 2096.3 ;
		double[] bb = MathUtils.linspace(1, 10, 100) ;
		double[] lossRatio = new double[bb.length] ;
		for(int j=0; j<bb.length; j++){
			double b = bb[j] ;
			LossModel model = new LossModel(a, b, 0.0) ;

			for(int i=0; i<d.length; i++) {
				BezierCurve180 bezier = new BezierCurve180(R, d[i]) ;
				BendLossCalculate lossCalc = new BendLossCalculate(model, bezier) ;
				lossdB[i] = lossCalc.getLossDB(0.0, 1.0) ;
			}

			int minIndex = FindMinimum.getIndex(lossdB) ;

			double dOpt = d[minIndex] ;
			BezierCurve180 optBend = new BezierCurve180(R, dOpt) ;
			BendLossCalculate lossCalcOpt = new BendLossCalculate(model, optBend) ;
			CircleCurve circ = new CircleCurve(R) ;
			BendLossCalculate lossCalcCirc = new BendLossCalculate(model, circ) ;
			lossRatio[j] = lossCalcOpt.getLossDB(0, 1)/lossCalcCirc.getLossDB(0, Math.PI);
		}


//		double[] t = MathUtils.linspace(0.0, 1.0, 200) ;
//		double[] x = ArrayFunc.apply(s -> optBend.getX(s) , t) ;
//		double[] y = ArrayFunc.apply(s -> optBend.getY(s), t) ;

		MatlabChart fig = new MatlabChart() ;
		fig.plot(bb, lossRatio);
		fig.RenderPlot();
		fig.markerON();
		fig.setFigLineWidth(0, 0f);
		fig.xlabel("b");
		fig.ylabel("Loss Ratio");
		fig.run(true);


	}

}
