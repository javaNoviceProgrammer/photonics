package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class ModeMux {
	
	public double muxPenaltydB ;
	
	public ModeMux(
			@ParamName(name="Mode Mux penalty (dB)") double muxPenaltydB
			) {
		this.muxPenaltydB = muxPenaltydB ;
	}

	public Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("Mode Mux Penalty (dB)", muxPenaltydB+"") ;
		return map ;
	}

}
