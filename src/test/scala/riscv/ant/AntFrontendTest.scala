package riscv.ant

import chiseltest._
import chisel3._
import chisel3.util.Queue
import commons.Assembler
import org.chipsalliance.cde.config.{Config, Parameters}


class AntFrontendTest extends hey.BaseTest {
  behavior of "Ant Frontend Test"

  def prerequisite(dut: AntFrontendWrap): Unit = {
    dut.io.imem.req.ready.poke(false)
    dut.clock.setTimeout(1000)
    dut.clock.step(10)
  }

  "AntFrontend" must "have a meal" in {
    implicit val hartId: Int = 0
    implicit val config: Config = new Config((_, _, _) => {
      case AntParamsKey(hartId) => AntParams()
    })

    val meals: Seq[String] = Seq(
      "lw a0, 0(x0)",
      "lw a1, 4(x0)",
      "add a2, a0, a1",
      "sw a2, 8(x0)",
    )
    val cooked: Seq[BigInt] = Assembler(meals, 32)

    test(new AntFrontendWrap).withAnnotations(anno) { dut =>
      prerequisite(dut)
      for(i <- 0 until 3) {
        dut.clock.step()
        dut.io.imem.req.ready.poke(true)
        dut.io.imem.resp.valid.poke(true)
        dut.io.imem.resp.bits.data.poke(cooked(dut.io.imem.req.bits.addr.peekInt().toInt/4))
      }
      dut.clock.step()
      dut.io.imem.req.ready.poke(false)
      dut.io.imem.resp.valid.poke(false)
      dut.clock.step(10)
    }
  }
}

class AntFrontendWrap(implicit hartId: Int, p: Parameters) extends AntModule {
  val dut = Module(new AntFrontend)
  val io = IO(dut.io.cloneType)
  dut.io <> io

  val rbuf = Module(new Queue(new AntResp, 1))
  rbuf.io.enq <> io.imem.resp
  dut.io.imem.resp <> rbuf.io.deq
}
