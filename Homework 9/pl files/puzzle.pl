% Question 1
zebra(Owner) :- houses(H), member(h(Owner,zebra,_,_,_), H).
% Question 2
water(Drinker) :- houses(H), member(h(Drinker,_,_,water,_), H).

% Utility Functions
nextto(X, Y, L) :-
	append(_, [X,Y|_], L).
nextto(X, Y, L) :-
	append(_, [Y,X|_], L).
rightof(X, Y, L) :-
	append(_, [X,Y|_], L).

% Core Analysis
houses(H) :-
	length(H, 5),
	member(h(englishman,_,_,_,red), H),
	member(h(spaniard,dog,_,_,_), H),
	member(h(_,_,_,coffee,green), H),
	member(h(ukrainian,_,_,tea,_), H),
	rightof(h(_,_,_,_,ivory), h(_,_,_,_,green), H),
	member(h(_,snails,oldgold,_,_), H),
	member(h(_,_,kools,_,yellow), H),
	H = [_,_,h(_,_,_,milk,_),_,_],
	H = [h(norwegian,_,_,_,_)|_],
	nextto(h(_,_,camel,_,_), h(_,fox,_,_,_), H),
	nextto(h(_,_,kools,_,_), h(_,horse,_,_,_), H),
	member(h(_,_,luckystrike,orangejuice,_), H),
	member(h(japanese,_,parlaiments,_,_), H),
	nextto(h(norwegian,_,_,_,_), h(_,_,_,_,blue), H),
	member(h(_,_,_,water,_), H),
	member(h(_,zebra,_,_,_), H).