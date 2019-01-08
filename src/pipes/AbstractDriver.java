package pipes;

import java.util.Map;

public abstract class AbstractDriver {

	public abstract double getVpp() ;
	public abstract double getEnergyPJperBit() ;
	
	public abstract void setVpp(double vpp) ;
	public abstract void setEnergyPJperBit(Object... inputs) ;
	
	protected abstract Map<? extends String, ? extends String> getAllParameters();

}
