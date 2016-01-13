package com.agilogy.json

import play.api.libs.json._

/** This class represents a change in an optional value.
  * We can't use simply Option[T] because None could represent not changing the value or setting it to None
  * This class allows for 3 distinct values:
  * - SetSome(v): We want to change the attribute by setting a new value v
  * - SetNone: We want to change the attribute by removing any value it may have
  * - LeaveUnchanged: We don't want to change the attribute
  *
  * Unfortunately, when using this type for attributes of a case class, the json format/reads/writes must be
  * defined manually. See updateProductRequestFmt for an example of such definition.
  */
sealed trait OptionChange[+A] extends Product with Serializable {
  self =>

  def isRemoval: Boolean

  def isChange: Boolean

  def isUnchanged: Boolean = !isChange

  @inline final def withDefault[B >: A](default: => OptionChange[B]): OptionChange[B] =
    if (isUnchanged) default else this

  @inline final def getResult[B >: A](currentValue: => Option[B]): Option[B] = this match {
    case LeaveUnchanged => currentValue
    case SetSome(nv) => Some(nv)
    case SetNone => None
  }

  @inline final def map[B](f:A => B): OptionChange[B] = this match{
    case SetSome(a) => SetSome(f(a))
    case SetNone => OptionChange.remove[B]
    case LeaveUnchanged => OptionChange.unchanged[B]
  }

  @inline final def mapChange[B](f: Option[A] => B):Option[B] = this match {
    case SetSome(v) => Some(f(Some(v)))
    case SetNone => Some(f(None))
    case LeaveUnchanged => None
  }
}

final case class SetSome[+A](x: A) extends OptionChange[A] {
  require(x != null)

  def isRemoval: Boolean = false

  def isChange: Boolean = true

}

case object LeaveUnchanged extends OptionChange[Nothing] {

  def isRemoval: Boolean = true

  def isChange: Boolean = false

}

case object SetNone extends OptionChange[Nothing] {

  def isRemoval: Boolean = true

  def isChange: Boolean = true

}

object OptionChangeFormat {

  implicit def optionChangeWrites[T](path: JsPath)(implicit writes: Writes[T]): OWrites[OptionChange[T]] = OWrites[OptionChange[T]] {
    case SetSome(t) => JsPath.createObj(path -> writes.writes(t))
    case SetNone => JsPath.createObj(path -> JsNull)
    case LeaveUnchanged => Json.obj()
  }

  implicit def optionChangeReads[T](path: JsPath)(implicit reads: Reads[T]): Reads[OptionChange[T]] = Reads[OptionChange[T]] {
    json =>
      path.applyTillLast(json).fold(
        jserr => jserr,
        jsres => jsres.fold(
          _ => JsSuccess(LeaveUnchanged),
          {
            case JsNull => JsSuccess(SetNone)
            case js => reads.reads(js).repath(path).map(SetSome(_))
          }
        )
      )
  }

  implicit def optionChangeFormat[A](path: JsPath)(implicit f: Format[A]): OFormat[OptionChange[A]] =
    OFormat(optionChangeReads(path)(f), optionChangeWrites(path)(f))

  implicit class OptionChangePathOps(path: JsPath) {

    def formatOptionChange[T](implicit f: Format[T]): OFormat[OptionChange[T]] = optionChangeFormat(path)(f)

    def writeOptionChange[T](implicit writes: Writes[T]): OWrites[OptionChange[T]] = optionChangeWrites(path)(writes)

    def readOptionChange[T](implicit reads: Reads[T]): Reads[OptionChange[T]] = optionChangeReads(path)(reads)
  }

}

object OptionChange {

  import scala.language.implicitConversions

  //  def asOption[A](xo: OptionalValueChange[A]): Option[A] = xo match {
  //    case NewValue(x) => Some(x)
  //    case _ => None
  //  }
  //
  //  def asIterable[A](xo: OptionalValueChange[A]): Iterable[A] = asOption(xo).toList

  def changeTo[T](o: Option[T]): OptionChange[T] = o match {
    case None => SetNone
    case Some(t) => SetSome(t)
  }

  def apply[A](x: A): OptionChange[A] = if (x == null) SetNone else SetSome(x)

  def remove[A]: OptionChange[A] = SetNone

  def unchanged[A]: OptionChange[A] = LeaveUnchanged

}






