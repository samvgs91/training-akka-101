package part1Recap

import scala.concurrent.Future

object MultithreadingRecap extends App{

    // creating a thread on the JVM
    val aThread = new Thread(() => println("I'm running in parallel"))
    aThread.start()
    aThread.join()

    val threadHello = new Thread(() => (1 to 100).foreach(_ => println("hello")))
    val threadGoodbye = new Thread(() => (1 to 100).foreach(_ => println("good bye")))

//    threadHello.start()
//    threadGoodbye.start()

    // different runs produce different resutls

    //volatile only works for integer
    class BankAccount(@volatile private var amount: Int) {
      override def toString: String = ""+amount
      def withDraw(money:Int): Unit =
        this.synchronized
      {
        this.amount -= money
      }
    }

    val aBankAccount = new BankAccount(10000)

    println( aBankAccount.toString() )


    val opeT1 = new Thread ( () =>
       {
         println( "Transaction T1")
         aBankAccount.withDraw(2000)
         println("Transaction T1 after withDraw:" + aBankAccount.toString() )
       }
    )

    val opeT2 = new Thread ( () =>
      {
        println( "Transaction T2")
        aBankAccount.withDraw(5000)
        println( "Transaction T2 after withDraw:" + aBankAccount.toString() )
      }
    )

    val opeT3 = new Thread ( () =>
      {
        println( "Transaction T3")
        aBankAccount.withDraw(1000)
        println( "Transaction T3 after withDraw:" + aBankAccount.toString() )
      }
    )

    opeT1.start()
    opeT2.start()
    opeT3.start()


    import scala.concurrent.ExecutionContext.Implicits.global
    val future = Future {
      42
    }

    val aProcessedFuture = future.map(_ + 1) //future with 43
    val aFlatFuture = future.flatMap {
      value =>
        Future(value + 2)
    }
}

