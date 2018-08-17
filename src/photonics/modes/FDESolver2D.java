package photonics.modes;

import static mathLib.numbers.Complex.j;

import java.util.ArrayList;

import Jama.EigenvalueDecomposition;
import mathLib.matrix.Matrix;
import mathLib.numbers.Complex;
import mathLib.pde.DiffOperator2D;
import mathLib.util.Conversions;
import mathLib.util.MathUtils;
import mathLib.util.Timer;
import mathLib.util.Units;
import photonics.util.Modes;
import plotter.chart.ColorMapPlot;
import plotter.chart.MatlabChart;
import plotter.util.ColorMap.ColorMapName;
import plotter.util.MeshGrid;

public class FDESolver2D {

	int numPointsX, numPointsY, numModes;
	double lambda, xMin, xMax, dx, yMin, yMax, dy, scale;
	IndexProfile2D indexProfile = null;
	Units gridUnit, lambdaUnit;
	double[] x, y;
	double[][] index;
	double[][] epsilon;
	double[][] coeffE, coeffH;
	ArrayList<Complex[][]> Ex, Ey, Ez;
	ArrayList<Complex[][]> Hx, Hy, Hz;
	ArrayList<Complex> neff;
	boolean debug = false;
	Modes modes;

	public FDESolver2D() {
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

//	public void setGrid(int numPointsX, int numPointsY, Units unit) {
//		this.numPointsX = numPointsX ;
//		this.numPointsY = numPointsY ;
//		this.gridUnit = unit;
//	}

	public void setGrid(double dx, double dy, Units unit) {
		this.dx = dx;
		this.dy = dy ;
		this.gridUnit = unit;
	}

	public void setIndexProfile(IndexProfile2D profile) {
		this.indexProfile = profile;
	}

	public void setWavelength(double lambda, Units unit) {
		this.lambda = lambda;
		this.lambdaUnit = unit;
	}

	public void solve(Modes modes) {
		if(indexProfile == null)
			throw new NullPointerException("First setup the index profile!") ;
		computeScale() ;
		createMesh() ;
		this.modes = modes ;
		switch (modes) {
		case quasiTE:
			solveQuasiTE();
			break;
		case quasiTM:
			solveQuasiTM();
			break ;
		default:
			break;
		}

	}

	private void computeScale() {
		scale = 1.0 ;
		scale = Conversions.length(scale, gridUnit, lambdaUnit) ;
	}

	private void createMesh() {
		yMin = indexProfile.getLowerBoundary() ;
		yMax = indexProfile.getUpperBoundary() ;
		xMin = indexProfile.getLeftBoundary() ;
		xMax = indexProfile.getRightBoundary() ;
		if(numPointsX == 0 && numPointsY == 0){
			numPointsX = (int) ((xMax-xMin)/dx) ;
			numPointsY = (int) ((yMax-yMin)/dy) ;
		}
		x = MathUtils.linspace(xMin, xMax, numPointsX) ;
		y = MathUtils.linspace(yMin, yMax, numPointsY) ;
		dx = x[2] - x[1] ;
		dy = y[2] - y[1] ;
		index = new double[numPointsX][numPointsY] ;
		epsilon = new double[numPointsX][numPointsY] ;
		for(int i=0; i<x.length; i++) {
			for(int j=0; j<y.length; j++) {
				index[i][j] = indexProfile.getRealIndex(x[i], y[j]) ;
				epsilon[i][j] = index[i][j]*index[i][j] ;
			}
		}
	}

	private void solveQuasiTM() {
		// operator:
		// size: nx * ny
		// field
		double k0 = 2*Math.PI/lambda ;
		double k0Squared = k0*k0 ;
		DiffOperator2D diff = new DiffOperator2D(numPointsX, numPointsY, dx*scale, dy*scale) ;
		Matrix Dxx = diff.getDxxMatrix() ;
		Matrix Dyy = diff.getDyyMatrix() ;
		Matrix operator = (Dxx+Dyy)*1.0/k0Squared + getDiagEpsilon() ;
		EigenvalueDecomposition eig = new EigenvalueDecomposition(new Jama.Matrix(operator.getData())) ;
		double[] eigReal = eig.getRealEigenvalues() ;
		double[] eigIma = eig.getImagEigenvalues() ;
		for(int i=0; i<eigReal.length; i++) {
			Complex eigVal = (eigReal[i] + j*eigIma[i]).sqrt() ;
			if(eigVal.re() < 3.444 && eigVal.re() > 1.444 ) {
				System.out.println(eigVal);
			}
		}
	}
	
	private void solveQuasiTE() {
		// operator:
		// size: nx * ny
		// field
		double k0 = 2*Math.PI/lambda ;
		double k0Squared = k0*k0 ;
		DiffOperator2D diff = new DiffOperator2D(numPointsX, numPointsY, dx*scale, dy*scale) ;
		Matrix Dyy = diff.getDyyMatrix() ;
		Matrix Dx = diff.getDxMatrix() ;
		Matrix operator = 1.0/k0Squared * (getDiagEpsilon()*Dx*getDiagInvEpsilon()*Dx + Dyy) + getDiagEpsilon() ;
		
//		PowerIteration eigen = new RegularIteration() ;
//		EigenValueVector v1 = eigen.solve(operator.getPowerIterationMatrix(), 1e-4) ;
//		System.out.println(v1.eigenValue.sqrt());
		
		EigenvalueDecomposition eig = new EigenvalueDecomposition(new Jama.Matrix(operator.getData())) ;
		double[] eigReal = eig.getRealEigenvalues() ;
		double[] eigIma = eig.getImagEigenvalues() ;
		for(int i=0; i<eigReal.length; i++) {
			Complex eigVal = (eigReal[i] + j*eigIma[i]).sqrt() ;
			if(eigVal.re() < 3.444 && eigVal.re() > 1.444 ) {
				System.out.println(eigVal);
			}
		}
		
		
	}

	public void plotIndexProfile() {
		MeshGrid xyGrid = new MeshGrid(x, y) ;
		ColorMapPlot fig = new ColorMapPlot(xyGrid, index, ColorMapName.Heat) ;
		fig.run(true);
	}

	public void plotIndexProfileXCut(double x) {
		MatlabChart fig = new MatlabChart() ;
		int M = 1000 ;
		double[] yRange = MathUtils.linspace(yMin, yMax, M) ;
		double[] indexXCut = new double[M] ;
		for(int i=0; i<M; i++) {
			indexXCut[i] = indexProfile.getRealIndex(x, yRange[i]) ;
		}
		fig.plot(yRange, indexXCut);
		fig.RenderPlot();
		fig.xlabel("Y " + "(" + gridUnit.name() + ")");
		fig.ylabel("Index");
		fig.run(true);
		fig.markerON();
	}

	public void plotIndexProfileYCut(double y) {
		MatlabChart fig = new MatlabChart() ;
		int M = 1000 ;
		double[] xRange = MathUtils.linspace(xMin, xMax, M) ;
		double[] indexYCut = new double[M] ;
		for(int i=0; i<M; i++) {
			indexYCut[i] = indexProfile.getRealIndex(xRange[i], y) ;
		}
		fig.plot(xRange, indexYCut);
		fig.RenderPlot();
		fig.xlabel("X " + "(" + gridUnit.name() + ")");
		fig.run(true);
		fig.markerON();
	}
	
	private Matrix getDiagEpsilon() {
		double[] diagEps = new double[numPointsX*numPointsY] ;
		for(int i=0; i<numPointsX; i++)
			for(int j=0; j<numPointsY; j++)
				diagEps[i*numPointsY+j] = epsilon[i][j] ;
		
		return Matrix.diag(diagEps) ;
	}
	
	private Matrix getDiagInvEpsilon() {
		double[] diagInvEps = new double[numPointsX*numPointsY] ;
		for(int i=0; i<numPointsX; i++)
			for(int j=0; j<numPointsY; j++)
				diagInvEps[i*numPointsY+j] = 1.0/epsilon[i][j] ;
		
		return Matrix.diag(diagInvEps) ;
	}


	// for test
	public static void main(String[] args) {
		IndexProfile2D profile = new IndexProfile2D() {

			@Override
			public double getUpperBoundary() {
				return 400;
			}

			@Override
			public double getRightBoundary() {
				return 1000;
			}

			@Override
			public double getRealIndex(double x, double y) {
				if(x >=0 && x <=400 && y>=0 && y<=220)
					return 3.477 ;
				else
					return 1.444 ;
			}

			@Override
			public double getLowerBoundary() {
				return -200;
			}

			@Override
			public double getLeftBoundary() {
				return -500;
			}

			@Override
			public double getImagIndex(double x, double y) {
				return 0;
			}
		};

		FDESolver2D fde = new FDESolver2D() ;
		fde.setDebug(true);
		fde.setGrid(40.0, 40.0, Units.nm);
		fde.setIndexProfile(profile);
		fde.setWavelength(1550, Units.nm);
		fde.createMesh();
		fde.plotIndexProfileXCut(100);
		fde.plotIndexProfileYCut(150);
		fde.plotIndexProfile();
		Timer timer = new Timer() ;
		timer.start();
		fde.solve(Modes.quasiTE);
		timer.end();
		System.out.println(timer);
	}


}
