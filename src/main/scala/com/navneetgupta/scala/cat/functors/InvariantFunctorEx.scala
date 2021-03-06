package com.navneetgupta.scala.cat.functors

object InvariantFunctorEx extends App {

  /**
    * Invariant Functor Type classes represents building a bidirectional chain of operation.
    *
    * These type classes implement a method imap that informally equivalent to combination of map and contramap.
    *
    * If map generates new type class instances by appending a func􏰁on to a chain, and contramap generates them by
    * prepending an opera􏰁on to a chain, imap generates them via a pair of bidirectional transformations.
    *
    * The most intui􏰁ve examples of this are a type class that represents encoding and decoding as some data type,
    * such as Play JSON’s Format and scodec’s Codec.
    */

  trait Codec[A] {
    self =>

    def encode(value: A): String

    def decode(value: String): A

    def imap[B](dec: A => B, enc: B => A): Codec[B] = new Codec[B] {
      override def encode(value: B): String = self.encode(enc(value))

      override def decode(value: String): B = dec(self.decode(value))
    }
  }

  def encode[A](value: A)(implicit c: Codec[A]): String = c.encode(value)

  def decode[A](value: String)(implicit c: Codec[A]): A = c.decode(value)

  /**
    * If we have a Codec[A] and a pair of func􏰁ons A => B and B => A, the imap method creates a Codec[B]:
    */

  implicit val stringCodec: Codec[String] =
    new Codec[String] {
      override def encode(value: String): String = value

      override def decode(value: String): String = value
    }

  implicit val intCodec: Codec[Int] = stringCodec.imap(_.toInt, _.toString)

  implicit val booleanCodec: Codec[Boolean] = stringCodec.imap(a => if (a == "") false else true, a => if (a) "\"" + "true" + "\"" else "\"" + "false" + "\"")
  implicit val booleanCodec2: Codec[Boolean] = stringCodec.imap(_.toBoolean, _.toString)

  println(encode("2323"))
  println(encode("1"))
  println(encode("true"))
  println(encode("false"))

  implicit val doubleCode: Codec[Double] = stringCodec.imap(_.toDouble, _.toString)

  //  decode("false")
  //  decode("false")

  final case class Box[A](value: A)

  implicit def boxCodec[A](implicit codec: Codec[A]) = codec.imap[Box[A]](a => Box(a), b => b.value)

  println(encode(Box("2323")))
  println(encode(Box("1")))
  println(encode(Box("true")))
}
