package reflex

class Var0<T> (val reflex: Reflex) : Var<T> {
    private val joints = ArrayList<(T) -> Unit>()

    private fun cycle(v:T) {
        for (joint in joints) {
            try {
                joint(v)
                reflex.reflexions++
            }
            catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    fun accept(v:T): Long {
        assert (v != null) { "null val" }
        return reflex.doCycle { cycle(v) }
    }

    override fun bind(f: (T) -> Unit) {
        assert (joints.size < 99) { "loop" }
        joints.add(f)
    }

    // so we don't have to pass around reflex? //
    override fun <X> spawn(): Var0<X> {
        return reflex.newVar()
    }
}
