package photonics.interconnect.elements.passive;

import static mathLib.numbers.Complex.j;
import static mathLib.numbers.ComplexMath.PI;
import static mathLib.numbers.ComplexMath.exp;
import static mathLib.numbers.ComplexMath.sqrt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.interconnect.modes.Neff;
import photonics.util.Wavelength;

public class AddDropRing extends AbstractElement {

	Wavelength lambda = null ;

	double radiusMicron, kappa1, t1, kappa2, t2, alphaDbPerCm ;
	Neff neff ;
	
	public Complex s11, s12, s13, s14 ; 
	public Complex s21, s22, s23, s24 ;
	public Complex s31, s32, s33, s34 ;
	public Complex s41, s42, s43, s44 ;
	
	public AddDropRing(
			@ParamName(name="Element Name") String name ,
			@ParamName(name="Waveguide Mode") Neff neff ,
			@ParamName(name="Radius (um)") double radius ,
			@ParamName(name="Loss (dB/cm)") double alphaDbPerCm ,
			@ParamName(name="kappa 1 (input)") double kappa1,
			@ParamName(name="kappa 2 (output)") double kappa2
			) {
		this.name = name ;
		this.neff = neff ;
		this.radiusMicron = radius ;
		this.kappa1 = kappa1 ;
		this.t1 = Math.sqrt(1-kappa1*kappa1) ;
		this.kappa2 = kappa2 ;
		this.t2 = Math.sqrt(1-kappa2*kappa2) ;
		this.alphaDbPerCm = alphaDbPerCm ;
	}
	
	@Override
	public void buildElement() {
		
		if(lambda == null)
			throw new NullPointerException("wavelength is not set for " + name) ;
		
		Complex a = exp(-PI*alphaDbPerCm*23*radiusMicron*1e-6) ; 
		Complex phi = 2*PI/lambda.getWavelengthMeter() * neff.evaluate(lambda.getWavelengthNm())*2*PI*radiusMicron*1e-6 ;
		s21 = (t1 - a*t2*exp(-j*phi))/(1-t1*a*t2*exp(-j*phi)) ;
		s12 = s34 = s43 = s21 ;
		s41 = (-kappa1*kappa2*sqrt(a)*exp(-j*phi/2.0))/(1-t1*a*t2*exp(-j*phi)) ;
		s14 = s23 = s34 = s41 ;
		
		nodes = new ArrayList<>() ;
		String port1_in = name+".port1.in" ;
		String port1_out = name+".port1.out" ;
		String port2_in = name+".port2.in" ;
		String port2_out = name+".port2.out" ;
		String port3_in = name+".port3.in" ;
		String port3_out = name+".port3.out" ;
		String port4_in = name+".port4.in" ;
		String port4_out = name+".port4.out" ;
		nodes.add(port1_in) ;
		nodes.add(port1_out) ;
		nodes.add(port2_in) ;
		nodes.add(port2_out) ;
		nodes.add(port3_in) ;
		nodes.add(port3_out) ;
		nodes.add(port4_in) ;
		nodes.add(port4_out) ;

		sfgElement = new SFG(nodes) ;
		
		sfgElement.addArrow(port1_in, port1_out, s11);
		sfgElement.addArrow(port1_in, port2_out, s21);
		sfgElement.addArrow(port1_in, port3_out, s31);
		sfgElement.addArrow(port1_in, port4_out, s41);

		sfgElement.addArrow(port2_in, port1_out, s12);
		sfgElement.addArrow(port2_in, port2_out, s22);
		sfgElement.addArrow(port2_in, port3_out, s32);
		sfgElement.addArrow(port2_in, port4_out, s42);

		sfgElement.addArrow(port3_in, port1_out, s13);
		sfgElement.addArrow(port3_in, port2_out, s23);
		sfgElement.addArrow(port3_in, port3_out, s33);
		sfgElement.addArrow(port3_in, port4_out, s43);

		sfgElement.addArrow(port4_in, port1_out, s14);
		sfgElement.addArrow(port4_in, port2_out, s24);
		sfgElement.addArrow(port4_in, port3_out, s34);
		sfgElement.addArrow(port4_in, port4_out, s44);
		
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
