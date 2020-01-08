package typelang;

import java.util.ArrayList;
import java.util.List;

import typelang.AST.*;
import typelang.Env.ExtendEnv;
import typelang.Env.GlobalEnv;
import typelang.Type.*;

public class Checker implements Visitor<Type,Env<Type>> {
	Printer.Formatter ts = new Printer.Formatter();
	Env<Type> initEnv = initialEnv(); //New for definelang
	
	private Env<Type> initialEnv() {
		GlobalEnv<Type> initEnv = new GlobalEnv<Type>();
		
		/* Type for procedure: (read <filename>). Following is same as (define read (lambda (file) (read file))) */
		List<Type> formalTypes = new ArrayList<Type>();
		formalTypes.add(new Type.StringT());
		initEnv.extend("read", new Type.FuncT(formalTypes, new Type.StringT()));

		/* Type for procedure: (require <filename>). Following is same as (define require (lambda (file) (eval (read file)))) */
		formalTypes = new ArrayList<Type>();
		formalTypes.add(new Type.StringT());
		initEnv.extend("eval", new Type.FuncT(formalTypes, new Type.UnitT()));
		
		/* Add type for new built-in procedures here */ 
		
		return initEnv;
	}
	
    Type check(Program p) {
		return (Type) p.accept(this, null);
	}

	public Type visit(Program p, Env<Type> env) {
		Env<Type> new_env = initEnv;

		for (DefineDecl d: p.decls()) {
			Type type = (Type)d.accept(this, new_env);

			if (type instanceof ErrorT) { return type; }

			Type dType = d.type();

			if (!type.typeEqual(dType)) {
				return new ErrorT("Expected " + dType + " found " + type + " in " + ts.visit(d, null));
			}

			new_env = new ExtendEnv<Type>(new_env, d.name(), dType);
		}
		return (Type) p.e().accept(this, new_env);
	}

	public Type visit(VarExp e, Env<Type> env) {
		try {
			return env.get(e.name());
		} catch(Exception ex) {
			return new ErrorT("Variable " + e.name() +
					" has not been declared in " + ts.visit(e, null));
		}
	}

	// (let ((x: num 34) (y : num 45) (cond: bool #t)) (if x (+ x y) (/ x y)))
	// (let ((x: num #t) (y: bool 8)) x)
	public Type visit(LetExp e, Env<Type> env) {
		// answer question 6
		for (Exp exp : e._value_exps) {
			Type ve_type = (Type) exp.accept(this, env);
			if (ve_type instanceof ErrorT) return new ErrorT("");
		}
		int count = 0;
		for (Type v_type : e._varTypes) {
			Type ve_type = (Type) e._value_exps.get(count).accept(this, env);
			if (!(ve_type.typeEqual(v_type))) return new ErrorT("The condition should have " + v_type.tostring() + " type, found " + ve_type.tostring() + " in expression");
			count ++;
		}
		return (Type) e._body.accept(this, env);
	}

	public Type visit(DefineDecl d, Env<Type> env) {
		String name = d.name();
		Type t =(Type) d._value_exp.accept(this, env);
		((GlobalEnv<Type>) initEnv).extend(name, t);
		return t;
	}

	public Type visit(LambdaExp e, Env<Type> env) {
		List<String> names = e.formals();
		List<Type> types = e.types();
		String message = "The number of formal parameters and the number of "
				+ "arguments in the function type do not match in ";
		if (types.size() == names.size()) {
			Env<Type> new_env = env;
			int index = 0;
			for (Type argType : types) {
				new_env = new ExtendEnv<Type>(new_env, names.get(index),
						argType);
				index++;
			}

			Type bodyType = (Type) e.body().accept(this, new_env);
			return new FuncT(types,bodyType);
		}
		return new ErrorT(message + ts.visit(e, null));
	}

	// (define add: (num num num -> num) (lambda (x: num y: num z: num) (+ x (+ y z))))
	// (add 5 56 #t)
	// (3 4)
	public Type visit(CallExp e, Env<Type> env) {
		// answer question 7
		Type op_type = (Type) e._operator.accept(this, env);
		if (op_type instanceof ErrorT) return new ErrorT("");
		if (!(op_type instanceof FuncT)) return new ErrorT("Expect a function type in the call expression, found " + op_type.tostring() + " in expression");
		int count = 0;
		for (Exp op : e._operands) {
			Type type = (Type) op.accept(this, env);
			Type tmp = ((FuncT) op_type)._argTypes.get(count);
			if (type instanceof ErrorT) return new ErrorT("");
			else if (!type.typeEqual(tmp)) {
				return new ErrorT("The expected type of the " + (count + 1) + "th acutal parameter is " +
						tmp.tostring() + ", found " + type.tostring() + " in expression");
			}
			count++;
		}
		return ((FuncT) e._operator.accept(this, env))._returnType;
	}

	// (if 5 56 67)
	// (if #t #t 56)
	public Type visit(IfExp e, Env<Type> env) {
		// answer question 5
		Exp condition = e._conditional;
		Type condition_type, else_type, then_type;
		condition_type = (Type) condition.accept(this, env);
		else_type = (Type) e.else_exp().accept(this, env);
		then_type = (Type) e.then_exp().accept(this, env);
		if (condition_type instanceof ErrorT) { return condition_type; }
		else if (!(condition_type instanceof BoolT))
			return new ErrorT("The condition should have boolean type, found " + condition_type.tostring() + " in " + ts.visit(e, null));
		if (then_type instanceof ErrorT) { return then_type; }
		else if (else_type instanceof ErrorT) { return else_type; }
		else if (then_type.typeEqual(else_type)) { return then_type; }
		else { 
			return new ErrorT("The then and else expressions should have the same type, then has type " + 
				then_type.toString() + " else has type " + else_type.tostring() + " in " + ts.visit(e, null));
		}
	}

	public Type visit(CarExp e, Env<Type> env) {
		Exp exp = e.arg();
		Type type = (Type)exp.accept(this, env);
		if (type instanceof ErrorT) { return type; }

		if (type instanceof PairT) {
			PairT pt = (PairT)type;
			return pt.fst();
		}

		return new ErrorT("The car expect an expression of type Pair, found "
				+ type.tostring() + " in " + ts.visit(e, null));
	}

	// (cdr 2)
	// (cdr (cdr 2))
	public Type visit(CdrExp e, Env<Type> env) {
		// answer question 2(a)
		Type type = (Type) e.arg().accept(this, env);
		if (type instanceof PairT) return ((PairT) type)._snd;
		else return new ErrorT("The cdr expect an expression of type Pair, found " + type.tostring() + " in equation");
	}

	public Type visit(ConsExp e, Env<Type> env) {
		Exp fst = e.fst(); 
		Exp snd = e.snd();

		Type t1 = (Type)fst.accept(this, env);
		if (t1 instanceof ErrorT) { return t1; }

		Type t2 = (Type)snd.accept(this, env);
		if (t2 instanceof ErrorT) { return t2; }

		return new PairT(t1, t2);
	}

	// (list : bool 1 2 3 4 5 6 7)
	// (list : num 1 2 3 4 5 #t 6 7 8)
	public Type visit(ListExp e, Env<Type> env) {
		// answer question 2(b)
		List<Exp> elmlst = e.elems();
		Type type = e.type();
		int count = 0;
		for (Exp elmt : elmlst) {
			Type tmp = (Type) elmt.accept(this,env);
			if (tmp instanceof ErrorT) return new ErrorT("");
			else if (!(tmp.typeEqual(type)))
				return new ErrorT("The " + count + " expression should have type " +
					type.tostring() + ", found " + ((Type) elmt.accept(this, env)).tostring() + " in expression");
			count ++;
		}
		return new ListT(type);
	}

	public Type visit(NullExp e, Env<Type> env) {
		Exp arg = e.arg();
		Type type = (Type)arg.accept(this, env);
		if (type instanceof ErrorT) { return type; }

		if (type instanceof ListT) { return BoolT.getInstance(); }

		return new ErrorT("The null? expect an expression of type List, found "
				+ type.tostring() + " in " + ts.visit(e, null));
	}

	public Type visit(RefExp e, Env<Type> env) {
		// answer question 1(a)
		Type type = (Type) e.value_exp().accept(this, env);
		Type t = e.type();
		if (type instanceof ErrorT) { return new ErrorT(""); }
		else if (type.typeEqual(t)) { return new RefT(type); }
		String tstr = t.tostring();
		return new ErrorT("The Ref expression expect type " + tstr + " found " + type.tostring() + " in " + "(ref "+ ((Type) e.value_exp().accept(this, env)).tostring() + ")");
	}

	public Type visit(DerefExp e, Env<Type> env) {
		Exp exp = e.loc_exp();
		Type type = (Type)exp.accept(this, env);
		if (type instanceof ErrorT) { return type; }

		if (type instanceof RefT) {
			RefT rt = (RefT)type;
			return rt.nestType();
		}

		return new ErrorT("The dereference expression expect a reference type " +
				"found " + type.tostring() + " in " + ts.visit(e, null));
	}

	// (set! (ref : num 0) #t)
	// (set! (ref : bool #t) (list : num 1 2 3 4 5 6 ))
	public Type visit(AssignExp e, Env<Type> env) {
		// answer question 1(b)
		Type lhs = (Type) e.lhs_exp().accept(this, env);
		Type rhs = (Type) e.rhs_exp().accept(this, env);
		Type t = ((RefExp) e.lhs_exp())._type;
		if (lhs instanceof ErrorT) return new ErrorT("");
		else if (lhs instanceof RefT && ((RefT) lhs)._nestType.typeEqual(t)) {
			if (rhs instanceof ErrorT) return new ErrorT("");
			else if (rhs.typeEqual(t)) return t;
			else return new ErrorT("The inner type of the reference type is " + t.tostring() + " the rhs type is " + rhs.tostring() + " in expression");
		}
		else return new ErrorT("The lhs of the assignment expression expect a reference type found " + lhs.tostring() + " in " + e.toString());
	}

	public Type visit(FreeExp e, Env<Type> env) {
		Exp exp = e.value_exp();
		Type type = (Type)exp.accept(this, env);

		if (type instanceof ErrorT) { return type; }

		if (type instanceof RefT) { return UnitT.getInstance(); }

		return new ErrorT("The free expression expect a reference type " +
				"found " + type.tostring() + " in " + ts.visit(e, null));
	}

	public Type visit(UnitExp e, Env<Type> env) {
		return Type.UnitT.getInstance();
	}

	public Type visit(NumExp e, Env<Type> env) {
		return NumT.getInstance();
	}

	public Type visit(StrExp e, Env<Type> env) {
		return Type.StringT.getInstance();
	}

	public Type visit(BoolExp e, Env<Type> env) {
		return Type.BoolT.getInstance();
	}

	public Type visit(LessExp e, Env<Type> env) {
		return visitBinaryComparator(e, env, ts.visit(e, null));
	}

	public Type visit(EqualExp e, Env<Type> env) {
		return visitBinaryComparator(e, env, ts.visit(e, null));
	}

	public Type visit(GreaterExp e, Env<Type> env) {
		return visitBinaryComparator(e, env, ts.visit(e, null));
	}

	// (< #t #t)
	// (> (list: num 45 45 56 56 67) 67)
	private Type visitBinaryComparator(BinaryComparator e, Env<Type> env, String printNode) {
		// answer question 4
		Type first_type = (Type) e.first_exp().accept(this, env);
		Type second_type = (Type) e.second_exp().accept(this, env);
		if (first_type instanceof ErrorT || second_type instanceof ErrorT) return new ErrorT("");
		else if (!(first_type instanceof NumT)) return new ErrorT("The first argument of a binary expression should be num Type, found " + first_type.tostring() + " in expression");
		else if (!(second_type instanceof NumT)) return new ErrorT("The second argument of a binary expression should be num Type, found " + second_type.tostring() + " in expression");
		return new BoolT();
	}


	public Type visit(AddExp e, Env<Type> env) {
		return visitCompoundArithExp(e, env, ts.visit(e, null));
	}

	public Type visit(DivExp e, Env<Type> env) {
		return visitCompoundArithExp(e, env, ts.visit(e, null));
	}

	public Type visit(MultExp e, Env<Type> env) {
		return visitCompoundArithExp(e, env, ts.visit(e, null));
	}

	public Type visit(SubExp e, Env<Type> env) {
		return visitCompoundArithExp(e, env, ts.visit(e, null));
	}

	// (+ #t 6)
	// (+ 5 6 7 #t 56)
	// (* 45.0 #t)
	// (/ (list : num 3 4 5 6 7) 45)
	private Type visitCompoundArithExp(CompoundArithExp e, Env<Type> env, String printNode) {
		// answer question 3
		int count = 0;
		for (Exp exp : e._rest) {
			Type type = (Type) exp.accept(this, env);
			if (type instanceof ErrorT) return new ErrorT("");
			else if (!(type instanceof NumT))
				return new ErrorT("expected num found " + type.tostring() + " in expression");
			count ++;
		}
		return new NumT();
	}

	private static boolean assignable(Type t1, Type t2) {
		if (t2 instanceof UnitT) { return true; }

		return t1.typeEqual(t2);
	}
	
	public Type visit(ReadExp e, Env<Type> env) {
		return UnitT.getInstance();
	}

	public Type visit(EvalExp e, Env<Type> env) {
		return UnitT.getInstance();
	}
}
