package photonics.slab;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.matrix.ComplexMatrix;
import mathLib.numbers.Complex;
import photonics.transfer.TransferMatrixTM;
import photonics.util.Wavelength;

public class ModeCoupledSlabWgTM_fast {

	Complex plusJ = new Complex(0,1), minusJ = new Complex(0,-1), one = new Complex(1,0), zero = new Complex(0,0) ;

	Wavelength inputLambda ;
	double w1_nm, w2_nm, gap_nm , lambdaNm , k0;
	double n_down, n_core1, n_core2, n_up, n_gap, n_high, n_low ;
	double neffSlabTM_min, neffSlabTM_max ;
	double[] neff_AllModes ;

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

	// First type of constructor for directly inputing parameters of the slab
	public ModeCoupledSlabWgTM_fast(
			@ParamName(name="wavelength (nm)") Wavelength inputLambda,
			@ParamName(name="Coupled Slab Structure") CoupledSlabWg coupledSlab
			){
		this.inputLambda = inputLambda ;
		this.k0 = 2*Math.PI/(lambdaNm*1e-9) ;
		this.w1_nm = coupledSlab.w1_nm ;
		this.w2_nm = coupledSlab.w2_nm ;
		this.gap_nm = coupledSlab.gap_nm ;
		this.n_up = coupledSlab.n_up ;
		this.n_core1 = coupledSlab.n_core1 ;
		this.n_gap = coupledSlab.n_gap;
		this.n_core2 = coupledSlab.n_core2 ;
		this.n_down = coupledSlab.n_down ;
		this.n_high = coupledSlab.n_high ;
		this.n_low = coupledSlab.n_low ;

	}

	public ModeCoupledSlabWgTM_fast(
			@ParamName(name="wavelength (nm)") double lambdaNm,
			@ParamName(name="waveguide 1 width (nm)") double w1_nm,
			@ParamName(name="waveguide 2 width (nm)") double w2_nm,
			@ParamName(name="gap size (nm)") double gap_nm,
			@ParamName(name="up index") double n_u,
			@ParamName(name="core 1 index") double n_c_1,
			@ParamName(name="gap index") double n_g,
			@ParamName(name="core 2 index") double n_c_2,
			@ParamName(name="down index") double n_d
			){
		this.lambdaNm = lambdaNm ;
		this.inputLambda = new Wavelength(lambdaNm) ;
		this.k0 = 2*Math.PI/(lambdaNm*1e-9) ;
		this.w1_nm = w1_nm ;
		this.w2_nm = w2_nm ;
		this.gap_nm = gap_nm ;
		this.n_up = n_u ;
		this.n_core1 = n_c_1 ;
		this.n_gap = n_g ;
		this.n_core2 = n_c_2 ;
		this.n_down = n_d ;
		this.n_high = Math.min(n_c_1, n_c_2) ;
		this.n_low = Math.max(Math.max(n_u, n_d), n_g) ;
		
	}

	// Next I need to find the Even mode equation (Y1 + Y2 = 0) 
	private double getModeEquation(double neff){
		TransferMatrixTM Q1 = new TransferMatrixTM(inputLambda, n_up, n_core1, neff, 0, 0) ;
		TransferMatrixTM Q2 = new TransferMatrixTM(inputLambda, n_core1, n_gap, neff, w1_nm, 0) ;
		TransferMatrixTM Q3 = new TransferMatrixTM(inputLambda, n_gap, n_core2, neff, w1_nm+gap_nm, 0) ;
		TransferMatrixTM Q4 = new TransferMatrixTM(inputLambda, n_core2, n_down, neff, w1_nm+gap_nm+w2_nm, 0) ;
		ComplexMatrix T1 = Q1.getTransferMatrix() ;
		ComplexMatrix T2 = Q2.getTransferMatrix() ;
		ComplexMatrix T3 = Q3.getTransferMatrix() ;
		ComplexMatrix T4 = Q4.getTransferMatrix() ;
		ComplexMatrix Ttot = T4.times(T3).times(T2).times(T1) ;
		return Ttot.getElement(1, 1).re() ;	
	}	

	// mode number starts from 0 --> TE0, TE1, TE2, ...
	@SuppressWarnings("unused")
	public double findNeffEven(int modeNumber){
		RealRootFunction func = new RealRootFunction() {
			@Override
			public double function(double neff) {
				return getModeEquation(neff);
			}
		};
		RealRoot rootFinder = new RealRoot() ;

		SlabWg slab1 = new SlabWg(inputLambda, w1_nm, n_gap, n_core1, n_up) ;
		SlabWg slab2 = new SlabWg(inputLambda, w2_nm, n_down, n_core2, n_gap) ;
		double neffTM1 = new ModeSlabWgTM(slab1).findSpecificModeIndex(modeNumber) ;
		double neffTM2 = new ModeSlabWgTM(slab2).findSpecificModeIndex(modeNumber) ;
		double neffSlabTM_min = Math.min(neffTM1, neffTM2) ;
		double neffSlabTM_max = Math.max(neffTM1, neffTM2) ;

		rootFinder.setEstimate(neffSlabTM_max);
		return rootFinder.bisect(func, neffSlabTM_max, n_high) ;
	}
	
	public double findNeffOdd(int modeNumber){
		RealRootFunction func = new RealRootFunction() {
			@Override
			public double function(double neff) {
				return getModeEquation(neff);
			}
		};
		RealRoot rootFinder = new RealRoot() ;

		SlabWg slab1 = new SlabWg(inputLambda, w1_nm, n_gap, n_core1, n_up) ;
		SlabWg slab2 = new SlabWg(inputLambda, w2_nm, n_down, n_core2, n_gap) ;
		double neffTM1 = new ModeSlabWgTM(slab1).findSpecificModeIndex(modeNumber) ;
		double neffTM2 = new ModeSlabWgTM(slab2).findSpecificModeIndex(modeNumber) ;
		double neffSlabTM_min = Math.min(neffTM1, neffTM2) ;
		double neffSlabTM_max = Math.max(neffTM1, neffTM2) ;

		double r = Double.NaN ;
		if(w1_nm == w2_nm){
			rootFinder.setEstimate(2*neffSlabTM_max-findNeffEven(modeNumber));
			r = rootFinder.bisect(func, 2*neffSlabTM_max-findNeffEven(modeNumber), neffSlabTM_max) ;
		}
		else{
			double neffSlabTM_avg = (neffSlabTM_max+neffSlabTM_min)/2 ;
			rootFinder.setmaximumStaticBoundsExtension(500);
			rootFinder.setEstimate(neffSlabTM_min);
			r = rootFinder.bisect(func, 2*neffSlabTM_avg-findNeffEven(modeNumber), neffSlabTM_min) ;
		}

		return r ;
	}


	// ********************* test *********************
//	public static void main(String[] args){
//		double lambda = 1550 ;
//		Wavelength inputLambda = new Wavelength(lambda) ;
//		double[] gaps = MoreMath.Arrays.concat(MoreMath.linspace(50, 300, 100),  MoreMath.linspace(300, 1000, 100)) ;
//		double[] neff_even = {} ;
//		double[] neff_odd = {} ;
//
//		for(double g : gaps){
//			CoupledSlabWg slabs = new CoupledSlabWg(500, 500, g, 1.444, 3.444, 1.444, 3.444, 1.444) ;
//			ModeCoupledSlabWgTM_fast modeSolver = new ModeCoupledSlabWgTM_fast(inputLambda, slabs) ;
//			neff_even = MoreMath.Arrays.append(neff_even, modeSolver.findNeffEven(0)) ;
//			neff_odd = MoreMath.Arrays.append(neff_odd, modeSolver.findNeffOdd(0)) ;
//		}
//
//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(gaps, neff_even, "b") ;
//		fig.plot(gaps, neff_odd, "r");
//
//		fig.RenderPlot();
//		fig.run();
//
//	}
	


}
