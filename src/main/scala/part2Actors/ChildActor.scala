package part2Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActor extends App{

    // Actors can create other actors

    object Parent {
      case class CreateChild(name:String)
      case class TellChild(message:String)
    }
    class Parent extends Actor {
      import Parent._

      override def receive: Receive = {
        case CreateChild(name) =>
          println(s"${self.path} creating name") //to identify in which actor is happening
          val childRef = context.actorOf(Props[Child],name)
          context.become(withChild(childRef))
      }
      def withChild(ref: ActorRef):Receive = {
        case TellChild(message) => ref forward message
      }
    }

    class Child extends Actor {
      override def receive: Receive = {
        case message => println(s"${self.path} I got: $message")
      }
    }

    import Parent._
    val system = ActorSystem("someSystem")
    val parent = system.actorOf(Props[Parent],"MeAsParent")

    parent ! CreateChild("firstKid")
    parent ! TellChild("Hey Kid!")

  // Actor Hierarchies
  // parent -> child 1 -> grandChild
  //        -> child 2 ->

  /*
    Guardian actors (top-level)
    - /system = system guardian
    - /user = user-level guardian
    - / = the root guardian
   */
  /** Actor Selection */

  val childSelection = system.actorSelection("/user/MeAsParent/firstKid")
  //this works well with "context"
  childSelection ! "I found you!"


  /*
  NEVER!! PASS ACTOR MUTABLE STATE, OR THE 'THIS' REFERENCE, TO CHILD ACTORS.
   */
}
