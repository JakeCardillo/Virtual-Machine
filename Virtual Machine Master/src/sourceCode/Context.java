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
	CApp(JExpr newLhs, JExpr newRhs, Context newC)
	{
		hole = newC;
		lhs = newLhs;
		rhs = newRhs;
	}
	
	//plug hole in context with e
	public JExpr plug(JExpr e)
	{
		JApp app;
		
		//if lhs has children
		if (lhs instanceof JCons)
		{
			JCons nav = (JCons) lhs;
			//find the end of lhs
			while (!(nav.rhs instanceof JNull))
			{
				nav = (JCons) nav.rhs;
			}
			//connect end of lhs with a node holding e and rhs
			nav.rhs = new JCons(hole.plug(e), rhs);
			app = new JApp(((JCons)lhs).lhs, ((JCons)lhs).rhs);
		}
		else
		{
			app = new JApp(lhs, new JCons(hole.plug(e), rhs));
		}
		
		return app;
	}
	
	Context hole;
	JExpr lhs;
	JExpr rhs;
}