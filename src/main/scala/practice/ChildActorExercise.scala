package practice

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorExercise extends App{


  object WordCounterMaster {
    case class Initialize(nChildren: Int)

    case class WordCountTask(id:Int, text: String)

    case class WordCountReply(id:Int, wordCount: Int)
  }

  class WordCounterMaster extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case Initialize(n) =>
        println("[master] initializing...")
        val childenRefs = for (i <- 1 to n)
          yield context.actorOf(Props[WordCounterWorker], s"wcw_${i}")
        context.become(withChildren(childenRefs,0,0,Map()))
    }

    def withChildren(childrenRefs: IndexedSeq[ActorRef], currentChildIndex:Int, currentTaskId:Int, requestMap:Map[Int,ActorRef]): Receive = {
      case text:String =>
        println(s"[master] I have received: $text - I will send it to child $currentChildIndex")
        val originalSender = sender()
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task // task sent to worker 0.
        val nextChildIndex = (currentChildIndex+1) % childrenRefs.length
        val newTaskId = currentTaskId+1
        val newRequestMap = requestMap + (currentTaskId -> originalSender)
        context.become(withChildren(childrenRefs,nextChildIndex,newTaskId,newRequestMap)) // task sent to NEXT worker.
      case WordCountReply(id,count) =>
        println(s"[master] I have received a reply for task id $id with $count")
        val originalSender = requestMap(id)
        originalSender ! count
        context.become(withChildren(childrenRefs, currentChildIndex, currentTaskId, requestMap-id))
    }

  }

  class WordCounterWorker extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(id,text) =>
        println(s"[${self.path}] I have received task $id with $text")
       sender() ! WordCountReply(id,text.split(" ").length)
    }
  }

  class TestActor extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case "go" =>
         val master = context.actorOf(Props[WordCounterMaster],"master")
         master ! Initialize(3)
        val texts = List("I love Akka","Scala is super cool"," Some Scala for Akka")
        texts.foreach(text => master ! text)
      case count: Int =>
        println(s"[test actor] I receive a reply: $count")
    }
  }

  val system = ActorSystem("roundRobinWordCountExpress")
  val testActor = system.actorOf(Props[TestActor],"testActor")
  testActor ! "go"
}
