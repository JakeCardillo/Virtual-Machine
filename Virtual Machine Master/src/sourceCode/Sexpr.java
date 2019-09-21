package sourceCode;

interface Sexpr {
	public String pp();
}

class SE_Str implements Sexpr {
	public String s;
	public String pp() { return s; }
	public SE_Str(String s) { this.s = s; } }

class SE_Num implements Sexpr {
	public int n;
	public String pp() { return "" + n; }
	public SE_Num(int n) { this.n = n; } }

class SE_MT implements Sexpr {
	public String pp() { return "⊥"; }
	public SE_MT() { } }

class SE_Cons implements Sexpr {
	public Sexpr lhs, rhs;
	public String pp() { return "(" + this.lhs.pp() + " " + this.rhs.pp() + ")"; }
	public SE_Cons(Sexpr lhs, Sexpr rhs) {
		this.lhs = lhs;
		this.rhs = rhs; } }