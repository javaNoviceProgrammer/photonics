package photonics.util;

import mathLib.util.PhysicalConstants;

public class Wavelength {

	double lambdaNm ;
	double freqHz ;
	final double speedOfLight = PhysicalConstants.SpeedOfLightVacuum() ;

	public Wavelength(
			double lambdaNm
			){
		this.lambdaNm = lambdaNm ;
		this.freqHz = speedOfLight/(lambdaNm*1e-9) ;
	}

	public double getWavelengthNm(){
		return lambdaNm ;
	}
	public double getWavelengthMeter(){
		return (lambdaNm*1e-9) ;
	}

	public double getWavelengthMicron(){
		return (lambdaNm*1e-3) ;
	}

	public double getFreqHz(){
		return freqHz ;
	}

	public double getFreqGHz(){
		return (freqHz * 1e-9) ;
	}

	public double getFreqTHz(){
		return (freqHz*1e-12) ;
	}

	public double getK0(){
		return (2*Math.PI/getWavelengthMeter()) ;
	}

	public double getFreqSpacingHz(double lambdaSpacingNm){
		return speedOfLight/(getWavelengthMeter()*getWavelengthMeter()) * lambdaSpacingNm * 1e-9 ;
	}

	public double getWavelengthSpacingNm(double freqSpacingHz){
		double lambdaSpacingMeter = speedOfLight/(getFreqHz()*getFreqHz()) * freqSpacingHz ;
		return (lambdaSpacingMeter * 1e9) ;
	}
	
	@Override
	public String toString() {
		return getWavelengthNm()+ "" ;
	}

	// ************ operator overloading **********************

	/**
	 * Operator overloading support (must be implemented as static)
	 *
	 * Object a = 5;
	 *
	 */
	public static Wavelength valueOf(int v) {
		return new Wavelength(v) ;
	}

	public static Wavelength valueOf(long v) {
		return new Wavelength(v) ;
	}

	public static Wavelength valueOf(float v) {
		return new Wavelength(v) ;
	}

	public static Wavelength valueOf(double v) {
		return new Wavelength(v) ;
	}

	public static Wavelength valueOf(Wavelength v) {
		return new Wavelength(v.getWavelengthNm()) ;
	}

	/**
	 * Operator overload support: a+b
	 */
	public Wavelength add(Wavelength v) {
		return new Wavelength(this.getWavelengthNm() + v.getWavelengthNm()) ;
	}

	public Wavelength addRev(Wavelength v) {
		return new Wavelength(this.getWavelengthNm() + v.getWavelengthNm()) ;
	}

	public Wavelength add(int v) {
		return new Wavelength(this.getWavelengthNm() + v) ;
	}

	public Wavelength addRev(int v) {
		return new Wavelength(this.getWavelengthNm() + v) ;
	}

	public Wavelength add(long v) {
		return new Wavelength(this.getWavelengthNm() + v) ;
	}

	public Wavelength addRev(long v) {
		return new Wavelength(this.getWavelengthNm() + v) ;
	}

	public Wavelength add(float v) {
		return new Wavelength(this.getWavelengthNm() + v) ;
	}

	public Wavelength addRev(float v) {
		return new Wavelength(this.getWavelengthNm() + v) ;
	}

	public Wavelength add(double v) {
		return new Wavelength(this.getWavelengthNm() + v) ;
	}

	public Wavelength addRev(double v) {
		return new Wavelength(this.getWavelengthNm() + v) ;
	}

	/**
	 * Operator overload support: a-b
	 */
	public Wavelength subtract(Wavelength v) {
		return new Wavelength(this.getWavelengthNm() - v.getWavelengthNm());
	}

	public Wavelength subtractRev(Wavelength v) {
		return new Wavelength(v.getWavelengthNm() - this.getWavelengthNm());
	}

	public Wavelength subtract(int v) {
		return new Wavelength(this.getWavelengthNm() - v);
	}

	public Wavelength subtractRev(int v) {
		return new Wavelength(v - this.getWavelengthNm());
	}

	public Wavelength subtract(long v) {
		return new Wavelength(this.getWavelengthNm() - v);
	}

	public Wavelength subtractRev(long v) {
		return new Wavelength(v - this.getWavelengthNm());
	}

	public Wavelength subtract(float v) {
		return new Wavelength(this.getWavelengthNm() - v);
	}

	public Wavelength subtractRev(float v) {
		return new Wavelength(v - this.getWavelengthNm());
	}

	public Wavelength subtract(double v) {
		return new Wavelength(this.getWavelengthNm() - v);
	}

	public Wavelength subtractRev(double v) {
		return new Wavelength(v - this.getWavelengthNm());
	}

	/**
	 * Operator overload support: a*b
	 */
	public Wavelength multiply(Wavelength v) {
		return new Wavelength(this.getWavelengthNm()*v.getWavelengthNm()) ;
	}

	public Wavelength multiplyRev(Wavelength v) {
		return new Wavelength(this.getWavelengthNm()*v.getWavelengthNm()) ;
	}

	public Wavelength multiply(int v) {
		return new Wavelength(this.getWavelengthNm()*v) ;
	}

	public Wavelength multiplyRev(int v) {
		return new Wavelength(this.getWavelengthNm()*v) ;
	}

	public Wavelength multiply(long v) {
		return new Wavelength(this.getWavelengthNm()*v) ;
	}

	public Wavelength multiplyRev(long v) {
		return new Wavelength(this.getWavelengthNm()*v) ;
	}

	public Wavelength multiply(float v) {
		return new Wavelength(this.getWavelengthNm()*v) ;
	}

	public Wavelength multiplyRev(float v) {
		return new Wavelength(this.getWavelengthNm()*v) ;
	}

	public Wavelength multiply(double v) {
		return new Wavelength(this.getWavelengthNm()*v) ;
	}

	public Wavelength multiplyRev(double v) {
		return new Wavelength(this.getWavelengthNm()*v) ;
	}

	/**
	 * Operator overload support: a/b
	 */
	public Wavelength divide(Wavelength v) {
		return new Wavelength(this.getWavelengthNm()/v.getWavelengthNm()) ;
	}

	public Wavelength divideRev(Wavelength v) {
		return new Wavelength(v.getWavelengthNm()/this.getWavelengthNm()) ;
	}

	public Wavelength divide(int v) {
		return new Wavelength(this.getWavelengthNm()/v) ;
	}

	public Wavelength divideRev(int v) {
		return new Wavelength(v/this.getWavelengthNm()) ;
	}

	public Wavelength divide(long v) {
		return new Wavelength(this.getWavelengthNm()/v) ;
	}

	public Wavelength divideRev(long v) {
		return new Wavelength(v/this.getWavelengthNm()) ;
	}

	public Wavelength divide(float v) {
		return new Wavelength(this.getWavelengthNm()/v) ;
	}

	public Wavelength divideRev(float v) {
		return new Wavelength(v/this.getWavelengthNm()) ;
	}

	public Wavelength divide(double v) {
		return new Wavelength(this.getWavelengthNm()/v) ;
	}

	public Wavelength divideRev(double v) {
		return new Wavelength(v/this.getWavelengthNm()) ;
	}

	/**
	 * Operator overload support: -a
	 */
	public Wavelength negate() {
		return new Wavelength(-this.getWavelengthNm());
	}
	
	// for test
	public static void main(String[] args) {
		Wavelength lambda1 = 1550 ;
		double R = 10e3 ;
		System.out.println(lambda1*lambda1/(2*Math.PI*R*4.2));
	}

}
