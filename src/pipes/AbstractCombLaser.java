package pipes;

import java.util.Map;

public abstract class AbstractCombLaser {
	
	public abstract double getPowerPerLinedBm() ;
	public abstract double getPowerPerLinemW() ;
	public abstract double getWPE() ;
	public abstract double getSpacingGHz() ;
	public abstract double getSpacingNm() ;

	protected abstract Map<? extends String, ? extends String> getAllParameters();

}
