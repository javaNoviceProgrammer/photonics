package photonics.interconnect.gui;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;
import mathLib.sfg.numeric.SFG;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

import static mathLib.numbers.Complex.*;

public class Interconnect implements Experiment {

	Wavelength inputLambda ;
	AbstractElement[] elements ;
	int numElements ;
	SFG globalSFG = new SFG(null) ;
	Connection[] connections ;
	Transfer[] transfers ;

	public Interconnect(
			@ParamName(name="Wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Photonic Elements") AbstractElement[] elements,
			@ParamName(name="Connections") Connection[] connections,
			@ParamName(name="Transfer Functions") Transfer[] transfers
			) {
		this.inputLambda = inputLambda ;
		this.elements = elements ;
		this.numElements = elements.length ;
		this.connections = connections ;
		this.transfers = transfers ;
		buildCircuit() ;
	}

	private void buildCircuit(){
		for(int i=0; i<numElements; i++){
			elements[i].setWavelength(inputLambda);
			elements[i].buildElement();
			globalSFG.append(elements[i].getSFG());
		}
		try {
			for(int i=0; i<connections.length; i++){
				globalSFG.addArrow(connections[i].getStartPort()+".out", connections[i].getEndPort()+".in", ONE);
				globalSFG.addArrow(connections[i].getEndPort()+".out", connections[i].getStartPort()+".in", ONE);
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
									MathUtils.Conversions.todB(globalSFG.getGain(transfers[i].getStartPort()+".in", transfers[i].getEndPort()+".out").absSquared()));
			dp.addResultProperty(transfers[i].getStartPort()+"->"+transfers[i].getEndPort()+" (rad)",
									globalSFG.getGain(transfers[i].getStartPort()+".in", transfers[i].getEndPort()+".out").phase());
		}

//		for(int i=0; i<numElements; i++){
//			if(elements[i] != null)
//				dp.addProperties(elements[i].getAllParameters());
//		}
		man.addDataPoint(dp);
	}

	public static void main(String[] args){
		String pacakgeString = "photonics" ;
		String classString = Interconnect.class.getName() ;
		ExperimentConfigurationCockpit.execute(new String[]{"-p", pacakgeString, "-c", classString}, true);
	}

}
