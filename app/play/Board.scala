package play

/* Created by bfattahov on 16.08.14. */
case class Board(width:Int, height:Int, length:Int, signs: IndexedSeq[Sign]) {
 def getWinner:Option[Sign] = None
}

sealed class Sign
case object Cross extends Sign
case object Circle extends Sign
case object Empty extends Sign