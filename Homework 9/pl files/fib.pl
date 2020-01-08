fib(0, 1) :- !.
fib(1, 1) :- !.
fib(I, O) :-
        I > 1,
        I1 is I-1,
        I2 is I-2,
        fib(I1, O1),
        fib(I2, O2),
        O is O1 + O2.