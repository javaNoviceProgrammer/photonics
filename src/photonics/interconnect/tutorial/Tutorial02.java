package photonics.interconnect.tutorial;

import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.StraightWg;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial02 {

	/*
	 * Simulating a two cascaded straight waveguides
	 */

	public static void main(String[] args) {

		double[] lambdaNm = MathUtils.linspace(1500, 1600, 1000) ;
		Complex[] transfer = new Complex[lambdaNm.length] ;
		// perform wavelength sweep
		for(int i=0; i<lambdaNm.length; i++) {
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			// create the circuit
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			// create circuit elements
			StraightWg wg1 = new StraightWg("wg1", s -> 2.23, 2, 50) ;
			StraightWg wg2 = new StraightWg("wg2", s -> 2.23, 3, 50) ;

			pc.addElement(wg1);
			pc.addElement(wg2);
			pc.connectPorts("wg1.port2", "wg2.port1");

			transfer[i] = pc.getTransfer("wg1.port1", "wg2.port2") ;

			if(i==0)
				System.out.println(pc.getCircuit().printForwardPaths_noGains());
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, transfer);
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Transfer");
		fig.run(true);

	}

}
