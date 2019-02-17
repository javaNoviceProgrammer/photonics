package photonics.interconnect.elements.passive.uni;

import static mathLib.numbers.Complex.j;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

public class CompactCouplerUnidirectional extends AbstractElement {

	Wavelength inputLambda = null ;
	public double kappa, t ;
	
	public Complex s11, s12, s13, s14 ;
	public Complex s21, s22, s23, s24 ;
	public Complex s31, s32, s33, s34 ;
	public Complex s41, s42, s43, s44 ;

	public CompactCouplerUnidirectional(
			@ParamName(name="Element Name") String name,
			@ParamName(name="kappa") double kappa
			) {
		this.name = name ;
		this.kappa = kappa ;
		this.t = Math.sqrt(1-kappa*kappa) ;
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

		String port1_in = name+".port1.in" ;
		String port2_out = name+".port2.out" ;
		String port3_out = name+".port3.out" ;
		String port4_in = name+".port4.in" ;

		nodes = new ArrayList<>() ;
		nodes.add(port1_in) ;
		nodes.add(port2_out) ;
		nodes.add(port3_out) ;
		nodes.add(port4_in) ;
		
		sfgElement = new SFG(nodes) ;
		
		s21 = s12 = s34 = s43 = t ;
		s31 = s13 = s24 = s42 = -j*kappa ;

		if(inputLambda == null)
			throw new NullPointerException("wavelength is not set for " + name) ;

		sfgElement.addArrow(port1_in, port2_out, s21);
		sfgElement.addArrow(port1_in, port3_out, s31);

		sfgElement.addArrow(port4_in, port2_out, s24);
		sfgElement.addArrow(port4_in, port3_out, s34);
	}


	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put(name+".kappa", kappa+"") ;
		map.put(name+".t", t+"") ;
		return map ;
	}

}
