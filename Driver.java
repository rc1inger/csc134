import java.sql.*;
import java.util.Scanner;

class Driver {

    public static void main(String[] args) throws Exception {
    // take care of input and input buffer here
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection con = DriverManager.getConnection(
            "jdbc:oracle:thin:@localhost:1521:CSC134", "rickc", "Password123");

        setupDB(con);
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
        switch (scanner.nextInt()) {
            case 1:
               String userInput = scanner.nextLine();
               System.out.println(); 
               System.out.print("Insert Menu: \nFrom which table do you want to insert?");
               showTables(con);
               System.out.print("Type table_name: ");
               userInput = scanner.nextLine();

               if (userInput.equalsIgnoreCase("team")) {
                   showTeams(con);
               }

               if (userInput.equalsIgnoreCase("player")) {
                   showPlayers(con);
               }
               break;
        }
        dropTables(con);
    } // end main


    // Setup db
    public static void setupDB(Connection con) throws Exception{
        Statement st = con.createStatement();
        st.executeQuery("create table ling_team (teamid number NOT NULL, teamname varchar2(30) NOT NULL, CONSTRAINT pk_team PRIMARY KEY(teamid))");
        st.executeQuery("create table ling_player (playerid number NOT NULL, fname varchar2(20) NOT NULL, lname varchar2(20) NOT NULL, position varchar2(20), captainrole varchar2(1), dob date, salary number, teamid number NOT NULL, CONSTRAINT pk_player PRIMARY KEY(playerid, teamid), CONSTRAINT fk_team FOREIGN KEY (teamid) REFERENCES ling_team(teamid))");
        st.executeQuery("insert into ling_team values (100, 'Sharks')");
        st.executeQuery("insert into ling_player values (39, 'Logan', 'Couture', 'Center', 'C', to_date('1989-03-28', 'YYYY-MM-DD'), 2000000, 100)");
    }
    // Show tables
    public static void showTables(Connection con) throws Exception{
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select substr(table_name,6) from user_tables where lower(table_name) like 'ling%'");
        System.out.println("TABLE_NAME");

        while (rs.next()) {
            int numColumns = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= numColumns; i++) {
                String value = rs.getString(i);

                if (value != null) {
                    System.out.print("| " + value + "\t");
                }
            }

            System.out.print("\n");
        }
        // newline and clear buffer
        System.out.println("\n");
        rs.close();
        st.close();
    }
    
    // Drop tables
    public static void dropTables(Connection con) throws Exception
    {
        Statement st =con.createStatement();
        st.executeQuery("drop table ling_player cascade constraints");
        st.executeQuery("drop table ling_team cascade constraints");
    }
    
    public static void showPlayers(Connection con) throws Exception
    {
        Statement st = con.createStatement();
        st.executeQuery("select * from ling_player");
    }
   public static void showTeams(Connection con) throws Exception
    {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select * from ling_team");
        
        System.out.println("This is not working");
    }
    
    // Insert
    public static void showInsertMenu(Connection con)
    {
    }
    
    // Update
    public static void showUpdateMenu(Connection con)
    {
    }
    
    // Delete
    public static void showDeleteMenu(Connection con)
    {
    }
    
    // View
    public static void showViewMenu(Connection con)
    {
    }


} // end program