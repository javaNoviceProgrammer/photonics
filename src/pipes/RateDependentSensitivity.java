package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

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

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("Sensitivity Offset (dB)", offsetdB+"") ;
		return map ;
	}


}
