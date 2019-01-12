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

public class SideCoupledModulator extends AbstractModulator implements Experiment {

	double Q_Qi, dl_Dl ;
	double capfF ;

	public SideCoupledModulator(
			@ParamName(name="Q/Qi ratio [< 0.5]") double Q_Qi,
			@ParamName(name="dlambda/FWHM ratio") double dl_Dl,
			@ParamName(name="Modulator Capacitance (fF)") double capfF
			) {
		this.Q_Qi = Q_Qi ;
		this.dl_Dl = dl_Dl ;
		this.capfF = capfF ;
	}

	double getTrans(double Q_Qi, double dl_Dl) {
		double x = Q_Qi * (2-1.0/Q_Qi) ;
		double num = (2*dl_Dl)*(2*dl_Dl) + x*x ;
		double denom = 1 + (2*dl_Dl)*(2*dl_Dl) ;
		return num/denom ;
	}

	double getTransdB(double Q_Qi, double dl_Dl) {
		return 10*Math.log10(getTrans(Q_Qi, dl_Dl)) ;
	}

	@Override
	public double getILdB() {
		return -getTransdB(Q_Qi, dl_Dl) ;
	}

	@Override
	public double getOMAdB() {
		return getTransdB(Q_Qi, dl_Dl) - getTransdB(Q_Qi, 0.0);
	}

	@Override
	public double getOMApenaltydB() {
		double er = getTrans(Q_Qi, dl_Dl)/getTrans(Q_Qi, 0.0) ;
		double arg = (er+1.0)/(er-1) ;
		return todB(arg) ;
	}

	@Override
	public double getOOKpenlatydB() {
		double er = getTrans(Q_Qi, dl_Dl)/getTrans(Q_Qi, 0.0) ;
		return todB(2.0*er/(er+1.0)) ;
	}

	@Override
	public double getTotalPenaltydB() {
		return getILdB() + getOMApenaltydB() + getOOKpenlatydB() ;
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

	static double todB(double x) {
		return 10*Math.log10(x) ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Q/Qi", Q_Qi);
		dp.addProperty("dlambda/FWHM", dl_Dl);
		dp.addResultProperty("Modulation IL (dB)", getILdB());
		dp.addResultProperty("OMA (dB)", getOMAdB());
		dp.addResultProperty("OMA penalty (dB)", getOMApenaltydB());
		dp.addResultProperty("OOK penalty (dB)", getOOKpenlatydB());
		dp.addResultProperty("Total Penalty (dB)", getTotalPenaltydB());
		man.addDataPoint(dp);
	}

	public static void main(String[] args) {
		String pkgName = "pipes" ;
		String clsName = SideCoupledModulator.class.getName() ;
		String[] arguments = {"-p", pkgName, "-c", clsName} ;
		ExperimentConfigurationCockpit.execute(arguments, true);
	}

}
