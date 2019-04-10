package photonics.interconnect.gui.tree;

import ch.epfl.general_libraries.clazzes.ParamName;

public class Transfer {

	String startPort ;
	String endPort ;

	public Transfer(
			@ParamName(name="Start Port") String startPort,
			@ParamName(name="End Port") String endPort
			){
		this.startPort = startPort ;
		this.endPort = endPort ;
	}

	public String getStartPort(){
		return startPort ;
	}

	public String getEndPort(){
		return endPort ;
	}

}
