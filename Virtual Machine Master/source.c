#include <stdio.h>
#include <string.h>
#include "header.h"

typedef enum { IF, NUM, APP, BOOL, PRIM, KRET, KIF, KAPP, CHECKED, UNCHECKED }tag;

void eval(expr* e
{
	expr * ok = make_kret();

	while (1)
	{
		switch (e->tag)
		{
		case IF: {
			Jif* c = (Jif*)e;
			e = c->c;
			ok = make_kif(c->t, c->f, ok);
			break; }
		case APP: {
			Japp* c = (Japp*)e;
			expr* list = make_unchecked(c->arg1, make_unchecked(c->arg2, NULL));

			e = c->fun;
			ok = make_kapp(NULL, NULL, list, ok);
			break; }
		case BOOL: 
		case NUM:
		case PRIM:
		{
			switch(ok->tag)
			{
			case KRET: {
				return; }
			case KIF: {
				Kif* k = (Kif*)ok;
				e = (boolVal(e)) ? k->t : k->f;
				ok = k->k;
				break; }
			case KAPP: {
				Kapp* tempK = (Kapp*)ok;
				expr* funP = tempK->fun;
				expr* checkedP = tempK->checked;

				if (!funP)
					funP = e;
				else
					checkedP = make_checked(e, checkedP);

				if (tempK->unchecked == NULL)
				{
					e = delta(funP, checkedP);
					ok = tempK->k;
					break;
				}	
				else
				{
					Unchecked* uc = tempK->unchecked;
					e = uc->data;
					uc = uc->next;
					break;
				}
				break; 
			}
		}
	}
}

expr* make_if(expr* c, expr* t, expr* f)
{
	Jif* p = malloc(sizeof(Jif));
	p->h.tag = IF;
	p->c = c;
	p->t = t;
	p->f = f;

	return p;
}

expr* make_num(int num)
{
	Jnum* p = malloc(sizeof(Jnum));
	p->h.tag = NUM;
	p->n = num;

	return p;
}

expr* make_app(expr* fun, expr* arg1, expr* arg2)
{
	Japp* p = malloc(sizeof(Japp));
	p->h.tag = APP;
	p->fun = fun;
	p->arg1 = arg1;
	p->arg2 = arg2;

	return p;
}

expr* make_bool(Bool b)
{
	Jbool* p = malloc(sizeof(Jbool));
	p->h.tag = BOOL;
	p->val = b;

	return p;
}
expr* make_prim(char* c)
{
	Jprim* p = malloc(sizeof(Jprim));
	p->h.tag = PRIM;
	p->prim = c;

	return p;
}

expr* make_kret()
{
	Kret* p = malloc(sizeof(Kret));
	p->h.tag = KRET;

	return p;
}

expr* make_kif(expr* t, expr* f, expr* k)
{
	Kif* p = malloc(sizeof(Kif));
	p->h.tag = KIF;
	p->t = t;
	p->f = f;
	p->k = k;

	return p;
}

expr* make_kapp(expr* fun, expr* checked, expr* unchecked, expr* k)
{
	Kapp* p = malloc(sizeof(Kapp));
	p->h.tag = KAPP;
	p->fun = fun;
	p->checked = checked;
	p->unchecked = unchecked;
	p->k = k;

	return p;
}

expr* make_checked(expr* next, expr* data)
{
	Checked* p = malloc(sizeof(Checked));
	p->h.tag = CHECKED;
	p->next = next;
	p->data = data;

	return p;
}

expr* make_unchecked(expr* next, expr* data)
{
	Unchecked* p = malloc(sizeof(Unchecked));
	p->h.tag = UNCHECKED;
	p->next = next;
	p->data = data;

	return p;
}

Bool boolVal(expr* e)
{
	switch (e->tag) {
	case NUM:
		return (Jnum)e->n;
	case PRIM:
		return FALSE;
	case BOOL:
		return (Jbool)e->val;
	}
}

expr* delta(expr* fun, expr* checked)
{
	Jprim* prim = (Jprim*)fun;
	Checked* arg1 = (Checked*)checked;
	Checked* arg2 = (Checked*)(checked->next);

	Jnum* num1 = arg1->data;
	Jnum* num2 = arg2->data;

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