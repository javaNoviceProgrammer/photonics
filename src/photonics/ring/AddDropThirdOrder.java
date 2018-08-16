package photonics.ring;

import java.util.ArrayList;
import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import static mathLib.numbers.Complex.*;
import static mathLib.numbers.ComplexMath.*;

public class AddDropThirdOrder {

	public double kin, kout, tin, tout, k12, t12, k23, t23 ;
	public double L, phi_rad ;
	ArrayList<String> nodes ;

	public SFG sfg ;

	public AddDropThirdOrder(
			@ParamName(name="round-trip phase (rad)") double phi_rad,
			@ParamName(name="ring loss") double L,
			@ParamName(name="input kappa") double kin,
			@ParamName(name="output kappa") double kout,
			@ParamName(name="k12") double k12,
			@ParamName(name="k23") double k23
			){
		this.phi_rad = phi_rad ;
		this.L = L ;
		this.kin = kin ;
		this.kout = kout ;
		this.tin = Math.sqrt(1-kin*kin) ;
		this.tout = Math.sqrt(1-kout*kout) ;
		this.k12 = k12 ;
		this.t12 = Math.sqrt(1-k12*k12) ;
		this.k23 = k23 ;
		this.t23 = Math.sqrt(1-k23*k23) ;
		nodes = new ArrayList<>() ;
		buildSFG() ;
	}

	private void buildSFG(){
		for(int i=1; i<=4; i++) {
			for(int j=1; j<=4; j++) {
				String st = "DC"+i+".N"+j ;
				nodes.add(st) ;
			}
		}
		
//		nodes.add("DC1.N1") ;
//		nodes.add("DC1.N2") ;
//		nodes.add("DC1.N3") ;
//		nodes.add("DC1.N4") ;
//
//		nodes.add("DC2.N1") ;
//		nodes.add("DC2.N2") ;
//		nodes.add("DC2.N3") ;
//		nodes.add("DC2.N4") ;
//		
//		nodes.add("DC3.N1") ;
//		nodes.add("DC3.N2") ;
//		nodes.add("DC3.N3") ;
//		nodes.add("DC3.N4") ;
//		
//		nodes.add("DC4.N1") ;
//		nodes.add("DC4.N2") ;
//		nodes.add("DC4.N3") ;
//		nodes.add("DC4.N4") ;

		sfg = new SFG(nodes.size(), nodes) ;
		
//		Complex gain = minusJ.times(phi_rad/2).exp().times(Math.pow(L, 0.25)) ;
		Complex gain = exp(-j*phi_rad/2.0)*Math.pow(L, 0.25) ;

		sfg.addArrow("DC1.N1", "DC1.N2", tin);
		sfg.addArrow("DC1.N1", "DC1.N3", -j*kin);
		sfg.addArrow("DC1.N4", "DC1.N3", tin);
		sfg.addArrow("DC1.N4", "DC1.N2", -j*kin);

		sfg.addArrow("DC2.N2", "DC2.N1", t12);
		sfg.addArrow("DC2.N2", "DC2.N4", -j*k12);
		sfg.addArrow("DC2.N3", "DC2.N4", t12);
		sfg.addArrow("DC2.N3", "DC2.N1", -j*k12);
		
		sfg.addArrow("DC1.N3", "DC2.N2", gain);
		sfg.addArrow("DC2.N1", "DC1.N4", gain);
		
		sfg.addArrow("DC3.N1", "DC3.N2", t23);
		sfg.addArrow("DC3.N1", "DC3.N3", -j*k23);
		sfg.addArrow("DC3.N4", "DC3.N3", t23);
		sfg.addArrow("DC3.N4", "DC3.N2", -j*k23);
		
		sfg.addArrow("DC2.N4", "DC3.N1", gain);
		sfg.addArrow("DC3.N2", "DC2.N3", gain);
		
		sfg.addArrow("DC4.N2", "DC4.N1", tout);
		sfg.addArrow("DC4.N2", "DC4.N4", -j*kout);
		sfg.addArrow("DC4.N3", "DC4.N4", tout);
		sfg.addArrow("DC4.N3", "DC4.N1", -j*kout);
		
		sfg.addArrow("DC3.N3", "DC4.N2", gain);
		sfg.addArrow("DC4.N1", "DC3.N4", gain);
	}
	
	public Complex getS41(){
		sfg.buildForwardPaths("DC1.N1", "DC4.N4");
		return sfg.computeForwardGain().divides(sfg.computeDelta()) ;
	}
	
	public Complex getS21(){
		sfg.buildForwardPaths("DC1.N1", "DC1.N2");
		return sfg.computeForwardGain().divides(sfg.computeDelta()) ;
	}
	
	public void buildDrop(){
		sfg.buildForwardPaths("DC1.N1", "DC4.N4");
	}
	
	public void buildThru(){
		sfg.buildForwardPaths("DC1.N1", "DC1.N2");
	}
	
	// for test
	public static void main(String[] args){
		AddDropThirdOrder adr = new AddDropThirdOrder(0, 1, 0.1, 0.1, 0.1, 0.1) ;
		System.out.println(adr.sfg.printAllLoops_compactForm());
	}
	
}
