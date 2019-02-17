package photonics.interconnect.elements.passive;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.func.intf.RealFunction;
import mathLib.numbers.Complex;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

public class AddDropRing extends AbstractElement {

	Wavelength lambda = null ;

	double radiusMicron, kappa1, t1, kappa2, t2, alphaDbPerCm ;
	RealFunction neff ;
	
	public Complex s11, s12, s13, s14 ; 
	public Complex s21, s22, s23, s24 ;
	public Complex s31, s32, s33, s34 ;
	public Complex s41, s42, s43, s44 ;
	
	public AddDropRing(
			@ParamName(name="Element Name") String name ,
			@ParamName(name="Radius (um)") double radius ,
			@ParamName(name="kappa 1 (input)") double kappa1,
			@ParamName(name="kappa 2 (output)") double kappa2
			) {
		this.name = name ;
		this.radiusMicron = radius ;
		this.kappa1 = kappa1 ;
		this.t1 = Math.sqrt(1-kappa1*kappa1) ;
		this.kappa2 = kappa2 ;
		this.t2 = Math.sqrt(1-kappa2*kappa2) ;
	}
	
	@Override
	public void buildElement() {
		
		
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		this.lambda = inputLambda ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<>() ;
		map.put(name+".radius (um)", radiusMicron+"") ;
		map.put(name+".kappa1", kappa1+"") ;
		map.put(name+".t1", t1+"") ;
		map.put(name+".kappa2", kappa2+"") ;
		map.put(name+".t2", t2+"") ;
		return null;
	}

}
