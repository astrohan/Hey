rm -rf workdir
verilator --main --timing --cc --exe --build -O2 -sv +1800-2017ext+sv --assert --timescale 1ns/1ps --trace-fst --trace-threads 2 --Mdir workdir --top-module top -I${project}/rtl_codes/commons CommonUtils.sv top.sv RRArbiter.sv
./workdir/Vtop
