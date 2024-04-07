package riscv

import chisel3._


trait RiscvCSR {
  def modeLSB: Int = 8
  def mode(addr: UInt): UInt = addr(modeLSB + PRV.SZ - 1, modeLSB)
}

object PRV {
  val SZ = 2
  val U = 0
  val S = 1
  val H = 2
  val M = 3
}
