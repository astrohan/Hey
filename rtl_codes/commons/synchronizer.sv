module synchronizer #(
  parameter int unsigned STAGES = 2,
  parameter bit ResetValue = 1'b0
) (
  input  logic clock,
  input  logic reset,
  input  logic x,
  output logic y
);
  (* dont_touch = "true" *)
  (* async_reg = "true" *)
  logic [STAGES-1:0] reg_q;
  logic x_d;

  always_ff @(posedge clock) begin
    if (reset)
      x_d <= ResetValue;
    else
      x_d <= x;
  end

  always_ff @(posedge clock) begin
    if (reset)
      reg_q <= {STAGES{ResetValue}};
    else
    //  reg_q <= {reg_q[STAGES-2:0], $random & 1 ? x_d : x};
      reg_q <= {reg_q[STAGES-2:0], x_d};
  end

  assign y = reg_q[STAGES-1];

endmodule: synchronizer
