package actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.{Logging, LoggingAdapter}
import helper.Rule

import scala.collection.mutable.ArrayBuffer

object AggregatorActor {
  def props(rules: ArrayBuffer[Rule]): Props = Props(new AggregatorActor(rules))
  case class Tag(val words: List[String])
}

class AggregatorActor(val rules: ArrayBuffer[Rule]) extends Actor with ActorLogging {
  import AggregatorActor._

  var filteredCategories: ArrayBuffer[String] = ArrayBuffer[String]()
  var filteredRules: ArrayBuffer[Rule] = ArrayBuffer[Rule]()

  override val log: LoggingAdapter = Logging.getLogger(context.system, this)

  override def receive: Receive = {
    case x @ Tag(words) => {
      filteredRules = matchedExtraction(x.words)
      filteredCategories = matchedFiltering(filteredRules, x.words)
      log.info(filteredCategories.mkString(" "))
    }
  }

  // checking against required words
  def matchedExtraction(words: List[String]): ArrayBuffer[Rule] = {
    var matchedRules: ArrayBuffer[Rule] = ArrayBuffer[Rule]()
    var count: Int = 0
    for (rule <- rules) {
      count = rule.requiredWords.length
      for (ruleWord <- rule.requiredWords) {
        var pause: Boolean = false
        for (word <- words if pause == false){
          if (word.equals(ruleWord)) {
            count -= 1
            pause = true
          }
        }
      }
      if (count == 0) {
        matchedRules += rule
      }
    }

    return matchedRules
  }

  // checking against banned words
  def matchedFiltering(extractedRules: ArrayBuffer[Rule], words: List[String]): ArrayBuffer[String] = {
    if (extractedRules.length > 0) {
      extractedRules.filter {
        _.bannedWords.intersect(words).length != 0
      }
    } else {
      return ArrayBuffer[String]()
    }
    var result: ArrayBuffer[String] = ArrayBuffer[String]()
    for (rule <- extractedRules) {
      result += rule.category
    }
    return result
  }
}
