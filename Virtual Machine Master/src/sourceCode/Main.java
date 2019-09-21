package sourceCode;

public class Main {

	public static void main(String[] args) {

		test(SN(4), new JNumber(4));
		test(SA(SN(7),SN(9)), new JNumber(16));
		test(SM(SA(SN(8), SN(3)), SN(2)), new JNumber(22));
		test(SA(SA(SA(SA(SN(8), SN(3)), SN(9)), SN(2)), SN(4)), new JNumber(26));
		test(SM(SN(9), SN(0)), new JNumber(0));
		test(SS(SN(5), SN(3)), new JNumber(2));
		test(SA(SN(42),SN(0)), new JNumber(42));
	    test(SM(SN(42),SN(0)), new JNumber(0));
	    test(SA(SM(SN(42),SN(0)),SN(0)), new JNumber(0));
	    test(SA(SM(SN(42),SN(0)),SA(SM(SN(42),SN(0)),SN(0))), new JNumber(0));
	    test(SM(SN(8), SS(SN(7), SA(SN(6), SN(3)))), new JNumber(-16));
	    test(SA(SN(7), SM(SN(9), SN(9))), new JNumber(88));
		
		System.out.println( testsPassed + " tests passed");
	}


	static Sexpr SN(int n) {
		return new SE_Num(n); }
	static Sexpr SA(Sexpr lhs, Sexpr rhs) {
		return new SE_Cons(new SE_Str("+"), new SE_Cons(lhs, new SE_Cons(rhs, new SE_MT()))); }
	static Sexpr SS(Sexpr lhs, Sexpr rhs) {
		return new SE_Cons(new SE_Str("-"), new SE_Cons(lhs, new SE_Cons(rhs, new SE_MT()))); }
	static Sexpr SM(Sexpr lhs, Sexpr rhs) {
		return new SE_Cons(new SE_Str("*"), new SE_Cons(lhs, new SE_Cons(rhs, new SE_MT()))); }
	
	
	static J0e desugar(Sexpr se)
	{
		//'n = n
		if ( se instanceof SE_Num)
			return new JNumber(((SE_Num) se).n);

		//'+ = 0
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("+")
				&& ((SE_Cons)se).rhs instanceof SE_MT )
			return new JNumber(0);

		//'* = 1
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("*")
				&& ((SE_Cons)se).rhs instanceof SE_MT )
			return new JNumber(1);

		//('+ lhs rhs ...) = (+ desugar(lhs) desugar(('+ rhs ...))
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("+")
				&& ((SE_Cons)se).rhs instanceof SE_Cons )
			return new JPlus( desugar(((SE_Cons)((SE_Cons)se).rhs).lhs),
					desugar(new SE_Cons(((SE_Cons)se).lhs, 
							((SE_Cons)((SE_Cons)se).rhs).rhs)));

		//('* lhs rhs) = (+ desugar(lhs) desugar(rhs))
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("*")
				&& ((SE_Cons)se).rhs instanceof SE_Cons )
			return new JMult( desugar(((SE_Cons)((SE_Cons)se).rhs).lhs),
					desugar(new SE_Cons(((SE_Cons)se).lhs, 
							((SE_Cons)((SE_Cons)se).rhs).rhs)));

		//Negation
		if ( se instanceof SE_Cons 
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("-")
				&& ((SE_Cons)se).rhs instanceof SE_Cons
				&& ((SE_Cons)((SE_Cons)se).rhs).rhs instanceof SE_MT)
			return new JMult( new JNumber(-1), 
					desugar(((SE_Cons)((SE_Cons)se).rhs).lhs) );

		//Subtraction
		if ( se instanceof SE_Cons
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.contentEquals("-")
				&& ((SE_Cons)se).rhs instanceof SE_Cons )
			return new JPlus( desugar(((SE_Cons)((SE_Cons)se).rhs).lhs),
					desugar(new SE_Cons(((SE_Cons)se).lhs, 
							((SE_Cons)((SE_Cons)se).rhs).rhs)));

		//Error
		return new JNumber(42069);
	}

	static int testsPassed = 0;
	
	static void test(Sexpr se, J0e expected)
	{
		J0e e = desugar(se);

		System.out.println(se.pp() + " desugars to " + e.pp());

		int actual = e.interp();
		int expVal = expected.interp();

		System.out.println("Actual: " + actual + "  Expected: " + expVal);

		if (actual != expVal)
			System.out.println("TEST FAILED");
		else
			testsPassed++;
	}
}


