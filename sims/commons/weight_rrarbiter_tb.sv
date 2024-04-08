module weight_rrarbiter_tb;

  parameter int nReq  = 4;
  parameter int wBits = 4 ;

  bit               clock     ;
  bit               reset   = 1;
  bit               trigger   ;
  bit   [nReq-1:0]  request   ;
  bit   [nReq-1:0]  grant     ;
  bit               weight_update   ;
  bit [nReq-1:0][wBits-1:0] weights ;

  weight_rrarbiter #(nReq) dut(.*);

  initial forever #1ns clock = ~clock;
  initial begin
    //for(int i = 0; i < nReq; nReq++) begin
    //  weights[i] = i == 0 ? 1 : i;
    //end
    weights[0] = 1;
    weights[1] = 2;
    weights[2] = 3;
    weights[3] = 4;
    #10ns

    reset = 0;
    #10ns
    trigger = 1;
    request = '1;
    #100ns
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
endmodule: weight_rrarbiter_tb
