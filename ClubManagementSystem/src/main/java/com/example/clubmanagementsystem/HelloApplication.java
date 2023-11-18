package com.example.clubmanagementsystem;


import ClubManager.Attendance;
import ClubManager.Club;
import ClubManager.Event;
import DataBaseManager.ClubAdvisorDataBaseManager;
import SystemUsers.ClubAdvisor;

import SystemUsers.Student;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;

public class HelloApplication extends Application {
    public static Connection connection;

    public static Statement statement;
    @Override
    public void start(Stage stage) throws IOException {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/clubmanagementsystem/Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 763,502);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
        }catch (Exception E){
            E.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        String URL = "jdbc:mysql://localhost:3306/ClubManagementSystsem";
        String user = "root";
        String password = "root";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, user, password);
            statement = connection.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        Student stud1 = new Student("arkh", "arkhash0309", "Arkhash",
                "Saravanakumar","0779073140", 5748,
                8, 'M');
        Student.studentDetailArray.add(stud1);
//        System.out.println(Student.studentDetailArray.get(0).getFirstName());
        CheckBox checkBox = new CheckBox();
        Attendance attendance = new Attendance(false, checkBox);
        Attendance.atdTracker.add(attendance);

        CheckBox checkBox2 = new CheckBox();
        Attendance attendance2 = new Attendance(false, checkBox2);
        Attendance.atdTracker.add(attendance2);

        CheckBox checkBox3 = new CheckBox();
        Attendance attendance3 = new Attendance(false, checkBox3);
        Attendance.atdTracker.add(attendance3);

        ClubAdvisorDataBaseManager clubAdvisorDataBaseManager = new ClubAdvisorDataBaseManager();
        clubAdvisorDataBaseManager.populateClubAdvisorArray("Lakshan200");
//        System.out.println(ClubAdvisor.clubAdvisorDetailsList.get(0).getClubAdvisorId());
        clubAdvisorDataBaseManager.populateStudentDetailArray();
//        System.out.println(Student.studentDetailArray.get(0).getFirstName());
//        clubAdvisorDataBaseManager.populateClubDetailArray();
//        System.out.println(Club.clubDetailsList.get(0).getClubName());

        clubAdvisorDataBaseManager.populateEventsDetailArray();
//        System.out.println(Event.eventDetails.get(0).getEventName());

        Club club1 = new Club(0001, "Rotract", "Done with the work", "lkt.img");
        Club.clubDetailsList.add(club1);

        Club club2 = new Club(0002, "IEEE", "Done with the work", "lkt.img");
        Club.clubDetailsList.add(club2);

        launch();
        connection.close();

    }

}