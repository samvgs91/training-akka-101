package part1Recap

import scala.concurrent.Future

object AdvancedRecap extends App {

    //partial functions
    val partialFunction : PartialFunction[Int, Int] = {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

   // same partial function but other syntax
    val pf = (x: Int) => x match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

  // partial extensions are extension of normal functions...
   val function: (Int => Int) = partialFunction

  // partial functions in HOF
   val modifiedList = List(1,2,3).map {
     case 1 => 42
     case _ => 0
   }

   // lifting
  val lifted = partialFunction.lift // total function Int => Option[Int]
  lifted(2) // Some(65)
  lifted(65) // None -> because partial order function as no defined for that but with lift will do Option None value.

  //orElse
  val pfChain = partialFunction.orElse[Int,Int] {
    case 60 => 90000
  }

  pfChain(5) // will return 999  cause of the partial function
  pfChain(60) // will return 90000 cause of the orElse

  //but..
  pfChain(587) // will throw a match error cause is not handle anywhere.

  //type Aliases
  type ReceiveFunction = PartialFunction[Any,Unit] // now we can use this new Type as alias for that new defined PartialFunction.

  def receive: ReceiveFunction = {
    case 1 => println("something")
    case _ => println("something else")
  }

  // implicits
  // on implicits if we define a parameter as an implicit compiler will pass the implicit parameter for us.

  implicit val timeout = 3000
  def setTimeout(f: ()=>Unit)(implicit timeout:Int) = f()

  setTimeout(() => println("timeout"))// extra parameter list omitted


  // implicit conversions

  // 1) implicit defs
  case class Person(name:String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(string:String):Person = Person(string)

  "Peter".greet
  //fromStringToPerson("Peter").greet - automatically done by the compiler.


  // 2) implicit classes

  implicit class Dog(name:String) {
    def bark = println("bark!")
  }
  // new Dog("Lassie").bark - automatically done by the compiler.

  //organize implicits

  //local scope: where the method is called the implicit is being passed.
  implicit val inverseOrdering: Ordering[Int] = Ordering.fromLessThan(_>_)
  List(1,2,3).sorted//the default

  // imported scope
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future {
    println("Hello, future")
  }


  // companion objects of the types included in the call

  object Person {
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a,b) => a.name.compareTo(b.name)<0)
  }

  List(Person("Bob"),Person("Alice")).sorted /// List(Person("Alice),Person("Bob"))





}
