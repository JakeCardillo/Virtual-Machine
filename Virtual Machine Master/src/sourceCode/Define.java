package sourceCode;

public class Define {
	JFun fun;
	JExpr exp;
	
	Define(JFun fun, JExpr exp) {
		this.fun = fun;
		this.exp = exp;
	}
	
	public String pp() {
		return ("define " + fun.pp() + "(" + exp.pp() + ")");
	}
}
