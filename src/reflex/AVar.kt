package reflex

abstract class AVar<out T, V:Var<T>>(val v:Var<T>): Var<T>
{
	override fun bind(f: (T) -> Unit) = v.bind(f)

	override fun <X> spawn(): Var0<X> = v.spawn()

	protected fun <X> spawnAndBind(f: (T) -> X): Var<X> {
		val var0 = spawn<X>()
		bind { v:T ->
			val newVal = f(v)
			if (newVal != SKIP) {
				var0.accept(newVal)
			}
		}
		return var0
	}

	private companion object {
	    val SKIP = Object()
	}

	protected fun skip():T = SKIP as T
}
