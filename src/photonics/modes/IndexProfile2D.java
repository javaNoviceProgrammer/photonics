package photonics.modes;

public interface IndexProfile2D {
	double getRealIndex(double x, double y) ;
	double getImagIndex(double x, double y) ;
	double getLowerBoundary() ;
	double getUpperBoundary() ;
	double getLeftBoundary() ;
	double getRightBoundary() ;
}
