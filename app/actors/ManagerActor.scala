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

/* Created by bfattahov on 22.08.14. */
class ManagerActor extends Actor {

  override def receive: Receive = {
    case StartSocket => {
      val (enumerator, channel) = Concurrent.broadcast[JsValue]

      Akka.system.actorOf(Props(new Gamer(1, enumerator, channel, self)))
      sender ! (channel, enumerator)
    }
    case x: JsValue => println(s"Message from $sender: $x")
    case SocketClosed(userId) =>  println(s"User $userId closed")
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

class Gamer(userId: Int,val enumerator: Enumerator[JsValue], channel: Channel[JsValue], receiver: ActorRef) extends Actor {

  val iteratee = Iteratee.foreach[JsValue](json => receiver ! json).mapDone(_ => receiver ! SocketClosed(userId))

  override def receive: Actor.Receive = {
    case Message(json) => channel push json
  }
}


 trait SocketMessage

case class StartSocket(userId: Int) extends SocketMessage

case class SocketClosed(userId: Int) extends SocketMessage

case class UpdateTime() extends SocketMessage

case class Start(userId: Int) extends SocketMessage

case class Stop(userId: Int) extends SocketMessage

case class Quit(userId: Int) extends SocketMessage

case class Message(json: JsValue) extends SocketMessage

case class Input(json: JsValue) extends SocketMessage