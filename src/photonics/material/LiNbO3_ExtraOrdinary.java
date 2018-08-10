package photonics.material;

import flanagan.interpolation.CubicSpline;
import photonics.util.Wavelength;

public class LiNbO3_ExtraOrdinary extends AbstractDielectric {

	double[] lambda_nm = {1.228e3,  1.274e3,  1.32e3,   1.366e3,  1.412e3,  1.458e3,  1.504e3,  1.55e3,   1.596e3,  1.642e3,  1.688e3,  1.734e3,  1.78e3,   1.826e3,  1.872e3} ;
	double[] nExtraOrdinary = {2.148157, 2.146414, 2.144772, 2.143212, 2.141722, 2.140290, 2.138905, 2.137560, 2.136246, 2.134959, 2.133691, 2.132440, 2.131200, 2.129968, 2.128742} ;
	CubicSpline indexInterpolator = new CubicSpline(lambda_nm, nExtraOrdinary) ;
	
	@Override
	public double getIndex(double inputLambda) {
		return indexInterpolator.interpolate(inputLambda);
	}

	@Override
	public double getGroupIndex(double inputLambda) {
		double lambdaNm = inputLambda ;
		double dlambdaNm = 1 ;
		double dneff = indexInterpolator.interpolate(lambdaNm+dlambdaNm) - indexInterpolator.interpolate(lambdaNm) ;
		double ng = getIndex(inputLambda) - lambdaNm * dneff/dlambdaNm ;
		return ng;
	}

	@Override
	public double getEpsilon(double inputLambda) {
		return Math.pow(getIndex(inputLambda), 2);
	}

	@Override
	public double getMu(double inputLambda) {
		return (4*Math.PI*1E-7);
	}

	@Override
	public String getMaterialName() {
		return "LiNbO3-ExtraOrdinary";
	}

	@Override
	public double getIndex(Wavelength inputLambda) {
		return indexInterpolator.interpolate(inputLambda.getWavelengthNm());
	}

	@Override
	public double getGroupIndex(Wavelength inputLambda) {
		double lambdaNm = inputLambda.getWavelengthNm() ;
		double dlambdaNm = 1 ;
		double dneff = indexInterpolator.interpolate(lambdaNm+dlambdaNm) - indexInterpolator.interpolate(lambdaNm) ;
		double ng = getIndex(inputLambda) - lambdaNm * dneff/dlambdaNm ;
		return ng;
	}

	@Override
	public double getEpsilon(Wavelength inputLambda) {
		return Math.pow(getIndex(inputLambda), 2);
	}

	@Override
	public double getMu(Wavelength inputLambda) {
		return (4*Math.PI*1E-7);
	}

}
