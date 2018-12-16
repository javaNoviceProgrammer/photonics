package photonics.ring.design;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;
import flanagan.interpolation.CubicSpline;
import flanagan.roots.RealRoot;
import flanagan.roots.RealRootFunction;
import mathLib.util.MathUtils;
import photonics.ring.AddDropFirstOrder;
import photonics.strip.ModeStripWgTE;
import photonics.strip.StripWg;
import photonics.util.Wavelength;
import photonics.wg.coupling.NeffCoupledStripWg_400X220_COMSOL;
import photonics.wg.coupling.NeffCoupledStripWg_450X220_COMSOL;
import photonics.wg.coupling.NeffCoupledStripWg_500X220_COMSOL;
import photonics.wg.coupling.RingWgCoupler;

public class AdrFirstOrderDesignSpace implements Experiment {

	// loss model
	double a = 2096.3 ;
	double b = 2.9123 ;

	// wg model
	double neff = 2.3505 ;
	double ng = 4.3927 ;

	StripWg stripWg ;

	// wavelength
	double lambda0 = 1550.0 ;
	double lambda ;

	double radius, outGap, inGap, kappaOut, kappaIn, lossdBperCm, loss, phi, fsr, bw ;
	AddDropFirstOrder adr ;

//	NeffCoupledStripWg_400X220_COMSOL db = new NeffCoupledStripWg_400X220_COMSOL() ;

	enum waveguide {
		WG_400X220,
		WG_450X220,
		WG_500X220
	}

	waveguide wg ;

	public AdrFirstOrderDesignSpace(
			@ParamName(name="Choose waveguide") waveguide wg,
			@ParamName(name="wavelength (nm)") double lambda,
			@ParamName(name="radius (um)") double radius,
			@ParamName(name="output gap (nm)") double outGap
			) {
		this.wg = wg ;
		this.lambda = lambda ;
		this.radius = radius ;
		this.outGap = outGap ;
		this.lossdBperCm = a * Math.pow(radius, -b) ;
		this.loss = Math.exp(-2*Math.PI*radius*1e-6*23*lossdBperCm) ;
		this.fsr = lambda*lambda*1e-9/(2*Math.PI*radius*1e-6*ng) ;
		this.kappaOut = getKappaRingWg(lambda0, radius, outGap, 400) ;
		double tOut = Math.sqrt(1-kappaOut*kappaOut) ;
		double tIn = tOut*Math.sqrt(loss) ;
		kappaIn = Math.sqrt(1-tIn*tIn) ;
		adr = new AddDropFirstOrder(0, loss, kappaIn, kappaOut) ;

	}

    private double getKappaRingWg(double lambda_nm, double radius_um, double d_nm, double width_nm){
    	double[] gap_nm = MathUtils.linspace(50, 500, 1000) ;
    	double[] neff_even = new double[gap_nm.length] ;
    	double[] neff_odd = new double[gap_nm.length] ;
    	ModeStripWgTE mode ;
    	switch (wg) {
		case WG_400X220:
			NeffCoupledStripWg_400X220_COMSOL db1 = new NeffCoupledStripWg_400X220_COMSOL() ;
			neff_even = db1.getNeffEven(lambda_nm, gap_nm) ;
			neff_odd = db1.getNeffOdd(lambda_nm, gap_nm) ;
			this.stripWg = new StripWg(new Wavelength(lambda), 400, 220) ;
			mode = new ModeStripWgTE(stripWg, 0, 0) ;
			this.phi = 2*Math.PI/(lambda*1e-9) * mode.getEffectiveIndex() * 2*Math.PI*radius*1e-6  ;
			break;
		case WG_450X220:
			NeffCoupledStripWg_450X220_COMSOL db2 = new NeffCoupledStripWg_450X220_COMSOL() ;
			neff_even = db2.getNeffEven(lambda_nm, gap_nm) ;
			neff_odd = db2.getNeffOdd(lambda_nm, gap_nm) ;
			this.stripWg = new StripWg(new Wavelength(lambda), 450, 220) ;
			mode = new ModeStripWgTE(stripWg, 0, 0) ;
			this.phi = 2*Math.PI/(lambda*1e-9) * mode.getEffectiveIndex() * 2*Math.PI*radius*1e-6  ;
			break;
		case WG_500X220:
			NeffCoupledStripWg_500X220_COMSOL db3 = new NeffCoupledStripWg_500X220_COMSOL() ;
			neff_even = db3.getNeffEven(lambda_nm, gap_nm) ;
			neff_odd = db3.getNeffOdd(lambda_nm, gap_nm) ;
			this.stripWg = new StripWg(new Wavelength(lambda), 500, 220) ;
			mode = new ModeStripWgTE(stripWg, 0, 0) ;
			this.phi = 2*Math.PI/(lambda*1e-9) * mode.getEffectiveIndex() * 2*Math.PI*radius*1e-6  ;
			break;
		default:
			break;
		}
    	CubicSpline neff_even_interpolator = new CubicSpline(gap_nm, neff_even) ;
    	CubicSpline neff_odd_interpolator = new CubicSpline(gap_nm, neff_odd) ;
    	RingWgCoupler rwDC = new RingWgCoupler(new Wavelength(lambda_nm), width_nm, radius_um, d_nm, neff_even_interpolator, neff_odd_interpolator) ;
    	return rwDC.getS31().abs() ;
    }

	double getDropILdB() {
		adr = new AddDropFirstOrder(0.0, loss, kappaIn, kappaOut) ;
		return 10*Math.log10(adr.getS41().absSquared()) ;
	}

	double getDropILatHalfFSR() {
		adr = new AddDropFirstOrder(Math.PI, loss, kappaIn, kappaOut) ;
		return 10*Math.log10(adr.getS41().absSquared()) ;
	}

	double getDropdB(double phi) {
		adr = new AddDropFirstOrder(phi, loss, kappaIn, kappaOut) ;
		return MathUtils.Conversions.todB(adr.getS41().absSquared()) ;
	}

	double getBwGhz() {
		double c = 3e8 ;
		RealRootFunction func = phi -> getDropdB(0.0) - getDropdB(phi) - 3 ;
		RealRoot root = new RealRoot() ;
		double dphi = 2.0 * root.bisect(func, 0.0, Math.PI) ;
		double bwNm = dphi/(2.0*Math.PI) * fsr ;
		return c/(lambda0*1e-9*lambda0) * bwNm * 1e-9 ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		DataPoint dp = new DataPoint() ;
		dp.addProperty("Wavelength (nm)", lambda);
		dp.addProperty("Radius (um)", radius);
		dp.addProperty("Gap (nm)", outGap);
		dp.addResultProperty("FSR (nm)", fsr);
		dp.addResultProperty("IL at Res (dB)", getDropILdB());
		dp.addResultProperty("IL at FSR/2 (dB)", getDropILatHalfFSR());
		dp.addResultProperty("BW (GHz)", getBwGhz());
		dp.addResultProperty("Drop (dB)", getDropdB(phi));
		dp.addResultProperty("Bending loss (dB/cm)", lossdBperCm);
		dp.addResultProperty("Kappa in", kappaIn);
		dp.addResultProperty("Kappa out", kappaOut);
		dp.addResultProperty("Clos 6 rings Drop (dB)", 6*getDropdB(phi));
		man.addDataPoint(dp);
	}

	public static void main(String[] args) {
		String p = "local" ;
		String c =AdrFirstOrderDesignSpace.class.getName() ;
		ExperimentConfigurationCockpit.execute(new String[]{"-p", p, "-c", c}, true);

	}

}
