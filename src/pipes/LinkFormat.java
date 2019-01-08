package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

public class LinkFormat {

	public int numberOfWavelengths, numberOfModes ;
	public double dataRateGbps ;
	
	public LinkFormat(
			@ParamName(name="Number of Wavelengths") int numberOfWavelengths,
			@ParamName(name="Number of Modes") int numberOfModes,
			@ParamName(name="Data Rate per Channel (Gbps)") double dataRateGbps
			) {
		this.numberOfWavelengths = numberOfWavelengths ;
		this.numberOfModes = numberOfModes ;
		this.dataRateGbps = dataRateGbps ;
	}
	
	public double getTotalAggregationTbps() {
		return numberOfWavelengths*numberOfModes*dataRateGbps/1e3 ;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.put("Number of Wavelengths", numberOfWavelengths+"") ;
		map.put("Number of Modes", numberOfModes+"") ;
		map.put("Data Rate per Channel (Gbps)", dataRateGbps+"") ;
		return map ;
	}
	
	
}
