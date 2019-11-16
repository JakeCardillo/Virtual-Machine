package sourceCode;

public interface JExpr {
	public Boolean isValue();
	public String pp();
	public JExpr interp();
	public JExpr step();
	public JExpr subst(JVar x, JExpr v);
}

class JNull implements JExpr {
	public String pp() { 
		return "NULL"; }
	public JNull() { }
	public Boolean isValue() { 
		return true; }
	public JExpr interp() { 
		return this; } 
	public JExpr step() {
		return this; }
	public JExpr subst(JVar x, JExpr v) {
		return this;
	}
}

class JCons implements JExpr {
	public JExpr lhs, rhs;
	public String pp() { 
		return "(" + this.lhs.pp() + " " + this.rhs.pp() + ")"; }
	public JCons(JExpr lhs, JExpr rhs) {
		this.lhs = lhs;
		this.rhs = rhs; }
	public Boolean isValue() { 
		return false; }
	public JExpr interp() {
		return new JCons(this.lhs.interp(), this.rhs.interp()); }
	public JExpr step() {
		return lhs.step(); }
	public JExpr subst(JVar x, JExpr v) {
		return new JCons(lhs.subst(x, v), rhs.subst(x, v));
	}
}

class JPrim implements JExpr {
	public String p;
	public JPrim(String p) {
		this.p = p; }
	public Boolean isValue() { 
		return true; }
	public String pp() {
		return "" + this.p; }
	public JExpr interp() {
		return this; } 
	public JExpr step() {
		return this; }
	public JExpr subst(JVar x, JExpr v) {
		return this;
	}
}

class JNum implements JExpr {
	public int n;
	public JNum(int n) {
		this.n = n; }
	public Boolean isValue() { 
		return true; }
	public String pp() {
		return "" + this.n; }
	public JExpr interp() {
		return this; } 
	public JExpr step() {
		return this; }
	public JExpr subst(JVar x, JExpr v) {
		return this;
	}
}

class JBool implements JExpr {
	public Boolean b;
	public JBool(Boolean b) {
		this.b = b; }
	public Boolean isValue() { 
		return true; }
	public String pp() {
		return "" + this.b; }
	public JExpr interp() {
		return this; } 
	public JExpr step() {
		return this; }
	public JExpr subst(JVar x, JExpr v) { 
		return this;
	}
}

class JIf implements JExpr {
	public JExpr cond, tbr, fbr;
	public JIf(JExpr cond, JExpr tbr, JExpr fbr) {
		this.cond = cond;
		this.tbr = tbr;
		this.fbr = fbr; }
	public Boolean isValue() { 
		return false; }
	public String pp() {
		return "(if " + this.cond.pp() + " " + this.tbr.pp() + " " + this.fbr.pp() + ")"; }
	public JExpr interp() {
		JExpr condv = this.cond.interp();
		if ( condv instanceof JBool
				&& ((JBool)condv).b == false ) {
			return this.fbr.interp(); }
		else {
			return this.tbr.interp(); } } 

	public JExpr step()
	{
		if ( cond instanceof JBool)
			if (cond == new JBool(true))
				return tbr;
			else
				return fbr;
		else
		{
			JExpr newCond = cond.step();
			cond = newCond;
			return this;
		}

	}
	public JExpr subst(JVar x, JExpr v) {
		return new JIf(cond.subst(x, v), tbr.subst(x, v), fbr.subst(x, v));
	}
}

class JApp implements JExpr {
	public JExpr fun, args;
	public JApp(JExpr fun, JExpr args) {
		this.fun = fun;
		this.args = args; }
	public Boolean isValue() { 
		return false; }
	public String pp() {
		return "(@ " + this.fun.pp() + " " + this.args.pp() + ")"; }
	public JExpr interp() {
		JExpr which_fun = this.fun.interp();
		JExpr arg_vals = this.args.interp();

		String p = ((JPrim)which_fun).p;
		int lhs = ((JNum)((JCons)arg_vals).lhs).n;
		int rhs = ((JNum)((JCons)((JCons)arg_vals).rhs).lhs).n;

		if ( p.equals("+") ) { return new JNum(lhs + rhs); }
		if ( p.equals("*") ) { return new JNum(lhs * rhs); }
		if ( p.equals("/") ) { return new JNum(lhs / rhs); }
		if ( p.equals("-") ) { return new JNum(lhs - rhs); }
		if ( p.equals("<") ) { return new JBool(lhs < rhs); }
		if ( p.equals("<=") ) { return new JBool(lhs <= rhs); }
		if ( p.equals("==") ) { return new JBool(lhs == rhs); }
		if ( p.equals(">") ) { return new JBool(lhs > rhs); }
		if ( p.equals(">=") ) { return new JBool(lhs >= rhs); }
		if ( p.equals("!=") ) { return new JBool(lhs != rhs); }

		return new JNum(666); }

	public JExpr step() {
		if (args instanceof JCons)
		{
			if (!((JCons) args).lhs.isValue())
			{
				((JCons) args).lhs = ((JCons) args).lhs.step();
				return this;
			}
			if (!((JCons) ((JCons) args).rhs).lhs.isValue())
			{
				((JCons) ((JCons) args).rhs).lhs = ((JCons) ((JCons) args).rhs).lhs.step();
				return this;
			}

			String p = ((JPrim)fun).p;
			int lhs = ((JNum)((JCons)args).lhs).n;
			int rhs = ((JNum)((JCons)((JCons)args).rhs).lhs).n;

			if ( p.equals("+") ) { return new JNum(lhs + rhs); }
			if ( p.equals("*") ) { return new JNum(lhs * rhs); }
			if ( p.equals("/") ) { return new JNum(lhs / rhs); }
			if ( p.equals("-") ) { return new JNum(lhs - rhs); }
			if ( p.equals("<") ) { return new JBool(lhs < rhs); }
			if ( p.equals("<=") ) { return new JBool(lhs <= rhs); }
			if ( p.equals("==") ) { return new JBool(lhs == rhs); }
			if ( p.equals(">") ) { return new JBool(lhs > rhs); }
			if ( p.equals(">=") ) { return new JBool(lhs >= rhs); }
			if ( p.equals("!=") ) { return new JBool(lhs != rhs); }

			return new JNum(666); 
		}
		return this;
	}
	public JExpr subst(JVar x, JExpr v) {
		return new JApp(fun.subst(x, v), args.subst(x, v));
	}
}

class lambda implements JExpr
{
	lambda(String Name, JExpr vars, JExpr e) {
		this.Name = Name;
		this.vars = vars;
		this.e = e;
	}
		
	public Boolean isValue() {
		return true; }
	public String pp() {
		return "(" + Name + " " + vars.pp() + " " + e.pp() + ")";
	}
	public JExpr interp() {
		return this;
	}
	public JExpr step() {
		return this;
	}
	public JExpr subst(JVar x, JExpr v) {
		return this.e.subst(x, v);
	}
	
	String Name;
	public JExpr vars;
	public JExpr e;
}

class JVar implements JExpr
{
	JVar(String name) {
		this.name = name;
	}
	
	public Boolean isValue() {
		return false; }
	public String pp() {
		return name;
	}
	public JExpr interp() {
		return this;
	}
	public JExpr step() {
		return this;
	}
	public JExpr subst(JVar x, JExpr v) { 
		if (this.name.equals(x.name))
			return v;
		else
			return this;
	}
	
	String name;
}