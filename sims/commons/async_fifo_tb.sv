module async_fifo_tb;

  parameter int FifoDepth = 2 ;
  parameter int DataWidth = 4 ;

  bit                   wclk    ;
  bit                   wreset  = 1;
  bit                   rclk    ;
  bit                   rreset  = 1;
  bit                   wvalid  ;
  bit [DataWidth-1:0]   wdata   ;
  bit                   wready  ;
  bit                   rvalid  ;
  bit [DataWidth-1:0]   rdata   ;
  bit                   rready  = 1;

  async_fifo #(
    .FifoDepth(FifoDepth),
    .DataWidth(DataWidth)
  ) dut(.*);

  always_ff @(posedge wclk) begin
    if(wvalid)
      wdata <= wdata + 1;
  end

  int wfreq = 100; // [MHz]
  int rfreq = 100; // [MHz]
  real whalf = 1000.0/wfreq;
  real rhalf = 1000.0/rfreq;
  initial #0.5 forever #whalf wclk = ~wclk;
  initial #0.7 forever #rhalf rclk = ~rclk;

  initial begin
    #10ns
    fork
      begin
        @(posedge wclk)
        #0.1
        wreset = 0;
      end
      begin
        @(posedge rclk)
        #0.1
        rreset = 0;
      end
    join
    #20ns
    wvalid  = 1;
    #10us
    $finish(2);
  end

  int max_cycles = 1000000;
  string dump_enable = "true";
  string dump_name = "l1_dump_000";
  string use_fst = "true";
  initial begin
    tb_pkg::init_tb(max_cycles, dump_enable, dump_name, use_fst);

    #max_cycles
    $fwrite(32'h80000002, "simulation finished due to time-out\n");
    $finish(2);
  end
endmodule: async_fifo_tb
