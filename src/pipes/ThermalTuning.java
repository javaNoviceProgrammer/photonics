package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class ThermalTuning {
	
	public double tuningPowermW ;
	
	public ThermalTuning(
			@ParamName(name="Tuning power (mW)") double tuningPowermW
			) {
		this.tuningPowermW = tuningPowermW ;
	}
	
	public double getThermalTuningPowermW() {
		return tuningPowermW ;
	}

	public Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		return map ;
	}

}
