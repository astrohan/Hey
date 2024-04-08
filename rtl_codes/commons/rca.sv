/**
* ==Ripple Carry Adder==
*/
module RCA #(
  parameter int dataWidth = 32
) (
  input   logic [dataWidth-1:0] a,
  input   logic [dataWidth-1:0] b,
  input   logic                 ci,
  output  logic [dataWidth-1:0] s,
  output  logic                 co
);

  function logic [1:0] fa(logic a, logic b, logic cin);
    return a + b + cin;
  endfunction

  logic [dataWidth:0] c;

  always_comb begin
    c[0] = ci;

    for(int i = 0; i < dataWidth; i++) begin 
      {c[i+1], s[i]} = fa(a[i], b[i], c[i]);
    end

    co = c[dataWidth];
  end

endmodule
