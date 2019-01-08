package pipes;

import java.util.Map;

public abstract class AbstractSerdes {

	public abstract double getSerdesEnergyPjPerBit(double dataRateGbps) ;
	
	protected abstract Map<? extends String, ? extends String> getAllParameters();

}
