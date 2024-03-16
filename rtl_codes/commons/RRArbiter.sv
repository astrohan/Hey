module RRArbiter import CommonUtils::*; #(
    parameter int nReq  = 4
) (
    input   logic               clock   ,
    input   logic               reset   ,
    input   logic               trigger ,
    input   logic   [nReq-1:0]  request ,
    output  logic   [nReq-1:0]  grant
);

    logic   [nReq-1:0]  mask, masked_request;
    logic   [nReq-1:0]  thermo;

    function automatic logic [nReq-1:0] UIntToThermo(input [nReq-1:0] data);
        logic [nReq-1:0] prev, next;

        next = data;
        for(int s = 0; s < $clog2(nReq); s++) begin
            prev = next;
            for(int i = 0; i < nReq; i++) begin
                automatic bit isBypass = (i>>s)%2 == 0;
                automatic int j = ((i>>s)<<s)-1;
                next[i] = isBypass ? prev[i] : prev[i] || prev[j];
            end
        end

        return next;
    endfunction : UIntToThermo

    assign  masked_request = |(mask & request) ? mask & request : request;
    assign  grant = thermo & ~(thermo<<1);

    always_comb begin
        thermo = UIntToThermo (masked_request);
    end

    always_ff @(posedge clock) begin
        if(reset) mask <= 0;
        else if(trigger) mask <= thermo<<1;
    end

endmodule : RRArbiter
