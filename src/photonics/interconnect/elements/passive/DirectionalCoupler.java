package photonics.interconnect.elements.passive;

import static mathLib.numbers.Complex.*;
import static mathLib.numbers.ComplexMath.*;
import java.util.ArrayList;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;
import mathLib.numbers.Complex;
import mathLib.sfg.numeric.SFG;
import photonics.interconnect.elements.AbstractElement;
import photonics.interconnect.modes.NeffCoupled;
import photonics.util.Wavelength;

public class DirectionalCoupler extends AbstractElement {

	Wavelength inputLambda = null ;
	public double length, gap, alphaDbPerCm ;
	private double gapNm ;
	NeffCoupled neffCoupled ;
	Complex t, kappa ;
	
	public Complex s11, s12, s13, s14 ;
	public Complex s21, s22, s23, s24 ;
	public Complex s31, s32, s33, s34 ;
	public Complex s41, s42, s43, s44 ;

	public DirectionalCoupler(
			@ParamName(name="Element Name") String name,
			@ParamName(name="Coupled Mode") NeffCoupled neff,
			@ParamName(name="Length (um)") double length,
			@ParamName(name="Gap (um)") double gap,
			@ParamName(name="Loss (dB/cm)") double alphaDbPerCm
			) {
		this.name = name ;
		this.neffCoupled = neff ;
		this.length = length ;
		this.gap = gap ;
		this.gapNm = gap*1e3 ;
		this.alphaDbPerCm = alphaDbPerCm ;
	}
	
	public void setName(String name) {
		this.name = name ;
	}

	@Override
	public void setWavelength(Wavelength inputLambda) {
		this.inputLambda = inputLambda ;

	}

	@Override
	public void buildElement() {
		
		if(inputLambda == null)
			throw new NullPointerException("wavelength is not set for " + name) ;
		
		double lambdaNm = inputLambda.getWavelengthNm() ;
		double nEven = neffCoupled.getNeffEven(lambdaNm, gapNm) ;
		double nOdd = neffCoupled.getNeffOdd(lambdaNm, gapNm) ;
		Complex betaPlus = 2*PI/(lambdaNm*1e-9)*(nEven+nOdd)/2.0  - j * alphaDbPerCm*23.0/2.0 ;
		Complex betaMinus = 2*PI/(lambdaNm*1e-9)*(nEven-nOdd)/2.0 ;
		t = exp(-j*betaPlus*length*1e-6)*cos(betaMinus*length*1e-6) ;
		kappa = exp(-j*betaPlus*length*1e-6)*sin(betaMinus*length*1e-6) ;
		
		s21 = s12 = s34 = s43 = t ;
		s31 = s13 = s24 = s42 = -j*kappa ;

		String port1_in = name+".port1.in" ;
		String port1_out = name+".port1.out" ;
		String port2_in = name+".port2.in" ;
		String port2_out = name+".port2.out" ;
		String port3_in = name+".port3.in" ;
		String port3_out = name+".port3.out" ;
		String port4_in = name+".port4.in" ;
		String port4_out = name+".port4.out" ;
		
		nodes = new ArrayList<>() ;
		nodes.add(port1_in) ;
		nodes.add(port1_out) ;
		nodes.add(port2_in) ;
		nodes.add(port2_out) ;
		nodes.add(port3_in) ;
		nodes.add(port3_out) ;
		nodes.add(port4_in) ;
		nodes.add(port4_out) ;
		
		sfgElement = new SFG(nodes) ;

		sfgElement.addArrow(port1_in, port1_out, s11);
		sfgElement.addArrow(port1_in, port2_out, s21);
		sfgElement.addArrow(port1_in, port3_out, s31);
		sfgElement.addArrow(port1_in, port4_out, s41);

		sfgElement.addArrow(port2_in, port1_out, s12);
		sfgElement.addArrow(port2_in, port2_out, s22);
		sfgElement.addArrow(port2_in, port3_out, s32);
		sfgElement.addArrow(port2_in, port4_out, s42);

		sfgElement.addArrow(port3_in, port1_out, s13);
		sfgElement.addArrow(port3_in, port2_out, s23);
		sfgElement.addArrow(port3_in, port3_out, s33);
		sfgElement.addArrow(port3_in, port4_out, s43);

		sfgElement.addArrow(port4_in, port1_out, s14);
		sfgElement.addArrow(port4_in, port2_out, s24);
		sfgElement.addArrow(port4_in, port3_out, s34);
		sfgElement.addArrow(port4_in, port4_out, s44);

	}


	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = new SimpleMap<String, String>() ;
		map.put(name+".kappa", kappa+"") ;
		map.put(name+".t", t+"") ;
		map.put(name+".length (um)", length+"") ;
		map.put(name+".gap (nm)", gapNm+"") ;
		map.put(name+".gap (um)", gap+"") ;
		return map ;
	}

}
