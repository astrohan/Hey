module top;

    parameter int nReq  = 16     ;
    bit               clock     ;
    bit               reset     = 1;
    bit               trigger   ;
    bit   [nReq-1:0]  request   ;
    bit   [nReq-1:0]  grant     ;

    RRArbiter #(nReq) dut(.*);

    initial forever #1ns clock = ~clock;
    initial begin
        #100ns
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
        $value$plusargs("max_cycles=%d", max_cycles);
        $value$plusargs("dump_enable=%s", dump_enable);
        $value$plusargs("dump_name=%s", dump_name);
        $value$plusargs("use_fst=%s", use_fst);

        if(use_fst == "true")   dump_name = {dump_name, ".fst"};
        else                    dump_name = {dump_name, ".vcd"};

        $fwrite(32'h80000002, "max-cycles: %d\n", max_cycles);

        if(dump_enable == "true") begin
            $fwrite(32'h80000002, "dump_name: %s\n", dump_name);
            $dumpfile(dump_name);
            $dumpvars(0, harness);
        end
        else begin
            $fwrite(32'h80000002, "run without dump\n");
        end

        #max_cycles
        $fwrite(32'h80000002, "simulation finished due to time-out\n");
        $finish(2);
    end
endmodule : top