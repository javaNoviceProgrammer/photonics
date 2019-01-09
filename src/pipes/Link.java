package pipes;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class Link implements Experiment {
	
	Transmitter transmitter ;
	Receiver receiver ;
	
	public Link(
			Transmitter transmitter,
			Receiver receiver
			) {
		this.transmitter = transmitter ;
		this.receiver = receiver ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		
		// TX energy consumptions
		double laserPowerPerLinePerMode = transmitter.combLaser.getPowerPerLinemW()/transmitter.linkFormat.numberOfModes ;
		double laserEnergy = (laserPowerPerLinePerMode/transmitter.combLaser.getWPE())/transmitter.linkFormat.dataRateGbps ;
		double txThermalEnergy = transmitter.thermal.tuningPowermW/transmitter.linkFormat.dataRateGbps ;
		double modulatorDynamicEnergy = 1.0/4.0 * transmitter.modulator.getCapfF()*1e-3 * 
															transmitter.driver.getVpp()*transmitter.driver.getVpp() ;
		double driverEnergy = transmitter.driver.getEnergyPJperBit(transmitter.linkFormat, transmitter.thermal, transmitter.modulator) ;
		double serdesEnergy = transmitter.serdes.getSerdesEnergyPjPerBit(transmitter.linkFormat.dataRateGbps) ;
		double txTotalEnergy = laserEnergy + txThermalEnergy + modulatorDynamicEnergy + driverEnergy + serdesEnergy ;
		
		// RX energy consumptions
		
		
		// power penalty
		
		DataPoint dp = new DataPoint() ;
		dp.addProperties(transmitter.getAllParameters());
		dp.addProperties(receiver.getAllParameters());

		dp.addResultProperty("Tx Laser Energy (pJ/bit)", laserEnergy);
		dp.addResultProperty("Tx Static Thermal Energy (pJ/bit)", txThermalEnergy);
		dp.addResultProperty("Tx Modulator Energy (pJ/bit)", modulatorDynamicEnergy);
		dp.addResultProperty("Tx Driver Energy (pJ/bit)", driverEnergy);
		dp.addResultProperty("Tx Serdes Energy (pJ/bit)", serdesEnergy);
		dp.addResultProperty("Tx Total Energy (pJ/bit)", txTotalEnergy);
		
		
		dp.addResultProperty("Receiver Sensitivity (dBm)", receiver.receiverElec.sensitivity.getOpticalSensivitydBm(transmitter.linkFormat.dataRateGbps));
		
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args) {
		String pkgName = "pipes" ;
		String clsName = Link.class.getName() ;
		String[] arg = {"-p", pkgName, "-c", clsName} ;
		ExperimentConfigurationCockpit.execute(arg, true);
	}

}
