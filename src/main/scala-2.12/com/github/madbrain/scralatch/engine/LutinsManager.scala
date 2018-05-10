package com.github.madbrain.scralatch.engine

import java.util.concurrent.TimeUnit

import com.github.madbrain.scralatch.model.{AtStartBlock, Lutin, WhenPressedBlock}

class LutinsManager(val lutins: Seq[Lutin]) {

  private val redrawListeners = scala.collection.mutable.ArrayBuffer[() => Unit]()
  private val executionQueue = new java.util.concurrent.LinkedBlockingDeque[BlockExecutionContext]()

  atStart()

  def redrawListener(listener: () => Unit) = {
    redrawListeners += listener
  }

  def keyPressed(keyCode: Int): Unit = {
    lutins.foreach(lutin => {
      lutin.blocks.foreach {
        case whenPressed: WhenPressedBlock =>
          if (whenPressed.key.keyCode ==  keyCode) {
            executionQueue.push(new BlockExecutionContext(whenPressed.instructions, lutin))
          }
        case _ =>
      }
    })
  }

  private def atStart() = {
    lutins.foreach(lutin => {
      lutin.blocks.foreach {
        case atStart: AtStartBlock =>
          executionQueue.push(new BlockExecutionContext(atStart.instructions, lutin))
        case _ =>
      }
    })
    new Thread(new ExecutionLoop(), "lutin-manager").start()
  }

  private class ExecutionLoop extends Runnable {

    implicit val contextOrdering = new Ordering[BlockExecutionContext] {
      override def compare(x: BlockExecutionContext, y: BlockExecutionContext): Int = x.restartAt.compareTo(y.restartAt)
    }

    val waitingContext = scala.collection.mutable.PriorityQueue[BlockExecutionContext]()

    override def run(): Unit = {
      while (true) {
        val context = waitingContext.headOption match {
          case Some(c) =>
            val timeout = c.restartAt - System.currentTimeMillis()
            val v = executionQueue.poll(timeout, TimeUnit.MILLISECONDS)
            Option(v).getOrElse(waitingContext.dequeue())
          case None =>
            executionQueue.take()
        }
        if (context != null) {
          context.execute()
          if (context.needsRedraw) {
            redrawListeners.foreach(listener => listener())
          }
          if (context.paused) {
            if (context.restartAt > 0L) {
              waitingContext.enqueue(context)
            } else {
              executionQueue.push(context)
            }
          }
        }
      }
    }
  }

}
