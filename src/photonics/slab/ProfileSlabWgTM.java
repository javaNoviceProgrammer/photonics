package photonics.slab;

import mathLib.numbers.Complex;

public class ProfileSlabWgTM {

	SlabWg slab ;
	ModeSlabWgTM slabTM ;
	double widthNm, neff, n_up, n_down, n_core, k0, XpositionNm ;
	int modeNumber ;
	Complex one = new Complex(1,0), zero = new Complex(0,0), minusJ = new Complex(0,-1), plusJ = new Complex(0,1) ;
	
	// First I need to define the constructor for the geometry
	public ProfileSlabWgTM(
			SlabWg slab, 
			double XpositionNm,
			int modeNumber
			){
		this.slab = slab ;
		this.widthNm = slab.getWidthNm() ;
		this.slabTM = new ModeSlabWgTM(slab) ;
		this.neff = slabTM.findSpecificModeIndex(modeNumber) ;
		this.n_core = slab.getCoreIndex() ;
		this.n_down = slab.getSubstrateIndex() ;
		this.n_up = slab.getCladIndex() ;
		this.k0 = 2*Math.PI/(slab.getWavelengthNm()*1e-9) ;
		this.XpositionNm = XpositionNm ;
	}
	
	public ProfileSlabWgTM(
			SlabWg slab, 
			double XpositionNm,
			double neff
			){
		this.slab = slab ;
		this.widthNm = slab.getWidthNm() ;
		this.slabTM = new ModeSlabWgTM(slab) ;
		this.neff = neff ;
		this.n_core = slab.getCoreIndex() ;
		this.n_down = slab.getSubstrateIndex() ;
		this.n_up = slab.getCladIndex() ;
		this.k0 = 2*Math.PI/(slab.getWavelengthNm()*1e-9) ;
		this.XpositionNm = XpositionNm ;
	}
	
	// now I need to calculate normal index of each area (n_x)
	private Complex kx_up(){
		double A = Math.sqrt(neff*neff - n_up * n_up) ;
		return new Complex(0, -1*A*k0) ;
	}
	
	private Complex kx_core(){
		double A = Math.sqrt(n_core * n_core - neff * neff) ;
		return new Complex(A*k0, 0) ;
	}
	
	private Complex kx_down(){
		double A = Math.sqrt(neff*neff - n_down * n_down) ;
		return new Complex(0, -1*A*k0) ;
	}
	
	// Now finding the only component of electric field (Ey)
	
	// ************ Hy component ****************
	private Complex[] find_Hy_fields_down(double xValNm){
		// forward wave
		Complex amp_forward = new Complex(0,0) ;
		Complex Hy_field_forward = (minusJ.times(kx_down()).times(xValNm*1e-9).exp().times(amp_forward)) ; // forward wave: -j*k_x
		// backward wave
		Complex amp_backward = new Complex(1,0) ; 
		Complex Hy_field_backward = (plusJ.times(kx_down()).times(xValNm*1e-9).exp().times(amp_backward)) ; // backward wave: +j*k_x
		return new Complex[] {Hy_field_forward, Hy_field_backward} ;
	}
	
	private Complex find_Hy_field_down(double xValNm){
		Complex[] Hy_down = find_Hy_fields_down(xValNm) ;
		return (Hy_down[0].plus(Hy_down[1])); // summing up forward and backward waves
	}
	
	private Complex[] find_Hy_fields_core(double xValNm){
		Complex A_plus_B = find_Hy_field_down(0) ;
		Complex A_minus_B = find_Hy_field_down(0).times(plusJ.times(kx_down())).divides(minusJ.times(kx_core())).times((n_core*n_core)/(n_down*n_down)) ;
		// forward wave
		Complex amp_forward = (A_plus_B).plus(A_minus_B).divides(2) ;
		Complex Hy_forward = minusJ.times(kx_core()).times(xValNm*1e-9).exp().times(amp_forward) ;
		// backward wave
		Complex amp_backward = (A_plus_B).minus(A_minus_B).divides(2) ;
		Complex Hy_backward = plusJ.times(kx_core()).times(xValNm*1e-9).exp().times(amp_backward) ;
		return new Complex[] {Hy_forward, Hy_backward} ;
	}
	
	private Complex find_Hy_field_core(double xValNm){
		Complex[] Hy_core = find_Hy_fields_core(xValNm) ;
		return (Hy_core[0].plus(Hy_core[1])); // summing up forward and backward waves
	}
	
	private Complex[] find_Hy_fields_up(double xValNm){
		// forward wave
		Complex amp_forward = find_Hy_field_core(widthNm).divides(minusJ.times(kx_up()).times(widthNm*1e-9).exp()) ;
		Complex Hy_field_forward = (minusJ.times(kx_down()).times(xValNm*1e-9).exp().times(amp_forward)) ; // forward wave: -j*k_x
		// backward wave
		Complex amp_backward = new Complex(0,0) ; 
		Complex Hy_field_backward = (plusJ.times(kx_down()).times(xValNm*1e-9).exp().times(amp_backward)) ; // backward wave: +j*k_x
		return new Complex[] {Hy_field_forward, Hy_field_backward} ;
	}
	
	private Complex find_Hy_field_up(double xValNm){
		Complex[] Hy_up = find_Hy_fields_up(xValNm) ;
		return (Hy_up[0].plus(Hy_up[1])); // summing up forward and backward waves
	}
	
	public Complex get_Hy_field(){
		double xValNm = XpositionNm ;
		if(xValNm<0){
			return find_Hy_field_down(xValNm) ;
		}
		else if(xValNm>=0 && xValNm < widthNm){
			return find_Hy_field_core(xValNm) ;
		}
		else{
			return find_Hy_field_up(xValNm) ;
		}
	}
	// ************ Hx component ****************
	public Complex get_Hx_field(){
		return new Complex(0,0) ;
	}
	// ************ Hz component ****************
	public Complex get_Hz_field(){
		return new Complex(0,0) ;
	}
	// ************ Ey component ****************
	public Complex get_Ey_field(){
		return new Complex(0,0) ;
	}
	// ************ Ex component ****************
	private Complex find_Ex_field_up(double xValNm){
		Complex coeff = new Complex(neff*(120*Math.PI)/(n_up*n_up) , 0) ;
		Complex Ex_up = find_Hy_field_up(xValNm).times(coeff) ;
		return Ex_up ;
	}
	
	private Complex find_Ex_field_down(double xValNm){
		Complex coeff = new Complex(neff*(120*Math.PI)/(n_down*n_down) , 0) ;
		Complex Ex_down = find_Hy_field_down(xValNm).times(coeff) ;
		return Ex_down ;
	}
	
	private Complex find_Ex_field_core(double xValNm){
		Complex coeff = new Complex(neff*(120*Math.PI)/(n_core*n_core) , 0) ;
		Complex Ex_core = find_Hy_field_core(xValNm).times(coeff) ;
		return Ex_core ;
	}
	
	public Complex get_Ex_field(){
		double xValNm = XpositionNm ;
		if(xValNm<0){
			return find_Ex_field_down(xValNm) ;
		}
		else if(xValNm>=0 && xValNm < widthNm){
			return find_Ex_field_core(xValNm) ;
		}
		else{
			return find_Ex_field_up(xValNm) ;
		}
	}
	// ************ Ez component ****************
	private Complex find_Ez_field_down(double xValNm){
		Complex coeff = new Complex(0, -120*Math.PI/(k0*n_down*n_down)) ;
		Complex Ez_forward = find_Hy_fields_down(xValNm)[0].times(minusJ.times(kx_down())).times(coeff) ;
		Complex Ez_backward = find_Hy_fields_down(xValNm)[1].times(plusJ.times(kx_down())).times(coeff) ;
		return (Ez_forward.plus(Ez_backward)) ;
	}
	
	private Complex find_Ez_field_up(double xValNm){
		Complex coeff = new Complex(0, -120*Math.PI/(k0*n_up*n_up)) ;
		Complex Ez_forward = find_Hy_fields_up(xValNm)[0].times(minusJ.times(kx_up())).times(coeff) ;
		Complex Ez_backward = find_Hy_fields_up(xValNm)[1].times(plusJ.times(kx_up())).times(coeff) ;
		return (Ez_forward.plus(Ez_backward)) ;
	}
	
	private Complex find_Ez_field_core(double xValNm){
		Complex coeff = new Complex(0, -120*Math.PI/(k0*n_core*n_core)) ;
		Complex Ez_forward = find_Hy_fields_core(xValNm)[0].times(minusJ.times(kx_core())).times(coeff) ;
		Complex Ez_backward = find_Hy_fields_core(xValNm)[1].times(plusJ.times(kx_core())).times(coeff) ;
		return (Ez_forward.plus(Ez_backward)) ;
	}
	
	public Complex get_Ez_field(){
		double xValNm = XpositionNm ;
		if(xValNm<0){
			return find_Ez_field_down(xValNm) ;
		}
		else if(xValNm>=0 && xValNm < widthNm){
			return find_Ez_field_core(xValNm) ;
		}
		else{
			return find_Ez_field_up(xValNm) ;
		}
	}
	
}
