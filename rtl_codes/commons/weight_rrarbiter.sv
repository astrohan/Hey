/**==weight_rrarbiter==
 * @params nReq number of request
 * @params wBits bit-width of weight
 */
module weight_rrarbiter import common_pkg::*; #(
  parameter int nReq = 4,
  parameter int wBits = 4
)(
  input   logic                       clock   ,
  input   logic                       reset   ,
  input   logic                       trigger ,
  input   logic [nReq-1:0]            request ,
  output  logic [nReq-1:0]            grant   ,
  input   logic                       weight_update ,
  input   logic [nReq-1:0][wBits-1:0] weights 
);

  logic   [nReq-1:0]  weighted_request;
  logic   [nReq-1:0][wBits-1:0]   wcnt;

  logic   [$clog2(nReq)+wBits-1:0] sum_wcnt;
  logic   last_one_granted ;

  always_comb begin
    sum_wcnt = 0;
    for(int i = 0; i < nReq; i++) begin
      sum_wcnt = sum_wcnt + wcnt[i];
    end
    last_one_granted = |grant && (sum_wcnt == 1);
  end

  assign  wcnt_flatten = wcnt;
  assign  last_one_granted = |grant && |wcnt && !(wcnt_flatten & (wcnt_flatten-1));

  for(genvar i = 0; i < nReq; i++) begin: GEN_MASK
    assign  weighted_request[i] = request[i] && |wcnt[i];

    always_ff @(posedge clock) begin
      if(reset) 
        wcnt[i] <= weights[i];
      else if(weight_update || last_one_granted)
        wcnt[i] <= weights[i];
      else if(grant[i])
        wcnt[i] <= |wcnt[i] ? wcnt[i] - 1 : 0;
    end
  end: GEN_MASK

  rrarbiter #(
    .nReq (nReq)
  ) rrarbiter (
    .request(weighted_request),
    .* 
  );

endmodule: weight_rrarbiter
