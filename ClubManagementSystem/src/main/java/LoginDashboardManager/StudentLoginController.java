package LoginDashboardManager;

import DataBaseManager.StudentDataBaseManager;
import StudentDashboardManager.StudentActivityController;
import StudentDashboardManager.StudentDashboardController;
import SystemUsers.ClubAdvisor;
import SystemUsers.Student;
import SystemUsers.User;
import com.example.clubmanagementsystem.ApplicationController;
import com.example.clubmanagementsystem.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.clubmanagementsystem.HelloApplication.statement;


public class StudentLoginController {
    private static String selectedGradeVal; // holds the user selected grade
    private static String selcetedGenderVal; // holds the user selected gender
    private int grade; // hold the grade from Grade combo box
    private String gender; // hold the gender from the Gender combo box
    public static boolean validateStatus = true; // uses to validation purposes of admission number, name, contact number etc
    static boolean loginStatus; // uses to validate user entered username and password in login screen
    String studentLoginPageUserName; // holds user entered user name in login page
    String studentLoginPagePassword; // hold user entered password in login page
    public  static String userNameForShowInStudentDashboard;
    private Scene scene;
    private Stage stage;
    private double xPosition;
    private double yPosition;

    @FXML
    private Label studentLoginUserNameErrorLabel;

    @FXML
    private Label studentRegisterConfirmPasswordErrorLabel;

    @FXML
    private Label studentIncorrectCredential;

    @FXML
    private Label studentLoginPasswordErrorLabel;

    @FXML
    private Label studentRegistrationGradeEmptyLabel;

    @FXML
    private Label studentRegistrationGenderEmptyLabel;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private StackPane StudentLoginForm;

    @FXML
    private PasswordField studentRegisterPassword;

    @FXML
    private PasswordField studentRegisterConfirmPassword;

    @FXML
    private TextField studentRegisterLastName;

    @FXML
    private TextField PasswordTextField;

    @FXML
    private TextField studentRegisterAdmissionNumber;

    @FXML
    private TextField studentRegisterFirstName;

    @FXML
    private TextField studentRegisterContactNumber;

    @FXML
    private TextField studentRegisterUserName;

    @FXML
    private TextField LoginStudentUserName;

    @FXML
    private TextField studentLoginPassword;

    @FXML
    private ComboBox<String> Grade;

    @FXML
    private ComboBox<String> Gender;

    @FXML
    private Label studentRegisterAdmissionNumErrorLabel;

    @FXML
    private Label studentRegisterFNameErrorLabel;

    @FXML
    private Label studentRegisterLNameErrorLabel;

    @FXML
    private Label studentRegisterContactNumErrorLabel;

    @FXML
    private Label studentRegisterUserNameErrorLabel;

    @FXML
    private Label studentRegisterPasswordErrorLabel;

    // work done by- Lakshan
    @FXML
    void DirectToStartPage(ActionEvent event) throws IOException { // method to direct user to back to main login page
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/clubmanagementsystem/Login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // work done by- Lakshan
    @FXML
    void StudentLoginPaneDragDetected(MouseEvent event) {
        Stage stage = (Stage) StudentLoginForm.getScene().getWindow();
        stage.setX(event.getScreenX() - xPosition);
        stage.setY(event.getScreenY() - yPosition);
    }

    // work done by- Lakshan
    @FXML
    void studentLoginPanePressedDetected(MouseEvent event) {
        xPosition = event.getSceneX();
        yPosition = event.getSceneY();
    }

    // work done by- Lakshan
    @FXML
    void minimizeTheProgram(ActionEvent event) { // minimize button in registration page
        ApplicationController applicationController = new ApplicationController();
        applicationController.MinimizeApp(StudentLoginForm);
    }

    // work done by- Lakshan
    @FXML
    void ExitTheProgram(ActionEvent event) { // back button of student registration page
        ApplicationController applicationController = new ApplicationController();
        applicationController.closingApp();
    }

    // work done by- Deelaka
    boolean fieldsChecker() { /* this method is used to check whether both studentLoginPagePassword and studentLoginPageUserName field are
                                  empty or not */
        loginStatus = true;
        studentLoginPageUserName = LoginStudentUserName.getText(); // receiving username from user in login page
        if(studentLoginPassword.isVisible()){
            studentLoginPagePassword = studentLoginPassword.getText(); // receiving password from user in login page
        }else{
            studentLoginPagePassword =  PasswordTextField.getText(); // receiving password from user in login page
        }

        userNameForShowInStudentDashboard = studentLoginPageUserName;
        if (studentLoginPageUserName.isEmpty()) { // if username is empty, error label will be set
            System.out.println("Empty user name !!!");
            loginStatus = false;
            studentLoginUserNameErrorLabel.setText("This field cannot be empty");
        }
        if (studentLoginPagePassword.isEmpty()) { // if password is field is empty, error label will be set
            loginStatus = false; // loginStatus will be false
            studentLoginPasswordErrorLabel.setText("This field cannot be empty");
            System.out.println("Empty password !!!");
        }
        return loginStatus;
    }

    // work done by- Deelaka
    //studentCredentialChecker will check whether entered credentials are correct according to the given values
    boolean studentCredentialChecker() { /* this method will check, whether entered username and password are correct
                                            according to the existing values*/
       Student student = new Student(studentLoginPageUserName, studentLoginPagePassword);
       String correctPassword = student.LoginToSystem(); // calling studentLoginToSystem method
        loginStatus = true;
        if (!studentLoginPagePassword.equals(correctPassword)) { // entered password is incorrect, error label will be set
            loginStatus = false;
            studentIncorrectCredential.setText("User name or Password Incorrect");
        }
        return loginStatus;
    }

    // work done by- Deelaka
    public void showTypedPassword() {
        if (showPasswordCheckBox.isSelected()) { // when user select show password checkbox
            studentLoginPassword.setVisible(false); //studentLoginPassword textfield will disable
            PasswordTextField.setVisible(true); // PasswordTextField textfield will enable
            PasswordTextField.setText(studentLoginPassword.getText()); /* this will take the values from studnetLoginPassword
                                                                           textfield and will set to PasswordTextField*/
        } else { // this will execute if user keep the checkbox as it is
            PasswordTextField.setVisible(false);
            studentLoginPassword.setVisible(true);
            studentLoginPassword.setText(PasswordTextField.getText());
        }
    }


    // work done by- Deelaka
    @FXML
    void DirectToStudentDashboard(ActionEvent event) throws IOException, SQLException { // when user click on

        if (!fieldsChecker()) { // calling to fieldsChecker
            return;
        }
        studentLoginUserNameErrorLabel.setText(""); // clearing set values of respective labels
        studentLoginPasswordErrorLabel.setText("");  // clearing set values of respective labels

        if (!studentCredentialChecker()) { // calling studentCredentialChecker method
            return;
        }
        studentLoginPasswordErrorLabel.setText("");
        System.out.println("Directing to student dashboard");

        StudentDataBaseManager studentDataBaseManager = new StudentDataBaseManager(userNameForShowInStudentDashboard); // this is the place data load form the database
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/clubmanagementsystem/StudentDashboard.fxml"));
        Parent root = loader.load();
        StudentActivityController controller = loader.getController(); // This is done to set login userName to dashboard
        controller.showUserName.setText(userNameForShowInStudentDashboard); // controller variable will get the access to control student activity controller
        controller.showUserName.setStyle("-fx-text-alignment: center");
        controller.displayEventCountPerClub();
        controller.studentAdmission = studentDataBaseManager.getStudentAdmissionNum(userNameForShowInStudentDashboard);
        StudentDashboardManager.StudentActivityController studentDashboardController = loader.getController();
        studentDashboardController.dashboardButton.setStyle("-fx-background-color: linear-gradient(#fafada, #ffffd2);");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1100, 600);
        stage.setScene(scene);
        stage.centerOnScreen();


        stage.show();
    }

    // work done by- Lakshan
    @FXML
    void GoToStudentRegistration(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegisterManager/StudentRegistration.fxml"));
        Parent root = loader.load();
        StudentLoginController controller = loader.getController();
        controller.setComboBoxValuesStudentRegistration(); // giving controller access to  student Login controller
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
    }


    // work done by- Lakshan
    @FXML
    void DirectToLoginPane(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/LoginDashboardManager/StudentLogin.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // work done by- Deelaka and Arkhash
    @FXML
    public void StudentRegistrationChecker(MouseEvent event) throws SQLException, IOException {
        validateStatus = true; //a boolean value is set to true initially

        // the entered details are retrieved into variables
        String firstName = this.studentRegisterFirstName.getText();
        String lastName = this.studentRegisterLastName.getText();
        String admissionNum = this.studentRegisterAdmissionNumber.getText();
        String contactNum = this.studentRegisterContactNumber.getText();


//        String gender = this.Gender.getValue();

        System.out.println("Grade is "+ grade);
        System.out.println("Gender is "+ gender);

        String userName = this.studentRegisterUserName.getText();
        String password = this.studentRegisterPassword.getText();
        String passwordConfirm = this.studentRegisterConfirmPassword.getText();

        System.out.println(admissionNum);
        // an object called student is created of data type Student
        Student student = new Student(userName, password, firstName, lastName);

        // the  first name is validated using the validator interface
        if (!student.validateFirstName()) {
            System.out.println("Wrong first name");
            validateStatus = false; // the boolean value is set to false as there is an error
        }
        displayNameError("FName"); //the error field is specified as the first and last names follow the same validation


        // the last name is validated using the validator interface
        if (!student.validateLastName()) {
            System.out.println("Wrong last name");
            validateStatus = false; // the boolean value is set to false as there is an error
        }
        displayNameError("LName"); //the error field is specified as the first and last names follow the same validation

        try {
            String tempContactVal = contactNum; // the contact number is stored in a temporary variable

            // check if the value is empty
            if (tempContactVal.isEmpty()) {
                User.contactNumberValidateStatus = "empty";
                throw new Exception(); // exception is thrown
            }
            Double.parseDouble(contactNum.trim()); // the string is converted to a double and it is trimmed
            Student std1 = new Student(tempContactVal); // a new object is created of data type Student with only the temporary holder as the parameter

            // the contact number is validated
            if (!std1.validateContactNumber()) {
                validateStatus = false; // the boolean value is set to false as there is an error
                System.out.println("Invalid Contact Number 1");
            } else {
                User.contactNumberValidateStatus = "";
            }

        } catch (NumberFormatException e) {

            System.out.println("Invalid ContactNumber 2");
            User.contactNumberValidateStatus = "format";
            validateStatus = false; // the boolean value is set to false as there is an error

        } catch (Exception e) {
            validateStatus = false; // the boolean value is set to false as there is an error
        }
        displayContactValError(); // the error method is called to specify what type of error is produced

        try {
            if(admissionNum.isEmpty()){
                validateStatus = false;
                Student.admissionNumStatus ="empty";
                throw new Exception();
            }

            int advisorIdValue = Integer.parseInt(admissionNum.trim());
            Student studentVal = new Student(advisorIdValue);

            if (!studentVal.validateStudentAdmissionNumber()) {
                validateStatus  = false;
            }else{
                Student.admissionNumStatus = "";
            }
        }catch(NumberFormatException e){
            Student.admissionNumStatus ="format";
            System.out.println("Invalid Advisor Id");
            validateStatus  = false;
        }
        catch (Exception e) {
            validateStatus = false;
        }
        displayAdmissionNumError();



        if (!student.validateUserName("registration", "student")) {
            System.out.println("Wrong user name");
            validateStatus = false;
        } else {
            User.userNameValidateStatus = "";
        }
        displayUserNameError();

        if (!student.validatePassword("registration")) {
            System.out.println("Wrong password");
            validateStatus = false;
        } else {
            User.passwordValidateStatus = "";
        }
        displayPasswordError();


        if (passwordConfirm.isEmpty()) {
            validateStatus = false;
            studentRegisterConfirmPasswordErrorLabel.setText("Confirm Password cannot be empty");
        } else if (!passwordConfirm.equals(password)) {
            studentRegisterConfirmPasswordErrorLabel.setText("Passwords are not matching");
            validateStatus = false;
        } else {
            studentRegisterConfirmPasswordErrorLabel.setText(" ");
        }

        if(selcetedGenderVal.equals("Select Gender")){
            studentRegistrationGenderEmptyLabel.setText("Please select your gender");
            validateStatus = false;
        }

        if(selectedGradeVal.equals("Select Grade")){
            studentRegistrationGradeEmptyLabel.setText("Please select your grade");
            validateStatus = false;
        }


        System.out.println(validateStatus + " : Valid Stat");
        if (validateStatus) {
            Student studentData = new Student(userName, password, firstName, lastName);
            Student.studentDetailArray.add(studentData);


            String studentPersonalDetailsQuery = "INSERT INTO Student (studentAdmissionNum, studentFName, studentLName, " +
                    "studentGrade, studentContactNum, Gender) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(studentPersonalDetailsQuery)) {
                preparedStatement.setInt(1, Integer.parseInt(admissionNum));
                preparedStatement.setString(2, firstName);
                preparedStatement.setString(3, lastName);
                preparedStatement.setInt(4, grade); // Assuming grade is an INT
                preparedStatement.setInt(5, Integer.parseInt(contactNum));
                preparedStatement.setString(6, gender);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error 1");
                System.out.println(e);
            }

            String studentCredentialsDetailsQuery = "INSERT INTO studentCredentials (studentUserName," +
                    " studentPassword, studentAdmissionNum) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(studentCredentialsDetailsQuery)) {
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, passwordConfirm);
                preparedStatement.setInt(3, Integer.parseInt(admissionNum));
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error 2");
                System.out.println(e);
            }

            try{
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println(e);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("School Club Management System");
            alert.setHeaderText("You have successfully registered with the system !!!");
            alert.showAndWait();


            this.DirectToLoginPane(event);
        }
        System.out.println("\n\n\n");


    }


    @FXML
    public void studentUpdateChecker(MouseEvent mouseEvent) throws SQLException, IOException {


    }



    // work done by- Arkhash and Deelaka
    public void displayNameError(String nameType) {
        if (nameType.equals("FName")) {
            if (Student.fNameValidateStatus.equals("empty")) {
                studentRegisterFNameErrorLabel.setText("First Name cannot be empty.");
            } else if (Student.fNameValidateStatus.equals("format")) {
                studentRegisterFNameErrorLabel.setText("First Name can only contain letters.");
            } else {
                studentRegisterFNameErrorLabel.setText("");
            }
        } else if (nameType.equals("LName")) {
            if (Student.lNameValidateStatus.equals("empty")) {
                studentRegisterLNameErrorLabel.setText("Last Name cannot be empty.");
            } else if (Student.lNameValidateStatus.equals("format")) {
                studentRegisterLNameErrorLabel.setText("Last Name can contain only letters.");
            } else {
                studentRegisterLNameErrorLabel.setText("");
            }
        }
    }

    // work done by- Arkhash and Deelaka
    public void displayAdmissionNumError() {
        if (Student.admissionNumStatus.equals("empty")) {
            studentRegisterAdmissionNumErrorLabel.setText("Admission Number cannot be empty.");
        } else if (Student.admissionNumStatus.equals("length")) {
            studentRegisterAdmissionNumErrorLabel.setText("Admission Number has to be 6 digits.");
        } else if (Student.admissionNumStatus.equals("exist")) {
            studentRegisterAdmissionNumErrorLabel.setText("Admission Number already exists.");
        } else if (Student.admissionNumStatus.equals("format")) {
            studentRegisterAdmissionNumErrorLabel.setText("Admission Number contain only numbers.");
        } else {
            studentRegisterAdmissionNumErrorLabel.setText("");
        }
    }


    // work done by- Arkhash and Deelaka
    public void displayPasswordError() {
        if (User.passwordValidateStatus.equals("empty")) {
            studentRegisterPasswordErrorLabel.setText("Password cannot be empty.");
        } else if (User.passwordValidateStatus.equals("format")) {
            // here we are splitting the sentence into two lines
            studentRegisterPasswordErrorLabel.setText(""" 
                    Password should consist of 8 characters
                    including numbers and special characters.""");
            studentRegisterPasswordErrorLabel.setStyle("-fx-text-alignment: justify;");
        } else {
            studentRegisterPasswordErrorLabel.setText("");
        }
    }

    // work done by- Deelaka
    public void setComboBoxValuesStudentRegistration(){
        Grade.getItems().add("Select Grade");
        for (int ComboGrade = 6; ComboGrade<14; ComboGrade++) {
            Grade.getItems().add((String.valueOf(ComboGrade)));
        }
        Grade.getSelectionModel().selectFirst();
        selectedGradeVal = "Select Grade";
       Gender.getItems().addAll("Select Gender","M", "F");
       Gender.getSelectionModel().selectFirst();
       selcetedGenderVal = "Select Gender";

       Grade.setOnAction(event -> validateGradeSelection());
       Gender.setOnAction(event -> validateGenderSelection());
    }

    // work done by- Deelaka
    private int validateGradeSelection() {
        selectedGradeVal = Grade.getValue();
        String selectedGrade = Grade.getValue();

        if (selectedGradeVal == "Select Grade") {
            System.out.println("Came to please select your grade line");
            studentRegistrationGradeEmptyLabel.setText("Please select your grade");
        } else {
            studentRegistrationGradeEmptyLabel.setText("");
            grade = Integer.parseInt(this.Grade.getValue());
            return grade;
        }
        return grade;
    }

    // work done by- Deelaka
    private String validateGenderSelection() {
        selcetedGenderVal = Gender.getValue();
        String selectedGender = Gender.getValue();

        if (selcetedGenderVal == "Select Gender") {
            System.out.println("Came to please select your gender line");
            studentRegistrationGenderEmptyLabel.setText("Please select your gender");
        } else {
            studentRegistrationGenderEmptyLabel.setText("");
            gender = this.Gender.getValue();
            // Both Grade and Gender are selected, continue with your logic
            System.out.println("Gender is "+ gender);
            return gender;
        }
        return gender;
    }


    // work done by- Deelaka
    public void displayUserNameError() {
        if (User.userNameValidateStatus.equals("empty")) {
            studentRegisterUserNameErrorLabel.setText("User Name cannot be empty");
        } else if (User.userNameValidateStatus.equals("exist")) {
            studentRegisterUserNameErrorLabel.setText("Entered Username already exists");
        } else if (User.userNameValidateStatus.equals("blank")) {
            studentRegisterUserNameErrorLabel.setText("User Name cannot contain spaces");
        } else if (User.userNameValidateStatus.equals("length")) {
            studentRegisterUserNameErrorLabel.setText("The length should be 5 to 10 characters.");
        } else {
            studentRegisterUserNameErrorLabel.setText("");
        }
    }

    // work done by- Deelaka
    public void displayContactValError() {
        if (User.contactNumberValidateStatus.equals("empty")) {
            studentRegisterContactNumErrorLabel.setText("Contact Number cannot be empty.");
        } else if (User.contactNumberValidateStatus.equals("length")) {
            studentRegisterContactNumErrorLabel.setText("Contact Number should have 10 digits.");
        } else if (User.contactNumberValidateStatus.equals("format")) {
            studentRegisterContactNumErrorLabel.setText("Number cannot contain characters.");
        } else {
            studentRegisterContactNumErrorLabel.setText("");
        }
    }
}