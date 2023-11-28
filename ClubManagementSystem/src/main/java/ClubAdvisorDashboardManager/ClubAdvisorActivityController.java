package ClubAdvisorDashboardManager;

import ClubManager.Club;
import DataBaseManager.ClubAdvisorDataBaseManager;
import SystemUsers.ClubAdvisor;
import SystemUsers.Student;
import SystemUsers.User;
import com.example.clubmanagementsystem.ApplicationController;
import ClubManager.Attendance;
import ClubManager.Event;
import ClubManager.EventManager;
import com.example.clubmanagementsystem.HelloApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.*;
import static ClubManager.Club.clubDetailsList;
import static SystemUsers.ClubAdvisor.clubAdvisorDetailsList;
import static SystemUsers.Student.studentDetailArray;

import java.time.LocalDate;
import java.time.LocalTime;


public class ClubAdvisorActivityController extends ClubAdvisorDashboardControlller {

    private int numberofAdvisors; // hold the count of club advisors
    private int numbeOfStudents; // hold the count of student
    public static String username; // Holds the username of the current user
    private static String selectedUser; // holds the selected usertype from registrationUserSelectComboBox
    public static boolean validStat = true;  // Represents the validation status, initialized as true
    public static int selectedEventId; // Holds the ID of the selected event
    public static Event selectedEventValue; // Holds the details of the selected event
    final FileChooser fileChooser = new FileChooser(); // FileChooser for handling file-related operations
    public static String imagePath;  // Holds the file path for an image
    public static int clubIdSetterValue; // Static value for setting club IDs
    public LocalDate selectedUpcomingDate; // Represents the selected upcoming date
    public LocalDate selectedMostFutureDate;  // Represents the selected most future date

    // work done by- Arkhash, Deelaka, Lakshan and Pramuditha
    // This method initializes all variables and call methods when loading club advisor dashboard
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        selectUserGettingFromComboBox();

        //Setting the values to the combo box in Club membership report
        populateMembershipCombo(clubMembershipCombo);
        //Setting up the club members details table columns
        memberAdmissionNumber.setCellValueFactory(new PropertyValueFactory<>("studentAdmissionNum"));
        memberUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        memberFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        memberLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        memberGrade.setCellValueFactory(new PropertyValueFactory<>("studentGrade"));
        memberGender.setCellValueFactory(new PropertyValueFactory<>("studentGender"));
        memberContactNumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        // setting values to generate report registration advisor table, in order load these values when Generate Page is loading
        registrationAdvisorID.setCellValueFactory(new PropertyValueFactory<>("clubAdvisorId")); // setting values to registrationAdvisorID column
        registrationAdvisorUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        registrationAdvisorFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        registrationAdvisorLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        registrationAdvisorContactNumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        registrationAdvisorTable.setVisible(true);  // loading registrationAdvisorTable table when the respective FXML is loading
        populateClubAdvisorTable();
        registrationStudentTable.setVisible(false);

        // make text fields not editable
        scheduleEventDatePicker.setEditable(false);
        updateEventDateDatePicker.setEditable(false);

        // populate combo boxes
        populateComboBoxes();
        // display male female student count dashboard
        findMaleFemaleStudentCount();
        // display enrolled student count
        displayEnrolledStudentCount();

        //Setting up values for the columns of the Create Club Table
        // display number of club advisors
        displayNumberOfClubAdvisors();
        // display generate report combo box to select clubs
        populateGenerateReportClubs(generateReportClubNameComboBox);
        // populate all generate report event tables
        populateGenerateReportEventsTable();

        // Set the membership table in Club Advisor Report
        setMembershipTable();
        //Set cell value factories for the columns of the Create Club Table
        createClubTableId.setCellValueFactory(new PropertyValueFactory<>("clubId"));
        createClubTableName.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        createClubTableDescription.setCellValueFactory(new PropertyValueFactory<>("clubDescription"));
        createClubTableLogo.setCellValueFactory(new PropertyValueFactory<>("absoluteImage"));

        // the columns are initialized for the attendance tracking table
        attendanceClubNameColumn.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        attendanceEventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        attendanceStudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        attendanceStudentAdmissionNumColumn.setCellValueFactory(new PropertyValueFactory<>("studentAdmissionNum"));
        attendanceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("attendanceStatus"));

        //Set cell value factories for the columns of the Update Club  Table
        updateClubTableId.setCellValueFactory(new PropertyValueFactory<>("clubId"));
        updateClubTableName.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        updateClubTableDescription.setCellValueFactory(new PropertyValueFactory<>("clubDescription"));
        updateClubTableLogo.setCellValueFactory(new PropertyValueFactory<>("absoluteImage"));

        // display the number of scheduled events in dashboard
        displayNumberOfScheduledEvents();
        // display the next event date in dashboard
        getNextEventDate();
        // display the student update details
        displayStudentUpdateDetails();

    }

    // work done by- Lakshan
    // this method populate the combo boxes with entity types  and its tables
    public void populateComboBoxes() {
        // Initialize the event related combo boxes
        scheduleEventTypeCombo.getItems().addAll("None", "Meeting", "Activity");
        scheduleEventTypeCombo.getSelectionModel().selectFirst();
        ScheduleEventsDeliveryType.getItems().addAll("None", "Online", "Physical");
        ScheduleEventsDeliveryType.getSelectionModel().selectFirst();
        updateEventTypeCombo.getItems().addAll("None", "Meeting", "Activity");
        updateEventTypeCombo.getSelectionModel().selectFirst();
        updateEventDeliveryTypeCombo.getItems().addAll("None", "Online", "Physical");
        updateEventDeliveryTypeCombo.getSelectionModel().selectFirst();

        // Populates ComboBoxes with hours
        for (int hour = 0; hour < 24; hour++) {
            updateHourComboBox.getItems().add(String.format("%02d", hour));
        }
        updateHourComboBox.getSelectionModel().selectFirst();

        for (int minutes = 0; minutes < 60; minutes++) {
            updateMinuteComboBox.getItems().add(String.format("%02d", minutes));
        }
        updateMinuteComboBox.getSelectionModel().selectFirst();

        for (int hour = 0; hour < 24; hour++) {
            scheduleEventHour.getItems().add(String.format("%02d", hour));
        }
        scheduleEventHour.getSelectionModel().selectFirst();


        for (int minutes = 0; minutes < 60; minutes++) {
            scheduleEventMinutes.getItems().add(String.format("%02d", minutes));
        }
        scheduleEventMinutes.getSelectionModel().selectFirst();

        // set cell value factories for schedule events table
        createEventClubNameColumn.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        createEventEventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        createEventEventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        createEventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("eventLocation"));
        createEventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        createEventDeliveryTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventDeliveryType"));
        createEventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("eventDescription"));
        createEventTimeColumn.setCellValueFactory(new PropertyValueFactory<>("eventTime"));

        // set cell value factories for update events table
        updateClubNameColumn.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        updateEventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        updateEventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        updateEventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("eventLocation"));
        updateEventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        updateDeliveryTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventDeliveryType"));
        updateEventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("eventDescription"));
        updateEventTimeColumn.setCellValueFactory(new PropertyValueFactory<>("eventTime"));

        // set cell value factories for cancel events table
        cancelEventClubNameColumn.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        cancelEventEventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        cancelEventEventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        cancelEventEventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("eventLocation"));
        cancelEventEventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        cancelEventDeliveryTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventDeliveryType"));
        cancelEventEventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("eventDescription"));
        cancelEventTimeColumn.setCellValueFactory(new PropertyValueFactory<>("eventTime"));

        // set cell value factories for view events table
        viewEventClubNameColumn.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        viewEventEventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        viewEventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        viewEventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("eventLocation"));
        viewEventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        viewEventDeliveryTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventDeliveryType"));
        viewEventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("eventDescription"));
        viewEventTimeColumn.setCellValueFactory(new PropertyValueFactory<>("eventTime"));

//        atColumn.setCellValueFactory(new PropertyValueFactory<>("attendanceStatus"));
//        stColumn.setCellValueFactory(new PropertyValueFactory<>("attendanceTracker"));

        // set cell value factories for generate reports event table
        generateReportClubName.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        generateReportEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        generateReportEventDate.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        generateReportEventTime.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        generateReportEventLocation.setCellValueFactory(new PropertyValueFactory<>("eventLocation"));
        generateReportEventType.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        generateReportDeliveryType.setCellValueFactory(new PropertyValueFactory<>("eventDeliveryType"));
        generateReportEventDescription.setCellValueFactory(new PropertyValueFactory<>("eventDescription"));


        // Set cell value factories for GenerateReportAttendanceTable
        generateReportAttendanceClubName.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        generateReportAttendanceEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        generateReportAttendanceAdmissionNum.setCellValueFactory(new PropertyValueFactory<>("studentAdmissionNum"));
        generateReportAttendanceStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        generateReportAttendanceStatus.setCellValueFactory(new PropertyValueFactory<>("nameAttendanceStatus"));

    }

    // work done by- Pramuditha
    public void setCreateTable() {
        // Check whether the sortedList is null and return the method, if it is null
        if (clubDetailsList == null) {
            return;
        }
        // Clear the Created Clubs Table
        createClubDetailsTable.getItems().clear();
        // Add Club details to the Created Clubs Table using an observable list
        for(Club club : clubDetailsList) {
            // Create a Club object with the Club details
            Club tableClub = new Club(club.getClubId() , String.valueOf(club.getClubName()) , String.valueOf(club.getClubDescription()) , String.valueOf(club.getClubLogo()));

            // Add the Club details to the Created Clubs Table
            ObservableList<Club> observableCreateClubList = createClubDetailsTable.getItems();
            observableCreateClubList.add(tableClub);
            createClubDetailsTable.setItems(observableCreateClubList);
        }
    }

    // work done by- Pramuditha
    public void setUpdateTable() {
        // Check whether the sortedList is null and return the method, if it is null
        if (clubDetailsList == null) {
            return;
        }
        // Clear the Club Update Table
        updateClubDetailsTable.getItems().clear();

        // Add Item details to the UpdateView Table using Sorted List
        for (Club club : clubDetailsList) {

            // Create an Item details object with the item details
            Club tableClub = new Club(club.getClubId(), String.valueOf(club.getClubName()), String.valueOf(club.getClubDescription()), String.valueOf(club.getClubLogo()));
            //Add the Club details to the Update Clubs Table
            ObservableList<Club> observableUpdateClubList = updateClubDetailsTable.getItems();
            observableUpdateClubList.add(tableClub);
            updateClubDetailsTable.setItems(observableUpdateClubList);
        }
    }

    // work done by- Lakshan
    // This method is responsible on populating various event tables with data from Event.event details list
    public void populateEventsTables() {
        // Check if Event.eventDetails is null, if it is return without populating tables
        if (Event.eventDetails == null) {
            return;
        }

        // Clear currently populated items in each TableView to prepare for population
        scheduleCreatedEventTable.getItems().clear();
        updateEventTable.getItems().clear();
        cancelEventTable.getItems().clear();
        viewCreatedEventsTable.getItems().clear();
        generateReportEventViewTable.getItems().clear();

        // Setting the table column resize policy to view the tables
        scheduleCreatedEventTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        updateEventTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        cancelEventTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        viewCreatedEventsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        generateReportEventViewTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Iterate through each event in Event.eventDetails and put the details into Event related tables
        for (Event value : Event.eventDetails) {
            // Extract relevant information from the Event Objects
            Club hostingClub = value.getHostingClub();
            Event event = new Event(value.getEventName(), value.getEventLocation(),
                    value.getEventType(), value.getEventDeliveryType(), value.getEventDate(),
                    value.getEventTime(), hostingClub, value.getEventDescription(), value.getEventId());

            // Add the event to the items of each TableView
            ObservableList<Event> viewScheduledEvents = scheduleCreatedEventTable.getItems();
            viewScheduledEvents.add(event);
            // Put the event items to schedule created event table
            scheduleCreatedEventTable.setItems(viewScheduledEvents);

            // Put the event items to update event table
            ObservableList<Event> updateScheduledEvents = updateEventTable.getItems();
            updateScheduledEvents.add(event);
            updateEventTable.setItems(updateScheduledEvents);

            // Put the event items to cancelEventTable
            ObservableList<Event> cancelScheduledEvents = cancelEventTable.getItems();
            cancelScheduledEvents.add(event);
            cancelEventTable.setItems(cancelScheduledEvents);

            // Put the event items to viewCreatedEvents table
            ObservableList<Event> viewCreatedScheduledEvents = viewCreatedEventsTable.getItems();
            viewCreatedScheduledEvents.add(event);
            viewCreatedEventsTable.setItems(viewCreatedScheduledEvents);
            viewCreatedEventsSortComboBox.getSelectionModel().selectFirst(); // select the first item of the view Events
        }
    }

    // work done by- Pramuditha
    // Club creation sequence 1.1 :clubCreationChecker()
    @Override
    public void clubCreationChecker(ActionEvent event) {
        //Setting the valid state to true
        validStat = true;

        //Getting the user given club details from text fields
        int clubId = Integer.parseInt(this.clubId.getText());
        String clubName = this.clubName.getText();
        String clubDescription = this.clubDescription.getText();

        // Club creation sequence 1.1.1  : club( )
        //Creating a Club Object to validate details
        Club club = new Club(clubId,clubName,clubDescription);

        // Club creation sequence 1.1.1.1  : validateClubName()
        //Validating club name using validateClubName method
        if (!club.validateClubName()){
            validStat = false;
        }

        //Displaying relevant club name error if there is
        displayClubNameError(clubNameError);

        //Validating club description using validateClubDescription method
        if (!club.validateClubDescription()){
            validStat = false;
        }

        // Club creation sequence 1.1.1.1  : displayClubDecriptionError()
        //Displaying relevant club description error if there is
        displayClubDecriptionError(clubDescriptionError);

        //If the user has not imported an image Alert will show
        if (imagePath == null){
            validStat = false;
            //Displaying an alert
            Alert clubUpdateAlert = new Alert(Alert.AlertType.ERROR);
            clubUpdateAlert.initModality(Modality.APPLICATION_MODAL);
            clubUpdateAlert.setTitle("School Club Management System");
            clubUpdateAlert.setHeaderText("Please import an image!");
            clubUpdateAlert.show();
        }

        //Checking if all user given details are correct
        if (validStat) {
            //Creating a new club in the system using the user given data
            ClubAdvisor clubAdvisor = new ClubAdvisor();
            // Club creation sequence :1.1.1.1.1.1.1 :createClub()
            clubAdvisor.createClub(clubIdSetterValue,clubName,clubDescription,imagePath,clubAdvisorId);

            //Setting tables and combo boxes that is related to club
            setCreateTable();
            setUpdateTable();
            populateMembershipCombo(clubMembershipCombo);

            //Displaying an alert
            Alert clubUpdateAlert = new Alert(Alert.AlertType.INFORMATION);
            clubUpdateAlert.initModality(Modality.APPLICATION_MODAL);
            clubUpdateAlert.setTitle("School Club Management System");
            clubUpdateAlert.setHeaderText(clubName + " Club created successfully!");
            clubUpdateAlert.show();

            //Generating the next club id for next club and, displaying
            clubIdSetterValue += 1;
            this.clubId.setText(String.valueOf(clubIdSetterValue));

            //Resetting the club details text fields
            this.clubName.setText("");
            this.clubDescription.setText("");
        }else {
            //Alerting if user has entered invalid values for club details
            Alert clubUpdateAlert = new Alert(Alert.AlertType.WARNING);
            clubUpdateAlert.initModality(Modality.APPLICATION_MODAL);
            clubUpdateAlert.setTitle("School Club Management System");
            clubUpdateAlert.setHeaderText("Please fill the club details properly!");
            clubUpdateAlert.show();
        }
    }

    // work done by- Pramuditha
    //Resetting the club details text fields and error labels
    @Override
    void clubCreationReset(ActionEvent event) {
        clubName.setText("");
        clubDescription.setText("");

        clubNameError.setText("");
        clubDescriptionError.setText("");
    }

    // work done by- Pramuditha
    // Club update sequence : 1.1.2.1 : clubUpdateChecker()
    public void clubUpdateChecker(ActionEvent event) {
        //Setting the valid state to true
        validStat = true;

        //Getting the user given club details from text fields
        int clubId = Integer.parseInt(updateClubID.getText());
        String clubName = updateClubName.getText();
        String clubDescription = updateClubDescription.getText();

        // Club update sequence : 1.1.2.1.1 : Club()
        //Creating a Club Object to validate details
        Club club = new Club(clubId,clubName,clubDescription);

        //Validating club name using validateClubName method
        if (!club.validateClubName()){
            validStat = false;
        }
        //Displaying relevant club name error if there is
        displayClubNameError(updateClubNameError);

        //Validating club description using validateClubDescription method
        if (!club.validateClubDescription()){
            validStat = false;
        }
        //Displaying relevant club description error if there is
        displayClubDecriptionError(updateClubDescriptionError);

        //Checking if all user given details are correct using the validStat
        if (validStat){
            //Searching through the club details list to find the relevant club
            for (Club foundClub : clubDetailsList){
                if (clubId == foundClub.getClubId()){
                    // Club update sequence : 1.1.2.1.1.1.1.2setClubName()
                    foundClub.setClubName(clubName);                    //Changing the club name to new club name
                    // Club update sequence : 1.1.2.1.1.1.1.2.1.setClubDescription()
                    foundClub.setClubDescription(clubDescription);      //Changing the club description to the new one
                    //Setting the new club logo
                    String clubLogo = this.updateClubImage.getImage().getUrl();
                    // Club update sequence : 1.1.2.1.1.1.1.2.1.1 : setClubLogo()
                    foundClub.setClubLogo(clubLogo);

                    //Alerting when the user enters correct details
                    Alert clubUpdateAlert = new Alert(Alert.AlertType.INFORMATION);
                    clubUpdateAlert.initModality(Modality.APPLICATION_MODAL);
                    clubUpdateAlert.setTitle("School Club Management System");
                    clubUpdateAlert.setHeaderText("Club details successfully updated!!!");
                    clubUpdateAlert.show();

                    //Clearing the update text-fields
                    this.updateClubID.setText(String.valueOf(""));
                    this.updateClubName.setText("");
                    this.updateClubDescription.setText("");

                    //Updating club details tables
                    setCreateTable();
                    setUpdateTable();
                    populateMembershipCombo(clubMembershipCombo);

                    //Update database
                    ClubAdvisor clubAdvisor = new ClubAdvisor();
                    // Club update sequence :  1.1.2.1.1.1.1.2.1.2.1 : updateClub()
                    clubAdvisor.updateClub(clubIdSetterValue,clubName,clubDescription,imagePath,clubAdvisorId);
                }
            }
        } else {
            //Alerting if user enters a invalid data
            Alert clubUpdateAlert = new Alert(Alert.AlertType.WARNING);
            clubUpdateAlert.initModality(Modality.APPLICATION_MODAL);
            clubUpdateAlert.setTitle("School Club Management System");
            clubUpdateAlert.setHeaderText("Please fill the club details properly!");
            clubUpdateAlert.show();
        }
    }

    // work done by- Pramuditha
    //Resetting the club details text fields and error labels
    @FXML
    void clubUpdationReset(ActionEvent event) {
        updateClubID.setText(String.valueOf(""));
        updateClubName.setText("");
        updateClubDescription.setText("");

        updateClubNameError.setText("");
        updateClubDescriptionError.setText("");
    }

    // work done by- Pramuditha
    @FXML
    void searchUpdateTable(ActionEvent event) {
        //Get the club name to search from the search bar
        String clubName = updateClubSearch.getText();

        // Search for the club name and handle non-existent club name
        Club foundClub = null;
        for (Club club : updateClubDetailsTable.getItems()) {
            if (club.getClubName().equals(clubName)) {
                foundClub = club;
                break;
            }
        }

        if (foundClub != null) {
            // Select the row with the found club in the updateClubDetailsTable
            updateClubDetailsTable.getSelectionModel().select(foundClub);
            updateClubDetailsTable.scrollTo(foundClub);
            // Update the input fields with the selected item's details for updating
            updateClubTableSelect();
        } else {
            // Show alert for non-existent Club Name
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Club Not Found");
            alert.setHeaderText(null);
            alert.setContentText("The Club with name " + clubName + " does not exist.");
            alert.showAndWait();
        }
    }

    // work done by- Pramuditha
    //Method which displays the club name error
    public void displayClubNameError(Label labelID) {
        if (Club.clubNameValidateStatus.equals("empty")) {
            //If the club name is empty an error will display
            labelID.setText("Club Name cannot be empty");
        } else if (Club.clubNameValidateStatus.equals("format")) {
            //If the club name value contains digits or special characters an error will display
            labelID.setText("Club Name can contain only\nletters");
        } else if (Club.clubNameValidateStatus.equals("exist")) {
            //If the user entered value for club name already taken an error will display
            labelID.setText("That club name already exists");
        } else {
            //When the user enters a valid club name
            labelID.setText("");
        }
    }

    // work done by- Pramuditha
    //Method which displays the club Description error
    public void displayClubDecriptionError(Label labelID) {
        if (Club.clubDescriptionValidateStatus.equals("empty")) {
            //If the club description is empty an error will display
            labelID.setText("Club Description cannot be empty");
        } else {
            //If the club description is valid error wil clear
            labelID.setText("");
        }
    }

    // work done by- Pramuditha
    // Update Club sequence : 1.1 : updateClubTableSelect()
    @FXML
    public void updateClubTableSelect(MouseEvent event) {
        updateClubTableSelect();
        //Enable the update buttons when the user selects a club to update
        updateClubImageButton.setDisable(false);
        updateClubButton.setDisable(false);

        //Resetting the error labels when a user selects a club
        updateClubNameError.setText("");
        updateClubDescriptionError.setText("");
    }

    // work done by- Pramuditha
    public void updateClubTableSelect() {
        //Getting the row in the table which user selected
        int row = updateClubDetailsTable.getSelectionModel().getSelectedIndex();

        //Setting the details of the user selected club to the update data fields
        String clubID = String.valueOf(clubDetailsList.get(row).getClubId());
        updateClubID.setText(clubID);
        updateClubName.setText(clubDetailsList.get(row).getClubName());
        updateClubDescription.setText(clubDetailsList.get(row).getClubDescription());
        updateClubImage.setImage(clubDetailsList.get(row).getAbsoluteImage().getImage());
    }


    // work done by- Pramuditha
    public void OpenImageHandler(ActionEvent event) {
        fileChooser.setTitle("File Chooser"); //Set the title of the file chooser dialog

        //Set the initial directory of the fileChooser to the user's home directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        //Clear any existing extension filters
        fileChooser.getExtensionFilters().clear();
        //Add a new extension filter for image files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        //Show the open dialog and get the selected file
        File file = fileChooser.showOpenDialog(null);

        //Check whether if a file is selected by the user
        if (file != null) {
            //get the button that handles the event
            Button clickedButton = (Button) event.getSource();

            //Take the fxID of the button
            String fxID = clickedButton.getId();
            //Get the selected image path
            imagePath = file.getPath();

            //Check whether the image imported is from the update or from the adding pane
            if (fxID.equals("createClubImageButton")) {
                //Set the input image view as the selected image
                createClubImage.setImage(new Image(String.valueOf(file.toURI())));
            } else {
                //Set the update image view as the selected image
                updateClubImage.setImage(new Image(String.valueOf(file.toURI())));
            }
        } else {
            //Show the import image error alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("School Activity Club Management System");
            alert.setHeaderText(null);
            alert.setContentText("Image is not imported!");
            alert.showAndWait();
        }
    }

    // work done by- Pramuditha
    public void updateOpenImageHandler(ActionEvent event) {
        fileChooser.setTitle("File Chooser"); //Set the title of the file chooser dialog

        //Set the initial directory of the fileChooser to the user's home directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        //Clear any existing extension filters
        fileChooser.getExtensionFilters().clear();
        //Add a new extension filter for image files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        //Show the open dialog and get the selected file
        File file = fileChooser.showOpenDialog(null);

        //Check whether if a file is selected by the user
        if (file != null) {
            //get the button that handles the event
            Button clickedButton = (Button) event.getSource();

            //Take the fxID of the button
            String fxID = clickedButton.getId();
            //Get the selected image path
            imagePath = file.getPath();

            //Check whether the image imported is from the update or from the adding pane
            if (fxID.equals("updateClubImageButton")) {
                //Set the input image view as the selected image
                updateClubImage.setImage(new Image(String.valueOf(file.toURI())));
            } else {
                //Set the update image view as the selected image
                updateClubImage.setImage(new Image(String.valueOf(file.toURI())));
            }
        } else {
            //Show the import image error alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("School Activity Club Management System");
            alert.setHeaderText(null);
            alert.setContentText("Image is not imported!");
            alert.showAndWait();
        }
    }

    // work done by- Lakshan
    // This method is used to clear all schedule event fields in event scheduling
    @Override
    public void clearScheduleEventFields(ActionEvent event) {
        clearEventScheduleFieldsDefault();
    }

    // work done by- Lakshan
    // This method will be used to clear scheduled event fields when creating event details
    public void clearEventScheduleFieldsDefault() {
        scheduleEventNameTextField.setText(""); // set schedule EventName field empty
        scheduleEventsLocationTextField.setText(""); // set schedule event location empty
        scheduleEventDescriptionTextField.setText(""); // set event description empty
        scheduleEventDatePicker.setValue(null); // set the event date picker value null
        scheduleEventTypeCombo.getSelectionModel().selectFirst(); // select the first selection item of event type
        // select the first selection item of event delivery type
        ScheduleEventsDeliveryType.getSelectionModel().selectFirst();
        // set the schedule event starting minute as default 0  by setting the first value
        scheduleEventMinutes.getSelectionModel().selectFirst();
        // set the scheduling event starting hour as default as 0  by setting the first value
        scheduleEventHour.getSelectionModel().selectFirst();
        // set the scheduling event club name as not selected  by setting the first value
        scheduleEventsClubName.getSelectionModel().selectFirst();
        clearAllScheduleEventLabels();
    }

    // This method will be used clear update event fields when updating event details
    public void clearUpdateEventFields() {
        // set the update event club as not selected by setting the first value
        updateEventClubCombo.getSelectionModel().selectFirst();
        // set the update event type combo box as not selected  by setting the first value
        updateEventTypeCombo.getSelectionModel().selectFirst();
        // set update event delivery type combo box as not selected  by setting the first value
        updateEventDeliveryTypeCombo.getSelectionModel().selectFirst();
        // set the update event location as empty
        updateEventLocationTextField.setText("");
        // set the update  event name as empty
        updateEventNameTextField.setText("");
        // set the update event description as empty
        updateEventDescription.setText("");
        // set the update event date as null
        updateEventDateDatePicker.setValue(null);
        // select the update event starting hour as 0 by setting the first value
        updateHourComboBox.getSelectionModel().selectFirst();
        // select the event starting minute as  0 by setting the first value
        updateMinuteComboBox.getSelectionModel().selectFirst();
        // select the club related to the event ads None by selecting the first value
        updateEventClubCombo.getSelectionModel().selectFirst();
        updateEventClubCombo.getSelectionModel().selectFirst();
        // call to clear all the update field labels
        clearAllUpdateEventLabels();
    }

    // work done by- Lakshan
    // This method will clear all the update related event fields
    @Override
    protected void clearUpdateEventFields(ActionEvent event) {
        clearUpdateEventFields();
    }

    // work done by- Lakshan
    // This method will check whether there are validation errors in the user given event name in both create and update
    @FXML
    void CheckNameError(KeyEvent event) {
        // taking the id of the textfield to do validations when typing
        String targetName = "TextField[id=scheduleEventNameTextField, styleClass=text-input text-field eventField]";
        // declare the event name variable
        String eventName;
        // create an object of event manager to do the validations
        EventManager eventManager = new EventManager();

        // Check whether typing in the schedule event textfield
        if (String.valueOf(event.getTarget()).equals(targetName)) {
            // if it is the schedule event text field get the text from text field
            eventName = scheduleEventNameTextField.getText();

            // validate the event name using validate event name method in eventManager class
            if (!eventManager.validateEventName(eventName)) {
                // set the error label if the event name is null
                scheduleErrorLabelEventName.setText("Event name cannot be empty");
            } else {
                // set the error label empty if the event name is correct
                scheduleErrorLabelEventName.setText("");
            }
        } else {
            // if the selected textfield is not event scheduling text field, update error labels
            // get the text input of update textfield
            eventName = updateEventNameTextField.getText();

            // validate the event name using validate event name method in event manager class
            if (!eventManager.validateEventName(eventName)) {
                // set the error label if the event name is empty
                updateErrorLabelEventName.setText("Event name cannot be empty");
            } else {
                // set the error label empty if the event name is correct
                updateErrorLabelEventName.setText(" ");
            }
        }
    }

    // work done by- Lakshan
    /* This method will check whether user given event location is according to the validation standards in
    both schedule and update text fields */
    @FXML
    void CheckLocationError(KeyEvent event) {
        // taking the id of the textfield to do validations when typing
        String targetLocation = "TextField[id=scheduleEventsLocationTextField, styleClass=text-input text-field eventField]";

        // declare the event location variable
        String eventLocation;

        // create an object of event manager to do the validations
        EventManager eventManager = new EventManager();

        // Check whether typing in the schedule event textfield
        if (String.valueOf(event.getTarget()).equals(targetLocation)) {
            // if it is the schedule event text field get the text from text field
            eventLocation = scheduleEventsLocationTextField.getText();

            // validate the event location using validate event location method in eventManager class
            if (!eventManager.validateEventLocation(eventLocation)) {
                // set the error label if the event location is null
                scheduleErrorLabelEventLocation.setText("Event Location cannot be empty");
            } else {
                // set the error label empty if the event location is correct
                scheduleErrorLabelEventLocation.setText(" ");
            }
        } else {
            // if it is the update event text field get the text from text field
            eventLocation = updateEventLocationTextField.getText();

            // validate the event location using validate event location method in eventManager class
            if (!eventManager.validateEventLocation(eventLocation)) {
                // set the error label if the update event location error label if update location is null
                updateErrorLabelEventLocation.setText("Event Location cannot be empty");
            } else {
                // set the update location error label empty if the given location is correct
                updateErrorLabelEventLocation.setText(" ");
            }
        }
    }

    // work done by- Lakshan
    /*This method will check whether user given event type is according to the event type validations*/
    @FXML
    void CheckEventTypeError(ActionEvent event) {
        // Taking the id of the create event to do validations when typing
        String targetType = "ComboBox[id=scheduleEventTypeCombo, styleClass=combo-box-base combo-box eventField]";

        // declare the selected event type option
        String selectedOption;

        // create an eventManager object to check whether using given event type is correct
        EventManager eventManager = new EventManager();

        // check the event location textfield belongs to event scheduling
        if (String.valueOf(event.getTarget()).equals(targetType)) {
            // get the current event scheduling type value to declared variable
            selectedOption = scheduleEventTypeCombo.getSelectionModel().getSelectedItem();

            // do the event type validation
            if (eventManager.validateEventType(selectedOption)) {
                // if the event type validation is correct set the label value
                scheduleErrorLabelEventType.setText("Event type cannot be None");
            } else {
                // if the event type validation is incorrect set the label value as null
                scheduleErrorLabelEventType.setText(" ");
            }
        } else {
            // if the selected option is in update text field, set the value to selected option
            selectedOption = updateEventTypeCombo.getSelectionModel().getSelectedItem();
            if (eventManager.validateEventType(selectedOption)) {
                // Show the update error label if the validation status is false(None)
                updateErrorLabelEventType.setText("Event type cannot be None");
            } else {
                // Show the update error label if the validation status is true
                updateErrorLabelEventType.setText(" ");
            }
        }
    }

    // work done by- Lakshan
    /* This method will check whether event delivery type is according to the validation standards*/
    @FXML
    void checkDeliveryTypeError(ActionEvent event) {
        // set the id of the delivery type text field
        String targetDelivery = "ComboBox[id=ScheduleEventsDeliveryType, styleClass=combo-box-base combo-box eventField]";
        // declare the selectedOption
        String selectedOption;

        // Create an event manager object to check whether event delivery type is according to the validation standard
        EventManager eventManager = new EventManager();

        // check whether the user typing text field
        if (String.valueOf(event.getTarget()).equals(targetDelivery)) {
            // set the selected option value if the event delivery type belongs to event scheduling
            selectedOption = ScheduleEventsDeliveryType.getSelectionModel().getSelectedItem();
            if (eventManager.validateEventType(selectedOption)) {
                // set the error label if the user selected delivery type is None (type)
                scheduleErrorLabelEventDeliveryType.setText("Event delivery type cannot be None");
            } else {
                // set the error label empty if the selected delivery type is correct
                scheduleErrorLabelEventDeliveryType.setText(" ");
            }
        } else {
            // set the selected option value if the event delivery type belongs to event updating
            selectedOption = updateEventDeliveryTypeCombo.getSelectionModel().getSelectedItem();
            if (eventManager.validateEventType(selectedOption)) {
                // set the error label if the user selected delivery type is None (type)
                updateErrorLabelDeliveryType.setText("Event delivery type cannot be None");
            } else {
                // set the error label empty if the selected delivery type is correct
                updateErrorLabelDeliveryType.setText(" ");
            }
        }

    }

    // work done by- Lakshan
    @FXML
    void checkSelectedEventDate(ActionEvent event) {
        // set the selected date picker
        String targetDate = "DatePicker[id=scheduleEventDatePicker, styleClass=combo-box-base date-picker eventField]";

        // declare the selected date
        LocalDate selectedDate;

        // Create an event manager object to check whether event date is according to the date validation status
        EventManager eventManager = new EventManager();

        // check the selected date is in the update or schedule field
        if (String.valueOf(event.getTarget()).equals(targetDate)) {
            // If the selected date is in the scheduling get its value
            selectedDate = scheduleEventDatePicker.getValue();

            if (!eventManager.validateEventDate(selectedDate)) {
                // display error, if the event date is not valid
                scheduleErrorLabelEventDate.setText("Event date cannot be a past date");
            } else {
                // display None, if the event date is valid
                scheduleErrorLabelEventDate.setText(" ");
            }
        } else {
            // get the current date value if the update option is selected
            selectedDate = updateEventDateDatePicker.getValue();

            if (!eventManager.validateEventDate(selectedDate)) {
                // display error if the event date is not valid
                updateErrorLabelEventDate.setText("Event date cannot be a past date");
            } else {
                // display None if the event date is valid
                updateErrorLabelEventDate.setText(" ");
            }

        }

    }

    // work done by- Lakshan
    @FXML
    void checkClubName(ActionEvent event) {
        // set the selected club Name
        String targetClub = "ComboBox[id=scheduleEventsClubName, styleClass=combo-box-base combo-box eventField]";

        // declare the club name
        String selectedClub;

        // Create EventManger object to check whether selected club name is valid
        EventManager eventManager = new EventManager();

        // Check the selected club is in the update or schedule field
        if (String.valueOf(event.getTarget()).equals(targetClub)) {

            selectedClub = scheduleEventsClubName.getSelectionModel().getSelectedItem();
            if (eventManager.validateEventType(selectedClub)) {
                // display error, if the event club name is not valid
                scheduleErrorLabelClubName.setText("Club Name cannot be None");
            } else {
                // display None if the club name is valid
                scheduleErrorLabelClubName.setText(" ");
            }
        } else {
            // set the event update Club as selectedClub
            selectedClub = updateEventClubCombo.getSelectionModel().getSelectedItem();

            if (eventManager.validateEventType(selectedClub)) {
                // display error, if the event club name is not valid
                updateErrorLabelClubName.setText("Club Name cannot be None");
            } else {
                // display None if the club name is valid
                updateErrorLabelClubName.setText(" ");
            }
        }
    }

    // work done by- Lakshan
    // Method to populate comboBoxes with their club names for scheduling and updating events
    public void getCreatedClubs() {
        // Check if None is already their in the scheduleEventsClubName ComboBox
        if (!scheduleEventsClubName.getItems().contains("None")) {
            // If not exist add it to scheduleEventsClubName comboBox
            scheduleEventsClubName.getItems().add("None");
        }

        // Check if None is already their in the updateEventClubCombo ComboBox
        if (!updateEventClubCombo.getItems().contains("None")) {
            // If not exist add it to updateEventClubCombo comboBox
            updateEventClubCombo.getItems().add("None");
        }

        // Check if None is already their in the viewCreatedEventsSortComboBox ComboBox
        if (!viewCreatedEventsSortComboBox.getItems().contains("All Clubs")) {
            // If not exist add it to viewCreatedEventsSortComboBox comboBox
            viewCreatedEventsSortComboBox.getItems().add("All Clubs");
        }

        // Iterate through each Club in the clubDetails list to populate the combo box
        for (Club club : Club.clubDetailsList) {
            String clubName;
            clubName = club.getClubName();

            // Check if the clubName is already present in each ComboBox
            boolean scheduleContainStatus = scheduleEventsClubName.getItems().contains(clubName);
            boolean updateContainsStatus = updateEventClubCombo.getItems().contains(clubName);
            boolean viewContainsStatus = viewCreatedEventsSortComboBox.getItems().contains(clubName);

            // If the clubName is not already present, add it to each ComboBox
            if (!scheduleContainStatus) {
                // Add the club names to scheduleEventsClubName
                scheduleEventsClubName.getItems().add(clubName);
            }

            if (!updateContainsStatus) {
                // Add the club names to updateEventClubCombo
                updateEventClubCombo.getItems().add(clubName);
            }

            if (!viewContainsStatus) {
                // Add the club names to viewCreatedEventsSortComboBox
                viewCreatedEventsSortComboBox.getItems().add(clubName);
            }

        }

        // Select the first item in each ComboBox
        scheduleEventsClubName.getSelectionModel().selectFirst();
        scheduleErrorLabelClubName.setText(" ");

        updateEventClubCombo.getSelectionModel().selectFirst();
        updateErrorLabelClubName.setText(" ");

        viewCreatedEventsSortComboBox.getSelectionModel().selectFirst();
    }


    // work done by- Lakshan
    /*This method is responsible on taking the user inputs and
    show error and information messages when scheduling events*/
    // Event scheduling sequence : 1.1 : scheduleEventController()
    @FXML
    void scheduleEventController(ActionEvent event) {

        // Get event name for scheduling input
        String eventName = scheduleEventNameTextField.getText();

        // Get the event location for scheduling input
        String eventLocation = scheduleEventsLocationTextField.getText();

        // Get the eventDate for scheduling input
        LocalDate eventDate = scheduleEventDatePicker.getValue();

        // Get the eventDeliveryType for scheduling input
        String deliveryType = ScheduleEventsDeliveryType.getValue();

        // Get the eventType for scheduling input
        String eventType = scheduleEventTypeCombo.getValue();

        // Get the clubName for scheduling input
        String clubName = scheduleEventsClubName.getValue();

        // Get the eventStartHour for scheduling input
        String eventStartHour = scheduleEventHour.getValue();

        // Get the eventStartMinute for scheduling input
        String eventStartMinute = scheduleEventMinutes.getValue();

        // Get the eventDescription for scheduling input
        String eventDescription = scheduleEventDescriptionTextField.getText();

        // This EventManager object is used to user given event scheduling input
        EventManager eventManager = new EventManager();

        // Event Scheduling sequence : 1.1.1 : validateAllEventDetails()
        // stat variables checks whether all the event scheduling inputs are correct
        boolean stat = eventManager.validateAllEventDetails(eventName, eventLocation, eventType, deliveryType,
                eventDate, clubName, eventStartHour, eventStartMinute, "create", eventDescription);
        // If the validation status is true, populate all event related tables in event scheduling section
        if (stat) {
            clearEventScheduleFieldsDefault(); // clear all event scheduling fields
            populateEventsTables(); // populating the tables
            displayNumberOfScheduledEvents(); // Update number of scheduled events
            getNextEventDate(); // get the next event date
        } else {
            // Show the event creation alert if event details are not valid
            Alert eventCreateAlert = new Alert(Alert.AlertType.WARNING);
            eventCreateAlert.initModality(Modality.APPLICATION_MODAL);
            eventCreateAlert.setTitle("School Club Management System");
            eventCreateAlert.setHeaderText("Please enter values properly to create an event!!!");
            eventCreateAlert.show();
        }
        // display the event related required fields
        DisplayEventErrors();
    }

    // work done by- Lakshan
    // This method is responsible on displaying event scheduling and updating error labels
    public void DisplayEventErrors() {
        // Check if the event date is not set to a future date
        if (!EventManager.eventDateStatus) {
            // both update and scheduling date error labels will be displayed
            scheduleErrorLabelEventDate.setText("It is compulsory to set a future date");
            updateErrorLabelEventDate.setText("It is compulsory to set a future date");
        } else {
            // clear the error messages if the event date is valid
            scheduleErrorLabelEventDate.setText(" ");
            updateErrorLabelEventDate.setText(" ");
        }

        // Check if the event type is set to "None"
        if (!EventManager.eventTypeStatus) {
            // both update and scheduling event type error will be displayed
            scheduleErrorLabelEventType.setText("Event type cannot be None");
            updateErrorLabelEventType.setText("Event type cannot be None");
        } else {
            // Clear the error messages if the event type is valid
            scheduleErrorLabelEventType.setText(" ");
            updateErrorLabelEventType.setText(" ");
        }

        // Check if the event delivery type is set to "None"
        if (!EventManager.eventDeliveryTypeStatus) {
            // both and scheduling event type error will be displayed
            scheduleErrorLabelEventDeliveryType.setText("Event delivery type cannot be None");
            updateErrorLabelDeliveryType.setText("Event delivery type cannot be None");
        } else {
            // Clear the error messages if the event type is valid
            scheduleErrorLabelEventDeliveryType.setText("");
            updateErrorLabelDeliveryType.setText(" ");
        }

        // Check if the event location is empty
        if (!EventManager.eventLocationStatus) {
            // both and scheduling event type error will be displayed
            scheduleErrorLabelEventLocation.setText("Event Location cannot be empty");
            updateErrorLabelEventLocation.setText("Event Location cannot be empty");
        } else {
            // Clear the error messages if the event location is valid
            scheduleErrorLabelEventLocation.setText(" ");
            updateErrorLabelEventLocation.setText(" ");
        }

        // Check if the event name is empty
        if (!EventManager.eventNameStatus) {
            // both and scheduling event type error will be displayed
            scheduleErrorLabelEventName.setText("Event name cannot be empty");
            updateErrorLabelEventName.setText("Event name cannot be empty");
        } else {
            // Clear the error messages if the event name is valid
            scheduleErrorLabelEventName.setText(" ");
            updateErrorLabelEventName.setText(" ");
        }

        // Check if the club name is set to "None"
        if (!EventManager.eventClubNameStatus) {
            // both and scheduling event type error will be displayed
            scheduleErrorLabelClubName.setText("Club Name cannot be None");
            updateErrorLabelClubName.setText("Club Name cannot be None");
        } else {
            // Clear the error messages if the club name is valid
            scheduleErrorLabelClubName.setText("");
            updateErrorLabelClubName.setText(" ");
        }
    }


    // work done by- Lakshan
    // This method clears all error labels in scheduling events
    public void clearAllScheduleEventLabels() {
        scheduleErrorLabelEventName.setText(""); // clear event name error label
        scheduleErrorLabelEventLocation.setText(" "); // clear event location error label
        scheduleErrorLabelEventDate.setText(" "); // clear event date error label
        scheduleErrorLabelEventDeliveryType.setText(" "); // clear event delivery type error label
        scheduleErrorLabelEventType.setText(" "); // clear event type error label
        scheduleErrorLabelClubName.setText(" ");  // clear event hosting club error label
    }

    // work done by- Lakshan
    // This method clear all error labels in updating events
    public void clearAllUpdateEventLabels() {
        updateErrorLabelEventDate.setText(" "); // clear update event date error label
        updateErrorLabelDeliveryType.setText(" "); // clear update delivery type error label
        updateErrorLabelEventType.setText(" "); // clear update event type error label
        updateErrorLabelEventLocation.setText(" "); // clear update event location error label
        updateErrorLabelEventName.setText(" "); // clear update event name error label
        updateErrorLabelClubName.setText(" "); // clear update event hosting club error label
    }

    // work done by- Lakshan
    // Overloading method of below updateRowSelection method to handle update row selections
    // Event update sequence :
    @FXML
    public void updateRowSelection(MouseEvent event) {
        updateRowSelection();
    }

    // work done by- Lakshan
    /*This table selects the row that has to be updated in updateEventTable,
     * and it is responsible on enabling updating fields for events and populate them with the selected event details.
     * and, its sets the selected event value and Id for do the updates in the database.*/
    public void updateRowSelection() {
        try {
            // Check if an item is selected in the update events table
            if (!(updateEventTable.getSelectionModel().getSelectedItem() == null)) {
                // Enabling input fields for updating events
                enableAllUpdateEventFields();
            }

            // Enable buttons for updating and clearing event fields
            updateEventFieldButton.setDisable(false);
            clearEventFieldButton.setDisable(false);

            // Get the selected event details and index
            selectedEventValue = updateEventTable.getSelectionModel().getSelectedItem();
            selectedEventId = updateEventTable.getSelectionModel().getSelectedIndex();

            // Set all the UI components including text-fields, combo boxes and text area with selected event details
            updateEventClubCombo.setValue(String.valueOf(selectedEventValue.getClubName()));
            updateEventTypeCombo.setValue(String.valueOf(selectedEventValue.getEventType()));
            updateEventDeliveryTypeCombo.setValue(String.valueOf(selectedEventValue.getEventDeliveryType()));
            updateEventLocationTextField.setText(String.valueOf(selectedEventValue.getEventLocation()));
            updateEventNameTextField.setText(String.valueOf(selectedEventValue.getEventName()));
            updateEventDescription.setText(String.valueOf(selectedEventValue.getEventDescription()));
            updateEventDateDatePicker.setValue(selectedEventValue.getEventDate());

            // Set hour and minute values from the event's start time
            LocalTime startTime = selectedEventValue.getEventTime();
            int hour = startTime.getHour();

            // Check if the hour is less than 10
            if (hour < 10) {
                // If the hour is a single digit, prepend '0' before setting the value in the combo box
                String hourVal = "0" + hour;
                updateHourComboBox.setValue(hourVal);
            } else {
                // If the hour is two digits, set the value in the combo box as a string
                updateHourComboBox.setValue(String.valueOf(hour));
            }


            int minute = startTime.getMinute();
            // Check if the minute is less than 10
            if (minute < 10) {
                // If the minute is a single digit, prepend '0' before setting the value in the combo box
                String minuteVal = "0" + minute;
                updateMinuteComboBox.setValue(minuteVal);
            } else {
                // If the minute is two digits, set the value in the combo box as a string
                updateMinuteComboBox.setValue(String.valueOf(minute));
            }


        } catch (NullPointerException E) {
            // Handling the case where no values are selected
            System.out.println("No values");
        }

    }

    // work done by- Lakshan
    // This method disables all update fields in event updating
    public void disableAllUpdateEventFields() {
        updateEventClubCombo.setDisable(true); // Disable update event club combo box
        updateEventTypeCombo.setDisable(true); // Disable update event type combo box
        updateEventDeliveryTypeCombo.setDisable(true); // Disable update event delivery type combo box
        updateEventLocationTextField.setDisable(true); // Disable update event location text field
        updateEventNameTextField.setDisable(true); // Disable update event name text field
        updateEventDescription.setDisable(true); //  Disable update event description field
        updateEventDateDatePicker.setDisable(true); // Disable update event date picker
        updateHourComboBox.setDisable(true); // Disable update hour combo box
        updateMinuteComboBox.setDisable(true); // Disable update minute combo box
        updateEventClubCombo.setDisable(true); // Disable update event club name combo box
        updateEventClubCombo.setDisable(true); // Disable update event club name combo box
    }

    // work done by- Lakshan
    // This method enables all update fields in event updating
    public void enableAllUpdateEventFields() {
        updateEventClubCombo.setDisable(false); // Enable update event club combo box
        updateEventTypeCombo.setDisable(false); // Enable update event type combo box
        updateEventDeliveryTypeCombo.setDisable(false); // Enable update event delivery type combo box
        updateEventLocationTextField.setDisable(false); // Enable update event location text field
        updateEventNameTextField.setDisable(false); // Enable update event name text field
        updateEventDescription.setDisable(false); // Enable update event description field
        updateEventDateDatePicker.setDisable(false); // Enable update event date picker
        updateHourComboBox.setDisable(false); // Enable update hour combo box
        updateMinuteComboBox.setDisable(false); // Enable update minute combo box
        updateEventClubCombo.setDisable(false); // Enable update event club name combo box
    }

    // work done by- Lakshan
    /*This method is responsible on  updating event related details*/
    // Update Events Sequence : 3.1 : updateEventsController()
    @FXML
    void updateEventsController(ActionEvent event) {
        // set the event name by getting the value from updateEventNameTextField
        String eventName = updateEventNameTextField.getText();

        // set the event location by getting the value from updateEventLocationTextField
        String eventLocation = updateEventLocationTextField.getText();

        // set the event date by getting the value from updateEventDateDatePicker
        LocalDate eventDate = updateEventDateDatePicker.getValue();

        // set the event delivery type by getting the value from updateEventDeliveryTypeCombo
        String deliveryType = updateEventDeliveryTypeCombo.getValue();

        // set the event type by getting the value from updateEventTypeCombo
        String eventType = updateEventTypeCombo.getValue();

        // set the club Name type by getting the value from updateEventClubCombo
        String clubName = updateEventClubCombo.getValue();

        // set the event start hour by getting the value from updateHourComboBox
        String eventStartHour = updateHourComboBox.getValue();

        // set the event start minute by getting the value from updateMinuteComboBox
        String eventStartMinute = updateMinuteComboBox.getValue();

        // set the event description by getting the value from updateEventDescription
        String eventDescription = updateEventDescription.getText();

        // Calling event manager object to check whether all the event update details are according to validation standards
        EventManager eventManager = new EventManager();

        // Update Events3.1.1 : validateAllEventDetails()
        // calling the validateAllDetails method in event manager class to validate details
        boolean stat = eventManager.validateAllEventDetails(eventName, eventLocation, eventType, deliveryType,
                eventDate, clubName, eventStartHour, eventStartMinute, "update", eventDescription);

        if (stat) {
            // Passing the updated eventName to Event class using its setter
            // Update Events sequence : 3.1.2.1 : setEventName()
            selectedEventValue.setEventName(eventName);
            // Update Events sequence : 3.1.2.1.1 : setEventLocation()
            // Passing the updated eventLocation to Event class using its setter
            selectedEventValue.setEventLocation(eventLocation);
            // Update Events sequence : 3.1.2.1.1.1 : setEventDate()
            // Passing the updated eventDate to Event class using its setter
            selectedEventValue.setEventDate(eventDate);
            // Update Events sequence : 3.1.2.1.1.1.1 : setEventDeliveryType()
            // Passing the updated eventDeliveryType to Event class using its setter
            selectedEventValue.setEventDeliveryType(deliveryType);
            // Update Events sequence : 3.1.2.1.1.1.1.1 : setEventType()
            // Passing the updated eventType to Event class using its setter
            selectedEventValue.setEventType(eventType);
            // Update Events sequence : 3.1.2.1.1.1.1.1.1 : setHostingClub()
            // Passing the updated eventHostingClub to Event class using its setter
            selectedEventValue.setHostingClub(EventManager.userSelectedClubChooser(clubName));
            // Update Events sequence : 3.1.2.1.1.1.1.1.1.1 : setEventTime()
            // Passing the updated eventTime to Event class using its setter
            selectedEventValue.setEventTime(eventManager.makeDateTime(eventStartHour, eventStartMinute));
            // Update Events sequence : 3.1.2.1.1.1.1.1.1.1 : setEventDescription()
            // Passing the updated eventDescription to Event class using its setter
            selectedEventValue.setEventDescription(eventDescription);

            // Update Events sequence : 3.1.2.1.1.1.1.1.1.2.1 : updateEventDetails()
            // create the given seperated hour and minute to Local time
            LocalTime eventStaringTime = eventManager.makeDateTime(eventStartHour, eventStartMinute);
            selectedEventValue.setEventTime(eventStaringTime);

            // creating a club advisor Object to update event details
            ClubAdvisor clubAdvisor = new ClubAdvisor();
            // call update event details method in clubAdvisor class
            clubAdvisor.updateEventDetails(selectedEventValue, selectedEventId);

            // Populate all the related event tables
            populateEventsTables();
            // Update next event date
            getNextEventDate();
            // call disable all update event fields
            disableAllUpdateEventFields();
            // Clear all update event fields
            clearUpdateEventFields();
        } else {
            // display alert if the user given update details are wrong
            Alert eventUpdateAlert = new Alert(Alert.AlertType.WARNING);
            eventUpdateAlert.initModality(Modality.APPLICATION_MODAL);
            eventUpdateAlert.setTitle("School Club Management System");
            eventUpdateAlert.setHeaderText("Please enter values properly to update an event!!!");
            eventUpdateAlert.show();
        }

        // Calling the method that displays update error labels
        DisplayEventErrors();
    }

    // work done by- Lakshan
    /*This method is responsible on cancel an event.*/
    // Event Cancel Sequence : 1.1.1.2.1 : cancelEventController()
    @FXML
    void cancelEventController(ActionEvent event) {
        try {
            // Taking the selected event to be canceled from the event table
            Event selectedEvent = cancelEventTable.getSelectionModel().getSelectedItem();

            // Get the event ID of the event to be canceled
            selectedEventId = cancelEventTable.getSelectionModel().getSelectedIndex();

            // Ask the user whether they really want to cancel the event by sending an alert
            Alert cancelEvent = new Alert(Alert.AlertType.CONFIRMATION);
            cancelEvent.initModality(Modality.APPLICATION_MODAL);
            cancelEvent.setTitle("School Activity Club Management System");
            cancelEvent.setHeaderText("Do you really want to delete the event ?");

            // Get the conformation of the user to cancel an event
            Optional<ButtonType> result = cancelEvent.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }

            // Create a clubAdvisor object
            ClubAdvisor clubAdvisor = new ClubAdvisor();
            // Event cancel sequence : 1.1.1.2.1.2.1 : cancelEvent()
            // call the cancel event method to update the database from ClubAdvisor class
            clubAdvisor.cancelEvent(selectedEvent, selectedEventId);

            // populate all club advisor relate tables
            populateEventsTables();

            // update the number of scheduled events
            displayNumberOfScheduledEvents();

            // update the next event date
            getNextEventDate();

        } catch (NullPointerException error) {
            // Show the event cancel error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("School Club Management System");
            alert.setHeaderText("Select an event from table to cancel the event");
            alert.show();
        }

    }

    // work done by- Lakshan
    // This method is used to search the cancel events
    // Event Cancel Sequences sequence - 1 search cancel event
    @FXML
    void searchCancelEvent(ActionEvent event) {
        //  Event Cancel Sequence 1.1 : searchEvents()
        // giving the searchbar and the table to be searched
        searchEvents(cancelEventTable, cancelEventSearchBar);
    }

    // work done by- Lakshan
    // This method is used to search for update events
    @FXML
    void searchUpdateEventDetails(ActionEvent event) {
        // giving the searchbar and the table to be searched
        searchEvents(updateEventTable, updateEventSearchBar);
    }

    // work done by- Lakshan
    // This method is used to search events in create view
    @FXML
    void searchScheduledEventsInCreate(ActionEvent event) {
        searchEvents(scheduleCreatedEventTable, createdEventSearchBar);
    }

    // work done by- Lakshan
    // This method searches for an event in the given table based on the given search bar input
    public void searchEvents(TableView<Event> tableView, TextField searchBar) {
        // Get the event name from the search bar
        String eventName = searchBar.getText();


        // Initializing foundEvent to null
        Event foundEvent = null;

        // Iterate through the items in the table to find a matching event
        for (Event eventVal : tableView.getItems()) {
            if (eventVal.getEventName().equals(eventName)) {
                foundEvent = eventVal;
                break;
            }
        }

        // If a matching event is found, perform the following actions
        if (foundEvent != null) {
            // Select the found event, in the table
            tableView.getSelectionModel().select(foundEvent);

            // Get the selected event index and scroll to it in the table
            selectedEventId = tableView.getSelectionModel().getSelectedIndex();
            tableView.scrollTo(foundEvent);

            // If the table is UpdateEventTable, call the updateRowSelection method
            // Event Cancel sequence - 1.1.1 : updateRowSelection()
            if (tableView == updateEventTable) {
                updateRowSelection();
            }
        } else {
            // If no matching event is found, display the error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("School Club Management System");
            alert.setHeaderText("The event " + eventName + " does not found");
            alert.showAndWait();
        }
    }

    // work done by- Lakshan
    // This method display the number of scheduled events
    public void displayNumberOfScheduledEvents() {
        numberOfScheduledEvents.setText(String.valueOf(Event.eventDetails.size()));
    }

    // work done by- Lakshan
    // This method finds and displays the date of the next scheduled event
    public void getNextEventDate() {
        // If there are no events, set the nextEventDate label to "No events"
        if (Event.eventDetails.isEmpty()) {
            nextEventDate.setText("   No events");
            return;
        }

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Initialize nextDate to null
        LocalDate nextDate = null;

        // Iterate through the events to find the next scheduled event date
        for (Event event : Event.eventDetails) {
            LocalDate eventDate = event.getEventDate();
            if ((eventDate.isAfter(currentDate) || eventDate.isEqual(currentDate)) &&
                    (nextDate == null || eventDate.isBefore(nextDate))) {
                nextDate = eventDate;
            }
        }

        // If a next event date is found display it
        if (nextDate != null) {
            nextEventDate.setText("   " + nextDate);
        }
    }

    // work done by- Lakshan
    // Display number of club advisors
    public void displayNumberOfClubAdvisors() {
        numberOfClubs.setText(String.valueOf(Club.clubDetailsList.size()));
    }

    // work done by- Lakshan
    // This method is responsible on filtering selected club related event details and put them into a table
    @FXML
    void filterSelectedClubEvents(ActionEvent event) {
        populateEventList(viewCreatedEventsTable, viewCreatedEventsSortComboBox);
    }

    // work done by- Lakshan
    // This method selects a club from the ComboBox and populate the relevant details to a table
    // Track attendance sequence - 1.1 : populateEventList()
    public void populateEventList(TableView<Event> table, ComboBox<String> comboBoxName) {
        // List to store dates of the selected events
        ArrayList<LocalDate> selectedEventDates = new ArrayList<>();

        // Clear existing items in the table
        table.getItems().clear();

        // List to store filtered events based on the selected club
        ArrayList<Event> filteredEvents = new ArrayList<>();

        // Get the selected club from the comboBox
        String selectedClub = comboBoxName.getSelectionModel().getSelectedItem();

        // Null check before comparing selectedClub
        if (selectedClub != null) {
            // Check if the club is "All Clubs"
            if (selectedClub.equals("All Clubs")) {
                // Check the club that has to be populated is generate report view table
                if (table == generateReportEventViewTable) {
                    // If it is the generateReportViewTable, populate that table
                    populateGenerateReportEventsTable();
                } else {
                    // If it is not in generateReportViewTable, populate all other event related tables
                    populateEventsTables();
                }
                return;
            } else {
                // If the selected club is not all "All Clubs", collect the events only relevant to the selected club
                for (Event events : Event.eventDetails) {
                    // Check club name equals to the selected club
                    if (events.getClubName().equals(selectedClub)) {
                        filteredEvents.add(events);
                    }
                }
            }

            // Counter to keep track of the number of events for the selected club
            int count = 0;

            // Iterate through the filtered events to create new Events objects and populate the table
            for (Event value : filteredEvents) {
                // Retrieve the hosting club details for the event
                Club hostingClubDetail = value.getHostingClub();

                // Create a new event object with relevant details to display in the selected table
                Event requiredEvent = new Event(value.getEventName(), value.getEventLocation(),
                        value.getEventType(), value.getEventDeliveryType(), value.getEventDate(),
                        value.getEventTime(), hostingClubDetail, value.getEventDescription(), value.getEventId());

                // Retrieve the current items in the table
                ObservableList<Event> viewScheduledEvents = table.getItems();

                // Add an event object to the table
                viewScheduledEvents.add(requiredEvent);

                // Set the updated list of events to the table
                table.setItems(viewScheduledEvents);

                // Increment the event count
                count++;

                // Add the event date to the list of selected event dates
                selectedEventDates.add(value.getEventDate());
            }

            // Check if there are selected event dates
            if (!selectedEventDates.isEmpty()) {
                // fins the earliest and most future dates among the selected event dates
                selectedUpcomingDate = findEarliestDate(selectedEventDates);
                selectedMostFutureDate = findMostFutureDate(selectedEventDates);
            }

            // Check if the table is the generateReportEventViewTable
            if (table == generateReportEventViewTable) {
                // Check if there are any events to when generating reports using event date list
                if (!selectedEventDates.isEmpty()) {
                    // Update the label with the total count of upcoming events for generating reports
                    UpcomingEventCountGenerateReports.setText("Total :  " + count);

                    // Update the label with the date range for generating reports
                    eventDateRange.setText(selectedUpcomingDate + " - " + selectedMostFutureDate);
                } else {
                    // Update the label with the total count of events when no events are selected for generating reports
                    UpcomingEventCountGenerateReports.setText("Total :  " + count);

                    // Update the label to indicate that there are no events for generating reports
                    eventDateRange.setText("No Events");
                }

            }
        }
    }

    // work done by- Lakshan
    // This method calculates and displays the count of male and female students in a bar chart
    public void findMaleFemaleStudentCount() {
        // Initialize the count for both male and female students
        int maleRate = 0;
        int femaleRate = 0;


        // Iterate through the student details array
        for (Student student : Student.studentDetailArray) {
            // Checking the gender of the registered student and updating its corresponding counter value
            if (student.getGender() == 'M') {
                maleRate++;
            } else {
                femaleRate++;
            }
        }

        // Create a new series for the XYChart to hold the data
        XYChart.Series setOfData = new XYChart.Series();

        // Add data points for male and female student counts to the series
        setOfData.getData().add(new XYChart.Data<>("Male", maleRate));
        setOfData.getData().add(new XYChart.Data<>("Female", femaleRate));

        // Add the series to the GenderRatioChart
        GenderRatioChart.getData().addAll(setOfData);

    }

    // work done by- Lakshan
    // This method displays the count of enrolled students in each grade using a bar chart
    public void displayEnrolledStudentCount() {
        // Create a HashMap to store the count of students for each grade
        HashMap<Integer, Integer> studentGrade = new HashMap<>();


        // Iterate through the student details array
        for (Student student : Student.studentDetailArray) {
            // Get the grade of the student
            int grade = student.getStudentGrade();

            // Update the count for the corresponding grade for the HashMap
            studentGrade.put(grade, studentGrade.getOrDefault(grade, 0) + 1);
        }

        // Create a new series for XYChart to hold the data
        XYChart.Series setOfData = new XYChart.Series();

        // Iterate through the entries in the studentGrade HashMap and add data point to its series
        for (Map.Entry<Integer, Integer> entry : studentGrade.entrySet()) {
            setOfData.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        // Add the series to the EnrollStudentCountEachGrade chart
        EnrollStudentCountEachGrade.getData().addAll(setOfData);

    }


    // work done by- Arkhash
    public void populateAttendanceClubNameComboBox() {
        // the club name combo box is cleared
        attendanceClubNameComboBox.getItems().clear();

        if(!attendanceClubNameComboBox.getItems().contains("Please Select")){
            // the default option of please select is added to the combo box
            attendanceClubNameComboBox.getItems().add("Please select");
        }

        // the following is done for every club in the clubDetailsList
        for(Club club : clubDetailsList){
            // the club names are added to the combo box from the array list
            attendanceClubNameComboBox.getItems().add(club.getClubName());
        }
        // retrieves the current selection in the combo box
        attendanceClubNameComboBox.getSelectionModel().selectFirst();
    }

    // work done by- Arkhash
    @FXML
    void populateEventList(ActionEvent event) {
        // a new array list to hold the events filtered to the respective club
        ArrayList<Event> filteredEvents = new ArrayList<>();
        // the selected club name is retrieved from the club name combo box
        String selectedClub = attendanceClubNameComboBox.getSelectionModel().getSelectedItem();

        // if no club is selected, the method is returned (not executed)
        if(selectedClub == null){
            return;
        }

        // if "Please Select" is chosen, the method is returned (not executed)
        if(selectedClub.equals("Please Select")){
            return;
        }else{
            // a object of data type Event is created to iterate over the eventDetails array list
            for(Event events : Event.eventDetails){
                if(events.getClubName().equals(selectedClub)){
                    filteredEvents.add(events); // the events for the respective club is added to the areay list
                }
            }
        }
        // the event name combo box is cleared
        attendanceEventNameComboBox.getItems().clear();

        // check if the option "Please select" is already available
        if(!attendanceEventNameComboBox.getItems().contains("Please Select")){
            // option is added if not available
            attendanceEventNameComboBox.getItems().add("Please select");
        }

        // an object event1 of data type Event is created to iterate over the filteredEvents array list
        for(Event event1 : filteredEvents){
            // the events are retrieved and added
            attendanceEventNameComboBox.getItems().add(event1.getEventName());
        }
        // retrieves the current selection in the combo box
        attendanceEventNameComboBox.getSelectionModel().selectFirst();
    }



    // work done by- Arkhash
    // Track attendance sequence 1.1.1.2.1 : selectStudentsForEvents
    @FXML
    void selectStudentsForEvents(ActionEvent event) {

        // an object of data type Event is created and set to null initially
        Event eventToBeTracked = null;
        // the name of the event is retrieved from the combo box value
        String eventName = attendanceEventNameComboBox.getValue();

        // the string value is set to null
        String eventRelatedClubName = null;
        // a for loop is iterated for each entry in the eventDetails Array list
        for (Event event1 : Event.eventDetails) {
            // if the event name in the array list is equal to the value in the combo box
            if (event1.getEventName().equals(eventName)) {
                eventRelatedClubName = event1.getHostingClub().getClubName();
            /*the details of that specific entry is assigned to
            the event variable declared initially at the beginning of the method.*/
                eventToBeTracked = event1;
                break; // break statement is used to move out of the loop
            }
        }

        if (eventToBeTracked == null) {
            return;
        }

        // An array list of data type Student is created
        ArrayList<Student> studentAttendanceList = new ArrayList<>();

        // Iterate through joinedClubForEachStudent
        for (Map.Entry<Student, ArrayList<Club>> entry : ClubAdvisorDataBaseManager.joinedClubForEachStudent.entrySet()) {
            Student student = entry.getKey();
            ArrayList<Club> joinedClubs = entry.getValue();

            // Check if the student has joined the club related to the event
            for (Club club : joinedClubs) {
                if (club.getClubName().equals(eventRelatedClubName)) {
                    studentAttendanceList.add(student);
                    break; // a break statement is used to move out of the loop
                }
            }
        }

        // Create attendance objects
        for (Student student : studentAttendanceList) {
            CheckBox attendanceCheckBox = new CheckBox();  // a checkbox is created
            // an object of data type Attendance is created with the initial attendance status set to false
            Attendance attendance = new Attendance(false, student, eventToBeTracked, attendanceCheckBox);
            boolean attendanceStat = true;

        /* for each entry in the eventAttendance Array list, if the student is the same as
        the student in the sheet, then the loop is exited */
            for (Attendance attendance1 : eventToBeTracked.eventAttendance) {
                if ((student.getStudentAdmissionNum() == attendance1.student.getStudentAdmissionNum())) {
                    attendanceStat = false;
                    break;
                }// a break statement is used to move out of the loop
            }

            // if the boolean value is true
            if (attendanceStat) {
                // the object attendance is added to the variable of data type Event
                eventToBeTracked.eventAttendance.add(attendance);
            }
        }

        // for each entry in the Array list
        for (Attendance attendance2 : eventToBeTracked.eventAttendance) {
            System.out.println(attendance2.student.getStudentAdmissionNum());
            System.out.println(attendance2.student.getFirstName());
            System.out.println(attendance2.getClubName());
            System.out.println(attendance2.getEventName());
        }
        System.out.println(eventToBeTracked.eventAttendance.size());

        attendanceTrackerTable.getItems().clear();
        // an observable array list is created to add the details into the table view
        ObservableList<Attendance> attendanceObservableList = FXCollections.observableArrayList(eventToBeTracked.eventAttendance);

        // the table columns are initialized
        attendanceClubNameColumn.setCellValueFactory(new PropertyValueFactory<>("clubName"));
        attendanceEventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        attendanceStudentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        attendanceStudentAdmissionNumColumn.setCellValueFactory(new PropertyValueFactory<>("studentAdmissionNum"));
        attendanceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("attendanceTracker"));

        // the details in the observable array list are set to the table view
        attendanceTrackerTable.setItems(attendanceObservableList);
    }


    // work done by-Arkhash
    // Track attendance sequence - 1.1.1.2.1.2.1 : onAttendanceSubmitButtonClick()
    @FXML
    void onAttendanceSubmitButtonClick(ActionEvent event) {
        // Get the selected event from the combo box
        Event trackingEvent = selectAttendenceTrackingEvent(attendanceEventNameComboBox.getValue());

        // the method is returned if the event is null
        if (trackingEvent == null) {
            return;
        }

        // Iterate through the rows and update the boolean status based on checkbox state
        for (Attendance attendance : attendanceTrackerTable.getItems()) {
            boolean isChecked = attendance.getAttendanceTracker().isSelected(); // Assuming getAttendanceTracker() returns the checkbox state
            // Track attendance sequence : 1.1.1.2.1.2.1.1 :  setAttendanceStatusProperty
            attendance.setAttendanceStatusProperty(isChecked); ; // Assuming setAttendanceStatus(boolean) sets the boolean status
        }

        // Retrieve the attendance data from the table before clearing it
        ObservableList<Attendance> attendanceData = FXCollections.observableArrayList(attendanceTrackerTable.getItems());

        // Clear the current items in the attendanceTrackerTable
        attendanceTrackerTable.getItems().clear();

        ClubAdvisor clubAdvisor = new ClubAdvisor();
        // Attendance tracking sequence : 1.1.1.2.1.2.1.1.1.1 : TrackAttendance()
        clubAdvisor.TrackAttendance(trackingEvent, attendanceData);

        // Repopulate the attendanceTrackerTable if necessary
        populateAttendanceClubNameComboBox();

        // Loop through the eventAttendance list of the trackingEvent
        for (Attendance attendance : trackingEvent.eventAttendance) {
            System.out.println(attendance.getStudentName() + " " + attendance.attendanceStatusProperty());
        }

        // If no matching event is found, display the error
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("School Club Management System");
        alert.setHeaderText("Attendence of the event" + trackingEvent.getEventName() + " updated successfully !!!");
        alert.showAndWait();
    }


    // work done by- Arkhash
    public Event selectAttendenceTrackingEvent(String eventName){
        for(Event event : Event.eventDetails){
            if(event.getEventName().equals(eventName)){
                return event;
            }
        }

        return null;
    }

    // work done by- Lakshan
    // This method handles dragging of the club advisor dashboard when the mouse is detected
    @Override
    void ClubAdvisorDashboardDetected(MouseEvent event) {
        // Get the current stage
        Stage stage = (Stage) ClubAdvisorDashboard.getScene().getWindow();

        // Set the new position of the stage based on mouse coordinates
        stage.setX(event.getScreenX() - xPosition);
        stage.setY(event.getScreenY() - yPosition);
    }

    // work done by- Lakshan
    // This method is used to set the initial mouse coordinates when the club advisor pane is pressed
    @Override
    void ClubAdvisorPanePressed(MouseEvent event) {
        // Set the initial mose coordinates for dragging
        xPosition = event.getSceneX();
        yPosition = event.getSceneY();
    }

    // work done by- Lakshan
    // This method handles logging out the club Advisor dashboard and navigates to the club advisor login page
    @Override
    void dashBoardLogOut(MouseEvent event) throws IOException {
        // Load the club advisor login page
        Parent root = FXMLLoader.load(getClass().getResource("/LoginDashboardManager/ClubAdvisorLogin.fxml"));

        // Get the reference to the current stage
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set the new scene and show the stage
        scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    // work done by- Lakshan
    // This method minimize the application when called
    @Override
    void MinimizePane(ActionEvent event) {
        // Create an instance of ApplicationController and call the minimize app method
        ApplicationController applicationController = new ApplicationController();
        applicationController.MinimizeApp(ClubAdvisorDashboard);
    }

    // work done by- Lakshan
    // This method closes the application when called
    @Override
    void ClosePane(ActionEvent event) {
        // Create an instance of ApplicationController and call the closingApp method
        ApplicationController applicationController = new ApplicationController();
        applicationController.closingApp();
    }

    // work done by- Lakshan
    // This method will make all club advisor panes invisible
    @Override
    public void makeAllClubAdvisorPanesInvisible() {
        dashboardMainPane.setVisible(false); // set dashboardMainPane invisible
        ManageClubPane.setVisible(false); // set  ManageClubPane invisible
        ScheduleEventsPane.setVisible(false); // set ScheduleEventsPane invisible
        AttendancePane.setVisible(false); // set AttendancePane invisible
        GenerateReportsPane.setVisible(false); // set  GenerateReportsPane invisible
        ProfilePane.setVisible(false); // set  ProfilePane invisible
    }

    // work done by- Lakshan
    /* This method will set all the club advisor dashboard left pane buttons to linear gradient color,
     * This is done to highlight the currently working pane related buttons */
    @Override
    public void makeAllButtonsColoured() {
        dashboardButton.setStyle("-fx-background-color: linear-gradient(#ffffd2, #f6d59a, #f6d59a);");
        ManageclubButton.setStyle("-fx-background-color: linear-gradient(#ffffd2, #f6d59a, #f6d59a);");
        ScheduleEventsButton.setStyle("-fx-background-color: linear-gradient(#ffffd2, #f6d59a, #f6d59a);");
        AttendanceButton.setStyle("-fx-background-color: linear-gradient(#ffffd2, #f6d59a, #f6d59a);");
        GenerateReportsButton.setStyle("-fx-background-color: linear-gradient(#ffffd2, #f6d59a, #f6d59a);");
        AdvisorProfileButton.setStyle("-fx-background-color: linear-gradient(#ffffd2, #f6d59a, #f6d59a);");
    }

    // work done by- Lakshan
    // This method will direct to the dashboard club advisor pane
    @Override
    void GoToDashBoardClubAdvisor(ActionEvent event) {
        // Making all club advisor panes invisible
        makeAllClubAdvisorPanesInvisible();
        // calling the makeAllButtonsColoured method to color all buttons
        makeAllButtonsColoured();
        // make dashboardMainPane visible
        dashboardMainPane.setVisible(true);
        // highlight the dashboard button
        dashboardButton.setStyle("-fx-background-color: linear-gradient(#fafada, #ffffd2)");
        // display the number of club advisors
        displayNumberOfClubAdvisors();
    }

    // work done by- Lakshan
    // This method will direct to the ManageClubPane pane
    @Override
    void GoToManageClubPane(ActionEvent event) {
        // Making all club advisor panes invisible
        makeAllClubAdvisorPanesInvisible();

        // calling the makeAllButtonsColoured method to color all buttons
        makeAllButtonsColoured();

        // make ManageClubPane visible
        ManageClubPane.setVisible(true);

        // highlight the Manage club button
        ManageclubButton.setStyle("-fx-background-color: linear-gradient(#fafada, #ffffd2)");

        // set the club If to clubId label
        clubId.setText(String.valueOf(clubIdSetterValue));

        // set create and update table values
        setCreateTable();
        setUpdateTable();
    }

    // work done by- Lakshan
    // This method will direct to the ScheduleEvents pane
    @Override
    void GoToScheduleEvents(ActionEvent event) {
        // Making all club advisor panes invisible
        makeAllClubAdvisorPanesInvisible();

        // calling the makeAllButtonsColoured method to color all buttons
        makeAllButtonsColoured();

        // make ScheduleEvents visible
        ScheduleEventsPane.setVisible(true);

        // highlight the  ScheduleEvents button
        ScheduleEventsButton.setStyle("-fx-background-color: linear-gradient(#fafada, #ffffd2)");
        getCreatedClubs();

        // Clear all update event labels
        clearAllUpdateEventLabels();

        // Clear all schedule event labels
        clearAllScheduleEventLabels();
    }

    // work done by- Lakshan
    // This method will direct to the TrackAttendance pane
    @Override
    void GoToTrackAttendance(ActionEvent event) {
        // Making all club advisor panes invisible
        makeAllClubAdvisorPanesInvisible();

        // calling the makeAllButtonsColoured method to color all buttons
        makeAllButtonsColoured();

        // make AttendancePane visible
        AttendancePane.setVisible(true);

        // highlight the  AttendancePane  button
        AttendanceButton.setStyle("-fx-background-color: linear-gradient(#fafada, #ffffd2)");

        // populate Attendance Club Name combo Box
        populateAttendanceClubNameComboBox();

        attendanceTrackerTable.getItems().clear();
    }

    // work done by- Lakshan
    @Override
    void GoToGenerateReports(ActionEvent event) {
        // Making all club advisor panes invisible
        makeAllClubAdvisorPanesInvisible();

        // calling the makeAllButtonsColoured method to color all buttons
        makeAllButtonsColoured();

        // make  GenerateReportsPane visible
        GenerateReportsPane.setVisible(true);

        // highlight the GenerateReportsButton  button
        GenerateReportsButton.setStyle("-fx-background-color: linear-gradient(#fafada, #ffffd2)");

        // populate the attendance table
        /*populateAttendanceTable();*/

        // populate generate report table
        populateGenerateReportEventsTable();

        // populate Generate report clubs generate report club name combo box
        populateGenerateReportClubs(generateReportClubNameComboBox);

        //populated Generate report ClubName Combo Box
        populateGenerateReportClubNameComboBox();

    }

    // work done by- Lakshan
    // This method will direct to the club advisor profile pane
    @Override
    void GoToClubAdvisorProfile(ActionEvent event) {
        // Making all club advisor panes invisible
        makeAllClubAdvisorPanesInvisible();

        // calling the makeAllButtonsColoured method to color all buttons
        makeAllButtonsColoured();

        // make visible profile pane
        ProfilePane.setVisible(true);

        // highlight the AdvisorProfileButton button
        AdvisorProfileButton.setStyle("-fx-background-color: linear-gradient(#fafada, #ffffd2)");

        // call display student update details method
        displayStudentUpdateDetails();
    }

    // work done by- Lakshan
    // This method will direct to the EventAttendance pane
    @Override
    void GoToEventAttendance(ActionEvent event) {
        // Making all generates report panes invisible
        makeAllPanesInvisibleGeneratingReport();

        // Making EventAttendancePane visible
        EventAttendancePane.setVisible(true);

        // highlight the GoToEventAttendanceButton button
        GoToEventAttendanceButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");
        populateGenerateReportClubNameComboBox();
        totalAbsentStudents.setText("0 students");
        totalAttendedStudents.setText("0 students");
        totalStudentCountAttendance.setText("0 students");
        generateReportAttendanceButton.setDisable(true);
    }

    // work done by- Lakshan
    // This method will direct to the ClubActivities pane
    @Override
    void GoToClubActivities(ActionEvent event) {
        // Making all generates report panes invisible
        makeAllPanesInvisibleGeneratingReport();

        // Making  ClubActivitiesPane visible
        ClubActivitiesPane.setVisible(true);

        // highlight the  GoToClubActivitiesButton button
        GoToClubActivitiesButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");

        // populate the generateReportClubNameComboBox with club names
        populateGenerateReportClubs(generateReportClubNameComboBox);

        // populate generate report event tables
        populateGenerateReportEventsTable();
    }

    // work done by- Lakshan
    // This method will direct to club membership report pane
    @Override
    void GoToClubMembership(ActionEvent event) {
        // Making all generates report panes invisible
        makeAllPanesInvisibleGeneratingReport();

        // Making MembershipReportPane visible
        MembershipReportPane.setVisible(true);

        // highlight the  GoToClubMembershipButton button
        GoToClubMembershipButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");
    }

    // work done by- Lakshan
    // This method will make all generate report panes invisible
    @Override
    public void makeAllPanesInvisibleGeneratingReport() {
        ClubActivitiesPane.setVisible(false); // make club activities pane invisible
        EventAttendancePane.setVisible(false); // make event attendance pane invisible
        MembershipReportPane.setVisible(false); // make membership report pane invisible
        RegistrationReportPane.setVisible(false); // make registration report pane invisible
        // make club membership button coloured
        GoToClubMembershipButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black;");

        // make GoToEventAttendance button coloured
        GoToEventAttendanceButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black;");

        // make GoToClubActivitiesButton button coloured
        GoToClubActivitiesButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black;");

        // make  GoToRegistrationButton button coloured
        GoToRegistrationButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black;");
    }

    // work done by- Lakshan
    // This make all event panes invisible
    @Override
    public void makeAllPanesInvisibleEventPane() {
        UpdatesEventPane.setVisible(false); // make update event pane invisible
        ViewEventsPane.setVisible(false); // make update view events pane invisible
        ScheduleEventsInnerPane.setVisible(false); // make schedule events inner pane invisible
        CancelEventsPane.setVisible(false); // make cancel events pane invisible

        // make update event button colored
        UpdateEventButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black;");

        // make view event button colored
        ViewEventButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black");

        // make schedule event button coloured
        ScheduleEventButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d)" +
                ";-fx-text-fill: black");

        // make cancel event button coloured
        CancelEventButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black");
    }

    // work done by- Lakshan
    // This method will direct to update events pane
    @Override
    void GoToUpdateEventsPanes(ActionEvent event) {
        // Hides all other panes related to event management
        makeAllPanesInvisibleEventPane();

        // Makes the ScheduleEventsInnerPane visible
        UpdatesEventPane.setVisible(true);

        // Updates the style of the ScheduleEventButton
        UpdateEventButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");

        // Retrieves information about created clubs
        getCreatedClubs();

        // Clears any existing labels related to scheduling events
        clearAllUpdateEventLabels();

        // Clears input fields for updating events
        clearUpdateEventFields();

        // Disables all input fields for updating events
        disableAllUpdateEventFields();

        // Disables the update and clear buttons
        updateEventFieldButton.setDisable(true);
        clearEventFieldButton.setDisable(true);
    }

    // work done by- Lakshan
    // Direct to view events pane
    @Override
    void GoToViewEventsPane(ActionEvent event) {
        // Hides all other panes related to event management
        makeAllPanesInvisibleEventPane();

        // Makes the ViewEventsPane visible
        ViewEventsPane.setVisible(true);

        // Updates the style of the ViewEventButton
        ViewEventButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");
    }

    // work done by- Lakshan
    // Direct to schedule events pane
    @Override
    void GoToScheduleEventsPane(ActionEvent event) {
        // make all event panes invisible
        makeAllPanesInvisibleEventPane();

        // Makes the ScheduleEventsInnerPane visible
        ScheduleEventsInnerPane.setVisible(true);

        // Updates the style of the ScheduleEventButton
        ScheduleEventButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");

        // Retrieves information about created clubs
        getCreatedClubs();

        // Clears any existing labels related to scheduling events
        clearAllScheduleEventLabels();
    }

    // work done by- Lakshan
    // Direct to cancel events pane
    @Override
    void GoToCancelEventsPane(ActionEvent event) {
        // make all event panes invisible
        makeAllPanesInvisibleEventPane();

        // Makes the CancelEventsPane visible
        CancelEventsPane.setVisible(true);

        // Updates the style of the CancelEventButton
        CancelEventButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");
    }

    // work done by- Lakshan
    // Make all club creation panes invisible
    @Override
    public void makeAllClubCreationPanesInvisible() {
        createClubPane.setVisible(false); // make created club panes invisible
        UpdateClubDetailPane.setVisible(false); // make UpdateClubDetailPane invisible
        // style CreateClubDirectorButton
        CreateClubDirectorButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black;");
        // style UpdateClubDirectorButton
        UpdateClubDirectorButton.setStyle("-fx-background-color: linear-gradient(to right, #165a6d, #6aa9bc, #6aa9bc, #165a6d);" +
                "-fx-text-fill: black;");
    }

    // work done by- Lakshan
    // Direct to create club pane
    @Override
    void GoToCreateClubPane(ActionEvent event) {
        makeAllClubCreationPanesInvisible(); // Make all club creation panes invisible
        createClubPane.setVisible(true); // make createdClubPane visible
        // Updates the style of the CreateClubDirectorButton
        CreateClubDirectorButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");
        // set club name error label empty
        clubNameError.setText("");
        // set club description error label empty
        clubDescriptionError.setText("");
    }

    // work done by- Lakshan
    // Direct to update club details pane
    @Override
    void GoToUpdateClubDetailsPane(ActionEvent event) {
        // Make all club creation panes invisible
        makeAllClubCreationPanesInvisible();
        // make UpdateClubDetailPane visible
        UpdateClubDetailPane.setVisible(true);
        // Updates the style of the UpdateClubDirectorButton
        UpdateClubDirectorButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");
        // set update club name error label empty
        updateClubNameError.setText("");
        // set update description error label empty
        updateClubDescriptionError.setText("");

        //Resetting the update club text fields
        updateClubID.setText("");
        updateClubName.setText("");
        updateClubDescription.setText("");

        //Disabling the update button until user selects a club to update
        updateClubImageButton.setDisable(true);
        updateClubButton.setDisable(true);
    }

    // work done by- Lakshan
    // Direct to registration pane
    @FXML
    void GoToRegistration(ActionEvent event) {
        // Make all generate report pane invisible
        makeAllPanesInvisibleGeneratingReport();
        // set registration report pane visible
        RegistrationReportPane.setVisible(true); // wrong
        // Updates the style of the GoToRegistrationButton
        GoToRegistrationButton.setStyle("-fx-text-fill: white; " +
                "-fx-background-color: linear-gradient(to right, #2b6779, #003543, #003543, #2b6779);");
    }


    static {
        if (clubDetailsList.isEmpty()) {
            clubIdSetterValue = 100;
        } else {
            clubIdSetterValue = ClubAdvisorDataBaseManager.lastClubIndex + 1;
        }
    }

    // work done by- Pramuditha and Deelaka
    @FXML
    void advisorProfileUpdateChecker(ActionEvent event) {
        //Setting the valid state as true
        validStat = true;

        //Getting the advisor id
        int advisorId = Integer.parseInt(profileAdvisorId.getText());
        //Getting the new advisor details to update
        String advisorFirstName = profileAdvisorFname.getText();
        String advisorLastName = profileAdvisorLname.getText();
        String advisorUsername = profileAdvisorUsername.getText();
        String advisorContactNumber = profileAdvisorCnumber.getText();
        //Getting the advisor password from arraylist to create the object
        String advisorPassword = clubAdvisorDetailsList.get(0).getPassword();

        //Creating a ClubAdvisor object to validate data entered by the user
        ClubAdvisor clubAdvisor = new ClubAdvisor(advisorUsername, advisorPassword, advisorFirstName, advisorLastName, advisorContactNumber, advisorId);

        //Setting all validate statuses as correct before validation
        ClubAdvisor.fNameValidateStatus = "correct";
        ClubAdvisor.lNameValidateStatus = "correct";
        ClubAdvisor.contactNumberValidateStatus = "correct";
        ClubAdvisor.passwordValidateStatus = "correct";
        ClubAdvisor.userNameValidateStatus = "correct";

        //Validating advisor first name to get a valid data
        if (!clubAdvisor.validateFirstName()) {
            validStat = false;
        }
        //Displaying an error if the user entered data for advisor first name is invalid so
        displayNameError("firstName");

        //Validating advisor last name to get a valid data
        if (!clubAdvisor.validateLastName()) {
            validStat = false;
        }
        //Displaying an error if the user entered data for advisor last name is invalid so
        displayNameError("lastName");

        try {
            //Getting the contact number for a temporary variable
            String tempContactNum = advisorContactNumber;
            //If contact number is empty
            if (tempContactNum.isEmpty()) {
                User.contactNumberValidateStatus = "empty";
                throw new Exception();
            }
            Double.parseDouble(advisorContactNumber.trim());
            ClubAdvisor clubAdvisor1 = new ClubAdvisor(tempContactNum);

            //Validating advisor contact number
            if (!clubAdvisor1.validateContactNumber()) {
                validStat = false;
            } else {
                User.contactNumberValidateStatus = "";
            }
        } catch (NumberFormatException e) {
            User.contactNumberValidateStatus = "format";
            validStat = false;
        } catch (Exception e) {
            validStat = false;
        }
        //Displaying an error if the user entered data for advisor contact number is invalid
        displayContactNumError();

        //Validating advisor username
        if (!clubAdvisor.validateUserName("updation", "advisor")) {
            validStat = false;
        } else {
            User.userNameValidateStatus = "";
        }
        //Displaying an error if the user entered data for advisor username is invalid
        displayUserNameError();

        if (validStat) {
            //When all details are valid
            for (ClubAdvisor foundClubAdvisor : clubAdvisorDetailsList) {
                //Searching for the relevant club advisor in the detail list
                if (advisorId == foundClubAdvisor.getClubAdvisorId()) {
                    //Updating the advisor's details
                    foundClubAdvisor.setClubAdvisorId(advisorId);
                    foundClubAdvisor.setFirstName(advisorFirstName);
                    foundClubAdvisor.setLastName(advisorLastName);
                    foundClubAdvisor.setUserName(advisorUsername);
                    foundClubAdvisor.setContactNumber(advisorContactNumber);

                    //Updating the details of the club advisor in database
                    String updatedPersonalDetailsQuery = "UPDATE TeacherInCharge set TICFName = ?, TICLName = ?, " +
                            "teacherContactNum = ? where teacherInChargeId = ?";
                    try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(updatedPersonalDetailsQuery)) {
                        preparedStatement.setString(1, advisorFirstName);
                        preparedStatement.setString(2, advisorLastName);
                        preparedStatement.setInt(3, Integer.parseInt(advisorContactNumber));
                        preparedStatement.setString(4, String.valueOf(advisorId));
                        preparedStatement.executeUpdate();

                    }catch (Exception e){
                        System.out.println(e);
                    }

                    String updateTeacherUserNameQuery = "update TeacherCredentials set teacherUserName = ? " + "where teacherInChargeId = ?";
                    // advisor username update query
                    try(PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(updateTeacherUserNameQuery)){
                        preparedStatement.setString(1, advisorUsername);
                        preparedStatement.setString(2, String.valueOf(advisorId));
                        preparedStatement.executeUpdate();
                        profileAdvisorUsername.setText(advisorUsername);
                        showUserNameClubAdvisor.setText(advisorUsername); // setting newly updated username to dashboard
                        showUserNameClubAdvisor.setStyle("-fx-text-alignment: center");
                    }catch (Exception e){
                        System.out.println(e);
                    }


                    //Alerting when the profile details successfully updated
                    Alert clubUpdateAlert = new Alert(Alert.AlertType.INFORMATION);
                    clubUpdateAlert.initModality(Modality.APPLICATION_MODAL);
                    clubUpdateAlert.setTitle("School Club Management System");
                    clubUpdateAlert.setHeaderText("Profile details successfully updated!!!");
                    clubUpdateAlert.show();
                }
            }
        }
    }

    // work done by- Pramuditha and Deelaka
    @FXML
    void advisorProfilePasswordChecker(ActionEvent event) throws SQLException {
        //Setting default validStat as true
        validStat = true;
        //Getting the data from text fields to create the object and validation
        int advisorId = Integer.parseInt(profileAdvisorId.getText());
        String advisorFirstName = profileAdvisorFname.getText();
        String advisorLastName = profileAdvisorLname.getText();
        String advisorUsername = profileAdvisorUsername.getText();
        String advisorContactNumber = profileAdvisorCnumber.getText();
        String advisorExistingPassword = profileAdvisorExistingpw.getText();
        String advisorNewPassword = profileAdvisorNewpw.getText();
        String advisorConfirmPassword = profileAdvisorConfirmpw.getText();

        //Searching through the advisor details list
        for (ClubAdvisor foundAdvisor : clubAdvisorDetailsList) {
            //Checking to see if the user entered password matches with the foundAdvisor's password in the advisor details list
            if (advisorExistingPassword.equals(foundAdvisor.getPassword())) {
                //Clearing existing password error
                profileAdvisorExistingpwError.setText("");
                //Creating a ClubAdvisor object for validation
                ClubAdvisor clubAdvisor = new ClubAdvisor(advisorUsername, advisorNewPassword, advisorFirstName, advisorLastName, advisorContactNumber, advisorId);

                //New password validation
                if (!clubAdvisor.validatePassword("update")) {
                    //Setting the validStat as false if the password is invalid
                    validStat = false;
                }
                //Displaying the password error if there is any
                displayPasswordError();

                //Validating confirm password value
                if (advisorConfirmPassword.isEmpty()) {
                    //If the confirm password value is empty error will be displayed
                    profileAdvisorConfirmpwError.setText("Cannot be empty.");
                    validStat = false;
                } else if (!advisorConfirmPassword.equals(advisorNewPassword)) {
                    //If the co0nfirm password value doesn't match the new password value a error will be displayed
                    profileAdvisorConfirmpwError.setText("Passwords do not match");
                    validStat = false;
                } else {
                    //If the confirm password value is valid error label will clear
                    profileAdvisorConfirmpwError.setText("");
                }

                //If all the values are valid password will be changed
                if (validStat) {
                    //Searching in the advisor details to find the respective advisor
                    for (ClubAdvisor foundClubAdvisor : clubAdvisorDetailsList) {
                        if (advisorId == foundClubAdvisor.getClubAdvisorId()) {
                            //Change the password in the advisor details list
                            foundClubAdvisor.setPassword(advisorNewPassword);

                            //Changing password of the advisor in the database
                            String updatedAdvisorCredentialsQueryt = "update TeacherCredentials set teacherUserName = ?, " +
                                    "teacherPassword  = ?  where teacherInChargeId = ?";

                            try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(updatedAdvisorCredentialsQueryt)) {
                                preparedStatement.setString(1, advisorUsername);
                                preparedStatement.setString(2, advisorConfirmPassword);
                                preparedStatement.setString(3, String.valueOf(advisorId));
                                preparedStatement.executeUpdate();
                            } catch (Exception e) {
                                System.out.println(e);
                            }


                            //Alerting when the password successfully changed

                            Alert clubUpdateAlert = new Alert(Alert.AlertType.INFORMATION);
                            clubUpdateAlert.initModality(Modality.APPLICATION_MODAL);
                            clubUpdateAlert.setTitle("School Club Management System");
                            clubUpdateAlert.setHeaderText("Profile password successfully changed!!!");
                            clubUpdateAlert.show();

                            //Clearing the text fields
                            profileAdvisorExistingpw.setText("");
                            profileAdvisorNewpw.setText("");
                            profileAdvisorConfirmpw.setText("");
                        }
                    }
                }
            } else {
                //If the user entered existing password is wrong error will display
                profileAdvisorExistingpwError.setText("Wrong password!");
            }
        }
    }


    // work done by- Pramuditha
    public void displayUserNameError() {
        if (User.userNameValidateStatus.equals("empty")) {
            //If the username is empty an error will show
            profileAdvisorUsernameError.setText("User name cannot be empty.");
        } else if (ClubAdvisor.userNameValidateStatus.equals("exist")) {
            //If the username is already taken an error will show
            profileAdvisorUsernameError.setText("Entered username already exists.");
        } else if (User.userNameValidateStatus.equals("blank")) {
            //If the username is blank an error will show
            profileAdvisorUsernameError.setText("Username cannot contain spaces.");
        } else if (User.userNameValidateStatus.equals("length")) {
            //If the username is invalid an error will show
            profileAdvisorUsernameError.setText("The length should be 5 to 10 characters.");
        } else {
            //When a valid username enters error will clear
            profileAdvisorUsernameError.setText("");
        }
    }

    // work done by- Pramuditha and Deelaka
    public void displayContactNumError() {
        if (User.contactNumberValidateStatus.equals("empty")) {
            //If the contact number field is empty an error will display
            profileAdvisorCnumberError.setText("Contact number cannot be empty.");
        } else if (User.contactNumberValidateStatus.equals("length")) {
            //If the contact number value isn't ten digits an error will display
            profileAdvisorCnumberError.setText("Contact number should be 10 digits.");
        } else if (User.contactNumberValidateStatus.equals("format")) {
            //If the contact number value is invalid an error will display
            profileAdvisorCnumberError.setText("It should contain only numbers.");
        } else {
            //When a valid contact number enters error will clear
            profileAdvisorCnumberError.setText("");
        }
    }

    // work done by- Pramuditha
    public void displayNameError(String nameType) {
        if (nameType.equals("firstName")) {
            //when name type is first name
            if (ClubAdvisor.fNameValidateStatus.equals("empty")) {
                //If name is empty an error will display
                profileAdvisorFnameError.setText("First Name cannot be empty.");
            } else if (ClubAdvisor.fNameValidateStatus.equals("format")) {
                //If name value contains digits or special characters an error will display
                profileAdvisorFnameError.setText("First Name can contain only letters.");
            } else {
                //If name value is valid error will clear
                profileAdvisorFnameError.setText("");
            }
        } else if (nameType.equals("lastName")) {
            //when name type is last name
            if (ClubAdvisor.lNameValidateStatus.equals("empty")) {
                //If name is empty an error will display
                profileAdvisorLnameError.setText("Last Name cannot be empty.");
            } else if (ClubAdvisor.lNameValidateStatus.equals("format")) {
                //If name value contains digits or special characters an error will display
                profileAdvisorLnameError.setText("Last name can contain only letters.");
            } else {
                //If name value is valid error will clear
                profileAdvisorLnameError.setText("");
            }
        }
    }

    // work done by- Pramuditha
    public void displayPasswordError() {
        if (User.passwordValidateStatus.equals("empty")) {
            //If the password state is "empty" respective error will be displayed
            profileAdvisorNewpwError.setText("New Password cannot be empty.");
        } else if (User.passwordValidateStatus.equals("format")) {
            //If the password state is "format" respective error will be displayed
            profileAdvisorNewpwError.setText("Password should consists of 8\ncharacters including numbers and\nspecial characters.");
        } else {
            //If the password is valid error will disappear
            profileAdvisorNewpwError.setText("");
        }
    }

    // work done by- Lakshan
    public void displayStudentUpdateDetails(){

        profileAdvisorId.setText(String.valueOf(ClubAdvisor.clubAdvisorDetailsList.get(0).getClubAdvisorId()));
        profileAdvisorFname.setText(String.valueOf(ClubAdvisor.clubAdvisorDetailsList.get(0).getFirstName()));
        profileAdvisorLname.setText(String.valueOf(ClubAdvisor.clubAdvisorDetailsList.get(0).getLastName()));
        profileAdvisorUsername.setText(String.valueOf(String.valueOf(ClubAdvisor.clubAdvisorDetailsList.get(0).getUserName())));
        String contactNumber = makeTenDigitsForNumber(Integer.parseInt(ClubAdvisor.clubAdvisorDetailsList.get(0).getContactNumber()));
        profileAdvisorCnumber.setText((contactNumber));
        username = String.valueOf(ClubAdvisor.clubAdvisorDetailsList.get(0).getUserName());
    }


    // work done by- Lakshan
    /* This method ensures that the input number is represented
     * as ten digit string, if not put zeros to the beginning*/
    public static String makeTenDigitsForNumber(int number) {
        // Convert the number to string
        String strNumber = Integer.toString(number);
        // If the number has less than 10 digits, add leading zeros
        if (strNumber.length() < 10) {
            StringBuilder zeros = new StringBuilder();
            for (int i = 0; i < 10 - strNumber.length(); i++) {
                zeros.append('0');
            }
            // combine leading zeros and the original number
            return zeros.toString() + strNumber;
        } else {
            // If the number has more than 10 digits, truncate it to the first ten digits
            return strNumber.substring(0, 10);
        }
    }

    // work done by- Pramuditha
    public void populateMembershipCombo(ComboBox<String> selectedCombo) {
        //Clearing the values in combo box
        selectedCombo.getItems().clear();
        //If the combo box doesn't have the "All Clubs" as a value it will be added
        if (!selectedCombo.getItems().contains("All Clubs")) {
            selectedCombo.getItems().add("All Clubs");
        }
        //Club names of the club which the respective advisor has created will be added to the combo box values
        for (Club club : clubDetailsList) {
            selectedCombo.getItems().add(club.getClubName());
        }
        //Setting the selected club as the first in the list
        selectedCombo.getSelectionModel().selectFirst();
    }

    // work done by- Pramuditha
    @FXML
    public void clubMembershipReportGenerator(ActionEvent event) {
        //Getting the selected club from the combo box
        String selectedClub = clubMembershipCombo.getSelectionModel().getSelectedItem();

        // Clear the UpdateViewTable
        clubMembershipTable.getItems().clear();

        //If user choose "All Clubs" members from all the club will be displayed
        if(selectedClub == null){
            return;
        }

        if (selectedClub.equals("All Clubs")){
            setMembershipTable();
        }else {//When the user selects a specific club
            //Creating a observable list to pass members details for table
            ObservableList<Student> observableMembersList = null;
            //Searching through the students list
            for (Student foundStudent : studentDetailArray) {
                //Getting clubs that "foundStudent" has joined
                ArrayList<Club> clubList = ClubAdvisorDataBaseManager.joinedClubForEachStudent.get(foundStudent);
                //Searching through that club list to see if "foundStudent" is a member of the club that user selected
                for (Club club : clubList) {
                    if (selectedClub.equals(club.getClubName())) {
                        // Check whether the sortedList is null and return the method, if it is null
                        if (studentDetailArray == null) {
                            return;
                        }

                        //If the "foundStudent" is a member of the selected club, a Student object with that "foundStudent" details will be created
                        Student tableStudent = new Student(foundStudent.getStudentAdmissionNum(),
                                foundStudent.getUserName(), foundStudent.getFirstName(), foundStudent.getLastName(),
                                foundStudent.getStudentGrade(), foundStudent.getGender(),
                                makeTenDigitsForNumber(Integer.parseInt(foundStudent.getContactNumber())));

                        //The object created above will be added to the observable list and sent to the table
                        observableMembersList = clubMembershipTable.getItems();
                        observableMembersList.add(tableStudent);

                        clubMembershipTable.setItems(observableMembersList);

                    }
                }
            }

            //Checking if the observable list is null
            if(observableMembersList != null){
                //Displaying the number of members
                membershipReportNumber.setText("Number of Members : " + observableMembersList.size());
            }else{
                //Displaying the number of members
                membershipReportNumber.setText("Number of Members : " + 0);
            }

        }
    }

    // work done by- Pramuditha
    public void setMembershipTable() {
        // Check whether the sortedList is null and return the method, if it is null
        if (studentDetailArray == null) {
            return;
        }

        // Clear the UpdateViewTable
        clubMembershipTable.getItems().clear();

        // Add Item details to the UpdateView Table using Sorted List
        ObservableList<Student> observableMembersList = null;
        for (Student student : studentDetailArray) {

            // Create an Item details object with the item details
            Student tableStudent = new Student(student.getStudentAdmissionNum(), student.getUserName(),
                    student.getFirstName(), student.getLastName(), student.getStudentGrade(), student.getGender(),
                    makeTenDigitsForNumber(Integer.parseInt(student.getContactNumber())));

            // Add the item details to the UpdateViewTable
            observableMembersList = clubMembershipTable.getItems();
            observableMembersList.add(tableStudent);
            clubMembershipTable.setItems(observableMembersList);
        }
        //Displaying the number of members
        membershipReportNumber.setText("Number of Members : " + observableMembersList.size());
    }

    // work done by- Pramuditha
    // Populate the given combo box with club names, including an option for "All clubs"
    public void populateGenerateReportClubs(ComboBox<String> selectedCombo) {
        selectedCombo.getItems().clear();

        // Adds "All Clubs" option if it doesn't already exist
        if (!selectedCombo.getItems().contains("All Clubs")) {
            selectedCombo.getItems().add("All Clubs");
        }

        // Adds each club name to the ComboBox
        for (Club club : clubDetailsList) {
            selectedCombo.getItems().add(club.getClubName());
        }

        // Selects the first item in the ComboBox
        selectedCombo.getSelectionModel().selectFirst();
    }

    // work done by- Lakshan
    // Populate the generateReportEventViewTable with events based on the selected club filter
    @FXML
    void populateGenerateReportsEventsFilteredTable(ActionEvent event) {
        populateEventList(generateReportEventViewTable, generateReportClubNameComboBox);
    }

    // work done by- Lakshan
    // Populate the generateReportEventViewTable with all events and updates relevant UI elements
    public void populateGenerateReportEventsTable() {
        ArrayList<LocalDate> selectedEventDates = new ArrayList<>();

        // Check if the eventDetails is null, if it is null, return the method
        if (Event.eventDetails == null) {
            return;
        }

        // Clears the table and adjust its column resizing policy
        generateReportEventViewTable.getItems().clear();
        generateReportEventViewTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // initialize the counter to calculate number of events
        int count = 0;
        ObservableList<Event> viewCreatedScheduledEvents = FXCollections.observableArrayList();

        // Iterate through the Event details list to put event values into the observable list
        for (Event value : Event.eventDetails) {
            Club hostingClub = value.getHostingClub();
            // create an event Object
            Event event = new Event(value.getEventName(), value.getEventLocation(),
                    value.getEventType(), value.getEventDeliveryType(), value.getEventDate(),
                    value.getEventTime(), hostingClub, value.getEventDescription(), value.getEventId());

            // Add the event object to the observable list
            viewCreatedScheduledEvents.add(event);
            selectedEventDates.add(event.getEventDate());
            // Increment the counter
            count++;
        }

        // Sets the items in the table and selects the first item in the combobox
        generateReportEventViewTable.setItems(viewCreatedScheduledEvents);
        generateReportClubNameComboBox.getSelectionModel().selectFirst();

        // Update UI event elements based on the event dates
        if (selectedEventDates != null && !selectedEventDates.isEmpty()) {
            // get the next event date
            selectedUpcomingDate = findEarliestDate(selectedEventDates);
            // get the most future event date
            selectedMostFutureDate = findMostFutureDate(selectedEventDates);
        } else {
            // set event date range labels
            UpcomingEventCountGenerateReports.setText("Total: " + count);
            eventDateRange.setText("No Events");
            return;
        }

        // set event date ranges if the club events length not equals to zero
        UpcomingEventCountGenerateReports.setText("Total: " + count);
        eventDateRange.setText(selectedUpcomingDate + " - " + selectedMostFutureDate);
    }

    // work done by- Lakshan
    // This method find and return the most future date from the given list of dates
    public static LocalDate findMostFutureDate(List<LocalDate> givenDateList) {
        // Checks if the input date list is null or empty
        if (givenDateList == null || givenDateList.isEmpty()) {
            // Throws an exception if the date list is null or empty
            throw new IllegalArgumentException("Date list cannot be null or empty.");
        }


        // Initializes with the first date in the list as the most future date
        LocalDate mostFutureDate = givenDateList.get(0);

        // Iterates through the list to find the most future date
        for (LocalDate date : givenDateList) {
            // Updates mostFutureDate if the current date is after the selected date
            if (date.isAfter(mostFutureDate)) {
                mostFutureDate = date;
            }
        }

        // Returns the most future date found in the list
        return mostFutureDate;
    }

    // work done by- Lakshan
    // This method finds and returns the earliest date from the given list of dates
    public static LocalDate findEarliestDate(List<LocalDate> givenDateList) {
        // Checks if the input date list is null or empty
        if (givenDateList == null || givenDateList.isEmpty()) {
            // Throws an exception if the date list is null or empty
            throw new IllegalArgumentException("Date list cannot be null or empty.");
        }

        // Initializes with the first date in the list
        LocalDate earliestDate = givenDateList.get(0);

        // Iterates through the list to find the earliest date
        for (LocalDate date : givenDateList) {
            // Updates earliestDate if the current date is before it
            if (date.isBefore(earliestDate)) {
                earliestDate = date;
            }
        }

        // Returns the earliest date found in the list
        return earliestDate;
    }

    // work done by- Deelaka
    public void populateClubAdvisorTable() {
        numberofAdvisors = 0; // for counting purpose of number of club advisors
        if (clubAdvisorDetailsList == null) {
            return;
        }

        registrationAdvisorTable.getItems().clear();

        for (ClubAdvisor clubAdvisor : clubAdvisorDetailsList) {
            numberofAdvisors += 1; // when a club advisor is found, increasing the numberofAdvisors by one
            ClubAdvisor clubAdvisor1 =  new ClubAdvisor(clubAdvisor.getUserName(), clubAdvisor.getPassword(),
                    clubAdvisor.getFirstName(), clubAdvisor.getLastName(), clubAdvisor.getContactNumber(),
                    clubAdvisor.getClubAdvisorId());

            ObservableList<ClubAdvisor> observableClubAdvisorRegistrationList = registrationAdvisorTable.getItems();
            observableClubAdvisorRegistrationList.add(clubAdvisor1);
            registrationAdvisorTable.setItems(observableClubAdvisorRegistrationList);
        }
        userCountLabel.setText("No of Advisors: " + (numberofAdvisors));
    }

    // work done by- Deelaka
    public void populateStudentRegisterTable(){
        numbeOfStudents = 0;
        if(studentDetailArray == null){
            return;
        }

        registrationStudentTable.getItems().clear();


        for(Student student : studentDetailArray) {
            numbeOfStudents +=1; // this is to show the number of selected users in
            Student student1 = new Student(student.getUserName(), student.getPassword(), student.getFirstName(),
                    student.getLastName(), student.getContactNumber(), student.getStudentAdmissionNum(),
                    student.getStudentGrade(), student.getGender());

            ObservableList<Student> observableStudentRegistrationList = registrationStudentTable.getItems();
            observableStudentRegistrationList.add(student1);
            registrationStudentAdmissionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentAdmissionNum")); // setting values to registrationStudentTable column
            registrationStudentUserName.setCellValueFactory(new PropertyValueFactory<>("userName")); // setting values to registrationStudentUserName column
            registrationStudentFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName")); // setting values to registrationStudentFirstNameColumn column
            registrationStudentLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName")); // setting values to registrationStudentLastNameColumn column
            registrationStudentGradeColumn.setCellValueFactory(new PropertyValueFactory<>("studentGrade")); // setting values to registrationStudentGradeColumn column
            registrationStudentContactNumberColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber")); // setting values to registrationStudentContactNumberColumn column
            registrationStudentGenderColumn.setCellValueFactory(new PropertyValueFactory<>("studentGender")); // setting values to registrationStudentGenderColumn column
            registrationStudentTable.setItems(observableStudentRegistrationList);

        }
        userCountLabel.setText("No of Students: " + (numbeOfStudents));
    }


    // work done by- Deelaka
    public void selectUserGettingFromComboBox(){ // this method is to obtain selected user from registrationUserSelectComboBox
        registrationUserSelectComboBox.getItems().addAll("Club Advisor","Student"); // passing values to registrationUserSelectComboBox
        registrationUserSelectComboBox.getSelectionModel().selectFirst(); // passing a default values to registrationUserSelectComboBox
    }


    // work done by- Deelaka
    public void onClickRegistrationGenerateReportButton(ActionEvent event){
        selectedUser = registrationUserSelectComboBox.getValue();
        if(selectedUser == "Student"){ // if selected user is Student
            registrationAdvisorTable.setVisible(false); // setting registrationAdvisorTable in-order to make visible student table
            registrationStudentTable.setVisible(true); // setting registrationStudentTable table visible
            populateStudentRegisterTable(); // when Student selected as the user respective table will visible

        }

        if(selectedUser == "Club Advisor"){
            registrationStudentTable.setVisible(false);// setting registrationStudentTable in-order to make visible student table
            registrationAdvisorTable.setVisible(true);// setting registrationAdvisorTable table visible
            populateClubAdvisorTable(); //
        }

    }

    // work done by- Pramuditha
    @FXML
    void GeneratePdfReportForMembership(ActionEvent event) throws IOException {
        ClubAdvisor clubAdvisor = new ClubAdvisor();
        clubAdvisor.generateMembershipDetailReport(clubMembershipTable, stage);
    }

    // work done by- Lakshan
    // This method handles generating report for events
    @FXML
    void GeneratePdfReportForEvents(ActionEvent event) throws IOException {
        // Creating and object of clubAdvisor
        ClubAdvisor clubAdvisor = new ClubAdvisor();
        // Call the generate report method
        clubAdvisor.generateEventDetailReport(generateReportEventViewTable, stage);
    }

    // work done by- Deelaka
    @FXML
    void GenerateRegistrationReport(ActionEvent event) throws IOException {
        ClubAdvisor clubAdvisor = new ClubAdvisor();
        if(registrationUserSelectComboBox.getValue().equals("Club Advisor")){
            clubAdvisor.generateClubAdvisorRegistrationDetailReport(registrationAdvisorTable, stage);
        }else{
            clubAdvisor.generateStudentRegistrationReport(registrationStudentTable, stage);
        }

    }

    // work done by- Arkhash
    @FXML
    // method to generate the report for attendance marking
    void GenerateAttendanceMarking(ActionEvent event) throws IOException {
        ClubAdvisor clubAdvisor = new ClubAdvisor(); // an object is created of data type Club Advisor
        // if the combo box value is "Please select"
        if(!(ReportAttendanceEventName.getValue().equals("Please Select") ||
                ReportAttendanceClubName.getValue().equals("Please Select"))){
            clubAdvisor.generateStudentAttendanceReport(generateReportAttendanceTable, stage);
        }else{
            // an alert is prompted
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("School Club Management System");
            alert.setHeaderText("Please Select an event to generate a report.");
            alert.show();
        }
    }

    // work done by- Arkhash
    // method to generate the CSV file for the attendance
    public static void generateAttendanceCsv(TableView<Attendance> tableView, Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser(); //
        // the file extension is chosen
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(stage);

        // If a file is selected, proceed to write the attendance data to the CSV file
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Call the method writeAttendanceContent to actually write the content to the CSV file
                writeAttendanceContent(writer, tableView);
                System.out.println("CSV generated and saved to: " + file.getAbsolutePath());
            }
        }else{
            // If no file is selected, return from the method
            return;
        }

        // alert to show the report has been generated successfully
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("School Club Management System");
        alert.setHeaderText("Report Generated Successfully");
        alert.show();
    }


     // work done by- Pramuditha
    //Generates and saves a CSV file containing the data from a TableView
    public static void generateMembershipCsv(TableView<Student> tableView, Stage stage) throws IOException {
        // Create a FileChooser for selecting the location to save the CSV file


        FileChooser fileChooser = new FileChooser();
        // Filter only to make csv files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Show the file chooser dialog box

        // Show the save dialog and get the selected file
        File file = fileChooser.showSaveDialog(stage);

        // Check if a file was selected
        if (file != null) {
            // Use file writer to write the content
            // Try-with-resources to ensure the FileWriter is properly closed
            try (FileWriter writer = new FileWriter(file)) {
                // Call the method to write the TableView content to the CSV file
                writeMembershipCsvContent(writer, tableView);
            }
        }else{
            // If no file was selected, return from the method
            return;
        }
        // Show the report generated successfully massage


        // Display an information alert to notify the user about successful report generation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("School Club Management System");
        alert.setHeaderText("Report Generated Successfully");
        alert.show();
    }


    // work done by- Arkhash
    // method to write content to the CSV file
    //Writes the contents of a TableView to a CSV file using the provided FileWriter
    private static void writeAttendanceContent(FileWriter writer, TableView<Attendance> tableView) throws IOException {
        // an observable list is created to set the values to the table
        ObservableList<TableColumn<Attendance, ?>> columns = tableView.getColumns();

        // the column headings are set
        for (TableColumn<Attendance, ?> column : columns) {
            // write function is used to write details onto the file
            writer.write(column.getText() + ",");
        }
        writer.write("\n");

        // for each entry in the table view the data is written using the write function of the FileWriter.
        for (Attendance attendance : tableView.getItems()) {
            for (TableColumn<Attendance, ?> column : columns) {
                String cellValue = column.getCellData(attendance).toString();
                writer.write(cellValue + ",");
            }
            writer.write("\n");
        }
    }

    // work done by- Pramuditha
    private static void writeMembershipCsvContent(FileWriter writer, TableView<Student> tableView) throws IOException {
        // Get the list of columns in the Table
        ObservableList<TableColumn<Student, ?>> columns = tableView.getColumns();

        // Write headers
        for (TableColumn<Student, ?> column : columns) {
            // Write each column header followed by a comma
            writer.write(column.getText() + ",");
        }
        // Move to the next line after writing all headers
        writer.write("\n");

        // Write data
        for (Student student : tableView.getItems()) {
            // Iterate through each row in the Table
            for (TableColumn<Student, ?> column : columns) {
                // Get the cell value for the current row and column
                String cellValue = column.getCellData(student).toString();
                // Write each cell value followed by a comma
                writer.write(cellValue + ",");
            }
            // Move to the next line after writing all cell values for the current row
            writer.write("\n");
        }
    }

    // work done by- Lakshan
    // This method generate csv for event objects
    public static void generateCsv(TableView<Event> tableView, Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser(); // Calling the file chooser
        // Filter only to make csv files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        // Show the file chooser dialog box
        File file = fileChooser.showSaveDialog(stage);

        // Check whether looking file is null
        if (file != null) {
            // Use file writer to write the content
            try (FileWriter writer = new FileWriter(file)) {
                // write the csv content to the file
                writeCsvContent(writer, tableView);
                System.out.println("CSV generated and saved to: " + file.getAbsolutePath());
            }
            // If file is null return the method
        }else{
            return;
        }

        // Show the report generated successfully massage
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("School Club Management System");
        alert.setHeaderText("Report Generated Successfully");
        alert.show();
    }

    // work done by- Lakshan
    // This method will write the event table content to the csv file
    private static void writeCsvContent(FileWriter writer, TableView<Event> tableView) throws IOException {
        // get the column wise details from the observable list
        ObservableList<TableColumn<Event, ?>> columns = tableView.getColumns();

        // Write headers of the table
        for (TableColumn<Event, ?> column : columns) {
            // Write each column header followed by a comma
            writer.write(column.getText() + ",");
        }
        // go to next line
        writer.write("\n");

        // Write data to csv file
        for (Event event : tableView.getItems()) {
            // Iterate through each row in the Table
            for (TableColumn<Event, ?> column : columns) {
                // Get the cell value for the current row and column
                String cellValue = column.getCellData(event).toString();
                // Write each cell value followed by a comma
                writer.write(cellValue + ",");
            }
            // go to the next line
            writer.write("\n");
        }
    }

    // work done by- Lakshan
    // This method will generate the csv file for club advisor
    private static void writeClubAdvisorCsvContent(FileWriter writer, TableView<ClubAdvisor> tableView) throws IOException {
        ObservableList<TableColumn<ClubAdvisor, ?>> columns = tableView.getColumns();

        // Write headers
        for (TableColumn<ClubAdvisor, ?> column : columns) {
            // Write each column header followed by a comma
            writer.write(column.getText() + ",");
        }
        // Move to the next line after writing all headers
        writer.write("\n");

        // Write data
        for (ClubAdvisor clubAdvisor: tableView.getItems()) {
            // Iterate through each row in the Table
            for (TableColumn<ClubAdvisor, ?> column : columns) {
                // Get the cell value for the current row and column
                String cellValue = column.getCellData(clubAdvisor).toString();
                // Write each cell value followed by a comma
                writer.write(cellValue + ",");
            }
            writer.write("\n");
        }
    }

    // work done by- Lakshan
    // This method will generate the csv file for club advisor
    public static void generateAdvisorCsv(TableView<ClubAdvisor> tableView, Stage stage) throws IOException {
        FileChooser fileChooser = new FileChooser(); // Calling the file chooser
        // Filter only to make csv files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        // Show the file chooser dialog box
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            // Use file writer to write the content
            try (FileWriter writer = new FileWriter(file)) {
                // write the csv content to the file
                writeClubAdvisorCsvContent(writer, tableView);
                System.out.println("CSV generated and saved to: " + file.getAbsolutePath());
            }
        }else{
            return;
        }

        // Show the report generated successfully massage
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("School Club Management System");
        alert.setHeaderText("Report Generated Successfully");
        alert.show();
    }


    // work done by- Arkhash
    @FXML
    void populateGenerateReportAttendanceClubName(ActionEvent event) {
        // a new array list to hold the events filtered to the respective club
        ArrayList<Event> filteredEvents = new ArrayList<>();
        // the selected club name is retrieved from the club name combo box
        String selectedClub = ReportAttendanceClubName.getSelectionModel().getSelectedItem();

        // if no club is selected, the method is returned (not executed)
        if(selectedClub == null){
            return;
        }

        // if "Please Select" is chosen, the method is returned (not executed)
        if(selectedClub.equals("Please Select")){
            return;
        }else{
            // a object of data type Event is created to iterate over the eventDetails array list
            for(Event events : Event.eventDetails){
                if(events.getClubName().equals(selectedClub)){
                    filteredEvents.add(events); // the events for the respective club is added to the areay list
                }
            }
        }
        // the event name combo box is cleared
        ReportAttendanceEventName.getItems().clear();

        // check if the option "Please select" is already available
        if(!ReportAttendanceEventName.getItems().contains("Please Select")){
            // option is added if not available
            ReportAttendanceEventName.getItems().add("Please select");
        }

        // an object event1 of data type Event is created to iterate over the filteredEvents array list
        for(Event event1 : filteredEvents){
            // the events are retrieved and added
            ReportAttendanceEventName.getItems().add(event1.getEventName());
        }
        // retrieves the current selection in the combo box
        ReportAttendanceEventName.getSelectionModel().selectFirst();
        generateReportAttendanceButton.setDisable(true);

    }

    // work done by- Arkhash
    public void populateGenerateReportClubNameComboBox(){
        // the club name combo box is cleared
        ReportAttendanceClubName.getItems().clear();

        if(!ReportAttendanceClubName.getItems().contains("Please Select")){
            // the default option of please select is added to the combo box
            ReportAttendanceClubName.getItems().add("Please select");
        }

        // the following is done for every club in the clubDetailsList
        for(Club club : clubDetailsList){
            // the club names are added to the combo box from the array list
            ReportAttendanceClubName.getItems().add(club.getClubName());
        }
        // retrieves the current selection in the combo box
        ReportAttendanceClubName.getSelectionModel().selectFirst();
        generateReportAttendanceButton.setDisable(true);
    }

    // work done by- Arkhash
    @FXML
    void populateGenerateReportAttendanceEventName(ActionEvent event) {
        int totalAttended = 0; // variable to set the total attended students
        int totalAbsent = 0; // variable to set the total absent students

        totalAbsentStudents.setText("");
        totalAttendedStudents.setText("");
        generateReportAttendanceTable.getItems().clear();
        String eventName = ReportAttendanceEventName.getValue();

        // return from the method if the event name is null
        if(eventName == null){
            return;
        }

        // if the event name is Please select, return from the method
        if(eventName.equals("Please Select")){
            return;
        }

        // for each entry in the eventDetails Array List
        for(Event eventVal : Event.eventDetails){
            // if the event name entered is equal to the name in the array list
            if(eventVal.getEventName().equals(eventName)){

                // an observable list is created with data type attendance
                ObservableList<Attendance> observableMembersList;
                // for each entry in the eventAttendance Array list
                for(Attendance attendance : eventVal.eventAttendance){
                    CheckBox checkBox = new CheckBox(); // a checkbox is defined
                    Attendance tableMembers = new Attendance(attendance.attendanceStatusProperty(),
                            attendance.getStudent(), attendance.getEvent(), checkBox);
                    // the items from the table are retrieved to the observable list
                    observableMembersList = generateReportAttendanceTable.getItems();
                    // the new members are added to the Observable list
                    observableMembersList.add(tableMembers);
                    // the values are set to the table
                    generateReportAttendanceTable.setItems(observableMembersList);

                    // if the boolean value is true
                    if(attendance.attendanceStatusProperty()){
                        totalAttended++; // value of attended students is increased by 1
                    }else{
                        totalAbsent++; // value of absent students is increased by 1
                    }
                }
            }
            // the text are set
            totalAbsentStudents.setText(totalAbsent + " students");
            totalAttendedStudents.setText(totalAttended + " students");
            // total attendance is the sum of attended and absent students
            int totalAttendance = (totalAttended  + totalAbsent);
            totalStudentCountAttendance.setText(totalAttendance+ " students");
        }

        // the button is enabled
        generateReportAttendanceButton.setDisable(false);

    }
}