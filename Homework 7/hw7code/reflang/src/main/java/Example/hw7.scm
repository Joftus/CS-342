// a.
(deref (ref 342))
// output: 342
(free (ref 342))
// output: UnitVal
(let ((loc (ref 342))) (set! loc 541))
// output: 541
(let ((loc (ref 540))) (set! loc (deref loc)))
// output: 540

// b.
// Alias 1:
(let ((class (ref 3))) (deref class))
// output: 3
// Alias 2:
(let ((class (ref 352))) (free class))
// output: UnitVal

// c.
// i.
// A.
(define node (lambda (value) (ref value)))
// B.
(define value (lambda (node) (deref node)))
// C.
(define right (lambda (node) (deref (+ (* (- (deref node) 1) 2) 2))))
(define left (lambda (node) (deref (+ (* (- (deref node) 1) 2) 1))))
// ii.
// A-D. Trying to exploit the 2n + (1 or 2, left vs right child), relation since the values are being stored in an array.
// Need to take parent_value - 1 to get the index the parent node is stored.
(define add (lambda (p which c) (if (= which #t) (set! (+ (* (- (deref p) 1) 2) 1) c) (set! (+ (* (- (deref p) 1) 2) 2) c))))