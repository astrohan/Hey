package riscv.ant

import chisel3._
import chisel3.util.Decoupled
import org.chipsalliance.cde.config.{Field, Parameters}
import riscv.{CoreParams, MemCmd}


case class AntParamsKey(hartId: Int) extends Field[AntParams](AntParams())
case class AntParams(
  isa: String         = "im",
  xLen: Int           = 32,
  useZICSR: Boolean   = false,
  useZIFENCE: Boolean = false,
  useZICBO: Boolean   = false,
) extends CoreParams

trait HasAntParams {
  implicit val hartId: Int
  implicit val p: Parameters
  val params = p(AntParamsKey(hartId))

  val isa = params.isa
  val xLen = params.xLen
  val useZICSR = params.useZICSR
  val useZIFENCE = params.useZIFENCE
  val useZICBO = params.useZICBO
}

abstract class AntBundle(implicit val hartId: Int, val p: Parameters) extends Bundle with HasAntParams
abstract class AntModule(implicit val hartId: Int, val p: Parameters) extends Module with HasAntParams

class AntReq(implicit hartId: Int, p: Parameters) extends AntBundle {
  val addr    = UInt(xLen.W)
  val data    = UInt(xLen.W)
  val mask    = UInt((xLen/8).W)
  val size    = UInt(2.W)
  val signed  = Bool()
  val cmd     = UInt(MemCmd.SZ.W)
}
class AntResp(implicit hartId: Int, p: Parameters) extends AntReq

class AntIMem(implicit hartId: Int, p: Parameters) extends AntBundle {
  val req   = Decoupled(new AntReq)
  val resp  = Flipped(Decoupled(new AntResp))
}
class AntDMem(implicit hartId: Int, p: Parameters) extends AntIMem

class AntIntreq extends Bundle {
  val mdip  = Bool()
  val mtip  = Bool()
  val msip  = Bool()
  val meip  = Bool()
}

class AntIO(implicit hartId: Int, p: Parameters) extends AntBundle {
  val bootAddr = UInt(xLen.W)
  val imem = new AntIMem
  val dmem = new AntDMem
  val enable = Bool()
  val intreq = Input(new AntIntreq)
}


