package part2Actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App{

    //part1 - actor systems
    val actorSystem = ActorSystem("firstActorSystem")
    println(actorSystem.name)

    //part2 - create an actor
    //word count actor

    class WordCountActor extends Actor {
      //data stored
      var totalWords = 0

      def receive: PartialFunction[Any,Unit] = {
        case message: String =>
          println(s"[word counter] message received: ${message} ")
          totalWords += message.split("").length
          println(s"[word counter] Word count till now is ${totalWords} ")
        case msg => println(s"[word counter] I cannot understand ${msg.toString}")
      }
    }


    //part3 - instantiate our actor
    val wordCounter = actorSystem.actorOf(Props[WordCountActor],"wordCounter")
    val anotherCounter = actorSystem.actorOf(Props[WordCountActor],"anotherWordCounter")

    //part4 - send messages to actor
    wordCounter ! "I'm learning akka and it's awesome!"
    anotherCounter ! "Different word counter"
    //all messages to actors are asyncronous!

    //can't create actor with as "new"
    // new  WordCountActor will fail if run it!

    //the correct way to instantiate a class on actors is with a companion object
    object Person {
      def props(name: String) = Props(new Person(name))
    }

    class Person(name:String) extends Actor {
      override def receive: Receive = {
        case "hi" => println(s"Hi, my name is ${name}")
        case _ =>
      }
    }

    val person = actorSystem.actorOf(Person.props("Sam"))
    person ! "hi"

}
