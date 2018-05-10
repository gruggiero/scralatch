package com.github.madbrain.scralatch.model

sealed trait RotationMode
object RotationMode {
  val ALL = new RotationMode {}
  val MIRROR = new RotationMode {}
  val NONE = new RotationMode {}
}
