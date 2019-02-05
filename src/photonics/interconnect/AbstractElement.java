package edu.lrl.interconnectSFG.elements;

import java.util.ArrayList;
import java.util.Map;

import complexSFG.edu.lrl.solver.SFG;
import edu.lrl.interconnectSFG.util.Wavelength;
import edu.lrl.interconnectSFG.util.WgProperties;

public abstract class AbstractElement {
	
	protected String name ;
	protected SFG sfgElement ;
	protected ArrayList<String> nodes ;
	
	public abstract void buildElement() ;
	
	public String getName(){
		return name ;
	}
	
	public SFG getSFG() {
		return sfgElement ;
	}
	
	public void addSFG(SFG sfg){
		sfg.append(sfgElement);
	}
	
	public abstract void setWavelength(Wavelength inputLambda) ;
	public abstract void setWgProperties(WgProperties wgProp) ;
	
	public abstract Map<String, String> getAllParameters() ;

}
