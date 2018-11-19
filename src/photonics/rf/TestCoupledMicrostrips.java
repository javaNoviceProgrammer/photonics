package photonics.rf;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

public class TestCoupledMicrostrips implements Experiment {

	CoupledMicrostripLines line ;
	
	public TestCoupledMicrostrips(
			CoupledMicrostripLines line
			) {
		this.line = line ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperties(line.getAllParameters());
		dp.addResultProperty("Neff (even)", line.getNeffEven());
		dp.addResultProperty("Neff (odd)", line.getNeffOdd());
		dp.addResultProperty("Ze (static)", line.getImpedanceEvenStatic());
		dp.addResultProperty("Zo (static)", line.getImpedanceOddStatic());
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args) {
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		String packageName = "photonics" ;
		String className = TestCoupledMicrostrips.class.getName() ;
		ExperimentConfigurationCockpit.execute(new String[] {"-p", packageName, "-c", className}, true);
	}

}
