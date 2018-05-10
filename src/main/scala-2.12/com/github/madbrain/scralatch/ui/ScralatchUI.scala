package com.github.madbrain.scralatch.ui

import java.awt.BorderLayout
import javax.swing.JFrame

import com.github.madbrain.scralatch.engine.LutinsManager

class ScralatchUI(val lutinManager: LutinsManager) extends JFrame {

  setTitle("Scralatch")
  setSize(500, 500)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  add(new ScralatchPanel(lutinManager), BorderLayout.CENTER)

}
