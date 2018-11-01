package photonics.slab;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.matrix.ComplexMatrix;
import mathLib.numbers.Complex;
import photonics.transfer.TransferMatrixTM;
import photonics.util.Wavelength;

public class ProfileCoupledSlabWgTM {

	Complex plusJ = new Complex(0,1), minusJ = new Complex(0,-1), one = new Complex(1,0), zero = new Complex(0,0) ;
	
	double neff, xValNm, k0 ;
	int modeNumber ;
	CoupledSlabWg coupledSlab ;
	TransferMatrixTM Q1, Q2, Q3, Q4 ;
	ModeCoupledSlabWgTM modeTM ;
	
/*				n_up
		-----------------------
				n_core1
		-----------------------
				n_gap
		-----------------------
				n_core2
		-----------------------
				n_down
*/
	public ProfileCoupledSlabWgTM(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Coupled Slab Structure") CoupledSlabWg coupledSlab,
			@ParamName(name="Mode Number (0, 1, 2, ...)") int modeNumber,
			@ParamName(name="Even or Odd mode? (Even = TRUE, Odd = FALSE)") boolean isEvenMode,
			@ParamName(name="Position (nm)") double xValNm
			){
		this.xValNm = xValNm ;
		this.modeNumber = modeNumber ;
		this.coupledSlab = coupledSlab ;
		modeTM = new ModeCoupledSlabWgTM(inputLambda, coupledSlab) ;
		if(isEvenMode){
			neff = modeTM.findNeffEven(modeNumber);
		}
		else{
			neff = modeTM.findNeffOdd(modeNumber);
		}
		Q1 = new TransferMatrixTM(inputLambda, coupledSlab.n_up, coupledSlab.n_core1, neff, 0, 0) ;
		Q2 = new TransferMatrixTM(inputLambda, coupledSlab.n_core1, coupledSlab.n_gap, neff, coupledSlab.w1_nm, 0) ;
		Q3 = new TransferMatrixTM(inputLambda, coupledSlab.n_gap, coupledSlab.n_core2, neff, coupledSlab.w1_nm+coupledSlab.gap_nm, 0) ;
		Q4 = new TransferMatrixTM(inputLambda, coupledSlab.n_core2, coupledSlab.n_down, neff, coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm, 0) ;
		k0 = inputLambda.getK0() ;
	}
	
	public ProfileCoupledSlabWgTM(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Coupled Slab Structure") CoupledSlabWg coupledSlab,
			@ParamName(name="neff") double neff,
			@ParamName(name="Position (nm)") double xValNm
			){
		this.xValNm = xValNm ;
		this.coupledSlab = coupledSlab ;
		this.neff = neff ;
		modeTM = new ModeCoupledSlabWgTM(inputLambda, coupledSlab) ;
		Q1 = new TransferMatrixTM(inputLambda, coupledSlab.n_up, coupledSlab.n_core1, neff, 0, 0) ;
		Q2 = new TransferMatrixTM(inputLambda, coupledSlab.n_core1, coupledSlab.n_gap, neff, coupledSlab.w1_nm, 0) ;
		Q3 = new TransferMatrixTM(inputLambda, coupledSlab.n_gap, coupledSlab.n_core2, neff, coupledSlab.w1_nm+coupledSlab.gap_nm, 0) ;
		Q4 = new TransferMatrixTM(inputLambda, coupledSlab.n_core2, coupledSlab.n_down, neff, coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm, 0) ;
		k0 = inputLambda.getK0() ;
	}
	
	public double getNeff(){
		return neff ;
	}
	
	public double getPositionNm(){
		return xValNm ;
	}
	
	// Now finding the electric field Hy in each region

	private ComplexMatrix find_Hy_amps_up(){
		Complex amp_forward = new Complex(0,0) ;
		Complex amp_backward = new Complex(1,0) ; 
		Complex[][] amps = new Complex[][] {{amp_forward}, {amp_backward}} ;
		return new ComplexMatrix(amps) ;
	}
	
	private Complex find_Hy_field_up(){
		Complex amp_forward = find_Hy_amps_up().getElement(0, 0) ;
		Complex amp_backward = find_Hy_amps_up().getElement(1, 0) ; 
		Complex kx = getKx() ;
		Complex Hy_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Hy_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Hy = Hy_forward.plus(Hy_backward) ;
		return new Complex(Hy.re(), 0) ;
//		return Hy ;
	}
	
	private ComplexMatrix find_Hy_amps_core1(){
		return  Q1.getTransferMatrix().times(find_Hy_amps_up()) ;
	}
	
	private Complex find_Hy_field_core1(){
		Complex amp_forward = find_Hy_amps_core1().getElement(0, 0) ;
		Complex amp_backward = find_Hy_amps_core1().getElement(1, 0) ; 
		Complex kx = getKx() ;
		Complex Hy_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Hy_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Hy = Hy_forward.plus(Hy_backward) ;
		return new Complex(Hy.re(), 0) ;
//		return Hy ;
	}
	
	private ComplexMatrix find_Hy_amps_gap(){
		return  Q2.getTransferMatrix().times(find_Hy_amps_core1()) ;
	}
	
	private Complex find_Hy_field_gap(){
		Complex amp_forward = find_Hy_amps_gap().getElement(0, 0) ;
		Complex amp_backward = find_Hy_amps_gap().getElement(1, 0) ; 
		Complex kx = getKx() ;
		Complex Hy_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Hy_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Hy = Hy_forward.plus(Hy_backward) ;
		return new Complex(Hy.re(), 0) ;
//		return Hy ;
	}
	
	private ComplexMatrix find_Hy_amps_core2(){
		return  Q3.getTransferMatrix().times(find_Hy_amps_gap()) ;
	}
	
	private Complex find_Hy_field_core2(){
		Complex amp_forward = find_Hy_amps_core2().getElement(0, 0) ;
		Complex amp_backward = find_Hy_amps_core2().getElement(1, 0) ; 
		Complex kx = getKx() ;
		Complex Hy_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Hy_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Hy = Hy_forward.plus(Hy_backward) ;
		return new Complex(Hy.re(), 0) ;
//		return Hy ;
	}
	
	private ComplexMatrix find_Hy_amps_down(){
		return  Q4.getTransferMatrix().times(find_Hy_amps_core2()) ;
	}
	
	private Complex find_Hy_field_down(){
		Complex amp_forward = find_Hy_amps_down().getElement(0, 0) ;
		Complex amp_backward = zero ; 
		Complex kx = getKx() ;
		Complex Hy_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Hy_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Hy = Hy_forward.plus(Hy_backward) ;
		return new Complex(Hy.re(), 0) ;
//		return Hy ;
	}
	
	public Complex get_Hy_field(){
		if(xValNm<0){
			return find_Hy_field_up() ;
		}
		else if(xValNm>=0 && xValNm < coupledSlab.w1_nm){
			return find_Hy_field_core1() ;
		}
		else if(xValNm>=coupledSlab.w1_nm && xValNm < coupledSlab.w1_nm + coupledSlab.gap_nm) {
			return find_Hy_field_gap() ;
		}
		else if(xValNm>=coupledSlab.w1_nm + coupledSlab.gap_nm && xValNm < coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm){
			return find_Hy_field_core2() ;
		}
		else{
			return find_Hy_field_down() ;
		}
	}
	
	// Now Hx and Hz Components (Hx = 0 and Hz = 0 for TM mode)
	public Complex get_Hx_field(){
		return new Complex(0,0) ;
	}
	
	public Complex get_Hz_field(){
		return new Complex(0,0) ;
	}
	
	// Ey = 0 for TM
	public Complex get_Ey_field(){
		return new Complex(0,0) ;
	}
	
	// Ex component
	public Complex get_Ex_field(){
		Complex coeff = new Complex(neff*(120*Math.PI)/(getIndex()*getIndex()) , 0) ;
		Complex Ex = get_Hy_field().times(coeff) ;
		return new Complex(Ex.re(), 0) ;
//		return Ex ;
	}
	
	// Ez component
	private Complex find_Ez_field_up(){
		Complex coeff = new Complex(0, -120*Math.PI/(k0*getIndex()*getIndex())) ;
		Complex Ez_amp_forward = find_Hy_amps_up().getElement(0, 0).times(minusJ).times(getKx()).times(coeff) ;
		Complex Ez_amp_backward = find_Hy_amps_up().getElement(1, 0).times(plusJ).times(getKx()).times(coeff) ;
		Complex Ez_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Ez_amp_forward) ;
		Complex Ez_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Ez_amp_backward) ;
		Complex Ez = Ez_forward.plus(Ez_backward) ;
//		return Ez ;
		return new Complex(0, Ez.im()) ;
	}
	
	private Complex find_Ez_field_core1(){
		Complex coeff = new Complex(0, -120*Math.PI/(k0*getIndex()*getIndex())) ;
		Complex Ez_amp_forward = find_Hy_amps_core1().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Ez_amp_backward = find_Hy_amps_core1().getElement(1, 0).times(plusJ.times(getKx())).times(coeff) ;
		Complex Ez_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Ez_amp_forward) ;
		Complex Ez_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Ez_amp_backward) ;
		Complex Ez = Ez_forward.plus(Ez_backward) ;
//		return Ez ;
		return new Complex(0, Ez.im()) ;
	}
	
	private Complex find_Ez_field_gap(){
		Complex coeff = new Complex(0, -120*Math.PI/(k0*getIndex()*getIndex())) ;
		Complex Ez_amp_forward = find_Hy_amps_gap().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Ez_amp_backward = find_Hy_amps_gap().getElement(1, 0).times(plusJ.times(getKx())).times(coeff) ;
		Complex Ez_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Ez_amp_forward) ;
		Complex Ez_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Ez_amp_backward) ;
		Complex Ez = Ez_forward.plus(Ez_backward) ;
//		return Ez ;
		return new Complex(0, Ez.im()) ;
	}
	
	private Complex find_Ez_field_core2(){
		Complex coeff = new Complex(0, -120*Math.PI/(k0*getIndex()*getIndex())) ;
		Complex Ez_amp_forward = find_Hy_amps_core2().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Ez_amp_backward = find_Hy_amps_core2().getElement(1, 0).times(plusJ.times(getKx())).times(coeff) ;
		Complex Ez_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Ez_amp_forward) ;
		Complex Ez_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Ez_amp_backward) ;
		Complex Ez = Ez_forward.plus(Ez_backward) ;
//		return Ez ;
		return new Complex(0, Ez.im()) ;
	}
	
	private Complex find_Ez_field_down(){
		Complex coeff = new Complex(0, -120*Math.PI/(k0*getIndex()*getIndex())) ;
		Complex Ez_amp_forward = find_Hy_amps_down().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Ez_amp_backward = zero ;
		Complex Ez_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Ez_amp_forward) ;
		Complex Ez_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Ez_amp_backward) ;
		Complex Ez = Ez_forward.plus(Ez_backward) ;
//		return Ez ;
		return new Complex(0, Ez.im()) ;
	}
	
	public Complex get_Ez_field(){
		if(xValNm<0){
			return find_Ez_field_up() ;
		}
		else if(xValNm>=0 && xValNm < coupledSlab.w1_nm){
			return find_Ez_field_core1() ;
		}
		else if(xValNm>=coupledSlab.w1_nm && xValNm < coupledSlab.w1_nm + coupledSlab.gap_nm) {
			return find_Ez_field_gap() ;
		}
		else if(xValNm>=coupledSlab.w1_nm + coupledSlab.gap_nm && xValNm < coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm){
			return find_Ez_field_core2() ;
		}
		else{
			return find_Ez_field_down() ;
		}
	}
	
	private Complex getKx(){
		if(xValNm<0){
			return Q1.getKnormalFirst() ;
		}
		else if(xValNm>=0 && xValNm < coupledSlab.w1_nm){
			return Q1.getKnormalSecond() ;
		}
		else if(xValNm>=coupledSlab.w1_nm && xValNm < coupledSlab.w1_nm + coupledSlab.gap_nm) {
			return Q2.getKnormalSecond() ;
		}
		else if(xValNm>=coupledSlab.w1_nm + coupledSlab.gap_nm && xValNm < coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm){
			return Q3.getKnormalSecond() ;
		}
		else{
			return Q4.getKnormalSecond() ;
		}
	}
	
	private double getIndex(){
		if(xValNm<0){
			return coupledSlab.n_up ;
		}
		else if(xValNm>=0 && xValNm < coupledSlab.w1_nm){
			return coupledSlab.n_core1 ;
		}
		else if(xValNm>=coupledSlab.w1_nm && xValNm < coupledSlab.w1_nm + coupledSlab.gap_nm) {
			return coupledSlab.n_gap ;
		}
		else if(xValNm>=coupledSlab.w1_nm + coupledSlab.gap_nm && xValNm < coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm){
			return coupledSlab.n_core2 ;
		}
		else{
			return coupledSlab.n_down ;
		}
	}
	
	
}
