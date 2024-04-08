module rrarbiter import common_pkg::*; #(
  parameter int nReq  = 4
) (
  input   logic             clock   ,
  input   logic             reset   ,
  input   logic             trigger ,
  input   logic [nReq-1:0]  request ,
  output  logic [nReq-1:0]  grant
);
  logic   [nReq-1:0]  mask, masked_request;
  logic   [nReq-1:0]  thermo;

  assign  masked_request = |(mask & request) ? mask & request : request;
  assign  grant = thermo & ~(thermo<<1);

  `UIntToThermoMacro(nReq)

  always_comb begin
    //thermo = nReq'(UIntToThermo(funcMaxBits'(masked_request)));
    thermo = UIntToThermo(masked_request);
  end

  always_ff @(posedge clock) begin
    if(reset) mask <= 0;
    else if(trigger) mask <= thermo<<1;
  end

endmodule: rrarbiter
