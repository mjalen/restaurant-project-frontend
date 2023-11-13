import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



@WebServlet("/CustomerCalendar")
public class Cust_Calendar_Creation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public Cust_Calendar_Creation() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		Connection connection = null;
		String grabReservations = "Select * FROM Reservations"; //TODO-select by username
		
		try {
			DBConnectionDelta.getDBConnection();
			connection = DBConnectionDelta.connection;
			
			PreparedStatement preparedStatement = connection.prepareStatement(grabReservations);
			//preparedStatement.setString(1, "username"); //TODO- get username as variable from JS
			ResultSet resultSet = preparedStatement.executeQuery();
			
			JSONArray reservationsArray = new JSONArray();
			
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String first_name = resultSet.getString("first_name");
				String last_name = resultSet.getString("last_name");
				String date = resultSet.getString("date");
				String time = resultSet.getString("time");   //TODO
				JSONObject reservation = new JSONObject();
				reservation.put("id", id);
				reservation.put("first_name", first_name);
				reservation.put("last_name", last_name);
				reservation.put("date", date);
				reservation.put("time", time);
				
				reservationsArray.put(reservation);
			}
			JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("reservation", reservationsArray);

            out.print(jsonResponse.toString());
            out.flush();
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.err.println("Database error: " + e.getMessage());
        } catch (JSONException e) { 
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.err.println("JSON error: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json;charset=UTF-8");
	    PrintWriter out = response.getWriter();

	    try {
	        JSONObject requestData = new JSONObject(request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual));
	        String first_name = requestData.getString("first_name");
	        String last_name = requestData.getString("last_name");
	        String phone = requestData.getString("phone");
	        String email = requestData.getString("email");
	        String date = requestData.getString("date");
	        String time = requestData.getString("time");

	        Connection connection = null;
	        String addReservation = "INSERT INTO Reservations (first_name, last_name, phone, email, date, time) VALUES (?, ?, ?, ?, ?, ?)";

	        try {
	            DBConnectionDelta.getDBConnection();
	            connection = DBConnectionDelta.connection;

	            PreparedStatement preparedStatement = connection.prepareStatement(addReservation, Statement.RETURN_GENERATED_KEYS);
	            preparedStatement.setString(1, first_name);
	            preparedStatement.setString(2, last_name);
	            preparedStatement.setString(3, phone);
	            preparedStatement.setString(4, email);
	            preparedStatement.setString(5, date);
	            preparedStatement.setString(6, time);

	            int affectedRows = preparedStatement.executeUpdate();
	            if (affectedRows == 0) {
	                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	                System.err.println("Failed to insert reservation.");
	                return;
	            }

	            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                int id = generatedKeys.getInt(1);
	                JSONObject jsonResponse = new JSONObject();
	                jsonResponse.put("success", true);
	                jsonResponse.put("id", id);
	                out.println(jsonResponse.toString());
	            } else {
	                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	                System.err.println("Failed to get id.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            System.err.println("Database error: " + e.getMessage());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        System.err.println("Error parsing JSON data: " + e.getMessage());
	    }
	}
}
