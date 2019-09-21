package sourceCode;

public class JMult implements J0e {
	
	//Constructor
	JMult(J0e neL, J0e neR)
	{
		eL = neL;
		eR = neR;
	}
	
	//Pretty Printer
	public String pp()
	{
		String multString;
		
		multString = "(" + eL.pp() + ") * (" + eR.pp() + ")";	
		return multString;
	}
	
	//Big-step interpreter
	public int interp()
	{
		return eL.interp() * eR.interp();
	}
	
	J0e eL;
	J0e eR;
}

