package reflex

class Trace<T> (v: Var<T> ?= null)
{
	val path = ArrayList<String>()
	var v:T? = null

	init {
		if (v != null)
			bind("", v)
	}

	fun bind(id:String, v:Var<*>) {
		var n = 0
		v.bind { vv ->
			++n
			path.add(id+":"+n+"="+vv)
			this.v = vv as T?
		}
	}

	fun reflexions() = path.size

	override fun toString() = path.toString()
}