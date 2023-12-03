package kent45.digitalforensics.service;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;

@Service
public class DatabaseService {
    private Connection connection;

    // When constructed creates the connection
    public DatabaseService(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://dragon.kent.ac.uk:3306/comp6000_46", "comp6000_46", "lasell6");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Determines if the provided username and password are found in the database
     * @param username username
     * @return Query Success/failure
     */
    public boolean checkLogin(String username) {
        // Set up the query
        var query = "SELECT * FROM Users WHERE Username = ?";

        // Create the parameter list and add the parameters (These will replace the above (?) in execution of the query)
        var parameters = new ArrayList<>();
        parameters.add(username);

        var results = runSelectQuery(query, parameters);
        try {
            // If the results are empty then the username and password are invalid, as it is empty then results.next() will return false
            // Else, the username and password and valid, as the results are not empty results.next() will return true
            return results.next();
        } catch (Exception e) {
            // If for whatever reason the above return fails we have a fallback
            return false;
        }
    }

    /**
     * Add a new user to the database
     * @param username username
     * @return Query Success/failure
     */
    public boolean createUser(String username){
        // SQL query to insert values into the users table
        var query = "INSERT INTO Users (username) VALUES(?)";

        //Sets the parameters in the query to the username, password and doctorID
        var params = new ArrayList<>();
        params.add(username);

        // If the query has failed it'll return 0 so return false, otherwise true
        //  The query will usually of failed because there is already a username with that name
        return runUpdateQuery(query, params) != 0;
    }



    /**
     *  A general select query method with parameters of String or int
     * @param query SQL select query
     * @param parameters Arraylist of String or Int Parameters
     * @return Result of the select
     */
    private ResultSet runSelectQuery(String query, ArrayList<Object> parameters) {
        // Attempts to connect and execute the given query
        try {
            var preparedStatement = createPreparedStatement(query, parameters);

            return preparedStatement.executeQuery(); // Returns the result of the query
        } catch (SQLException e) {
            return null; // If the connection or query fails return null
        }
    }

    /**
     *  A general insert/update/delete query method with parameters of String or int
     * @param query SQL insert/update/delete Query
     * @param parameters Arraylist of String or Int Parameters
     * @return Returns the number of rows affected
     */
    private int runUpdateQuery(String query, ArrayList<Object> parameters) {
        // Attempts to connect and execute the given query
        try {
            var preparedStatement = createPreparedStatement(query, parameters);

            // Use the execute update method which returns the number of rows affected
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return 0; // If the connection or query fails return 0
        }
    }

    /**
     * A general method to create a prepared statement from a query and parameters
     * @param query SQL Query
     * @param parameters Arraylist of String or Int Parameters
     * @return preparedStatement
     * @throws SQLException If connection or query fails
     */
    private PreparedStatement createPreparedStatement(String query, ArrayList<Object> parameters) throws SQLException {
        // Setups the connection with the DB
        // Creating the prepared statement with the query, these allow for parameters to be inputted into the query String
        var preparedStatement = connection.prepareStatement(query);

        // Setting the parameters in the prepared statement from the given parameter list
        int index = 1; // The prepared statement parameter inputs starts at index 1
        // Loops through the parameters
        for (Object parameter : parameters) {
            // Each type of parameter requires a different .set
            if (parameter instanceof String) {
                preparedStatement.setString(index, (String) parameter); // Sets the parameters (?) in the query / prepared statement
                index++;
            }
            else if (parameter instanceof Integer) {
                preparedStatement.setInt(index, (Integer) parameter); // Sets the parameters (?) in the query / prepared statement
                index++;
            }
        }

        return preparedStatement;
    }
}