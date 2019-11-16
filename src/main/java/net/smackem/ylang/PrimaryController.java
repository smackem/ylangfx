package net.smackem.ylang;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrimaryController {

    @FXML
    private Button primaryButton;
    @FXML
    private Button sayHelloButton;

    @FXML
    private void switchToSecondary() throws IOException {
        App.getInstance().setRoot("secondary");
    }

    @FXML
    private void sayHello() throws Exception {
        try (final RemoteImageProcService service = new RemoteImageProcService("localhost", 50051)) {
            service.sayHello("gurke");
        }
    }
}
