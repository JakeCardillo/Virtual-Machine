package sourceCode;

public class JPlus implements J0e {
	
	//Constructor
	JPlus(J0e neL, J0e neR)
	{
		eL = neL;
		eR = neR;
	}
	
	//Pretty Printer
	public String pp()
	{
		String plusString;
		
		plusString = "(" + eL.pp() + ") + (" + eR.pp() + ")";	
		return plusString;
	}
	
	//Big-step interpreter
	public int interp()
	{
		return eL.interp() + eR.interp();
	}
	
	J0e eL;
	J0e eR;
}
