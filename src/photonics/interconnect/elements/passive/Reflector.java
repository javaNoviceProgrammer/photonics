package photonics.interconnect.elements.passive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

public class Reflector extends AbstractElement {

	Wavelength lambda = null ;

	public double r, t ;
	
	public Complex s11, s12, s21, s22 ;

	public Reflector(
			@ParamName(name="Element Name") String name,
			@ParamName(name="Field Reflectance") double r
			) {
		this.name = name ;
		this.r = r ;
		this.t = Math.sqrt(1-r*r) ;
	}

	// setters and getters

	public void setName(String name) {
		this.name = name ;
	}

	public void setWavelength(Wavelength lambda) {
		this.lambda = lambda ;
	}

	@Override
	public void buildElement() {

		nodes = new ArrayList<>() ;
		String port1_in = name+".port1.in" ;
		String port1_out = name+".port1.out" ;
		String port2_in = name+".port2.in" ;
		String port2_out = name+".port2.out" ;
		nodes.add(port1_in) ;
		nodes.add(port1_out) ;
		nodes.add(port2_in) ;
		nodes.add(port2_out) ;

		sfgElement = new SFG(nodes) ;

		if(lambda == null)
			throw new NullPointerException("wavelength is not set for " + name) ;

		s21 = t ;
		s12 = s21.conjugate() ;
		s11 = r ;
		s22 = -s11 ;

		sfgElement.addArrow(port1_in, port1_out, s11);
		sfgElement.addArrow(port2_in, port2_out, s22);

		sfgElement.addArrow(port1_in, port2_out, s21);
		sfgElement.addArrow(port2_in, port1_out, s12);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<String, String>() ;
		map.put(name+".r", r+"") ;
		return map ;
	}

	
}
