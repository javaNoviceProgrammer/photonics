package photonics.rf.tests;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;
import photonics.rf.StripLine;

public class TestStripLine implements Experiment {

	StripLine line ;
	
	public TestStripLine(
			StripLine line
			) {
		this.line = line ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperties(line.getAllParameters());
		dp.addResultProperty("impedance (ohm)", line.getImpedance());
		dp.addResultProperty("eps_eff", line.getEpsEff());
		dp.addResultProperty("neff", line.getNeff());
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args) {
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		String packageName = "photonics" ;
		String className = TestStripLine.class.getName() ;
		ExperimentConfigurationCockpit.execute(new String[] {"-p", packageName, "-c", className}, true);
	}

}
