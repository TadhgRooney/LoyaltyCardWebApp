
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {
    
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        
        Connection connection = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Get the username and password from the login page
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
          
            Class.forName("com.mysql.cj.jdbc.Driver"); // Initialize driver

           
            connection = DriverManager.getConnection( "jdbc:mysql://localhost:3306/new_loyalty?serverTimezone=UTC","root","rootroot1");

            // check if user exists in db
            PreparedStatement logUser = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            logUser.setString(1, username);
            logUser.setString(2, password);
            
            ResultSet rs = logUser.executeQuery();

            if (rs.next()) {
                // if user exists send them to points 
            	response.sendRedirect("home.html"); 
            } else {
                // Else let them try again
                out.println("<html><body>");
                out.println("<h3>Invalid username or password.</h3>");
                out.println("<a href='login.html'>Try again</a>");
                out.println("</body></html>");
            }

            rs.close();
            logUser.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<h3>There was a problem with SQL.</h3>");
            
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } 
            }
        
    }

