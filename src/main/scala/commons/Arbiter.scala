package commons

import chisel3._
import chisel3.util.RegEnable
import commons.CommonUtils.UIntToThermo

class ArbiterIO(nReq: Int) extends Bundle {
  val request = Input(UInt(nReq.W))
  val trigger = Input(Bool())
  val grant = Output(UInt(nReq.W))
}

abstract class Arbiter(val nReq: Int = 4) extends Module {
  val io = IO(new ArbiterIO(nReq))

  val mask = RegInit(0.U(nReq.W))
  val maskedRequest = Mux((mask & io.request).orR, mask & io.request, io.request)

  io.grant := getGrant
  when(io.trigger) { mask := getNextMask }
  
  def getGrant: UInt
  def getNextMask: UInt
}

class SimpleRRArbiter(nReq: Int) extends Arbiter {
  def getGrant: UInt = maskedRequest & ((~maskedRequest).asUInt + 1.U)
  def getNextMask: UInt = (~((io.grant<<1).asUInt - 1.U)).asUInt
}

class RRArbiter(nReq: Int) extends Arbiter {
  def getGrant: UInt = UIntToThermo(maskedRequest) & (~getNextMask).asUInt
  def getNextMask: UInt = (UIntToThermo(maskedRequest) << 1).asUInt
}
