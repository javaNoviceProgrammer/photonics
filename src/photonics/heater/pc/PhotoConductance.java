package photonics.heater.pc;

import ch.epfl.general_libraries.clazzes.ParamName;

public class PhotoConductance {
	
	public double kp1_per_mW, kp2_per_mW_squared ;
	
	public PhotoConductance(
			@ParamName(name="Kp1 (per mW)") double kp1_per_mW,
			@ParamName(name="Kp2 (per mW^2)") double kp2_per_mW_squared
			){
		this.kp1_per_mW = kp1_per_mW ;
		this.kp2_per_mW_squared = kp2_per_mW_squared ;
	}
	

}
