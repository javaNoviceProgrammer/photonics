package weights;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;

import static java.lang.Math.* ;

import java.lang.reflect.Executable;

public class WeightMapping implements Experiment {

	double t1, t2, k1, k2, L, phiRad ;
	
	public WeightMapping(
			@ParamName(name="k1") double k1,
			@ParamName(name="k2") double k2,
			@ParamName(name="L") double L,
			@ParamName(name="phi (rad)") double phiRad
			) {
		this.t1 = sqrt(1-k1*k1) ;
		this.t2 = sqrt(1-k2*k2) ;
		this.k1 = k1 ;
		this.k2 = k2 ;
		this.L = L ;
		this.phiRad = phiRad ;
	}
	
	
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		double finesse = sqrt(t1*t2*sqrt(L))/(1-t1*t2*sqrt(L)) ;
		double T0drop = (1-t1*t1)*(1-t2*t2)*sqrt(L)/pow(1-t1*t2*sqrt(L), 2) ;
		double T0thru = pow((t1-t2*sqrt(L))/(1-t1*t2*sqrt(L)), 2) ;
		double arg = 2*finesse/PI * sin(phiRad/2) ;
		double argSquared = arg*arg ;
		double thru = (T0thru+argSquared)/(1+argSquared) ;
		double drop = T0drop/(1+argSquared) ;
		double arg0 = 2*finesse/PI ;
		double arg0Squared = arg0*arg0 ;
		double weight = (arg0Squared+1)/(argSquared+1) * (argSquared+T0thru-T0drop)/(arg0Squared+T0thru-T0drop) ;
		
		DataPoint dp = new DataPoint() ;
		dp.addProperty("phi (rad)", phiRad);
		dp.addResultProperty("drop (dB)", 10*log10(drop));
		dp.addResultProperty("drop", drop);
		dp.addResultProperty("thru (dB)", 10*log10(thru));
		dp.addResultProperty("thru", thru);
		dp.addResultProperty("weight", weight);
		man.addDataPoint(dp);
	}
	
	public static void main(String[] args) {
		String pkgName = "weights" ;
		String clsName = WeightMapping.class.getName() ;
		String[] arg = {"-p", pkgName, "-c", clsName} ;
		ExperimentConfigurationCockpit.execute(arg, true);
	}

}
