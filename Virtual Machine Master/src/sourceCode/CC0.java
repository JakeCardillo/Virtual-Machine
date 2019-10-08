package sourceCode;

public class CC0 {
	private static Context last;
	
	public static state inject(JExpr e) {
		Context E = new CHole();
		return new state(e, E);
	}

	public static JExpr extract(state s) {
		return s.E.plug(s.e);
	}

	public static state step(state s) {
		//JIf
		if(s.e instanceof JIf)
		{
			if (last instanceof CHole)
			{
				last = new CIf(((JIf)s.e).tbr, ((JIf)s.e).fbr, new CHole());
				return new state(((JIf)s.e).cond, last);
			}
			else if (last instanceof CIf)
			{
				((CIf)last).cond = new CIf(((JIf)s.e).tbr, ((JIf)s.e).fbr, new CHole());
				last = ((CIf)last).cond;
				return new state(((JIf)s.e).cond, s.E);
			}
			else if (last instanceof CApp)
			{
				((CApp)last).hole = new CIf(((JIf)s.e).tbr, ((JIf)s.e).fbr, new CHole());
				last = ((CApp)last).hole;
				return new state(((JIf)s.e).cond, s.E);
			}
		}
		
		//JIf true
		if(s.e instanceof JBool && ((JBool)s.e).b == true && last instanceof CIf) {
            JExpr nexte = new JNull();
            Context prev = null;
            Context temp = s.E;
            Context next = new CHole();

            nexte = ((CIf)last).lhs;

            if(s.E == last)
                s.E = new CHole();
            else {
                if(s.E instanceof CApp)
                    next = ((CApp)s.E).hole;
                else
                    next = ((CIf)s.E).cond;
                while(!(next instanceof CHole)) {
                    prev = temp;
                    temp = next;
                    if(temp instanceof CApp)
                        next = ((CApp)temp).hole;
                    else
                        next = ((CIf)temp).cond;
                }
                last = prev;
                if(last instanceof CApp)
                    ((CApp)last).hole = new CHole();
                else
                    ((CIf)last).cond = new CHole();
            }
            return new state(nexte, s.E);
        }
		
		//JIf false
		if(s.e instanceof JBool && ((JBool)s.e).b == false && last instanceof CIf) {
            JExpr nexte = new JNull();
            Context prev = null;
            Context temp = s.E;
            Context next = new CHole();

            nexte = ((CIf)last).rhs;

            if(s.E == last)
                s.E = new CHole();
            else {
                if(s.E instanceof CApp)
                    next = ((CApp)s.E).hole;
                else
                    next = ((CIf)s.E).cond;
                while(!(next instanceof CHole)) {
                    prev = temp;
                    temp = next;
                    if(temp instanceof CApp)
                        next = ((CApp)temp).hole;
                    else
                        next = ((CIf)temp).cond;
                }
                last = prev;
                if(last instanceof CApp)
                    ((CApp)last).hole = new CHole();
                else
                    ((CIf)last).cond = new CHole();
            }
            return new state(nexte, s.E);
        }
		
		//JApp
		if(s.e instanceof JApp) {
            if(last instanceof CHole) {
                last = new CApp(new CHole(), ((JApp)s.e).fun, new JNull(), ((JCons)((JCons)((JApp)s.e).args).rhs).lhs);
                return new state(((JCons)((JApp)s.e).args).lhs, last);
            }
            else if(last instanceof CIf){
                ((CIf)last).cond = new CApp(new CHole(), ((JApp)s.e).fun, new JNull(), ((JCons)((JCons)((JApp)s.e).args).rhs).lhs);
                last = ((CIf)last).cond;
                return new state(((JCons)((JApp)s.e).args).lhs, s.E);
            }
            else if(last instanceof CApp){
                ((CApp)last).hole = new CApp(new CHole(), ((JApp)s.e).fun, new JNull(), ((JCons)((JCons)((JApp)s.e).args).rhs).lhs);
                last = ((CApp)last).hole;
                return new state(((JCons)((JApp)s.e).args).lhs, s.E);
            }
        }
		
		//JApp hole first
		if(s.e.isValue() && last instanceof CApp && ((CApp)last).lhs instanceof JNull) {
            JExpr r = ((CApp)last).rhs;
            ((CApp)last).lhs = s.e;
            ((CApp)last).rhs = new JNull();
            return new state(r, s.E);
        }
		//hole second
		if(s.e.isValue() && last instanceof CApp && ((CApp)last).rhs instanceof JNull) {
            JExpr nexte = new JNull();
            Context prev = null;
            Context temp = s.E;
            Context next = new CHole();

            nexte = delta(last.plug(s.e));

            // fix the stack
            if(s.E == last)
                s.E = new CHole();
            else {
                if(s.E instanceof CApp)
                    next = ((CApp)s.E).hole;
                else
                    next = ((CIf)s.E).cond;
                while(!(next instanceof CHole)) {
                    prev = temp;
                    temp = next;
                    if(temp instanceof CApp)
                        next = ((CApp)temp).hole;
                    else
                        next = ((CIf)temp).cond;
                }
                last = prev;
                if(last instanceof CApp)
                    ((CApp)last).hole = new CHole();
                else
                    ((CIf)last).cond = new CHole();
            }
            return new state(nexte, s.E);
        }

        return new state(new JNum(6969), new CHole());
	
	}
	
	static public JExpr interp(JExpr e)
	{
		state s = inject(e);
		last = s.E;
		s = step(s);
		
		while (s.e.isValue() == false || !(s.E instanceof CHole))
			s = step(s);
		
		return extract(s);
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