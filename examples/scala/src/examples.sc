val x = 5

def y = 5

x + y

def fn1(x: Any) = println(x)
fn1(x + y)

def fn2(x: Any) = {
  println("uh, ich bin ein echter Codeblock")
  println(x)
}
fn2(4)

def fac1(i: Int): Int =
  if (i < 2) 1
  else i * fac1(i-1)

fac1(30)


def fac2(i: Int): Int = {
  def h(i: Int, j: Int): Int =
    if (i < 2) j
    else h(i-1, i * j)
  h(i, 1)
}

fac2(30)

def fib(i: Int): Int =
  if (i < 2) i
  else fib(i-1) + fib(i-2)


def insert(xs: List[Int], x: Int): List[Int] = xs match {
  // if the list was empty, return a new list with just x
  case Nil => List(x)
  // otherwise: cut off the first element of xs and ...
  case y :: ys =>
    if (x < y) x :: xs       // prepend x to xs
    else y :: insert(ys, x)  // insert x into ys
}

def isort(xs: List[Int]): List[Int] = xs match {
  // an empty list is sorted
  case Nil => Nil
  // a list with a single element is also sorted
  case List(x) => List(x)
  // otherwise, cut off the first element (y) and
  // insert it into the sorted remaining list (ys)
  case y :: ys => insert(isort(ys), y)
}



