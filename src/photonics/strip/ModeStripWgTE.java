package photonics.strip;

import ch.epfl.general_libraries.clazzes.ParamName;
import photonics.slab.ModeSlabWgTE;
import photonics.slab.ModeSlabWgTM;
import photonics.slab.SlabWg;
import photonics.util.Wavelength;

public class ModeStripWgTE {

	// Slab X --> TM mode (d = widthNm)
	// Slab Y --> TE mode (d = heightNm)

	SlabWg slabX, slabY ;
	ModeSlabWgTE slabTE ;
	ModeSlabWgTM slabTM ;
	StripWg stripWg ;
	double lambdaNm, neff_x, neff_y ;
	int mNumber, nNumber ;

	public ModeStripWgTE(
			@ParamName(name="Strip Waveguide") StripWg stripWg,
			@ParamName(name="m: TE_(m,n) [m=0,1,2,...]") int mNumber,
			@ParamName(name="n: TE_(m,n) [n=0,1,2,...]") int nNumber
			){
		this.stripWg = stripWg ;
		this.mNumber = mNumber ;
		this.nNumber = nNumber ;
		lambdaNm = stripWg.getWavelengthNm() ;
		slabX = stripWg.getSlabX() ; // for the TM mode
		slabY = stripWg.getSlabY() ; // for the TE mode
		slabTE = new ModeSlabWgTE(slabY) ;
		slabTM = new ModeSlabWgTM(slabX) ;
		neff_x = slabTM.findSpecificModeIndex(mNumber) ;
		neff_y = slabTE.findSpecificModeIndex(nNumber) ;
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

    private double getNeffTE(double width_nm, double height_nm, double lambda_nm, int m_index, int n_index){
        Wavelength inputLambda = new Wavelength(lambda_nm) ;
        StripWg stripWg_temp = new StripWg(inputLambda, width_nm, height_nm, stripWg.getCladIndex(), stripWg.getCoreIndex(), stripWg.getSubstrateIndex()) ;
        ModeStripWgTE modeSolver = new ModeStripWgTE(stripWg_temp, m_index, n_index) ;
        return modeSolver.getEffectiveIndex() ;
    }

    public double getGroupIndex(){
        double dlambda_nm = 1e-1 ;
        double lambda_max_nm, lambda_min_nm, neff_max, neff_min ;
            lambda_max_nm = lambdaNm + dlambda_nm ;
            lambda_min_nm = lambdaNm ;
            neff_max = getNeffTE(stripWg.getWidthNm(), stripWg.getHeightNm(), lambda_max_nm, mNumber, nNumber) ;
            neff_min = getEffectiveIndex() ;
            double dneff_dlambda = (neff_max-neff_min)/(lambda_max_nm-lambda_min_nm) ;
            return (neff_min - lambdaNm * dneff_dlambda) ;
    }

}
