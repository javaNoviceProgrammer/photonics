package photonics.material;

public abstract class AbstractDielectric {
	
	final double mu0 = 4*Math.PI*1e-7 ;
	final double eps0 = 1/(36*Math.PI) * 1e-9 ;
	
	public abstract double getIndex(double inputLambda) ;
	public abstract double getGroupIndex(double inputLambda) ;
	public abstract double getEpsilon(double inputLambda) ;
	public abstract double getMu(double inputLambda) ;
	public abstract String getMaterialName() ;
	

}
