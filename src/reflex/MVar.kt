package reflex;

class MVar <N:Number> (v:Var<N>) : AVar<N, MVar<N>> (v) {
	fun <M:Number> op(f: (N) -> M): MVar<M>
		= MVar(spawnAndBind(f))

	private fun _op(f: (Double) -> Double): MVar<N>
		= MVar(spawnAndBind { v -> f(v.toDouble()) as N })

	fun plus(num:N): MVar<N>
		= _op { v -> v + num.toDouble()}

	fun times(num:N): MVar<N>
		= _op { v -> v * num.toDouble() }

	fun sum(): MVar<N> {
		var sum = 0.0
		return _op { v -> sum += v ; sum }
	}

	fun count(): MVar<Long> {
		var count = 0L
		return op { _ -> ++count }
	}
}
