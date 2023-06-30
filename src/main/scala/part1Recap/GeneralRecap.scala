package part1Recap

object GeneralRecap extends App{

  val aContition:Boolean = false

  var aVariable = 42
  aVariable+=2

  // expressions
  val aConditionedVal = if (aContition) 42 else 65

  //Types
  //Unit
  val TheUnit = println("Hello, Scala")

  def aFunction(x: Int): Int = x + 1
  // recursion - TAIL recursion

  def factorial(n: Int,acc: Int): Int =
    if (n<=0) acc
    else factorial(n - 1, acc * n)

  // OOP

  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog

  trait Carnivore {
    def eat(a:Animal):Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("Crunch!")
  }

  //method notation
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog

  //annonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("roar!")
  }

  aCarnivore eat aDog

  // generics
  abstract class MyList[+A]

  // companion objects
  object MyList


  // case classes
  case class Person(name:String, Age:Int)

  // exception
  val aPotentialFailure = try {
    throw new RuntimeException("I'm innocent!")
  } catch {
    case e: Exception => "I caught an Exception"
  } finally {
    //side effects
    println("some logs")
  }

  //functional programming

  val incrementer = new Function[Int,Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  val incremented = incrementer(42)
  //   incrementer.apply(42)

  // lambda syntax
  val anonymousIncrementer = (x: Int) => x + 1

  // Higher order function are the one the can call other functions
  // example map
  List(1,2,3).map(incrementer)


  // for comprehensions
  val pairs = for {
    num <- List(1,2,3,4)
    char <- List('a','b','c','d')
  } yield num + "-" + char

  // List(1,2,3,4).flatMap( num => List('a','b','c','d').map(char => num + "-" + char))

  // Seq, Array, List, Vector, Map, Tuples, Sets

  // "Collections"
  // Options and Try

  val anOption = Some(2)
//  val aTry = try {
//      throw new Exception
//  }

  // pattern matching
  val unknown = 2
  val order = unknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "unknown"
  }

  // pattern matching helps to extract values from complex structures.
  val bob = Person("Bob",22)
  val greeting = bob match {
    case Person(n,_) => s"Hi, my name is $n"
    case _ => "Probably is not a person."
  }

}
