package photonics.modes;

import java.util.ArrayList;
import java.util.List;

import Jama.EigenvalueDecomposition;
import mathLib.matrix.Matrix;
import mathLib.numbers.Complex;
import mathLib.ode.DiffOperator;
import mathLib.util.MathUtils;
import mathLib.util.Timer;
import mathLib.util.Units;
import photonics.util.Fields;
import photonics.util.Modes;
import plotter.chart.MatlabChart;

public class FDESolver1D {

	int numPoints, numModes ;
	double lambda, xMin, xMax, dx, scale ;
	IndexProfile1D indexProfile = null ;
	Units gridUnit, lambdaUnit;
	double[] x ;
	double[] index ;
	double[] epsilon ;
	double[][] coeffEy, coeffHy ;
	ArrayList<Complex[]> Ex, Ey, Ez ;
	ArrayList<Complex[]> Hx, Hy, Hz ;
//	ArrayList<Complex> neff ;
	List<Complex> neff ;
	boolean debug = false ;
	Modes modes ;

	public FDESolver1D() {
	}

	public void setDebug(boolean debug){
		this.debug = debug ;
	}

	public void setGrid(int numPoints, Units unit) {
		this.numPoints = numPoints ;
		this.gridUnit = unit ;
	}

	public void setGrid(double dx, Units unit) {
		this.dx = dx ;
		this.gridUnit = unit ;
	}

	public void setIndexProfile(IndexProfile1D profile) {
		this.indexProfile = profile ;
	}

	public void setWavelength(double lambda, Units unit) {
		this.lambda = lambda ;
		this.lambdaUnit = unit ;
	}

	public void setBoundary() {

	}

	public void solve(Modes modes) {
		if(indexProfile == null)
			throw new NullPointerException("First setup the index profile!") ;
		this.modes = modes ;
		computeScale() ;
		createMesh() ;
		if(modes == Modes.TE) {
			solveTE();
		}
		else {
			solveTM();
		}
		
	}

	private void computeScale() {
		scale = 1.0 ;
		if(gridUnit.equals(Units.um) && lambdaUnit.equals(Units.nm))
			scale = 1e3 ;
		else if(gridUnit.equals(Units.nm) && lambdaUnit.equals(Units.um))
			scale = 1e-3 ;
		else
			scale = 1.0 ;
	}

	private void createMesh() {
		xMin = indexProfile.getLowerBoundary() ;
		xMax = indexProfile.getUpperBoundary() ;
		if(numPoints == 0){
			numPoints = (int) ((xMax-xMin)/dx) ;
		}
		x = MathUtils.linspace(xMin, xMax, numPoints) ;
		dx = x[2] - x[1] ;
		index = new double[numPoints] ;
		epsilon = new double[numPoints] ;
		for(int i=0; i<x.length; i++) {
			index[i] = indexProfile.getRealIndex(x[i]) ;
			epsilon[i] = index[i]*index[i] ;
		}
	}

	private void solveTE() {
		printDebugInfo();
		numModes = 0 ;
		// Ey, Hx, Hz
		Ey = new ArrayList<>() ;
//		Hx = new ArrayList<>() ;
//		Hz = new ArrayList<>() ;
		coeffEy = new double[numPoints][numPoints] ;
		double var1 = (2*Math.PI*dx/lambda)*scale ;
		double var2 = var1*var1 ;
		assembleTE(coeffEy, var2) ;
		Jama.Matrix coeffEyMatrix = new Jama.Matrix(coeffEy) ;
		EigenvalueDecomposition eigDecomp = coeffEyMatrix.eig() ;
		double[] tempReal = eigDecomp.getRealEigenvalues() ;
		double[] tempImag = eigDecomp.getImagEigenvalues() ;
		neff = new ArrayList<>() ;
		double minIndex = MathUtils.Arrays.FindMinimum.getValue(index) ;
		double maxIndex = MathUtils.Arrays.FindMaximum.getValue(index) ;
		for(int i=numPoints-1; i>=0; i--) {
			Complex eig = new Complex(tempReal[i], tempImag[i]).sqrt().divides(var1) ;
			if(eig.re()>minIndex && eig.re()<maxIndex){
				System.out.println(eig);
				neff.add(eig) ;
				numModes ++ ;
				// finding corresponding eigen vectors
				double[][] vec = eigDecomp.getV().getArray() ;
				Complex[] ey = new Complex[numPoints] ;
				for(int j=0;j<numPoints; j++){
					ey[j] = new Complex(vec[j][i], 0.0) ;
				}
				Ey.add(ey) ;
			}
		}
		
	}

	// f''(x) = ( f(x+h) -2 f(x) + f(x-h) ) / h^2

	private void assembleTE(double[][] coeff, double var) {
		int M = coeff.length ;
		coeff[0][0] = -2 ;
		coeff[0][1] = 1 ;
		coeff[M-1][M-1] = -2 ;
		coeff[M-1][M-2] = 1 ;
		for(int i=1; i<M-1; i++) {
				coeff[i][i] = -2 ;
				coeff[i][i-1] = 1;
				coeff[i][i+1] = 1;
		}
		for(int i=0; i<M; i++) {
			coeff[i][i] += var*epsilon[i] ;
		}
	}
	
	private void solveTM() {
		printDebugInfo();
		numModes = 0 ;
		// Hy, Ex, Ez
		Hy = new ArrayList<>() ;
//		Ex = new ArrayList<>() ;
//		Ez = new ArrayList<>() ;
		coeffHy = new double[numPoints][numPoints] ;
		coeffHy = assembleTM();
		Jama.Matrix coeffHyMatrix = new Jama.Matrix(coeffHy) ;
		EigenvalueDecomposition eigDecomp = coeffHyMatrix.eig() ;
		double[] tempReal = eigDecomp.getRealEigenvalues() ;
		double[] tempImag = eigDecomp.getImagEigenvalues() ;
		neff = new ArrayList<>() ;
		double minIndex = MathUtils.Arrays.FindMinimum.getValue(index) ;
		double maxIndex = MathUtils.Arrays.FindMaximum.getValue(index) ;
		for(int i=0; i<numPoints; i++) {
			Complex eig = new Complex(tempReal[i], tempImag[i]).sqrt() ;
			if(eig.re()>minIndex && eig.re()<maxIndex){
				System.out.println(eig);
				neff.add(eig) ;
				numModes ++ ;
				// finding corresponding eigen vectors
				double[][] vec = eigDecomp.getV().getArray() ;
				Complex[] hy = new Complex[numPoints] ;
				for(int j=0;j<numPoints; j++){
					hy[j] = new Complex(Math.abs(vec[j][i]), 0.0) ;
				}
				Hy.add(hy) ;
			}
		}
	}

	private double[][] assembleTM() {
		double var1 = lambda/(2*Math.PI) ;
		double var2 = var1*var1 ;
		DiffOperator D = new DiffOperator(numPoints, dx*scale) ;
		double[] invEps = new double[numPoints] ;
		for(int i=0; i<numPoints; i++)
			invEps[i] = 1.0/epsilon[i] ;
//		Matrix M = var2 * D.getDxxMatrix() + Matrix.diag(epsilon) ;
		Matrix M = var2 * D.getDxxMatrix() + Matrix.diag(epsilon) + Matrix.diag(epsilon) * D.getDxMatrix() *
											Matrix.diag(invEps)* D.getDxMatrix() * var2 ;
		return M.getData() ;
	}

	private void printDebugInfo() {
		System.out.println("xMin = " + xMin + " "+ gridUnit.name());
		System.out.println("xMax = " + xMax + " "+ gridUnit.name());
		System.out.println("dx = " + dx + " "+ gridUnit.name());
		System.out.println("number of grid points = " + numPoints);
		System.out.println("computing " + modes + "...");
		plotIndexProfile();
	}

	public void plotIndexProfile() {
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, index);
		fig.RenderPlot();
		fig.xlabel("X " + "(" + gridUnit.name() + ")");
		fig.ylabel("Index");
		fig.run(true);
		fig.markerON();
	}

	private void plotComponent(double[] x, Complex[] f, String name){
		MatlabChart fig = new MatlabChart() ;
		double[] fReal = new double[f.length] ;
		for(int i=0; i<f.length; i++){
			fReal[i] = f[i].re() ;
		}
		fig.plot(x, fReal);
		fig.RenderPlot();
		fig.xlabel("X " + "(" + gridUnit.name() + ")");
		fig.ylabel(name);
		fig.run();
		fig.markerON();
	}

	public void plotField(Fields field, int modeNum){
		switch (field) {
		case Ey: { plotComponent(x, Ey.get(modeNum-1), Fields.Ey.name()); break; }
		case Hy: { plotComponent(x, Hy.get(modeNum-1), Fields.Hy.name()); break; }
		default:
			break;
		}
	}


	// for test
	public static void main(String[] args) {
		FDESolver1D fde = new FDESolver1D() ;
		fde.setWavelength(1550.0, Units.nm);
		fde.setGrid(10.0, Units.nm);
		fde.setIndexProfile(new IndexProfile1D() {

			@Override
			public double getUpperBoundary() {
				return 1500.0;
			}

			@Override
			public double getLowerBoundary() {
				return -1000.0;
			}

			@Override
			public double getRealIndex(double x) {
				if(x<0) return 1.444 ;
				else if(x < 400.0) return 3.477 ;
				else return 1.444 ;
			}

			@Override
			public double getImagIndex(double x) {
				return 0;
			}
		});
		// time benchmarking
		Timer timer = new Timer() ;
		timer.start();
		fde.solve(Modes.TM);
		fde.plotField(Fields.Hy, 1);
//		fde.plotField(Fields.Hy, 2);
//		fde.plotField(Fields.Hy, 3);
//		fde.plotField(Fields.Ey, 4);
//		fde.plotField(Fields.Ey, 5);
//		fde.plotField(Fields.Ey, 6);
//		fde.plotField(Fields.Ey, 7);
//		fde.plotField(Fields.Ey, 8);
//		fde.plotField(Fields.Ey, 9);
//		fde.plotField(Fields.Ey, 10);
		timer.end();
		timer.show();


	}

}
