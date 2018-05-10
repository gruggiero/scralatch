package com.github.madbrain.scralatch.model

class Block {
  val instructions = scala.collection.mutable.ArrayBuffer[Instruction]()
  def push(instr: Instruction) = {
    instructions += instr
  }
}

class AtStartBlock extends Block

trait KeyboardKey {
  val keyCode: Int
}

class WhenPressedBlock(val key: KeyboardKey) extends Block