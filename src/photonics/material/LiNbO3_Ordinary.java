package photonics.material;

import flanagan.interpolation.CubicSpline;
import photonics.util.Wavelength;

public class LiNbO3_Ordinary extends AbstractDielectric {

	double[] lambda_nm = {1.228e3,  1.274e3,  1.32e3,   1.366e3,  1.412e3,  1.458e3,  1.504e3,  1.55e3,   1.596e3,  1.642e3,  1.688e3,  1.734e3,  1.78e3,   1.826e3,  1.872e3} ;
	double[] nOrdinary = {2.223511, 2.221478, 2.219559, 2.217736, 2.215992, 2.214314, 2.212690, 2.211111, 2.209568, 2.208054, 2.206562, 2.205088, 2.203626, 2.202173, 2.200725} ;
	CubicSpline indexInterpolator = new CubicSpline(lambda_nm, nOrdinary) ;
	
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
		return "LiNbO3-Ordinary";
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
