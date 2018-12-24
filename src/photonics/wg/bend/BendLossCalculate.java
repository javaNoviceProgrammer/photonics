package photonics.wg.bend;

import flanagan.integration.IntegralFunction;
import mathLib.integral.Integral1D;
import photonics.wg.bend.nature.LossModel;

public class BendLossCalculate {

	LossModel lossModel ;
	AbstractCurveModel curveModel ;

	public BendLossCalculate(
			LossModel lossModel,
			AbstractCurveModel curveModel
			){
		this.lossModel = lossModel ;
		this.curveModel = curveModel ;
	}

	public double getLossDB(double tStart, double tEnd){
		IntegralFunction func = new IntegralFunction() {
			public double function(double t) {
				return lossModel.getAlpha(curveModel.getRadiusOfCurvature(t))*curveModel.getDS(t)*1e-4;
			}
		};

		Integral1D integral = new Integral1D(func, tStart, tEnd) ;
		double modeMismatchLossdB = lossModel.getAlphaM(curveModel.getRadiusOfCurvature(tStart)) +
									lossModel.getAlphaM(curveModel.getRadiusOfCurvature(tEnd)) ;
		return integral.getIntegral() + modeMismatchLossdB  ;
	}

	public double getLossDBperCm(double tStart, double tEnd){
		return (getLossDB(tStart, tEnd)/curveModel.getLength(tStart, tEnd)) ;
	}

	public LossModel getLossModel() {
		return lossModel;
	}

	public AbstractCurveModel getCurveModel() {
		return curveModel;
	}

}
