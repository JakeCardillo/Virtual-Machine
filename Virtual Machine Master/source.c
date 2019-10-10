#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "header.h"

Map* map = NULL;

//make if object
expr* make_if(expr* c, expr* t, expr* f)
{
	printf("make_if\n");
	Jif* p = malloc(sizeof(Jif));
	p->h.tag = IF;
	p->c = c;
	p->t = t;
	p->f = f;

	return (expr*) p;
}

//make num object
expr* make_num(int num)
{
	printf("make_num\n");
	Jnum* p = malloc(sizeof(Jnum));
	p->h.tag = NUM;
	p->n = num;

	return (expr*) p;
}

//make app object
expr* make_app(expr* fun, expr* arg1, expr* arg2)
{
	printf("make_app\n");
	Japp* p = malloc(sizeof(Japp));
	p->h.tag = APP;
	p->fun = fun;
	p->arg1 = arg1;
	p->arg2 = arg2;

	return (expr*) p;
}

//make bool object
expr* make_bool(Bool b)
{
	printf("make_bool\n");
	Jbool* p = malloc(sizeof(Jbool));
	p->h.tag = BOOL;
	p->val = b;

	return (expr*) p;
}

//make prim object
expr* make_prim(char* c)
{
	printf("make_prim\n");
	Jprim* p = malloc(sizeof(Jprim));
	p->h.tag = PRIM;
	p->prim = c;

	return (expr*) p;
}

//make kret object
expr* make_kret()
{
	printf("make_kret\n");
	Kret* p = malloc(sizeof(Kret));
	p->h.tag = KRET;

	return (expr*) p;
}

//make kif object
expr* make_kif(expr* t, expr* f, expr* k, expr* env)
{
	printf("make_kif\n");
	Kif* p = malloc(sizeof(Kif));
	p->h.tag = KIF;
	p->t = t;
	p->f = f;
	p->k = k;
	p->env = env;

	return (expr*) p;
}

//make kapp object
expr* make_kapp(expr* fun, expr* checked, expr* unchecked, expr* k, expr* env)
{
	printf("make_kapp\n");
	Kapp* p = malloc(sizeof(Kapp));
	p->h.tag = KAPP;
	p->fun = fun;
	p->checked = checked;
	p->unchecked = unchecked;
	p->k = k;
	p->env = env;

	return (expr*) p;
}

//make checked object
expr* make_checked(expr* data, expr* next)
{
	printf("make_checked\n");
	Checked* p = malloc(sizeof(Checked));
	p->h.tag = CHECKED;
	p->next = next;
	p->data = data;

	return (expr*) p;
}

//make unchecked object
expr* make_unchecked(expr* data, expr* next)
{
	printf("make_unchecked\n");
	Unchecked* p = malloc(sizeof(Unchecked));
	p->h.tag = UNCHECKED;
	p->next = next;
	p->data = data;

	return (expr*) p;
}

//make fun object
expr* make_fun(char* Name, expr* params)
{
	printf("make_fun\n");
	Jfun* p = malloc(sizeof(Jfun));
	p->h.tag = FUN;
	p->Name = Name;
	p->params = params;

	return (expr*)p;
}

//make var object
expr* make_var(char* name)
{
	printf("make_var\n");
	Jvar* p = malloc(sizeof(Jvar));
	p->h.tag = VAR;
	p->name = name;

	return (expr*)p;
}

//push def into map of defs
int pushMap(expr* def)
{
	Map* temp = malloc(sizeof(Map));
	temp->def = def;
	temp->next = map;
	map = temp;

	return 1;
}

//check if fun is in map, return index
expr* inMap(expr* fun)
{
	Jfun* f = (Jfun*)fun;
	Map* temp = map;

	if (map == NULL)
	{
		map = malloc(sizeof(Jdef*));
	}
	else {
		while (temp != NULL)
		{
			if (strcmp(((Jfun*)temp->def->fun)->Name, f->Name) == 0)
			{
				return temp->def;
			}
			else
				temp = temp->next;
		}
	}
	return NULL;
}

//make def object
expr* make_def(expr* fun, expr* exp)
{
	printf("make_def\n");
	if (inMap(fun))
		return NULL;
	Jdef* p = malloc(sizeof(Jdef));
	p->h.tag = DEF;
	p->fun = fun;
	p->exp = exp;

	pushMap(p);

	return (expr*)p;
}

//make env object
expr* make_env(expr* var, expr* val, expr** next)
{
	printf("make_env\n");
	Jenv* p = malloc(sizeof(Jenv));
	p->h.tag = ENV;
	p->var = var;
	p->val = val;
	p->next = next;

	return (expr*)p;
}

//is e a val?
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

//delta function
expr* delta(expr* fun, expr* checked)
{
	Jprim* prim = (Jprim*)fun;
	Checked* arg1 = (Checked*)checked;
	Checked* arg2 = (Checked*)(((Checked*)checked)->next);

	Jnum* num1 = (Jnum*)(arg1->data);
	Jnum* num2 = (Jnum*)(arg2->data);

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

//evaluation function
void eval(expr** e)
{
	Jenv* env = NULL;
	expr* ok = make_kret();

	while (1)
	{
		printf("e tag: %d\n", (*e)->tag);
		switch ((*e)->tag)
		{
		case IF: {
			printf("IF\n");
			Jif* c = (Jif*)* e;
			*e = c->c;
			ok = make_kif(c->t, c->f, ok, env);
			break; }
		case APP: {
			printf("APP\n");
			Japp* c = (Japp*)* e;
			expr* list = make_unchecked(c->arg1, make_unchecked(c->arg2, NULL));

			*e = c->fun;
			ok = make_kapp(NULL, NULL, list, ok, env);
			break; }
		case FUN: {
			printf("FUN\n");
			Jfun* temp = (Jfun*)* e;
			expr* def = inMap(temp);

			if (def != NULL)
			{
				expr* exp = ((Jdef*)def)->exp;
				expr* pNode = ((Jfun*)((Jdef*)def)->fun)->params;
				expr* cNode = temp->params;
				expr* envir = NULL;

				printf("pNode: %d next: %d\n", pNode->tag, ((Checked*)pNode)->next->tag);

				while (pNode != NULL && cNode != NULL)
				{
					printf("ad");
					printf("%d %d", ((Checked*)pNode)->data->tag, ((Checked*)cNode)->data->tag);
					envir = make_env(((Checked*)pNode)->data, ((Checked*)cNode)->data, envir);
					pNode = ((Checked*)pNode)->next;
					cNode = ((Checked*)cNode)->next;
					printf("as");
				}
				*e = exp;
				env = envir;
				printf("asd");
			}
			break;
		}
		case VAR: {
			printf("VAR\n");
			Jvar* temp = (Jvar*)* e;
			expr* nav = env;

			printf("%s\n", temp->name);

			while (nav != NULL)
			{
				printf("1");
				printf("nav: %s  temp: %s\n", ((Jvar*)((Jenv*)nav)->var)->name, temp->name);
				if (strcmp(((Jvar*)((Jenv*)nav)->var)->name, temp->name) == 0)
				{
					printf("name match\n");
					*e = ((Jenv*)nav)->val;
					nav = NULL;
				}
				else
					nav = ((Jenv*)nav)->next;
			}
			break;
		}
		case BOOL:
		case NUM:
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
				env = k->env;
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
					checkedP = make_checked(*e, checkedP);
					tempK->checked = checkedP;
				}

				if (tempK->unchecked == NULL)
				{
					*e = delta(tempK->fun, tempK->checked);
					ok = tempK->k;
					env = tempK->k;
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

//substitute x for v
expr* subst(expr* e, expr* x, expr* v)
{
	switch (e->tag)
	{
	//expressions
	case IF: {
		Jif* temp = (Jif*)e;
		return make_if(subst(temp->c, x, v), subst(temp->t, x, v), subst(temp->f, x, v));
			break;
	}
	case APP: {
		Japp* temp = (Japp*)e;
		return make_app(subst(temp->fun, x, v), subst(temp->arg1, x, v), subst(temp->arg2, x, v));
		break;
	}
	case VAR: {
		if (e == x)
			return v;
		else
			return e;
		break;
	}
	//values
	case NUM: 
	case BOOL:
	case PRIM:
	case FUN:
		return e;
	}
}

int main(int argc, char* argv)
{
	make_def(make_fun("Test", make_checked(make_var("var1"), make_checked(make_var("var2"), NULL))),
		make_app(make_prim("*"), make_var("var1"), make_var("var2")));

	printf("made\n");
	expr* f = make_fun("Test", make_checked(make_num(3), make_checked(make_num(4), NULL)));
	eval(&f);

	printf("%d", ((Jnum*)f)->n);
	return 0;
}