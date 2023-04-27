import java.sql.*;
import java.util.Scanner;

class Driver {

    public static void main(String[] args) throws Exception {
    // take care of input and input buffer here
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection con = DriverManager.getConnection(
        //    "jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus", "student");
            "jdbc:oracle:thin:@sabzevi2.homeip.net:1521:orcl", "csus", "student");
        Statement st = con.createStatement();
        try
        {
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
               System.out.print("Insert Menu: \nFrom which table do you want to insert?");
               showTables(st);
               System.out.print("Type table_name: ");
               inputString = scanner.nextLine();

               if (inputString.equalsIgnoreCase("team")) {
                   showTeams(st);
                   System.out.println("This executed");
               }

               if (inputString.equalsIgnoreCase("player")) {
                   showPlayers(st);
               }
               break;
        }
            dropTables(st); // exit code 0
        }
        catch (SQLException e)
        {   
            e.printStackTrace();
            dropTables(st); // exit code 1
        }
    } // end main


    // Setup db
    public static void setupDB(Statement st) throws Exception{
        st.executeQuery("create table ling_team (teamid number NOT NULL, teamname varchar2(30) NOT NULL, CONSTRAINT pk_team PRIMARY KEY(teamid))");
        st.executeQuery("create table ling_player (playerid number NOT NULL, fname varchar2(20) NOT NULL, lname varchar2(20) NOT NULL, position varchar2(20), captainrole varchar2(1), dob date, salary number, teamid number NOT NULL, CONSTRAINT pk_player PRIMARY KEY(playerid, teamid), CONSTRAINT fk_team FOREIGN KEY (teamid) REFERENCES ling_team(teamid))");
        st.executeQuery("insert into ling_team values (100, 'Sharks')");
        st.executeQuery("insert into ling_player values (39, 'Logan', 'Couture', 'Center', 'C', to_date('1989-03-28', 'YYYY-MM-DD'), 2000000, 100)");
    }
    // Execute query
    public static void executeQuery(Statement st,String query) throws Exception{
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
    
    
    
    
    
    // Show tables
    public static void showTables(Statement st) throws Exception{
        System.out.println("TABLE_NAME");
        executeQuery(st, "select substr(table_name,6) from user_tables where lower(table_name) like 'ling%'");
    }
    
    // Drop tables
    public static void dropTables(Statement st) throws Exception
    {
        st.executeQuery("drop table ling_player cascade constraints");
        st.executeQuery("drop table ling_team cascade constraints");
        System.out.println("dropped tables successfully");
    }
    
    public static void showPlayers(Statement st) throws Exception
    {
        executeQuery(st, "select * from ling_player");
    }
   public static void showTeams(Statement st) throws Exception
    {
        executeQuery(st, "select * from ling_team");
    }
    
    // Insert
    public static void showInsertMenu(Statement st)
    {
    }
    
    // Update
    public static void showUpdateMenu(Statement st)
    {
    }
    
    // Delete
    public static void showDeleteMenu(Statement st)
    {
    }
    
    // View
    public static void showViewMenu(Statement st)
    {
    }


} // end program