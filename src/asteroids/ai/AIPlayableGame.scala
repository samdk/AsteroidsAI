package samdk.asteroids.ai

import scala.util.Random

import samdk.asteroids.core._

@serializable
class AIPlayableGame extends AsteroidsGame {
  var lrVote = 0.0
  var udVote = 0.0

  def one() = {1.0}
  def none() = {-1 * one()}
  def zero() = {0.0}
  def half() = {0.5}

  def rand() = {(new Random).nextDouble() * 2 - 1}
  
  def heading() = {(getShip().getHeading() - Math.Pi) / Math.Pi}
  def xvel() = {getShip().getXVel() / Ship.MAX_SPEED}
  def yvel() = {getShip().getYVel() / Ship.MAX_SPEED}

  // these are somewhat complicated. sensors is an int(16)
  // forward/back use 3 sensors, sides use 5. values are normalized so it doesn't matter
  def forwardDanger(): Double = {
    val sensors = getShip().getSensors()
    val a = if (sensors(15) == 0) 0.0 else {(1.0 / Math.pow(sensors(15),1.1))}
    val b = if (sensors(0) == 0) 0.0 else {(1.0 / Math.pow(sensors(0),1.1))}
    val c = if (sensors(1) == 0) 0.0 else {(1.0 / Math.pow(sensors(1),1.1))}
    (a + b + c) / 3
  }
  def backDanger(): Double = {
    val sensors = getShip().getSensors()
    val a = if (sensors(7) == 0) 0.0 else {(1.0 / Math.pow(sensors(7),1.1))}
    val b = if (sensors(8) == 0) 0.0 else {(1.0 / Math.pow(sensors(8),1.1))}
    val c = if (sensors(9) == 0) 0.0 else {(1.0 / Math.pow(sensors(9),1.1))}
    (a + b + c) / -3 
  }
  def leftDanger(): Double = {
    val sensors = getShip().getSensors()
    val z = if (sensors(1) == 0) 0.0 else {(1.0 / Math.pow(sensors(1),1.1))}
    val a = if (sensors(2) == 0) 0.0 else {(1.0 / Math.pow(sensors(2),1.1))}
    val b = if (sensors(3) == 0) 0.0 else {(1.0 / Math.pow(sensors(3),1.1))}
    val c = if (sensors(4) == 0) 0.0 else {(1.0 / Math.pow(sensors(4),1.1))}
    val d = if (sensors(5) == 0) 0.0 else {(1.0 / Math.pow(sensors(5),1.1))}
    val e = if (sensors(6) == 0) 0.0 else {(1.0 / Math.pow(sensors(6),1.1))}
    (z + a + b + c + d + e) / 6
  }
  def rightDanger(): Double = {
    val sensors = getShip().getSensors()
    val a = if (sensors(10) == 0) 0.0 else {(1.0 / Math.pow(sensors(10),1.1))}
    val b = if (sensors(11) == 0) 0.0 else {(1.0 / Math.pow(sensors(11),1.1))}
    val c = if (sensors(12) == 0) 0.0 else {(1.0 / Math.pow(sensors(12),1.1))}
    val d = if (sensors(13) == 0) 0.0 else {(1.0 / Math.pow(sensors(13),1.1))}
    val e = if (sensors(14) == 0) 0.0 else {(1.0 / Math.pow(sensors(14),1.1))}
    val z = if (sensors(15) == 0) 0.0 else {(1.0 / Math.pow(sensors(15),1.1))}
    (a + b + c + d + e + z) / -6
  }

  def ifPos(x: Double, y: Double, z: Double): Double = {
    if (x > 0) return y
    else return z
  }
  def signedSquare(x: Double): Double = {
    val y = Math.pow(x,2)
    if (x < 0) return -1*y
    else return y
  }
  def signedSqrt(x: Double): Double = {
    Math.sqrt(Math.abs(x))
  }
  def negate(x: Double): Double = { -1 * x }
  def plus(x: Double, y: Double) = { Math.min(1,Math.max(-1,x + y)) }
  def minus(x: Double, y: Double) = { Math.min(1,Math.max(-1,x - y)) }
  def times(x: Double, y: Double) = { x * y }
  def divide(x: Double, y: Double) = { if (y == 0) y else Math.min(1,Math.max(-1,x / y)) }

  def updown(x: Double): Double  = {
    if (x < 0) {
      //doUp(false)
      //doDown(true)
      voteDown(x)
    }
    else if (x > 0) {
      //doUp(true)
      //doDown(false)
      voteUp(x)
    }
    else {
      //doUp(false)
      //doDown(false)
    }
    x
  }
  def leftright(x: Double): Double = {
    if (x > 0) {
      //doRight(false)
      //doLeft(true)
      voteLeft(x)
    }
    else if (x < 0) {
      //doRight(true)
      //doLeft(false)
      voteRight(x)
    }
    else {
      //doRight(false)
      doLeft(false)
    }
    x
  }

  def terms = { 
    Array(
      (one _,"1   "),
      (none _,"-1  "),
      (zero _,"0   "),
      //(half _,"0.5 "),
      (rand _,"rand"),
      //(heading _,"head"),
      //(xvel _,"xvel"),
      //(yvel _,"yvel"),
      (forwardDanger _,"fw d"),
      (backDanger _,"bk d"),
      (leftDanger _,"le d"),
      (rightDanger _,"ri d")
    )
  }
  def funcs = {
    Array(
      (ifPos _, 3, "if  "),
      (signedSquare _, 1, "sq  "),
      //(signedSqrt _, 1, "sqrt"),
      (negate _, 1, "-1* "),
      (plus _, 2, "+   "),
      (minus _,2, "-   "),
      (times _,2, "*   "),
      //(divide _,2,"/   "),
      (updown _,1,"ud  "),
      (leftright _,1,"lr  ")
    )
  }

  def voteUp(x:Double) {
    udVote += x
  }
  def voteDown(x:Double) {
    udVote -= x
  }
  def voteLeft(x:Double) {
    lrVote += x
  }
  def voteRight(x:Double) {
    lrVote -= x
  }
  def ePlurbusUnum() {
    if (lrVote > 0) {
      doLeft(true)
      doRight(false)
    }
    else if (lrVote < 0) {
      doLeft(false)
      doRight(true)
    }
    else {
      doLeft(false)
      doRight(false)
    }
    if (udVote > 0) {
      doUp(true)
      doDown(false)
    }
    else if (udVote < 0) {
      doUp(false)
      doDown(true)
    }
    else {
      doUp(false)
      doDown(false)
    }
    lrVote = 0
    udVote = 0
  }
}
