grammar ArithLang;

 // Grammar of this Programming Language
 //  - grammar rules start with lowercase
 program returns [Program ast] : 
		e=exp { $ast = new Program($e.ast); }
		;

 exp returns [Exp ast]:
		n=numexp { $ast = $n.ast; }
        | a=addexp { $ast = $a.ast; }
        | m=multexp { $ast = $m.ast; }
        ;
  
 numexp returns [NumExp ast]:
 		n0=Number { $ast = new NumExp(Integer.parseInt($n0.text)); } 
  		| '-' n0=Number { $ast = new NumExp(-Integer.parseInt($n0.text)); }
  		| n0=Number Dot n1=Number { $ast = new NumExp(Double.parseDouble($n0.text+"."+$n1.text)); }
  		| '-' n0=Number Dot n1=Number { $ast = new NumExp(Double.parseDouble("-" + $n0.text+"."+$n1.text)); }
  		| n0=Identifier { $ast = new NumExp($n0.text); }
  		;		
  
 addexp returns [AddExp ast]
        locals [ArrayList<Exp> list]
 		@init { $list = new ArrayList<Exp>(); } :
 		'(' '+'
 		    e=exp { $list.add($e.ast); } 
 		    ( e=exp { $list.add($e.ast); } )+
 		')' { $ast = new AddExp($list); }
 		;

 multexp returns [MultExp ast] 
        locals [ArrayList<Exp> list]
 		@init { $list = new ArrayList<Exp>(); } :
 		'(' '*'
 		    e=exp { $list.add($e.ast); } 
 		    ( e=exp { $list.add($e.ast); } )+ 
 		')' { $ast = new MultExp($list); }
 		;

 // Lexical Specification of this Programming Language
 //  - lexical specification rules start with uppercase
 
 Define : 'define' ;
 Let : 'let' ;
 Dot : '.' ;

 Number : DIGIT+ ;

 Identifier :   Letter LetterOrDigit*;

 Letter :   [a-zA-Z$_]
	|   ~[\u0000-\u00FF\uD800-\uDBFF] 
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|   [\uD800-\uDBFF] [\uDC00-\uDFFF] 
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}? ;

 LetterOrDigit: [a-zA-Z0-9$_]
	|   ~[\u0000-\u00FF\uD800-\uDBFF] 
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|    [\uD800-\uDBFF] [\uDC00-\uDFFF] 
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?;

 fragment DIGIT: ('0'..'9');

 AT : '@';
 ELLIPSIS : '...';
 WS  :  [ \t\r\n\u000C]+ -> skip;
 Comment :   '/*' .*? '*/' -> skip;
 Line_Comment :   '//' ~[\r\n]* -> skip;
