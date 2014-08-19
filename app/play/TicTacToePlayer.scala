package play

import akka.actor.Actor
import akka.actor.Actor.Receive

/* Created by bfattahov on 16.08.14. */
abstract class TicTacToePlayer extends Actor {

  def makeStep(board: Board): Board

  override def receive: Receive = {
    case board: Board =>
      sender ! makeStep(board)
  }
}
