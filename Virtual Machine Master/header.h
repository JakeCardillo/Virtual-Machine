//CK0 Machine Header

#include <string.h>

typedef enum {TRUE, FALSE}Bool;

typedef enum { IF, NUM, APP, BOOL, PRIM, KRET, KIF, KAPP, CHECKED, UNCHECKED }Tag;

typedef struct {
	Tag tag;
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
	expr* t, *f, *k;
}Kif;

typedef struct {
	expr h;
	expr* fun, *checked, *unchecked, *k;
}Kapp;

typedef struct {
	expr h;
	expr* next, *data;
}Checked;

typedef struct {
	expr h;
	expr* next, *data;
}Unchecked;