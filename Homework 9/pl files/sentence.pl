% S -> F|T N T
% F -> if B then S|if B then S else S
% B -> T E T
% T -> x|y|z|1|0
% E -> > | <
% N -> +| - | =


sentence([]).
sentence([if|Tail]) :- 
	ruleF(Tail).
sentence([T, else|Tail]) :- 
	ruleT(T),
	sentence(Tail).
sentence([T, then|Tail]) :- 
	ruleT(T),
	sentence(Tail).
sentence([A]) :-
	ruleT(A).

ruleF([T1, E, T2, then|Tail]) :-
	checkT(T1),
	checkOP(E),
	checkT(T2),
	(ruleT(Tail) -> tmp(a); sentence(Tail)).

ruleB([T1, E, T2]) :-
	checkT(T1),
	checkOP(E),
	checkT(T2).

ruleT([T1, A, T2|Tail]) :-
	checkT(T1),
	checkOP(A),
	checkT(T2),
	(Tail == [] -> tmp(a); sentence(Tail)).

checkT(x).
checkT(y).
checkT(z).
checkT(1).
checkT(0).

checkOP(>).
checkOP(<).
checkOP(+).
checkOP(-).
checkOP(=).

tmp(a).