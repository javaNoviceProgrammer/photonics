package photonics.interconnect;

import java.util.ArrayList;
import java.util.Map;

public class StraightWg extends AbstractElement {

	Wavelength inputLambda = null ;
	WgProperties wgProp = null ;
	edu.lrl.interconnectSFG.util.StraightWg wg ;
	double lengthMicron ;
	
	public StraightWg(
			String name,
			double lengthMicron
			) {
		this.name = name ;
		this.lengthMicron = lengthMicron ;
	}
	
	public void setWavelength(Wavelength inputLambda){
		this.inputLambda = inputLambda ;
	}
	
	public void setWgProperties(WgProperties wgProp){
		this.wgProp = wgProp ;
	}

	@Override
	public void buildElement() {
		wg = new edu.lrl.interconnectSFG.util.StraightWg(inputLambda, wgProp, lengthMicron, false, null, false, null) ;
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
		sfgElement.addArrow(port1_in, port1_out, wg.S11.re(), wg.S11.im());
		sfgElement.addArrow(port1_in, port2_out, wg.S21.re(), wg.S21.im());
		sfgElement.addArrow(port2_in, port1_out, wg.S12.re(), wg.S12.im());
		sfgElement.addArrow(port2_in, port2_out, wg.S22.re(), wg.S22.im());
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put(name+".length (um)", lengthMicron+"") ;
		return map ;
	}

}
