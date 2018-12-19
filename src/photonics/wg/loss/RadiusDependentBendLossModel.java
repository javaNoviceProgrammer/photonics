package photonics.wg.loss;

import ch.epfl.general_libraries.clazzes.ParamName;

public class RadiusDependentBendLossModel extends AbstractBendLossModel {

	double alphaPropdBperCm = 2 ; // in dB/cm --> This is the waveguide absorption loss
	double a, b ;

	public RadiusDependentBendLossModel() {
		a = 4.8411e7 ;
		b = 7.8016 ;
	}

	public RadiusDependentBendLossModel(
			@ParamName(name = "a") double a,
			@ParamName(name="b") double b,
			@ParamName(name="c") double c
			){
		this.a = a ;
		this.b = b ;
		this.alphaPropdBperCm = c ;
	}

	@Override
	public double getLossdBperCm(double radiusMicron) {

//		double alphaProp = 2 ; // dB/cm --> this is the constant offset part
//		double a = 4.8411e7 ;
//		double b = 7.8016 ;
		double alphaBending = a * Math.pow(1/radiusMicron, b) ; // in dB/cm based on the measurements of UBC group
		return (alphaBending + alphaPropdBperCm ) ;
		 }

	@Override
	public double getLossPerCm(double radiusMicron) {
		return (getLossdBperCm(radiusMicron) * 23/100);
	}

	@Override
	public double getLossPerMeter(double radiusMicron) {
		return (getLossdBperCm(radiusMicron) * 23);
	}

	@Override
	public double getAbsorptionLossdBperCm(double radiusMicron) {
		return alphaPropdBperCm ;
	}

	@Override
	public double getAbsorptionLossPerCm(double radiusMicron) {
		return alphaPropdBperCm * 23/100 ;
	}

	@Override
	public double getAbsorptionLossPerMeter(double radiusMicron) {
		return alphaPropdBperCm * 23;
	}

}



