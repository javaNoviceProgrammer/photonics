package photonics.interconnect.gui;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;
import photonics.interconnect.elements.passive.StraightWg;

public class Test implements Experiment {

	StraightWg wg ;
	
	public Test(
			StraightWg wg
			) {
		this.wg = wg ;
	}
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperties(wg.getAllParameters());
		dp.addProperty("Phase (rad)", wg.s21.phaseDegree());
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args) {
		String cls = Test.class.getName() ;
		String pkg = "mathlib; photonics.interconnect.gui;" ;
		String[] a0 = {"-p", pkg, "-c", cls} ;
		ExperimentConfigurationCockpit.execute(a0, true);
	}

}
