package photonics.interconnect.elements;

import java.util.ArrayList;
import java.util.Map;

import mathLib.sfg.SFG;
import photonics.util.Wavelength;

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

	public abstract Map<String, String> getAllParameters() ;

}
