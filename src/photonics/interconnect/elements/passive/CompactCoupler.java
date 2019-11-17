package photonics.interconnect.elements.passive;

import static mathLib.numbers.Complex.j;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.numbers.Complex;
import mathLib.sfg.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

public class CompactCoupler extends AbstractElement {

	Wavelength inputLambda = null ;
	public double kappa, t ;
	
	public Complex s11, s12, s13, s14 ;
	public Complex s21, s22, s23, s24 ;
	public Complex s31, s32, s33, s34 ;
	public Complex s41, s42, s43, s44 ;

	public CompactCoupler(
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
		String port1_out = name+".port1.out" ;
		String port2_in = name+".port2.in" ;
		String port2_out = name+".port2.out" ;
		String port3_in = name+".port3.in" ;
		String port3_out = name+".port3.out" ;
		String port4_in = name+".port4.in" ;
		String port4_out = name+".port4.out" ;
		nodes = new ArrayList<>() ;
		nodes.add(port1_in) ;
		nodes.add(port1_out) ;
		nodes.add(port2_in) ;
		nodes.add(port2_out) ;
		nodes.add(port3_in) ;
		nodes.add(port3_out) ;
		nodes.add(port4_in) ;
		nodes.add(port4_out) ;
		sfgElement = new SFG(nodes) ;
		
		s21 = s12 = s34 = s43 = t ;
		s31 = s13 = s24 = s42 = -j*kappa ;
//		s11 = s22 = s33 = s44 = null ;
//		s41 = s14 = s23 = s32 = null ;
		
		if(inputLambda == null)
			throw new NullPointerException("wavelength is not set for " + name) ;

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
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put(name+".kappa", kappa+"") ;
		map.put(name+".t", t+"") ;
		return map ;
	}

}
