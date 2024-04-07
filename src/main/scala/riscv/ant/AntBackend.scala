package riscv.ant

import chisel3._
import org.chipsalliance.cde.config.Parameters
import riscv.DecodeCmd


class AntBackend(implicit hartId: Int, p: Parameters) extends AntModule {
  val io = IO(new Bundle {
    val dmem = new AntDMem
    val decodeCmd = Input(new DecodeCmd)
  })
}
