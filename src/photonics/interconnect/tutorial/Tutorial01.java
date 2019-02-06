package photonics.interconnect.tutorial;

import mathLib.numbers.Complex;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.StraightWg;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial01 {

	/*
	 * Simulating a simple straight waveguide
	 */

	public static void main(String[] args) {

		double[] lambdaNm = MathUtils.linspace(1500, 1600, 1000) ;
		Complex[] transfer = new Complex[lambdaNm.length] ;
		// perform wavelength sweep
		for(int i=0; i<lambdaNm.length; i++) {
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			// create circuit elements
			StraightWg wg1 = new StraightWg("wg1", s -> 2.23, 20, 100) ;
			// create the circuit
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.addElement(wg1);
			pc.setWavelength(lambda) ;

			transfer[i] = pc.getTransfer("wg1.port1", "wg1.port2") ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, transfer);
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Transfer");
		fig.run(true);

	}

}
