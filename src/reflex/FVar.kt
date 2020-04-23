package reflex

class FVar <T> (v:Var<T>) : AVar<T,FVar<T>>(v) {
	fun <V> map(xform:(T) -> V): FVar<V>
		= FVar(spawnAndBind(xform))

	fun filter(cond:(T) -> Boolean): FVar<T>
		= FVar(spawnAndBind { v -> if (cond(v)) v else skip() })

	// todo: allow type conversion //

	fun fold(x0:T=skip(), f:(T,T) -> T): FVar<T> = _fold(x0, f, true)

	fun foldl(x0:T=skip(), f:(T,T) -> T): FVar<T> = _fold(x0, f, false)

	private fun _fold(x0:T=skip(), f:(T,T) -> T, right:Boolean): FVar<T> {
		var x = x0
		return FVar(spawnAndBind { y ->
			if (x == skip())
				x = y
			else
				x = if (right) f(y, x!!) else f(x!!, y)
			x
		})
	}
}
