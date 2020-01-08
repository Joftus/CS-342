package funclang;
import static funclang.AST.*;
import static funclang.Value.*;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import funclang.Env.*;

public class Evaluator implements Visitor<Value> {

	private Printer.Formatter ts = new Printer.Formatter();

	private Env initEnv = initialEnv(); //New for definelang

	Value valueOf(Program p) {
			return (Value) p.accept(this, initEnv);
	}

	@Override
	public Value visit(AddExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 0;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result += intermediate.v(); //Semantics of AddExp in terms of the target language.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(UnitExp e, Env env) {
		return new UnitVal();
	}

	@Override
	public Value visit(NumExp e, Env env) {
		return new NumVal(e.v());
	}

	@Override
	public Value visit(StrExp e, Env env) {
		return new StringVal(e.v());
	}

	@Override
	public Value visit(BoolExp e, Env env) {
		return new BoolVal(e.v());
	}

	@Override
	public Value visit(DivExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result / rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(MultExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 1;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result *= intermediate.v(); //Semantics of MultExp.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(Program p, Env env) {
		try {
			for(DefineDecl d: p.decls())
				d.accept(this, initEnv);
			return (Value) p.e().accept(this, initEnv);
		} catch (ClassCastException e) {
			return new DynamicError(e.getMessage());
		}
	}

	@Override
	public Value visit(SubExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result - rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(VarExp e, Env env) {
		// Previously, all variables had value 42. New semantics.
		return env.get(e.name());
	}

	@Override
	public Value visit(LetExp e, Env env) { // New for varlang.
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		List<Value> values = new ArrayList<>(value_exps.size());

		for(Exp exp : value_exps)
			values.add((Value)exp.accept(this, env));

		Env new_env = env;
		for (int index = 0; index < names.size(); index++)
			new_env = new ExtendEnv(new_env, names.get(index), values.get(index));

		return (Value) e.body().accept(this, new_env);
	}

	@Override
	public Value visit(DefineDecl e, Env env) { // New for definelang.
		String name = e.name();
		Exp value_exp = e.value_exp();
		Value value = (Value) value_exp.accept(this, env);
		((GlobalEnv) initEnv).extend(name, value);
		return new Value.UnitVal();
	}

	// New custom helper type
	@Override
	public Value visit(BaseExp e, Env env) {
		System.out.println("in visit(BaseExp)");
		return null;
	}

	// (define func (lambda ((v = 8)) v))
	// (define add (lambda (a (b = 3)) (+ a b)))
	@Override
	public Value visit(LambdaExp e, Env env) { // New for funclang.
		GlobalEnv genv = (GlobalEnv) env;
		for (Exp exp : e.formals()) {
			BaseExp bexp = (BaseExp) exp;
			if (bexp.getExp() != null) genv.extend(bexp.getVar(), new NumVal(((NumExp) bexp.getExp())._val));
			else genv.extend(bexp.getVar(), new UnitVal());
		}
		return new Value.FunVal(genv, e.formals(), e.body());
	}

	@Override
	public Value visit(CallExp e, Env env) { // New for funclang.
		Object result = e.operator().accept(this, env);
		if(!(result instanceof Value.FunVal))
			return new Value.DynamicError("Operator not a function in call " +  ts.visit(e, env));
		Value.FunVal operator =  (Value.FunVal) result; //Dynamic checking
		List<Exp> operands = e.operands();

		// Call-by-value semantics
		List<Value> actuals = new ArrayList<>(operands.size());
		for(Exp exp : operands) {
			actuals.add((Value) exp.accept(this, env));
		}

		List<Exp> formals = operator.formals();
		if (formals.size()!=actuals.size()) {
			for (int a = actuals.size(); a < formals.size(); a++){
				if (!(env.get(((BaseExp) formals.get(a)).getVar()) instanceof UnitVal))
					actuals.add(env.get(((BaseExp) formals.get(a)).getVar()));
			}
			if (formals.size() != actuals.size())
				return new Value.DynamicError("Argument mismatch in call " + ts.visit(e, env));
		}

		Env fun_env = operator.env();
		for (int index = 0; index < formals.size(); index++)
			fun_env = new ExtendEnv(fun_env, ((BaseExp) formals.get(index)).getVar(), actuals.get(index));

		return (Value) operator.body().accept(this, fun_env);
	}

	@Override
	public Value visit(IfExp e, Env env) { // New for funclang.
		Object result = e.conditional().accept(this, env);
		if(!(result instanceof Value.BoolVal))
			return new Value.DynamicError("Condition not a boolean in expression " +  ts.visit(e, env));
		Value.BoolVal condition =  (Value.BoolVal) result; //Dynamic checking

		if(condition.v())
			return (Value) e.then_exp().accept(this, env);
		else return (Value) e.else_exp().accept(this, env);
	}





	// Q3 Code Starts Here
	@SuppressWarnings("unchecked")
	private List toLst(Exp exp, Env env) {
		List lst = new ArrayList();
		PairVal tmp;

		if (exp instanceof CdrExp) {
			tmp = (PairVal) exp.accept(this, env);
			lst.add(new NumExp(((NumVal) tmp._fst).v()));
			while (!(tmp._snd instanceof Value.Null)) {
				tmp = (PairVal) tmp._snd;
				lst.add(new NumExp(((NumVal) tmp._fst).v()));
			}
		}
		else if (exp instanceof ConsExp) {
			tmp = (PairVal) exp.accept(this, env);
			lst.add(new NumExp(((NumVal) tmp._fst).v()));
			while (!(tmp._snd instanceof Value.Null)) {
				tmp = (PairVal) tmp._snd;
				lst.add(new NumExp(((NumVal) tmp._fst).v()));
			}
		}
		return lst;
	}

	private BoolVal lstCmp(List lst_1, List lst_2) {
		BoolVal t = new BoolVal(true);
		BoolVal f = new BoolVal(false);
		double num_1, num_2;

		if (lst_1.size() != lst_2.size()) return f;
		for (int a = 0; a < lst_1.size(); a++) {
			if (lst_1.get(a) instanceof NumExp && lst_2.get(a) instanceof NumExp) {
				num_1 = ((NumExp) lst_1.get(a))._val;
				num_2 = ((NumExp) lst_2.get(a))._val;
				if (num_1 != num_2) return f;
			}
			else if (lst_1.get(a) instanceof ListExp && lst_2.get(a) instanceof ListExp) {
				if (lstCmp(((ListExp) lst_1.get(a)).elems(), ((ListExp) lst_2.get(a)).elems()) == f) return f;
			}
		}

		return t;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Value visit(EqualExp e, Env env) { // New for funclang.
		Object obj_1, obj_2;
		BoolVal t, f;
		List lst_1, lst_2;
		double num_1, num_2;

		t = new BoolVal(true);
		f = new BoolVal(false);
		obj_1 = e.first_exp();
		obj_2 = e.second_exp();
		lst_2 = new ArrayList();

		// NumExp
		if (obj_1 instanceof NumExp) {
			num_1 = ((NumVal) ((NumExp) obj_1).accept(this, env)).v();
			if (obj_2 instanceof NumExp) num_2 = ((NumVal) ((NumExp) obj_2).accept(this, env)).v();
			else num_2 = ((NumVal) ((CarExp) obj_2).accept(this, env)).v();
			if (num_1 == num_2) return t;
			return f;
		}

		// StrExp
		else if (obj_1 instanceof StrExp && obj_2 instanceof StrExp) {
			String str_1, str_2;
			str_1 = ((StrExp) obj_1)._val;
			str_2 = ((StrExp) obj_2)._val;
			if (str_1.length() != str_2.length()) return f;
			for (int a = 0; a < str_1.length(); a++) {
				if (str_1.charAt(a) != str_2.charAt(a)) return f;
			}
			return t;
		}

		// BoolExp
		else if (obj_1 instanceof BoolExp && obj_2 instanceof BoolExp) {
			boolean bool_1, bool_2;
			bool_1 = ((BoolExp) obj_1)._val;
			bool_2 = ((BoolExp) e.second_exp())._val;
			if ((bool_1 && bool_2) || (!bool_1 && !bool_2)) return t;
			return f;
		}

		// CarExp
		else if (obj_1 instanceof CarExp) {
			num_1 = ((NumVal) ((CarExp) obj_1).accept(this, env)).v();
			if (obj_2 instanceof CarExp)
				num_2 = ((NumVal) ((CarExp) obj_2).accept(this, env)).v();
			else if (obj_2 instanceof NumExp)
				num_2 = ((NumVal) ((NumExp) obj_2).accept(this, env)).v();
			else return f;
			if (num_1 != num_2) return f;
			return t;
		}

		// ListExp, CdrExp, ConsExp
		else {
			if (obj_1 instanceof ListExp) lst_1 = ((ListExp) obj_1).elems();
			else if (obj_1 instanceof CdrExp) lst_1 = toLst((CdrExp) obj_1, env);
			else if (obj_1 instanceof ConsExp) lst_1 = toLst((ConsExp) obj_1, env);
			else return f;

			if (obj_2 instanceof CdrExp) lst_2 = toLst((CdrExp) obj_2, env);
			else if (obj_2 instanceof ConsExp) lst_2 = toLst((ConsExp) obj_2, env);
			else if (obj_2 instanceof ListExp) lst_2 = ((ListExp) obj_2).elems();
			else if (obj_2 instanceof NumExp) lst_2.add(((NumExp) obj_2).accept(this, env));
			else if (obj_2 instanceof CarExp) lst_2.add(new NumExp(((NumVal) ((CarExp) obj_2).accept(this, env)).v()));
			else return f;
			return lstCmp(lst_1, lst_2);
		}
	}

	@Override
	public Value visit(LessExp e, Env env) { // New for funclang.
		Object obj_1, obj_2;
		BoolVal t, f;
		List lst_1, lst_2;

		obj_1 = e.first_exp();
		obj_2 = e.second_exp();
		t = new BoolVal(true);
		f = new BoolVal(false);

		if (obj_1 instanceof NumExp && obj_2 instanceof NumExp) {
			if (((NumVal) ((NumExp) obj_1).accept(this, env)).v() < ((NumVal) ((NumExp) obj_2).accept(this, env)).v())
				return t;
			return f;
		}
		else if (obj_1 instanceof StrExp && obj_2 instanceof StrExp) {
			String str_1 = ((StrExp) obj_1)._val;
			String str_2 = ((StrExp) obj_2)._val;
			if (str_1.length() < str_2.length()) return t;
			return f;
		}
		else {
			if (obj_1 instanceof ListExp) lst_1 = ((ListExp) obj_1).elems();
			else if (obj_1 instanceof CdrExp) lst_1 = toLst((CdrExp) obj_1, env);
			else if (obj_1 instanceof ConsExp) lst_1 = toLst((ConsExp) obj_1, env);
			else return f;
			if (obj_2 instanceof ListExp) lst_2 = ((ListExp) e.second_exp()).elems();
			else if (obj_2 instanceof CdrExp) lst_2 = toLst((CdrExp) obj_2, env);
			else if (obj_2 instanceof ConsExp) lst_2 = toLst((ConsExp) obj_2, env);
			else return f;

			if (lst_1.size() < lst_2.size()) return t;
			return f;
		}
	}

	@Override
	public Value visit(GreaterExp e, Env env) { // New for funclang.
		Object obj_1, obj_2;
		BoolVal t, f;
		List lst_1, lst_2;

		obj_1 = e.first_exp();
		obj_2 = e.second_exp();
		t = new BoolVal(true);
		f = new BoolVal(false);

		if (obj_1 instanceof NumExp && obj_2 instanceof NumExp) {
			if (((NumVal) ((NumExp) obj_1).accept(this, env)).v() > ((NumVal) ((NumExp) obj_2).accept(this, env)).v())
				return t;
			return f;
		}
		else if (obj_1 instanceof StrExp && obj_2 instanceof StrExp) {
			String str_1 = ((StrExp) obj_1)._val;
			String str_2 = ((StrExp) obj_2)._val;
			if (str_1.length() > str_2.length()) return t;
			return f;
		}
		else {
			if (obj_1 instanceof ListExp) lst_1 = ((ListExp) obj_1).elems();
			else if (obj_1 instanceof CdrExp) lst_1 = toLst((CdrExp) obj_1, env);
			else if (obj_1 instanceof ConsExp) lst_1 = toLst((ConsExp) obj_1, env);
			else return f;
			if (obj_2 instanceof ListExp) lst_2 = ((ListExp) e.second_exp()).elems();
			else if (obj_2 instanceof CdrExp) lst_2 = toLst((CdrExp) obj_2, env);
			else if (obj_2 instanceof ConsExp) lst_2 = toLst((ConsExp) obj_2, env);
			else return f;

			if (lst_1.size() > lst_2.size()) return t;
			return f;
		}
	}
	// Q3 Code Ends Here





	@Override
	public Value visit(CarExp e, Env env) {
		Value.PairVal pair = (Value.PairVal) e.arg().accept(this, env);
		return pair.fst();
	}

	@Override
	public Value visit(CdrExp e, Env env) {
		Value.PairVal pair = (Value.PairVal) e.arg().accept(this, env);
		return pair.snd();
	}

	@Override
	public Value visit(ConsExp e, Env env) {
		Value first = (Value) e.fst().accept(this, env);
		Value second = (Value) e.snd().accept(this, env);
		return new Value.PairVal(first, second);
	}

	@Override
	public Value visit(ListExp e, Env env) { // New for funclang.
		List<Exp> elemExps = e.elems();
		int length = elemExps.size();
		if(length == 0)
			return new Value.Null();

		//Order of evaluation: left to right e.g. (list (+ 3 4) (+ 5 4))
		Value[] elems = new Value[length];
		for(int i=0; i<length; i++)
			elems[i] = (Value) elemExps.get(i).accept(this, env);

		Value result = new Value.Null();
		for(int i=length-1; i>=0; i--)
			result = new PairVal(elems[i], result);
		return result;
	}

	@Override
	public Value visit(NullExp e, Env env) {
		Value val = (Value) e.arg().accept(this, env);
		return new BoolVal(val instanceof Value.Null);
	}

	@Override
	public Value visit(LengthStrExp e, Env env) {
		Value val = (Value) e.getStrExpr().accept(this, env);
		if (val instanceof StringVal) {
			String string = ((StringVal) val).v();
			return new NumVal(string.length() - 2); // - 2 to remove double quotes
		}

		return new DynamicError("Parameter for length was not a string.");

	}

	public Value visit(EvalExp e, Env env) {
		StringVal programText = (StringVal) e.code().accept(this, env);
		Program p = _reader.parse(programText.v());
		return (Value) p.accept(this, env);
	}

	public Value visit(ReadExp e, Env env) {
		StringVal fileName = (StringVal) e.file().accept(this, env);
		try {
			String text = Reader.readFile("" + System.getProperty("user.dir") + File.separator + fileName.v());
			return new StringVal(text);
		} catch (IOException ex) {
			return new DynamicError(ex.getMessage());
		}
	}

	private Env initialEnv() {
		GlobalEnv initEnv = new GlobalEnv();

		List<String> definedformals = new ArrayList<>();
		/* Procedure: (read <filename>). Following is same as (define read (lambda (file) (read file))) */
		List<Exp> formals = new ArrayList<>();
		List<Exp> values = new ArrayList<Exp>();
		formals.add(new StrExp("file"));
		Exp body = new AST.ReadExp(new VarExp("file"));
		Value.FunVal readFun = new Value.FunVal(initEnv, formals, body);
		initEnv.extend("read", readFun);

		/* Procedure: (require <filename>). Following is same as (define require (lambda (file) (eval (read file)))) */
		formals = new ArrayList<>();
		formals.add(new StrExp("file"));
		body = new EvalExp(new AST.ReadExp(new VarExp("file")));
		Value.FunVal requireFun = new Value.FunVal(initEnv, formals, body);
		initEnv.extend("require", requireFun);

		/* Add new built-in procedures here */
		formals = new ArrayList<>();
		formals.add(new StrExp("file"));
		body = new AST.LengthStrExp(new VarExp("str"));
		Value.FunVal lengthFun = new Value.FunVal(initEnv, formals, body);
		initEnv.extend("length", lengthFun);

		return initEnv;
	}

	Reader _reader;
	public Evaluator(Reader reader) {
		_reader = reader;
	}
}
