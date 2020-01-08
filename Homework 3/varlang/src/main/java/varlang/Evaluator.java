package varlang;
import static varlang.AST.*;
import static varlang.Value.*;

import java.util.List;
import java.util.ArrayList;

import varlang.AST.AddExp;
import varlang.AST.NumExp;
import varlang.AST.DivExp;
import varlang.AST.MultExp;
import varlang.AST.Program;
import varlang.AST.SubExp;
import varlang.AST.VarExp;
import varlang.AST.LeteExp;
import varlang.AST.DecExp;
import varlang.AST.Visitor;
import varlang.Env.EmptyEnv;
import varlang.Env.ExtendEnv;
import varlang.Env.GlobalEnv;

public class Evaluator implements Visitor<Value> {

	GlobalEnv global_env = new GlobalEnv();

	Value valueOf(Program p) {
		Env env = new EmptyEnv();
		// Value of a program in this language is the value of the expression
		return (Value) p.accept(this, env);
	}

	@Override
	public Value visit(AddExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 0;
		for(Exp exp: operands) {
			NumVal intermediate;
			if (exp instanceof VarExp && global_env.get(((VarExp) exp)._name) != null) {
				intermediate = (NumVal) global_env.get(((VarExp) exp)._name);
			}
			else {
				try {
					intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
				} catch (Exception a) {
					intermediate = (NumVal) env.get(exp.accept(this, env) + "");
				}
			}
			result += intermediate.v(); //Semantics of AddExp in terms of the target language.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(NumExp e, Env env) {
		return new NumVal(e.v());
	}

	@Override
	public Value visit(DivExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal;
        if (operands.get(0) instanceof VarExp && global_env.get(((VarExp) operands.get(0))._name) != null) {
            lVal = (NumVal) global_env.get(((VarExp) operands.get(0))._name);
        }
        else lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal;
			if (operands.get(i) instanceof VarExp && global_env.get(((VarExp) operands.get(i))._name) != null) {
				rVal = (NumVal) global_env.get(((VarExp) operands.get(i))._name);
			}
			else rVal = (NumVal) operands.get(i).accept(this, env);
			result = result / rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(MultExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 1;
		for(Exp exp: operands) {
			NumVal intermediate;
			if (exp instanceof VarExp && global_env.get(((VarExp) exp)._name) != null) {
				intermediate = (NumVal) global_env.get(((VarExp) exp)._name);
			}
			else intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result *= intermediate.v(); //Semantics of MultExp.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(Program p, Env env) {
		return (Value) p.e().accept(this, env);
	}

	@Override
	public Value visit(SubExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal;
        if (operands.get(0) instanceof VarExp && global_env.get(((VarExp) operands.get(0))._name) != null) {
            lVal = (NumVal) global_env.get(((VarExp) operands.get(0))._name);
        }
        else lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal;
			if (operands.get(i) instanceof VarExp && global_env.get(((VarExp) operands.get(i))._name) != null) {
				rVal = (NumVal) global_env.get(((VarExp) operands.get(i))._name);
			}
			else rVal = (NumVal) operands.get(i).accept(this, env);
			result = result - rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(VarExp e, Env env) {
		// Previously, all variables had value 42. New semantics.
		return env.get(e.name());
	}

	// Start
	@Override
	public Value visit(DefExp e, Env env) {
		List<Exp> value_exps = e.value_exps();
		int count = 0;
		for (Exp exp : value_exps) {
			if(global_env.get(e.names().get(count)) == null)
				global_env.addVarVal(e.names().get(count), (Value) exp.accept(this, env));
			else global_env.setVal(e.names().get(count), (Value) exp.accept(this, env));
			count++;
		}
		return new Value.UnitVal();
	}

	@Override
	public Value visit(LetExp e, Env env) { // New for varlang.
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		Value current = null;
		int count = 0;
		for (Exp exp : value_exps) {
			if (exp instanceof VarExp && global_env.get(((VarExp) exp)._name) != null)
				current = (NumVal) global_env.get(((VarExp) exp)._name);
			else current = (Value) exp.accept(this, env);
			env = new ExtendEnv(env, names.get(count), current);
			count++;
		}
		return (Value) e.body().accept(this, env);
	}

	@Override
    public Value visit(LeteExp e, Env env){
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		Value current = null;
		int count = 0;
		for (Exp exp : value_exps){
			if (exp instanceof VarExp && global_env.get(((VarExp) exp)._name) != null)
				current = (NumVal) global_env.get(((VarExp) exp)._name);
			else current = (Value) exp.accept(this, env);
			env = new ExtendEnv(env, names.get(count), current, ((NumExp) e._key)._val);
			count++;
		}
		return (Value) e.body().accept(this, env);
    }

    @Override
    public Value visit(DecExp e, Env env){
		if (env.get(e._lock.name()) != null)
			return ((ExtendEnv) env).getDec(e._lock.name(), e._key._val);
		else return new Value.UnitVal();
		/*
		try {
			if (env.get(e._lock.name()) != null)
				return ((ExtendEnv) env).getDec(e._lock.name(), e._key._val);
			else return new UnitVal();
		}
		catch(Exception a) {
			System.out.println("Keys did not match");
			return new UnitVal();
		}
		*/
	}

	/*
	Basic Testing
	(define month 9)
	(let ((x 1)) (+ x month))

	(let ((a 3) (b a) (c (+ a b))) c)
	(define z 10000)
	(let ((a 3) (b z) (c (+ a b))) c)

	(lete 2 ((x 1)) (let ((y 20)) (+ (dec 2 y) x)))
	(lete 20 ((x 1)) (dec 20 x))
	(lete 2 ((x 1)) x)
	(lete 10 ((y 8)) y)
	(lete 10 ((y 12)) (dec 10 y))
	 */

	// End
}
