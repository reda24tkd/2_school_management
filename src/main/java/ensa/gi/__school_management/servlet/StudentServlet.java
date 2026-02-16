package ensa.gi.__school_management.servlet;

import ensa.gi.__school_management.dao.StudentDAO;
import ensa.gi.__school_management.model.Student;
import ensa.gi.__school_management.util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
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
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("GET".equals(method)) {
            doGet(req, resp);
        } else if ("POST".equals(method)) {
            doPost(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String action = req.getParameter("action");
        String id = req.getParameter("id");
        
        try {
            if ("delete".equals(action) && id != null) {
                studentDAO.deleteStudent(Integer.parseInt(id));
                resp.sendRedirect("students");
                return;
            }
            
            List<Student> students = studentDAO.getAllStudents();
            req.setAttribute("students", students);
            req.getRequestDispatcher("index.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.getWriter().println("<h1>Error: " + e.getMessage() + "</h1>");
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
