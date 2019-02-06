package photonics.interconnect.elements;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import complexSFG.edu.lrl.solver.SFG;
import edu.lrl.interconnectSFG.elements.AbstractElement;
import edu.lrl.interconnectSFG.util.Wavelength;
import edu.lrl.interconnectSFG.util.WgProperties;

public class CompactCoupler extends AbstractElement {

	Wavelength inputLambda = null ;
	WgProperties wgProp = null ;
	edu.lrl.photonics.interconnect.elements.CompactCoupler coupler ;
	double k, t ;
	
	public CompactCoupler(
			@ParamName(name="Element Name") String name,
			@ParamName(name="kappa") double k
			) {
		this.name = name ;
		this.k = k ;
		this.t = Math.sqrt(1-k*k) ;
	}
	
	@Override
	public void buildElement() {
		coupler = new edu.lrl.interconnectSFG.util.CompactCoupler(k) ;
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
		
		sfgElement.addArrow(port1_in, port1_out, coupler.getS11().re(), coupler.getS11().im());
		sfgElement.addArrow(port1_in, port2_out, coupler.getS21().re(), coupler.getS21().im());
		sfgElement.addArrow(port1_in, port3_out, coupler.getS31().re(), coupler.getS31().im());
		sfgElement.addArrow(port1_in, port4_out, coupler.getS41().re(), coupler.getS41().im());
		
		sfgElement.addArrow(port2_in, port1_out, coupler.getS12().re(), coupler.getS12().im());
		sfgElement.addArrow(port2_in, port2_out, coupler.getS22().re(), coupler.getS22().im());
		sfgElement.addArrow(port2_in, port3_out, coupler.getS32().re(), coupler.getS32().im());
		sfgElement.addArrow(port2_in, port4_out, coupler.getS42().re(), coupler.getS42().im());
		
		sfgElement.addArrow(port3_in, port1_out, coupler.getS13().re(), coupler.getS13().im());
		sfgElement.addArrow(port3_in, port2_out, coupler.getS23().re(), coupler.getS23().im());
		sfgElement.addArrow(port3_in, port3_out, coupler.getS33().re(), coupler.getS33().im());
		sfgElement.addArrow(port3_in, port4_out, coupler.getS43().re(), coupler.getS43().im());
		
		sfgElement.addArrow(port4_in, port1_out, coupler.getS14().re(), coupler.getS14().im());
		sfgElement.addArrow(port4_in, port2_out, coupler.getS24().re(), coupler.getS24().im());
		sfgElement.addArrow(port4_in, port3_out, coupler.getS34().re(), coupler.getS34().im());
		sfgElement.addArrow(port4_in, port4_out, coupler.getS44().re(), coupler.getS44().im());
		
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		this.inputLambda = inputLambda ;
		
	}

	@Override
	public void setWgProperties(WgProperties wgProp) {
		this.wgProp = wgProp ;
		
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put(name+".kappa", k+"") ;
		return map ;
	}

}
