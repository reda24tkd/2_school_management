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

        HttpSession session = req.getSession(true);
        String welcomeMessage = showSessionMsg(session);

        String action = req.getParameter("action");
        String id = req.getParameter("id");
        
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        try {
            if ("delete".equals(action) && id != null) {
                int studentId = Integer.parseInt(id);
                studentDAO.deleteStudent(studentId);
                removeStudentFromSession(session, studentId);
                resp.sendRedirect("students");
                return;
            }
            
            if ("edit".equals(action) && id != null) {
                Student student = studentDAO.getStudentById(Integer.parseInt(id));
                if (student != null) {
                    showEditForm(out, student);
                    return;
                }
            }
            
            List<Student> students = studentDAO.getAllStudents();
            syncSessionWithDB(session, students);
            
            out.println("<html><head><link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<div class='container'>");
            out.println("<p style='text-align:center; color:#4CAF50; font-weight:bold;'>" + welcomeMessage + "</p>");
            out.println("<h1>Student List</h1>");
            out.println("<table><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Age</th><th>Grade</th><th>Action</th></tr></thead><tbody>");
            
            for (Student student : students) {
                out.println("<tr>");
                out.println("<td>" + student.getId() + "</td>");
                out.println("<td>" + student.getName() + "</td>");
                out.println("<td>" + student.getEmail() + "</td>");
                out.println("<td>" + student.getAge() + "</td>");
                out.println("<td>" + student.getGrade() + "</td>");
                out.println("<td><a href='students?action=edit&id=" + student.getId() + "'>Edit</a> | <a href='students?action=delete&id=" + student.getId() + "' onclick='return confirm(\"Delete this student?\")'>Delete</a></td>");
                out.println("</tr>");
            }
            
            out.println("</tbody></table>");
            out.println("<a href='students/create.html' class='btn-link'>+ Add New Student</a>");
            
            out.println("<div style='margin-top:30px; padding:15px; background:#f9f9f9; border-radius:5px;'>");
            out.println("<h3>Session Info</h3>");
            out.println("<p><strong>Session ID:</strong> " + session.getId() + "</p>");
            out.println("<p><strong>Visit Count:</strong> " + session.getAttribute("visitCount") + "</p>");
            // The SESSIONS
//            @SuppressWarnings("unchecked")
            List<Student> sessionStudents = (List<Student>) session.getAttribute("allStudents");
            if (sessionStudents != null && !sessionStudents.isEmpty()) {
                out.println("<p><strong>Students in Session:</strong> " + sessionStudents.size() + "</p>");
                out.println("<ul>");
                for (Student s : sessionStudents) {
                    out.println("<li>" + s.getName() + " (" + s.getEmail() + ")</li>");
                }
                out.println("</ul>");
            } else {
                out.println("<p><strong>Students in Session:</strong> None</p>");
            }
            out.println("</div>");
            
            out.println("</div></body></html>");
            
        } catch (Exception e) {
            out.println("<h1>Error: " + e.getMessage() + "</h1>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        String action = req.getParameter("action");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        int age = Integer.parseInt(req.getParameter("age"));
        String grade = req.getParameter("grade");
        
        try {
            if ("update".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Student student = new Student(id, name, email, age, grade);
                studentDAO.updateStudent(student);
            } else {
                Student student = new Student(0, name, email, age, grade);
                studentDAO.addStudent(student);
                
                HttpSession session = req.getSession();
                addStudentToSession(session, student);
                logSessionInfo(session);
            }
            
            resp.sendRedirect("students");
        } catch (Exception e) {
            resp.getWriter().println("<h1>Error: " + e.getMessage() + "</h1>");
        }
    }

    private void syncSessionWithDB(HttpSession session, List<Student> students) {
        session.setAttribute("allStudents", students);
    }
    
    private void addStudentToSession(HttpSession session, Student student) {
//        @SuppressWarnings("unchecked")
        List<Student> sessionStudents = (List<Student>) session.getAttribute("allStudents");
        if (sessionStudents != null) {
            sessionStudents.add(student);
        }
    }
    
    private void removeStudentFromSession(HttpSession session, int studentId) {
//        @SuppressWarnings("unchecked")
        List<Student> sessionStudents = (List<Student>) session.getAttribute("allStudents");
        if (sessionStudents != null) {
            sessionStudents.removeIf(s -> s.getId() == studentId);
        }
    }

    private static String showSessionMsg(HttpSession session) {

        String welcomeMessage = "";
        Integer visitCount = (Integer) session.getAttribute("visitCount");

        if (visitCount == null) {
            visitCount = 1;
        } else {
            visitCount++;
        }
        session.setAttribute("visitCount", visitCount);

        if (visitCount == 1) {
            welcomeMessage = "Welcome! This is your first visit.";
        } else {
            welcomeMessage = "Welcome back! You've visited this page " + visitCount + " times.";
        }
        return welcomeMessage;
    }

    private void logSessionInfo(HttpSession session) {
        log("========== SESSION INFO ==========");
        log("Session ID: " + session.getId());
        log("Creation Time: " + new java.util.Date(session.getCreationTime()));
        log("Last Accessed: " + new java.util.Date(session.getLastAccessedTime()));
        log("Max Inactive Interval: " + session.getMaxInactiveInterval() + " seconds");
        
        @SuppressWarnings("unchecked")
        List<Student> allStudents = (List<Student>) session.getAttribute("allStudents");
        if (allStudents != null) {
            log("Total Students in Session: " + allStudents.size());
            for (Student s : allStudents) {
                log("  - " + s.getName() + " (" + s.getEmail() + ")");
            }
        }
        
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount != null) {
            log("Visit Count: " + visitCount);
        }
        log("==================================");
    }
    
    private void showEditForm(PrintWriter out, Student student) {
        out.println("<html><head><link rel='stylesheet' href='css/style.css'></head><body>");
        out.println("<div class='container'>");
        out.println("<h1>Edit Student</h1>");
        out.println("<form action='students' method='post'>");
        out.println("<input type='hidden' name='action' value='update'>");
        out.println("<input type='hidden' name='id' value='" + student.getId() + "'>");
        
        out.println("<div class='form-row'>");
        out.println("<div class='form-group'>");
        out.println("<label for='name'>Name:</label>");
        out.println("<input type='text' id='name' name='name' value='" + student.getName() + "' required>");
        out.println("</div>");
        out.println("<div class='form-group'>");
        out.println("<label for='email'>Email:</label>");
        out.println("<input type='email' id='email' name='email' value='" + student.getEmail() + "' required>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div class='form-row'>");
        out.println("<div class='form-group'>");
        out.println("<label for='age'>Age:</label>");
        out.println("<input type='number' id='age' name='age' value='" + student.getAge() + "' min='15' max='100' required>");
        out.println("</div>");
        out.println("<div class='form-group'>");
        out.println("<label for='grade'>Grade:</label>");
        out.println("<select id='grade' name='grade' required>");
        out.println("<option value='A' " + ("A".equals(student.getGrade()) ? "selected" : "") + ">A</option>");
        out.println("<option value='B' " + ("B".equals(student.getGrade()) ? "selected" : "") + ">B</option>");
        out.println("<option value='C' " + ("C".equals(student.getGrade()) ? "selected" : "") + ">C</option>");
        out.println("<option value='D' " + ("D".equals(student.getGrade()) ? "selected" : "") + ">D</option>");
        out.println("</select>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<button type='submit'>Update Student</button>");
        out.println("</form>");
        out.println("<a href='students' class='btn-link'>← Back to List</a>");
        out.println("</div></body></html>");
    }

    @Override
    public void destroy() {
        DatabaseConnection.closeConnection();
    }

}
