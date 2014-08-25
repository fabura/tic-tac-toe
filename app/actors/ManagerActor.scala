package actors

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Props, Actor, ActorRef, OneForOneStrategy}

import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.iteratee.{Iteratee, Concurrent, Enumerator}
import play.api.libs.json.JsValue

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import play.api.Play.current
import ExecutionContext.Implicits.global

import play.api.libs.json._
import play.api.libs.json.Json._

/* Created by bfattahov on 22.08.14. */
class ManagerActor extends Actor {

  override def receive: Receive = {
    case StartSocket(userId) => {
      val (enumerator, channel) = Concurrent.broadcast[JsValue]

      val gamer = Akka.system.actorOf(Props(new Gamer(userId, enumerator, channel, self)))
      val iteratee = Iteratee.foreach[JsValue](json => gamer ! Input(json)).mapDone(_ => gamer ! SocketClosed(userId))
      sender !(iteratee, enumerator)
    }
    case x: JsValue => {
      println(s"Message from $sender: $x")
      sender ! Message(Json.toJson(Map("message" -> toJson(s"Okay! You send: $x"))))
    }
    case SocketClosed(userId) => println(s"User $userId closed")
  }

}

//
//class GameActor(player1: ActorRef, player2: ActorRef) extends Actor {
//  override def receive: Actor.Receive = ???
//}
//
//class Waiter(userId: Int, enumerator: Enumerator[JsValue], channel: Channel[JsValue]) extends Actor {
//  override def receive: Actor.Receive = ???
//}

class Gamer(userId: Int, val enumerator: Enumerator[JsValue], channel: Channel[JsValue], receiver: ActorRef) extends Actor {

  override def receive: Actor.Receive = {
    case Input(json) => receiver ! json
    case Message(json) => channel push json
  }
}


trait SocketMessage

case object GetIteratee extends SocketMessage

case class StartSocket(userId: Int) extends SocketMessage

case class SocketClosed(userId: Int) extends SocketMessage

case class UpdateTime() extends SocketMessage

case class Start(userId: Int) extends SocketMessage

case class Stop(userId: Int) extends SocketMessage

case class Quit(userId: Int) extends SocketMessage

case class Message(json: JsValue) extends SocketMessage

case class Input(json: JsValue) extends SocketMessage