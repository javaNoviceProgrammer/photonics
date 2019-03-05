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

public class Tutorial15 {
	
	public static void main(String[] args) {
		double L = 36.8 ; // um
		double Lwg = 40 ; // um
		double gap = 200e-3 ; // um
		double loss = 0 ; // dB per cm
		double[] lambdaNm = MathUtils.linspace(1530, 1570, 1000) ;
		double[] r1 = new double[lambdaNm.length] ; 
		double[] r2 = new double[lambdaNm.length] ; 
		double[] r3 = new double[lambdaNm.length] ; 
		double[] r4 = new double[lambdaNm.length] ; 
		double[] r10 = new double[lambdaNm.length] ; 
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
			r1[i] = Utils.todB(pc.getTransfer("dc1.port1", "dc1.port1").absSquared()) ;
			r2[i] = Utils.todB(pc.getTransfer("dc1.port1", "dc1.port1").pow(2).absSquared()) ;
			r3[i] = Utils.todB(pc.getTransfer("dc1.port1", "dc1.port1").pow(3).absSquared()) ;
			r4[i] = Utils.todB(pc.getTransfer("dc1.port1", "dc1.port1").pow(4).absSquared()) ;
			r10[i] = Utils.todB(pc.getTransfer("dc1.port1", "dc1.port1").pow(10).absSquared()) ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(lambdaNm, r1, "b", 2f, "r1");
		fig.plot(lambdaNm, r2, "r", 2f, "r2");
		fig.plot(lambdaNm, r3, "m", 2f, "r3");
		fig.plot(lambdaNm, r4, "k", 2f, "r4");
		fig.plot(lambdaNm, r10, "g", 2f, "r10");
		fig.renderPlot();
		fig.xlabel("Wavelength (nm)");
		fig.ylabel("Reflection (dB)");
		fig.run(true);
	}
}
