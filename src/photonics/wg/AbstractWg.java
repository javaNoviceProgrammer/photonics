package photonics.wg;

import photonics.util.Wavelength;

public abstract class AbstractWg {
	
	public abstract double getNeff(double lambdaNm) ;
	
	public double getNeff(Wavelength lambdaNm) {
		return getNeff(lambdaNm.getWavelengthNm()) ;
	}
	
	public abstract double getNg(double lambdaNm) ;
	
	public double getNg(Wavelength lambdaNm) {
		return getNg(lambdaNm.getWavelengthNm()) ;
	}
	
	public abstract String getModeName() ;
	
	public abstract double[] getDimensionsMicron() ;

}
