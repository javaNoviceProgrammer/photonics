package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class Positioning {
	
	public double wgLossdBperCm, wgLengthMicron ;
	
	public Positioning(
			@ParamName(name="Total Waveguide length (um)") double wgLengthMicron,
			@ParamName(name="Waveguide propagation loss (dB/cm)") double wgLossdBperCm
			) {
		this.wgLengthMicron = wgLengthMicron ;
		this.wgLossdBperCm = wgLossdBperCm ;
	}
	
	public double getTotalPropLossdB() {
		return wgLengthMicron*1e-4 * wgLossdBperCm ;
	}

	public Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		return map ;
	}

}
