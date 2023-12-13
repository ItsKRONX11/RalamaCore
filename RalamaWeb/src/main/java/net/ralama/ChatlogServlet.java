package net.ralama;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class ChatlogServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Check if the 'report' parameter is set in the URL
        String reportIdParam = request.getParameter("report");
        if (reportIdParam != null) {
            try {
                int reportId = Integer.parseInt(reportIdParam);

                // Establish a database connection (Replace with your actual database credentials)
                String dbUrl = "jdbc:mysql://localhost:3306/ralama";
                String dbUser = "root";
                String dbPass = "";
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);

                // Prepare and execute a SQL query to fetch chat messages for the given report ID
                String sql = "SELECT SENDER, MESSAGE FROM chatlogs WHERE ID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, reportId);
                ResultSet result = stmt.executeQuery();

                // Display the chat messages in a formatted way
                while (result.next()) {
                    String sender = result.getString("SENDER");
                    String message = result.getString("MESSAGE");
                    out.println("<p>" + sender + ": " + message + "</p>");
                }

                // Close the database connection
                stmt.close();
                conn.close();
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                out.println("Error: Unable to retrieve chat logs.");
            }
        } else {
            out.println("<p>No report ID provided.</p>");
        }
    }
}
