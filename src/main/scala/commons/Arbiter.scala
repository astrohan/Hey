package commons

import chisel3._
import chisel3.util.RegEnable
import commons.CommonUtils.UIntToThermo


abstract class Arbiter(val nReq: Int = 4) extends Module {
  val io = IO(new Bundle {
    val request = Input(UInt(nReq.W))
    val trigger = Input(Bool())
    val grant = Output(UInt(nReq.W))
  })

  val mask = RegEnable(getNextMask, 0.U, io.trigger)
  val maskedRequest = Mux((mask & io.request).orR, mask & io.request, io.request)

  io.grant := getGrant

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
