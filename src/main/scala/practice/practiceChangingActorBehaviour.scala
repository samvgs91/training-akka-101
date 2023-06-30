package practice

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object practiceChangingActorBehaviour extends App{

  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    import Counter._
    override def receive: Receive = counterReceive(0)

    def counterReceive(currentCount:Int):Receive = {
      case Increment =>
        println(s"[$currentCount] incrementing")
        context.become(counterReceive(currentCount+1))
      case Decrement =>
        println(s"[$currentCount] decrementing")
        context.become(counterReceive(currentCount-1))
      case Print => println(s"[stateless counter]: count is $currentCount")
    }
  }

  import Counter._

  val system = ActorSystem("practiceStatelessCounter")

  val statelessCounter = system.actorOf(Props[Counter],"statelessCounter")

  (1 to 10).foreach(_ => statelessCounter ! Increment)
  (1 to 3).foreach(_ => statelessCounter ! Decrement)
  statelessCounter ! Print



  /// Vote Practice

  case class Vote(Candidate:String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])

  class Citizen extends Actor {
    var candidate: Option[String] = None

    override def receive: Receive = {
      case Vote(c) =>  context.become(voted(c))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(c:String): Receive ={
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(c))
    }
  }

  case class AggregateVotes(citizen: Set[ActorRef])
  class VoteAggregator extends Actor {
//    var stillWaiting: Set[ActorRef] = Set()
//    var currentStats: Map[String, Int] = Map()

    override def receive: Receive = awaitingCommand

    def awaitingCommand: Receive = {
      case AggregateVotes(citizens) =>
        citizens.foreach(citizenRef => citizenRef ! VoteStatusRequest)
        context.become(awaitingStatuses(citizens,Map()))
    }

    def awaitingStatuses(stillWaiting:Set[ActorRef],currentStats:Map[String,Int]):Receive = {
      case VoteStatusReply(None) =>
        // a citizen hasn't voted yet.
        sender() ! VoteStatusRequest // this might end up in an infinite loop.
      case VoteStatusReply(Some(candidate)) =>
        // citizen has voted!
        val newStillWaiting = stillWaiting - sender()
        val currentVoteOfCandidate = currentStats.getOrElse(candidate, 0)
        val newStats = currentStats + (candidate -> (currentVoteOfCandidate + 1))

        if (newStillWaiting.isEmpty) {
          println(s"[aggregator] poll stats: $newStats")
        }
        else {
          context.become(awaitingStatuses(newStillWaiting,newStats))
        }
    }
  }

    val alice = system.actorOf(Props[Citizen],"alice")
    val bob = system.actorOf(Props[Citizen],"bob")
    val charlie = system.actorOf(Props[Citizen],"charlie")
    val daniel = system.actorOf(Props[Citizen],"daniel")

    alice ! Vote("Martin")
    bob ! Vote("Jonas")
    charlie ! Vote("Roland")
    daniel ! Vote("Daniel")

    val voteAggregator = system.actorOf(Props[VoteAggregator])
    voteAggregator ! AggregateVotes(Set(alice,bob,charlie,daniel))


}

