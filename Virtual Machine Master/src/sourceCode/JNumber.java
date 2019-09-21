package sourceCode;

public class JNumber implements J0e {
	
	//Constructor
	JNumber(int n)
	{
		num = n;
	}
	
	//Pretty Printer
	public String pp()
	{
		return Integer.toString(num);
	}
	
	//Big-step interpreter
	public int interp()
	{
		return num;
	}
	
	int num;
}