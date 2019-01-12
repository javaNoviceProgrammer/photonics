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

		// power penalty
		double txPenaltydB = transmitter.getTotalTxPenaltydB() ;
		double rxPenaltydB = receiver.demux.getPenaltydB(transmitter.linkFormat.dataRateGbps, transmitter.combLaser.getSpacingGHz(), transmitter.modulator.getOMAdB())
								+ receiver.modeDemux.getPenaltydB(transmitter.modulator.getOMAdB())
								+ receiver.positioning.getTotalPropLossdB()
								+ receiver.receiverElec.jitterPenaltydB
								+ receiver.receiverElec.polarizationLossdB ;

		double linkPenaltydB = txPenaltydB + rxPenaltydB ;
		double requiredLaserPowerPerModedBm = receiver.receiverElec.getSensitivitydBm(transmitter.linkFormat)
												+ linkPenaltydB ;
		double requiredLaserPowerPerLinedBm = requiredLaserPowerPerModedBm + 10*Math.log10(transmitter.linkFormat.numberOfModes) ;
		double laserPowerPerLinedBm = 0.0 ;
		boolean linkFeasible = requiredLaserPowerPerLinedBm <= transmitter.combLaser.getPowerPerLinedBm() ;

		if(requiredLaserPowerPerLinedBm > transmitter.combLaser.getPowerPerLinedBm())
			laserPowerPerLinedBm = requiredLaserPowerPerLinedBm ;
		else
			laserPowerPerLinedBm = transmitter.combLaser.getPowerPerLinedBm() ;

		double laserPowerPerLinemW = Math.pow(10.0, laserPowerPerLinedBm/10.0) ;
		double linkBudgetdB = laserPowerPerLinedBm - 10*Math.log10(transmitter.linkFormat.numberOfModes)
													- receiver.receiverElec.getSensitivitydBm(transmitter.linkFormat) ;
		// TX energy consumptions
		double laserPowerPerLinePerMode = laserPowerPerLinemW/transmitter.linkFormat.numberOfModes ;
		double laserEnergy = (laserPowerPerLinePerMode/transmitter.combLaser.getWPE())/transmitter.linkFormat.dataRateGbps ;
		double txThermalEnergy = transmitter.thermal.tuningPowermW/transmitter.linkFormat.dataRateGbps ;
		double modulatorDynamicEnergy = 1.0/4.0 * transmitter.modulator.getCapfF()*1e-3 *
															transmitter.driver.getVpp()*transmitter.driver.getVpp() ;
		double driverEnergy = transmitter.driver.getEnergyPJperBit(transmitter.linkFormat, transmitter.thermal, transmitter.modulator) ;
		double serdesEnergy = transmitter.serdes.getSerdesEnergyPjPerBit(transmitter.linkFormat.dataRateGbps) ;
		double txTotalEnergy = laserEnergy + txThermalEnergy + modulatorDynamicEnergy + driverEnergy + serdesEnergy ;

		// RX energy consumptions
		double rxThermalEnergy = receiver.thermal.tuningPowermW/transmitter.linkFormat.dataRateGbps ;
		double rxTiaEnergy = receiver.receiverElec.tia.getEnergyPerBit(transmitter.linkFormat.dataRateGbps,
																		receiver.receiverElec.getSensitivitydBm(transmitter.linkFormat)) ;
		double rxTotalEnergy = rxThermalEnergy + rxTiaEnergy ;


		DataPoint dp = new DataPoint() ;
		dp.addProperties(transmitter.getAllParameters());
		dp.addProperties(receiver.getAllParameters());

		dp.addResultProperty("Tx Laser Energy (pJ/bit)", laserEnergy);
		dp.addResultProperty("Tx Static Thermal Energy (pJ/bit)", txThermalEnergy);
		dp.addResultProperty("Tx Modulator Energy (pJ/bit)", modulatorDynamicEnergy);
		dp.addResultProperty("Tx Driver Energy (pJ/bit)", driverEnergy);
		dp.addResultProperty("Tx Serdes Energy (pJ/bit)", serdesEnergy);
		dp.addResultProperty("Tx Total Energy (pJ/bit)", txTotalEnergy);

		dp.addResultProperty("Rx Static Thermal Energy (pJ/bit)", rxThermalEnergy);
		dp.addResultProperty("Rx TIA Energy (pJ/bit)", rxTiaEnergy);
		dp.addResultProperty("Rx Total Energy (pJ/bit)", rxTotalEnergy);

		dp.addResultProperty("Link Total Energy (pJ/bit)", txTotalEnergy + rxTotalEnergy);

		dp.addResultProperty("Receiver Sensitivity (dBm)", receiver.receiverElec.sensitivity.getOpticalSensivitydBm(transmitter.linkFormat.dataRateGbps));

		dp.addResultProperty("Link Penalty (dB)", linkPenaltydB);
		dp.addResultProperty("Available Laser Power Per Line (dBm)", laserPowerPerLinedBm);
		dp.addResultProperty("Required Minimum Laser Power Per Line (dBm)", requiredLaserPowerPerLinedBm);
		dp.addResultProperty("Available Link Budget (dB)", linkBudgetdB);
		dp.addResultProperty("Link Feasible", linkFeasible);

		man.addDataPoint(dp);
	}

	public static void main(String[] args) {
		String pkgName = "pipes" ;
		String clsName = Link.class.getName() ;
		String[] arg = {"-p", pkgName, "-c", clsName} ;
		ExperimentConfigurationCockpit.execute(arg, true);
	}

}
