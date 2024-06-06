module netconex {
    requires transitive com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports qc.netconex;
    exports qc.netconex.error;
    exports qc.netconex.request;
}
