1 + 1

val x = 5

def fib(a: Int, b: Int, n: Int): Int = {
  if (n == 0) a
  else if (n == 1) b
  else fib(b, a+b, n-1)
}

