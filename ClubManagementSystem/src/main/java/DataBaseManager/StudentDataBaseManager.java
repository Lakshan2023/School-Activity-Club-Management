package DataBaseManager;

import ClubManager.Club;
import ClubManager.Event;
import SystemUsers.ClubAdvisor;
import SystemUsers.Student;
import com.example.clubmanagementsystem.HelloApplication;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
// work done by- Lakshan
// This class handles loading data to Object Arraylists that are created in different Classes
public class StudentDataBaseManager {
    private int StudentAdmissionNum; // To identify the admission number of the login student
    private static String StudentUserName; // To identify the username of the login student

    // Constructor that calls all the methods that load to Object arraylists
    public StudentDataBaseManager(String StudentUserName) throws SQLException {
        this.setStudentUserName(StudentUserName);
        this.StudentAdmissionNum = getStudentAdmissionNum(this.getStudentUserName());
        System.out.println(this.StudentAdmissionNum);
        populateStudentDetails(); // Populate student details arraylist
        populateAllClubDetails(); // Populate club details arraylist
        populateAllClubAdvisorDetails(); // Populate club advisor details array list
        populateClubAdvisorCreatedClubs(); // Populate club advisor created clubs arraylist
        populateStudentJoinedClubs(); // populate student joined clubs arraylist
        populateEventDetails(); // Populate event details array list
        populateAllEvents(); // Populate all events related to the student
    }

    // This method gets the student admission number
    public int getStudentAdmissionNum(String userName){
        // Initialize student admission number as 0
        int studentAdmissionNum = 0;

        // Select the student admission number for the given username
        String selectStudentQuery = "SELECT studentAdmissionNum FROM studentCredentials WHERE studentUserName = ?";
        try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(selectStudentQuery)) {
            preparedStatement.setString(1, userName);

            // execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // give the student admission number
            if (resultSet.next()) {
                studentAdmissionNum = resultSet.getInt("studentAdmissionNum");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // return the student admission number
        return studentAdmissionNum;
    }

    // This method populates the student details array list
    public void populateStudentDetails(){
        // Clear the student details array
        Student.studentDetailArray.clear();

        // Select all the student related details by considering the admission number
        String query = "SELECT STC.studentUserName, STC.studentPassword, STD.studentAdmissionNum, STD.studentFName, " +
                "STD.studentLName, STD.studentGrade, STD.studentContactNum, STD.Gender " +
                "FROM Student STD " +
                "JOIN studentCredentials STC ON STD.studentAdmissionNum = STC.studentAdmissionNum  " +
                "WHERE STD.studentAdmissionNum = ?";

        // Prepare the statement and execute
        try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, this.StudentAdmissionNum);
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    // Create a student objet by populating the retrieved student data
                    Student student  = new Student(
                            result.getString("studentUserName"),
                            result.getString("studentPassword"),
                            result.getString("studentFName"),
                            result.getString("studentLName"),
                            result.getString("studentContactNum"),
                            result.getInt("studentAdmissionNum"),
                            result.getInt("studentGrade"),
                            result.getString("Gender").charAt(0)
                    );

                    // Populate student detail array list
                    Student.studentDetailArray.add(student);
                }
            }
            // Handle the exception appropriately
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println(Student.studentDetailArray.size());
    }

    // This method populate club details array list
    public void populateAllClubDetails(){
        // Clear the clubDetailsList array list
        Club.clubDetailsList.clear();

        // Select the club details relevant to the logged in student
        String query = "SELECT C.clubId, C.clubName, C.clubDescription, C.clubLogo " +
                "FROM Club C JOIN TeacherInCharge TIC ON C.teacherInChargeId = TIC.teacherInChargeId ";

        // Execute the statement
        try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(query)) {
            // Get the result of the prepared statement
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    // Create a club object by populating the retrieved club details
                    Club club  = new Club(
                            result.getInt("clubId"),
                            result.getString("clubName"),
                            result.getString("clubDescription"),
                            result.getString("clubLogo")
                    );

                    System.out.println(result.getString("clubLogo"));

                    // Add club details to the clubDetailsList
                    Club.clubDetailsList.add(club);
                    System.out.println("Elama Elama");
                }
            }
            // Handle the exception appropriately
        } catch (SQLException e) {
            // Handle the exception appropriately
            throw new RuntimeException(e);
        }
    }

    // This method populate the club advisor details array list
    public void populateAllClubAdvisorDetails() throws SQLException {
        // Clear the club advisor details list
        ClubAdvisor.clubAdvisorDetailsList.clear();
        // Select the club advisor related details for the student using club advisor id
        String query = "SELECT TIC.teacherInChargeId, TC.teacherUserName, TC.teacherPassword, TIC.TICFName, TIC.TICLName, TIC.teacherContactNum " +
                "FROM TeacherCredentials TC " +
                "JOIN TeacherInCharge TIC ON TC.teacherInChargeId = TIC.teacherInChargeId";

        // This statement execute the selected club advisor statements
        try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(query)) {
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    // Creates an object of the club advisor
                    ClubAdvisor clubAdvisor = new ClubAdvisor(
                            result.getString("teacherUserName"),
                            result.getString("teacherPassword"),
                            result.getString("TICFName"),
                            result.getString("TICLName"),
                            result.getString("teacherContactNum"),
                            result.getInt("teacherInChargeId")
                    );

                    // Add club advisor details to clubAdvisor details list
                    ClubAdvisor.clubAdvisorDetailsList.add(clubAdvisor);

                }
            }
        }

    }

    // This method populate all the details of the created clubs in the system
    public void populateClubAdvisorCreatedClubs(){
        // Get the count to identify the index of the club advisor list
        int count = 0;
        for(ClubAdvisor clubAdvisor : ClubAdvisor.clubAdvisorDetailsList){
            // Clear the created club details list
            clubAdvisor.createdClubDetailsList.clear();

            // This query selects all details of the club advisors created clubs in the system
            String query = "SELECT C.clubId, C.clubName, C.clubDescription, C.clubLogo " +
                    "FROM Club C " +
                    "JOIN TeacherCredentials TC ON C.teacherInChargeId = TC.teacherInChargeId " +
                    "WHERE TC.teacherUserName = ?";

            // Execute the select statements for clubs
            try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(query)) {
                // Execute the sql statement
                preparedStatement.setString(1, clubAdvisor.getUserName());

                // Get the result of the prepared statement
                try (ResultSet result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                        int clubId = result.getInt("clubId");
                        String clubName = result.getString("clubName");
                        String clubDescription = result.getString("clubDescription");
                        String clubLogo = result.getString("clubLogo");

                        // Parse the values using a club object
                        Club club = new Club(clubId, clubName, clubDescription, clubLogo);

                        // Add the club details to createdClubDetaild Array List
                        clubAdvisor.createdClubDetailsList.add(club);
                    }

                    // Update the data in the required index
                    ClubAdvisor.clubAdvisorDetailsList.set(count, clubAdvisor);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            count++;
        }
    }

    // This method populates the details of the student joined clubs
    public void populateStudentJoinedClubs(){
        // clear the studentJoinedClubs
        Student.studentJoinedClubs.clear();

        // Select the club details using club related details
        String query = "SELECT C.clubId, C.clubName, C.clubDescription, C.clubLogo " +
                "FROM Club C " +
                "JOIN StudentClub SC ON C.clubId = SC.clubId " +
                "WHERE SC.studentAdmissionNum = ?";

        // execute the statement to populate the student joined
        try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, this.StudentAdmissionNum);

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    int clubId = result.getInt("clubId");
                    String clubName = result.getString("clubName");
                    String clubDescription = result.getString("clubDescription");
                    String clubLogo = result.getString("clubLogo");

                    // Create the club Object by including  clubId, clubName, clubDescription, clubLogo
                    Club clubDetail = new Club(clubId, clubName, clubDescription, clubLogo);

                    Student.studentJoinedClubs.add(clubDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    // This method populate the event details array list
    public void populateEventDetails(){
        // Clear the studentEvent detail
        Student.studentEvent.clear();

        // Iterate through the Club arraylist to select the event related details
        for(Club club : Student.studentJoinedClubs){
            String query = "SELECT EventId, eventName, eventDate, eventTime, eventLocation, eventType, eventDeliveryType, eventDescription " +
                    "FROM EventDetails WHERE clubId = ?";

            // Execute the prepared statement
            try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(query)) {
                preparedStatement.setInt(1, club.getClubId());

                try (ResultSet result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                        Date eventDate = result.getDate("eventDate");
                        Time eventTime = result.getTime("eventTime");
                        LocalDate localDate = eventDate.toLocalDate();
                        LocalTime localTime = eventTime.toLocalTime();

                        /*Create an object of the event by including event name, location, type,
                         deliveryType, description */
                        Event event = new Event(
                                result.getString("eventName"),
                                result.getString("eventLocation"),
                                result.getString("eventType"),
                                result.getString("eventDeliveryType"),
                                localDate,
                                localTime,
                                club,
                                result.getString("eventDescription"),
                                result.getInt("EventId")
                        );

                        // populate details related to each student
                        Student.studentEvent.add(event);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    // Populate all event related details to event details list
    public void populateAllEvents(){
        // Clear the event details list
        Event.eventDetails.clear();
        // Iterate through the Club details list to populate all events scheduled by club advisors
        for(Club club : Club.clubDetailsList){
            // query that gives event details related to each club
            String query = "SELECT EventId, eventName, eventDate, eventTime, eventLocation, eventType, eventDeliveryType, eventDescription " +
                    "FROM EventDetails WHERE clubId = ?";

            // Execute the prepared statement
            try (PreparedStatement preparedStatement = HelloApplication.connection.prepareStatement(query)) {
                preparedStatement.setInt(1, club.getClubId());

                try (ResultSet result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                        Date eventDate = result.getDate("eventDate");
                        Time eventTime = result.getTime("eventTime");
                        LocalDate localDate = eventDate.toLocalDate();
                        LocalTime localTime = eventTime.toLocalTime();

                        /*Create an event object by including event name, location, type,
                        delivery type, description, event Id */
                        Event event = new Event(
                                result.getString("eventName"),
                                result.getString("eventLocation"),
                                result.getString("eventType"),
                                result.getString("eventDeliveryType"),
                                localDate,
                                localTime,
                                club,
                                result.getString("eventDescription"),
                                result.getInt("EventId")
                        );

                        // Add the event object to the event details list
                        Event.eventDetails.add(event);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

    }

    // Method to get the login students username
    public static String getStudentUserName() {
        return StudentUserName;
    }

    // method to set the login students username
    public static void setStudentUserName(String studentUserName) {
        StudentUserName = studentUserName;
    }
}
