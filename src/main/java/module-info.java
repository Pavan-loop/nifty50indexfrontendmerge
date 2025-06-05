module com.nichi.mergednifty50index {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;
    requires jdk.compiler;
    requires io.github.cdimascio.dotenv.java;


    opens com.nichi.mergednifty50index to javafx.fxml, org.hibernate.orm.core;
    opens com.nichi.mergednifty50index.controller to javafx.fxml;
    opens com.nichi.mergednifty50index.model to javafx.fxml;
    opens com.nichi.mergednifty50index.database.pavan.model to org.hibernate.orm.core;

    exports com.nichi.mergednifty50index;
    exports com.nichi.mergednifty50index.controller;
    exports com.nichi.mergednifty50index.model;
}