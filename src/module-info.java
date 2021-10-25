module SQLLibrary {
    requires java.base;
    requires java.sql;
    requires java.logging;
    requires java.prefs;
    exports sqlibrary.annotation;
    exports sqlibrary.connection;
    exports sqlibrary.queries;
}