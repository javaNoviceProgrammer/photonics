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

public class Modulator extends AbstractModulator implements Experiment {


	double Q_Qi, dl_Dl ;
	double capfF ;

	public Modulator(
			@ParamName(name="Q/Qi ratio [< 0.5]") double Q_Qi,
			@ParamName(name="dlambda/FWHM ratio") double dl_Dl,
			@ParamName(name="Modulator Capacitance (fF)") double capfF
			) {
		this.Q_Qi = Q_Qi ;
		this.dl_Dl = dl_Dl ;
		this.capfF = capfF ;
	}

	double getTrans(double Q_Qi, double dl_Dl) {
		double num = 2*Q_Qi ;
		double denom = 1 + (2*dl_Dl)*(2*dl_Dl) ;
		return num/denom ;
	}

	public double getILdB() {
		return todB(1.0/getTrans(Q_Qi, 0.0)) ;
	}

	public double getOMAdB() {
		double er = getTrans(Q_Qi, 0.0)/getTrans(Q_Qi, dl_Dl) ;
		return todB(er) ;
	}

	double getOMAPenalty() {
		double er = getTrans(Q_Qi, 0.0)/getTrans(Q_Qi, dl_Dl) ;
		double arg = (er+1.0)/(er-1) ;
		return todB(arg) ;
	}

	double getOOKPenalty() {
		double er = getTrans(Q_Qi, 0.0)/getTrans(Q_Qi, dl_Dl) ;
		return todB(2.0*er/(er+1.0)) ;
	}

	double getTotPenalty() {
		return getILdB() + getOMAPenalty() + getOOKPenalty() ;
	}

	static double todB(double x) {
//		if(x<=0)
//			throw new IllegalArgumentException("argument must be positive") ;
		return 10*Math.log10(x) ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Q/Qi", Q_Qi);
		dp.addProperty("dlambda/FWHM", dl_Dl);
		dp.addResultProperty("Modulation IL (dB)", getILdB());
		dp.addResultProperty("OMA (dB)", getOMAdB());
		dp.addResultProperty("OMA penalty (dB)", getOMAPenalty());
		dp.addResultProperty("OOK penalty (dB)", getOOKPenalty());
		dp.addResultProperty("Total Penalty (dB)", getTotPenalty());
		man.addDataPoint(dp);
	}

	public static void main(String[] args) {
		String pkgName = "pipes" ;
		String clsName = Modulator.class.getName() ;
		String[] arguments = {"-p", pkgName, "-c", clsName} ;
		ExperimentConfigurationCockpit.execute(arguments, true);
	}

	@Override
	public double getOMApenaltydB() {
		return getOMAPenalty();
	}

	@Override
	public double getOOKpenlatydB() {
		return getOOKPenalty();
	}

	@Override
	public double getTotalPenaltydB() {
		return getTotPenalty();
	}

	@Override
	public double getCapfF() {
		return capfF;
	}

	@Override
	public double getEOeff() {
		throw new IllegalArgumentException("Not implemented") ;
	}

	@Override
	public Map<? extends String, ? extends String> getAllParameters() {
		Map<String, String> map = new SimpleMap<>() ;
		return map ;
	}

}
