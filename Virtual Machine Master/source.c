#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "header.h"

expr* make_if(expr* c, expr* t, expr* f)
{
	Jif* p = malloc(sizeof(Jif));
	p->h.tag = IF;
	p->c = c;
	p->t = t;
	p->f = f;

	return (expr*) p;
}

expr* make_num(int num)
{
	Jnum* p = malloc(sizeof(Jnum));
	p->h.tag = NUM;
	p->n = num;

	return (expr*) p;
}

expr* make_app(expr* fun, expr* arg1, expr* arg2)
{
	Japp* p = malloc(sizeof(Japp));
	p->h.tag = APP;
	p->fun = fun;
	p->arg1 = arg1;
	p->arg2 = arg2;

	return (expr*) p;
}

expr* make_bool(Bool b)
{
	Jbool* p = malloc(sizeof(Jbool));
	p->h.tag = BOOL;
	p->val = b;

	return (expr*) p;
}
expr* make_prim(char* c)
{
	Jprim* p = malloc(sizeof(Jprim));
	p->h.tag = PRIM;
	p->prim = c;

	return (expr*) p;
}

expr* make_kret()
{
	Kret* p = malloc(sizeof(Kret));
	p->h.tag = KRET;

	return (expr*) p;
}

expr* make_kif(expr* t, expr* f, expr* k)
{
	Kif* p = malloc(sizeof(Kif));
	p->h.tag = KIF;
	p->t = t;
	p->f = f;
	p->k = k;

	return (expr*) p;
}

expr* make_kapp(expr* fun, expr* checked, expr* unchecked, expr* k)
{
	Kapp* p = malloc(sizeof(Kapp));
	p->h.tag = KAPP;
	p->fun = fun;
	p->checked = checked;
	p->unchecked = unchecked;
	p->k = k;

	return (expr*) p;
}

expr* make_checked(expr* next, expr* data)
{
	Checked* p = malloc(sizeof(Checked));
	p->h.tag = CHECKED;
	p->next = next;
	p->data = data;

	return (expr*) p;
}

expr* make_unchecked(expr* next, expr* data)
{
	Unchecked* p = malloc(sizeof(Unchecked));
	p->h.tag = UNCHECKED;
	p->next = next;
	p->data = data;

	return (expr*) p;
}

expr* make_fun(char* Name)
{
	Jfun* p = malloc(sizeof(Jfun));
	p->h.tag = FUN;
	p->Name = Name;

	return (expr*)p;
}

expr* make_var(char* name)
{
	Jvar* p = malloc(sizeof(Jvar));
	p->h.tag = VAR;
	p->name = name;

	return (expr*)p;
}

expr* make_def(expr* fun, expr* params, expr* exp)
{
	Jdef* p = malloc(sizeof(Jdef));
	p->h.tag = DEF;
	p->fun = fun;
	p->params = params;
	p->exp = exp;

	return (expr*)p;
}

Bool boolVal(expr* e)
{
	switch (e->tag) {
	case NUM:
		return ((Jnum*)e)->n;
	case PRIM:
		return FALSE;
	case BOOL:
		return ((Jbool*)e)->val;
	default:
		return FALSE;
	}
}

expr* delta(expr* fun, expr* checked)
{
	Jprim* prim = (Jprim*)fun;
	Checked* arg1 = (Checked*)checked;
	Checked* arg2 = (Checked*)(((Checked*)checked)->next);

	Jnum* num1 = (Jnum*) (arg1->data);
	Jnum* num2 = (Jnum*) (arg2->data);

	char* p = prim->prim;
	int lhs = num1->n;
	int rhs = num2->n;

	if (!strcmp(p, "+")) { return make_num(lhs + rhs); }
	if (!strcmp(p, "*")) { return make_num(lhs * rhs); }
	if (!strcmp(p, "/")) { return make_num(lhs / rhs); }
	if (!strcmp(p, "-")) { return make_num(lhs - rhs); }
	if (!strcmp(p, "<")) { return make_bool(lhs < rhs); }
	if (!strcmp(p, "<=")) { return make_bool(lhs <= rhs); }
	if (!strcmp(p, "==")) { return make_bool(lhs == rhs); }
	if (!strcmp(p, ">")) { return make_bool(lhs > rhs); }
	if (!strcmp(p, ">=")) { return make_bool(lhs >= rhs); }
	if (!strcmp(p, "!=")) { return make_bool(lhs != rhs); }

	return make_num(6969);
}	

void eval(expr** e)
{
	expr* ok = make_kret();

	while (1)
	{
		switch ((*e)->tag)
		{
		case IF: {
			printf("IF\n");
			Jif* c = (Jif*)*e;
			*e = c->c;
			ok = make_kif(c->t, c->f, ok);
			break; }
		case APP: {
			printf("APP\n");
			Japp* c = (Japp*)*e;
			expr* list = make_unchecked(make_unchecked(NULL, c->arg2), c->arg1);

			*e = c->fun;
			ok = make_kapp(NULL, NULL, list, ok);
			break; }
		case BOOL:
		case NUM:
		case FUN:
		case PRIM:
		{
			printf("VALUE\n");
			switch (ok->tag)
			{
			case KRET: {
				printf("KRET\n");
				return; }
			case KIF: {
				printf("KIF\n");
				Kif* k = (Kif*)ok;
				*e = (boolVal(*e)) ? k->t : k->f;
				ok = k->k;
				break; }
			case KAPP: {
				printf("KAPP\n");
				Kapp* tempK = (Kapp*)ok;
				expr* funP = tempK->fun;
				expr* checkedP = tempK->checked;

				if (!funP)
				{
					funP = *e;
					tempK->fun = funP;
				}
				else
				{
					checkedP = make_checked(checkedP, *e);
					tempK->checked = checkedP;
				}

				if (tempK->unchecked == NULL)
				{
					*e = delta(tempK->fun, tempK->checked);
					ok = tempK->k;
					break;
				}
				else
				{
					Unchecked* uc = (Unchecked*)tempK->unchecked;
					*e = uc->data;
					uc = (Unchecked*)(uc->next);
					tempK->unchecked = uc;
					ok = tempK;
					break;
				}
				break;
			}
			}
		}
		}
	}
}

int main(int argc, char* argv)
{
	expr* e = make_app(make_prim("+"), make_app(make_prim("*"), make_num(2), make_num(2)), make_num(5));

	eval(&e);

	printf("%d", ((Jnum*)e)->n);

	return 0;
}