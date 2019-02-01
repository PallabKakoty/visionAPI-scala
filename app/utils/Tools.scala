package utils

class Tools {
  val random = new scala.util.Random
  def randomString(alphabet: String)(n: Int): String = Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString
  def TokenGenerateBySize(n: Int) = randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)
}