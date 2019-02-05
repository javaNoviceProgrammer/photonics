package photonics.interconnect.gui;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;
import complexSFG.edu.lrl.math.Complex;
import complexSFG.edu.lrl.math.MoreMath;
import complexSFG.edu.lrl.solver.SFG;
import edu.lrl.interconnectSFG.assemble.Connection;
import edu.lrl.interconnectSFG.assemble.Transfer;
import edu.lrl.interconnectSFG.elements.AbstractElement;
import edu.lrl.interconnectSFG.util.Wavelength;
import edu.lrl.interconnectSFG.util.WgProperties;

public class Interconnect implements Experiment {

	Wavelength inputLambda ;
	WgProperties wgProp ;
	AbstractElement[] elements ;
	int numElements ;
	SFG globalSFG = new SFG(null) ;
	Connection[] connections ;
	Transfer[] transfers ;

	public Interconnect(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Waveguide Properties") WgProperties wgProp,
			@ParamName(name="Photonic Elements") AbstractElement[] elements,
			@ParamName(name="Connections") Connection[] connections,
			@ParamName(name="Transfer Functions") Transfer[] transfers
			) {
		this.inputLambda = inputLambda ;
		this.wgProp = wgProp ;
		this.elements = elements ;
		this.numElements = elements.length ;
		this.connections = connections ;
		this.transfers = transfers ;
		buildCircuit() ;
	}

	private void buildCircuit(){
		for(int i=0; i<numElements; i++){
			elements[i].setWavelength(inputLambda);
			elements[i].setWgProperties(wgProp);
			elements[i].buildElement();
			globalSFG.append(elements[i].getSFG());
		}
		try {
			for(int i=0; i<connections.length; i++){
				globalSFG.addArrow(connections[i].getStartPort()+".out", connections[i].getEndPort()+".in", Complex.ONE);
				globalSFG.addArrow(connections[i].getEndPort()+".out", connections[i].getStartPort()+".in", Complex.ONE);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("wavelength (nm)", inputLambda.getWavelengthNm());
		for(int i=0; i<transfers.length; i++){
			dp.addResultProperty(transfers[i].getStartPort()+"->"+transfers[i].getEndPort()+" (dB)",
									MoreMath.Conversions.todB(globalSFG.getGain(transfers[i].getStartPort()+".in", transfers[i].getEndPort()+".out").absSquared()));
			dp.addResultProperty(transfers[i].getStartPort()+"->"+transfers[i].getEndPort()+" (rad)",
									globalSFG.getGain(transfers[i].getStartPort()+".in", transfers[i].getEndPort()+".out").phase());
		}
		dp.addProperties(wgProp.getAllParameters());
		for(int i=0; i<numElements; i++){
			dp.addProperties(elements[i].getAllParameters());
		}
		man.addDataPoint(dp);
	}

	public static void main(String[] args){
		String pacakgeString = "edu.lrl.interconnectSFG" ;
		String classString = "edu.lrl.interconnectSFG.Interconnect" ;
		ExperimentConfigurationCockpit.main(new String[]{"-p", pacakgeString, "-c", classString});
	}

}
