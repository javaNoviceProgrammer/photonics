package photonics.modes;

import photonics.util.Units;
import plotter.chart.MatlabChart;

import static mathLib.numbers.Complex.*;

import Jama.Matrix;
import ch.epfl.javancox.results_manager.display.gui.MatlabPlot;
import mathLib.numbers.Complex;
import mathLib.utils.MathUtils;

public class FDESolver1D {
	
	int numPoints = 50 ;
	double lambda, xMin, xMax, dx, scale ;
	IndexProfile1D indexProfile = null ;
	Units gridUnit, lambdaUnit;
	double[] x ;
	double[] Ex, Ey, Ez ;
	double[] Hx, Hy, Hz ;
	double[] index ;
	double[] epsilon ;
	double[][] coeffEy, coeffHy ;
	double[] eigensReal, eigensImag ;
	Complex[] eigens ; 
	
	public FDESolver1D() {
	}
	
	public void setGrid(int numPoints, Units unit) {
		this.numPoints = numPoints ;
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
	
	public void solve() {
		if(indexProfile == null)
			throw new NullPointerException("First setup the index profile!") ;
		computeScale() ;
		createMesh() ;
		solveForEy();
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
		x = MathUtils.linspace(xMin, xMax, numPoints) ;
		dx = x[2] - x[1] ;
		index = new double[numPoints] ;
		epsilon = new double[numPoints] ;
		for(int i=0; i<x.length; i++) {
			index[i] = indexProfile.getIndex(x[i]) ;
			epsilon[i] = index[i]*index[i] ;
		}
	}
	
	private void solveForEy() {
		printDebugInfo();
		// Ey, Hx, Hz
		Ey = new double[numPoints] ;
		Hx = new double[numPoints] ;
		Hz = new double[numPoints] ;
		coeffEy = new double[numPoints][numPoints] ;
		double var1 = 2*Math.PI*dx/lambda*scale ;
		double var2 = var1*var1 ;
		assemble(coeffEy, var2) ;
		Jama.Matrix coeffEyMatrix = new Jama.Matrix(coeffEy) ;
		eigensReal = coeffEyMatrix.eig().getRealEigenvalues() ;
		eigensImag = coeffEyMatrix.eig().getImagEigenvalues() ;
		eigens = new Complex[numPoints] ;
		for(int i=0; i<numPoints; i++) {
			eigens[i] = new Complex(eigensReal[i], eigensImag[i]).sqrt().divides(var1) ;
			System.out.println(eigens[i]);
		}
	}
	
	private void solveForHy() {
		printDebugInfo();
		// Hy, Ex, Ez
		
	}
	
	private void assemble(double[][] coeff, double var) {
		int M = coeff.length ;
		System.out.println(M);
		coeff[0][0] = -2 ;
		coeff[0][1] = 1 ;
		coeff[M-1][M-1] = 1 ;
		coeff[M-1][M-2] = -2 ;
		for(int i=1; i<M-1; i++) {
				coeff[i][i] = -2 ;
				coeff[i][i-1] = 1;
				coeff[i][i+1] = 1;
		}
		for(int i=0; i<M; i++) {
			coeff[i][i] += var*epsilon[i] ;
		}
	}
	
	private void printDebugInfo() {
		System.out.println("xMin = " + xMin + " "+ gridUnit.name());
		System.out.println("xMax = " + xMax + " "+ gridUnit.name());
		System.out.println("dx = " + dx + " "+ gridUnit.name());
		System.out.println("number of grid points = " + numPoints);
		plotIndexProfile();
	}
	
	public void plotIndexProfile() {
		MatlabChart fig = new MatlabChart() ;
		fig.plot(x, index);
		fig.RenderPlot();
		fig.run(true);
		fig.markerON();
	}
	
	
	// for test
	public static void main(String[] args) {
		FDESolver1D fde = new FDESolver1D() ;
		fde.setWavelength(1550.0, Units.nm);
		fde.setGrid(500, Units.nm);
		fde.setIndexProfile(new IndexProfile1D() {
			
			@Override
			public double getUpperBoundary() {
				return 1000.0;
			}
			
			@Override
			public double getLowerBoundary() {
				return -500.0;
			}
			
			@Override
			public double getIndex(double x) {
				if(x<0) return 1.444 ;
				else if(x < 500.0) return 3.4777 ;
				else return 1.444 ;
			}
		});
		fde.computeScale();
		fde.solve();
		
		
		
	}

}
