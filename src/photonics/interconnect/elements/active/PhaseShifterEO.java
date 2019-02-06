package photonics.interconnect.elements.active;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import complexSFG.edu.lrl.solver.SFG;
import edu.lrl.interconnectSFG.elements.AbstractElement;
import edu.lrl.interconnectSFG.util.PlasmaDispersionModel;
import edu.lrl.interconnectSFG.util.Wavelength;
import edu.lrl.interconnectSFG.util.WgProperties;
import photonics.interconnect.elements.edu;

public class PhaseShifterEO  {

	Wavelength inputLambda = null ;
	WgProperties wgProp = null ;
	edu.lrl.photonics.interconnect.elements.passive.StraightWg wg ;
	PlasmaDispersionModel plasmaEffect ;
	double lengthMicron ;

	public PhaseShifterEO(
			@ParamName(name="Element Name") String name,
			@ParamName(name="length (um)") double lengthMicron,
			@ParamName(name="Plasma Dispersion Effect") PlasmaDispersionModel plasmaEffect
			) {
		this.name = name ;
		this.lengthMicron = lengthMicron ;
		this.plasmaEffect = plasmaEffect ;
	}

	@Override
	public void buildElement() {
		wg = new edu.lrl.interconnectSFG.util.StraightWg(inputLambda, wgProp, lengthMicron, true, plasmaEffect, false, null) ;
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
		map.put(name+".length (um)", lengthMicron+"") ;
		map.put(name+".Dalpha (1/cm)", plasmaEffect.getDalphaPerCm()+"") ;
		map.put(name+".DnSi ()", plasmaEffect.getDnSi()+"") ;
		map.put(name+".DN (1/cm^3)", plasmaEffect.getDN()+"") ;
		return map ;
	}

}
