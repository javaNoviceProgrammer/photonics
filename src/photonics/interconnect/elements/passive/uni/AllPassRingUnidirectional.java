package photonics.interconnect.elements.passive.uni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.func.intf.RealFunction;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;
import static mathLib.numbers.ComplexMath.*;
import static mathLib.numbers.Complex.*;

public class AllPassRingUnidirectional extends AbstractElement {

	Wavelength lambda = null ;

	double radiusMicron, kappa, t, alphaDbPerCm ;
	RealFunction neff ;
	
	public Complex s11, s12 ; 
	public Complex s21, s22 ;
	
	public AllPassRingUnidirectional(
			@ParamName(name="Element Name") String name ,
			@ParamName(name="Waveguide Mode") RealFunction neff ,
			@ParamName(name="Radius (um)") double radius ,
			@ParamName(name="Loss (dB/cm)") double alphaDbPerCm ,
			@ParamName(name="kappa") double kappa 
			) {
		this.name = name ;
		this.neff = neff ;
		this.radiusMicron = radius ;
		this.kappa = kappa ;
		this.t = Math.sqrt(1-kappa*kappa) ;
		this.alphaDbPerCm = alphaDbPerCm ;
	}
	
	@Override
	public void buildElement() {
		nodes = new ArrayList<>() ;
		String port1_in = name+".port1.in" ;
		String port2_out = name+".port2.out" ;
		nodes.add(port1_in) ;
		nodes.add(port2_out) ;

		sfgElement = new SFG(nodes) ;
		
		if(lambda == null)
			throw new NullPointerException("wavelength is not set for " + name) ;
		
		Complex a = exp(-PI*alphaDbPerCm*23*radiusMicron*1e-6) ; 
		Complex phi = 2*PI/lambda.getWavelengthMeter() * neff.evaluate(lambda.getWavelengthNm())*2*PI*radiusMicron*1e-6 ;
		s21 = (t - a*exp(-j*phi))/(1-t*a*exp(-j*phi)) ;
		s12 = s21 ;

		sfgElement.addArrow(port1_in, port2_out, s21);
		
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
