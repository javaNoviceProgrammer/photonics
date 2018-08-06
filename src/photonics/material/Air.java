package photonics.material;

public class Air extends AbstractDielectric {

	@Override
	public double getIndex(double inputLambda) {
		return 1;
	}
	
	@Override
	public double getGroupIndex(double inputLambda) {
		return 1;
	}

	@Override
	public double getEpsilon(double inputLambda) {
		return (eps0*getIndex(inputLambda)*getIndex(inputLambda)) ;
	}

	@Override
	public double getMu(double inputLambda) {
		return mu0 ;
	}

	@Override
	public String getMaterialName() {
		return "Air" ;
	}

}
