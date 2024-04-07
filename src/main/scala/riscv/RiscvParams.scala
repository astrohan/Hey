package riscv

import chisel3._
import chisel3.util.BitPat

trait CoreParams {
  val isa: String           // imacfd
  val xLen: Int             // 32 or 64
  val useZICSR: Boolean
  val useZIFENCE: Boolean
  val useZICBO: Boolean

  require(isa.contains('i'), "IType ISA is mandantory profile")
  require(Seq(32, 64).contains(xLen), "xLen must be 32 or 64")

  def isaMap: Map[String, BitPat] = isa.map( _ match {
    case 'i' => Instructions.IType
    case 'm' => Instructions.MType
    case 'a' => Instructions.AType
    case 'c' => Instructions.CType
    case 'f' => Instructions.FType
    case 'd' => Instructions.DType
  }).flatten.toMap
  def isaExt: Map[String, BitPat] = {
    (if(useZICSR)    Instructions.ZICSRType else Map.empty) ++
    (if(useZIFENCE)  Instructions.ZIFENCEIType else Map.empty) ++
    (if(useZICBO)    Instructions.ZICSRType else Map.empty) ++
    Instructions.SYSTEMType
  }.toMap
  def allInst: Map[String, BitPat] = isaMap ++ isaExt
}
