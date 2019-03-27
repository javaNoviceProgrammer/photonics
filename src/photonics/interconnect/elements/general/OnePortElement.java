package photonics.interconnect.elements.general;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;
import static mathLib.numbers.Complex.*;

public class OnePortElement extends AbstractElement {
	
	public OnePortElement(
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

		nodes = new ArrayList<>() ;
		nodes.add(port1_in) ;
		nodes.add(port1_out) ;

		sfgElement = new SFG(nodes) ;
		
		sfgElement.addArrow(port1_in, port1_out, ONE);
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		return map ;
	}

}
