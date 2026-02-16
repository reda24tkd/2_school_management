package ensa.gi.__school_management.dao;

import ensa.gi.__school_management.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class  StudentDAO {
    private Connection conn;
    
    public StudentDAO(Connection conn) {
        this.conn = conn;
    }
    
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM students");
        
        while (rs.next()) {
            students.add(new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("age"),
                rs.getString("grade")
            ));
        }
        
        rs.close();
        stmt.close();
        return students;
    }
    
    public void addStudent(Student student) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO students (name, email, age, grade) VALUES (?, ?, ?, ?)");
        stmt.setString(1, student.getName());
        stmt.setString(2, student.getEmail());
        stmt.setInt(3, student.getAge());
        stmt.setString(4, student.getGrade());
        stmt.executeUpdate();
        stmt.close();
    }
}
