package com.github.madbrain.scralatch

import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.awt.{Dimension, Point}
import java.io.File
import javax.imageio.ImageIO

import com.github.madbrain.scralatch.engine.LutinsManager
import com.github.madbrain.scralatch.model._
import com.github.madbrain.scralatch.ui.ScralatchUI

trait Scralatch {

  def main(args: Array[String]) {
    val ui = new ScralatchUI(new LutinsManager(lutins.toSeq))
    ui.setVisible(true)
  }

  val lutins = scala.collection.mutable.ArrayBuffer[Lutin]()
  var currentLutin: Lutin = null
  var currentBlock: Block = null
  val builders = scala.collection.mutable.ArrayBuffer[Builder]()

  def lutin(name: String)(body: => Unit) = {
    currentLutin = new Lutin(name)
    lutins += currentLutin
    body
    finishBuilders()
  }

  def costume(name: String) = {
    val builder = new CostumeBuilder(name)
    builders += builder
    builder
  }

  def peutPivoter = {
    currentLutin.setRotationMode(RotationMode.ALL)
  }

  def peutSeRetourner = {
    currentLutin.setRotationMode(RotationMode.MIRROR)
  }

  def nePeutPasSeRetourner = {
    currentLutin.setRotationMode(RotationMode.NONE)
  }

  // blocks
  def auDepart(body: => Unit) = {
    startBlock(new AtStartBlock())
    body
  }

  def quandEstPressé(key: KeyboardKey)(body: => Unit): Unit = {
    startBlock(new WhenPressedBlock(key))
    body
  }

  // instructions
  def vaA(x: Int, y: Int) = currentBlock.push(Goto(new Point(x, y)))
  def avance(amount: Int) = currentBlock.push(MoveForward(amount))
  def pointeVers(angle: Double) = currentBlock.push(PointInDirection(angle))

  def costumeSuivant = currentBlock.push(NextCostume())

  def attend(amount: Double) = currentBlock.push(Wait(amount))

  private def finishBuilders() = {
    builders.foreach(_.finish())
    builders.clear()
  }

  private def startBlock(block: Block) = {
    currentBlock = block
    currentLutin.addBlock(block)
  }

  trait Builder {
    def finish(): Unit
  }

  class CostumeBuilder(val name: String) extends Builder {

    var costumeImage: BufferedImage = null
    var originPoint: java.awt.Point = null
    var originPosition: OriginPosition = null

    def image(filename: String) = {
      costumeImage = ImageIO.read(new File(filename))
      this
    }

    def origine(position: OriginPosition) = {
      originPosition = position
      this
    }
    def origine(x: Int, y: Int) = {
      originPoint = new Point(x, y)
      this
    }

    def finish() = {
      val origin = if (originPoint != null) originPoint
        else originPosition.originOf(new Dimension(costumeImage.getWidth, costumeImage.getHeight))
      currentLutin.addCostume(Costume(name, costumeImage, origin))
    }
  }

  sealed trait OriginPosition {
    def originOf(size: Dimension): Point
  }
  object centre extends OriginPosition {
    override def originOf(size: Dimension): Point = {
      new Point(size.width / 2, size.height / 2)
    }
  }

  // keys
  object flecheDroite extends KeyboardKey {
    override val keyCode: Int = KeyEvent.VK_RIGHT
  }
  object flecheGauche extends KeyboardKey {
    override val keyCode: Int = KeyEvent.VK_LEFT
  }
  object flecheHaut extends KeyboardKey {
    override val keyCode: Int = KeyEvent.VK_UP
  }
  object flecheBas extends KeyboardKey {
    override val keyCode: Int = KeyEvent.VK_DOWN
  }
  def touche(key: String): KeyboardKey = new KeyboardKey {
    override val keyCode: Int = KeyEvent.getExtendedKeyCodeForChar(key(0))
  }

}
