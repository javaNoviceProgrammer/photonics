package photonics.wg.loss;

import ch.epfl.general_libraries.clazzes.ParamName;

public class ConstantBendLossModel extends AbstractBendLossModel {

	double alphadBperCm ;
	
	public ConstantBendLossModel (
			@ParamName(name="Constant Propagation Loss (dB/cm)", default_="1") double alphadBperCm
			){
		this.alphadBperCm = alphadBperCm ;
	}
	
	@Override
	public double getLossdBperCm(double radiusMicron) {
		return alphadBperCm ;
	}

	@Override
	public double getLossPerCm(double radiusMicron) {
		return (alphadBperCm * 23/100);
	}

	@Override
	public double getLossPerMeter(double radiusMicron) {
		return (alphadBperCm * 23);
	}

	@Override
	public double getAbsorptionLossdBperCm(double radiusMicron) {
		return getLossdBperCm(radiusMicron);
	}

	@Override
	public double getAbsorptionLossPerCm(double radiusMicron) {
		return getLossPerCm(radiusMicron);
	}

	@Override
	public double getAbsorptionLossPerMeter(double radiusMicron) {
		return getLossPerMeter(radiusMicron);
	}

}
