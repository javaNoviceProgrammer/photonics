package photonics.interconnect.elements.general;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.sfg.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;
import static mathLib.numbers.Complex.*;

public class GeneralElement extends AbstractElement {
	
	int numPorts ;
	boolean includeReflections ;
	
	public GeneralElement(
			@ParamName(name="Element Name") String name,
			@ParamName(name="Number of Ports") int numPorts,
			@ParamName(name="Include port reflections?") boolean includeReflections
			) {
		this.name = name ;
		this.numPorts = numPorts ;
		this.includeReflections = includeReflections ;
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void buildElement() {
		if(numPorts<1)
			throw new IllegalArgumentException("Number of ports should be >= 1") ;
		nodes = new ArrayList<>() ;
		for(int i=1; i<= numPorts; i++) {
			nodes.add(name+".port"+i+".in") ;
			nodes.add(name+".port"+i+".out") ;
		}
		sfgElement = new SFG(nodes) ;
		for(int i=1; i<= numPorts; i++) {
			for(int j=1; j<= numPorts; j++) {
				if(j==i && !includeReflections)
					sfgElement.addArrow(name+".port"+i+".in", name+".port"+j+".out", ZERO);
				else
					sfgElement.addArrow(name+".port"+i+".in", name+".port"+j+".out", ONE);
			}
		}
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		return map ;
	}

}
