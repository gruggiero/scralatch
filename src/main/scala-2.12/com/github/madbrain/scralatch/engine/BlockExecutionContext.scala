package com.github.madbrain.scralatch.engine

import com.github.madbrain.scralatch.model.{Lutin, Instruction}

class BlockExecutionContext(val instructions: Seq[Instruction], val lutin: Lutin) {

  var index = 0
  var restartAt = 0L
  var paused = false
  var needsRedraw = false

  def execute() = {
    needsRedraw = false
    paused = false
    restartAt = 0L
    lutin.execute(this)
  }

  def next = if (! paused && index < instructions.size) {
    val instr = instructions(index)
    index += 1
    Some(instr)
  } else {
    None
  }

  def continueIn(amount: Double): Unit = {
    restartAt = System.currentTimeMillis() + (amount * 1000).toLong
    paused = true
  }

  def redraw(): Unit = {
    needsRedraw = true
  }
}
