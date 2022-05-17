module ru.yarsu.molab {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens ru.yarsu.molab to javafx.fxml;
    exports ru.yarsu.molab;
}