package photonics.interconnect.elements.active;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

public class BasicPhaseShifter extends AbstractElement {
	
	double deltaPhi, excessLossdB ;
	Wavelength lambda ;
	Complex s11, s12, s21, s22 ;
	
	public BasicPhaseShifter(
			@ParamName(name="Phase shift (rad)") double deltaPhi,
			@ParamName(name="Excess Loss (dB)") double excessLossdB
			) {
		this.deltaPhi = deltaPhi ;
		this.excessLossdB = excessLossdB ;
	}

	@Override
	public void buildElement() {
		// TODO Auto-generated method stub
		
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
