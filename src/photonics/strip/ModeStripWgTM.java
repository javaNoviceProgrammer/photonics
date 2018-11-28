package photonics.strip;

import ch.epfl.general_libraries.clazzes.ParamName;
import photonics.slab.ModeSlabWgTE;
import photonics.slab.ModeSlabWgTM;
import photonics.slab.SlabWg;
import photonics.util.Wavelength;

public class ModeStripWgTM {

	// Slab X --> TE mode (d = widthNm)
	// Slab Y --> TM mode (d = heightNm)

	SlabWg slabX, slabY ;
	ModeSlabWgTE slabTE ;
	ModeSlabWgTM slabTM ;
	StripWg stripWg ;
	double lambdaNm, neff_x, neff_y ;
	int mNumber, nNumber ;

	public ModeStripWgTM(
			@ParamName(name="Strip Waveguide") StripWg stripWg,
			@ParamName(name="m: TM_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TM_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.stripWg = stripWg ;
		this.mNumber = mNumber ;
		this.nNumber = nNumber ;
		lambdaNm = stripWg.getWavelengthNm() ;
		slabX = stripWg.getSlabX() ; // for the TE mode
		slabY = stripWg.getSlabY() ; // for the TM mode
		slabTE = new ModeSlabWgTE(slabX) ;
		slabTM = new ModeSlabWgTM(slabY) ;
		neff_x = slabTE.findSpecificModeIndex(mNumber) ;
		neff_y = slabTM.findSpecificModeIndex(nNumber) ;
	}

	public StripWg getStripWg(){
		return stripWg ;
	}

	public double getWavelengthNm(){
		return lambdaNm ;
	}

	public double getEffectiveIndex(){
		double n_core = stripWg.getCoreIndex() ;
		double neff = Math.sqrt(neff_x*neff_x + neff_y * neff_y - n_core * n_core) ;
		return neff ;
	}

    private double getNeffTM(double width_nm, double height_nm, double lambda_nm, int m_index, int n_index){
        Wavelength inputLambda = new Wavelength(lambda_nm) ;
        StripWg stripWg_temp = new StripWg(inputLambda, width_nm, height_nm, stripWg.getCladIndex(), stripWg.getCoreIndex(), stripWg.getSubstrateIndex()) ;
        ModeStripWgTM modeSolver = new ModeStripWgTM(stripWg_temp, m_index, n_index) ;
        return modeSolver.getEffectiveIndex() ;
    }

    public double getGroupIndex(){
        double dlambda_nm = 1e-1 ;
        double lambda_max_nm, lambda_min_nm, neff_max, neff_min ;
            lambda_max_nm = lambdaNm + dlambda_nm ;
            lambda_min_nm = lambdaNm ;
            neff_max = getNeffTM(stripWg.getWidthNm(), stripWg.getHeightNm(), lambda_max_nm, mNumber, nNumber) ;
            neff_min = getEffectiveIndex() ;
            double dneff_dlambda = (neff_max-neff_min)/(lambda_max_nm-lambda_min_nm) ;
            return (neff_min - lambdaNm * dneff_dlambda) ;
    }

}
