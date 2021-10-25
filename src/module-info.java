module SQLLibrary {
    requires java.base;
    requires java.sql;
    requires java.prefs;
    requires java.xml;
    exports sqlibrary.annotation;
    exports sqlibrary.connection;
    exports sqlibrary.queries;
    opens sqlibrary.util;
}