package photonics.interconnect.elements.passive;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.func.intf.RealFunction;
import mathLib.numbers.Complex;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

public class AllPassRing extends AbstractElement {

	Wavelength lambda = null ;

	double radiusMicron, kappa, t, alphaDbPerCm ;
	RealFunction neff ;
	
	public Complex s11, s12, s13, s14 ; 
	public Complex s21, s22, s23, s24 ;
	public Complex s31, s32, s33, s34 ;
	public Complex s41, s42, s43, s44 ;
	
	public AllPassRing(
			@ParamName(name="Element Name") String name ,
			@ParamName(name="Radius (um)") double radius ,
			@ParamName(name="kappa") double kappa
			) {
		this.name = name ;
		this.radiusMicron = radius ;
		this.kappa = kappa ;
		this.t = Math.sqrt(1-kappa*kappa) ;
	}
	
	@Override
	public void buildElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		this.lambda = inputLambda ;
		
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<>() ;
		map.put(name+".radius (um)", radiusMicron+"") ;
		map.put(name+".kappa", kappa+"") ;
		map.put(name+".t", t+"") ;
		return null;
	}

}
