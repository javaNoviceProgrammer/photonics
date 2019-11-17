package photonics.interconnect.elements.general;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.sfg.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;
import static mathLib.numbers.Complex.*;

public class FourPortElement extends AbstractElement {
	
	public FourPortElement(
			@ParamName(name="Element Name") String name
			) {
		this.name = name ;
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		// TODO Auto-generated method stub
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
		
		sfgElement.addArrow(port1_in, port1_out, ONE);
		sfgElement.addArrow(port1_in, port2_out, ONE);
		sfgElement.addArrow(port1_in, port3_out, ONE);
		sfgElement.addArrow(port1_in, port4_out, ONE);

		sfgElement.addArrow(port2_in, port1_out, ONE);
		sfgElement.addArrow(port2_in, port2_out, ONE);
		sfgElement.addArrow(port2_in, port3_out, ONE);
		sfgElement.addArrow(port2_in, port4_out, ONE);

		sfgElement.addArrow(port3_in, port1_out, ONE);
		sfgElement.addArrow(port3_in, port2_out, ONE);
		sfgElement.addArrow(port3_in, port3_out, ONE);
		sfgElement.addArrow(port3_in, port4_out, ONE);

		sfgElement.addArrow(port4_in, port1_out, ONE);
		sfgElement.addArrow(port4_in, port2_out, ONE);
		sfgElement.addArrow(port4_in, port3_out, ONE);
		sfgElement.addArrow(port4_in, port4_out, ONE);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		return map ;
	}

}
