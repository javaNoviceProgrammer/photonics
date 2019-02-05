package edu.lrl.interconnectSFG.assemble;

import complexSFG.edu.lrl.math.Complex;
import complexSFG.edu.lrl.solver.SFG;

public class Solver {
	
	// Task 1: append the SFG from each element to the global SFG
	// Task 2: establish the port connections in global SFG ;

	SFG globalSFG ;
	
	
	
	
	public void connectPorts(String startPort, String endPort){
		globalSFG.addArrow(startPort+".in", endPort+".out", Complex.ONE);
	}
	
}
