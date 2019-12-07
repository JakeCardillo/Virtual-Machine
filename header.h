//CK0 Machine Header

#include <string.h>

typedef enum {TRUE, FALSE}Bool;

enum Tag { IF, NUM, APP, BOOL, PRIM, KRET, KIF, KAPP, CHECKED, UNCHECKED, 
	LAMBDA, VAR, DEF, ENV , CLOS, CASE, UNIT, PAIR, INL, INR, KCASE};

typedef struct {
	enum Tag tag;
}expr;

typedef struct {
	expr h;
	expr* c, *t, *f;
}Jif;

typedef struct {
	expr h;
	int n;
}Jnum;

typedef struct {
	expr h;
	expr* fun, *arg1, *arg2;
}Japp;

typedef struct {
	expr h;
	Bool val;
}Jbool;

typedef struct {
	expr h;
	char* prim;
}Jprim;

typedef struct {
	expr h;
}Kret;

typedef struct {
	expr h;
	expr* t, *f, *k, *env;
}Kif;

typedef struct {
	expr h;
	expr* fun, *checked, *unchecked, *k, *env;
}Kapp;

typedef struct {
	expr h;
	expr* next, *data;
}Checked;

typedef struct {
	expr h;
	expr* next, *data;
}Unchecked;

typedef struct {
	expr h;
	char* Name;
	expr* params;
}lambda;

typedef struct {
	expr h;
	char* name;
}Jvar;

typedef struct {
	expr h;
	expr* fun, *exp;
}Jdef;

typedef struct {
	expr h;
	Jvar* var;
	expr* val;
	expr** next;
}Jenv;

typedef struct {
	expr h;
	expr* lam;
	expr* env;
}Closure;

typedef struct M{
	Jdef* def;
	struct M* next;
}Map;

typedef struct {
	expr h;
}Unit;

typedef struct {
	expr h;
	expr* left;
	expr* right;
}Pair;

typedef struct {
	expr h;
	expr* val;
}InL;

typedef struct {
	expr h;
	expr* val;
}InR;

typedef struct {
	expr h;
	expr* e;
	expr* inl;
	expr* lexp;
	expr* inr;
	expr* rexp;
}Case;

typedef struct {
	expr h;
	expr* env;
	expr* inl;
	expr* lexp;
	expr* inr;
	expr* rexp;
	expr* k;
}KCase;