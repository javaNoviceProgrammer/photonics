package pipes;

import java.util.Map;

public abstract class AbstractDemux {

	public abstract double getILdB() ;
	public abstract double getTruncationPenaltydB(double rateGbps) ;
	public abstract double getXtalkPenaltydB(double channelSpacingGHz, double omadB) ;
	public abstract double getPenaltydB(double rateGbps, double channelSpacingGHz, double omadB) ;
	
	public abstract Map<String, String> getAllParameters() ;
	
}
