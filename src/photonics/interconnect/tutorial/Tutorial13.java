package photonics.interconnect.tutorial;

import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.DirectionalCoupler;
import photonics.interconnect.modes.Neff450X220CoupledStrip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.util.Wavelength;

public class Tutorial13 {
	
	public static void main(String[] args) {
		double L = 36.8 ; // um
		double gap = 200e-3 ; // um
		double loss = 0 ; // dB per cm
		double[] lambdaNm = MathUtils.linspace(1500, 1600, 1000) ;
		double[] thru = new double[lambdaNm.length] ; 
		double[] cross = new double[lambdaNm.length] ; 
		for(int i=0; i<lambdaNm.length; i++) {
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			DirectionalCoupler dc1 = new DirectionalCoupler("dc1", new Neff450X220CoupledStrip(), L, gap, loss) ;
			pc.addElement(dc1);
			thru[i] = pc.getTransfer("dc1.port1", "dc1.port2").absSquared() ;
			cross[i] = pc.getTransfer("dc1.port1", "dc1.port3").absSquared() ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, thru, "b");
		fig.plot(lambdaNm, cross, "r");
		fig.renderPlot();
		fig.xlabel("Length (um)");
		fig.ylabel("Transmission");
		fig.run(true);
	}
}
