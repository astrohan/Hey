module simple_rrarbiter #(
  parameter int nReq  = 4
) (
  input   logic             clock   ,
  input   logic             reset   ,
  input   logic             trigger ,
  input   logic [nReq-1:0]  request ,
  output  logic [nReq-1:0]  grant
);

  logic   [nReq-1:0]  mask, masked_request;

  assign  masked_request = |(mask & request) ? mask & request : request;
  assign  grant = masked_request & (-masked_request);

  always_ff @(posedge clock) begin
    if(reset) mask <= 0;
    else if(trigger) mask <= ~((grant<<1) - 1);
  end

endmodule : simple_rrarbiter
