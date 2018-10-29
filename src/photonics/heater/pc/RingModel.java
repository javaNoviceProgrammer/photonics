package photonics.heater.pc;

import static java.lang.Math.sqrt;

import ch.epfl.general_libraries.clazzes.ParamName;

public class RingModel {
	
	public double kIn, kOut, tIn, tOut, L, L_dBperCm, FSR_nm, resLambda_nm, TOeff_nm_per_mW ;
	
	public RingModel(
			@ParamName(name="input Kappa") double kIn,
			@ParamName(name="output Kappa") double kOut,
			@ParamName(name="round-trip loss") double L,
			@ParamName(name="FSR (nm)") double FSR_nm,
			@ParamName(name="Resonance Wavelength (nm)") double resLambda_nm,
			@ParamName(name="TO efficiency (nm/mW)") double TOeff_nm_per_mW
			){
		this.kIn = kIn ;
		this.tIn = sqrt(1-kIn*kIn) ;
		this.kOut = kOut ;
		this.tOut = sqrt(1-kOut*kOut) ;
		this.L= L ;
		this.FSR_nm = FSR_nm ;
		this.resLambda_nm = resLambda_nm ;
		this.TOeff_nm_per_mW = TOeff_nm_per_mW ;
	}
	

}
