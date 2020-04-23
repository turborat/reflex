package reflex

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNull

abstract class MVarTest <T:Number> (
		val nTwo:T, 
		val two:T, 
		val three:T, 
		val four:T, 
		val seven:T, 
		val ten:T, 
		val twenty:T, 
		val hundred:T
) {
	val reflex = Reflex()
	val var0 = reflex.newVar<T>()
	val mv = MVar(var0)
	
	@Test fun plus() {
		val trace = Trace(mv.plus(four))
		assertNull(trace.v)
		assertEquals(0, trace.reflexions())

		var0.accept(two)
		assertEquals(6.0, trace.v?.toDouble())
		assertEquals(1, trace.reflexions())

		var0.accept(nTwo)
		assertEquals(2.0, trace.v?.toDouble())
		assertEquals(2, trace.reflexions())
	}

	@Test fun sum() {
		val trace = Trace(mv.sum())
		assertNull(trace.v)
		assertEquals(0, trace.reflexions())

		var0.accept(three)
		assertEquals(3.0, trace.v?.toDouble())
		assertEquals(1, trace.reflexions())

		var0.accept(four)
		assertEquals(7.0, trace.v?.toDouble())
		assertEquals(2, trace.reflexions())
	}

	@Test fun count() {
		val trace = Trace(mv.count())
		assertNull(trace.v)
		assertEquals(0, trace.reflexions())

		var0.accept(three)
		assertEquals(1.0, trace.v?.toDouble())
		assertEquals(1, trace.reflexions())

		var0.accept(four)
		assertEquals(2.0, trace.v?.toDouble())
		assertEquals(2, trace.reflexions())

		var0.accept(four)
		assertEquals(3.0, trace.v?.toDouble())
		assertEquals(3, trace.reflexions())
	}

	@Test fun arithmetic() {
		val count  = Trace(mv.count())
		val plus10 = Trace(mv.plus(ten))
		val plus20 = Trace(mv.plus(twenty))
		val times2 = Trace(mv.times(two))

		assertEquals(8,  var0.accept(three))
		assertEquals(1.0,  count.v?.toDouble())
		assertEquals(13.0, plus10.v?.toDouble())
		assertEquals(23.0, plus20.v?.toDouble())
		assertEquals(6.0,  times2.v?.toDouble())
	}

	@Test fun arithmetic2() {
		val res = mv.times(ten).plus(seven).times(hundred)
		val out = Trace(res)

		assertEquals(4, var0.accept(three))
		assertEquals(3700.0, out.v?.toDouble())

		val out2 = Trace(res)
		assertEquals(5, var0.accept(three))
		assertEquals(3700.0, out2.v?.toDouble())
	}

	@Test fun op1() {
		val sqrt = Trace(mv.op { v -> Math.sqrt(v.toDouble()) })

		var0.accept(four)
		assertEquals(2.0, sqrt.v)

		var0.accept(seven)
		assertEquals(2.6457513110645907, sqrt.v)
	}

	@Test fun op2() {
		val sqr = Trace(mv.op { v -> (v.toDouble() * v.toDouble()) })

		var0.accept(four)
		assertEquals(16.0, sqr.v)

		var0.accept(seven)
		assertEquals(49.0, sqr.v)
	}

}

class DoubleTest : MVarTest<Double>(-2.0, 2.0, 3.0, 4.0, 7.0, 10.0, 20.0, 100.0) {
	@Test fun fractions() {
		val trace = Trace(mv.times(.5))
		var0.accept(5.0)
		assertEquals(2.5, trace.v)
	}

	@Test fun opInt() {
		val trace = Trace(mv.op { d -> d.toInt() })
		var0.accept(5.5)
		assertEquals(5, trace.v)
	}
}

class LongTest : MVarTest<Long>(-2L, 2L, 3L, 4L, 7L, 10L, 20L, 100L)

class IntTest : MVarTest<Int>(-2, 2, 3, 4, 7, 10, 20, 100)
