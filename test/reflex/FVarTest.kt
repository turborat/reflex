package reflex

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FVarTest {
	val r = Reflex()
	val v0:Var0<Int> = r.newVar()
	val v1:FVar<Int> = FVar(v0)

 	@Test fun map()	{
		val trace = Trace(v1.map { n -> n * n })
		assertEquals(0, trace.reflexions())

		v0.accept(2)
		assertEquals(1, trace.reflexions())
		assertEquals(4, trace.v)

		v0.accept(4)
		assertEquals(2, trace.reflexions())
		assertEquals(16, trace.v)
	}

	@Test fun filter() {
		val trace = Trace(v1.filter { v -> v % 2 == 0 })

		v0.accept(1)
		assertEquals(0, trace.reflexions())

		v0.accept(2)
		assertEquals(1, trace.reflexions())
		assertEquals(2, trace.v)
	}

	@Test fun dep2d() {
		val trace1 = Trace(v1.map { x -> x }.filter { _ -> true })

		v0.accept(5)
		assertEquals(1, trace1.reflexions())
		assertEquals(5, trace1.v)
	}

	@Test fun fold_sum() {
		val trace1 = Trace(v1.fold(0) { x, y -> x + y })
		assertNull(trace1.v)

		v0.accept(2)
		assertEquals(2, trace1.v)

		v0.accept(3)
		assertEquals(5, trace1.v)

		v0.accept(7)
		assertEquals(12, trace1.v)
	}

	@Test fun fold_max() {
		val trace1 = Trace(v1.foldl(0) { x, y -> Math.max(x, y) })
		assertNull(trace1.v)

		v0.accept(2)
		assertEquals(2, trace1.v)

		v0.accept(3)
		assertEquals(3, trace1.v)

		v0.accept(1)
		assertEquals(3, trace1.v)
	}

	@Test fun fold_count() {
		var n = 0
		val trace1 = Trace(v1.foldl(7) { _, _ -> ++n })
		v0.accept(34)
		assertEquals(1, trace1.v)
		v0.accept(56)
		assertEquals(2, trace1.v)
		v0.accept(12)
		assertEquals(3, trace1.v)
	}

	@Test fun fold_div1() {
		val v0d = r.newVar<Double>()
		val v1d = FVar(v0d)
		val trace1 = Trace(v1d.fold { x, y -> x / y })
		assertNull(trace1.v)
		v0d.accept(1.0)
		assertEquals(1.0, trace1.v)
		v0d.accept(2.0)
		assertEquals(2.0, trace1.v)
		v0d.accept(3.0)
		assertEquals(1.5, trace1.v)
	}

	@Test fun fold_div2() {
		val v0d = r.newVar<Double>()
		val v1d = FVar(v0d)
		val trace1 = Trace(v1d.fold(1.0) { x, y -> x / y })
		assertNull(trace1.v)
		v0d.accept(2.0)
		assertEquals(2.0, trace1.v)
		v0d.accept(3.0)
		assertEquals(1.5, trace1.v)
	}

	@Test fun foldl_div1() {
		val v0d = r.newVar<Double>()
		val v1d = FVar(v0d)
		val trace1 = Trace(v1d.foldl { x, y -> x / y })
		assertNull(trace1.v)
		v0d.accept(1.0)
		assertEquals(1.0, trace1.v)
		v0d.accept(2.0)
		assertEquals(0.5, trace1.v)
		v0d.accept(3.0)
		assertEquals(1/6.0, trace1.v)
	}

	@Test fun foldl_div2() {
		val v0d = r.newVar<Double>()
		val v1d = FVar(v0d)
		val trace1 = Trace(v1d.foldl(1.0) { x, y -> x / y })
		assertNull(trace1.v)
		v0d.accept(2.0)
		assertEquals(0.5, trace1.v)
		v0d.accept(3.0)
		assertEquals(1/6.0, trace1.v)
	}
}
