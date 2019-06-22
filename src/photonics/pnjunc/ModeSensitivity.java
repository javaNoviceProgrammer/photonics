package photonics.pnjunc;

import flanagan.interpolation.CubicSpline;
import mathLib.fitting.interpol.LinearInterpolation1D;
import mathLib.func.ArrayFunc;
import mathLib.func.intf.RealFunction;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;

public class ModeSensitivity {

	// type of waveguide
	// dneff/dnsi (x) normalized
	// dalhpaeff/dalphasi(x) normalized
	
	private double dnSi = 0.01, dAlphaDbperCmSi = 10.0 ;
	private double neff0, alphaEff0 ;
	private double[] dneff_dnsi, dalphaEff_dalphaSi ;
	String lambda;
	
	private CubicSpline dnEffDnSi ;
	private CubicSpline dAlphaEffDalpha ;

	private double[] x_neff_nm_1550 = { -1000, -916.667, -833.333, -750, -666.667, -583.333, -500, -416.667, -333.333,
			-250, -166.667, -83.3333, 0, 83.3333, 166.667, 250, 333.333, 416.667, 500, 583.333, 666.667, 750, 833.333,
			916.667, 1000 };
	
	private double[] neff_1550 = { 2.59971, 2.59971, 2.59971, 2.59971, 2.59971, 2.59971, 2.59971, 2.5997, 2.5997,
			2.5997, 2.59968, 2.59964, 2.59917, 2.59816, 2.59649, 2.59448, 2.5926, 2.59124, 2.59053, 2.59014, 2.58988,
			2.58978, 2.58974, 2.58972, 2.58971 };

	private double[] x_alpha_nm_1550 = { -1000, -959.184, -918.367, -877.551, -836.735, -795.918, -755.102, -714.286,
			-673.469, -632.653, -591.837, -551.02, -510.204, -469.388, -428.571, -387.755, -346.939, -306.122, -265.306,
			-224.49, -183.673, -142.857, -102.041, -61.2245, -20.4082, 20.4082, 61.2245, 102.041, 142.857, 183.673,
			224.49, 265.306, 306.122, 346.939, 387.755, 428.571, 469.388, 510.204, 551.02, 591.837, 632.653, 673.469,
			714.286, 755.102, 795.918, 836.735, 877.551, 918.367, 959.184, 1000};
	
	private double[] alphaEff_1550 = { 997.979, 997.979, 997.977, 997.976, 997.973, 997.969, 997.964, 997.954,
			997.939, 997.915, 997.878, 997.82, 997.73, 997.591, 997.375, 997.038, 996.506, 995.688, 994.417, 992.456,
			989.409, 984.646, 977.369, 966.254, 949.582, 919.372, 874.43, 815.322, 741.698, 655.679, 560.351, 461.856,
			365.071, 276.073, 199.468, 136.935, 88.9167, 53.5881, 35.1203, 22.8745, 14.8029, 9.57433, 6.17413, 3.96001,
			2.56311, 1.65996, 1.0815, 0.708029, 0.465036, 0.312903};

	private double[] x_neff_nm_1310 = {};
	private double[] neff_1310 = {};
	private double[] x_alpha_nm_1310 = {};
	private double[] alphaEff_1310 = {};

	public ModeSensitivity(String lambdaNm) {
		this.lambda = lambdaNm;
		
		switch (lambdaNm) {
		case "1550":
			neff0 = neff_1550[neff_1550.length-1] ;
			alphaEff0 = alphaEff_1550[alphaEff_1550.length-1]*0.01 ; // convert from dBperMeter to dBperCm
			dneff_dnsi = ArrayFunc.apply(t -> (t-neff0)/dnSi, neff_1550) ;
			dalphaEff_dalphaSi = ArrayFunc.apply(t -> t*0.01/dAlphaDbperCmSi, alphaEff_1550) ;
			dnEffDnSi = new CubicSpline(x_neff_nm_1550, dneff_dnsi) ;
			dAlphaEffDalpha = new CubicSpline(x_alpha_nm_1550, dalphaEff_dalphaSi) ;
			break;
		case "1310":
			break ;
		default:
			break ;
		}
	}

	public double getDneffDnsi(double xNm) {
		return dnEffDnSi.interpolate(xNm) ;
	}

	public double getDalphaEffDalphaSi(double xNm) {
		return dAlphaEffDalpha.interpolate(xNm) ;
	}

	
	// test the interpolation
	
	public static void main(String[] args) {
		
//		ModeSensitivity modeSens = new ModeSensitivity("1550");
//		double[] xNm = MathUtils.linspace(-1000.0, 1000.0, 1000);
//		double[] dneffDnSi = ArrayFunc.apply(x -> modeSens.getDneffDnsi(x), xNm);
//
//		MatlabChart fig = new MatlabChart();
//		fig.plot(xNm, dneffDnSi, "b");
//		fig.plot(modeSens.x_neff_nm_1550, modeSens.dneff_dnsi, "r");
//		fig.renderPlot();
//		fig.markerON(1);
//		fig.setFigLineWidth(1, 0f);
//		fig.run(true);
//
//		MatlabChart fig2 = new MatlabChart();
//		double[] dneffDnsiDx = ArrayFunc.apply(x -> modeSens.dnEffDnSi.interpolate_for_y_and_dydx(x)[1], xNm);
//		fig2.plot(xNm, dneffDnsiDx);
//		fig2.renderPlot();
//		fig2.run(true);
//		
//		MatlabChart fig3 = new MatlabChart() ;
//		double[] dAlphaEffDalphaSi = ArrayFunc.apply(x -> modeSens.getDalphaEffDalphaSi(x), xNm) ;
//		fig3.plot(xNm, dAlphaEffDalphaSi, "b");
//		fig3.plot(modeSens.x_alpha_nm_1550, modeSens.dalphaEff_dalphaSi, "r");
//		fig3.renderPlot();
//		fig3.markerON(1);
//		fig3.setFigLineWidth(1, 0f);
//		fig3.run(true);
//		
//		MatlabChart fig4 = new MatlabChart();
//		double[] dAlphaEffDnsiDx = ArrayFunc.apply(x -> modeSens.dAlphaEffDalpha.interpolate_for_y_and_dydx(x)[1], xNm);
//		fig4.plot(xNm, dAlphaEffDnsiDx);
//		fig4.renderPlot();
//		fig4.run(true);
		
		// clearance loss
		
		ModeSensitivity modeSens = new ModeSensitivity("1550");
//		RealFunction alphaDbpCm = clearanceNm -> 20.54 * Math.exp(-0.013*(clearanceNm+24.138)) + 0.149 ; // N+
//		RealFunction alphaDbpCm = clearanceNm -> 17.872 * Math.exp(-0.013*(clearanceNm+24.013)) + 0.138 ; // P+
//		RealFunction alphaDbpCm = clearanceNm -> 0.386 * Math.exp(-0.012*(clearanceNm-719.136)) -0.022 ; // N++
		RealFunction alphaDbpCm = clearanceNm -> 0.385 * Math.exp(-0.012*(clearanceNm-699.408)) -0.068 ; // P++
		RealFunction dAlphaEffDbpCm = clearanceNm -> modeSens.getDalphaEffDalphaSi(500.0 + clearanceNm) ;
		
		double[] xNm = MathUtils.linspace(450.0, 500.0, 100) ;
		double[] lossDbpCm = ArrayFunc.apply(x -> dAlphaEffDbpCm.evaluate(x), xNm) ;
		
		MatlabChart fig5 = new MatlabChart() ;
		fig5.plot(xNm, lossDbpCm, "m");
		fig5.renderPlot();
		fig5.run(true);
		
		// step 1: find DalphaSi
		RealFunction dAlphaSidBperCm = clearanceNm -> alphaDbpCm.evaluate(clearanceNm)/dAlphaEffDbpCm.evaluate(clearanceNm) ;
//		RealFunction dopingNN = clearanceNm -> dAlphaSidBperCm.evaluate(clearanceNm)/4.343/ PlasmaDispersion.An_DaSi_1550 * 1e-19 ;
		RealFunction dopingNN = clearanceNm -> dAlphaSidBperCm.evaluate(clearanceNm)/4.343/ PlasmaDispersion.Ap_DaSi_1550 * 1e-19 ;
		
		MatlabChart fig6 = new MatlabChart() ;
		fig6.plot(xNm, ArrayFunc.apply(x -> dopingNN.evaluate(x), xNm));
		fig6.renderPlot();
		fig6.run(true);
		
		MatlabChart fig7 = new MatlabChart() ;
		double[] x2 = MathUtils.linspace(450.0, 1000.0, 100) ;
		fig7.plot(x2, ArrayFunc.apply(x -> alphaDbpCm.evaluate(x), x2), "b");
		double[] x1 = new double[]{450, 480, 500.0} ;
		fig7.plot(x1, ArrayFunc.apply(x -> dAlphaEffDbpCm.evaluate(x) * PlasmaDispersion.DalphaDbPerCmSi_1550nm(0.0, 5.1e20), x1), "k");
		fig7.renderPlot();
		fig7.xlabel("Clearance (nm)");
		fig7.ylabel("Loss (dB/cm)");
		fig7.markerON(1);
		fig7.setFigLineWidth(1, 0f);
		fig7.run(true);
//		
	}

}
