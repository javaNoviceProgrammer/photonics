package pipes;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

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
		// setup parameters
		this.driver.setEnergyPJperBit(linkFormat, thermal, modulator);
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
	
	public double getTotalTxCouplingLossdB() {
		return coupler.getTotalLossdB() ;
	}
	
	public double getTotalTxPenaltydB() {
		return getTotalTxCouplingLossdB() + modulator.getTotalPenaltydB() + positioning.getTotalPropLossdB() + 
				modeMux.muxPenaltydB  ;
	}
	
//	public double getTxTotalEnergyPjPerBit() {
//		return 
//	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		// add all input parameters
		dp.addProperties(getAllParameters());
		// add calculated values
		dp.addResultProperty("Total Aggregation (Tbps)", linkFormat.getTotalAggregationTbps());
		dp.addResultProperty("Tx Coupling loss (dB)", getTotalTxCouplingLossdB());
		// modulator
		dp.addResultProperty("Modulator OMA (dB)", modulator.getOMAdB());
		dp.addResultProperty("Modulator IL (dB)", modulator.getILdB());
		dp.addResultProperty("Modulator Penalty (dB)", modulator.getTotalPenaltydB());
		dp.addResultProperty("Modulator energy (pJ/bit)", 1.0/4.0 * modulator.getCapfF()*1e-3 * driver.getVpp()*driver.getVpp());
		// thermal
		dp.addProperty("TX thermal tuning (mW)", thermal.getThermalTuningPowermW());
		dp.addResultProperty("TX thermal tuning (pJ/bit)", thermal.getThermalTuningPowermW()/linkFormat.dataRateGbps);
		// positioning
		dp.addResultProperty("Tx Wg loss (dB)", positioning.getTotalPropLossdB());
		
		// Tx penalty
		dp.addResultProperty("TX total penalty (dB)", getTotalTxPenaltydB());
		
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args) {
		String pkgName = "pipes" ;
		String clsName = Transmitter.class.getName() ;
		String[] arg = {"-p", pkgName, "-c", clsName} ;
		ExperimentConfigurationCockpit.execute(arg, true);
	}
	


}
