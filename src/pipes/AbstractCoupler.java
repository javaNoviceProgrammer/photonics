package pipes;

import java.util.Map;

public abstract class AbstractCoupler {

	public abstract double getTotalLossdB() ;
	protected abstract Map<? extends String, ? extends String> getAllParameters();

}
