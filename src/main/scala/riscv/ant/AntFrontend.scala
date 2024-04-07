package riscv.ant

import chisel3._
import chisel3.util.Valid
import org.chipsalliance.cde.config.Parameters
import riscv.DecodeCmd
import commons.CommonUtils.UtilsForBundle

/**==AntFrontend==
 * Assuming there is a L0 cache and it can send responce with zero-latency.
 * And there is no constraints for IC. L0 cache must have AC=1
 */
class AntFrontend(implicit hartId: Int, p: Parameters) extends AntModule {
  val io = IO(new Bundle {
    val bootAddr = Input(UInt(xLen.W))
    val imem = new AntIMem
    val intreq = Input(new AntIntreq)
    val decodeCmd = Valid(new DecodeCmd)
  })

  val pc = RegInit(io.bootAddr)
  val pc4 = pc + 4.U
  val npc = WireInit(pc4)
  val wfi = RegInit(false.B)

  when(io.imem.req.fire) {
    pc := npc
  }

  io.imem.req.valid := !wfi
  io.imem.req.bits.connect(0.U.asTypeOf(new AntReq),
    _.addr -> pc
  )
  io.imem.req.bits.addr := pc

  io.imem.resp.ready := true.B
  io.decodeCmd.valid := io.imem.resp.valid
  io.decodeCmd.bits := DecodeCmd(io.imem.resp.bits.data, params.allInst)

  when(io.intreq.asUInt.orR) {
    wfi := false.B
  }.elsewhen(io.decodeCmd.valid && io.decodeCmd.bits.wfi) {
    wfi := true.B
  }
}
