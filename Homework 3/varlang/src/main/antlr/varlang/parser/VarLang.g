grammar VarLang;

import ArithLang; //Import all rules from Arithlang grammar.
 
 // New elements in the Grammar of this Programming Language
 //  - grammar rules start with lowercase

 exp returns [Exp ast]: 
		v=varexp { $ast = $v.ast; }
		| n=numexp { $ast = $n.ast; }
        | a=addexp { $ast = $a.ast; }
        | s=subexp { $ast = $s.ast; }
        | m=multexp { $ast = $m.ast; }
        | d=divexp { $ast = $d.ast; }
        | l=letexp { $ast = $l.ast; }
        | b=defexp { $ast = $b.ast; }
        | c=leteexp { $ast = $c.ast; }
        | e=decexp { $ast = $e.ast; }
        ;

 varexp returns [VarExp ast]: 
 		id=Identifier { $ast = new VarExp($id.text); }
 		;

 letexp  returns [LetExp ast] 
        locals [ArrayList<String> names, ArrayList<Exp> value_exps]
 		@init { $names = new ArrayList<String>(); $value_exps = new ArrayList<Exp>(); } :
 		'(' Let
 			'(' ( '(' id=Identifier e=exp ')' { $names.add($id.text); $value_exps.add($e.ast); } )+  ')'
 			body=exp 
 			')' { $ast = new LetExp($names, $value_exps, $body.ast); }
 		;

 defexp returns [DefExp ast]
         locals [ArrayList<String> names, ArrayList<Exp> value_exps]:
         { $names = new ArrayList<String>(); $value_exps = new ArrayList<Exp>(); }
         '(' Define
             ( id=Identifier e=exp { $names.add($id.text); $value_exps.add($e.ast); } )+
             ')' { $ast = new DefExp($names, $value_exps); }
         ;

  leteexp returns [LeteExp ast]
     locals [ArrayList<String> names, ArrayList<Exp> value_exps]:
     { $names = new ArrayList<String>(); $value_exps = new ArrayList<Exp>(); }
     '(' Lete
         key=exp
         '(' ( '(' id=Identifier e=exp ')' { $names.add($id.text); $value_exps.add($e.ast); } )+  ')'
         body=exp
         ')' { $ast = new LeteExp($names, $value_exps, $key.ast, $body.ast); }
     ;

  decexp returns [DecExp ast]:
     '(' Dec
         key=numexp
         var=varexp
         ')' { $ast = new DecExp($key.ast, $var.ast); }
     ;

 // Lexical Specification of this Programming Language
 //  - lexical specification rules start with uppercase
 