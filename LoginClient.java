import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class LoginClient {
    private static PrintWriter pw;
    private static BufferedReader bfr;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static JFrame frame;
    private static JPanel panel1;
    private static JPanel panel2;

    public LoginClient(JFrame frame, PrintWriter pw, BufferedReader bfr, ObjectOutputStream oos, ObjectInputStream ois) {
        this.frame = frame;
        this.pw = pw;
        this.bfr = bfr;
        this.oos = oos;
        this.ois = ois;
    }

    public static void loginOrCreateAccountClient(LoginClient loginClient){
        createAccountLogin(loginClient);
    }

    private static void createAccountLogin(LoginClient loginClient){
        panel2 = new JPanel();
        JLabel welcomeMessage = new JLabel("Welcome to the Application");
        JButton createAccountButton = new JButton("Create a new account");
        JButton loginButton = new JButton("Login");
        createAccountButton.setActionCommand("Create a new account");
        loginButton.setActionCommand("Login");
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    oos.writeObject(createAccountButton);
                    oos.flush();
                    showCreateAccountDialog(loginClient, panel2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        loginButton.addActionListener(new ActionListener() { // listens for login button
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    oos.writeObject(loginButton);
                    oos.flush();
                    showLoginDialog(loginClient, panel2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        panel2.add(welcomeMessage); // adds welcome message to panel
        panel2.add(createAccountButton); // adds create account button to panel
        panel2.add(loginButton); // adds login button to panel
        frame.getContentPane().removeAll();
        frame.repaint(); // so that JFrame does not freeze
        frame.add(panel2); // adds panel to the frame
        frame.setVisible(true);
    }

    private static void showCreateAccountDialog(LoginClient loginClient, JPanel panel2){
        panel1 = new JPanel(); // this JPanel will contain all things to be displayed by the method
        panel1.setSize(100, 600);
        JLabel usernamePrompt = new JLabel("Please enter a username");
        JLabel passwordPrompt = new JLabel("Please enter a password");
        JTextField usernameText = new JTextField(10); // text field to enter username
        JTextField passwordText = new JTextField(10); // text field to enter password
        JButton continueButton = new JButton("continue");
        continueButton.setActionCommand("continue"); // setting action command for communication with server
        continueButton.addActionListener(new ActionListener() { // listens for continue button
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = ""; // this local variable store username


                try {
                    username = isUsernameValid(usernameText); // checking if username is valid
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                String password = isPasswordValid(passwordText); // stores password

                writeUsernamePassword(username, password); // sends the username, password, role to server while creating account
                DiningCourtListClient.displayDiningCourtGui(frame, oos, bfr, pw, panel2, loginClient, ois);



            }
        });
        panel1.add(usernamePrompt); // add enter username label to panel
        panel1.add(usernameText); // add username text field to panel
        panel1.add(passwordPrompt); // add enter password label to panel
        panel1.add(passwordText); // add password text field to panel
        panel1.add(continueButton); // add continue button to panel
        frame.getContentPane().removeAll(); // removes whatever was there previously
        frame.repaint(); // so that JFrame does not freeze
        frame.add(panel1); // adding the JPanel created
        frame.setVisible(true); // displaying the changes
    }
    public static String isUsernameValid(JTextField usernameText) throws IOException, ClassNotFoundException {
        String input;

        ArrayList<String> usernames = (ArrayList<String>) ois.readObject(); // reads the String[] which contains list of courses from server


        do {

            input = usernameText.getText(); // gets input fromm text field
            if (input.isEmpty()) { // shows error message dialog if username is empty
                JOptionPane.showMessageDialog(null, "username cannot be empty", "Username", JOptionPane.ERROR_MESSAGE);
            } else if (input.contains(" ")) { // shows error message if username contains blank space
                JOptionPane.showMessageDialog(null, "Username should not contain blank spaces !", "Username", JOptionPane.ERROR_MESSAGE);
            } else if (usernames.contains(input)) { // shows error message is username is already in use
                JOptionPane.showMessageDialog(null, "Please enter a different username. This username is already in use", "Username", JOptionPane.ERROR_MESSAGE);
            }
        } while (input.isEmpty() || input.contains(" ") || usernames.contains(input)); // loops back and asks for username again when error message is thrown


        return input;

    }
    // checks if password is valid and displays error if invalid
    public static String isPasswordValid(JTextField passwordText) {

        String input2; // this variable stores input for password while creating new account
        do {
            // takes the input for password
            input2 = passwordText.getText();
            // If user clicks on close button, then program closes with thank you message.

            if (input2.isEmpty()) { // shows error message dialog if password is empty
                JOptionPane.showMessageDialog(null, "password cannot be empty", "Password", JOptionPane.ERROR_MESSAGE);
            } else if (input2.contains(" ")) { // shows error message if password contains blank space
                JOptionPane.showMessageDialog(null, "Password should not contain blank spaces !", "Password", JOptionPane.ERROR_MESSAGE);
            }
        } while (input2.isEmpty() || input2.contains(" ")); // loops back and asks for password again when error message is thrown
        return input2;
    }

    // writes username, password to server while creating a new account
    public static void writeUsernamePassword(String username, String password) {
        pw.println(username);
        pw.flush();
        pw.println(password);
        pw.flush();
    }

    // shows the dialog for login
    public static void showLoginDialog(LoginClient loginClient, JPanel panel2){
        panel1 = new JPanel(); // this JPanel will contain all things to be displayed by the method
        panel1.setSize(100, 600);
        JLabel usernamePrompt = new JLabel("Please enter your username");
        JLabel passwordPrompt = new JLabel("Please enter your password");
        JTextField usernameText = new JTextField(10); // text field to enter username
        JTextField passwordText = new JTextField(10); // text field to enter password
        JButton loginButton = new JButton("Login");
        loginButton.setActionCommand("Login"); // setting action command for communication with server

        loginButton.addActionListener(new ActionListener() { // listens for login button
            @Override
            public void actionPerformed(ActionEvent e) {

                // the code below checks to see whether an account with the provided login details exists
                ArrayList<String> usernames = new ArrayList<>();
                ArrayList<String> passwords = new ArrayList<>();

                try {
                    usernames = (ArrayList<String>) ois.readObject(); // reads the ArrayList<String> which contains list of usernames from server

                    passwords = (ArrayList<String>) ois.readObject(); // reads the ArrayList<String> which contains list of passwords from server
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                String usernameInput;

                String passwordInput;

                boolean detailsVerified = false; // tells if user details have been verified

                usernameInput = usernameText.getText(); // gets the username input
                passwordInput = passwordText.getText(); // gets the password input
                try {
                    detailsVerified = verifyLoginDetails(usernameInput, passwordInput, usernames, passwords);
                    oos.writeObject(detailsVerified);
                    oos.flush();

                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }


                if(detailsVerified) {
                    writeUsernamePasswordLogin(usernameInput, passwordInput);
                    System.out.println("login done, going to start messaging");
                    DiningCourtListClient.displayDiningCourtGui(frame, oos, bfr, pw, panel2, loginClient, ois);


                }


            }
        });
        panel1.add(usernamePrompt); // add enter username label to panel
        panel1.add(usernameText); // add username text field to panel
        panel1.add(passwordPrompt); // add enter password label to panel
        panel1.add(passwordText); // add password text field to panel
        panel1.add(loginButton); // add login button to panel
        frame.getContentPane().removeAll(); // removes whatever was there previously
        frame.repaint(); // so that JFrame does not freeze
        frame.add(panel1); // adding the JPanel created
        frame.setVisible(true); // displaying the changes
    }

    // the method below checks to see whether an account with the provided login details exists
    private static boolean verifyLoginDetails(String input, String input2, ArrayList<String> usernames, ArrayList<String> passwords) throws IOException, ClassNotFoundException {

        if (usernames.contains(input)) {
            int indexOfUsername = usernames.indexOf(input);
            if (passwords.get(indexOfUsername).equals(input2)) {
                return true;
            }
        } else {
            // shows error message if login details are not verified
            JOptionPane.showMessageDialog(null, "Please try again", "Login", JOptionPane.ERROR_MESSAGE);


        }
        return false;
    }


    // writes login info to server
    public static void writeUsernamePasswordLogin(String username, String password) {
        pw.println(username);
        pw.flush();
        pw.println(password);
        pw.flush();
    }



}
