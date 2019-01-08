package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.utils.SimpleMap;

public class Transmitter implements Experiment {
	
	//link format
	LinkFormat linkFormat ;
	// comb laser
	AbstractCombLaser combLaser ;
	// coupling
	AbstractCoupler coupler ;
	// modulator
	AbstractModulator modulator ;
	// thermal tuning
	ThermalTuning thermal ;
	// positioning
	Positioning positioning ;
	// mode multiplexing
	ModeMux modeMux ;
	// driver
	AbstractDriver driver ;
	// serdes
	AbstractSerdes serdes ;
	
	public Transmitter(
			@ParamName(name="Link Format") LinkFormat linkFormat,
			@ParamName(name="Comb Laser") AbstractCombLaser combLaser,
			@ParamName(name="Coupling in/out") AbstractCoupler coupler,
			@ParamName(name="Modulator") AbstractModulator modulator,
			@ParamName(name="Thermal Tuning") ThermalTuning thermal,
			@ParamName(name="Positioning") Positioning positioning,
			@ParamName(name="Mode Mux") ModeMux modeMux,
			@ParamName(name="Driver") AbstractDriver driver,
			@ParamName(name="SERDES") AbstractSerdes serdes
			) {
		this.linkFormat = linkFormat ;
		this.combLaser = combLaser ;
		this.coupler = coupler ;
		this.modulator = modulator ;
		this.thermal = thermal ;
		this.positioning = positioning ;
		this.modeMux = modeMux ;
		this.driver = driver ;
		this.serdes = serdes ;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.putAll(linkFormat.getAllParameters());
		map.putAll(combLaser.getAllParameters());
		map.putAll(coupler.getAllParameters());
		map.putAll(modulator.getAllParameters());
		map.putAll(thermal.getAllParameters());
		map.putAll(positioning.getAllParameters());
		map.putAll(modeMux.getAllParameters());
		map.putAll(driver.getAllParameters());
		map.putAll(serdes.getAllParameters());
		return map ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		// calculate transmitter stuff
		
		
		
		
		man.addDataPoint(dp);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
