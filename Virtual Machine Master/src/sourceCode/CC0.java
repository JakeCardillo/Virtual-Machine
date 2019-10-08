package sourceCode;

public class CC0 {
	public static state inject(JExpr e) {
		Context E = new CHole();
		return new state(e, E);
	}

	public static JExpr extract(state s) {
		return s.E.plug(s.e);
	}

	public static state step(state s) {
		if(s.e instanceof JIf)
			return new state(((JIf)s.e).cond, new CIf(((JIf)s.e).tbr, ((JIf)s.e).fbr, new CHole()));
		if(s.e instanceof JBool && ((JBool)s.e).b == true && s.E instanceof CIf)
			return new state(((CIf)s.E).lhs, new CHole());
		if(s.e instanceof JBool && ((JBool)s.e).b == false && s.E instanceof CIf)
			return new state(((CIf)s.E).rhs, new CHole());

		if(s.e instanceof JApp)
			return new state(((JCons)((JApp)s.e).args).lhs, new CApp(new CHole(), ((JApp)s.e).fun, new JNull(), ((JCons)((JApp)s.e).args).rhs));
		if(s.e.isValue() && s.E instanceof CApp && ((CApp)s.E).lhs instanceof JNull)
			return new state(((CApp)s.E).rhs, new CApp(new CHole(), ((JApp)s.e).fun, s.e, new JNull()));
		if(s.e.isValue() && s.E instanceof CApp && ((CApp)s.E).rhs instanceof JNull)
			return new state(delta(s.E.plug(s.e)), new CHole());
		return new state(new JNum(6969), new CHole());
	}
	
	static private JExpr delta(JExpr e)
	{
		JExpr fun = ((JApp)e).fun;
		JExpr args = ((JApp)e).args;
		
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
}

class state {
	JExpr e;
	Context E;
	public state(JExpr e, Context E) {
		this.e = e;
		this.E = E;
	}
}