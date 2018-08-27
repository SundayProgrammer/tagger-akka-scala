import actors.Supervisor
import actors.Supervisor.Categorize
import akka.actor.{ActorRef, ActorSystem}
import config.AppConstants
import helper.Categorization

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object Main extends App {
  val system: ActorSystem = ActorSystem("sentence-tagger")

  val categorization: Categorization = new Categorization()
  categorization.readRules(AppConstants.RULES_PATH)
  categorization.readSentences(AppConstants.SENTENCES_PATH)
  val sentences: ArrayBuffer[String] = categorization.getSentences

  final val taggerActor: ActorRef = system.actorOf(Supervisor.props(categorization.getRules), "supervisor")
  var num: Int = 0
  var sentence: String = ""
  val scope: Int = sentences.length

  for ( i <- 1 to 10 ) {
    num = Random.nextInt(scope)
    sentence = sentences(num)
    taggerActor ! Categorize(sentence)
  }
}
