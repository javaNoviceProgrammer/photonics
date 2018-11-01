package photonics.slab;

import ch.epfl.general_libraries.clazzes.ParamName;
import mathLib.matrix.ComplexMatrix;
import mathLib.numbers.Complex;
import photonics.transfer.TransferMatrixTE;
import photonics.util.Wavelength;

public class ProfileCoupledSlabWgTE {

	Complex plusJ = new Complex(0,1), minusJ = new Complex(0,-1), one = new Complex(1,0), zero = new Complex(0,0) ;
	
	double neff, xValNm, k0 ;
	int modeNumber ;
	CoupledSlabWg coupledSlab ;
	TransferMatrixTE Q1, Q2, Q3, Q4 ;
	ModeCoupledSlabWgTE modeTE ;
	
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
	public ProfileCoupledSlabWgTE(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Coupled Slab Structure") CoupledSlabWg coupledSlab,
			@ParamName(name="Mode Number (0, 1, 2, ...)") int modeNumber,
			@ParamName(name="Even or Odd mode? (Even = TRUE, Odd = FALSE)") boolean isEvenMode,
			@ParamName(name="Position (nm)") double xValNm
			){
		this.xValNm = xValNm ;
		this.modeNumber = modeNumber ;
		this.coupledSlab = coupledSlab ;
		modeTE = new ModeCoupledSlabWgTE(inputLambda, coupledSlab) ;
		if(isEvenMode){
			neff = modeTE.findNeffEven(modeNumber);
		}
		else{
			neff = modeTE.findNeffOdd(modeNumber);
		}
		Q1 = new TransferMatrixTE(inputLambda, coupledSlab.n_up, coupledSlab.n_core1, neff, 0, 0) ;
		Q2 = new TransferMatrixTE(inputLambda, coupledSlab.n_core1, coupledSlab.n_gap, neff, coupledSlab.w1_nm, 0) ;
		Q3 = new TransferMatrixTE(inputLambda, coupledSlab.n_gap, coupledSlab.n_core2, neff, coupledSlab.w1_nm+coupledSlab.gap_nm, 0) ;
		Q4 = new TransferMatrixTE(inputLambda, coupledSlab.n_core2, coupledSlab.n_down, neff, coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm, 0) ;
		k0 = inputLambda.getK0() ;
	}
	
	public ProfileCoupledSlabWgTE(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Coupled Slab Structure") CoupledSlabWg coupledSlab,
			@ParamName(name="neff") double neff,
			@ParamName(name="Position (nm)") double xValNm
			){
		this.xValNm = xValNm ;
		this.coupledSlab = coupledSlab ;
		this.neff = neff ;
		modeTE = new ModeCoupledSlabWgTE(inputLambda, coupledSlab) ;
		Q1 = new TransferMatrixTE(inputLambda, coupledSlab.n_up, coupledSlab.n_core1, neff, 0, 0) ;
		Q2 = new TransferMatrixTE(inputLambda, coupledSlab.n_core1, coupledSlab.n_gap, neff, coupledSlab.w1_nm, 0) ;
		Q3 = new TransferMatrixTE(inputLambda, coupledSlab.n_gap, coupledSlab.n_core2, neff, coupledSlab.w1_nm+coupledSlab.gap_nm, 0) ;
		Q4 = new TransferMatrixTE(inputLambda, coupledSlab.n_core2, coupledSlab.n_down, neff, coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm, 0) ;
		k0 = inputLambda.getK0() ;
	}
	
	public double getNeff(){
		return neff ;
	}
	
	public double getPositionNm(){
		return xValNm ;
	}
	
	// Now finding the electric field Ey in each region

	private ComplexMatrix find_Ey_amps_up(){
		Complex amp_forward = new Complex(0,0) ;
		Complex amp_backward = new Complex(1,0) ; 
		Complex[][] amps = new Complex[][] {{amp_forward}, {amp_backward}} ;
		return new ComplexMatrix(amps) ;
	}
	
	private Complex find_Ey_field_up(double xValNm){
		Complex amp_forward = find_Ey_amps_up().getElement(0, 0) ;
		Complex amp_backward = find_Ey_amps_up().getElement(1, 0) ; 
		Complex kx = Q1.getKnormalFirst() ;
		Complex Ey_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Ey_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Ey = Ey_forward.plus(Ey_backward) ;
//		return Ey ;
		return new Complex(Ey.re(), 0) ;
	}
	
	private ComplexMatrix find_Ey_amps_core1(){
		return  Q1.getTransferMatrix().times(find_Ey_amps_up()) ;
	}
	
	private Complex find_Ey_field_core1(double xValNm){
		Complex amp_forward = find_Ey_amps_core1().getElement(0, 0) ;
		Complex amp_backward = find_Ey_amps_core1().getElement(1, 0) ; 
		Complex kx = Q1.getKnormalSecond() ;
		Complex Ey_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Ey_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Ey = Ey_forward.plus(Ey_backward) ;
//		return Ey ;
		return new Complex(Ey.re(), 0) ;
	}
	
	private ComplexMatrix find_Ey_amps_gap(){
		return  Q2.getTransferMatrix().times(find_Ey_amps_core1()) ;
	}
	
	private Complex find_Ey_field_gap(double xValNm){
		Complex amp_forward = find_Ey_amps_gap().getElement(0, 0) ;
		Complex amp_backward = find_Ey_amps_gap().getElement(1, 0) ; 
		Complex kx = Q2.getKnormalSecond() ;
		Complex Ey_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Ey_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Ey = Ey_forward.plus(Ey_backward) ;
//		return Ey ;
		return new Complex(Ey.re(), 0) ;
	}
	
	private ComplexMatrix find_Ey_amps_core2(){
		return  Q3.getTransferMatrix().times(find_Ey_amps_gap()) ;
	}
	
	private Complex find_Ey_field_core2(double xValNm){
		Complex amp_forward = find_Ey_amps_core2().getElement(0, 0) ;
		Complex amp_backward = find_Ey_amps_core2().getElement(1, 0) ; 
		Complex kx = Q3.getKnormalSecond() ;
		Complex Ey_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Ey_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Ey = Ey_forward.plus(Ey_backward) ;
//		return Ey ;
		return new Complex(Ey.re(), 0) ;
	}
	
	private ComplexMatrix find_Ey_amps_down(){
		return  Q4.getTransferMatrix().times(find_Ey_amps_core2()) ;
	}
	
	private Complex find_Ey_field_down(double xValNm){
		Complex amp_forward = find_Ey_amps_down().getElement(0, 0) ;
		Complex amp_backward = zero ; 
		Complex kx = Q4.getKnormalSecond() ;
		Complex Ey_forward = kx.times(minusJ).times(xValNm*1e-9).exp().times(amp_forward) ;
		Complex Ey_backward = kx.times(plusJ).times(xValNm*1e-9).exp().times(amp_backward) ;
		Complex Ey = Ey_forward.plus(Ey_backward) ;
//		return Ey ;
		return new Complex(Ey.re(), 0) ;
	}
	
	public Complex get_Ey_field(){
		if(xValNm<0){
			return find_Ey_field_up(xValNm) ;
		}
		else if(xValNm>=0 && xValNm < coupledSlab.w1_nm){
			return find_Ey_field_core1(xValNm) ;
		}
		else if(xValNm>=coupledSlab.w1_nm && xValNm < coupledSlab.w1_nm + coupledSlab.gap_nm) {
			return find_Ey_field_gap(xValNm) ;
		}
		else if(xValNm>=coupledSlab.w1_nm + coupledSlab.gap_nm && xValNm < coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm){
			return find_Ey_field_core2(xValNm) ;
		}
		else{
			return find_Ey_field_down(xValNm) ;
		}
	}
	
	// Now Ex and Ez Components (Ex = 0 and Ez = 0 for TE mode)
	public Complex get_Ex_field(){
		return new Complex(0,0) ;
	}
	
	public Complex get_Ez_field(){
		return new Complex(0,0) ;
	}
	// Hy = 0 for TE mode
	public Complex get_Hy_field(){
		return new Complex(0,0) ;
	}
	
	// Hx component
	public Complex get_Hx_field(){
		Complex coeff = new Complex(-neff/(120*Math.PI), 0) ;
		Complex Hx = get_Ey_field().times(coeff) ;
		return Hx ;
	}
	
	// Hz component
	
	private Complex find_Hz_field_up(){
		Complex coeff = new Complex(0, 1/(k0*120*Math.PI)) ;
		Complex Hz_amp_forward = find_Ey_amps_up().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Hz_amp_backward = find_Ey_amps_up().getElement(1, 0).times(plusJ.times(getKx())).times(coeff) ;
		Complex Hz_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Hz_amp_forward) ;
		Complex Hz_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Hz_amp_backward) ;
		Complex Hz = Hz_forward.plus(Hz_backward) ;
//		return Hz ;
		return new Complex(0, Hz.im()) ;
	}
	
	private Complex find_Hz_field_core1(){
		Complex coeff = new Complex(0, 1/(k0*120*Math.PI)) ;
		Complex Hz_amp_forward = find_Ey_amps_core1().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Hz_amp_backward = find_Ey_amps_core1().getElement(1, 0).times(plusJ.times(getKx())).times(coeff) ;
		Complex Hz_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Hz_amp_forward) ;
		Complex Hz_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Hz_amp_backward) ;
		Complex Hz = Hz_forward.plus(Hz_backward) ;
//		return Hz ;
		return new Complex(0, Hz.im()) ;
	}
	
	private Complex find_Hz_field_gap(){
		Complex coeff = new Complex(0, 1/(k0*120*Math.PI)) ;
		Complex Hz_amp_forward = find_Ey_amps_gap().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Hz_amp_backward = find_Ey_amps_gap().getElement(1, 0).times(plusJ.times(getKx())).times(coeff) ;
		Complex Hz_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Hz_amp_forward) ;
		Complex Hz_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Hz_amp_backward) ;
		Complex Hz = Hz_forward.plus(Hz_backward) ;
//		return Hz ;
		return new Complex(0, Hz.im()) ;
	}
	
	private Complex find_Hz_field_core2(){
		Complex coeff = new Complex(0, 1/(k0*120*Math.PI)) ;
		Complex Hz_amp_forward = find_Ey_amps_core2().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Hz_amp_backward = find_Ey_amps_core2().getElement(1, 0).times(plusJ.times(getKx())).times(coeff) ;
		Complex Hz_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Hz_amp_forward) ;
		Complex Hz_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Hz_amp_backward) ;
		Complex Hz = Hz_forward.plus(Hz_backward) ;
//		return Hz ;
		return new Complex(0, Hz.im()) ;
	}
	
	private Complex find_Hz_field_down(){
		Complex coeff = new Complex(0, 1/(k0*120*Math.PI)) ;
		Complex Hz_amp_forward = find_Ey_amps_down().getElement(0, 0).times(minusJ.times(getKx())).times(coeff) ;
		Complex Hz_amp_backward = zero ;
		Complex Hz_forward = getKx().times(minusJ).times(xValNm*1e-9).exp().times(Hz_amp_forward) ;
		Complex Hz_backward = getKx().times(plusJ).times(xValNm*1e-9).exp().times(Hz_amp_backward) ;
		Complex Hz = Hz_forward.plus(Hz_backward) ;
//		return Hz ;
		return new Complex(0, Hz.im()) ;
	}
	
	public Complex get_Hz_field(){
		if(xValNm<0){
			return find_Hz_field_up() ;
		}
		else if(xValNm>=0 && xValNm < coupledSlab.w1_nm){
			return find_Hz_field_core1() ;
		}
		else if(xValNm>=coupledSlab.w1_nm && xValNm < coupledSlab.w1_nm + coupledSlab.gap_nm) {
			return find_Hz_field_gap() ;
		}
		else if(xValNm>=coupledSlab.w1_nm + coupledSlab.gap_nm && xValNm < coupledSlab.w1_nm+coupledSlab.gap_nm+coupledSlab.w2_nm){
			return find_Hz_field_core2() ;
		}
		else{
			return find_Hz_field_down() ;
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
	
	
}
