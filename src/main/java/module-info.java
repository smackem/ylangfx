module net.smackem.ylang {
    requires javafx.controls;
    requires javafx.fxml;

    opens net.smackem.ylang to javafx.fxml;
    exports net.smackem.ylang;
}