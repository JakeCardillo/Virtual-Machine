package sourceCode;

public class Define {
	JExpr fun;
	JExpr params;
	JExpr exp;
	
	Define(JExpr fun, JExpr params, JExpr exp) {
		this.fun = fun;
		this.params = params;
		this.exp = exp;
	}
	
	public String pp() {
		return ("define (" + fun.pp() + ", " + params.pp() + ") (" + exp.pp() + ")");
	}
}
