package photonics.modes;

import photonics.util.Units;

public class FDESolver1D {
	
	int numPoints = 1000 ;
	double lambdaNm, xMin, xMax, dx, scale ;
	IndexProfile1D indexProfile = null ;
	Units gridUnit, lambdaUnit;
	double[] x ;
	double[] Ex, Ey, Ez ;
	double[] Hx, Hy, Hz ;
	double[] index ;
	double[] epsilon ;
	
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
		if(unit.equals(Units.nm))
			System.out.println("lambda (nm) = " + lambda);
		else 
			System.out.println("lambda (um) = " + lambda);
	}
	
	public void solve() {
		if(indexProfile == null)
			throw new NullPointerException("First setup the index profile!") ;
		computeScale() ;
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
	
	private void solveForEy() {
		// Ey, Hx, Hz
		
	}
	
	private void solveForHy() {
		// Hy, Ex, Ez
		
	}
	
	
	// for test
	public static void main(String[] args) {
		FDESolver1D fde = new FDESolver1D() ;
		fde.setWavelength(1550.0, Units.um);
		fde.solve();
	}

}
