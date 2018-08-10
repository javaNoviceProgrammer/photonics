package photonics.material;

import photonics.util.Wavelength;

public class Silicon extends AbstractDielectric {

	@Override
	public double getIndex(double inputLambda) {
		double lambdaNm = inputLambda ;
		double lambdaMicron = lambdaNm/1000 ;
		double A1 = 10.6684293 ;
		double A2 = 0.0030434748 ;
		double A3 = 1.54133408 ;
		double B1 = 0.301516485 ;
		double B2 = 1.13475115 ;
		double B3 = 1104 ;
		double firstTerm = (A1 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B1*B1) ;
		double secondTerm = (A2 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B2*B2) ;
		double thirdTerm = (A3 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B3*B3) ;
		double indexSilicon = Math.sqrt(1+firstTerm+secondTerm+thirdTerm) ;
		return indexSilicon;
	}
	
	@Override
	public double getGroupIndex(double inputLambda) {
		double n = getIndex(inputLambda) ;
		double lambda = inputLambda ;
		double dLambda = 1e-2 ;
		double lambdaPlus = lambda+dLambda ;
		double nPlus = getIndex(lambdaPlus) ;
		double dn_dLambda = (nPlus-n)/(dLambda) ;
		double ng = n - lambda * dn_dLambda ;
		return ng ;
	}

	@Override
	public double getEpsilon(double inputLambda) {
		return (eps0*getIndex(inputLambda)*getIndex(inputLambda));
	}


	@Override
	public double getMu(double inputLambda) {
		return mu0;
	}
	
	@Override
	public String getMaterialName() {
		return "Si";
	}
	
	@Override
	public double getIndex(Wavelength inputLambda) {
		double lambdaNm = inputLambda.getWavelengthNm() ;
		double lambdaMicron = lambdaNm/1000 ;
		double A1 = 10.6684293 ;
		double A2 = 0.0030434748 ;
		double A3 = 1.54133408 ;
		double B1 = 0.301516485 ;
		double B2 = 1.13475115 ;
		double B3 = 1104 ;
		double firstTerm = (A1 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B1*B1) ;
		double secondTerm = (A2 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B2*B2) ;
		double thirdTerm = (A3 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B3*B3) ;
		double indexSilicon = Math.sqrt(1+firstTerm+secondTerm+thirdTerm) ;
		return indexSilicon;
	}
	
	@Override
	public double getGroupIndex(Wavelength inputLambda) {
		double n = getIndex(inputLambda) ;
		double lambda = inputLambda.getWavelengthNm() ;
		double dLambda = 1e-2 ;
		double lambdaPlus = lambda+dLambda ;
		double nPlus = getIndex(lambdaPlus) ;
		double dn_dLambda = (nPlus-n)/(dLambda) ;
		double ng = n - lambda * dn_dLambda ;
		return ng ;
	}

	@Override
	public double getEpsilon(Wavelength inputLambda) {
		return (eps0*getIndex(inputLambda)*getIndex(inputLambda));
	}


	@Override
	public double getMu(Wavelength inputLambda) {
		return mu0;
	}

}
