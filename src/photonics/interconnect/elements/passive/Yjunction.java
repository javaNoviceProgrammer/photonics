package photonics.interconnect.elements.passive;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

public class Yjunction extends AbstractElement {

	Wavelength inputLambda = null ;
	public double delta ;
	
	public Complex s11, s12, s13 ;
	public Complex s21, s22, s23 ;
	public Complex s31, s32, s33 ;

	public Yjunction(
			@ParamName(name="Element Name") String name,
			@ParamName(name="Delta") double delta
			) {
		this.name = name ;
		this.delta = delta ;
	}
	
	public void setName(String name) {
		this.name = name ;
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		this.inputLambda = inputLambda ;
	}

	@Override
	public void buildElement() {
		
		if(inputLambda == null)
			throw new NullPointerException("wavelength is not set for " + name) ;
		
		s12 = s21 = Math.sqrt((1+delta)/2.0) ;
		s13 = s31 = Math.sqrt((1-delta)/2.0) ;

		String port1_in = name+".port1.in" ;
		String port1_out = name+".port1.out" ;
		String port2_in = name+".port2.in" ;
		String port2_out = name+".port2.out" ;
		String port3_in = name+".port3.in" ;
		String port3_out = name+".port3.out" ;

		nodes = new ArrayList<>() ;
		nodes.add(port1_in) ;
		nodes.add(port1_out) ;
		nodes.add(port2_in) ;
		nodes.add(port2_out) ;
		nodes.add(port3_in) ;
		nodes.add(port3_out) ;

		sfgElement = new SFG(nodes) ;
		
		sfgElement.addArrow(port1_in, port1_out, s11);
		sfgElement.addArrow(port1_in, port2_out, s21);
		sfgElement.addArrow(port1_in, port3_out, s31);

		sfgElement.addArrow(port2_in, port1_out, s12);
		sfgElement.addArrow(port2_in, port2_out, s22);
		sfgElement.addArrow(port2_in, port3_out, s32);

		sfgElement.addArrow(port3_in, port1_out, s13);
		sfgElement.addArrow(port3_in, port2_out, s23);
		sfgElement.addArrow(port3_in, port3_out, s33);
	}


	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put(name+".delta", delta+"") ;
		return map ;
	}

}
