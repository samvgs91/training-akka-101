package part2Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends App{

    class SimpleLoggingActor extends Actor with ActorLogging {
      override def receive: Receive = {
        case message => log.info(message.toString)
      }
    }

    /**
     * 1 - inline configuration
    **/

    val configString =
     """
      | akka {
      |    loglevel = "ERROR"
      | }
      |""".stripMargin

//    val configString =
//      """
//        | akka {
//        |    loglevel = "DEBUG"
//        | }
//        |""".stripMargin

    val config = ConfigFactory.parseString(configString)
    val system = ActorSystem("ConfigurationDemo",ConfigFactory.load(config))

    val actor = system.actorOf(Props[SimpleLoggingActor])
    actor ! "some message"

    /**
     * 2 - default file configuration
     * When we create a ActorSystem without any configuration all together. It will look for main/resources/application.conf file.
     **/

  val defaultConfigFileSystem = ActorSystem("DefaultFileSystem")
  val defaultActor = defaultConfigFileSystem.actorOf(Props[SimpleLoggingActor])
  defaultActor ! "default message test"

  /**
   * 3 - Separate configuration in the same file
   **/
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialActorSystem = ActorSystem("SpecialConfigDemo",specialConfig)
  val specialConfigActor = specialActorSystem.actorOf(Props[SimpleLoggingActor])

  specialConfigActor ! "I'm an special message"

  /**
   * 4 - Separate configuration in the another file
   **/

  val anotherFileConfig = ConfigFactory.load("secretFolder/secretConfiguration.conf")
  println(s"separated config log level: ${anotherFileConfig.getString("akka.loglevel")}")
//  val anotherFileActorSystem = ActorSystem("SpecialConfigDemo",anotherFileConfig)
//  val anotherFileConfigActor = anotherFileActorSystem.actorOf(Props[SimpleLoggingActor])
//
//  anotherFileConfigActor ! "I'm an message using another config file"

  /**
   * 5 - different file formats
   * JSON, Properties
   **/

  val jsonConfig = ConfigFactory.load("json/jsonConfig.json")
  println(s"json config: ${jsonConfig.getString("aJsonProperty")}")
  println(s"json config: ${jsonConfig.getString("akka.loglevel")}")

  //Properties configuration does not support nested configuration you must specify properties correctly. fully qualify name like akka.loglevel
  val propsConfig = ConfigFactory.load("props/propsConfiguration.properties")
  println(s"props config: ${propsConfig.getString("akka.loglevel")}")
}
