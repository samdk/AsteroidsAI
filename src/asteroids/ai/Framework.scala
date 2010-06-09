package samdk.asteroids.ai

import scala.util.Random

import java.util._

import samdk.asteroids.core._
import gui._
import ai._

@serializable
class Framework(
  val ai: AsteroidsAI, 
  val game: AIPlayableGame,
  var guiExists: Boolean) {
  // alternate constructors
  def this(ai:AsteroidsAI) = this(ai,ai.game,false)
  def this(ai:AsteroidsAI,guiExists:Boolean) = this(ai,ai.game,guiExists)

  var timeRestriction = true

  // constants
  private final val DEFAULT_TIMESTEP = 1000 / Constants.FPS
  
  // methods
  def run():Int = {
    run((new Random).nextInt)
  }
  def run(seed:Int):Int = {
    def runBlind():Int = {
      while (!done(game)) {
        step()
      }
      game.setGameOver()
      game.getPoints()
    }
    def runGUI():Int = {
      val gui = new AsteroidsGUI(game)
      val t = new Timer()
      t.schedule(new TimerTask() {
        override def run() {
          step()
          gui.repaint()
          if (done(game)) {
            game.setGameOver()
            cancel()
          }
        }
      },0,DEFAULT_TIMESTEP)
      while (!done(game)) {
        Thread.sleep(100)
      }
      game.getPoints()
    }

    game.newGame(seed)

    var x = 0
    if (guiExists) {
      x = runGUI()
    }
    else {
      x = runBlind()
    }
    if (ai.still) x / 2
    else x
    //if (ai.still) game.getFrameCount() / 2
    //else game.getFrameCount()
  }

  def runMulti():Array[Int]= {
    val seeds = Array(1,2,3,4,5,6,7,8,9,10)
    Array(run(61393921),
          run(0),
          run(1012),
          run(92459)
        )
  }
  def runMultiRandom():Array[Int] = {
    Array(run(),run(),run(),run())
  }
  def runSplitMultiRandom():Array[Int] = {
    Array(run(),run(),run(),run())
  }

  def step() {
    ai.doAction(game)
    game.update()
  }

  def done(game:AIPlayableGame):Boolean = {
    if (timeRestriction) game.isGameOver || game.getFrameCount() > 2700
    else game.isGameOver
  }
}
