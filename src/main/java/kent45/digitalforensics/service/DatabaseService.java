package kent45.digitalforensics.service;

import kent45.digitalforensics.model.*;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

        //Sets the parameters in the query to the username
        var params = new ArrayList<>();
        params.add(username);

        // If the query has failed it'll return 0 so return false, otherwise true
        //  The query will usually of failed because there is already a username with that name
        return runUpdateQuery(query, params) != 0;
    }

    /**
     * Puts the given username into the LoggedInUser table
     * @param username username
     * @return Query Success/failure
     */
    public boolean logInUser(String username) {
        // SQL queries to delete users in table and insert new user into the users table
        var query = "TRUNCATE TABLE LoggedInUser";
        runUpdateQuery(query, new ArrayList<>());
        query = "INSERT INTO LoggedInUser (user) VALUES(?)";

        //Sets the parameters in the query to the username
        var params = new ArrayList<>();
        params.add(username);

        // If the query has failed it'll return 0 so return false, otherwise true
        return runUpdateQuery(query, params) != 0;
    }

    /**
     * Returns the currently logged-in user
     * @return logged in user
     */
    public String getLoggedInUser() {
        var query = "SELECT user FROM LoggedInUser";

        return getStringFromQuery(query, new ArrayList<>());
    }

    /**
     * Returns the game play stats
     * @return the game play stats json
     */
    public GamePlayStatsJson getGamePlayStats() {
        var query = "SELECT * FROM LoggedInUser";

        try (ResultSet results = runSelectQuery(query)) {
            if (results != null) {
                results.next();

                return new GamePlayStatsJson(results.getString(1),
                        results.getInt(2),
                        results.getInt(3),
                        results.getInt(4) / 1000);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    /**
     * Resets the game play stats for the logged-in user
     * @return If the update was successful
     */
    public boolean resetGamePlayStats() {
        var username = getLoggedInUser();

        var query = "UPDATE LoggedInUser SET scenariosCorrect=0, scenariosWrong=0, timeTaken=0 WHERE user=?";

        //Sets the parameters in the query to the username
        var params = new ArrayList<>();
        params.add(username);

        return runUpdateQuery(query, params) != 0 & updateUsersCurrentScore(0);
    }

    /**
     * Updates the game play stats for the logged-in user
     * @param correctJudgment
     * @param timeTakenIncrement
     * @return If the update was successful
     */
    public boolean updateGamePlayStats(boolean correctJudgment, int timeTakenIncrement) {
        var username = getLoggedInUser();

        var query = "";

        if (correctJudgment) {
            query = "UPDATE LoggedInUser SET scenariosCorrect=scenariosCorrect+1, timeTaken=timeTaken+? WHERE user=?";
        } else {
            query = "UPDATE LoggedInUser SET scenariosWrong=scenariosWrong+1, timeTaken=timeTaken+? WHERE user=?";
        }

        //Sets the parameters in the query
        var params = new ArrayList<>();
        params.add(timeTakenIncrement);
        params.add(username);

        return runUpdateQuery(query, params) != 0;

    }

    /**
     * Returns all available scenario IDs in a random order
     * @return An Arraylist of Scenario IDs
     */
    public ArrayList<Integer> scenarioQueue() {
        var returnList = new ArrayList<Integer>();
        var query = "SELECT scenarioId FROM Scenarios ORDER BY RAND()";

        try (ResultSet results = runSelectQuery(query, new ArrayList<>())) {
            if (results != null) {
                while (results.next()) {
                    returnList.add(results.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return returnList;
    }

    /**
     * Get scenario data
     * @param scenarioId the scenario ID
     * @return Scenario data
     */
    public ScenarioJson getScenario(int scenarioId) {
        var query = "SELECT * FROM Scenarios WHERE scenarioId = ?";

        // Create the parameter list and add the parameters (These will replace the above (?) in execution of the query)
        var parameters = new ArrayList<>();
        parameters.add(scenarioId);

        try (ResultSet results = runSelectQuery(query, parameters)) {
            if (results != null) {
                results.next();

                return new ScenarioJson(
                        scenarioId,
                        results.getString(2),
                        results.getString(3),
                        getTableData(scenarioId, "Emails", x -> new EmailJson(x.getString(3), x.getString(4))),
                        getTableData(scenarioId, "Payments", x -> new PaymentJson(x.getString(3), x.getInt(4))),
                        getTableData(scenarioId, "TextMessages", x -> new TextJson(x.getString(3), x.getString(4))),
                        getTableData(scenarioId, "Records", x -> new RecordJson(x.getString(3), x.getString(4))),
                        results.getInt(4),
                        results.getBoolean(5)
                );
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    /**
     * Returns the users and scores in the leaderboard ordered by their score
     * @return List of leaderboard jsons
     */
    public List<LeaderboardJson> getLeaderboardData() {
        var query = "SELECT username, highScore FROM Users ORDER BY highScore DESC";

        try (ResultSet results = runSelectQuery(query)) {
            var users = new ArrayList<LeaderboardJson>();
            var rank = 1;
            while (results.next()) {
                users.add(new LeaderboardJson(rank, results.getString(1), results.getInt(2)));
                rank++;
            }
            return users;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Returns the users current score
     * @return current score
     */
    public int getUsersCurrentScore() {
        var username = getLoggedInUser();

        var query = "SELECT currentScore FROM Users WHERE username=?";

        // Create the parameter list and add the parameters (These will replace the above (?) in execution of the query)
        var parameters = new ArrayList<>();
        parameters.add(username);

        return getIntFromQuery(query, parameters);
    }

    /**
     * Updates the users current score
     * @param score current score
     * @return If the update was successful
     */
    public boolean updateUsersCurrentScore(int score) {
        var username = getLoggedInUser();

        var query = "UPDATE Users SET currentScore=? WHERE username=?";

        // Create the parameter list and add the parameters (These will replace the above (?) in execution of the query)
        var parameters = new ArrayList<>();
        parameters.add(score);
        parameters.add(username);

        return runUpdateQuery(query, parameters) == 1;
    }

    /**
     * Sets the users high score if the current score is higher
     * @return If the update was successful
     */
    public boolean setUsersHighScore() {
        var username = getLoggedInUser();
        var score = getUsersCurrentScore();

        if (getUsersHighScore(username) < score) {
            var query = "UPDATE Users SET highScore=? WHERE username=?";

            // Create the parameter list and add the parameters (These will replace the above (?) in execution of the query)
            var parameters = new ArrayList<>();
            parameters.add(score);
            parameters.add(username);

            return runUpdateQuery(query, parameters) == 1;
        }
        return true;
    }

    /**
     * Private helper method to return the users current score
     * @return high score
     */
    private int getUsersHighScore(String username) {
        var query = "SELECT highScore FROM Users WHERE username=?";

        // Create the parameter list and add the parameters (These will replace the above (?) in execution of the query)
        var parameters = new ArrayList<>();
        parameters.add(username);

        return getIntFromQuery(query, parameters);
    }

    /**
     * Helper method for creating a list of objects from a table, for a given scenario
     * @param scenarioId Scenario data is related to
     * @param table Name of the table to query
     * @param constructor Method for creating an object of type J from a ResultSet
     * @return List of constructed objects from the query
     * @param <R> Type of object to return
     */
    private <R> List<R> getTableData(int scenarioId, String table, SQLFunction<ResultSet, R> constructor)  {
        ArrayList<R> returnList = new ArrayList<>();

        var query = "SELECT * FROM %s WHERE scenarioId = ?".formatted(table);

        // Create the parameter list and add the parameters (These will replace the above (?) in execution of the query)
        var parameters = new ArrayList<>();
        parameters.add(scenarioId);

        try (ResultSet results = runSelectQuery(query, parameters)) {
            while (results.next()) {
                returnList.add(constructor.apply(results));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return returnList;
    }

    private int getIntFromQuery(String query, ArrayList<Object> parameters) {
        try (ResultSet results = runSelectQuery(query, parameters)) {
            if (results != null) {
                results.next();
                return results.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return 0;
    }

    private String getStringFromQuery(String query, ArrayList<Object> parameters) {
        try (ResultSet results = runSelectQuery(query, parameters)) {
            if (results != null) {
                results.next();
                return results.getString(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return "";
    }

    /**
     * A general select query method with no parameters
     * @param query      SQL select query
     * @return Result of the select
     */
    private ResultSet runSelectQuery(String query) {
        return runSelectQuery(query, new ArrayList<>());
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

    /**
     * A functional interface for a function which can throw an SQL exception
     * @param <T> Input type for the function
     * @param <R> Return type of the function
     */
    @FunctionalInterface
    interface SQLFunction<T, R> extends Function<T, R> {
        @Override
        default R apply(final T elem) {
            try {
                return applyThrows(elem);
            } catch (final SQLException e) {
                System.err.println("SQL error when parsing data in lambda function");
                return null;
            }
        }

        R applyThrows(T elem) throws SQLException;
    }
}