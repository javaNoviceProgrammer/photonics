package photonics.interconnect.elements.active;

import static mathLib.numbers.Complex.j;
import static mathLib.numbers.ComplexMath.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

public class BasicPhaseShifter extends AbstractElement {
	
	double deltaPhi, excessLossdB ;
	Wavelength lambda ;
	Complex s11, s12, s21, s22 ;
	
	public BasicPhaseShifter(
			@ParamName(name="Element Name") String name,
			@ParamName(name="Phase shift (rad)") double deltaPhi,
			@ParamName(name="Excess Loss (dB)") double excessLossdB
			) {
		this.name = name ;
		this.deltaPhi = deltaPhi ;
		this.excessLossdB = excessLossdB ;
	}

	@Override
	public void buildElement() {
		
		if(lambda == null)
			throw new NullPointerException("wavelength is not set for " + name) ;
		
		double lossFactor = Math.pow(10.0, excessLossdB/10.0) ;
		s21 = exp(-j*deltaPhi)*lossFactor ;
		s12 = s21 ;
		
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

		sfgElement.addArrow(port1_in, port1_out, s11);
		sfgElement.addArrow(port2_in, port2_out, s22);

		sfgElement.addArrow(port1_in, port2_out, s21);
		sfgElement.addArrow(port2_in, port1_out, s12);
		
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		this.lambda = inputLambda ;
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new HashMap<>() ;
		map.put("delta phi (rad)", deltaPhi+"") ;
		map.put("excess loss (dB)", excessLossdB+"") ;
		return map;
	}

}
