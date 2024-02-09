package kent45.digitalforensics.service;

import kent45.digitalforensics.model.Scenario;
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
     * Add a new user to the database
     * @param username username
     * @return Query Success/failure
     */
    public boolean addUser(String username){
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
     * Add a new user to the database
     * @param username username
     * @param score score
     * @return Query Success/failure
     */
    public boolean addUserAndScore(String username, int score){
        // SQL query to insert values into the users table
        var query = "INSERT INTO Users (username, score) VALUES(?, ?)";

        //Sets the parameters in the query to the username, password and doctorID
        var params = new ArrayList<>();
        params.add(username);
        params.add(score);

        // If the query has failed it'll return 0 so return false, otherwise true
        //  The query will usually of failed because there is already a username with that name
        return runUpdateQuery(query, params) != 0;
    }

    /**
     * Get scenario data
     * @param scenarioId the scenario ID
     * @return Scenario data
     */
    public Scenario getScenario(int scenarioId) {
        var query = "SELECT * FROM Scenarios WHERE scenarioId = ?";

        // Create the parameter list and add the parameters (These will replace the above (?) in execution of the query)
        var parameters = new ArrayList<>();
        parameters.add(scenarioId);

        try (ResultSet results = runSelectQuery(query, parameters)) {
            if (results != null) {
                results.next();

                return new Scenario(
                        scenarioId,
                        results.getString(2),
                        results.getString(3),
                        results.getInt(4),
                        results.getBoolean(5)
                );
            }
        } catch (SQLException e) {

        }

        return null;
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
            System.err.println(e.getMessage());
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