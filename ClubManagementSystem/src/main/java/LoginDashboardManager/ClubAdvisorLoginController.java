package LoginDashboardManager;

import DataBaseManager.ClubAdvisorDataBaseManager;
import SystemUsers.ClubAdvisor;
import SystemUsers.User;
import com.example.clubmanagementsystem.ApplicationController;
import com.example.clubmanagementsystem.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// work done by- Deelaka and Lakshan
public class ClubAdvisorLoginController {
    public static String userNameForShowInAdvisorDashboard;  // hold username to show in advisor dashboard when it loads
    static boolean loginStatus;  // uses to validate user entered username and password in login screen
    private String clubAdvisorLoginPageUserName; // holds user entered user name in login page
    private String clubAdvisorLoginPagePassword;  // hold user entered password in login page
    @FXML
    private StackPane clubAdvisorStackPane;
    private Scene scene;
    private Stage stage;

    private Parent root;

    private double xPosition;

    private double yPosition;

    @FXML
    private Button ClubAdvisorLoginMinimizer;
    @FXML
    private TextField advisorLoginUserName;
    @FXML
    private TextField advisorLoginPassword;

    @FXML
    private TextField advisorUserName;

    @FXML
    private PasswordField advisorPassword;

    @FXML
    private PasswordField advisorConfirmPassword;

    @FXML
    private TextField advisorLastName;

    @FXML
    private TextField advisorId;

    @FXML
    private TextField advisorFirstName;

    @FXML
    private TextField advisorContactNumber;

    @FXML
    private Label advisorIdLabel;

    @FXML
    private Label advisorFirstNameLabel;

    @FXML
    private Label advisorLastNameLabel;

    @FXML
    private Label contactNumberLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private Label clubAdvisorIncorrectCredential;
    @FXML
    private Label advisorUserNameEmpty;
    @FXML
    private Label advisorPasswordEmpty;


    @FXML
    private CheckBox showPassword;

    @FXML
    private PasswordField PasswordFieldLogin;

    @FXML
    private TextField PasswordTextField;

    @FXML
    private Label passwordCommentLogin;

    public static boolean validStat = true;
    // This method direct  the user to the main login page
    @FXML
    void DirectToStartPane(ActionEvent event) throws IOException { // method to direct user to back to main login page
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/clubmanagementsystem/Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // This method is responsible for dragging the login page
    @FXML
    void clubAdvisorPaneDragDetector(MouseEvent event) {
        // getting the current stage associated with the provided stackPane
        Stage stage =  (Stage)clubAdvisorStackPane.getScene().getWindow();
        // setting the stage position according to the mouse movement
        stage.setX(event.getScreenX()- xPosition);
        stage.setY(event.getScreenY() - yPosition);
    }

    // This method is responsible for pressing the login page
    @FXML
    void clubAdvisorPanePressDetector(MouseEvent event) {
        // getting the current stage associated with the provided stackPane
        xPosition = event.getSceneX();
        yPosition = event.getSceneY();
    }
    // This method is responsible for minimizing the login page
    @FXML
    void MinimizeClubAdvisorLogin(ActionEvent event) { // minimize button in registration page
        ApplicationController applicationController = new ApplicationController();
        //
        applicationController.MinimizeApp(clubAdvisorStackPane);
    }

    // This method is responsible for closing the login page
    @FXML
    void ClubAdvisorLoginExit(ActionEvent event) { // back button of student registration page
        ApplicationController applicationController = new ApplicationController();
        applicationController.closingApp();
    }

    // This method is responsible for showing the password
    @FXML
    void showTypedPassword(ActionEvent event) {
        if(showPassword.isSelected()){ // when user select show password checkbox
            advisorLoginPassword.setVisible(false);  //studentLoginPassword textfield will disable
            PasswordTextField.setVisible(true); // PasswordTextField textfield will enable
            PasswordTextField.setText(advisorLoginPassword.getText()); /* this will take the values from studnetLoginPassword
                                                                           textfield and will set to PasswordTextField*/
        }else{ // this will execute if user keep the checkbox as it is
            PasswordTextField.setVisible(false);
            advisorLoginPassword.setVisible(true);
            advisorLoginPassword.setText(PasswordTextField.getText());
        }
    }

    boolean fieldsChecker() {  /* this method is used to check whether both studentLoginPagePassword and studentLoginPageUserName field are
                                  empty or not */
        loginStatus = true;
        clubAdvisorLoginPageUserName = advisorLoginUserName.getText(); // receiving username from user in login page
        if(advisorLoginPassword.isVisible()){
            clubAdvisorLoginPagePassword = advisorLoginPassword.getText(); // receiving password from user in login page
        }else{
            clubAdvisorLoginPagePassword =  PasswordTextField.getText(); // receiving password from user in login page
        }

        userNameForShowInAdvisorDashboard = clubAdvisorLoginPageUserName;
        if(clubAdvisorLoginPageUserName.isEmpty()){ // if clubAdvisorLoginPageUserName is empty
            loginStatus = false;
            advisorUserNameEmpty.setText("This field cannot be empty");
        }
        if(clubAdvisorLoginPagePassword.isEmpty()){ // if clubAdvisorLoginPagePassword is empty
            loginStatus = false;
            advisorPasswordEmpty.setText("This field cannot be empty");
        }
        return loginStatus;
    }

    //advisorCredentialsChecker will check whether entered credentials are correct according to the given values
    boolean advisorCredentialsChecker() {
        ClubAdvisor advisor = new ClubAdvisor(clubAdvisorLoginPageUserName,clubAdvisorLoginPagePassword); /* creating an object
                                                                                                in Club advisor class */
        String correctPassword = advisor.LoginToSystem(); // calling to LoginToSystem method
        loginStatus = true;
        if(!clubAdvisorLoginPagePassword.equals(correctPassword)){ // entered password is incorrect, error label will be set
            loginStatus = false;
            clubAdvisorIncorrectCredential.setText("User name or Password Incorrect");
        }
        return loginStatus;
    }

    // This method
    @FXML
    public void DirectToClubAdvisorDashBoard(ActionEvent event) throws IOException, SQLException { // when user click on DirectToStudentDashboard button
        if(!fieldsChecker()){ // calling to fieldsChecker
            return;
        }
        advisorUserNameEmpty.setText(""); // clearing set values of respective labels
        advisorPasswordEmpty.setText(""); // clearing set values of respective labels

        if(!advisorCredentialsChecker()){
            return;
        }
        clubAdvisorIncorrectCredential.setText("");
        // creating an object in ClubAdvisorDataBaseManager class
        ClubAdvisorDataBaseManager clubAdvisorDataBaseManager = new ClubAdvisorDataBaseManager(userNameForShowInAdvisorDashboard);
        FXMLLoader loader = new FXMLLoader();
        // loading ClubAdvisorDashboard.fxml file
        loader.setLocation(getClass().getResource("/com/example/clubmanagementsystem/ClubAdvisorDashboard.fxml"));
        Parent root = loader. load();
        ClubAdvisorDashboardManager.ClubAdvisorActivityController clubAdvisorDashboardControlller = loader.getController();
        clubAdvisorDashboardControlller.showUserNameClubAdvisor.setText(userNameForShowInAdvisorDashboard); /* controller variable will get the access
                                                                                  to control student activity controller
                                                                                   to set username to dashboard
                                                                                   when it loads*/
        clubAdvisorDashboardControlller.showUserNameClubAdvisor.setStyle("-fx-text-alignment: center"); // centering username in dashboard
        clubAdvisorDashboardControlller.clubAdvisorId = clubAdvisorDataBaseManager.selectClubAdvisorId(userNameForShowInAdvisorDashboard);
        clubAdvisorDashboardControlller.dashboardButton.setStyle("-fx-background-color: linear-gradient(#fafada, #ffffd2);");
        clubAdvisorDashboardControlller.ViewEventButton.setStyle("-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779); " +
                "-fx-text-fill: white");
        clubAdvisorDashboardControlller.GoToClubMembershipButton.setStyle("-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779); " +
                "-fx-text-fill: white");
        clubAdvisorDashboardControlller.CreateClubDirectorButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    // This method is responsible for directing the user to the registration page
    @FXML
    void GoToRegisterForm(ActionEvent event) throws IOException { // direct user to club advisor registration page
        ClubAdvisor.advisorIdStatus = "";
        Parent root = FXMLLoader.load(getClass().getResource("/RegisterManager/ClubAdvisorRegistration.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // This method is responsible for directing the user to the login pane
    @FXML
    void DirectToLoginPane(MouseEvent event) throws IOException { // go to main login page
        this.goToLoginPage(event);
    }

    // This method is responsible for directing the user to the login page
    public void goToLoginPage(MouseEvent event) throws IOException { // direct user to advisor login page
        Parent root = FXMLLoader.load(getClass().getResource("/LoginDashboardManager/ClubAdvisorLogin.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // This method checks advisor details registration
    public void AdvisorRegistrationChecker(MouseEvent event) throws SQLException, IOException {
        validStat = true;
        // the entered details are retrieved into variables
        String firstName = this.advisorFirstName.getText();
        String lastName = this.advisorLastName.getText();
        String advisorId = this.advisorId.getText();
        String contactNum = this.advisorContactNumber.getText();
        String userName = this.advisorUserName.getText();
        String password = this.advisorPassword.getText();
        String confirmPassword = this.advisorConfirmPassword.getText();
        // an object called clubAdvisor is created of data type ClubAdvisor
        ClubAdvisor clubAdvisor = new ClubAdvisor(userName, password, firstName, lastName);
        // the  first name is validated using the validator interface
        if(!clubAdvisor.validateFirstName()){
            // the boolean value is set to false as there is an error
            validStat = false;
        }
        displayNameError("Fname");
        // the last name is validated using the validator interface
        if(!clubAdvisor.validateLastName()){
            validStat = false;  // the boolean value is set to false as there is an error
        }
        displayNameError("Lname"); //the error field is specified as the first and last names follow the same validation
        try {
            String tempContactVal = contactNum; // the contact number is stored in a temporary variable
            // check if the value is empty
            if(tempContactVal.isEmpty()){
                User.contactNumberValidateStatus = "empty";
                throw new Exception(); // general exception is thrown
            }
            Double.parseDouble(contactNum.trim()); // the string is converted to a double and it is trimmed
            ClubAdvisor cb = new ClubAdvisor(tempContactVal);// a new object is created of data type ClubAdvisor with only the temporary holder as the parameter
            // the contact number is validated
            if (!cb.validateContactNumber()) {
                validStat = false;  // the boolean value is set to false as there is an error
            }else{
                User.contactNumberValidateStatus ="";
            }
            // catching number format exceptions
        }catch(NumberFormatException e){
            User.contactNumberValidateStatus = "format";
            validStat = false;
        } catch (Exception e) {
            validStat = false;
        }
        displayContactValError();  // the error method is called to specify what type of error is produced
        try {
            if(advisorId.isEmpty()){ // if advisor id is empty
                validStat = false;
                ClubAdvisor.advisorIdStatus ="empty"; // advisorIdStatus will set to "empty"
                throw new Exception(); // general exception
            }
            int advisorIdValue = Integer.parseInt(advisorId); // converting advisor ID to an integer
            ClubAdvisor cb2 = new ClubAdvisor(advisorIdValue); // creating an object to validate advisor ID

            if (!cb2.validateClubAdvisorId()) {  // if advisor ID is not under system validation standards
                validStat = false;  // validStat will be false
            }else{
                ClubAdvisor.advisorIdStatus = "";  // when entered advisor ID has no issues
            }
        }catch(NumberFormatException e){ // when entered advisor ID got unknown errors in number format
            ClubAdvisor.advisorIdStatus ="format"; // advisorIdStatus will set to "format"
            validStat = false;
        }
        catch (Exception e) { // if unknown errors occur
            validStat = false;
        }
        displayIdError();
        if(!clubAdvisor.validateUserName("registration", "clubAdvisor")){  /* passing parameters to validateUserName method,
                                                      and if username did not meet system standards*/
            validStat = false;
        }else{
            User.userNameValidateStatus = ""; // when entered username is valid
        }
        displayUserNameError(); // calling to displayUserNameError method

        if(!clubAdvisor.validatePassword("registration")){  /* passing values to validatePassword method, and if
                                                                   password didn't meet system standards */
            validStat = false;
        }else{
            User.passwordValidateStatus = ""; // when entered password is valid
        }
        displayPasswordError();

        if(confirmPassword.isEmpty()){  // when confirm password is empty
            validStat = false; // setting validStat to false
            confirmPasswordLabel.setText("Confirm password cannot be empty");/* setting an error label to
                                                                                    confirmPasswordLabel */
        } else if (!confirmPassword.equals(password)){ // when entered passwords are not matching with each other
            confirmPasswordLabel.setText("Passwords are not matching"); /* setting an error label to
                                                            studentRegisterConfirmPasswordErrorLabel saying passwords are not matching */
            validStat = false;
        }else{
            confirmPasswordLabel.setText(" "); // when entered password has no issues
        }
        if(validStat){ // if there is no issue in entered data
            ClubAdvisor clubAdvisorData = new ClubAdvisor(userName, password, firstName, lastName, contactNum, Integer.parseInt(advisorId));
            ClubAdvisor.clubAdvisorDetailsList.add(clubAdvisorData);

            clubAdvisorData.registerToSystem();
            // delaying the alert window process, in order make it user friendly
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println(e);
            }
            // showing alert window to user that has successfully registered with the system
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("School Club Management System");
            alert.setHeaderText("You have successfully registered with the system !!!");
            alert.showAndWait();

            this.goToLoginPage(event);
        }
    }
    public void displayNameError(String nameType){ // entered name checking
        if(nameType.equals("Fname")){  // checking first name
            if(ClubAdvisor.fNameValidateStatus.equals("empty")){  // if first name field is empty
                advisorFirstNameLabel.setText("First name cannot be empty"); // setting an error label to advisorFirstNameLabel
            }else if(ClubAdvisor.fNameValidateStatus.equals("format")){ // if first name field contain invalid characters
                advisorFirstNameLabel.setText("First name can contain only letters"); /* setting an error label to
                                                                                        advisorFirstNameLabel */
            }else{
                advisorFirstNameLabel.setText("");  // when user correctly enter first name
            }
        }else if (nameType.equals("Lname")){ // checking last name
            if(ClubAdvisor.lNameValidateStatus.equals("empty")){ // if last name field is empty
                advisorLastNameLabel.setText("Last name cannot be empty"); // setting an error label to advisorLastNameLabel
            }else if(ClubAdvisor.lNameValidateStatus.equals("format")){
                advisorLastNameLabel.setText("Last name can contain only letters");
            }else{
                advisorLastNameLabel.setText(""); // when user correctly enter last name
            }
        }
    }
    public void displayIdError(){ // admission number checking
        if(ClubAdvisor.advisorIdStatus.equals("empty")){ // when admission number field is empty
            advisorIdLabel.setText("Advisor Id cannot be empty.");
        }else if(ClubAdvisor.advisorIdStatus.equals("length")){ // when admission number length is not valid
            advisorIdLabel.setText("Advisor Id should have 6 digits.");
        }else if(ClubAdvisor.advisorIdStatus.equals("exist")){ // when user enter an existed admission number
            advisorIdLabel.setText("Advisor Id already exists.");
        }else if(ClubAdvisor.advisorIdStatus.equals("format")){
            advisorIdLabel.setText("Advisor Id contain only numbers."); // when user enter admission number correctly
        }else{
            advisorIdLabel.setText("");
        }
    }
    public void displayUserNameError(){  // username checking
        if(User.userNameValidateStatus.equals("empty")){ // when username field is empty
            userNameLabel.setText("User name cannot be empty");
        }else if(User.userNameValidateStatus.equals("exist")){ // when user enter an existed admission number
            userNameLabel.setText("User name already exists");
        }else if(User.userNameValidateStatus.equals("blank")){ // when username contain spaces
            userNameLabel.setText("User name cannot contain spaces");
        } else if (User.userNameValidateStatus.equals("length")) { // when username is not lengthier enough
            userNameLabel.setText("length should be 5 to 10 digits");
        }else{
            userNameLabel.setText(""); // when entered username is correct
        }
    }
    public void displayPasswordError(){  // password checking
        if(User.passwordValidateStatus.equals("empty")){ // when password field is empty
            passwordLabel.setText("Password cannot be empty");
        }else if(User.passwordValidateStatus.equals("format")){ // when user entered password is not strong enough
            // here we are splitting the sentence into two lines, in order to set the label correctly
            passwordLabel.setText("""
                    Password should consist with at least 8
                    characters including numbers and special
                    characters""");
            passwordLabel.setStyle("-fx-text-alignment: justify;");
        }else{
            passwordLabel.setText("");  // when user entered password is valid
        }
    }
    public void displayContactValError(){  // contact number checking
        if(User.contactNumberValidateStatus.equals("empty")){ // when contact number field is empty
            contactNumberLabel.setText("Contact Number cannot be empty");
        } else if (User.contactNumberValidateStatus.equals("length")) {  // when entered contact number is not a valid number
            contactNumberLabel.setText("Contact Number should have 10 numbers.");
        }else if(User.contactNumberValidateStatus.equals("format")){  // when entered contact number has string values
            contactNumberLabel.setText("Number cannot contain characters.");
        }else{
            contactNumberLabel.setText(""); // when entered contact is correct
        }
    }
}