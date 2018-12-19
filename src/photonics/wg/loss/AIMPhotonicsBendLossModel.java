package photonics.wg.loss;

public class AIMPhotonicsBendLossModel extends AbstractBendLossModel {

	@Override
	public double getLossdBperCm(double radiusMicron) {
		double a = 2096.3 ;
		double b = 2.9123 ;
		double c = 0 ;
		double alphaBending = a * Math.pow(1/radiusMicron, b) + c ; 
		return alphaBending ;
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
		return getLossdBperCm(12) ;
	}

	@Override
	public double getAbsorptionLossPerCm(double radiusMicron) {
		return getAbsorptionLossdBperCm(12) * 23/100 ;
	}

	@Override
	public double getAbsorptionLossPerMeter(double radiusMicron) {
		return getAbsorptionLossdBperCm(12) * 23;
	}

}
