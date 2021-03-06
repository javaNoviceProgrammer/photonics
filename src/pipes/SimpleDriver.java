package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SimpleDriver extends AbstractDriver {

	public double vpp, energyPJperBit ;
	
	public SimpleDriver(
			@ParamName(name="Peak to peak output voltage (V)") double vpp,
			@ParamName(name="Energy per bit (pJ/bit)") double energyPJperBit
			) {
		this.vpp = vpp ;
		this.energyPJperBit = energyPJperBit ;
	}
	
	@Override
	protected Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("driver Vpp (V)", vpp+"") ;
		return map ;
	}

	@Override
	public double getVpp() {
		return vpp;
	}

	@Override
	public double getEnergyPJperBit(LinkFormat linkFormat, ThermalTuning thermal, AbstractModulator modulator) {
		return energyPJperBit;
	}
	
}
