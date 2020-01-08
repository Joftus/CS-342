flip(L,R) :- rev(L,[],R).

rev([],A,A).
rev([H|T],A,R) :-
    ( is_list(H) ->        % If H is a list
      rev(H,[],X),         %   then reverse H as well
      rev(T,[X|A],R)
    ;
      rev(T,[H|A],R)
    ).