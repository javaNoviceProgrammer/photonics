package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class ReceiverElectronics {
	
	double responsivity, elecBW, jitterPenaltydB, polarizationLossdB ;
	AbstractSensitivity sensitivity ;
	AbstractTIA tia ;
	
	public ReceiverElectronics(
			@ParamName(name="Responsivity (A/W)") double responsivity,
			@ParamName(name="Electrical Bandwidth (GHz)") double elecBW,
			@ParamName(name="Jitter Penalty (dB)") double jitterPenaltydB,
			@ParamName(name="Polarization Dependent Loss (dB)") double polarizationLossdB,
			@ParamName(name="Sensitivity Model") AbstractSensitivity sensitivity,
			@ParamName(name="Trans-impedance Amplifier") AbstractTIA tia
			) {
		this.responsivity = responsivity ;
		this.elecBW = elecBW ;
		this.jitterPenaltydB = jitterPenaltydB ;
		this.polarizationLossdB = polarizationLossdB ;
		this.sensitivity = sensitivity ;
		this.tia = tia ;
	}
	
	public double getPenaltydB() {
		return jitterPenaltydB + polarizationLossdB ;
	}
	
	public double getSensitivitydBm(LinkFormat linkFormat) {
		return sensitivity.getOpticalSensivitydBm(linkFormat.dataRateGbps) ;
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("Responsivity (A/W)", responsivity+"") ;
		map.put("Electrical Bandwidth (GHz)", elecBW+"") ;
		map.put("Jitter Penalty (dB)", jitterPenaltydB+"") ;
		map.put("Polarization Dependent Loss (dB)", polarizationLossdB+"") ;
		return map ;
	}
	
}
