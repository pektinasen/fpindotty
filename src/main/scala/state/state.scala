package state

case class State[S, +A](run: S => (A, S)) with

  import State._

  def map[B](f: A => B): State[S, B] = 
    flatMap(a => unit(f(a)))
  
  def map2[B, C](rb: State[S, B])(f: (A,B) => C): State[S, C] =
    flatMap(a => rb.map(b => f(a,b)))

  def flatMap[B](f: A => State[S, B]): State[S, B] = State(s => {
    val (a, s1) = run(s)
    f(a).run(s1)
  })


object State
  def unit[S, A](a: A): State[S, A] = State((a, _))

  def both[S, A,B](ra: State[S, A], rb: State[S, B]): State[S, (A,B)] = 
    ra.map2(rb)((_,_))

  def sequence[S, A](fs: List[State[S, A]]): State[S, List[A]] = State(s => {
    fs match
      case Nil => (Nil, s)
      case r :: xs => 
        val ss = sequence(xs)
        r.map2(ss)(_ :: _).run(s)
  })

  def modify[S](f: S => S): State[S, Unit] = for {
    s <- get
    _ <- set(f(s))
  } yield ()

  def get[S]: State[S, S] = State(s => (s, s))
  def set[S](s: S) : State[S, Unit] = State(_ => ((),s))

object rng

  trait RNG
    def nextInt: (Int, RNG)

  case class SimpleRandom(seed: Long) extends RNG
    def nextInt: (Int, RNG) =
      val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFL
      val nextRNG = SimpleRandom(newSeed)
      val n = (newSeed >>> 16).toInt
      (n, nextRNG)


  def _ints(count: Int)(rng: RNG): (List[Int], RNG) = count match
    case 0 =>
      (List.empty, rng)
    case n =>
      val (i, r) = rng.nextInt
      val (l, rr) = _ints(n - 1)( r)
      (i :: l, rr)

  type Rand[A] = State[RNG, A]

  val int: Rand[Int] = State(_.nextInt)

  def ints(count: Int) : Rand[List[Int]] =
    State.sequence(List.fill(count)(int))

  def nonNegativeInt: Rand[Int] = State(rng => {
    val (n, newRng) = rng.nextInt
    if n < 0
      ((n+1) * -1, newRng)
    else
      (n, newRng)
  })

  def double: Rand[Double] =
    nonNegativeInt.map(_ / (Int.MaxValue.toDouble +1))

  val intDouble: Rand[(Int, Double)] =
    State.both(int, double)

  def nonNegativeEven : Rand[Int] = 
    nonNegativeInt.map(i => i - i % 2)

  def nonNegativeLessThan(n: Int): Rand[Int] =
    nonNegativeInt.flatMap { i => 
      val mod = i % n
      if i + (n-1) - mod >= 0 then
        State.unit(mod)
      else
        nonNegativeLessThan(n)
    }

object candy

  enum Input
    case Coin
    case Turn

  opaque type Candies = Int
  object Candies
    def apply(candies: Int): Candies = candies
  
  opaque type Coins = Int
  object Coins
    def apply(coins: Int): Coins = coins

  extension CoinsOps on (c: Coins)
    def toInt: Int = c
  

  case class Locked()
  case class Open()

  case class Machine(locked: Locked | Open, candies: Candies, coins: Coins)

  /**
  * RULES
  * 1. Inserting a coin into locked machine will cause it to unlock if theres any candy left.
  * 2. Turning the knob on an unlocked machine will cause it to dispense candy and become locked
  * 3. Tuning the knob on a loxked machine or inserting a coin into an unlocked machine does nothing
  * 4. A machine that's out of candy ignores all inputs
  */
  val update: Input => Machine => Machine = i => m => 
    (i, m) match
      case (Input.Coin, Machine(Locked(), ca, co)) if ca.toInt >= 1 => Machine(Open(), ca, co.toInt + 1) 
      case (Input.Turn, Machine(Open(), ca, co)) => Machine(Locked(), ca.toInt -1, co)
      case (Input.Turn, Machine(Locked(), _ , _ )) => m
      case (Input.Coin, Machine(Open(), _ , _ )) => m
      case (_, Machine(_, 0, _)) => m


  def simulateMachine(inputs: List[Input]): State[Machine, (Candies, Coins)] = for 
    _ <- State.sequence(inputs map (State.modify[Machine] _ compose update))
    m <- State.get
  yield (m.candies, m.coins) 


  // old implementation

  
  // def _nonNegativeLessThan(n: Int): Rand[Int] = rng =>
  //   val (i, rng2) = nonNegativeInt(rng)
  //   val mod = i % n
  //   if i + (n-1) - mod >= 0 then
  //     (mod, rng2)
  //   else
  //     _nonNegativeLessThan(n)(rng2)


  // def _map[A,B](r: Rand[A])(f: A => B): Rand[B] = rng =>
  //   val (a, rr) = r(rng)
  //   (f(a), rr)

  // def map2[A, B, C](ra: Rand[A], rb: Rand[B])(f: (A,B) => C): Rand[C] =
  //   rng => 
  //     val (a, r1) = ra(rng)
  //     val (b, r2) = rb(r1)
  //     (f(a,b), r2)
