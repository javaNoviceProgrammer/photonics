package photonics.pnjunc;

import mathLib.fitting.lmse.LeastSquareFitter;
import mathLib.fitting.lmse.LeastSquareFunction;
import mathLib.fitting.lmse.MarquardtFitter;
import mathLib.func.ArrayFunc;
import mathLib.func.intf.RealFunction;
import mathLib.plot.MatlabChart;
import mathLib.util.MathUtils;
import mathLib.util.StringUtils;

public class PNJunction {

	private double ni = 1.45e10 ;
	private double eps0 = 8.85e-12 ;
	private double epsSi = 11.68 ;
	private double q = 1.6022e-19 ;
	private double vth = 0.026 ;
	private double twgNm = 220.0 ;
	private double widthNm = 500.0 ;

	private double N, P, NN, PP ;
	private double clearanceNNnm, clearancePPnm ;
	private double offsetNm ;

	private ModeSensitivity modeSens ;
	private OpticalBand opticalBand ;

	public enum OpticalBand {
		cBand1550(1550.0),
		oBand1310(1310.0) ;

		private double wavelength ;

		private OpticalBand(double wavelength) {
			this.wavelength = wavelength ;
		}

		public double getLambda() {
			return wavelength ;
		}
	}

	public PNJunction(
			double N, double P, double NN, double PP, double offsetNm, double clearanceNNnm,
			double clearancePPnm
			) {
		this.N = N ;
		this.P = P ;
		this.NN = NN ;
		this.PP = PP ;
		this.offsetNm = offsetNm ;
		this.clearanceNNnm = clearanceNNnm ;
		this.clearancePPnm = clearancePPnm ;
	}

	public PNJunction() {

	}

	public void setOpticalBand(OpticalBand lambda) {
		this.opticalBand = lambda ;
	}

	public void setModeSensitivity(ModeSensitivity modeSens) {
		this.modeSens = modeSens;
	}

	public void setModeSensitivity(OpticalBand lambda) {
		this.opticalBand = lambda ;
		switch (lambda) {
		case cBand1550:
			this.modeSens = new ModeSensitivity("1550") ;
			break;
		case oBand1310:
			this.modeSens = new ModeSensitivity("1310") ;
		default:
			this.modeSens = new ModeSensitivity("1550") ;
			break;
		}
	}

	public double getN() {
		return this.N;
	}

	public void setN(double N) {
		this.N = N ;
	}

	public double getP() {
		return this.P;
	}

	public void setP(double P) {
		this.P = P ;
	}

	public double getNN() {
		return this.NN;
	}

	public void setNN(double NN) {
		this.NN = NN;
	}

	public double getPP() {
		return this.PP;
	}

	public void setPP(double PP) {
		this.PP = PP ;
	}

	public double getClearanceNNnm() {
		return clearanceNNnm;
	}

	public void setClearanceNNnm(double clearanceNNnm) {
		this.clearanceNNnm = clearanceNNnm;
	}

	public double getClearancePPnm() {
		return clearancePPnm;
	}

	public void setClearancePPnm(double clearancePPnm) {
		this.clearancePPnm = clearancePPnm;
	}

	public double getOffsetNm() {
		return offsetNm;
	}

	public void setOffsetNm(double offsetNm) {
		this.offsetNm = offsetNm;
	}

	public double getVbi() {
		return vth * Math.log(N*P/(ni*ni)) ;
	}

	public double getWjunctionNm(double voltage) {
		double a = 2.0*eps0*epsSi/q * (1.0/N + 1.0/P) * 1e-6 * (getVbi() - voltage) ;
		return Math.sqrt(a)*1e9 ;
	}

	public double getXNnm(double voltage) {
		return getWjunctionNm(voltage) * P/(N+P) ;
	}

	public double getXPnm(double voltage) {
		return getWjunctionNm(voltage) * N/(N+P) ;
	}

	public double getCjuncfFpMilli(double voltage) {
		return eps0*epsSi/getWjunctionNm(voltage) * twgNm * 1e12 ;
	}

	private double getXnm(double voltage) {
		return widthNm/2.0 - offsetNm + getXPnm(voltage) ;
	}

	private double getYnm(double voltage) {
		return widthNm/2.0 + offsetNm + getXNnm(voltage) ;
	}

	public double getNeff(double voltage) {
		double xp = getXnm(voltage) ;
		double xpp = widthNm + clearancePPnm ;
		double yn = getYnm(voltage) ;
		double ynn = widthNm + clearanceNNnm ;
		double a1 = PlasmaDispersion.Dnsi_1550nm(0.0, P) * modeSens.getDneffDnsi(xp) ;
		double a2 = PlasmaDispersion.Dnsi_1550nm(0.0, PP) * modeSens.getDneffDnsi(xpp) ;
		double a3 = PlasmaDispersion.Dnsi_1550nm(N, 0.0) * modeSens.getDneffDnsi(yn) ;
		double a4 = PlasmaDispersion.Dnsi_1550nm(NN, 0.0) * modeSens.getDneffDnsi(ynn) ;
		double neff0 = modeSens.getNeff0() ;
		return neff0 + a1 + a2 + a3 + a4 ;
	}

	public double getDNeff(double v1, double v2) {
		return getNeff(v2) - getNeff(v1) ;
	}

	public double getDphiRadperCm(double v1, double v2) {
		double dphi = 0.0 ;
		switch (opticalBand) {
		case cBand1550:
			dphi = 2.0*Math.PI/(1550e-7) * getDNeff(v1, v2) ;
			break;
		case oBand1310:
			dphi = 2.0*Math.PI/(1310e-7) * getDNeff(v1, v2) ;
		default:
			break;
		}

		return dphi ;
	}

	public double getAlphaEffDbPerCm(double voltage) {
		double xp = getXnm(voltage) ;
		double xpp = widthNm + clearancePPnm ;
		double yn = getYnm(voltage) ;
		double ynn = widthNm + clearanceNNnm ;
		double a1 = PlasmaDispersion.DalphaDbPerCmSi_1550nm(0.0, P) * modeSens.getDalphaEffDalphaSi(xp) ;
		double a2 = PlasmaDispersion.DalphaDbPerCmSi_1550nm(0.0, PP) * modeSens.getDalphaEffDalphaSi(xpp) ;
		double a3 = PlasmaDispersion.DalphaDbPerCmSi_1550nm(N, 0.0) * modeSens.getDalphaEffDalphaSi(yn) ;
		double a4 = PlasmaDispersion.DalphaDbPerCmSi_1550nm(NN, 0.0) * modeSens.getDalphaEffDalphaSi(ynn) ;
		double alphaEff0 = modeSens.getAlphaEff0() ;
		return alphaEff0 + a1 + a2 + a3 + a4 ;
	}

	public double getDAlphaEffDbPerCm(double v1, double v2) {
		return getAlphaEffDbPerCm(v2) - getAlphaEffDbPerCm(v1) ;
	}

	public double getLossPPdBperCm() {
		double xpp = widthNm + clearancePPnm ;
		return PlasmaDispersion.DalphaDbPerCmSi_1550nm(0.0, PP) * modeSens.getDalphaEffDalphaSi(xpp) ;
	}

	public double getLossNNdBperCm() {
		double ynn = widthNm + clearanceNNnm ;
		return PlasmaDispersion.DalphaDbPerCmSi_1550nm(NN, 0.0) * modeSens.getDalphaEffDalphaSi(ynn) ;
	}









	//*********** test *************

	public static void main(String[] args) {
		PNJunction pn = new PNJunction() ;
		pn.setN(2.5e17);
		pn.setP(2.2e17);
		pn.setNN(5.73e18);
		pn.setPP(5.73e18);
		pn.setModeSensitivity(OpticalBand.cBand1550);
		pn.setOffsetNm(60.0);

		double[] voltage = MathUtils.linspace(0.0, -6.0, 100) ;

		double[] wj = ArrayFunc.apply(v -> pn.getWjunctionNm(v), voltage) ;
		double[] xN = ArrayFunc.apply(v -> pn.getXNnm(v), voltage) ;
		double[] xP = ArrayFunc.apply(v -> pn.getXPnm(v), voltage) ;
		double[] cjunc = ArrayFunc.apply(v -> pn.getCjuncfFpMilli(v), voltage) ;
		double[] dneff = ArrayFunc.apply(v -> pn.getDNeff(0.0, v), voltage) ;
		double[] dphiRadperCm = ArrayFunc.apply(v -> pn.getDphiRadperCm(0.0, v), voltage) ;


//		MatlabChart fig = new MatlabChart() ;
//		fig.plot(voltage, wj, "b", 2f, "W_j (nm)");
//		fig.plot(voltage, xN, "r", 2f, "x_n (nm)");
//		fig.plot(voltage, xP, "g", 2f, "x_p (nm)");
//		fig.renderPlot();
//		fig.run(true);

		MatlabChart fig2 = new MatlabChart() ;
		fig2.plot(voltage, cjunc, "b", 2f, "C_j (fF/mm)");
		fig2.renderPlot();
		fig2.run(true);

//		double[] clearance = MathUtils.linspace(0.0, 500.0, 100) ;
//		double[] excessLoss = new double[clearance.length] ;
//		for(int i=0; i<clearance.length; i++) {
//			pn.setClearanceNNnm(clearance[i]);
//			excessLoss[i] = pn.getLossNNdBperCm() ;
//		}
//
//		MatlabChart fig3 = new MatlabChart() ;
//		fig3.plot(clearance, excessLoss, "b");
//		fig3.renderPlot();
//		fig3.run(true);
//
//		MatlabChart fig4 = new MatlabChart() ;
//		fig4.plot(voltage, dneff, "b");
//		fig4.renderPlot();
//		fig4.run(true);

		RealFunction saturn1 = v -> 1.71 * Math.pow(-v+0.7, 0.8) - 1.26 ;
		RealFunction saturn1Cap = v -> 237.45 * Math.pow(-v+0.855, -0.3) - 26.11 ;

		MatlabChart fig5 = new MatlabChart() ;
		fig5.plot(voltage, dphiRadperCm, "b");
		fig5.plot(voltage, ArrayFunc.apply(saturn1, voltage), "r");
		fig5.renderPlot();
		fig5.run(true);

		LeastSquareFunction func = new LeastSquareFunction() {
			@Override
			public int getNParameters() {
				return 2;
			}

			@Override
			public int getNInputs() {
				return 1;
			}

			@Override
			public double evaluate(double[] values, double[] parameters) {
				double valN = parameters[0]*1e17 ;
				double valP = parameters[1]*1e17 ;
				pn.setN(valN);
				pn.setP(valP);
				return pn.getDphiRadperCm(0.0, values[0]) ;
			}
		};

		LeastSquareFitter fitter = new MarquardtFitter(func) ;
		fitter.setParameters(new double[] {1.0, 1.5});
		double[][] data = new double[voltage.length][1] ;
		for(int i=0; i<voltage.length; i++)
			data[i][0] = voltage[i] ;
		fitter.setData(data, ArrayFunc.apply(saturn1, voltage));

		fitter.fitData();
		double[] params = fitter.getParameters() ;
		System.out.println("N = " + StringUtils.fixedWidthDoubletoString(params[0], 3) +
							", P = " + StringUtils.fixedWidthDoubletoString(params[1], 3));


		pn.setN(params[0]*1e17);
		pn.setP(params[1]*1e17);

		fig5.plot(voltage, ArrayFunc.apply(v -> pn.getDphiRadperCm(0.0, v), voltage), "g");
		fig5.renderPlot();


		fig2.plot(voltage, ArrayFunc.apply(v -> pn.getCjuncfFpMilli(v), voltage), "g");
		fig2.plot(voltage, ArrayFunc.apply(saturn1Cap, voltage), "r");
		fig2.renderPlot();

	}



}
