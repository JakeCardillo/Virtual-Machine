package sourceCode;

import java.util.Stack;

public class Main {

	public static void main(String[] args) {

		conTest(JN(4), JN(4));
		conTest(JA("+", JN(7), JA("+", JN(7),JN(9))), JN(23)); //no worky
		conTest(JA("*", JN(7),JN(9)), JN(63));
		conTest(JA("*", JA("+", JN(8), JN(3)), JN(2)), JN(22)); //no worky
		conTest(JI(JB(true), JN(2), JN(3)), JN(2)); //no worky
		
		test(SA(SA(SA(SA(SN(8), SN(3)), SN(9)), SN(2)), SN(4)), new JNum(26));
		test(SM(SN(9), SN(0)), new JNum(0));
		test(SS(SN(5), SN(3)), new JNum(2));
		test(SA(SN(42),SN(0)), new JNum(42));
		test(SM(SN(42),SN(0)), new JNum(0));
		test(SA(SM(SN(42),SN(0)),SN(0)), new JNum(0));
		test(SA(SM(SN(42),SN(0)),SA(SM(SN(42),SN(0)),SN(0))), new JNum(0));
		test(SM(SN(8), SS(SN(7), SA(SN(6), SN(3)))), new JNum(-16));
		test(SA(SN(7), SM(SN(9), SN(9))), new JNum(88));
		
		conTest((JExpr) JA("+", JN(45), JN(3)), JN(48));

		System.out.println( testsPassed + " tests passed");
	}

	static JExpr JN(int n) {
		return new JNum(n); }
	static JExpr JB(Boolean b) {
		return new JBool(b); }
	static JExpr JA(String prim, JExpr lhs, JExpr rhs) {
		return new JApp(new JPrim(prim), new JCons(lhs, new JCons(rhs, new JNull()))); }
	static JExpr JI(JExpr cond, JExpr tbr, JExpr fbr) {
		return new JIf(cond, tbr, fbr); }


	static Sexpr SN(int n) {
		return new SE_Num(n); }
	static Sexpr SA(Sexpr lhs, Sexpr rhs) {
		return SApp("+", lhs, rhs); }
	static Sexpr SM(Sexpr lhs, Sexpr rhs) {
		return SApp("*", lhs, rhs); }
	static Sexpr SS(Sexpr lhs, Sexpr rhs) {
		return SApp("-", lhs, rhs); }
	static Sexpr SIf(Sexpr cond, Sexpr lhs, Sexpr rhs) {
	    return new SE_Cons(new SE_Str("if"),
	                       new SE_Cons(cond,
	                                   new SE_Cons(lhs,
	                                               new SE_Cons(rhs,
	                                                           new SE_MT())))); }

	static Sexpr SApp(String op, Sexpr lhs, Sexpr rhs) {
		return new SE_Cons(new SE_Str(op),
				new SE_Cons(lhs,
						new SE_Cons(rhs,
								new SE_MT()))); }


	static JExpr desugar(Sexpr se)
	{
		//'n = n
		if ( se instanceof SE_Num)
			return new JNum(((SE_Num) se).n);

		//'+ = 0
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("+")
				&& ((SE_Cons)se).rhs instanceof SE_MT )
			return new JNum(0);

		//'* = 1
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("*")
				&& ((SE_Cons)se).rhs instanceof SE_MT )
			return new JNum(1);

		//('+ lhs rhs ...) = (+ desugar(lhs) desugar(('+ rhs ...))
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("+")
				&& ((SE_Cons)se).rhs instanceof SE_Cons )
			return JA( "+", desugar(((SE_Cons)((SE_Cons)se).rhs).lhs),
					desugar(new SE_Cons(((SE_Cons)se).lhs, 
							((SE_Cons)((SE_Cons)se).rhs).rhs)));

		//('* lhs rhs) = (+ desugar(lhs) desugar(rhs))
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("*")
				&& ((SE_Cons)se).rhs instanceof SE_Cons )
			return JA( "*", desugar(((SE_Cons)((SE_Cons)se).rhs).lhs),
					desugar(new SE_Cons(((SE_Cons)se).lhs, 
							((SE_Cons)((SE_Cons)se).rhs).rhs)));

		//Negation
		if ( se instanceof SE_Cons 
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("-")
				&& ((SE_Cons)se).rhs instanceof SE_Cons
				&& ((SE_Cons)((SE_Cons)se).rhs).rhs instanceof SE_MT)
			return JA( "*", new JNum(-1), 
					desugar(((SE_Cons)((SE_Cons)se).rhs).lhs) );

		//Subtraction
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.contentEquals("-")
				&& ((SE_Cons)se).rhs instanceof SE_Cons )
			return JA( "+", desugar(((SE_Cons)((SE_Cons)se).rhs).lhs),
					desugar(new SE_Cons(((SE_Cons)se).lhs, 
							((SE_Cons)((SE_Cons)se).rhs).rhs)));

		//App
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Cons)se).rhs instanceof SE_Cons
				&& ((SE_Cons)((SE_Cons)se).rhs).rhs instanceof SE_Cons
				&& ((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).rhs instanceof SE_MT) {
			return new JApp( new JPrim(((SE_Str)((SE_Cons)se).lhs).s),
					new JCons(desugar(((SE_Cons)((SE_Cons)se).rhs).lhs),
							new JCons(desugar(((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).lhs), new JNull()))); }

		//If
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("if")
				&& ((SE_Cons)se).rhs instanceof SE_Cons
				&& ((SE_Cons)((SE_Cons)se).rhs).rhs instanceof SE_Cons
				&& ((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).rhs instanceof SE_Cons
				&& ((SE_Cons)((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).rhs).rhs instanceof SE_MT ) {
			return new JIf( desugar(((SE_Cons)((SE_Cons)se).rhs).lhs),
					desugar(((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).lhs),
					desugar(((SE_Cons)((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).rhs).lhs) ); }

		//Error
		return new JNum(42069);
	}

	static int testsPassed = 0;

	static void test(Sexpr se, JExpr expected)
	{
		JExpr e = desugar(se);

		System.out.println(se.pp() + " desugars to " + e.pp());

		JExpr actual = e.interp();
		JExpr expVal = expected.interp();

		System.out.println("Actual: " + actual.pp() + "  Expected: " + expVal.pp());

		if (!actual.pp().equals( expVal.pp()))
			System.out.println("TEST FAILED");
		else
			testsPassed++;
	}
	
	static void conTest(JExpr e, JExpr real)
	{
		JExpr given = interp(e);
		JExpr big = Interp(e);
		
		System.out.println("Real: " + real.pp() + " Given: " + given.pp() + " Big: " + big.pp());
		
		if (!given.isValue())
		{
			System.out.println("TEST FAILED");
			return;
		}
		else if (given.pp().equals(real.pp()) && big.pp().equals(real.pp()))
			testsPassed++;
		else
			System.out.println("TEST FAILED");
		
		return;
	}
	
	static JExpr findRedex(Context C, JExpr e)
	{
		if (e.isValue())
		{
			C = new CHole();
			return e;
		}
		
	
		if (e instanceof JIf)
		{
			//if ec is a value, return 
			if (((JIf)e).cond.isValue())
			{
				C = new CHole();
				return e;
			}
			else
			{
				JExpr redex = findRedex(C, ((JIf)e).cond);
				C = new CIf(((JIf)e).tbr, ((JIf)e).fbr, C);
				return redex;
			}
		}
		
		if (e instanceof JApp)
		{
			if(((JCons)((JApp)e).args).lhs.isValue() == false) {
                JExpr redex = findRedex(C, ((JCons)((JApp)e).args).lhs);
                C = new CApp(C, ((JApp)e).fun, new JNull(), ((JCons)((JCons)((JApp)e).args).rhs).lhs);
                return redex;
            }
            if(((JCons)((JCons)((JApp)e).args).rhs).lhs.isValue() == false) {
                JExpr redex = findRedex(C, ((JCons)((JCons)((JApp)e).args).rhs).lhs);
                C = new CApp(C, ((JApp)e).fun, ((JCons)((JApp)e).args).lhs, new JNull());
                return redex;
            }
		}
		
		return e;
	}
	
	static JExpr interp(JExpr e)
	{
		 Context C = new CHole();
		 JExpr e1 = findRedex(C, e);
		 
		 System.out.println("e1: " + e1.pp());
		 
		 JExpr e2 = e1.step();
		 
		 System.out.println("e2: " + e2.pp());
		 
		 return C.plug(e2);
	}
	
	static JExpr Interp(JExpr e)
	{
		JExpr e1 = interp(e);
		if (e == e1)
			return e;
		else
			return Interp(e1);
	}
}


