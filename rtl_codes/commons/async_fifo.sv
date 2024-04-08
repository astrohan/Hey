module async_fifo #(
  parameter int FifoDepth = 16,
  parameter int DataWidth = 32
)(
  input   logic                   wclk    ,
  input   logic                   wreset  ,
  input   logic                   rclk    ,
  input   logic                   rreset  ,
  input   logic                   wvalid  ,
  input   logic [DataWidth-1:0]   wdata   ,
  output  logic                   wready  ,
  output  logic                   rvalid  ,
  output  logic [DataWidth-1:0]   rdata   ,
  input   logic                   rready
);

  localparam LogDepth = $clog2(FifoDepth);
  localparam PtrFull  = 1<<(LogDepth);
  localparam PtrEmpty = 0;

  logic   [LogDepth:0] wptr, wgry;
  logic   [LogDepth:0] rptr, rgry;
  logic   [LogDepth:0] wptr_rclk, wgry_rclk;
  logic   [LogDepth:0] rptr_wclk, rgry_wclk;

  logic   [FifoDepth-1:0][DataWidth-1:0] fifo;
  logic   full, empty;

  assign  full  = (wptr ^ rptr_wclk) == PtrFull;
  assign  empty = (wptr_rclk ^ rptr) == PtrEmpty;

  assign wgry = wptr ^ (wptr>>1);
  assign rgry = rptr ^ (rptr>>1);
  for(genvar i = 0; i < LogDepth; i++) begin
    assign wptr_rclk[i] = ^wgry_rclk[LogDepth:i];
    assign rptr_wclk[i] = ^rgry_wclk[LogDepth:i];

    synchronizer r2w (wclk, wreset, rgry[i], rgry_wclk[i]);
    synchronizer w2r (rclk, rreset, wgry[i], wgry_rclk[i]);
  end

  always_ff @(posedge wclk) begin
    if(wvalid && wready)
      fifo[wptr[LogDepth-1:0]] <= wdata;
  end

 always_ff @(posedge wclk) begin
     if(wreset) wptr <= 0;
     else if(wvalid && wready) wptr <= wptr + 1;
 end

  always_ff @(posedge rclk) begin
    if(rreset) rptr <= 0;
    else if(rvalid && rready) rptr <= rptr + 1;
  end

  assign wready = !full;
  assign rvalid = !empty;
  assign rdata = fifo[rptr[LogDepth-1:0]];

endmodule : async_fifo
