/**
* ==Carry Select Adder==
*/
module CSA #(
  parameter int dataWidth = 32,
  parameter int nStage = 4
) (
  input   logic [dataWidth-1:0] a,
  input   logic [dataWidth-1:0] b,
  input   logic                 ci,
  output  logic [dataWidth-1:0] s,
  output  logic                 co
);
  initial assert (dataWidth % nStage == 0) else $error("Error: dataWidth must be a multiple of nStage.");

  localparam subWidth = dataWidth/nStage;

  logic [nStage-1:0] selC;
  assign co = selC[nStage-1];
  for(genvar i = 0; i < nStage; i++) begin 
    if(i == 0) begin: HEAD_OF_CSA
      RCA #(subWidth) first(
        a[subWidth-1:0], 
        b[subWidth-1:0], 
        ci, s[subWidth-1:0], selC[0]);
    end
    else begin: BODY_OF_CSA
      logic c0, c1;
      logic [subWidth-1:0] s0, s1;
      RCA #(subWidth) add0(
        a[i*subWidth+subWidth-1:i*subWidth], 
        b[i*subWidth+subWidth-1:i*subWidth], 
        0, s0, c0);
      RCA #(subWidth) add1(
        a[i*subWidth+subWidth-1:i*subWidth], 
        b[i*subWidth+subWidth-1:i*subWidth], 
        1, s1, c1);
      assign selC[i] = selC[i-1] ? c1 : c0;
      assign s[i*subWidth+subWidth-1:i*subWidth] = selC[i-1] ? s1 : s0;
    end
  end

endmodule
