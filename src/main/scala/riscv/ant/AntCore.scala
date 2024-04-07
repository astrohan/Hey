package riscv.ant

import chisel3._
import commons.ClockGate
import org.chipsalliance.cde.config.Parameters

class GPR(implicit val hartId: Int, val p: Parameters) extends HasAntParams {
  private val rf = RegInit(VecInit.fill(32)(0.U(xLen.W)))

  def read(id: UInt) = Mux(id === 0.U, 0.U, rf(id))
  def write(id: UInt, data: UInt) = {
    when(id =/= 0.U) { rf(id) := data }
  }
}

class AntCore(implicit hartId: Int, p: Parameters) extends AntModule {
  val io = IO(new AntIO)

  val icg_enable = RegInit(true.B)
  val gated_clock = ClockGate(clock, icg_enable, "ant_icg")
  icg_enable := Seq(!io.enable).reduce(_||_)

  val frontend -> backend = withClockAndReset(gated_clock, reset) {
    Module(new AntFrontend) -> Module(new AntBackend)
  }

  frontend.io.bootAddr := io.bootAddr
  frontend.io.imem <> io.imem
  frontend.io.intreq := io.intreq
  io.imem.req.valid := frontend.io.imem.req.valid && io.enable

  backend.io.dmem <> io.dmem
  backend.io.decodeCmd := frontend.io.decodeCmd
}
