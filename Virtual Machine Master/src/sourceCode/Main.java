package sourceCode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.io.FileWriter;

public class Main {
	
	public static int tests_passed = 0;
	public static HashMap<String, Define> sigMap = new HashMap<String, Define>();
	
	public static void main(String[] args) throws IOException {
		
		JExpr e = JA("+", JA("*", JN(2), JN(4)), JN(8));
		//JExpr e = JI(JB(false), JN(8), JA("*", JN(8), JN(7)));
		
		emit(e);
		
		System.out.println("CC0: " + CC0.interp(e).pp());
		System.out.println("Big: " + e.interp().pp());
		
		test_j3();
		
		System.out.println("Tests passed: " + tests_passed);
	}

	public static void emit(JExpr e) throws IOException
	{
		System.out.println(e.pp());
		
		FileWriter filewriter = new FileWriter("LowLevel.c");
		PrintWriter printwriter = new PrintWriter(filewriter);
		
		printwriter.printf("#include " + '"' + "source.c" + '"' + ";\n");
		printwriter.printf("#include <stdio.h>\n");
		printwriter.printf("int main(int argc, char* argv[]) {\n");
		
		if (e instanceof JIf)
		{
			System.out.println("if");
			printwriter.printf(printJIf(e));
		}
		if (e instanceof JNum)
		{
			System.out.println("num");
			printwriter.printf(printJNum(e));
		}
		if (e instanceof JBool) 
		{
			System.out.println("bool");
			printwriter.printf(printJBool(e));
		}
		if (e instanceof JApp)
		{
			System.out.println("app");
			printwriter.printf(printJApp(e));
		}
		if (e instanceof lambda)
		{
			System.out.println("lambda");
			printwriter.printf(printLambda(e));
		}
		
		printwriter.printf(";\nreturn 0; \n }\n");
		
		printwriter.close();
	}
	
	public static String printJIf(JExpr e)
	{
		String line = "";
		
		line = line.concat("make_if(");
		
		if (((JIf)e).cond instanceof JIf)
		{
			line = line.concat(printJIf(((JIf)e).cond));
		}
		if (((JIf)e).cond instanceof JApp)
		{
			line = line.concat(printJApp(((JIf)e).cond));
		}
		if (((JIf)e).cond instanceof JBool)
		{
			line = line.concat(printJBool(((JIf)e).cond));
		}
		if (((JIf)e).cond instanceof JNum)
		{
			line = line.concat(printJNum(((JIf)e).cond));
		}
		
		line = line.concat(", ");
		
		if (((JIf)e).tbr instanceof JIf)
		{
			line = line.concat(printJIf(((JIf)e).tbr));
		}
		if (((JIf)e).tbr instanceof JApp)
		{
			line = line.concat(printJApp(((JIf)e).tbr));
		}
		if (((JIf)e).tbr instanceof JBool)
		{
			line = line.concat(printJBool(((JIf)e).tbr));
		}
		if (((JIf)e).tbr instanceof JNum)
		{
			line = line.concat(printJNum(((JIf)e).tbr));
		}
		
		line = line.concat(", ");
		
		if (((JIf)e).fbr instanceof JIf)
		{
			line = line.concat(printJIf(((JIf)e).fbr));
		}
		if (((JIf)e).fbr instanceof JApp)
		{
			line = line.concat(printJApp(((JIf)e).fbr));
		}
		if (((JIf)e).fbr instanceof JBool)
		{
			line = line.concat(printJBool(((JIf)e).fbr));
		}
		if (((JIf)e).fbr instanceof JNum)
		{
			line = line.concat(printJNum(((JIf)e).fbr));
		}
		
		line = line.concat(")");
		return line;
	}
	
	public static String printJApp(JExpr e)
	{
		String line = "";
		
		line = line.concat("make_app(");
		
		
		line = line.concat("make_prim(" + (((JPrim)((JApp)e).fun).p + ")"));
		
		line = line.concat(", ");
		
		if ((((JCons)((JApp)e).args).lhs) instanceof JIf)
		{
			line = line.concat(printJIf((((JCons)((JApp)e).args).lhs)));
		}if ((((JCons)((JApp)e).args).lhs) instanceof JApp)
		{
			line = line.concat(printJApp((((JCons)((JApp)e).args).lhs)));
		}
		if ((((JCons)((JApp)e).args).lhs) instanceof JBool)
		{
			line = line.concat(printJBool((((JCons)((JApp)e).args).lhs)));
		}
		if ((((JCons)((JApp)e).args).lhs) instanceof JNum)
		{
			line = line.concat(printJNum((((JCons)((JApp)e).args).lhs)));
		}
		
		line = line.concat(", ");
		
		if (((JCons)((JCons)((JApp)e).args).rhs).lhs instanceof JIf)
		{
			line = line.concat(printJIf(((JCons)((JCons)((JApp)e).args).rhs).lhs));
		}if (((JCons)((JCons)((JApp)e).args).rhs).lhs instanceof JApp)
		{
			line = line.concat(printJApp(((JCons)((JCons)((JApp)e).args).rhs).lhs));
		}
		if (((JCons)((JCons)((JApp)e).args).rhs).lhs instanceof JBool)
		{
			line = line.concat(printJBool(((JCons)((JCons)((JApp)e).args).rhs).lhs));
		}
		if (((JCons)((JCons)((JApp)e).args).rhs).lhs instanceof JNum)
		{
			line = line.concat(printJNum(((JCons)((JCons)((JApp)e).args).rhs).lhs));
		}
		
		line = line.concat(")");
		return line;
	}
	
	public static String printLambda(JExpr e)
	{
		String line = "";
		
		line = line.concat("make_lambda(");
		line = line.concat(printJCons(((lambda)e).vars));
		line = line.concat(")");
		
		return line;
	}
	
	public static String printJCons(JExpr e)
	{
		String line = "";
		
		if(e instanceof JCons) {
			if(((JCons)e).lhs instanceof JIf) {
				line = line.concat(printJIf(((JCons)e).lhs));
			}
			if(((JCons)e).lhs instanceof JApp) {
				line = line.concat(printJApp(((JCons)e).lhs));
			}
			if(((JCons)e).lhs instanceof JBool) {
				line = line.concat(printJBool(((JCons)e).lhs));
			}
			if(((JCons)e).lhs instanceof JNum) {
				line = line.concat(printJNum(((JCons)e).lhs));
			}
			if(((JCons)e).lhs instanceof JVar) {
				line = line.concat("make_var(" + ((JVar)((JCons)e).lhs).name + ")");
			}
		}
		
		line = line.concat(printJCons(((JCons)e).rhs));
		
		return line;
	}
	
	public static String printJBool(JExpr e)
	{
		return "make_bool(" + ((JBool)e).b + ")";
	}
	
	public static String printJNum(JExpr e)
	{
		return "make_num(" + ((JNum)e).n + ")";
	}
	
	static JExpr JN(int n) {
		return new JNum(n); }
	static JExpr JB(Boolean b) {
		return new JBool(b); }
	static JExpr JA(String prim, JExpr lhs, JExpr rhs) {
		return new JApp(new JPrim(prim), new JCons(lhs, new JCons(rhs, new JNull()))); }
	static JExpr JI(JExpr cond, JExpr tbr, JExpr fbr) {
		return new JIf(cond, tbr, fbr); }


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

		//('* lhs rhs) = (* desugar(lhs) desugar(rhs))
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
		
		//lambda j3
		if(se instanceof SE_Cons 
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("let")
				&& ((SE_Cons)se).rhs instanceof SE_Cons)
			return new lambda(((SE_Str)((SE_Cons)((SE_Cons)se).rhs).lhs).s, 
					desugar(((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).lhs), 
					desugar(((SE_Cons)((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).rhs).lhs));
		
		//lambda j4
		if(se instanceof SE_Cons 
				&& ((SE_Cons)se).lhs instanceof SE_Str
				&& ((SE_Str)((SE_Cons)se).lhs).s.equals("let")
				&& ((SE_Cons)se).rhs instanceof SE_Cons)
			return new lambda("func", 
					desugar(((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).lhs), 
					desugar(((SE_Cons)((SE_Cons)((SE_Cons)((SE_Cons)se).rhs).rhs).rhs).lhs));

		//Error
		return new JNum(42069);
	}
	
	public static void test_j3()
	{
		JExpr e1 = JA("+", JN(7), new JVar("x"));
		JExpr e2 = JA("*", JN(7), new JVar("x"));
		JExpr e3 = JA("+", new JVar("x"), JN(7));
		JExpr e4 = JA("*", new JVar("x"), JN(7));
		
		JExpr function = new lambda("func", new JCons(new JVar("x"), new JNull()), e1);
		JExpr result = function.subst(new JVar("x"), JN(5));
		if (result.interp().pp().equals("12"))
			tests_passed++;
		
		function = new lambda("func", new JCons(new JVar("x"), new JNull()), e2);
		result = function.subst(new JVar("x"), JN(5));
		if (result.interp().pp().equals("35"))
			tests_passed++;
		
		function = new lambda("func", new JCons(new JVar("x"), new JNull()), e3);
		result = function.subst(new JVar("x"), JN(5));
		if (result.interp().pp().equals("12"))
			tests_passed++;
		
		function = new lambda("func", new JCons(new JVar("x"), new JNull()), e4);
		result = function.subst(new JVar("x"), JN(5));
		if (result.interp().pp().equals("35"))
			tests_passed++;
		
		Sexpr se = Slambda(new SE_Str("new func"), new SE_MT(), SA(SN(3), SN(5)));
		JExpr test = desugar(se);
		if (test.subst(null, null).interp().pp().equals("8"))
			tests_passed++;
	}
	
	public static Sexpr SN(int n)
	{
		return new SE_Num(n);
	}
	public static Sexpr SA(Sexpr l, Sexpr r)
	{
		return new SE_Cons(new SE_Str("+"), new SE_Cons(l, new SE_Cons(r, new SE_MT())));
	}
	public static Sexpr SM(Sexpr l, Sexpr r)
	{
		return new SE_Cons(new SE_Str("*"), new SE_Cons(l, new SE_Cons(r, new SE_MT())));
	}
	public static Sexpr Slambda(Sexpr name, Sexpr e1, Sexpr e2)
	{
		return new SE_Cons(new SE_Str("let"), new SE_Cons(name, new SE_Cons(e1, new SE_Cons(e2, new SE_MT()))));
	}
	
	public static void lambda_factorial()
	{
		JExpr mkfac = new lambda("fac", new JCons(new JVar("n"), new JNull()), //function
				new JIf(new lambda("zero?", new JCons(new JVar("n"), new JNull()), new JIf(new JVar("n"), new JBool(true), new JBool(false))), //zero?
						new lambda("one", new JCons(new JVar("f"), new JCons(new JVar("x"), new JNull())), new JCons(new JVar("x"), new JNull())), //true
						new lambda("mult", new JCons(new JVar("n"), new JNull()), new JNull()))); //false
	}
	
}



