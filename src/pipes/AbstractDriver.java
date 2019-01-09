package pipes;

import java.util.Map;

public abstract class AbstractDriver {

	public abstract double getVpp() ;
	public abstract double getEnergyPJperBit(LinkFormat linkFormat, ThermalTuning thermal, AbstractModulator modulator) ;
	
	protected abstract Map<? extends String, ? extends String> getAllParameters();

}
