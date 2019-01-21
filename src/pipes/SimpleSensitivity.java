package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SimpleSensitivity extends AbstractSensitivity {

	double sensitivitydBm ;

	public SimpleSensitivity(
			@ParamName(name="Sensitivity (dBm)") double sensitivitydBm
			) {
		this.sensitivitydBm = sensitivitydBm ;
	}

	@Override
	public double getOpticalSensivitydBm(double dataRateGbps) {
		return sensitivitydBm ;
	}

	@Override
	protected Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		return map ;
	}

}
