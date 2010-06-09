package samdk.asteroids.ai

import samdk.asteroids.core._
import ai._
import gp._

@serializable
abstract class AsteroidsAI {
  val tree:GPTree
  val game:AIPlayableGame;
  var still:Boolean;
  def doAction(game: AIPlayableGame)
}
/*
class WaverAI(val game:AIPlayableGame) extends AsteroidsAI {
  def this() = this(new AIPlayableGame)

  var waiting = 0
  override def doAction(game: AIPlayableGame) {
    if (game.getShip().getYVel() > -3) {
      game.doUp(true)
    }
    else {
      game.doUp(false)
    }
    if (waiting == 0) {
      if (game.getShip().getHeading() < 3) {
        game.doRight(true)
        game.doLeft(false)
      }
      else {
        game.doLeft(true)
        game.doRight(false)
      }
      waiting = 6
    }
    else waiting = waiting - 1
    game.doShoot(true)
  }
}*/

@serializable
class SimpleGPAI(val tree:GPTree,val game:AIPlayableGame) extends AsteroidsAI {
  def this(game:AIPlayableGame) = this(GPTree.randFullTree(game,20),game)
  def this() = this(new AIPlayableGame)
  def this(tree:GPTree) = this(tree,new AIPlayableGame)

  var still = true
  var alwaysShoot = true

  override def doAction(game: AIPlayableGame) {
    game.doUp(false)
    game.doDown(false)
    game.doLeft(false)
    game.doRight(false)
    if (alwaysShoot) game.doShoot(true)
    else game.doShoot(false)
    
    tree.call()
    if (game.udVote != 0) still = false
    game.ePlurbusUnum()
  }
}
