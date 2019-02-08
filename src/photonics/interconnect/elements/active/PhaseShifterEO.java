package photonics.interconnect.elements.active;

import java.util.ArrayList;
import java.util.Map;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.pnjunc.PlasmaDispersionModel;
import photonics.util.Wavelength;

public class PhaseShifterEO extends AbstractElement  {

	Wavelength inputLambda = null ;
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
		sfgElement.addArrow(port1_in, port1_out, null);
		sfgElement.addArrow(port1_in, port2_out, null);
		sfgElement.addArrow(port2_in, port1_out, null);
		sfgElement.addArrow(port2_in, port2_out, null);
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		this.inputLambda = inputLambda ;
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
