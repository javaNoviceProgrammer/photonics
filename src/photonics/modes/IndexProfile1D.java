package photonics.modes;

public interface IndexProfile1D {
	
	double getRealIndex(double x) ;
	double getImagIndex(double x) ;
	double getLowerBoundary() ;
	double getUpperBoundary() ;

}
