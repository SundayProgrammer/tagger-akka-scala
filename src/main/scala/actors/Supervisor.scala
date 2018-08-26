package actors

import actors.WrapperActor.ExtractWords
import akka.actor.{Actor, ActorRef, Props}
import akka.routing.{RoundRobinPool}
import helper.Rule

import scala.collection.mutable.ArrayBuffer

object Supervisor {

  def props(rules: ArrayBuffer[Rule]): Props = Props(new Supervisor(rules))
  case class Categorize(var message: String)

}

class Supervisor(val rules: ArrayBuffer[Rule]) extends Actor {
  import Supervisor._

  val router: ActorRef = context.actorOf(new RoundRobinPool(2).props(
    routeeProps = Props(new WrapperActor(rules))))

  override def receive: Receive = {
    case x @ Categorize(sentence) => {
      router.tell(new ExtractWords(x.message), ActorRef.noSender)
    }
  }
}