package samdk.asteroids.gp

import scala.util.Random

import samdk.asteroids.ai._

object GPTree {
  val gen = new Random
  /*
   * population initialization
   */
  def rampedHalfAndHalf(game:AIPlayableGame):Array[GPTree] = {
    val fulls:Array[GPTree] = (for (i <- 0 until 250) yield (randFullTree(game,5+i/25))).toArray
    val grows:Array[GPTree] = (for (i <- 0 until 250) yield (randGrowTree(game,5+i/25))).toArray
    fulls ++ grows
  }

  /*
   * tree creation
   */
  def randFullTree(game:AIPlayableGame,height:Int):GPTree = height match {
    case 1 => 
      val (func,funcName) = randTerminal(game)
      new GPTree(func,funcName)
    case _ =>
      val (func, arity, funcName) = randFunc(game)
      val children:Array[GPTree] =
        (for ( i <- 0 until arity ) yield randFullTree(game,height-1)).toArray
      arity match {
        case 1 => new GPTree(func.asInstanceOf[Double=>Double],children,funcName)
        case 2 => new GPTree(func.asInstanceOf[(Double,Double)=>Double],children,funcName)
        case 3 => new GPTree(func.asInstanceOf[(Double,Double,Double)=>Double],children,funcName)
        case _ => 
          new GPTree(func.asInstanceOf[(Double,Double,Double,Double)=>Double],children,funcName)
      }
  }

  def randGrowTree(game:AIPlayableGame,height:Int):GPTree = height match {
    case 1 =>
      val (func,funcName) = randTerminal(game)
      new GPTree(func,funcName)
    case _ =>
      val r = gen.nextInt(4)
      r match {
        case 1 =>
          val (func,funcName) = randTerminal(game)
          new GPTree(func,funcName)
        case _ =>
          val (func, arity, funcName) = randFunc(game)
          val children:Array[GPTree] =
            (for ( i <- 0 until arity ) yield randFullTree(game,height-1)).toArray
          arity match {
            case 1 => new GPTree(func.asInstanceOf[Double=>Double],children,funcName)
            case 2 => new GPTree(func.asInstanceOf[(Double,Double)=>Double],children,funcName)
            case 3 => new GPTree(func.asInstanceOf[(Double,Double,Double)=>Double],children,funcName)
            case _ => 
              new GPTree(func.asInstanceOf[(Double,Double,Double,Double)=>Double],children,funcName)
          }
      }
  }

  /*
   * mutation and crossover
   */
  def mutateRandomSubtree(tree:GPTree,game:AIPlayableGame) = {
    tree.arity match {
      case 0 => treeClone(tree)
      case _ => treeClone(randChild(tree)) 
    }
  }

  /*
  def mutateReplaceSubtree(tree:GPTree,game:AIPlayableGame) = {
    def selectSubtree(current:GPTree,depth:Int):GPTree = depth match {
      case 0 => current
      case _ => selectSubtree(randChild(current),depth-1)
    }
    val height = tree.height
    val depth = Math.min(1,(new Random).nextInt(height))
    val treeNew = treeClone(tree)
    val selected = selectSubtree(treeNew,depth)
    val newChildren:Array[GPTree] =
      (for (i <- 1 to selected.children.length)
        yield randGrowTree(game,(new Random).nextInt(depth))).toArray
    selected.children = newChildren
    treeNew
  }*/


  def mutateReplaceSubtree(wholeTree:GPTree,game:AIPlayableGame):GPTree = {
    def chooseAndReplace(tree:GPTree,depth:Int):GPTree = depth match {
      case 1 =>
        val newChildren:Array[GPTree] =
          (for (oldchild <- tree.children)
            yield randGrowTree(game,gen.nextInt(2 * oldchild.height))).toArray
        val x = new GPTree(
            tree.func0,
            tree.func1,
            tree.func2,
            tree.func3,
            tree.func4,
            tree.arity,
            newChildren,
            tree.funcName
          )
        x.randValue = tree.randValue
        x
      case _ =>
        val newChildren:Array[GPTree] = tree.arity match {
          case 0 => new Array[GPTree](0)
          case 1 =>
            Array(chooseAndReplace(tree.children(0),depth-1))
          case 2 =>
            gen.nextInt(2) match {
              case 0 =>
                Array(chooseAndReplace(tree.children(0),depth-1),
                          treeClone(tree.children(1)))
              case 1 =>
                Array(treeClone(tree.children(0)),
                          chooseAndReplace(tree.children(1),depth-1))
            }
          case 3 =>
            gen.nextInt(3) match {
              case 0 =>
                Array(chooseAndReplace(tree.children(0),depth-1),
                          treeClone(tree.children(1)),
                          treeClone(tree.children(2)))
              case 1 =>
                Array(treeClone(tree.children(0)),
                          chooseAndReplace(tree.children(1),depth-1),
                          treeClone(tree.children(2)))
              case 2 =>
                Array(treeClone(tree.children(0)),
                          treeClone(tree.children(1)),
                          chooseAndReplace(tree.children(2),depth-1))
            }
        }
        val x = new GPTree(
          tree.func0,
          tree.func1,
          tree.func2,
          tree.func3,
          tree.func4,
          tree.arity,
          newChildren,
          tree.funcName
        )
        x.randValue = tree.randValue
        x
    }
    chooseAndReplace(wholeTree,Math.min(1,gen.nextInt(wholeTree.height)))
  }
  
  def crossSwapSubtree(wholeTree1:GPTree,wholeTree2:GPTree,game:AIPlayableGame):GPTree = {
    def selectSubtree(current:GPTree,depth:Int):GPTree = depth match {
      case 0 => current
      case _ => 
        if (current.arity > 0) selectSubtree(randChild(current),depth-1)
        else current
    }
    val selected = selectSubtree(treeClone(wholeTree2),gen.nextInt(wholeTree2.height))
    def chooseAndReplace(tree:GPTree,depth:Int):GPTree = depth match {
      case 1 =>
        val newChildren = tree.arity match {
          case 0 => tree.children
          case _ =>
            val r = gen.nextInt(tree.arity)
            var nc = tree.children
            nc(r) = selected
            nc
        }
        val x = new GPTree(
            tree.func0,
            tree.func1,
            tree.func2,
            tree.func3,
            tree.func4,
            tree.arity,
            newChildren,
            tree.funcName
          )
        x.randValue = tree.randValue
        x
      case _ =>
        val newChildren:Array[GPTree] = tree.arity match {
          case 0 => new Array[GPTree](0)
          case 1 =>
            Array(chooseAndReplace(tree.children(0),depth-1))
          case 2 =>
            gen.nextInt(2) match {
              case 0 =>
                Array(chooseAndReplace(tree.children(0),depth-1),
                          treeClone(tree.children(1)))
              case 1 =>
                Array(treeClone(tree.children(0)),
                          chooseAndReplace(tree.children(1),depth-1))
            }
          case 3 =>
            gen.nextInt(3) match {
              case 0 =>
                Array(chooseAndReplace(tree.children(0),depth-1),
                          treeClone(tree.children(1)),
                          treeClone(tree.children(2)))
              case 1 =>
                Array(treeClone(tree.children(0)),
                          chooseAndReplace(tree.children(1),depth-1),
                          treeClone(tree.children(2)))
              case 2 =>
                Array(treeClone(tree.children(0)),
                          treeClone(tree.children(1)),
                          chooseAndReplace(tree.children(2),depth-1))
            }
        }
        val x = new GPTree(
          tree.func0,
          tree.func1,
          tree.func2,
          tree.func3,
          tree.func4,
          tree.arity,
          newChildren,
          tree.funcName
        )
        x.randValue = tree.randValue
        x
    }
    chooseAndReplace(wholeTree1,Math.min(1,gen.nextInt(wholeTree1.height)))
  }


  def crossCombination(tree1:GPTree,tree2:GPTree,game:AIPlayableGame) = {
    new GPTree(game.plus _,
      Array(
        treeClone(tree1),
        treeClone(tree2)
      ),"+   ")
  }


  /*
   * utility functions 
   */
  def randChild(tree:GPTree):GPTree = {
    val r = gen.nextInt(tree.children.length)
    tree.children(r)
  }

  def randTerminal(game:AIPlayableGame) = {
    val terms = game.terms
    val r = gen.nextInt(terms.length)
    terms(r)
  }

  def randFunc(game:AIPlayableGame) = {
    val funcs = game.funcs
    val r = gen.nextInt(funcs.length)
    funcs(r)
  }

  def treeClone(tree:GPTree):GPTree = {
    tree.height match {
      case 1 => 
        val newChildren = new Array[GPTree](0)
        val x = new GPTree(
          tree.func0,
          tree.func1,
          tree.func2,
          tree.func3,
          tree.func4,
          tree.arity,
          newChildren,
          tree.funcName
        )
        x.randValue = tree.randValue
        x
      case _ =>
        val newChildren = (for (child <- tree.children) yield treeClone(child)).toArray
        new GPTree(
          tree.func0,
          tree.func1,
          tree.func2,
          tree.func3,
          tree.func4,
          tree.arity,
          newChildren,
          tree.funcName
        )
    }
  }
}
@serializable
class GPTree(
  val func0: ()=>Double,
  val func1: Double=>Double,
  val func2: (Double,Double)=>Double,
  val func3: (Double,Double,Double)=>Double,
  val func4: (Double,Double,Double,Double)=>Double,
  val arity: Int,
  val children: Array[GPTree],
  val funcName: String
) {
  def this(func: (Double,Double,Double,Double)=>Double, children: Array[GPTree],funcName:String) =
    this(H.p0,H.p1,H.p2,H.p3,func,4,children,funcName)
  def this(func: (Double,Double,Double)=>Double, children: Array[GPTree],funcName:String) =
    this(H.p0,H.p1,H.p2,func,H.p4,3,children,funcName)
  def this(func: (Double,Double)=>Double, children:Array[GPTree],funcName:String) =
    this(H.p0,H.p1,func,H.p3,H.p4,2,children,funcName)
  def this(func: Double=>Double, children: Array[GPTree],funcName:String) =
    this(H.p0,func,H.p2,H.p3,H.p4,1,children,funcName)
  def this(func: ()=>Double,funcName:String) =
    this(func,H.p1,H.p2,H.p3,H.p4,0,new Array[GPTree](0),funcName)

  var randValue = -2.0

  def call():Double = {
    var x = 0.0
    if (arity == 1) {
      x = func1(children(0).call())
    }
    else if (arity == 2) {
      x = func2(children(0).call(),children(1).call())
    }
    else if (arity == 3) {
      x = func3(children(0).call(),children(1).call(),children(2).call())
    }
    else if (arity == 4) {
      x = func4(children(0).call(),children(1).call(),children(2).call(),children(3).call())
    }
    else {
      if (funcName == "rand") {
        if (randValue < -1.5) randValue = func0()
        x = randValue
      }
      else {x = func0()}
    }
    x
  }

  def height():Int = {
    arity match {
      case 0 => 1
      case 1 => 1 + children(0).height
      case 2 => 1 + H.max(children(0).height, children(1).height)
      case 3 => 1 + H.max(children(0).height, children(1).height, children(2).height)
      case 4 => 1 + H.max(children(0).height, children(1).height,
                             children(2).height, children(3).height)
    }
  }
}
object H {
  def p0():Double = {0.0}
  def p1(a:Double):Double = {0.0}
  def p2(a:Double,b:Double):Double = {0.0}
  def p3(a:Double,b:Double,c:Double):Double = {0.0}
  def p4(a:Double,b:Double,c:Double,d:Double):Double = {0.0}

  // works for up to 4 positive integers
  def max(a:Int,b:Int,c:Int,d:Int):Int = {
    Math.max(Math.max(a,b),Math.max(c,d))
  }
  def max(a:Int,b:Int,c:Int):Int = {
    Math.max(Math.max(a,b),c)
  }
  def max(a:Int,b:Int):Int = {
    Math.max(a,b)
  }
}
