package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class SimpleSerdes extends AbstractSerdes {

	public double rateThresholdGbps, pjOverhead ;
	
	public SimpleSerdes(
			@ParamName(name="Critical Data Rate (Gbps)") double rateThresholdGbps,
			@ParamName(name="Overhead energy (pJ/bit)") double pjOverhead 
			) {
		this.rateThresholdGbps = rateThresholdGbps ;
		this.pjOverhead = pjOverhead ;
	}
	
	@Override
	protected Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("Serdes threshold data rate (Gbps)", rateThresholdGbps+"") ;
		map.put("Serdes energy overhead (pJ/bit)", pjOverhead+"") ;
		return map ;
	}

	@Override
	public double getSerdesEnergyPjPerBit(double dataRateGbps) {
		if(dataRateGbps <= rateThresholdGbps)
			return 0.0 ;
		else
			return pjOverhead ;
	}

}
