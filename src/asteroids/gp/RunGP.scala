//package samdk.asteroids.gp

import scala.util.Random

import java.util.Scanner
import java.util.Date
import java.io._

import samdk.asteroids.gp._
import samdk.asteroids.ai._

object RunGP {
  def main(args: Array[String]) {
    val popsize = args(0).toInt
    val maxEvals = args(1).toInt
    var evals = 0
     
    val game = new AIPlayableGame
    val population = GPTree.rampedHalfAndHalf(game)
    println("population initialized")
    // initialize population
    val evaluated =
      for (tree <- population) yield (rankNote(tree,game),tree)
    println("population evaluated")
    // get top 10% of population
    val sorted:Array[(Int,GPTree)] =
      evaluated.toList.sort( (a,b) => a._1 < b._1).toArray
    val sliced = sorted.reverse.slice(0,popsize)
    println("best: " + sorted.last._1)
    println("worst: " + sorted.first._1)
    doGP(sliced,maxEvals,game)
  }

  def doGP(sorted:Array[(Int,GPTree)],maxEvals:Int,game:AIPlayableGame) {
    var pop = sorted
    var evals = 0
    while (evals < maxEvals) {
      val selected = select3(pop)
      val ranked = sortSols(selected).reverse
      val r = (new Random).nextInt(4)
      val newSol =
        r match {
          case 0 => GPTree.mutateReplaceSubtree(ranked.first._2._2,game)
          case 1 => GPTree.mutateRandomSubtree(ranked.first._2._2,game)
          case 2 => GPTree.crossCombination(ranked(0)._2._2,ranked(1)._2._2,game)
          case _ => GPTree.crossSwapSubtree(ranked(0)._2._2,ranked(1)._2._2,game)
        }
      val newRank = rank(newSol,game)
      if (newSol.height <= 50) {
        if (newRank == ranked.first._2._1) {
          if (newSol.height <= ranked.first._2._2.height)
            pop = replace(pop,ranked.first._1,(newRank,newSol))
        }
        else {
          pop = replace(pop,ranked.last._1,(newRank,newSol))
        }
      }
      evals += 4

      if ( evals % 120 == 0 ) {
        println("best/evals: " + getBestVal(pop) + "/" + evals)
        //println("\t"+ (for ((v,s) <- pop) yield v).toList.sort( (a,b) => a < b).toArray)
      }
    }
    // at the end, show a demonstration of the best thingy
    
    println(">>> final best: " + getBestVal(pop))
    val bestSol = getBestSol(pop)
  
    val s = new Scanner(System.in)
    var framework = new Framework(new SimpleGPAI(bestSol,game),true)
    serializeSolution(framework.ai,("sol_" + sorted.length + "_" + maxEvals + "_" + (new Date).getTime() + ".sol"))
    /*var str = ""
    while (true) {
      str = s.nextLine()
      str match {
        case "play" =>
          println("\tresult: " + framework.run())
        case "save" =>
          println("saving solution...")
          serializeSolution(framework.ai,("tmp_" + (new Date).getTime() + ".sol"))
          println("solution saved")
        case "exit" =>
          System.exit(-1)
        case "height" =>
          println(framework.ai.tree.height)
        case _ =>
          try {println("\tresult: " + framework.run(str.toInt) + " (ran: " + str.toInt + ")")}
          catch { case e: Exception => "not valid" }
      }
    }*/
  }

  def rankNote(sol:GPTree,game:AIPlayableGame):Int = {
    print(".")
    rank(sol,game)
  }
  def rank(sol:GPTree,game:AIPlayableGame):Int = {
    val framework = new Framework(new SimpleGPAI(sol,game))
    //framework.runMultiRandom().toList.sort((a,b)=>a < b).toArray.slice(2,4).reduceLeft(_+_)
    framework.runMulti().reduceLeft(_+_)
  }

  def select3(pop:Array[(Int,GPTree)]):Array[(Int,(Int,GPTree))] = {
    val rand = new Random
    val len = pop.length
    val r1 = rand.nextInt(len)
    val r2 = rand.nextInt(len)
    val r3 = rand.nextInt(len)
    Array((r1,pop(r1)),(r2,pop(r2)),(r3,pop(r3)))
  }

  def sortSols(sols:Array[(Int,(Int,GPTree))]):Array[(Int,(Int,GPTree))] = {
    sols.toList.sort( (a,b) => a._2._1 < b._2._1 ).toArray
  }

  def replace(
    pop:Array[(Int,GPTree)],
    oldIndex:Int,
    newSol:(Int,GPTree)
  ):Array[(Int,GPTree)] = {
    pop(oldIndex) = newSol
    pop
  }

  def getBestVal(pop:Array[(Int,GPTree)]):Int = {
    (for ((v,tree) <- pop) yield v).reduceLeft(Math.max(_,_))
  }
  def getBestSol(pop:Array[(Int,GPTree)]):GPTree = {
    def reduction(a:(Int,GPTree),b:(Int,GPTree)):(Int,GPTree) = {
      if (a._1 < b._1) b
      else a
    }
    pop.reduceLeft(reduction(_,_))._2
  }


  def serializeSolution(ai:AsteroidsAI,filename:String) {
    val stream = new ObjectOutputStream(new FileOutputStream(filename))
    stream.writeObject(ai)
    stream.close()
  }
  def unserializeSolution():AsteroidsAI = {unserializeSolution("tmp.sol")}
  def unserializeSolution(filename:String):AsteroidsAI = {
    val stream = new ObjectInputStream(new FileInputStream(filename))
    val ai:AsteroidsAI = (stream.readObject()).asInstanceOf[AsteroidsAI]
    stream.close
    ai
  }
}
