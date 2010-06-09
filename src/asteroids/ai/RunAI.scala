//package samdk.asteroids.ai

import java.util.Scanner
import java.io._

import samdk.asteroids.ai._

object RunAI extends Application {
  var result = new Framework(new SimpleGPAI).run()
  println(result)
}

object RunAIGUI extends Application {
  var result = new Framework(new SimpleGPAI,true).run()
  println(result)
}

object RunCL {
  def main(args: Array[String]) {
    val s = new Scanner(System.in)
    var framework = new Framework(new SimpleGPAI,true)
    var str = ""
    while (true) {
      str = s.nextLine()
      str match {
        case "new" =>
          framework = new Framework(new SimpleGPAI,true)
          println("\tresult: " + framework.run())
        case "play" =>
          println("\tresult: " + framework.run())
        case "load" =>
          println("loading...")
          framework = new Framework(RunGP.unserializeSolution(args(0)),true)
          println("done")
        case "guioff" =>
          framework.guiExists = false
        case "guion" =>
          framework.guiExists = true
        case "troff" =>
          framework.timeRestriction = false
        case "exit" =>
          System.exit(-1)
        case _ =>
          println("not a valid command! (you entered " + str + ")")
      }
    }
  }
}

object RunQuickTest {
  def main(args: Array[String]) {
    val framework = new Framework(RunGP.unserializeSolution(args(0)))
    println("starting...")
    val rs = framework.runMulti()
    println("  rs:   " + rs.reduceLeft(_+_))
    println("  height: " + framework.ai.tree.height)
    framework.timeRestriction = false
    val num = args(1).toFloat
    println("doing randoms...")
    val ranks = (for (i <- 1 to num.toInt) yield framework.run()).toArray
    val avg = ranks.reduceLeft(_+_) / num
    println("  rank: " + ranks.reduceLeft(_+_))
    println("  avg:  " + avg)
    val stddevs = (for (x <- ranks) yield Math.pow(x-avg,2))
    val stddev = Math.sqrt(stddevs.reduceLeft(_+_) / num)
    println("  sdev: " + stddev)
    println("  range:[" + ranks.reduceLeft(Math.min(_,_)) + "," + ranks.reduceLeft(Math.max(_,_)) + "]")
  }
}
