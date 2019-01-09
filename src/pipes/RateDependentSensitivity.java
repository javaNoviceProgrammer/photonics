package pipes;

import ch.epfl.general_libraries.clazzes.ParamName;

public class RateDependentSensitivity extends AbstractSensitivity {
	
	double offsetdB ;
	
	public RateDependentSensitivity(
			@ParamName(name="Sensitivity Offset (dB)") double offsetdB
			) {
		this.offsetdB = offsetdB ;
	}

	@Override
	public double getOpticalSensivitydBm(double dataRateGbps) {
		return 0.8171258157*dataRateGbps-30.51177109 + offsetdB ;
	}
	

}
