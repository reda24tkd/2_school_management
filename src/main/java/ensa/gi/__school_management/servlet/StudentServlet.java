package ensa.gi.__school_management.servlet;

import ensa.gi.__school_management.dao.StudentDAO;
import ensa.gi.__school_management.model.Student;
import ensa.gi.__school_management.util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

public class StudentServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DatabaseConnection.getConnection(getServletContext());
            studentDAO = new StudentDAO(conn);
        } catch (Exception e) {
            throw new ServletException("Database connection failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        String action = req.getParameter("action");
        String id = req.getParameter("id");
        
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        try {
            if ("delete".equals(action) && id != null) {
                studentDAO.deleteStudent(Integer.parseInt(id));
                resp.sendRedirect("students");
                return;
            }
            
            List<Student> students = studentDAO.getAllStudents();
            
            out.println("<html><head><link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<div class='container'><h1>Student List</h1>");
            out.println("<table><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Age</th><th>Grade</th><th>Action</th></tr></thead><tbody>");
            
            for (Student student : students) {
                out.println("<tr>");
                out.println("<td>" + student.getId() + "</td>");
                out.println("<td>" + student.getName() + "</td>");
                out.println("<td>" + student.getEmail() + "</td>");
                out.println("<td>" + student.getAge() + "</td>");
                out.println("<td>" + student.getGrade() + "</td>");
                out.println("<td><a href='students?action=delete&id=" + student.getId() + "' onclick='return confirm(\"Delete this student?\")'>Delete</a></td>");
                out.println("</tr>");
            }
            
            out.println("</tbody></table>");
            out.println("<a href='students/create.html' class='btn-link'>+ Add New Student</a>");
            out.println("</div></body></html>");
            
        } catch (Exception e) {
            out.println("<h1>Error: " + e.getMessage() + "</h1>");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        int age = Integer.parseInt(req.getParameter("age"));
        String grade = req.getParameter("grade");
        
        try {
            Student student = new Student(0, name, email, age, grade);
            studentDAO.addStudent(student);
            resp.sendRedirect("students");
        } catch (Exception e) {
            resp.getWriter().println("<h1>Error: " + e.getMessage() + "</h1>");
        }
    }

    @Override
    public void destroy() {
        DatabaseConnection.closeConnection();
    }

}
