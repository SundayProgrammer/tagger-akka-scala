package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.RoundRobinPool
import helper.Rule

import scala.collection.mutable.ArrayBuffer

object WrapperActor {
  def props(rules: ArrayBuffer[Rule]): Props = Props(new WrapperActor(rules))
  case class ExtractWords(var sentence: String)
}

class WrapperActor(val rules: ArrayBuffer[Rule]) extends Actor {
  import WrapperActor._

  var words: ArrayBuffer[String] = ArrayBuffer[String]()

  val router: ActorRef = context.actorOf(new RoundRobinPool(4).props(
    routeeProps = Props(new AggregatorActor(rules))))

  override def receive: Receive = {
    case x @ ExtractWords(sentence) => {
      words.clear()
      for (word <- x.sentence.split("\\s")) {
        words += word
      }
      router.tell(new AggregatorActor.Tag(words.toList), ActorRef.noSender)
    }
  }
}
