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
	public double getEnergyPJperBit() {
		return 0;
	}

	@Override
	protected Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("driver Vpp (V)", vpp+"") ;
		return map ;
	}

	@Override
	public void setVpp(double vpp) {
		this.vpp = vpp ;
	}

	@Override
	public void setEnergyPJperBit(Object... inputs) {
		LinkFormat linkFormat = (LinkFormat) inputs[0] ;
		ThermalTuning thermal = (ThermalTuning) inputs[1] ;
		AbstractModulator modulator = (AbstractModulator) inputs[2] ;
		this.energyPJperBit = 2.0*(thermal.tuningPowermW/linkFormat.dataRateGbps + 1.0/4.0 * modulator.getCapfF()*1e-3*vpp*vpp) ;
	}

}
