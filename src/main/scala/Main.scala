import state._
import state.rng._
object Main extends App {
    println("-" * 50)

    val rng1 = SimpleRandom(1234L)
    val (n, rng2) = rng1.nextInt
    val (n2, _) = rng2.nextInt
    println(n)
    println(n2)
    // // val normalInts = _ints(10)(rng)
    val sequenceInts = ints(10).run(rng1)
    // println(normalInts)
    println(sequenceInts)
    println("double:" + double.run(rng1))
    println("int: " + int.run(rng1))
    println(intDouble.run(rng1))
    // println(_nonNegativeLessThan(500)(rng))
    println(nonNegativeLessThan(6).run(rng1))

    val xx = for {
        x <- nonNegativeLessThan(10)
        y <- int
        xs <- ints(x)
    } yield xs.map(_ % y)

    println(xx.run(rng1))
    // for {
    //   (n, rng2) <- rng.nextInt
    // }
    println("-" * 50)
}