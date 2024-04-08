PROJECT := ${shell git rev-parse --show-toplevel}

TESTS  = rrarbiter weight_rrarbiter


VERILATOR_OPTS  = --main --timing --cc --exe --build -O2
VERILATOR_OPTS += -sv +1800-2017ext+sv --assert
VERILATOR_OPTS += --timescale 1ns/1ps
VERILATOR_OPTS += --trace-fst --trace-threads 2 --trace-underscore
VEIRLATOR_OPTS += --threads 16
VERILATOR_OPTS += -Wall
VERILATOR_OPTS += -Wno-BLKANDNBLK -Wno-LITENDIAN -Wno-CASEINCOMPLETE
VERILATOR_OPTS += -Wno-CMPCONST -Wno-WIDTH -Wno-WIDTHCONCAT -Wno-UNSIGNED
VERILATOR_OPTS += -Wno-UNOPTFLAT -Wno-fatal -Wno-UNUSED -Wno-MODDUP


.PHONY: all clean ${TESTS}
all: ${TESTS}
${TESTS}:
	mkdir -p sim_dump
	rm -rf ./workspace_$@/V$@_tb sim_dump/$@.fst
	echo ${PROJECT}/rtl_codes/commons/common_pkg.sv > vcode_$@.f
	echo ${PROJECT}/rtl_codes/commons/*.sv >> vcode_$@.f
	echo ./tb_pkg.sv >> vcode_$@.f
	echo ./$@_tb.sv >> vcode_$@.f
	verilator ${VERILATOR_OPTS} --Mdir workspace_$@ --top-module $@_tb -f vcode_$@.f
	./workspace_$@/V$@_tb +dump_name=sim_dump/$@ +use_fst=true +dump_enable=true

clean:
	${foreach test,${TESTS},rm -rf ./workspace_${test} ${test}.fst vcode_${test}.f sim_dump}
