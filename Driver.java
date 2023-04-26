import java.sql.*;
import java.util.Scanner;

class Driver {

    public static void main(String[] args) throws Exception {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection con = DriverManager.getConnection(
            "jdbc:oracle:thin:@localhost:1521:CSC134", "rickc", "Password123");

        Statement st = con.createStatement();
        
        try {
            
            st.executeQuery("alter table ling_player drop constraint fk_team");
            st.executeQuery("alter table ling_player drop constraint pk_player;");
            st.executeQuery("alter table ling_team drop constraint pk_team");
            st.executeQuery("drop table ling_player");
            st.executeQuery("drop table ling_team");
        } catch (SQLException s) {}
        
        st.executeQuery("create table ling_team (teamid number NOT NULL, teamname varchar2(30) NOT NULL, CONSTRAINT pk_team PRIMARY KEY(teamid))");
        st.executeQuery("create table ling_player (playerid number NOT NULL, fname varchar2(20) NOT NULL, lname varchar2(20) NOT NULL, position varchar2(20), captainrole varchar2(1), dob date, salary number, teamid number NOT NULL, CONSTRAINT pk_player PRIMARY KEY(playerid, teamid), CONSTRAINT fk_team FOREIGN KEY (teamid) REFERENCES ling_team(teamid))");
        st.executeQuery("insert into ling_team values (100, 'Sharks')");
        st.executeQuery("insert into ling_player values (39, 'Logan', 'Couture', 'Center', 'C', to_date('1989-03-28', 'YYYY-MM-DD'), 2000000, 100)");

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
                System.out.println();
                System.out.println("Insert Menu: \nFrom which table do you want to insert?");

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
                System.out.print("\n");
                scanner.nextLine();

                System.out.print("Type table_name: ");
                String userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase("team")) {
                    System.out.println("Do you know the team id #?: ");
                    userInput = scanner.nextLine();

                    if (userInput.equalsIgnoreCase("no") || userInput.equalsIgnoreCase("n")) {
                        System.out.println("Showing all teams...");
                        rs = st.executeQuery("select * from ling_team");

                        while (rs.next()) {
                            int numColumns = rs.getMetaData().getColumnCount();

                            for (int i = 1; i <= numColumns; i++) {
                                String value = rs.getString(i);

                                if (value != null) {
                                    System.out.print("| " + value + "\t");
                                }
                            }
                        }
                    }

                    System.out.print("\n");
                    System.out.println("If your team does not exist, enter a new team id #: ");
                    String userInputTeamID = scanner.nextLine();
                    System.out.print("\n");
                    System.out.println("What is the new team name?");
                    String userInputTeamName = scanner.nextLine();
                    st.executeQuery("insert into ling_team values(" + userInputTeamID + ", '" + userInputTeamName + "')");
                    rs = st.executeQuery("select * from ling_team");

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

                    System.out.print("\n");
                }

                if (userInput.equalsIgnoreCase("player")) {
                    System.out.println("Do you know the team id #?: ");
                    userInput = scanner.nextLine();

                    if (userInput.equalsIgnoreCase("no") || userInput.equalsIgnoreCase("n")) {
                        System.out.println("Showing all teams...");
                        rs = st.executeQuery("select * from ling_team");

                        while (rs.next()) {
                            int numColumns = rs.getMetaData().getColumnCount();

                            for (int i = 1; i <= numColumns; i++) {
                                String value = rs.getString(i);

                                if (value != null) {
                                    System.out.print("| " + value + "\t");
                                }
                            }
                        }
                    }

                    System.out.print("\n What is the team id #?: ");
                    String tid = scanner.nextLine();
                    System.out.print("\n What is the player jersey #?:");
                    String pid = scanner.nextLine();
                    System.out.print("\n What is the player first name?: ");
                    String pfname = scanner.nextLine();
                    System.out.print("\n What is the player last name?: ");
                    String plname = scanner.nextLine();
                    System.out.print("\n What is the player Captain role? Leave blank if none: ");
                    String prole = scanner.nextLine();
                    System.out.print("\n What is the player position?(F or D or G?: ");
                    String ppos = scanner.nextLine();
                    System.out.print("\n What is the player date of birth(YYYY-MM-DD)?: ");
                    String pdob = scanner.nextLine();
                    System.out.print("\n What is the player salary?: ");
                    double psalary = scanner.nextDouble();

                    st.executeQuery("insert into ling_player values(" + pid + ", '" + pfname + "', '" + plname + "', '" + prole + "', '" + ppos + "', to_date('" + pdob + "', 'YYYY-MM-DD'), " + psalary + ", "
                        + tid + ")");

                    rs = st.executeQuery("select * from ling_player where playerid=" + pid + " and teamid=" + tid);

                    while (rs.next()) {
                        int numColumns = rs.getMetaData().getColumnCount();

                        for (int i = 1; i <= numColumns; i++) {
                            String value = rs.getString(i);

                            if (value != null) {
                                System.out.print("| " + value + "\t");
                            }
                        }
                    }

                }

                break;
            case 2:
                System.out.println();
                System.out.println("Delete Menu: \nFrom which table do you want to delete?");

                rs = st.executeQuery("select substr(table_name,6) from user_tables where lower(table_name) like 'ling%'");
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
                System.out.print("\n");
                scanner.nextLine();

                System.out.print("Type table_name: ");
                userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase("team")) {
                    System.out.println("Okay team!");
                }

                if (userInput.equalsIgnoreCase("player")) {
                    System.out.println("Okay player.");
                }

                break;
            case 3:

                break;
            case 4:
                rs = st.executeQuery("select substr(table_name,6) from user_tables where lower(table_name) like 'ling%'");
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
                System.out.print("\n");
                scanner.nextLine();

                System.out.print("Type table_name: ");
                userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase("team")) {
                    rs = st.executeQuery("select * from ling_team");

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
                    System.out.print("\n");

                }

                if (userInput.equalsIgnoreCase("player")) {
                    rs = st.executeQuery("select * from ling_player");

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
                    System.out.print("\n");

                }

                break;
            case 5:

                break;
            default:
                System.out.println("Choose 1 to 5");
        }

        System.out.println();
        
        try {
            st.executeQuery("drop table ling_team cascade constraints");
            st.executeQuery("drop table ling_player cascade constraints");
        } catch (SQLException s) {}

    } // end main
    
    

} // end program