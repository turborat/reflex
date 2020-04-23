package reflex

class Reflex {
	val fifo        = ArrayList<() -> Unit>()
	var firing      = false
	var cycle       = 0L
	var reflexions  = 0L

	fun <T> newVar() = Var0<T>(this)

	fun doCycle(r:() -> Unit): Long {
		fifo.add(r)

		if (firing) {
			return 0
		}

		cycle++
		firing = true

		val prevReflexions = reflexions
		var n = 0

		while (n < fifo.size) {
			assert (fifo.size < 999) { "loop" }
			fifo[n++]()
			// note: no error handling //
		}

		fifo.clear()
		firing = false

		return reflexions - prevReflexions
	}
}
