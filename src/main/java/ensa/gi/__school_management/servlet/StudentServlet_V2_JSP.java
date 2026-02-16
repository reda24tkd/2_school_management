package ensa.gi.__school_management.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

/**
 * V2 - Reuses single connection, uses JSP for view, context params for config
 */
public class StudentServlet_V2_JSP extends HttpServlet {

    private Connection conn;

    @Override
    public void init() throws ServletException {
        try {
            String dbUrl = getServletContext().getInitParameter("db-url");
            String dbUser = getServletContext().getInitParameter("db-user");
            String dbPassword = getServletContext().getInitParameter("db-password");
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (Exception e) {
            throw new ServletException("Database connection failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            
            req.setAttribute("students", rs);
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
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO students (name, email, age, grade) VALUES (?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, age);
            stmt.setString(4, grade);
            stmt.executeUpdate();
            
            stmt.close();
            
            resp.sendRedirect("students");
            
        } catch (Exception e) {
            resp.getWriter().println("<h1>Error: " + e.getMessage() + "</h1>");
        }
    }

    @Override
    public void destroy() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
