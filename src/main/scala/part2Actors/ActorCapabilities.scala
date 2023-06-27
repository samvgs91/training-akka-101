package part2Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities  extends  App {
  class SimpleActor extends Actor {
    override def receive: Receive = {
          // when we send message we send ourself too sender contains the actuall context.sender() or sender.
      case "Hi!" => context.sender() ! "Hello there!"
      case message: String => println(s"[${self}] message received: ${message} ")
      case number: Int => println(s"[simple actor] I have received a number: ${number} ")
      case SpecialMessage(msg) => println(s"[simple actor] I have received something SPECIAL: ${msg}")
      case SendMessageToYourSelf(content) => self ! content
      case SayHiTo(ref) => ref ! "Hi!"
      case WirelessPhoneMessage(content,ref) => ref forward content+"s" // I keep the original sender of the wireless
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor],"simpleActor")

  simpleActor ! "hello, actor"

  // 1 - messages can be any type like classes of defined by ourself
  // we can send everything with some conditions:
  // a) messages must be INMUTABLE.
  // b) messages must be SERIALIZABLE. JVM Transformed into byteStream and send it to antoher machine or the network.
  // In practice we use case classes and case objects for INMUTABILITY and SERIALIZABLE.
  simpleActor ! 42

  case class SpecialMessage(contents:String)
  simpleActor ! SpecialMessage("some special content")

  // 2 - actors have information about their context and about themself
  // context.self === 'this' in OOP

  case class SendMessageToYourSelf(content:String)

  simpleActor ! SendMessageToYourSelf("I'm a proud Actor!")


  // 3 - actors can REPLY to messages
  val alice = system.actorOf(Props[SimpleActor],"alice")
  val bob = system.actorOf(Props[SimpleActor],"bob")

  case class SayHiTo(ref: ActorRef)

  alice ! SayHiTo(bob)

  // 4 - DEAD LETTERS

  alice ! "Hi!" // when sender is null -> this should show INFO message about the dead letter handler.

  // 5 - forwarding messages
  // D -> A -> B
  // forwarding = message with ORIGINAL sender
  case class WirelessPhoneMessage(content:String, ref: ActorRef)

  alice ! WirelessPhoneMessage("Hi", bob)

}
