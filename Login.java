import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Login {
    private String username;
    private String password;
    private static ArrayList<String > usernames; // contains all the usernames
    private static ArrayList<String > passwords; // contains all the passwords
    private static Object gatekeeper = new Object(); // so that multiple threads do not update the same static fields and face race conditions

    // this constructor creates an object at the time when you create a new account and saves the details to UserDetails.txt file
    public Login(String username, String password) {
        this.username = username;
        this.password = password;
        if (!usernames.contains(username)) {
            synchronized (gatekeeper) {
                usernames.add(username);
                passwords.add(password);
            }
            synchronized (gatekeeper) {
                try {
                    PrintWriter pw = new PrintWriter(new FileOutputStream("UserDetails.txt", true));
                    pw.printf(username + " " + password + "\n");
                    pw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // this constructor creates an object at the time when user logs in

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static ArrayList<String> getUsernames() {
        return usernames;
    }

    public static void setUsernames(ArrayList<String> usernames) {
        synchronized (gatekeeper) {
            Login.usernames = usernames;
        }
    }

    public static ArrayList<String> getPasswords() {
        return passwords;
    }

    public static void setPasswords(ArrayList<String> passwords) {
        synchronized (gatekeeper) {
            Login.passwords = passwords;
        }
    }

    // this method checks if there exists any user details file and reads it, if a user details file does not exist then creates one.
    private static void readUserDetails() throws IOException {
        File userDetails = new File("UserDetails.txt");
        if(userDetails.createNewFile()){

        } else{
            BufferedReader bfr = new BufferedReader(new FileReader(userDetails));
            usernames = new ArrayList<>();
            passwords = new ArrayList<>();
            String[] lines;
            String line = bfr.readLine();
            while (line != null) {
                lines = line.split(" ");
                synchronized (gatekeeper) {
                    usernames.add(lines[0]);
                    passwords.add(lines[1]);
                }
                line = bfr.readLine();
            }
            bfr.close();
        }
    }



    // this method handles the server side processing for the GUI with continue, edit account, delete account ,and return to main menu button displayed by client
    public static void showAccountOptionsDialog(Login login, ObjectInputStream ois, ObjectOutputStream oos, BufferedReader bfr, PrintWriter pw, ArrayList<PrintWriter> printWriterArrayList) throws IOException, ClassNotFoundException {
        String buttonCommand;



        System.out.println("in continue button");
        DiningCourtList.diningCourtList(ois, bfr, printWriterArrayList, login, pw, oos);





    }

    // this method handles most of the login and create account stuff in the server
    public static Login loginOrCreateAccountServer(PrintWriter pw, BufferedReader bfr, ObjectOutputStream oos, ObjectInputStream ois, ArrayList<PrintWriter> printWriterArrayList) throws IOException, ClassNotFoundException, EOFException {
        System.out.println("inside loginOrCreateAccountServer method");

        Login login = null; // Login object to be returned
        // checks if there exists any user details file and reads it, if a user details file does not exist then creates one.
        readUserDetails();
        // receives what option the user selected from client

        JButton loginOrCreateAccountButton = (JButton) ois.readObject(); // reads the button selected(login or create account)

        String loginOrCreateAccountButtonCommand = loginOrCreateAccountButton.getActionCommand(); // stores the action command of the button

        // if the user selects create new account option a new account is created
        if (loginOrCreateAccountButtonCommand.equals("Create a new account")) {
            oos.writeObject(usernames);// writes usernames array list to client
            oos.flush();

            String usernameInput = bfr.readLine(); // reads username input from client
            String passwordInput = bfr.readLine(); // reads password input from client
            login = new Login(usernameInput, passwordInput); // Login object created using Login constructor

            // Client shows the edit, delete, continue, return to main menu options
            showAccountOptionsDialog(login, ois, oos, bfr, pw, printWriterArrayList);


            return login;

        }
        // if the user selects login option, the user details are verified
        else if (loginOrCreateAccountButtonCommand.equals("Login")) {

            boolean detailsVerified;
            do {

                oos.writeObject(usernames);
                oos.flush();

                oos.writeObject(passwords);
                oos.flush();

                detailsVerified = (boolean) ois.readObject();
            } while (!detailsVerified);

            String input = bfr.readLine();// contains username entered while logging in
            String input2 = bfr.readLine();// contains password entered while logging in
            // the line below creates a Login object
            login = new Login(input, input2);

            showAccountOptionsDialog(login, ois, oos, bfr, pw, printWriterArrayList); // shows the edit, delete, continue, return to main menu options

            return login;

        }

        return null;

    }

    // this method is a proxy method and is called in the run method of server
    public static Login loginServer (PrintWriter pw, BufferedReader bfr, ObjectOutputStream oos, ObjectInputStream ois, ArrayList<PrintWriter> printWriterArrayList) throws IOException, ClassNotFoundException {
        System.out.println("entered login server method");
        Login login = null;
        do {
            try {
                login = Login.loginOrCreateAccountServer(pw, bfr, oos, ois, printWriterArrayList);
            } catch (EOFException eofException){
                eofException.printStackTrace();
                break;
            } catch (IndexOutOfBoundsException indexOutOfBoundsException){
                indexOutOfBoundsException.printStackTrace();
                break;
            }


        } while (login.username == null);
        return login;
    }

}



