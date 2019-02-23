package photonics.interconnect.tutorial;

import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.DirectionalCoupler;
import photonics.interconnect.modes.Neff450X220CoupledStrip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial09 {

	/*
	 * Simulating a Directional Coupler
	 */

	public static void main(String[] args) {
		double L = 100 ;
		double[] LambdaNm = MathUtils.linspace(1500, 1600, 10000) ;
		double gapNm = 200 ;
		double[] transfer = new double[LambdaNm.length] ;
		for(int i=0; i<LambdaNm.length; i++) {
			Wavelength lambda = new Wavelength(LambdaNm[i]) ;
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			DirectionalCoupler dc1 = new DirectionalCoupler("dc1", new Neff450X220CoupledStrip(), L, gapNm*1e-3, 0) ;
			pc.addElement(dc1);
			transfer[i] = pc.getTransfer("dc1.port1", "dc1.port3").abs() ;
			
			if(i==0)
				System.out.println(pc.getCircuit().printAllLoops_compactForm());
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(LambdaNm, transfer);
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Transfer (dB)");
		fig.run(true);
	}
}
