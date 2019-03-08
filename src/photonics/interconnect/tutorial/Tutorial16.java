package photonics.interconnect.tutorial;

import static java.lang.Math.PI;

import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import photonics.interconnect.elements.active.BasicPhaseShifter;
import photonics.interconnect.elements.passive.Yjunction;
import photonics.interconnect.solver.PhotonicCircuit;
import photonics.interconnect.util.Utils;
import photonics.util.Wavelength;

public class Tutorial16 {
	
	public static void main(String[] args) {
		double excessLossdB = 0 ; // dB 
		double lambdaNm = 1550 ;
		double[] phaseShift = MathUtils.linspace(0, 2*PI, 1000) ;
		double[] transfer = new double[phaseShift.length] ; 
		for(int i=0; i<phaseShift.length; i++) {
			Wavelength lambda = new Wavelength(lambdaNm) ;
			PhotonicCircuit pc = new PhotonicCircuit() ;
			pc.setWavelength(lambda) ;
			Yjunction y1 = new Yjunction("y1", 0) ;
			Yjunction y2 = new Yjunction("y2", 0) ;
			BasicPhaseShifter ps1 = new BasicPhaseShifter("ps1", 0, 0) ;
			BasicPhaseShifter ps2 = new BasicPhaseShifter("ps2", phaseShift[i], excessLossdB) ;
			pc.addElements(y1, y2, ps1, ps2);
			pc.connectPorts("y1.port2", "ps1.port1");
			pc.connectPorts("y1.port3", "ps2.port1");
			pc.connectPorts("y2.port2", "ps1.port2");
			pc.connectPorts("y2.port3", "ps2.port2");
			transfer[i] = Utils.todB(pc.getTransfer("y1.port1", "y2.port1").absSquared()) ;
		}

		MatlabChart fig = new MatlabChart() ;
		fig.plot(phaseShift, transfer);
		fig.renderPlot();
		fig.xlabel("Phase shift (rad)");
		fig.ylabel("Transmission (dB)");
		fig.run(true);
	}
}
