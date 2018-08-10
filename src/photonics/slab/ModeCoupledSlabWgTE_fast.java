package photonics.slab;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.matrix.ComplexMatrix;
import mathLib.util.MathUtils;
import mathLib.util.Timer;
import photonics.transfer.TransferMatrixTE;
import photonics.util.Wavelength;
import plotter.chart.MatlabChart;

public class ModeCoupledSlabWgTE_fast {

	Wavelength inputLambda ;
	double w1_nm, w2_nm, gap_nm , lambdaNm , k0;
	double n_down, n_core1, n_core2, n_up, n_gap, n_high, n_low ;
	ModeSlabWgTE slabTE1, slabTE2 ;

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


	// First type of constructor for directly inputting parameters of the slab
	public ModeCoupledSlabWgTE_fast(
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

	public ModeCoupledSlabWgTE_fast(
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
		this.n_high = Math.max(n_c_1, n_c_2) ;
		this.n_low = Math.min(Math.min(n_u, n_d), n_g) ;

	}
	
	// Next I need to find the Mode Equation
	private double getModeEquation(double neff){
		TransferMatrixTE Q1 = new TransferMatrixTE(inputLambda, n_up, n_core1, neff, 0, 0) ;
		TransferMatrixTE Q2 = new TransferMatrixTE(inputLambda, n_core1, n_gap, neff, w1_nm, 0) ;
		TransferMatrixTE Q3 = new TransferMatrixTE(inputLambda, n_gap, n_core2, neff, w1_nm+gap_nm, 0) ;
		TransferMatrixTE Q4 = new TransferMatrixTE(inputLambda, n_core2, n_down, neff, w1_nm+gap_nm+w2_nm, 0) ;
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
		double neffTE1 = new ModeSlabWgTE(slab1).findSpecificModeIndex(modeNumber) ;
		double neffTE2 = new ModeSlabWgTE(slab2).findSpecificModeIndex(modeNumber) ;
		double neffSlabTE_min = Math.min(neffTE1, neffTE2) ;
		double neffSlabTE_max = Math.max(neffTE1, neffTE2) ;

		rootFinder.setEstimate(neffSlabTE_max);
		return rootFinder.bisect(func, neffSlabTE_max, n_high) ;
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
		double neffTE1 = new ModeSlabWgTE(slab1).findSpecificModeIndex(modeNumber) ;
		double neffTE2 = new ModeSlabWgTE(slab2).findSpecificModeIndex(modeNumber) ;
		double neffSlabTE_min = Math.min(neffTE1, neffTE2) ;
		double neffSlabTE_max = Math.max(neffTE1, neffTE2) ;

		double r = Double.NaN ;
		if(w1_nm == w2_nm){
			rootFinder.setEstimate(2*neffSlabTE_max-findNeffEven(modeNumber));
			r = rootFinder.bisect(func, 2*neffSlabTE_max-findNeffEven(modeNumber), neffSlabTE_max) ;
		}
		else{
			double neffSlabTE_avg = (neffSlabTE_max+neffSlabTE_min)/2 ;
			rootFinder.setmaximumStaticBoundsExtension(500);
			rootFinder.setEstimate(neffSlabTE_min);
			r = rootFinder.bisect(func, 2*neffSlabTE_avg-findNeffEven(modeNumber), neffSlabTE_min) ;
		}

		return r ;
	}


	// ********************* test *********************
	public static void main(String[] args){
		double lambda = 1550 ;
		Wavelength inputLambda = new Wavelength(lambda) ;
		double[] gaps = MathUtils.Arrays.concat(MathUtils.linspace(50, 300, 100),  MathUtils.linspace(300, 1000, 100)) ;
		double[] neff_even = {} ;
		double[] neff_odd = {} ;
		Timer timer = new Timer() ;
		timer.start();
		for(double g : gaps){
			CoupledSlabWg slabs = new CoupledSlabWg(450, 450, g, 1.444, 3.444, 1.444, 3.444, 1.444) ;
			ModeCoupledSlabWgTE_fast modeSolver = new ModeCoupledSlabWgTE_fast(inputLambda, slabs) ;
			neff_even = MathUtils.Arrays.append(neff_even, modeSolver.findNeffEven(1)) ;
			neff_odd = MathUtils.Arrays.append(neff_odd, modeSolver.findNeffOdd(1)) ;
		}
		
		timer.end();
		timer.show();
		
		MatlabChart fig = new MatlabChart() ;
		fig.plot(gaps, neff_even, "b") ;
		fig.plot(gaps, neff_odd, "r");

		fig.RenderPlot();
		fig.run(true);

	}


}
