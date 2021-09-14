(define (NewtonRoot x)
	(define (NewtonHelp bignum guess lastguess loopcheck)
		(if (= guess lastguess)
			guess
			(if (= guess loopcheck)
				(if (> guess lastguess)
					lastguess
					guess
				)
				(NewtonHelp
					bignum
					(quotient (+ guess (quotient bignum guess)) 2)
					guess
					lastguess
				)
			)
		)
	)
	(if (< x 1)
		'()
		(NewtonHelp x (expt 2 (inexact->exact (round (/ (log x) (log 4))))) -1 -2)
	)
)
