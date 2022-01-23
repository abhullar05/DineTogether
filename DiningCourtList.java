import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class DiningCourtList {
    private static ObjectInputStream objectInputStream;
    private static BufferedReader bufferedReader;
    private static ArrayList<PrintWriter> printWriters;
    private static Login loginObject;
    private static PrintWriter printWriter;
    private static ObjectOutputStream objectOutputStream;
    public static void diningCourtList(ObjectInputStream ois, BufferedReader bfr, ArrayList<PrintWriter> printWriterArrayList, Login login, PrintWriter pw, ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        objectInputStream = ois;
        bufferedReader = bfr;
        printWriters = printWriterArrayList;
        loginObject = login;
        printWriter = pw;
        objectOutputStream = oos;
        System.out.println("entered diningCourtList method");
        JButton button = (JButton) ois.readObject();
        String buttonCommand = button.getActionCommand();
        if(buttonCommand.equals("windsor")){
            System.out.println("entered windsor chat method");
            System.out.println(login.getUsername());
            pw.println(login.getUsername());
            pw.flush();
            windsorChat();
        }
    }

    private static void windsorChat() throws IOException, ClassNotFoundException {
        // write the username of client


        System.out.println("entered windsor chat method");

        String sendButtonCommand;
        do {
            System.out.println("entered do while loop");
            printWriter.println(printWriters.size());
            printWriter.flush();

            JButton sendButtonReceived = (JButton) objectInputStream.readObject();
            System.out.println(sendButtonReceived);
            sendButtonCommand = sendButtonReceived.getActionCommand();

            System.out.println(sendButtonCommand);
            if (sendButtonCommand.equals("send")) {
                String lineRead = bufferedReader.readLine();
                System.out.println(lineRead);
                System.out.println(printWriters.size());

                for (int i = 0; i < printWriters.size(); i++) {
                    PrintWriter printWriter = printWriters.get(i);
                    printWriter.println(lineRead);
                    printWriter.flush();
                    // printWriter.close();
                }
                // windsorChat(bfr, ois, printWriterArrayList, login, pw, oos);


            } else if (sendButtonCommand.equals("logout")) {
                Login.loginServer(printWriter, bufferedReader, objectOutputStream, objectInputStream, printWriters);
            }
        } while (sendButtonCommand.equals("send"));

    }


}
