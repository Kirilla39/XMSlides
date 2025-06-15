module com.kirilla.xmslides {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires org.fxmisc.richtext;
    requires org.fxmisc.undo;
    requires java.desktop;
    requires java.prefs;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;


    opens com.kirilla.xmslides to javafx.fxml;
    exports com.kirilla.xmslides;
    exports com.kirilla.xmslides.controllers;
    opens com.kirilla.xmslides.controllers to javafx.fxml;
    exports com.kirilla.xmslides.controllers.main_modules;
    opens com.kirilla.xmslides.controllers.main_modules to javafx.fxml;
}