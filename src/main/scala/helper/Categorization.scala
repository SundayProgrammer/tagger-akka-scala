package helper

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.parsing.json.JSON

class Categorization() {

  private var rules: ArrayBuffer[Rule] = new ArrayBuffer[Rule]()
  private var sentences: ArrayBuffer[String] = new ArrayBuffer[String]()

  def getRules: ArrayBuffer[Rule] = rules
  def getSentences: ArrayBuffer[String] = sentences

  def readRules(path: String): Unit = {
    val jsonString = Source.fromFile(path).getLines.mkString
    val result = for {
      Some(M(map)) <- List(JSON.parseFull(jsonString))
      L(rules) = map("rules")
      M(rules) <- rules
      LS(required) = rules("requiredWords")
      LS(banned) = rules("bannedWords")
      S(category) = rules("category")
    } yield {
      new Rule(required.toArray, banned.toArray, category)
    }
    for (rule <- result) {
      rules += rule
    }
  }

  def readSentences(path: String) = {
    val source = Source.fromFile(path)
    val result: Array[String] = source.getLines().toArray[String]
    source.close()
    for (sentence <- result) {
      sentences += sentence
    }
  }
}

class CC[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T]) }

object M extends CC[Map[String, Any]]
object L extends CC[List[Any]]
object LS extends CC[List[String]]
object S extends CC[String]