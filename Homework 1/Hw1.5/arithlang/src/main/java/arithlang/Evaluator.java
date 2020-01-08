package arithlang;
import static arithlang.AST.*;
import static arithlang.Value.*;

import java.util.List;

public class Evaluator implements Visitor<Value> {
    private NumVal record = new NumVal(0);
    Printer.Formatter ts = new Printer.Formatter();
	
    Value valueOf(Program p) {
        // Value of a program in this language is the value of the expression
        return (Value) p.accept(this);
    }
	
    @Override
    public Value visit(AddExp e) {
        List<Exp> operands = e.all();
        String a = (operands.get(0).accept(this).toString());
        String b = (operands.get(1).accept(this).toString());
        String result = "";
        if (operands.size() > 2) {
            System.out.println("\nToo Big My Dood!\n");
            throw new Error();
        }
        if (a.equals("u") || b.equals("u")) result = "u";
        else if (a.equals("e")){
            if (b.equals("e")) result = "e";
            else if (b.equals("o")) result = "o";
            else throw new Error();
        }
        else if (a.equals("o")){
            if (b.equals("e")) result = "o";
            else if (b.equals("o")) result = "e";
            else throw new Error();
        }
        else throw new Error();
        return new NumVal(result);
    }

    @Override
    public Value visit(NumExp e) {
        return new NumVal(e.t());
    }


    @Override
    public Value visit(MultExp e) {
        List<Exp> operands = e.all();
        String a = (operands.get(0).accept(this).toString());
        String b = (operands.get(1).accept(this).toString());
        String result = "";

        if (operands.size() > 2) {
            System.out.println("\nToo Big My Dood!\n");
            throw new Error();
        }
        if (a.equals("u") || b.equals("u")) result = "u";
        else if (a.equals("e")){
            if (b.equals("e") || b.equals("o")) result = "e";
            else throw new Error();
        }
        else if (a.equals("o")){
            if (b.equals("e")) result = "e";
            else if (b.equals("o")) result = "o";
            else throw new Error();
        }
        else throw new Error();
        return new NumVal(result);
    }

    @Override
    public Value visit(Program p) {
        return (Value) p.e().accept(this);
    }
}
