package pipes;

import java.util.Map;

public abstract class AbstractModulator {
	
	public abstract double getILdB() ;
	public abstract double getOMAdB() ;
	public abstract double getOMApenaltydB() ;
	public abstract double getOOKpenlatydB() ;
	public abstract double getTotalPenaltydB() ;
	public abstract double getCapfF() ;
	public abstract double getEOeff() ;

	public abstract Map<? extends String, ? extends String> getAllParameters();

}
