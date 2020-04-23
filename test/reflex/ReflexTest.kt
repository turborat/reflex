package reflex

import org.junit.Assert.assertEquals
import org.junit.Test

class ReflexTest {
	val r = Reflex()
	val v1 = r.newVar<Int>()

	@Test fun dep1() {
		val trace1 = Trace(v1)

		v1.accept(5)
		assertEquals(1, trace1.reflexions())
		assertEquals(5, trace1.v)

		v1.accept(6)
		assertEquals(2, trace1.reflexions())
		assertEquals(6, trace1.v)
	}

	@Test fun dep2() {
		val trace1 = Trace(v1)
		val trace2 = Trace(v1)

		v1.accept(5)
		assertEquals(1, trace1.reflexions())
		assertEquals(5, trace1.v)
		assertEquals(1, trace2.reflexions())
		assertEquals(5, trace2.v)
	}

	@Test fun dep2deep() {
		val v2 = r.newVar<Int>()
		val trace1 = Trace(v1)
		val trace2 = Trace(v2)
		v1.bind { v -> v2.accept(v) }

		v1.accept(5)
		assertEquals(1, trace1.reflexions())
		assertEquals(5, trace1.v)
		assertEquals(1, trace2.reflexions())
		assertEquals(5, trace2.v)
	}

	@Test fun cycleCount() {
		assertEquals(0, r.cycle)

		v1.accept(3)
		assertEquals(1, r.cycle)

		v1.accept(4)
		assertEquals(2, r.cycle)
	}

	@Test fun reflectionsHorizontal() {
		assertEquals(0, v1.accept(3))

		v1.bind(System.out::println)

		assertEquals(1, v1.accept(4))

		v1.bind(System.out::println)
		v1.bind(System.out::println)
		v1.bind(System.out::println)

		assertEquals(4, v1.accept(4))
		assertEquals(4, v1.accept(4))
	}

	@Test fun reflectionsVertical() {
		val v2 = r.newVar<Int>()
		val v3 = r.newVar<Int>()
		val v4 = r.newVar<Int>()

		assertEquals(0, v1.accept(3))

		v1.bind { v -> v2.accept(v) }
		v2.bind { v -> v3.accept(v) }
		v3.bind { v -> v4.accept(v) }

		assertEquals(3, v1.accept(4))

		v1.bind(System.out::println)

		assertEquals(4, v1.accept(4))
		assertEquals(4, v1.accept(4))
	}

	@Test fun old() {
		val capture = ArrayList<String>()
		val v0 = r.newVar<Int>()

		val v1 = FVar(v0)
		v1.bind(trace(capture, "v1"))

		val v2 = v1.map { x -> x * x }
		v2.bind(trace(capture, "v2"))

		val v3 = v2.map { x -> x * x }
		v2.bind(trace(capture, "v3"))

		val v4 = v3.map { x -> x * x }
		v4.bind(trace(capture, "v4"))

		val v2b = v1.map { x -> x * x }
		v2b.bind(trace(capture, "v2b"))

		val v3b = v2b.map { x -> x * x }
		v3b.bind(trace(capture, "v3b"))

		val v4b = v3b.map { x -> x * x }
		v4b.bind(trace(capture, "v4b"))

		v0.accept(1)
		v0.accept(2)

		assertEquals(
			"[1]v1:=1\n" +
            "[1]v2:=1\n" +
            "[1]v3:=1\n" +
            "[1]v2b:=1\n" +
            "[1]v3b:=1\n" +
            "[1]v4:=1\n" +
            "[1]v4b:=1\n" +
            "[2]v1:=2\n" +
            "[2]v2:=4\n" +
            "[2]v3:=4\n" +
            "[2]v2b:=4\n" +
            "[2]v3b:=16\n" +
            "[2]v4:=256\n" +
            "[2]v4b:=256", java.lang.String.join("\n", capture))
	}

	@Test fun propagation1() {
		val v2 = FVar(v1).map { v -> v }
		val v3 = FVar(v2).map { v -> v }
		val v4 = FVar(v3).map { v -> v }

		val trace = Trace<Int>()
		trace.bind("v1", v1)
		trace.bind("v2", v2)
		trace.bind("v3", v3)
		trace.bind("v4", v4)

		assertEquals("[]", trace.toString())

		v1.accept(1)
		assertEquals("[v1:1=1, v2:1=1, v3:1=1, v4:1=1]", trace.toString())

		v1.accept(2)
		assertEquals("[v1:1=1, v2:1=1, v3:1=1, v4:1=1," +
				            " v1:2=2, v2:2=2, v3:2=2, v4:2=2]", trace.toString())
	}

	@Test fun propagation2() {
		val v2 = FVar(v1).map { v -> v }
		val v3 = FVar(v1).map { v -> v }
		val v4 = FVar(v3).map { v -> v }

		val trace = Trace<Int>()
		trace.bind("v1", v1)
		trace.bind("v2", v2)
		trace.bind("v3", v3)
		trace.bind("v4", v4)

		assertEquals("[]", trace.toString())

		v1.accept(1)
		assertEquals("[v1:1=1, v2:1=1, v3:1=1, v4:1=1]", trace.toString())

		v1.accept(2)
		assertEquals("[v1:1=1, v2:1=1, v3:1=1, v4:1=1," +
				            " v1:2=2, v2:2=2, v3:2=2, v4:2=2]", trace.toString())
	}

	@Test fun propagation3() {
		val v2 = FVar(v1).map { v -> v }
		val v3 = FVar(v2).map { v -> v }
		val v4 = FVar(v1).map { v -> v }

		val trace = Trace<Int>()
		trace.bind("v1", v1)
		trace.bind("v2", v2)
		trace.bind("v3", v3)
		trace.bind("v4", v4)

		assertEquals("[]", trace.toString())

		v1.accept(1)
		assertEquals("[v1:1=1, v2:1=1, v4:1=1, v3:1=1]", trace.toString())

		v1.accept(2)
		assertEquals("[v1:1=1, v2:1=1, v4:1=1, v3:1=1," +
				            " v1:2=2, v2:2=2, v4:2=2, v3:2=2]", trace.toString())
	}

	@Test fun propagation3x() {
		val v2 = FVar(v1).map { _ -> throw Exception("ach!")}
		val v3 = FVar(v2).map { v -> v }
		val v4 = FVar(v1).map { v -> v }

		val trace = Trace<Int>()
		trace.bind("v1", v1)
		trace.bind("v2", v2)
		trace.bind("v3", v3)
		trace.bind("v4", v4)

		assertEquals("[]", trace.toString())

		v1.accept(1)
		assertEquals("[v1:1=1, v4:1=1]", trace.toString())
		assertEquals(2, trace.reflexions())

		v1.accept(2)
		assertEquals("[v1:1=1, v4:1=1, v1:2=2, v4:2=2]", trace.toString())
		assertEquals(4, trace.reflexions())
	}

	fun trace(ll: MutableList<String>, id: String): (Int) -> Unit {
		return { v -> ll.add(String.format("[%d]%s:=%s", r.cycle, id, v)) }
	}
}

