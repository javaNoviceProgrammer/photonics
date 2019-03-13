package photonics.interconnect.solver;

public class SparamParser {
	
	public static String parse(String st) {
		try {
			String st0 = st.trim() ;
			String[] st1 = st0.split(":") ;
			String st2 = st1[1].trim() ;
			String[] st3 = st2.split(" ") ;
			StringBuilder out = new StringBuilder() ;
			out.append(st1[0]+": ") ;
			int m = st3.length ;
			for(int i=0; i<m-1; i++) {
				String[] st4 = st3[i].split("\\.") ; // "." is a special character and needs to be used with \\.
				String[] st5 = st3[i+1].split("\\.") ;
				if(st4[0].equals(st5[0])) {
					String st6 = "S" + st5[1].charAt(4) + st4[1].charAt(4) + "(" + st4[0] + ")"  ;
					out.append(st6 + " ") ;
				}
			}
			return out ;
		} catch (Exception e) {
			return st ;
		}
	}
	
	// for test
	public static void main(String[] args) {
		String st = "L1: cwg1.port1.in cwg1.port2.out lr1.port2.in lr1.port2.out cwg1.port2.in "
					+ "cwg1.port1.out dc1.port4.in dc1.port3.out lr1.port1.in lr1.port1.out dc1.port3.in dc1.port4.out cwg1.port1.in" ;
		System.out.println(SparamParser.parse(st));
	}

}
