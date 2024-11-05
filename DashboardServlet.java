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
import javax.servlet.http.HttpSession;


public class DashboardServlet extends HttpServlet {

   
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null; 
        response.setContentType("text/html"); 
        PrintWriter out = response.getWriter(); 

        // Get username from login
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // Get the action(either spend or add)
        String action = request.getParameter("action");

        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/new_loyalty?serverTimezone=UTC", "root", "rootroot1");

            // Check if the action is to add points
            if ("addPoints".equals(action)) {
                // Add 25 points to the user's account
                updateUserPoints(username, 25, connection);
                // update the session
                int newPointsTotal = getUserPoints(username, connection);
                session.setAttribute("loyaltyPoints", newPointsTotal);
                out.println("<h3>25 points added!</h3>");
               

            //if the action is spend points
            } else if ("spendPoints".equals(action)) {
                
                String giftCardValue = request.getParameter("gift_card");
                // Get the points needed for gift card
                int pointsRequired = getPointsRequiredForGiftCard(giftCardValue);


                // Retrieve the user's current points
                int currentPoints = getUserPoints(username, connection);
                
                if (currentPoints >= pointsRequired) {
                   
                    updateUserPoints(username, -pointsRequired, connection);
                    // Update session with the new points total
                    session.setAttribute("loyaltyPoints", currentPoints - pointsRequired);
                    out.println("<h3>Gift card redeemed successfully!</h3>");
                   
                } else {
                    out.println("<h3>Not enough points to spend.</h3>"); 
                }
            } else {
            	out.println("<h3>Select add points or spend points.</h3>");

            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<h3>There was a problem with SQL.</h3>");
            
            
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


     
    }

    // Updates the user's loyalty points in the database
    private void updateUserPoints(String username, int points, Connection conn) throws SQLException {
        String query = "UPDATE users SET loyalty_points = loyalty_points + ? WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, points); 
            pstmt.setString(2, username); 
            pstmt.executeUpdate();
        }
    }

    // Retrieves the current loyalty points of the user from the database
    private int getUserPoints(String username, Connection conn) throws SQLException {
        String query = "SELECT loyalty_points FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username); 
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("loyalty_points"); 
                }
            }
        }
        return 0;
    }

   
    private int getPointsRequiredForGiftCard(String giftCardValue) {
        switch (giftCardValue) {
            case "10":
                return 50; // €10 gift card requires 50 points
            case "15":
                return 75; // €15 gift card requires 75 points
            case "20":
                return 100; // €20 gift card requires 100 points
            default:
                return -1; // Invalid gift card selection
        }
    }
}




       
       

