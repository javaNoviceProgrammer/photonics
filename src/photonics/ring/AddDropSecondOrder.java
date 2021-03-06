package photonics.ring;

import static mathLib.numbers.Complex.j;
import static mathLib.numbers.ComplexMath.exp;

import java.util.ArrayList;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.numbers.Complex;
import mathLib.sfg.SFG;

public class AddDropSecondOrder {

	public double kin, kout, tin, tout, kmiddle, tmiddle ;
	public double L, phi_rad ;
	ArrayList<String> nodes ;

	public SFG sfg ;

	public AddDropSecondOrder(
			@ParamName(name="round-trip phase (rad)") double phi_rad,
			@ParamName(name="ring loss") double L,
			@ParamName(name="input kappa") double kin,
			@ParamName(name="output kappa") double kout,
			@ParamName(name="middle kappa") double kmiddle
			){
		this.phi_rad = phi_rad ;
		this.L = L ;
		this.kin = kin ;
		this.kout = kout ;
		this.tin = Math.sqrt(1-kin*kin) ;
		this.tout = Math.sqrt(1-kout*kout) ;
		this.kmiddle = kmiddle ;
		this.tmiddle = Math.sqrt(1-kmiddle*kmiddle) ;
		nodes = new ArrayList<>() ;
		buildSFG() ;
	}

	private void buildSFG(){

		for(int i=1; i<=3; i++) {
			for(int j=1; j<=4; j++) {
				String st = "DC"+i+".N"+j ;
				nodes.add(st) ;
			}
		}

		sfg = new SFG(nodes.size(), nodes) ;

		Complex gain = exp(-j*phi_rad/2.0)*Math.pow(L, 0.25) ;

		sfg.addArrow("DC1.N1", "DC1.N2", tin);
		sfg.addArrow("DC1.N1", "DC1.N3", -j*kin);
		sfg.addArrow("DC1.N4", "DC1.N3", tin);
		sfg.addArrow("DC1.N4", "DC1.N2", -j*kin);

		sfg.addArrow("DC2.N2", "DC2.N1", tmiddle);
		sfg.addArrow("DC2.N2", "DC2.N4", -j*kmiddle);
		sfg.addArrow("DC2.N3", "DC2.N4", tmiddle);
		sfg.addArrow("DC2.N3", "DC2.N1", -j*kmiddle);

		sfg.addArrow("DC1.N3", "DC2.N2", gain);
		sfg.addArrow("DC2.N1", "DC1.N4", gain);

		sfg.addArrow("DC3.N1", "DC3.N2", tout);
		sfg.addArrow("DC3.N1", "DC3.N3", -j*kout);
		sfg.addArrow("DC3.N4", "DC3.N3", tout);
		sfg.addArrow("DC3.N4", "DC3.N2", -j*kout);

		sfg.addArrow("DC2.N4", "DC3.N1", gain);
		sfg.addArrow("DC3.N2", "DC2.N3", gain);
	}

	public Complex getS31(){
		sfg.buildForwardPaths("DC1.N1", "DC3.N3");
		return sfg.computeForwardGain().divides(sfg.computeDelta()) ;
	}

	public Complex getS21(){
		sfg.buildForwardPaths("DC1.N1", "DC1.N2");
		return sfg.computeForwardGain().divides(sfg.computeDelta()) ;
	}

	public void buildDrop(){
		sfg.buildForwardPaths("DC1.N1", "DC3.N3");
	}

	public void buildThru(){
		sfg.buildForwardPaths("DC1.N1", "DC1.N2");
	}

	// for test
	public static void main(String[] args){
		AddDropSecondOrder adr = new AddDropSecondOrder(0, 1, 0.1, 0.1, 0.1) ;
		System.out.println(adr.sfg.printAllLoops_compactForm());
	}

}
