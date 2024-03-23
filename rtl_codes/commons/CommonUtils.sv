package CommonUtils;

    localparam int funcMaxBits = 32;
    function automatic logic [funcMaxBits-1:0] UIntToThermo(input [funcMaxBits-1:0] data);
        logic [funcMaxBits-1:0] prev, next;

        next = data;
        for(int s = 0; s < $clog2(funcMaxBits); s++) begin
            prev = next;
            for(int i = 0; i < funcMaxBits; i++) begin
                automatic bit isBypass = (i>>s)%2 == 0;
                automatic int j = ((i>>s)<<s)-1;
                next[i] = isBypass ? prev[i] : prev[i] || prev[j];
            end
        end

        return next;
    endfunction : UIntToThermo

endpackage : CommonUtils