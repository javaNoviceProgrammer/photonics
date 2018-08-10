package photonics.slab;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import photonics.util.Wavelength;

public class ModeSlabWgTE {
	
	Wavelength inputLambda ;
	double widthNm, lambdaNm ;
	double n_down, n_core, n_up, n_low, n_high , V ;
	public double asymmetryFactor;
	
	// First type of constructor for directly inputing parameters of the slab
	public ModeSlabWgTE(
			@ParamName(name="normalized frequency V") double V,
			@ParamName(name="waveguide width (nm)") double widthNm,
			@ParamName(name="down index") double n_d,
			@ParamName(name="core index") double n_c,
			@ParamName(name="up index") double n_u
			){
		this.V = V ;
		this.lambdaNm = (2*Math.PI*widthNm*Math.sqrt(n_c*n_c-n_d*n_d))/V ;
		this.widthNm = widthNm ;
		this.n_core = n_c ;
		this.n_down = n_d ;
		this.n_up = n_u ;
		this.n_low = Math.max(n_d, n_u) ;
		this.n_high = n_c ;
		this.asymmetryFactor = (n_d*n_d-n_u*n_u)/(n_c*n_c-n_d*n_d) ;
	}
	
	// Second type of constructor for using the SlabWg class
	public ModeSlabWgTE(
			SlabWg slab
			){
		this.V = slab.getNormalizedFreq() ;
		this.lambdaNm = slab.getWavelengthNm() ;
		this.widthNm = slab.getWidthNm() ;
		this.n_core = slab.getCoreIndex() ;
		this.n_down = slab.getSubstrateIndex() ;
		this.n_up = slab.getCladIndex() ;
		this.n_low = slab.getSubstrateIndex() ;
		this.n_high = slab.getCoreIndex() ;
		this.asymmetryFactor = slab.getTEsymmetryFactor() ;
	}
	
	// Finally need to solve the eigen value equation
	private double getModeEquation(double b, int modeNumber){
		double aTE = asymmetryFactor ;
		double A = V * Math.sqrt(1-b) - modeNumber * Math.PI ;
		double arg1 = Math.sqrt(b/(1-b)) ;
		double arg2 = Math.sqrt((b+aTE)/(1-b)) ;
		double B = Math.atan(arg1) + Math.atan(arg2) ;
		return (A-B) ;
	}	
	
	// mode number starts from zero --> TE0, TE1, TE2, ...
	public double findSpecificModeIndex(int modeNumber){
		double b = findSpecificModeNormalizedIndex(modeNumber) ;
		double neffSquared = b * (n_core*n_core - n_down*n_down) + n_down * n_down ;
		return Math.sqrt(neffSquared) ;
	}
	
	public double findSpecificModeNormalizedIndex(final int modeNumber){
		RealRootFunction func = new RealRootFunction() {
			@Override
			public double function(double b) {
				return getModeEquation(b, modeNumber);
			}
		};
		RealRoot rootFinder = new RealRoot() ;
		double b = rootFinder.bisect(func, 0, 1) ;
		try {
			b = rootFinder.bisect(func, 0, 1) ;
			if(Double.isNaN(b)){b = 0 ;}
		} catch (Exception e) {
			b = 0 ;
		}
//		double b = rootFinder.bisect(func, 0, 1) ;
		return b ;
	}

}
