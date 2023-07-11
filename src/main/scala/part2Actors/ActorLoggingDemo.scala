package part2Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLoggingDemo extends App{

   class SimpleActorWithExplicitLogger extends Actor {
     val logger = Logging(context.system, this)
     override def receive: Receive = {
       /* Usuall levels of logging
        1 - DEBUG
        2 - INFO
        3 - WARNING/WARN
        4 - ERROR
        */
       case message => logger.info(message.toString) // Log it
     }
   }

  val system = ActorSystem("LoggingDemo")
  val actor = system.actorOf(Props[SimpleActorWithExplicitLogger])

  actor ! "Simple demo message!"
  // #2 ActorLogging

  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case (a,b) => log.info("Two things {} and {} ",a , b) // Two things: 2 and 3
      case message => log.info(message.toString)
    }
  }
  val simpleActor = system.actorOf(Props[ActorWithLogging])
  simpleActor ! "Logging a simple message by extending a trait"
  simpleActor ! (42,60)

   // Notes: logging is async.
  // akka logging is done with actors.
  // we can change the logger, e.g. SLF4J.
}
