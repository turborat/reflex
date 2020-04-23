package reflex;

interface Var <out T> {
	fun bind(f:(T) -> Unit)
	fun <X> spawn(): Var0<X>
}
