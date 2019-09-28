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

//(if C e e)
//(if cond lhs rhs)
class CIf0 implements Context {
	CIf0(JExpr newLhs, JExpr newRhs, Context newC)
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

//(if e C e)
//(if cond lhs rhs)
class CIf1 implements Context {
	CIf1(JExpr newCond, JExpr newRhs, Context newC)
	{
		lhs = newC;
		cond = newCond;
		rhs = newRhs;
	}
	
	//plug hole in context with e
	public JExpr plug(JExpr e)
	{
		return new JIf(cond, lhs.plug(e), rhs);
	}
	
	JExpr cond;
	Context lhs;
	JExpr rhs;
}

//(if e e C)
//(if cond lhs rhs)
class CIf2 implements Context {
	CIf2(JExpr newLhs, JExpr newCond, Context newC)
	{
		rhs = newC;
		lhs = newLhs;
		cond = newCond;
	}
	
	//plug hole in context with e
	public JExpr plug(JExpr e)
	{
		return new JIf(cond, lhs, rhs.plug(e));
	}
	
	JExpr cond;
	JExpr lhs;
	Context rhs;
}

//(e ... C e ...)
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