package CommonUtils;

    function automatic logic [N-1:0] UIntToThermo(input [N-1:0] data);
        parameter int N = 4;
        $display("here is %x", $bits(data));
    endfunction : UIntToThermo

endpackage : CommonUtils