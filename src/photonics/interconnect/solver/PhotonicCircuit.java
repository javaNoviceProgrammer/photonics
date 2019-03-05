package photonics.interconnect.solver;

import java.util.ArrayList;

import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

import static mathLib.numbers.Complex.*;

public class PhotonicCircuit {

	ArrayList<AbstractElement> elements ;
	Wavelength lambda ;
	SFG globalSFG ;

	public PhotonicCircuit(Wavelength lambda) {
		this.elements = new ArrayList<>() ;
		this.globalSFG = new SFG(null) ;
		this.lambda = lambda ;
	}

	public PhotonicCircuit() {
		this.elements = new ArrayList<>() ;
		this.globalSFG = new SFG(null) ;
	}

	public void setWavelength(Wavelength lambda) {
		this.lambda = lambda ;
	}

	public void addElement(AbstractElement elem) {
		elem.setWavelength(lambda);
		elem.buildElement();
		elements.add(elem) ;
		globalSFG.append(elem.getSFG());
	}
	
	public void addElements(AbstractElement... elems) {
		for(AbstractElement elem: elems) {
			elem.setWavelength(lambda);
			elem.buildElement();
			elements.add(elem) ;
			globalSFG.append(elem.getSFG());
		}
	}

//	public void connectPorts(String startPort, String endPort){
//		globalSFG.addArrow(startPort+".out", endPort+".in", ONE);
//		globalSFG.addArrow(endPort+".out", startPort+".in", ONE);
//	}
	
	public void connectPortsUnidirectional(String startPort, String endPort){
		try {
			globalSFG.addArrow(startPort+".out", endPort+".in", ONE);
		} catch (Exception e) {
			globalSFG.addArrow(endPort+".out", startPort+".in", ONE);
		}
	}
	
	public void connectPorts(String startPort, String endPort){
		try {
			globalSFG.addArrow(startPort+".out", endPort+".in", ONE);
		} catch (Exception e) {
			
		}
		
		try {
			globalSFG.addArrow(endPort+".out", startPort+".in", ONE);
		} catch (Exception e) {
			
		}
	}

	public Complex getTransfer(String startPort, String endPort) {
		return globalSFG.getGain(startPort + ".in", endPort + ".out") ;
	}

	public SFG getCircuit() {
		return globalSFG ;
	}

}
