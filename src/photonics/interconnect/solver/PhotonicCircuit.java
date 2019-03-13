package photonics.interconnect.solver;

import static mathLib.numbers.Complex.ONE;

import java.util.ArrayList;

import flanagan.io.FileOutput;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.util.Wavelength;

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
			// ADD LATER
		}
		
		try {
			globalSFG.addArrow(endPort+".out", startPort+".in", ONE);
		} catch (Exception e) {
			// ADD LATER
		}
	}

	public Complex getTransfer(String startPort, String endPort) {
		return globalSFG.getGain(startPort + ".in", endPort + ".out") ;
	}

	public SFG getCircuit() {
		return globalSFG ;
	}
	
	public void printAllDetails() {
		System.out.println(getCircuit().printForwardPaths_noGains());
		System.out.print(getCircuit().printAllLoops_compactForm());
		System.out.println(getCircuit().printDelta_compactForm());
	}
	
	public void printDetails() {
		String st0 = getCircuit().printForwardPaths_noGains() ;
		String[] st1 = st0.split("\\n") ;
		for(String s: st1)
			if(s != null && !s.equals(""))
				System.out.println(SparamParser.parse(s));
		
		String st2 = getCircuit().printAllLoops_compactForm() ;
		String[] st3 = st2.split("\\n") ;
		for(String s: st3)
			if(s != null && !s.equals(""))
				System.out.println(SparamParser.parse(s));
		
		System.out.println(getCircuit().printDelta_compactForm());
	}
	
	public void printDetails(String startPort, String endPort) {
		globalSFG.getGain(startPort + ".in", endPort + ".out") ;
		String st0 = getCircuit().printForwardPaths_noGains() ;
		String[] st1 = st0.split("\\n") ;
		for(String s: st1)
			if(s != null && !s.equals(""))
				System.out.println(SparamParser.parse(s));
		
		String st2 = getCircuit().printAllLoops_compactForm() ;
		String[] st3 = st2.split("\\n") ;
		for(String s: st3)
			if(s != null && !s.equals(""))
				System.out.println(SparamParser.parse(s));
		
		System.out.println(getCircuit().printDelta_compactForm());
	}
	
	public ArrayList<String> getAllDetails() {
		ArrayList<String> details = new ArrayList<>() ;
		
		String st0 = getCircuit().printForwardPaths_noGains() ;
		String[] st1 = st0.split("\\n") ;
		for(String s: st1)
			if(s != null && !s.equals(""))
				details.add(SparamParser.parse(s)) ;
		
		String st2 = getCircuit().printAllLoops_compactForm() ;
		String[] st3 = st2.split("\\n") ;
		for(String s: st3)
			if(s != null && !s.equals(""))
				details.add(SparamParser.parse(s)) ;
		
		String st4 = getCircuit().printDelta_compactForm() ;
		String[] st5 = st4.split("\\n") ;
		for(String s: st5)
			if(s != null && !s.equals(""))
				details.add(SparamParser.parse(s)) ;
		
		return details ;
	}
	
	public void saveToFile(String filePath) {
		ArrayList<String> details = getAllDetails() ;
		String[] lines = new String[details.size()] ;
		for(int i=0; i<lines.length; i++)
			lines[i] = details.get(i) ;
		
		FileOutput fo = new FileOutput(filePath, 'w') ;
		fo.println(lines);
		fo.close();
		System.gc();
	}

}
