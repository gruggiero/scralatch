package com.github.madbrain.scralatch.model

import java.awt.Point

sealed trait Instruction
case class Goto(position: Point) extends Instruction
case class MoveForward(amount: Int) extends Instruction
case class PointInDirection(angle: Double) extends Instruction

case class NextCostume() extends Instruction

case class Wait(amount: Double) extends Instruction