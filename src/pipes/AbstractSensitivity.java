package pipes;

import java.util.Map;

public abstract class AbstractSensitivity {

	public abstract double getOpticalSensivitydBm(double dataRateGbps) ;
	protected abstract Map<String, String> getAllParameters();
}
