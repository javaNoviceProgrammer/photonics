package photonics.heater.pc;

import ch.epfl.general_libraries.clazzes.ParamName;

public class Laser {
	public double Pin_mW, lambdaLaser_nm ;
	public Laser(
			@ParamName(name="input Power (mW)") double Pin_mW,
			@ParamName(name="input wavelength (nm)") double lambdaLaser_nm
			){
		this.Pin_mW = Pin_mW ;
		this.lambdaLaser_nm = lambdaLaser_nm ;
	}
}
