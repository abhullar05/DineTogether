import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client {
    private static JFrame frame;
    private static String hostName;


    public static void main(String[] args) throws IOException {
        hostName = JOptionPane.showInputDialog(null, "Please enter the host name.", JOptionPane.QUESTION_MESSAGE);
        if (hostName == null) {
            JOptionPane.showMessageDialog(null, "Thanks for using the application");
            return;
        }

        String portNumberString = JOptionPane.showInputDialog(null, "Please enter the port number", "DineTogether", JOptionPane.QUESTION_MESSAGE);
        if (portNumberString == null) {
            JOptionPane.showMessageDialog(null, "Thanks for using the application.");
            return;
        }
        Socket socket = new Socket();
        try {
            int portNumber = Integer.parseInt(portNumberString);
            socket = new Socket(hostName, portNumber);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error establishing connection", "Application", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Connection established successfully", "Application", JOptionPane.PLAIN_MESSAGE);

        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame();
                frame.setLocationRelativeTo(null);
                frame.setSize(400, 600);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        pw.close();
                        try {
                            bfr.close();
                            ois.close();
                            oos.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        frame.dispose();

                    }
                });
                createGUI(pw, bfr, oos, ois);
            }
        });


    }
    private static void createGUI(PrintWriter pw, BufferedReader bfr , ObjectOutputStream oos, ObjectInputStream ois){
        LoginClient loginClient = new LoginClient(frame, pw, bfr, oos, ois);
        loginClient.loginOrCreateAccountClient(loginClient); // calls loginOrCreateAccountClient to handle login related GUIs


    }
}



