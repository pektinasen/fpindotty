package lists

@scala.annotation.tailrec
def foldLeft[A, B](z: B)(f: (B, A) => B)(xs: List[A]): B = 
  xs match 
    case Nil => z
    case a :: as => foldLeft(f(z, a))(f)(as)