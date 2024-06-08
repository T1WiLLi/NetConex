module netconex {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires transitive com.fasterxml.jackson.databind;

    exports qc.netconex;
    exports qc.netconex.error;
    exports qc.netconex.request;
}