package photonics.interconnect.elements.passive.uni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.func.intf.RealFunction;
import mathLib.numbers.Complex;
import mathLib.sfg.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

import static mathLib.numbers.Complex.* ;
import static mathLib.numbers.ComplexMath.*;

public class StraightWgUnidirectional extends AbstractElement {

	Wavelength lambda = null ;

	double lengthMicron, alphaDbPerCm ;
	RealFunction neff ;
	
	public Complex s11, s12, 
				   s21, s22 ;

	public StraightWgUnidirectional(
			@ParamName(name="Element Name") String name,
			RealFunction neff,
			double alphaDbPerCm,
			double lengthMicron
			) {
		this.name = name ;
		this.neff = neff ;
		this.alphaDbPerCm = alphaDbPerCm ;
		this.lengthMicron = lengthMicron ;
	}
	
	public StraightWgUnidirectional(
			@ParamName(name="Element Name") String name,
			double neff,
			double alphaDbPerCm,
			double lengthMicron
			) {
		this.name = name ;
		this.neff = s -> neff ;
		this.alphaDbPerCm = alphaDbPerCm ;
		this.lengthMicron = lengthMicron ;
	}

	// setters and getters

	public void setName(String name) {
		this.name = name ;
	}

	public void setWavelength(Wavelength lambda) {
		this.lambda = lambda ;
	}

	public void setNeff(RealFunction neff) {
		this.neff = neff ;
	}

	public void setLength(double lengthMicron) {
		this.lengthMicron = lengthMicron ;
	}

	public void setLossDbPerCm(double alphaDbPerCm) {
		this.alphaDbPerCm = alphaDbPerCm ;
	}

	public void setS11(Complex s11) {
		this.s11 = s11;
	}

	public void setS22(Complex s22) {
		this.s22 = s22;
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

		Complex beta = 2*PI/(lambda.getWavelengthNm()*1e-9) * neff.evaluate(lambda.getWavelengthNm()) - j * alphaDbPerCm*23.0/2.0 ;
		s21 = exp(-j*beta*lengthMicron*1e-6) ;
		s12 = s21 ;

		sfgElement.addArrow(port1_in, port2_out, s21);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<String, String>() ;
		map.put(name+".length (um)", lengthMicron+"") ;
		map.put(name+".neff", neff+"") ;
		map.put(name+".loss (dB/cm)", alphaDbPerCm+"") ;
		return map ;
	}


}
