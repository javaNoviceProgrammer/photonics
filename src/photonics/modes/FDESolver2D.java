package photonics.modes;

import java.util.ArrayList;

import mathLib.numbers.Complex;
import mathLib.util.MathUtils;
import mathLib.util.Units;
import photonics.util.Modes;

public class FDESolver2D {

	int numPoints, numModes;
	double lambda, xMin, xMax, dx, yMin, yMax, dy, scale;
	IndexProfile1D indexProfile = null;
	Units gridUnit, lambdaUnit;
	double[] x;
	double[] index;
	double[] epsilon;
	double[][] coeffEy, coeffHy;
	ArrayList<Complex[]> Ex, Ey, Ez;
	ArrayList<Complex[]> Hx, Hy, Hz;
	ArrayList<Complex> neff;
	boolean debug = false;
	Modes modes;

	public FDESolver2D() {
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setGrid(int numPoints, Units unit) {
		this.numPoints = numPoints;
		this.gridUnit = unit;
	}

	public void setGrid(double dx, Units unit) {
		this.dx = dx;
		this.gridUnit = unit;
	}

	public void setIndexProfile(IndexProfile1D profile) {
		this.indexProfile = profile;
	}

	public void setWavelength(double lambda, Units unit) {
		this.lambda = lambda;
		this.lambdaUnit = unit;
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



}
