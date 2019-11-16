package sourceCode;

public class Define {
	lambda fun;
	JExpr exp;
	
	Define(lambda fun, JExpr exp) {
		this.fun = fun;
		this.exp = exp;
	}
	
	public String pp() {
		return ("define " + fun.pp() + "(" + exp.pp() + ")");
	}
}
