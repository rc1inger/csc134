import java.sql.*;
import java.util.Scanner;

class Driver {

    public static void main(String[] args) throws Exception {
        // take care of input and input buffer here
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection con = DriverManager.getConnection(
            "jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus", "student");
        Statement st = con.createStatement();
        try {
            setupDB(st);
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
                System.out.println();
                System.out.println("Insert Menu: \nFrom which table do you want to insert?");
                showTables(st);
                System.out.print("Type table_name: ");
                String inputStringTableName = scanner.nextLine();

                /* Does not check for non-digit, does not check if teamname already exists */
                if (inputStringTableName.equalsIgnoreCase("team")) {
                    showTeams(st);
                    System.out.println("Create a teamID # of the new team: ");
                    String inputStringTeamID = scanner.nextLine();
                    
                    
                    if (inputStringTeamID.isEmpty()){
                        System.out.println("input must not be empty!");
                    }
                    else if (checkTeam(st, inputStringTeamID)) {
                        System.out.println("That teamID # already exists!");
                    }
                    else if (isDigit(inputStringTeamID)) {
                        System.out.println("What is the team name?");
                        String inputStringTeamName = scanner.nextLine();
                        addRow(st, "ling_"+inputStringTableName,""+ inputStringTeamID+", '"+inputStringTeamName+"'");
                        showTeams(st);
                    } else
                        System.out.println("Something unexpected happened");
                }
                
                
                
                else if (inputStringTableName.equalsIgnoreCase("player")) {
                    showTeams(st);
                    insertPlayer(st);
                }
                break;

            case 2:
                System.out.println("");
                break;

            default:
                break;
            }
            dropTables(st); // exit code 0
        } catch (SQLException e) {
            e.printStackTrace();
            dropTables(st); // exit code 1
        }
    } // end main

    // Setup db
    public static void setupDB(Statement st) throws Exception {
        st.executeQuery("create table ling_team (teamid number NOT NULL, teamname varchar2(30) NOT NULL, CONSTRAINT pk_team PRIMARY KEY(teamid))");
        st.executeQuery("create table ling_player (playerid number NOT NULL, fname varchar2(20) NOT NULL, lname varchar2(20) NOT NULL, position varchar2(20), captainrole varchar2(1), dob date, salary number, teamid number NOT NULL, CONSTRAINT pk_player PRIMARY KEY(playerid, teamid), CONSTRAINT fk_team FOREIGN KEY (teamid) REFERENCES ling_team(teamid))");
        st.executeQuery("insert into ling_team values (100, 'Sharks')");
        st.executeQuery("insert into ling_player values (39, 'Logan', 'Couture', 'C', 'C', to_date('1989-03-28', 'YYYY-MM-DD'), 2000000, 100)");
    }
    
    // Insert player
    public static void insertPlayer(Statement st) throws Exception{
        try {
           Scanner scanner = new Scanner(System.in);
           System.out.println("Type in the team ID # to insert player: ");
           String inputStringTeamID = scanner.nextLine();
           if (checkTeam(st, inputStringTeamID)){
               showTeamAndPlayers(st, inputStringTeamID);
               System.out.print("Player number: ");
               int playerID = Integer.parseInt(scanner.nextLine());
               System.out.print("First name: ");
               String fname = scanner.nextLine();
               System.out.print("Last name: ");
               String lname = scanner.nextLine();
               System.out.print("Position(F/D/G): ");
               String position = scanner.nextLine();
               System.out.print("Captain(C/A): ");
               String captainRole = scanner.nextLine();
               System.out.print("Date of birth(MM-DD-YYYY): ");
               String dob = scanner.nextLine();
               System.out.print("Salary: ");
               double salary = Double.parseDouble(scanner.nextLine());
               addRow(st, "ling_player", playerID+",'"+fname+"','"+lname+"','"+position+"','"+captainRole+"',to_date('"+dob+"','MM-DD-YYYY'),"+salary+","+inputStringTeamID);
               showTeamAndPlayers(st, inputStringTeamID);
           }
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }

    }
    
    // Show Team Name and Players
    public static void showTeamAndPlayers(Statement st, String teamid) throws Exception{
        try{
           System.out.println();
           executeQuerySimple(st, "select teamname from ling_team where teamid="+teamid);
           System.out.println(" Roster");
           System.out.println();
           showPlayers(st, teamid);
           }
       catch(Exception e)
       {
           e.printStackTrace();
           dropTables(st);
       }
    }
    
    // Check team
    public static boolean checkTeam(Statement st, String teamID) throws Exception{
        try{
            ResultSet rs = st.executeQuery("select teamid from ling_team where teamid="+teamID);
            rs.next();
            String result = rs.getString(1);
            if (teamID.equals(result))
                return true;
            return false;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        
    }
    
    // Add row
    public static void addRow(Statement st, String table_name, String values) throws Exception{
        try {
                st.executeQuery("insert into "+table_name+" values("+values+")");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    // Execute query
    public static void executeQuery(Statement st, String query) throws Exception {
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
    
    public static void executeQuerySimple(Statement st, String query) throws Exception {
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

    // override method to return integer
    public static String executeQuery(Statement st, boolean bool, String query) throws Exception {
        ResultSet rs = st.executeQuery(query);
        try
        {
            if (rs.next()) {
                return rs.getString(1);
            }
            return "0";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("input must be a digit.");
        }
        return rs.getString(1);
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
        executeQuery(st, "select * from ling_player where teamid="+teamID);
    }
    public static void showPlayers(Statement st) throws Exception {
        executeQuery(st, "select * from ling_player");
    }
    
    // Show teams
    public static void showTeams(Statement st) throws Exception {
        executeQuery(st, "select * from ling_team");
    }
    // Insert
    public static void showInsertMenu(Statement st) {}

    // Update
    public static void showUpdateMenu(Statement st) {}

    // Delete
    public static void showDeleteMenu(Statement st) {}

    // View
    public static void showViewMenu(Statement st) {}

        // Helper fn
    public static boolean isDigit(String s)
    {
        for (int i=0; i<s.length(); i++)
        {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }
} // end program