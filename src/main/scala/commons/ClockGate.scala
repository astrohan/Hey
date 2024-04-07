package commons

import chisel3._
import chisel3.util.HasBlackBoxInline


object ClockGate {
  def apply(clk: Clock, en: Bool, name: String = "icg"): Clock = {
    val icg = Module(new ClockGate).suggestName(name)

    icg.io.clk := clk
    icg.io.en := en
    icg.io.test_en := false.B
    icg.io.gclk
  }
}

class ClockGate extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val clk     = Input(Clock())
    val en      = Input(Bool())
    val test_en = Input(Bool())
    val gclk    = Output(Clock())
  })

  setInline("EICG_wrapper.sv",
    """module EICG_wrapper (
      |  input clk,
      |  input en,
      |  input test_en,
      |  output gclk
      |);
      |
      |  reg en_latched /*verilator clock_enable*/;
      |
      |  always @(*) begin
      |     if (!clk) begin
      |        en_latched = en || test_en;
      |     end
      |  end
      |
      |  assign gclk = en_latched && clk;
      |
      |endmodule
    """.stripMargin)
}

