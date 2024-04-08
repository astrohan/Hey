package tb_pkg;

  function void init_tb(
    output int max_cycles,
    output string dump_enable,
    output string dump_name,
    output string use_fst,
  );

  // default setting
  max_cycles = 1000000;
  dump_enable = "true";
  dump_name = "l1_dump_000";
  use_fst = "true";

  // get value from args
  $value$plusargs("max_cycles=%d", max_cycles);
  $value$plusargs("dump_enable=%s", dump_enable);
  $value$plusargs("dump_name=%s", dump_name);
  $value$plusargs("use_fst=%s", use_fst);

  if(use_fst == "true")   dump_name = {dump_name, ".fst"};
  else          dump_name = {dump_name, ".vcd"};

  $fwrite(32'h80000002, "max-cycles: %d\n", max_cycles);

  if(dump_enable == "true") begin
    $fwrite(32'h80000002, "dump_name: %s\n", dump_name);
    $dumpfile(dump_name);
    $dumpvars(0, harness);
  end
  else begin
    $fwrite(32'h80000002, "run without dump\n");
  end
endfunction : init_tb

endpackage : tb_pkg