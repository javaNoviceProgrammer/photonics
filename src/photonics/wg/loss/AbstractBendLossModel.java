package photonics.wg.loss;

public abstract class AbstractBendLossModel {
		
		public abstract double getLossdBperCm(double radiusMicron) ;
		public abstract double getLossPerCm(double radiusMicron) ;
		public abstract double getLossPerMeter(double radiusMicron) ;
		
		public abstract double getAbsorptionLossdBperCm(double radiusMicron) ;
		public abstract double getAbsorptionLossPerCm(double radiusMicron) ;
		public abstract double getAbsorptionLossPerMeter(double radiusMicron) ;
}
