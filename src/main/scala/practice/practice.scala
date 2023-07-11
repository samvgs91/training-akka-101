package practice

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object practice extends App{

  /**
   * 1) Counter actor
   * - Increment
   * - Decrement
   * - Print
   */

  val system = ActorSystem("practiceAkka")

//  case class Increment(value:Int)
//  case class Decrement(value:Int)


  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    import Counter._
    var counter = 0
    override def receive: Receive = {
      case Increment => counter+=1
      case Decrement => counter-=1
      case Print => println(s"[counter actor] counter is ${counter}")
    }
  }


  val counterActor = system.actorOf(Props[Counter],"counterActor")
  import Counter._

  (1 to 5).foreach(_ => counterActor ! Increment)
  (1 to 3).foreach(_ => counterActor ! Decrement)
  counterActor ! Print
//  counterActor ! Increment
//  counterActor ! Print
//
//  counterActor ! Decrement
//  counterActor ! Print


  /**
   * 2) Bank Account
   *
   * //recieve
   * - Deposit
   * - Withdraw
   * - Statement
   *  //reply
   * -Success
   * -Failiure
   */


 // Bank Account
  object BankAccount {
    case class Deposit(amount:Int)
    case class Withdraw(amount:Int)
    case object Statement

    case class TransactionSuccess(message:String)
    case class TransactionFailure(message:String)
  }


  class BankAccount extends Actor {
     import BankAccount._
     var balance = 0

    override def receive: Receive = {
      case Deposit(amount) =>
        if (amount < 0) sender() ! TransactionFailure("invalid deposit amount")
        else {
          balance += amount
          sender() ! TransactionSuccess(s"succesfully deposited $amount")
        }
      case Withdraw(amount) =>
        if (amount < 0) sender() ! TransactionFailure("invalid withdraw amount")
        else if (amount > balance ) sender() ! TransactionFailure("insufficient balance")
        else {
          balance-=amount
          sender() ! TransactionSuccess(s"Successfully withdrew $amount")
        }
      case Statement => println(s"Your balance is $balance")
    }
  }



  object Person {
     case class LiveTheFile(account: ActorRef)
  }

  class Person extends Actor {
    import Person._
    import BankAccount._
    override def receive: Receive = {
      case LiveTheFile(account) =>
        account ! Deposit(100000)
        account ! Withdraw(9000000)
        account ! Withdraw(3000)
        account ! Statement
      case message=> println(message.toString)
    }
  }

  import Person._

  val bankAccount = system.actorOf(Props[BankAccount],"bankAccount")
  val person = system.actorOf(Props[Person],"richPerson")

  person ! LiveTheFile(bankAccount)



}
