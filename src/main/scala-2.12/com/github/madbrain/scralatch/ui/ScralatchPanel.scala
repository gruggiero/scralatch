package com.github.madbrain.scralatch.ui

import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.geom.AffineTransform
import java.awt.{Graphics2D, Color, Graphics}
import javax.swing.JPanel
import com.github.madbrain.scralatch.engine.LutinsManager
import com.github.madbrain.scralatch.model.RotationMode

class ScralatchPanel(val manager: LutinsManager) extends JPanel {

  val X_OFFSET = 250
  val Y_OFFSET = 250

  setOpaque(true)
  setBackground(Color.WHITE)
  setFocusable(true)

  addKeyListener(new KeyAdapter {
    override def keyPressed(e: KeyEvent): Unit = {
      manager.keyPressed(e.getKeyCode)
    }
  })

  manager.redrawListener(() => {
    repaint()
  })

  override def paintComponent(g: Graphics) = {
    super.paintComponent(g)
    manager.lutins.foreach(lutin => {
      val costume = lutin.costume
      val at = new AffineTransform()
      var invertX = 1.0
      at.translate(-costume.origin.x, -costume.origin.y)
      lutin.rotationMode match {
        case RotationMode.ALL =>
          at.rotate(lutin.direction - 90)
        case RotationMode.MIRROR =>
          if (lutin.direction == -90) {
            at.scale(-1, 1)
            at.translate(-costume.image.getWidth, 0)
            invertX = -1.0
          }
        case _ =>
      }
      at.translate((X_OFFSET + lutin.position.x) * invertX, Y_OFFSET + lutin.position.y)
      g.asInstanceOf[Graphics2D].drawImage(costume.image, at, this)
    })
  }
}
