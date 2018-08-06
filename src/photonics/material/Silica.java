package photonics.material;

public class Silica extends AbstractDielectric  {

	
	//*****************************************************
	@Override
	public double getIndex(double inputLambda) {
		double lambdaNm = inputLambda ;
		double lambdaMicron = lambdaNm/1000 ;
		double A1 = 0.6961663 ;
		double A2 = 0.4079726 ;
		double A3 = 0.8974794 ;
		double B1 = 0.0684043 ;
		double B2 = 0.1162414 ;
		double B3 = 9.896161 ;
		double firstTerm = (A1 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B1*B1) ;
		double secondTerm = (A2 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B2*B2) ;
		double thirdTerm = (A3 * Math.pow(lambdaMicron, 2))/(Math.pow(lambdaMicron, 2)-B3*B3) ;
		double indexSilica = Math.sqrt(1+firstTerm+secondTerm+thirdTerm) ;
		return indexSilica;
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
	//*****************************************************
	@Override
	public String getMaterialName() {
		return "SiO2";
	}
	
	@Override
	public double getEpsilon(double inputLambda) {
		return (eps0*getIndex(inputLambda)*getIndex(inputLambda));
	}

	@Override
	public double getMu(double inputLambda) {
		return mu0;
	}
	//*****************************************************


}
