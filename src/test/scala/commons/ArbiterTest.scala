package commons

import chiseltest._
import commons.CommonUtils.log2


class ArbiterTest extends hey.BaseTest {
  behavior of "Arbiter Test"

  "Arbiter" should "be fair" in {
    for(nReq <- 1 until 16) {
      test(new RRArbiter(nReq)).withAnnotations(anno) { dut =>
        dut.clock.setTimeout(1000)
        dut.clock.step(10)

        for(i <- 0 until 32) {
          dut.io.trigger.poke(true)
          dut.io.request.poke((1 << nReq) - 1)
          dut.io.grant.expect(1<<(i%nReq))
          dut.clock.step()
        }
      }
    }
  }
}
