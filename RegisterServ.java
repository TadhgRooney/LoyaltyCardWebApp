import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServ extends HttpServlet {
    public RegisterServ() {
    	
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter(); 
        
        // Get parameters from the request
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            out.println("<h3>Passwords do not match.</h3>");
            out.println("<a href='register.html'>Go back to registration</a>");
            return;
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Initialize driver 

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/new_loyalty?serverTimezone=UTC", "root", "rootroot1");
            PreparedStatement regUser = connection.prepareStatement("INSERT INTO users (username, password, loyalty_points) VALUES (?, ?, ?)");
            
            regUser.setString(1, username);
            regUser.setString(2, password);
            regUser.setInt(3, 100);
            
            int rowsUpdated = regUser.executeUpdate();
            regUser.close();
            
            if (rowsUpdated > 0) {
                response.sendRedirect("loginPage.html"); 
            } else {
                // if there are no rows updated
                out.println("<h3>User wasn't added to table</h3>");
            }
            
          //If there is a SQL problem
        } catch (SQLException e) {
            e.printStackTrace();  
            out.println("<h3>There was a problem with SQL.</h3>");
    
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        	
    }
}




