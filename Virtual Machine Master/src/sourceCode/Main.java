package sourceCode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.io.FileWriter;

public class Main {
	
	public static HashMap<String, Define> sigMap = new HashMap<String, Define>();
	
	public static void main(String[] args) throws IOException {
		
		JExpr e = JA("+", JA("*", JN(2), JN(4)), JN(8));
		
		emit(e);
		
		System.out.println("CC0: " + CC0.interp(e).pp());
		System.out.println("Big: " + e.interp().pp());
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
			line = line.concat(printJIf(((JIf)e).cond));
		}
		if (((JIf)e).cond instanceof JNum)
		{
			line = line.concat(printJIf(((JIf)e).cond));
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
			line = line.concat(printJIf(((JIf)e).tbr));
		}
		if (((JIf)e).tbr instanceof JNum)
		{
			line = line.concat(printJIf(((JIf)e).tbr));
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
	
}


