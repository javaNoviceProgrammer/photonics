package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SimpleCoupler extends AbstractCoupler {

	public int numberOfCouplers ;
	public double lossPerCouplerdB ;
	
	public SimpleCoupler(
			@ParamName(name="Number of couplers") int numberOfCouplers,
			@ParamName(name="Loss per Coupler (dB)") double lossPerCouplerdB
			) {
		this.numberOfCouplers = numberOfCouplers ;
		this.lossPerCouplerdB = lossPerCouplerdB ;
	}
	
	public double getTotalLossdB() {
		return numberOfCouplers*lossPerCouplerdB ;
	}
	
	@Override
	protected Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		return map ;
	}

}
