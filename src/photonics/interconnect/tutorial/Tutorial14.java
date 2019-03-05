package photonics.interconnect.tutorial;

import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.passive.DirectionalCoupler;
import photonics.interconnect.elements.passive.StraightWg;
import photonics.interconnect.modes.Neff450X220CoupledStrip;
import photonics.interconnect.modes.Neff450X220Strip;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.interconnect.util.Utils;
import photonics.util.Wavelength;

public class Tutorial14 {
	
	public static void main(String[] args) {
		double L = 36.8 ; // um
		double Lwg = 40 ; // um
		double gap = 200e-3 ; // um
		double loss = 0 ; // dB per cm
		double[] lambdaNm = MathUtils.linspace(1500, 1600, 1000) ;
		double[] reflection = new double[lambdaNm.length] ; 
		for(int i=0; i<lambdaNm.length; i++) {
			Wavelength lambda = new Wavelength(lambdaNm[i]) ;
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			DirectionalCoupler dc1 = new DirectionalCoupler("dc1", new Neff450X220CoupledStrip(), L, gap, loss) ;
			StraightWg wg1 = new StraightWg("wg1", new Neff450X220Strip(), loss, Lwg) ;
			pc.addElements(dc1);
			pc.addElement(wg1);
			pc.connectPorts("dc1.port2", "wg1.port1");
			pc.connectPorts("wg1.port2", "dc1.port3");
			reflection[i] = Utils.todB(pc.getTransfer("dc1.port1", "dc1.port1").absSquared()) ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, reflection, "b");
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Reflection (dB)");
		fig.run(true);
	}
}
