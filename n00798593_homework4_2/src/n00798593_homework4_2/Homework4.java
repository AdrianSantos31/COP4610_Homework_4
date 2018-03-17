/**
 *
 */
package n00798593_homework4_2;

/**
 * @author adriansantos
 *
 */
//Library Imports
import java.sql.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;

import com.opencsv.CSVReader;


public class Homework4 {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException{
		// TODO Change this file path
		String filePath = "/Users/adriansantos/eclipse-workspace/n00798593_homework4_2/Data/";

		//JDBC Driver
		String jdbcDriver = "com.mysql.jdbc.Driver";
		String mysqlLink = "jdbc:mysql://localhost:3306/";

		//Strings
		String databaseName = "PlayerDB_Assign4";
		String username = "root";
		String password = "adriansantos";

		//Shortcuts
		String createDatabase = "CREATE DATABASE ";
		String showDatabases = "SHOW DATABASES";
		String createTable = "CREATE TABLE ";
		String showTables = "SHOW TABLES";
		String describe = "DESCRIBE ";

		//Prepared Statement for executing queries
		PreparedStatement preparedStatement;

		//Date Formatter
		SimpleDateFormat date = new SimpleDateFormat("\"YYYY-MM-DD\"");

		Connection connection = null;
		try {
			/* * * * * * * * * * * * * Loading JDBC * * * * * * * * * * * * * */
			//Load the JDBC driver
			Class.forName(jdbcDriver);
			System.out.println("Driver loaded");

			//Establish the connection
		    connection =
		       DriverManager.getConnection(mysqlLink,username, password);
		    System.out.println("Database successfully connected");

		    System.out.println("- - - - - - - - - -");//Separator

		    //Create a statement
			Statement statement = connection.createStatement();

			/* * * * * * * * * * * * * Create DB * * * * * * * * * * * * * */
			//Create a database
			System.out.println("Creating the database...");
			statement.executeUpdate(createDatabase + databaseName);
			System.out.println("Database successfully created");

			System.out.println("- - - - - - - - - -");//Separator

			//Use the database
			ResultSet useDatabase = statement.executeQuery
					("USE " + databaseName);
			System.out.println("Now using " + databaseName + "...");

			System.out.println("- - - - - - - - - -");

			/* * * * * * * * * * * * * Create Table * * * * * * * * * * * * * */
		    //Create Players Table			
			String createPlayers = createTable + "Players"
					+ "(player_id INT (10) unsigned NOT NULL AUTO_INCREMENT, "
					+ "tag VARCHAR(45), "
					+ "real_name VARCHAR(45), "
					+ "nationality VARCHAR(45), "
					+ "birthday DATE, "
					+ "game_race VARCHAR(45), "
					+ "PRIMARY KEY (player_id)"
					+ ");";

			System.out.println("Creating the Players Table...");
			statement.executeUpdate(createPlayers);
			System.out.println("Table Players successfully created");

			//Create Teams Table
			String createTeams = createTable + "Teams"
					+ "(team_id INT(10) unsigned NOT NULL, "
					+ "name VARCHAR(45), "
					+ "founded DATE, "
					+ "disbanded DATE, "
					+ "PRIMARY KEY (team_id)"
					+ ");";
			System.out.println("Creating the Teams Table...");
			statement.executeUpdate(createTeams);
			System.out.println("Table Teams successfully created");

			//Create Tournaments Table
			String createTournaments = createTable + "Tournaments"
					+ "(tournament_id INT NOT NULL AUTO_INCREMENT, "
					+ "name VARCHAR(100), "
					+ "region VARCHAR(45), "
					+ "major VARCHAR(45) NOT NULL, "
					+ "PRIMARY KEY (tournament_id)"
					+ ");";
			System.out.println("Creating the Tournaments Table...");
			statement.executeUpdate(createTournaments);
			System.out.println("Table Tournaments successfully created");
			
			//Create Members Table
			String createMembers = createTable + "Members"
					+ "(player INT (10) unsigned not NULL, "
					+ "team INT(10), "
					+ "start_date DATE not NULL, "
					+ "end_date DATE, "
					+ "PRIMARY KEY (player, start_date) "
					+ ");";
			System.out.println("Creating the Members Table...");
			statement.executeUpdate(createMembers);
			System.out.println("Table Members successfully created");



			//Create Matches Table
			String createMatches = createTable + "Matches"
					+ "(match_id INT (10) unsigned not NULL AUTO_INCREMENT,"
					+ "date DATE, "
					+ "tournament VARCHAR(45), "
					+ "playerA INT(10), "
					+ "playerB INT(10), "
					+ "scoreA VARCHAR(45), "
					+ "scoreB VARCHAR(45), "
					+ "offline VARCHAR(45), "
					+ "PRIMARY KEY (match_id)"
					+ ");";
			System.out.println("Creating the Matches Table...");
			statement.executeUpdate(createMatches);
			System.out.println("Table Matches successfully created");

			//Create Earnings Table
			String createEarnings = createTable + "Earnings"
					+ "(tournament INT(10) unsigned NOT NULL,"
					+ "player INT(10) unsigned, "
					+ "prize_money VARCHAR(45),"
					+ "position VARCHAR(45),"
					+ "PRIMARY KEY (tournament, player)"
					+ ");";

			System.out.println("Creating the Earnings Table...");
			statement.executeUpdate(createEarnings);
			System.out.println("Table Earnings successfully created");

			System.out.println("- - - - - - - - - -");//Separator

			//Check if tables were created
			ResultSet tableCheck = statement.executeQuery
					(showTables);

			System.out.println("Here are the tables: ");


			int countTable = 0;
			String describeTable = null;
			//Prints all tables in the database
			while(tableCheck.next()) {
				countTable++;
				System.out.println(countTable + " - " + tableCheck.getString(1));
				describeTable = describe + tableCheck.getString(1) + ";";
//				System.out.println(describeTable);
//				String resultCombo = tableCheck.getString(1)+"Description";
//				System.out.println(resultCombo);
//
//				ResultSet earningsDescription = statement.executeQuery(describeTable);
//				System.out.println("- - - - - - - - - -");//Separator
			}

			/* * * * * * * * * * * * * Loading data * * * * * * * * * * * * * */
			// Earnings data upload
			System.out.println("Now inserting earnings.csv...");
			String insertEarningsCSV = "LOAD DATA LOCAL INFILE '" + filePath + "earnings.csv'"
					+ "INTO TABLE Earnings FIELDS TERMINATED BY ','"
					+ "LINES TERMINATED BY '\n'";
			preparedStatement = connection.prepareStatement(insertEarningsCSV);
			ResultSet earningsTest = preparedStatement.executeQuery();
			//Clean up
			earningsTest.close();
			System.out.println("Data from earnings.csv was inserted");
			
			//Matches Data Upload
			System.out.println("Now inserting matches_v2.csv...");
			String insertMatchesCSV = "LOAD DATA LOCAL INFILE '" + filePath + "matches_v2.csv'"
					+ "INTO TABLE Matches FIELDS TERMINATED BY ',' "
					+ "LINES TERMINATED BY '\n' "
					+ "(match_id, @temp_date, tournament, playerA, playerB, scoreA, scoreB, offline) " 
					+ "SET date = STR_TO_DATE(@temp_date, '\"%Y-%m-%d\"');";
			preparedStatement = connection.prepareStatement(insertMatchesCSV);
			ResultSet matchesTest = preparedStatement.executeQuery();
			//Clean up
			matchesTest.close();
			System.out.println("Data from matches_v2.csv was inserted");
			
			//Members Data Upload
			System.out.println("Now inserting members.csv...");
			String insertMembersCSV = "LOAD DATA LOCAL INFILE '" + filePath + "members.csv'"
					+ "INTO TABLE Members FIELDS TERMINATED BY ',' "
					+ "LINES TERMINATED BY '\n' "
					+ "(player, team, @temp_start_date, @temp_end_date) " 
					+ "SET start_date = STR_TO_DATE(@temp_start_date, '\"%Y-%m-%d\"'), "
					+ "    end_date = STR_TO_DATE(@temp_end_date, '\"%Y-%m-%d\"');";
			preparedStatement = connection.prepareStatement(insertMembersCSV);
			ResultSet membersTest = preparedStatement.executeQuery();
			//Clean up
			membersTest.close();
			System.out.println("Data from members.csv was inserted");
			
			//Players Data Upload
			System.out.println("Now inserting players.csv...");
			String insertPlayersCSV = "LOAD DATA LOCAL INFILE '" + filePath + "players.csv'"
					+ "INTO TABLE Players FIELDS TERMINATED BY ','"
					+ "LINES TERMINATED BY '\n'"
					+ "(player_id, tag, real_name, nationality, @birth_date, game_race) "
					+ "SET birthday = STR_TO_DATE(@birth_date, '\"%Y-%m-%d\"');";
			preparedStatement = connection.prepareStatement(insertPlayersCSV);
			ResultSet playersTest = preparedStatement.executeQuery();
			//Clean up
			playersTest.close();
			System.out.println("Data from players.csv was inserted");
			
			//Teams Data Upload
			System.out.println("Now inserting teams.csv...");
			String insertTeamsCSV = "LOAD DATA LOCAL INFILE '" + filePath + "teams.csv'"
					+ "INTO TABLE Teams FIELDS TERMINATED BY ','"
					+ "LINES TERMINATED BY '\n'"
					+ "(team_id, name, @founded_date, @disbanded_date)"
					+ "SET founded = STR_TO_DATE(@founded_date, '\"%Y-%m-%d\"'), "
					+ "    disbanded = STR_TO_DATE(@disbanded_date, '\"%Y-%m-%d\"');";
			preparedStatement = connection.prepareStatement(insertTeamsCSV);
			ResultSet teamsTest = preparedStatement.executeQuery();
			//Clean up
			teamsTest.close();
			System.out.println("Data from teams.csv was inserted");
			
			//Tournaments Data Upload
			System.out.println("Now inserting tournaments.csv...");
			String insertTournamentsCSV = "LOAD DATA LOCAL INFILE '" + filePath + "tournaments.csv'"
					+ "INTO TABLE Tournaments FIELDS TERMINATED BY ','"
					+ "LINES TERMINATED BY '\n'";
			preparedStatement = connection.prepareStatement(insertTournamentsCSV);
			ResultSet tournamentsTest = preparedStatement.executeQuery();
			//Clean up
			tournamentsTest.close();
			System.out.println("Data from tournaments.csv was inserted");
			
/*			
			/* * * * * * * * * * * * * Display Menu* * * * * * * * * * * * * */
			System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");//Separator
			System.out.println("Menu: Pick a choice of query **Press 0 to exit");
			System.out.println("- - - - - - - - - - - - - - -");
			System.out.println("1 - Given a year and month, provide the real name, tag and nationality "
					+ "of players who were born in that month");
			System.out.println("2 - Given a player id and a team id, add that player as a member of "
					+ "the specified team, with the start date set according to the current system "
					+ "time.\n    If the player is currently a member of another team, the database "
					+ "should also be updated to reflect their departure from the \"old\" team\n    "
					+ "with the end date set as above. If the player was already a current member of "
					+ "the given \"new\" team, no changes are necessary");
			System.out.println("3 - Show the list of real names and birthdays of each input "
					+ "nationality (e.g., KR ~ non-Korean) player who was born in the input year "
					+ "(e.g., 1988)");
			System.out.println("4 - A \"triple crown\" is the accomplishment of having won a major "
					+ "championship (i.e. came in the first position in a major tournament) in each "
					+ "of the three main regions\n    namely: Europe (EU), America (AM), and Korea "
					+ "(KR). List the tag and game race of each player who has managed to attain a "
					+ "triple crown at least once");
			System.out.println("5 - List the former candidates of \"ROOT Gaming\". Give the tag "
					+ "and real name of each player, and the data of their most recent departure "
					+ "from the team");

			/* * * * * * * * * * * * * Get User Input * * * * * * * * * * * * */
			Scanner userInput = new Scanner(System.in);
			System.out.print("Query Number: ");
			int userChoice;

		    while ((userChoice = userInput.nextInt()) != 0) {
				/* * * * * * * * * * * * * Switch Case * * * * * * * * * * * * * */
		    		switch(userChoice){
					case 1:
						System.out.println("You've picked choice 1");
						System.out.print("Enter a year(YYYY): ");
						String year_case_1 = userInput.next();
						System.out.print("Enter a month: ");
						String month_case_1 = userInput.next();
						ResultSet Query_1 = statement.executeQuery("SELECT real_name, tag, nationality, birthday FROM Players WHERE (YEAR(birthday) = " + year_case_1 + " AND MONTH(birthday) = " + month_case_1 + ");");
						while(Query_1.next()) {
							System.out.printf("%-20s | %-20s | %-7s | %-7s", Query_1.getString(1), Query_1.getString(2),
									Query_1.getString(3), Query_1.getString(4) + "\n");
						}//end while
						System.out.print("Query Number(Enter 0 to exit): ");
						break;
					case 2:
						System.out.println("You've picked choice 2");
						System.out.print("Enter the player id: ");
						String player_id_case_2 = userInput.next();
						System.out.print("Enter the team id: ");
						String team_id_case_2 = userInput.next();
						//ResultSet Query_2 = statement.executeQuery("INSERT INTO Members (player, team, start_date) VALUES (" + player_id_case_2 + "," +  team_id_case_2 + ", CURDATE()); ");
						int Query_2 = statement.executeUpdate("INSERT INTO Members (player,team,start_date) VALUES ("
								+ player_id_case_2 +" ," + team_id_case_2 + " , CURDATE());");
						int Query_2_1 = statement.executeUpdate("UPDATE Members m SET end_date = CURDATE() WHERE m.player = "
								+ player_id_case_2 +" AND m.team = " + team_id_case_2 +  " AND m.end_date IS NULL;");	
						if(Query_2==1) {
							System.out.println("Accepted: Data inserted");
						}else
							System.out.println("Failed: Data NOT inserted");
						if(Query_2_1 == 1) {
							System.out.println("Accepted: Data updated");
						}else
							System.out.println("Failed: Data NOT updated");
						System.out.print("Query Number(Enter 0 to exit): ");
						break;
					case 3:
						System.out.println("You've picked choice 3");
						System.out.print("Enter a nationality (To see choices, type \"help\"): ");
						String nationality_case_3 = userInput.next();
						if (nationality_case_3.equals("help")) {
							System.out.println(" KR, FR, CA, PL, TW, UA, ES, RU,\n "
											 + "SE, AT, DE, BY, FI, US, BE, NO,\n "
											 + "DK, RS, AU, UK, RO, NL, CN, CH,\n "
											 + "PE, CL, MX, PT, IL, IT, CO, NZ,\n "
											 + "LT, BG, CZ, ZA, KZ, AR, HU, GR,\n "
											 + "BR, SG, TH, PA, PH, AM, MY, HR,\n "
											 + "SK, JP, EC, SI, IN, VN, IE, IS, IR\n");
							System.out.print("Enter a nationality: ");
							nationality_case_3 = userInput.next();
						}
						System.out.print("Enter a year: ");
						String year_case_3 = userInput.next();
						ResultSet Query_3 = statement.executeQuery("SELECT real_name, birthday FROM Players WHERE nationality='\"" + nationality_case_3 + "\"' AND YEAR(birthday) = "+ year_case_3 + ";");
						while(Query_3.next()) {
							System.out.printf("%-20s | %-10s\n", Query_3.getString(1), Query_3.getString(2));
						}//end while
						System.out.print("Query Number(Enter 0 to exit): ");
						break;
					case 4:
						System.out.println("You've picked choice 4");
						ResultSet Query_4 = statement.executeQuery("SELECT p.tag, p.game_race from Players p, Earnings e WHERE "
								+ "p.player_id = e.player AND e.position=1;");
						while(Query_4.next()) {
							System.out.printf("%-15s | %-15s\n", Query_4.getString(1), Query_4.getString(2));
						}//end while
						System.out.print("Query Number(Enter 0 to exit): ");
						break;
					case 5:
						System.out.println("You've picked choice 5");
						ResultSet Query_5 = statement.executeQuery("SELECT p.tag, p.real_name, m.end_date FROM players P INNER JOIN "
								+ "Members m ON p.player_id = m.player JOIN Teams t ON m.team = t.team_id AND t.name = '\"ROOT Gaming\"' "
								+ "AND m.end_date IS NOT NULL;");
						while(Query_5.next()) {
							System.out.printf("%-15s | %-25s | %-12s \n", Query_5.getString(1), Query_5.getString(2), Query_5.getString(3));
						}//end while
						System.out.print("Query Number(Enter 0 to exit): ");
						break;
					default:
						System.out.println("The query number is not in the choices");
						System.out.print("Query Number(Enter 0 to exit): ");
				}//end switch-case
		      }//end while loop
		    System.out.println("Program closing..");
		    System.exit(0);
			//Close the connection
			connection.close();
		}//end try
		catch (SQLException ex) {
			// Error Handler
			//ex.printStackTrace();
		    System.out.println("SQLException: " + ex.getMessage());
		    // DEBUG - System.out.println("SQLState: " + ex.getSQLState());
		    // DEBUG - System.out.println("VendorError: " + ex.getErrorCode());
		}//end catch
	}//end main
}//end Homework4 Class

//Code Dump
/* * * * * * * * * * * * * Table Description * * * * * * * * * * * * * */
//Execute a statement
/*
ResultSet resultSet = statement.executeQuery
		(showDatabases);
System.out.println("Here are the databases in the server: ");

//Iterate through the database
while(resultSet.next()) {
	System.out.println(resultSet.getString(1));
}

System.out.println("- - - - - - - - - -");//Separator
*/
/*
 * 			System.out.println("Here are the current databases in the server after database creation:");

//Second check if database was created
ResultSet resultSetCheck = statement.executeQuery
		(showDatabases);

//Second check: iterate through the databases
while(resultSetCheck.next()) {
	System.out.println(resultSetCheck.getString(1));
}
System.out.println("- - - - - - - - - -");//Separator
*/





//Earnings Description
/*
System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");//Separator
ResultSet EarningsDescription = statement.executeQuery(describe + "Earnings");
System.out.println("Description of Earnings Table");
System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", "Field", "Type", "Null", "Key", "Default", "Extra");
System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");//Separator
while(EarningsDescription.next()) {
	System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", EarningsDescription.getString(1), EarningsDescription.getString(2),
			EarningsDescription.getString(3), EarningsDescription.getString(4), EarningsDescription.getString(5), EarningsDescription.getString(6));
}//end while

//Matches Description
System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");//Separator
ResultSet MatchesDescription = statement.executeQuery(describe + "Matches");
System.out.println("Description of Matches Table");
System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", "Field", "Type", "Null", "Key", "Default", "Extra");
System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");//Separator
while(MatchesDescription.next()) {
	System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", MatchesDescription.getString(1), MatchesDescription.getString(2),
			MatchesDescription.getString(3), MatchesDescription.getString(4), MatchesDescription.getString(5), MatchesDescription.getString(6));
}//end while

//Members Description
System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");//Separator
ResultSet MembersDescription = statement.executeQuery(describe + "Members");
System.out.println("Description of Members Table");
System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", "Field", "Type", "Null", "Key", "Default", "Extra");
System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");//Separator
while(MembersDescription.next()) {
	System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", MembersDescription.getString(1), MembersDescription.getString(2),
			MembersDescription.getString(3), MembersDescription.getString(4), MembersDescription.getString(5), MembersDescription.getString(6));
}//end while

//Players Description
System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");//Separator
ResultSet PlayersDescription = statement.executeQuery(describe + "Players");
System.out.println("Description of Players Table");
System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", "Field", "Type", "Null", "Key", "Default", "Extra");
System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");//Separator
while(PlayersDescription.next()) {
	System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", PlayersDescription.getString(1), PlayersDescription.getString(2),
			PlayersDescription.getString(3), PlayersDescription.getString(4), PlayersDescription.getString(5), PlayersDescription.getString(6));
}//end while

//Teams Description
System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");//Separator
ResultSet TeamsDescription = statement.executeQuery(describe + "Teams");
System.out.println("Description of Teams Table");
System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", "Field", "Type", "Null", "Key", "Default", "Extra");
System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");//Separator
while(TeamsDescription.next()) {
	System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", TeamsDescription.getString(1), TeamsDescription.getString(2),
			TeamsDescription.getString(3), TeamsDescription.getString(4), TeamsDescription.getString(5), TeamsDescription.getString(6));
}//end while

//Tournaments Description
System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");//Separator
ResultSet TournamentsDescription = statement.executeQuery(describe + "Tournaments");
System.out.println("Description of Tournaments Table");
System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", "Field", "Type", "Null", "Key", "Default", "Extra");
System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");//Separator
while(TournamentsDescription.next()) {
	System.out.printf("%-15s | %-15s | %-4s | %-4s | %-6s | %-5s\n", TournamentsDescription.getString(1), TournamentsDescription.getString(2),
			TournamentsDescription.getString(3), TournamentsDescription.getString(4), TournamentsDescription.getString(5), TournamentsDescription.getString(6));
}//end while
System.out.println("");//Spacer
*/

/* * * * * * * * * * * * * * * EXTRA CREDIT * * * * * * * * * * * * * * 
System.out.println("6 - Extra Credit - From among those Protoss (P) players who have "
		+ "played at least 10 games against Terran (T) opponents, find those who were "
		+ "able to win more than 65% of their \"P vs. T\" matches\n    Give the tag, "
		+ "nationality and P vs T's win rate (in percent) of these players. Sort the "
		+ "players according to their P vs. T's win rate, witht he most succesful player "
		+ "first.");
System.out.println("7 - Extra Credit - List all the teams founded before 2011 that are "
		+ "still active (not yet disbanded). For each such team, give the team name, "
		+ "date founded, and the number of current team members who\n    play Protoss, "
		+ "Terran and Zerg, respectively. Sort the teams alphabetically by name");
*/

/* * * * * * * * * * * * * * EXTRA CREDIT - Print Statements * * * * * * 
case 6:
	System.out.println("You've picked choice 6");
	ResultSet Query_6 = statement.executeQuery("SELECT\n" + 
			"	IF (COUNT/(SELECT COUNT(m.playerA) FROM Matches m GROUP BY m.playerA)>0.65,\n" + 
			"   (SELECT m.playerA, p.tag, count(m.playerA) as woncount FROM Matches m, Players p\n" + 
			"   WHERE m.playerA = p.player_id  AND (m.scoreA > m.scoreB) AND woncount >10 GROUP BY\n" + 
			"   m.playerA);");
	while(Query_6.next()) {
		System.out.printf("%-25s | %-15s\n", Query_6.getString(1), Query_6.getString(2));
	}//end while
	System.out.print("Query Number(Enter 0 to exit): ");
	break;
case 7:
	System.out.println("You've picked choice 7");
	System.out.print("Query Number(Enter 0 to exit): ");
	break;
case 8:
	System.out.println("You've picked choice 8");
	ResultSet Query_8 = statement.executeQuery("SELECT player, team, start_date, end_date FROM Members WHERE player = 2000");
	while(Query_8.next()) {
		System.out.printf("%-15s | %-15s | %-15s | %-15s\n",Query_8.getString(1),Query_8.getString(2),Query_8.getString(3),Query_8.getString(4));
	}
	break;
	*/