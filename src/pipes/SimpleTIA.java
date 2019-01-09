package pipes;

import ch.epfl.general_libraries.clazzes.ParamName;

public class SimpleTIA extends AbstractTIA {
	
	double energyPerBit ;
	
	public SimpleTIA(
			@ParamName(name="Energy Consumption (pJ/bit)") double energyPerBit
			) {
		this.energyPerBit = energyPerBit ;
	}

	@Override
	public double getEnergyPerBit(double rateGbps, double opticalPowerdBm) {
		return energyPerBit;
	}

}
