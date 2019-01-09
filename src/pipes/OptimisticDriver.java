package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class OptimisticDriver extends AbstractDriver {

	public double vpp, energyPJperBit ;
	
	public OptimisticDriver(
			@ParamName(name="Peak to peak output voltage (V)") double vpp
			) {
		this.vpp = vpp ;
	}
	
	@Override
	public double getVpp() {
		return vpp;
	}

	@Override
	public double getEnergyPJperBit(LinkFormat linkFormat, ThermalTuning thermal, AbstractModulator modulator) {
		this.energyPJperBit = 2.0*(thermal.tuningPowermW/linkFormat.dataRateGbps + 1.0/4.0 * modulator.getCapfF()*1e-3*vpp*vpp) ;
		return energyPJperBit ;
	}

	@Override
	protected Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("driver Vpp (V)", vpp+"") ;
		return map ;
	}

}
