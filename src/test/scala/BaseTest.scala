package hey

import chiseltest.{ChiselScalatestTester, VerilatorBackendAnnotation, WriteFstAnnotation}
import chiseltest.simulator.{SimulatorDebugAnnotation, VerilatorFlags}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

abstract class BaseTest extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  private val vFlags = Seq(
    "--timing",
    "--threads", "4",
    "--trace-fst",
    "--trace-threads", "2",
    "--trace-underscore",
  )
  protected val anno = Seq(
    WriteFstAnnotation,
    VerilatorBackendAnnotation,
    VerilatorFlags(vFlags),
    SimulatorDebugAnnotation)
}
