package pipes;

import java.util.Map;

public abstract class AbstractDriver {

	public abstract double getVpp() ;
	public abstract double getEnergyPJperBit() ;
	
	protected abstract Map<? extends String, ? extends String> getAllParameters();

}
