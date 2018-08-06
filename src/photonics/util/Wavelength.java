package photonics.util;

import mathLib.util.PhysicalConstants;

public class Wavelength {

	double lambdaNm ;
	double freqHz ;
	final double speedOfLight = PhysicalConstants.getSpeedOfLightVacuum() ;

	public Wavelength(
			double lambdaNm
			){
		this.lambdaNm = lambdaNm ;
		this.freqHz = speedOfLight/(lambdaNm*1e-9) ;
	}

	public double getWavelengthNm(){
		return lambdaNm ;
	}
	public double getWavelengthMeter(){
		return (lambdaNm*1e-9) ;
	}

	public double getWavelengthMicron(){
		return (lambdaNm*1e-3) ;
	}

	public double getFreqHz(){
		return freqHz ;
	}

	public double getFreqGHz(){
		return (freqHz * 1e-9) ;
	}

	public double getFreqTHz(){
		return (freqHz*1e-12) ;
	}

	public double getK0(){
		return (2*Math.PI/getWavelengthMeter()) ;
	}

	public double getFreqSpacingHz(double lambdaSpacingNm){
		return speedOfLight/(getWavelengthMeter()*getWavelengthMeter()) * lambdaSpacingNm * 1e-9 ;
	}

	public double getWavelengthSpacingNm(double freqSpacingHz){
		double lambdaSpacingMeter = speedOfLight/(getFreqHz()*getFreqHz()) * freqSpacingHz ;
		return (lambdaSpacingMeter * 1e9) ;
	}

	// also implement static methods if possible...

}
