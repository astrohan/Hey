package common_pkg;

  `define UIntToThermoMacro(bits) \
  function automatic logic [bits-1:0] UIntToThermo(input [bits-1:0] data); \
    (* dont_touch = "true" *) \
    logic [bits-1:0] prev, next; \
    next = data; \
    for(int s = 0; s < $clog2(bits); s++) begin \
      prev = next; \
      for(int i = 0; i < bits; i++) begin \
        automatic bit isBypass = (i>>s)%2 == 0; \
        automatic int j = ((i>>s)<<s)-1; \
        next[i] = isBypass ? prev[i] : prev[i] || prev[j]; \
      end \
    end \
    return next; \
  endfunction: UIntToThermo \

endpackage: common_pkg
