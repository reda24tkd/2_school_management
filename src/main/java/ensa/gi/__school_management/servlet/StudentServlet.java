package ensa.gi.__school_management.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


/**
 * V1 - Creates new connection per request, uses PrintWriter for HTML output
 */
public class StudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        String action = req.getParameter("action");
        String id = req.getParameter("id");
        
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/school_management", "root", "");
            
            if ("delete".equals(action) && id != null) {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM students WHERE id = ?");
                stmt.setInt(1, Integer.parseInt(id));
                stmt.executeUpdate();
                stmt.close();
                conn.close();
                resp.sendRedirect("students");
                return;
            }
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            
            out.println("<html><head><link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<div class='container'><h1>Student List</h1>");
            out.println("<table><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Age</th><th>Grade</th><th>Action</th></tr></thead><tbody>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("<td>" + rs.getInt("age") + "</td>");
                out.println("<td>" + rs.getString("grade") + "</td>");
                out.println("<td><a href='students?action=delete&id=" + rs.getInt("id") + "' onclick='return confirm(\"Delete this student?\")'>Delete</a></td>");
                out.println("</tr>");
            }
            
            out.println("</tbody></table>");
            out.println("<a href='students/create.html' class='btn-link'>+ Add New Student</a>");
            out.println("</div></body></html>");
            
            rs.close();
            stmt.close();
            conn.close();
            
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
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/school_management", "root", "");
            
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO students (name, email, age, grade) VALUES (?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, age);
            stmt.setString(4, grade);
            stmt.executeUpdate();
            
            stmt.close();
            conn.close();
            
            resp.sendRedirect("students");
            
        } catch (Exception e) {
            resp.getWriter().println("<h1>Error: " + e.getMessage() + "</h1>");
        }
    }

}
