package play

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor.Receive

/* Created by bfattahov on 16.08.14. */
class Manager(player1: ActorRef, player2: ActorRef) extends Actor {
  override def receive: Receive = {
    case Start =>
      player1 ! Board(3, 3, 3, IndexedSeq.empty)
      context.become(getReceiver(player1, player2).andThen(common))
  }

  private def getReceiver(from: ActorRef, to: ActorRef): Receive = {
    case board: Board if sender == from => if (board.getWinner.isEmpty) {
      to ! board
      context become getReceiver(to, from).andThen(common)
    } else println(from + " is a winner!")
    case _ if sender == to => println("This player must not send me a message!")
  }

  private def common: Receive = {
    case Stop => println("We will stop!")
      context become common
    case _ => println("We are stopped or message does not applicable")
  }

}

object Start

object Stop

object GetCurrent
