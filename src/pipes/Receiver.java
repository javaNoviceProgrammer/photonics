package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.utils.SimpleMap;

public class Receiver implements Experiment {

	// wavelength demux
	AbstractDemux demux ;
	// mode demux
	ModeDemux modeDemux ;
	// thermal
	ThermalTuning thermal ;
	// electronics
	ReceiverElectronics receiverElec ;
	// positioning
	Positioning positioning ;
	
	public Receiver(
			@ParamName(name="Wavelength Demux") AbstractDemux demux,
			@ParamName(name="Mode Demux") ModeDemux modeDemux,
			@ParamName(name="Thermal Tuning") ThermalTuning thermal,
			@ParamName(name="Positioning") Positioning positioning,
			@ParamName(name="E/O conversion") ReceiverElectronics receiverElec
			) {
		this.demux = demux ;
		this.modeDemux = modeDemux ;
		this.thermal = thermal ;
		this.positioning = positioning ;
		this.receiverElec = receiverElec ;
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		map.putAll(demux.getAllParameters());
		map.putAll(modeDemux.getAllParameters());
		map.putAll(thermal.getAllParameters());
		map.putAll(positioning.getAllParameters());
		map.putAll(receiverElec.getAllParameters());
		return map ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		// TODO Auto-generated method stub
		
	}

}
