package com.github.madbrain.scralatch.model

import java.awt.Point
import com.github.madbrain.scralatch.engine.BlockExecutionContext

class Lutin(val name: String) {

  val costumes = scala.collection.mutable.ArrayBuffer[Costume]()
  val blocks = scala.collection.mutable.ArrayBuffer[Block]()
  private var currentCostume = 0
  var position = new Point(0, 0)
  var direction = 90.0
  var rotationMode = RotationMode.ALL

  def costume = costumes(currentCostume)

  def addCostume(costume: Costume) = {
    costumes += costume
  }

  def setRotationMode(rotationMode: RotationMode) = {
    this.rotationMode = rotationMode
  }

  def addBlock(block: Block) = {
    blocks += block
  }

  def execute(context: BlockExecutionContext): Unit = {
    Iterator.continually(context.next)
      .takeWhile(_.isDefined)
      .map(_.get)
      .foreach(instr => execute(instr, context))
  }

  def execute(instr: Instruction, context: BlockExecutionContext): Unit = {
    instr match {
      case Goto(p) =>
        position = p
        context.redraw()

      case MoveForward(amount) =>
        position = new Point(
          position.x + (amount * Math.cos((direction - 90) * Math.PI / 180)).toInt,
          position.y + (amount * Math.sin((direction - 90) * Math.PI / 180)).toInt)
        context.redraw()

      case NextCostume() =>
        currentCostume = (currentCostume + 1) % costumes.size
        context.redraw()

      case PointInDirection(angle) =>
        direction = angle
        context.redraw()

      case Wait(amount) =>
        context.continueIn(amount)

      case _ =>
        println(s"TODO: unknown instruction $instr")
    }
  }

}
