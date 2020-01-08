mystery([], L2, L2).
mystery([H|Tail], L2, [R|RTail]) :-
	H = R,
	mystery(Tail, L2, RTail).