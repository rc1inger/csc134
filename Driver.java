import java.sql.*;
import java.util.Scanner;

class Driver {

    public static void main(String[] args) throws Exception {
        // take care of input and input buffer here
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection con = DriverManager.getConnection(
            "jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus", "student");
        Statement st = con.createStatement();
        boolean exit=false;
        setupDB(st);
        try {
            do {
                // Menu Array
                String[] menu = {
                    "Insert",
                    "Delete",
                    "Update",
                    "View",
                    "Quit"
                };

                for (int i = 0; i < menu.length; i++) {
                    System.out.println(i + 1 + ")\t\t" + menu[i]);
                }

                System.out.println();
                System.out.print("Choose#: ");
                Scanner scanner = new Scanner(System.in);
                String inputString = scanner.nextLine();
                int choice = Integer.parseInt(inputString);
                switch (choice) {
                case 1:
                    insertMenu(st);
                    break;
                case 2:
                    deletePlayer(st);
                    break;
                case 3:
                    updateTeam(st);
                    break; 
                case 4:
                    viewData(st);
                    break;
                case 5:
                    exit=true;
                default:
                    break;
                }
            }
            while (!exit);
            dropTables(st); // exit code 0
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            dropTables(st); // exit code 1
        }
    } // end main

    // Setup db
    public static void setupDB(Statement st) throws Exception {
        st.executeQuery("create table ling_team (teamid number NOT NULL, teamname varchar2(30) NOT NULL, CONSTRAINT pk_team PRIMARY KEY(teamid))");
        st.executeQuery("create table ling_player (playerid number NOT NULL, fname varchar2(20) NOT NULL, lname varchar2(20) NOT NULL, pos varchar2(20), captainrole varchar2(1), dob date, salary number, teamid number NOT NULL, CONSTRAINT pk_player PRIMARY KEY(playerid, teamid), CONSTRAINT fk_player_team FOREIGN KEY (teamid) REFERENCES ling_team(teamid) ON DELETE CASCADE)");
        st.executeQuery("insert into ling_team values (100, 'Sharks')");
        st.executeQuery("insert into ling_player values (39, 'Logan', 'Couture', 'C', 'C', to_date('1989-03-28', 'YYYY-MM-DD'), 2000000, 100)");
    }
    
    
    // View data
    public static void viewData(Statement st) throws Exception {
        try{
            executeQuery(st, "select t.teamname, (fname || ' ' || lname) as Name, pos, captainrole, trunc((months_between(sysdate, dob)/12)) as age, salary FROM ling_player p INNER JOIN ling_team t ON t.teamid=p.teamid"); 
        
        }
        catch(Exception e) {
        
        }
    }
    
    // Update team
    public static void updateTeam(Statement st) {
        try{
           showTeams(st);
           System.out.println("Case-sensitive");
           System.out.print("Type the team name: ");
           Scanner scanner = new Scanner(System.in);
           String inputStringTeamName = scanner.nextLine();
           System.out.print("What do you want to change it to?: ");
           String inputStringNewTeamName = scanner.nextLine();
           
           st.executeQuery("update ling_team set teamname='"+inputStringNewTeamName+"' where teamname='"+inputStringTeamName+"'");
           showTeams(st);
           System.out.println();

         }
         catch (Exception e) {
             e.printStackTrace();
         }
    }
    
    // Delete player
    public static void deletePlayer(Statement st) throws Exception {
        try{
            Scanner scanner = new Scanner(System.in);
            System.out.println();
            System.out.println("Delete Player Menu:\n");
            showTeams(st);
            System.out.println("What is the team ID #? ");
            String inputStringTeamID = scanner.nextLine();
            if (inputStringTeamID.isEmpty()) {
                System.out.println("input must not be empty!\n");
            } else if (!isDigit(inputStringTeamID)) {
                System.out.println("input must be a number!\n");
            } else if (!checkTeamID(st, inputStringTeamID)) {
                System.out.println("That team ID # does not exists!\n");
            } else if (isDigit(inputStringTeamID)) {
                executeQuerySimple(st, "select teamname from ling_team where teamid="+inputStringTeamID);
                System.out.println(" Roster");
                showPlayers(st, inputStringTeamID);
                System.out.print("What is the playerid #?: ");
                String inputStringPlayerID = scanner.nextLine();
                System.out.print("Delete player ");
                executeQuerySimple(st, "select playerid, (' ' || fname || ' ' || lname) as Name from ling_player where playerid="+inputStringPlayerID);
                System.out.print("?(y/n):");
                
                char deleteConfirm = scanner.next().charAt(0);
                
                if (checkPlayer(st, inputStringPlayerID) && deleteConfirm == 'y')
                {
                    st.executeUpdate("delete from ling_player where teamid="+inputStringTeamID+" AND playerid="+inputStringPlayerID);
                    
                    if (executeQuery(st, true, "select count(*) from ling_player where teamid="+inputStringTeamID).equals("0")) {
                        System.out.println("No players. Deleting the team too.");
                        st.executeUpdate("delete from ling_team where teamid="+inputStringTeamID);
                    }
                    showPlayers(st);
                    System.out.println("Successfully deleted |"+inputStringPlayerID+" |\n");
                }
                else{
                    System.out.println("exiting delete menu...");
                }
            } else
                System.out.println("Something unexpected happened");

        }
        catch(Exception e) {
            e.printStackTrace();
            dropTables(st);
        }
    }


    // Insert menu
    public static void insertMenu(Statement st) throws Exception {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println();
            System.out.println("Insert Menu: \n");
            showTables(st);
            System.out.print("Type table_name: ");
            String inputStringTableName = scanner.nextLine();

            /* Does not check for non-digit, does not check if teamname already exists */
            if (inputStringTableName.equalsIgnoreCase("team")) {
                showTeams(st);
                insertTeam(st);
            } else if (inputStringTableName.equalsIgnoreCase("player")) {
                showTeams(st);
                insertPlayer(st);
            }
        } catch (Exception e) {
            e.printStackTrace();
            dropTables(st);
        }
    }

    // Insert team
    public static void insertTeam(Statement st) throws Exception {
        try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Create a teamID # of the new team: ");
                String inputStringTeamID = scanner.nextLine();
// -------------SQLException result set after last row. GeneratedScrollableResultSet.getString()
                if (inputStringTeamID.isEmpty()) {
                    System.out.println("input must not be empty!\n");
                }else if (!isDigit(inputStringTeamID)) {
                    System.out.println("input must be a number!");
                } else if (checkTeamID(st, inputStringTeamID)) {
                    System.out.println("That teamID # already exists!\n");
                    return;
                } 
                
                System.out.println("What is the team name?");
                String inputStringTeamName = scanner.nextLine();
                st.executeQuery("insert into ling_team values(" + inputStringTeamID + ",'" + inputStringTeamName + "')"); // not using addRow
                showTeams(st);
                System.out.println("Successfully added |"+inputStringTeamID+" |"+inputStringTeamName+"\n");
        } catch (Exception e) {
            e.printStackTrace();
            dropTables(st);
        }

    }
   
    // Insert player
    public static void insertPlayer(Statement st) throws Exception {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Type in the team ID # to insert player: ");
            String inputStringTeamID = scanner.nextLine();
            if (!checkTeamID(st, inputStringTeamID)) {
                System.out.println("Team id # doesn't exist.");
                System.out.println("Do you want to create a new team? (y/n): ");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("n"))
                    return;
                else
                    insertTeam(st);
            }
            if (checkTeamID(st, inputStringTeamID)) {
                showTeamAndPlayers(st, inputStringTeamID);
                System.out.print("Player number: ");
                int playerID = Integer.parseInt(scanner.nextLine());
                System.out.print("First name: ");
                String fname = scanner.nextLine();
                System.out.print("Last name: ");
                String lname = scanner.nextLine();
                System.out.print("Position(F/D/G): ");
                String pos = scanner.nextLine();
                System.out.print("Captain(C/A): ");
                String captainRole = scanner.nextLine();
                System.out.print("Date of birth(MM-DD-YYYY): ");
                String dob = scanner.nextLine();
                System.out.print("Salary: ");
                double salary = Double.parseDouble(scanner.nextLine());
                addRow(st, "ling_player", playerID + ",'" + fname + "','" + lname + "','" + pos + "','" + captainRole + "',to_date('" + dob + "','MM-DD-YYYY')," + salary + "," + inputStringTeamID);
                showTeamAndPlayers(st, inputStringTeamID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            dropTables(st);
        }

    }

    // Show Team Name and Players
    public static void showTeamAndPlayers(Statement st, String teamid) throws Exception {
        try {
            System.out.println();
            executeQuerySimple(st, "select teamname from ling_team where teamid=" + teamid);
            System.out.println(" Roster");
            System.out.println();
            executeQuery(st, "select column_name from user_tab_columns where lower(table_name)='ling_player'");
            showPlayers(st, teamid);
        } catch (Exception e) {
            e.printStackTrace();
            dropTables(st);
        }
    }

    // Check team
    public static boolean checkTeamID(Statement st, String teamID) throws Exception {
        try {
            ResultSet rs = st.executeQuery("select teamid from ling_team where teamid=" + teamID);
            rs.next();
            String result = rs.getString(1);
            if (teamID.equals(result))
                return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Check player
    public static boolean checkPlayer(Statement st, String playerID) throws Exception {
        try {
            ResultSet rs = st.executeQuery("select playerid from ling_player where playerid=" + playerID);
            rs.next();
            String result = rs.getString(1);
            if (playerID.equals(result))
                return true;
            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            dropTables(st);
            return false;
        }
    }

    // Add row
    public static void addRow(Statement st, String table_name, String values) throws Exception {
        try {
            st.executeQuery("insert into " + table_name + " values(" + values + ")");
        } catch (Exception e) {
            e.printStackTrace();
            dropTables(st);
        }
    }

    // Execute query
    public static void executeQuery(Statement st, String query) throws Exception {
        try{
              ResultSet rs = st.executeQuery(query);
              while (rs.next()) {
                  int numColumns = rs.getMetaData().getColumnCount();
                  for (int i = 1; i <= numColumns; i++) {
                      String value = rs.getString(i);
                      if (value != null) {
                          System.out.print("| " + value + "\t");
                      }
                  }
                  System.out.println();
              }
              System.out.println();
         }
         catch (Exception e) {
             e.printStackTrace();
             dropTables(st);
         }
    }
    public static void executeQuerySimple(Statement st, String query) throws Exception {
        try{ 
           ResultSet rs = st.executeQuery(query);
           while (rs.next()) {
               int numColumns = rs.getMetaData().getColumnCount();
               for (int i = 1; i <= numColumns; i++) {
                   String value = rs.getString(i);
                   if (value != null) {
                       System.out.print(value);
                   }
               }
           }
        }
         catch (Exception e) {
             e.printStackTrace();
             dropTables(st);
         }
    }

    // override method to return integer
    public static String executeQuery(Statement st, boolean bool, String query) throws Exception {
        ResultSet rs = st.executeQuery(query);
        try {
            if (rs.next()) {
                return rs.getString(1);
            }
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            dropTables(st);
        }
        return "0";
    }
    // Show tables
    public static void showTables(Statement st) throws Exception {
        System.out.println("TABLE_NAME");
        executeQuery(st, "select substr(table_name,6) from user_tables where lower(table_name) like 'ling%'");
    }

    // Drop tables
    public static void dropTables(Statement st) throws Exception {
        st.executeQuery("drop table ling_player cascade constraints");
        st.executeQuery("drop table ling_team cascade constraints");
        System.out.println("dropped tables successfully");
    }
    // Show players
    public static void showPlayers(Statement st, String teamID) throws Exception {
        try{
            executeQuery(st, "select * from ling_player where teamid=" + teamID);
        }
        catch(Exception e){
            e.printStackTrace();
            dropTables(st);
        }
    }
    public static void showPlayers(Statement st) throws Exception {
        executeQuery(st, "select * from ling_player");
    }

    // Show teams
    public static void showTeams(Statement st) throws Exception {
        executeQuery(st, "select * from ling_team");
    }

    // Helper fn
    public static boolean isDigit(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }
} // end program