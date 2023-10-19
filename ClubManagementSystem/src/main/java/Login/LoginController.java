package Login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    private double xPosition;

    private double yPosition;

    @FXML
    private StackPane LoginPane;

    @FXML
    void ClubAdvisorDetector(ActionEvent event) {
        try {
            System.out.println("Done");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ClubAdvisorDashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage and setting the scene as LoginPage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 750, 500);
            stage.setScene(scene);
            stage.centerOnScreen();

            // Show the stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // or handle the exception appropriately
        }
    }

    @FXML
    void StudentDirector(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Login/StudentLogin.fxml"));
        Parent root = loader.load();
        // Get the current stage and setting the scene as LoginPage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(root, 750, 500);
        stage.setScene(scene);
        stage.centerOnScreen();

        // Show the stage
        stage.show();
    }

    @FXML
    void LoginDragDetected(MouseEvent event) {
        Stage stage =  (Stage)LoginPane.getScene().getWindow();
        stage.setX(event.getScreenX()- xPosition);
        stage.setY(event.getScreenY() - yPosition);
    }

    @FXML
    void LoginPanePressed(MouseEvent event) {
        xPosition = event.getSceneX();
        yPosition = event.getSceneY();
    }

}
