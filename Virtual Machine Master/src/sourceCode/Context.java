package sourceCode;

public interface Context {
	//plug hole in context with e
	public JExpr plug(JExpr e);
}

//Hole
class CHole implements Context {
	//plug hole in context with e
	public JExpr plug(JExpr e)
	{
		return e;
	}
}

//(if E e e)
//(if cond lhs rhs)
class CIf implements Context {
	CIf(JExpr newLhs, JExpr newRhs, Context newC)
	{
		cond = newC;
		lhs = newLhs;
		rhs = newRhs;
	}
	
	//plug hole in context with e
	public JExpr plug(JExpr e)
	{
		return new JIf(cond.plug(e), lhs, rhs);
	}
	
	Context cond;
	JExpr lhs;
	JExpr rhs;
}

//(e ... E e ...)
//(lhs ... hole rhs)
class CApp implements Context {
	Context hole;
	JExpr fun;
	JExpr lhs;
	JExpr rhs;
	
	public CApp(Context C, JExpr func, JExpr left, JExpr right)
	{
		hole = C;
		fun = func;
		lhs = left;
		rhs = right;
	}
	
	public JExpr plug(JExpr x)
	{
		if (lhs instanceof JNull)
			return new JApp(fun, new JCons(x, new JCons(rhs, new JNull())));
		else
			return new JApp(fun, new JCons(lhs, new JCons(x, new JNull())));
	}
}