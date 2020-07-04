import lists._
import state._
import state.rng._

object testing {


    /*
    // Execise 8.1
    // reversing a list and summing it should give same result
    // summing a list with same values should be: list.length * value
    // summing list of incrementing is : (l[0] + l[-1]) * n / 2
    // summing an empty list is ... 0 (?)
    */

    def sum: List[Int] => Int = foldLeft(0)(_ + _)

    // Exervise 8.2
    // What properties specify a function that find the maximum of a list
    // max of an empty list is an error
    // max of single element list is that element
    // max of the list is element of that list
    def max: List[Int] => Int = foldLeft(Integer.MIN_VALUE)((b, a) => if a > b then a else b)


    def forAll[A](a: Gen[A])(f: A => Boolean): Prop = ???


    trait Prop_v1:  
        def check: Boolean

        def &&(p: Prop_v1): Prop_v1 = new Prop_v1: 
            def check : Boolean = this.check && p.check
        
    
    trait Prop:

        def check: Either[(FailedCase, SuccessCount), SuccessCount]

        def &&(p: Prop): Prop = ???

    
    type FailedCase = String
    type SuccessCount = Int

    case class Gen[A](sample: State[RNG, A]):

        def map[B](f: A => B) : Gen[B] = Gen(sample.map(f))

        def flatMap[B](f: A => Gen[B]): Gen[B] = Gen(State(
            rng => 
                val (a, r1) = sample.run(rng)
                f(a).sample.run(r1)
        ))

        def listOfN(size: Gen[Int]): Gen[List[A]] = size.flatMap( n =>
            Gen.listOfN(n, Gen(sample))    
        )

    object Gen:
        
        def choose(start: Int, stopExclusive: Int): Gen[Int] = 
            Gen(State[RNG, Int](_.nextInt).map(i =>
                if (i < start)
                    i +  stopExclusive
                else if (i >= stopExclusive)
                    i - start
                else
                    i
                    
            ))
        
        def unit[A](a: => A): Gen[A] =
            Gen(State.unit(a))

        def boolean: Gen[Boolean] = Gen(State[RNG, Int](_.nextInt).map(_ < 0))

        def listOfN[A](n: Int, g: Gen[A]): Gen[List[A]] = 
            Gen(State.sequence(List.fill(n)(g.sample)))

        def both[A,B](ga: Gen[A], gb: Gen[B]): Gen[(A,B)] = Gen(State.both(ga.sample,gb.sample))
        
        def string: Gen[String] = listOfN(5, Gen.choose(32, 127)).map(_.map(_.toChar).mkString)

        def intPair(start: Int, stopExclusive: Int): Gen[(Int, Int)] =
            val a = Gen.choose(start, stopExclusive)
            val b = Gen.choose(start, stopExclusive)
            Gen.both(a, b)

        def union[A](ga: Gen[A], gb: Gen[A]): Gen[A] = 
            boolean.flatMap( b => if b then ga else gb)

        // def union[A](ga: Gen[A], gb: Gen[A]): Gen[A] = Gen(State(
        //     rng =>
        //         val (i, r) = rng.nextInt
        //         if i < 0
        //           ga.sample.run(r)
        //         else
        //           gb.sample.run(r)
        // ))

        // def weighted[A](ga: (Gen[A], Double), gb: (Gen[A], Double)) = (ga, gb) match 
        //     case ((genA, da), (genB, db)) => 
        //         rng =>
                    

    end Gen


    enum Result:
        case Passed
        case Falsified(failure: FailedCase, success: SuccessCount)

              
}
