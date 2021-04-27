package Project;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.Stream;

public class Server {
    //Member variables
    public String name;
    String anoName;
    int port;
    static ServerSocket serverSocket;
    DataInputStream clientReadSource;
    DataOutputStream clientWriteSource;
    String location = System.getProperty("user.dir");

    //Constructor
    Server(String name, int port) {
        try {

            this.name = name;
            anoName = name.substring(0, name.length() - 4);
            this.port = port;
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            System.out.println("The port is used");
        }
    }

    //Function of accepting connection with server
    private void accCommunication() {
        System.out.println(anoName + " server with port " + port + " is booted up");
        //Function of creating servers' main folder and credentials file...
        auto();

        Socket socket = null;
        try {
            while (true) {
                //accept any connection
                socket = serverSocket.accept();
                //Threads
                ClientConnection clientConnection = new ClientConnection(socket, anoName, name);
                clientConnection.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Function of creating servers' main folder and credentials file...
    private void auto() {
        try {
            //Creating folder
            //Gets the file path
            Path path = Paths.get(location + "\\" + anoName);
            if (!Files.isDirectory(path)) {
                //Creating file
                File file = new File(location + "\\" + anoName);
                file.mkdirs();
                file = new File(location + "\\" + anoName + "\\credentials.txt");
                file.createNewFile();
                file = new File(location + "\\" + anoName + "\\port.txt");
                file.createNewFile();
                FileWriter w = new FileWriter(location + "\\" + anoName + "\\port.txt", true);
                w.write(String.format(String.valueOf(port)));
                w.write(System.lineSeparator());
                w.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public static void main(String[] args) {
        //Get  name and port
        Scanner inl = new Scanner(System.in);
        System.out.println("Please Enter the server name and port number");
        String ame = inl.nextLine();
        int ort = inl.nextInt();
        //An object of server class
        Server sr = new Server(ame, ort);
        //Function of accepting connection with server
        sr.accCommunication();
    }

    //Class handles multi-client connection with same serve (threads)
    static class ClientConnection extends Thread {
        //Member variables
        Socket socket;
        String anoName;
        String name;
        DataInputStream clientReadSource;
        DataOutputStream clientWriteSource;
        String cName;
        String location = System.getProperty("user.dir");

        //Constructor
        ClientConnection(Socket socket, String anoName, String name) {
            this.socket = socket;
            this.anoName = anoName;
            this.name = name;
        }

        //built-in function of starting threads
        public void run() {//When we call it we use the word start()
            String str = "";

            try {
                //Variable that reads data from client
                clientReadSource = new DataInputStream(socket.getInputStream());
                //Variable that contains data to client
                clientWriteSource = new DataOutputStream(socket.getOutputStream());
                clientWriteSource.writeUTF("220 " + name);

                clientWriteSource.writeUTF("Please Choose 'REGISTER' or 'LOGIN' or 'QUIT'");
                str = clientReadSource.readUTF();
                //Function takes parameters of choose
                choose(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //Function takes parameters of choose
        private void choose(String str) {
            if (str.equalsIgnoreCase("register")) {
                //Function of register
                Register();
            } else if (str.equalsIgnoreCase("login")) {
                //Function of Login
                LogIn();
            } else if(str.equalsIgnoreCase("Send")){
                Send();
            }
            else{
                Quit();
            }
        }

        public void Register() {
            try {
                clientWriteSource.writeUTF("Please enter an email and password");
                //Variables that hold received data
                String email = clientReadSource.readUTF();
                String password = clientReadSource.readUTF();
                //Function saves user's data in the credential file
                dataSave(email, password);
                //Separating data
                StringTokenizer tokenizer = new StringTokenizer(email, "@");
                cName = tokenizer.nextToken().trim();
                //Function creates user's folder and in-box, it takes user name as a parameter
                userInbox(cName);
                clientWriteSource.writeUTF("HELLO " + email);
                clientWriteSource.writeUTF("250 Hello " + cName + " pleased to meet you");
                while (true) {
                    clientWriteSource.writeUTF("Please choose Send or Quit");
                    String choose = clientReadSource.readUTF();
                    if (choose.equalsIgnoreCase("quit")) {
                        //Function of taking an action
                        chooseReg(choose);
                        break;
                    } else {
                        //Function of taking an action
                        chooseReg(choose);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //Function saves user's data in the credential file
        private void dataSave(String e, String p) {
            try {
                FileWriter w = new FileWriter(location + "\\" + anoName + "\\credentials.txt", true);
                w.write(String.format(e));
                w.write(System.lineSeparator());
                w.write(String.format(p));
                w.write(System.lineSeparator());

                w.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        //Function creates user's folder and in-box, it takes user name as a parameter
        public void userInbox(String cName) {
            File file = new File(location + "\\" + anoName + "\\" + cName);
            file.mkdirs();
            file = new File(location + "\\" + anoName + "\\" + cName + "\\Inbox.txt");//inbox file
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        //Function of taking an action
        private void chooseReg(String str) {
            if (str.compareToIgnoreCase("send") == 0) {
                //Text another client
                Send();
            } else {
                Quit();
            }
        }

        //Text another client
        public void Send() {
            try {
                //User Email
                clientWriteSource.writeUTF("MAIL FROM");
                String from = clientReadSource.readUTF();
                String senderName = from.substring(0, from.indexOf("@"));
                String senderExtension = from.substring(from.indexOf("@") + 1, from.length() - 4);

                //Receiver email
                clientWriteSource.writeUTF("RCPT TO");
                String to = clientReadSource.readUTF();
                String receiverName = to.substring(0, to.indexOf("@"));
                String receiverExtension = to.substring(to.indexOf("@") + 1, to.length() - 4);//to be updated
                clientWriteSource.writeUTF("250 " + to + "...Recipient OK");
                clientWriteSource.writeUTF("Data");
                clientWriteSource.writeUTF("Server: Please enter the body of your email ended by ‘&&&‘");
                //Save data in that array
                List<String> bodyOfEmail = new ArrayList<>();
                String messagePart;
                //Choosing whether the two clients are connected on the same server or different servers in order to send the Email in the right way
                if (anoName.equalsIgnoreCase(receiverExtension)) {
                    while (true) {
                        messagePart = clientReadSource.readUTF();
                        if (messagePart.equalsIgnoreCase("&&&")) {
                            clientWriteSource.writeUTF("250 Message accepted for Delivery");
                            break;
                        } else {
                            bodyOfEmail.add(messagePart);
                        }
                    }
                    for (String z : bodyOfEmail) {
                        String x = z;
                        //Save messages in another client's inbox
                        FileWriter w = new FileWriter(location + "\\" + receiverExtension + "\\" + receiverName + "\\Inbox.txt", true);
                        w.write(String.format(x));
                        w.write(System.lineSeparator());
                        w.close();
                    }
                    //texting through different server
                } else {//yahoo.com gmail.com
                    File portNumber = new File(location+"\\"+receiverExtension+"\\port.txt");
                    Scanner toRead = new Scanner(portNumber);
                    int ported = Integer.parseInt(toRead.nextLine());
                    InetAddress ip = InetAddress.getByName("localhost");
                    Socket toBeConnectedServerSocket = new Socket(ip, ported);
                    DataInputStream otherReadSource = new DataInputStream(toBeConnectedServerSocket.getInputStream());
                    DataOutputStream otherWriteSource = new DataOutputStream(toBeConnectedServerSocket.getOutputStream());
                    otherReadSource.readUTF();
                    otherReadSource.readUTF();

                    otherWriteSource.writeUTF("Send");
                    otherReadSource.readUTF();
                    otherWriteSource.writeUTF(from);
                    otherReadSource.readUTF();
                    otherWriteSource.writeUTF(to);
                    otherReadSource.readUTF();
                    otherReadSource.readUTF();
                    otherReadSource.readUTF();
                    while (true) {
                        messagePart = clientReadSource.readUTF();
                        if (messagePart.equalsIgnoreCase("&&&")) {
                            otherWriteSource.writeUTF(messagePart);
                            clientWriteSource.writeUTF("250 Message accepted for Delivery");
                            //otherReadSource.readUTF();
                            break;
                        } else {
                            otherWriteSource.writeUTF(messagePart);
                        }
                    }

                }
            } catch(IOException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        //LogIn function
        public void LogIn () {
            try {
                while(true) {
                    clientWriteSource.writeUTF("Please Enter Your Email");
                    String email = clientReadSource.readUTF();
                    clientWriteSource.writeUTF("Please Enter Your Password");
                    String password = clientReadSource.readUTF();
                    File myObj = new File(location + "\\" + anoName + "\\credentials.txt");
                    //Check that account is registered
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine()) {
                        String chEmail = myReader.nextLine();
                        String chPassword = myReader.nextLine();
                        if (chEmail.equalsIgnoreCase(email) && chPassword.equals(password)) {
                            clientWriteSource.writeUTF("Login Successful");
                            while (true) {
                                //Actions that can be taken after login
                                clientWriteSource.writeUTF("Please choose Send or Quit");
                                String decision = clientReadSource.readUTF();
                                if (decision.equalsIgnoreCase("quit")) {
                                    //Function of taking an action
                                    chooseReg(decision);
                                    break;
                                } else {
                                    chooseReg(decision);
                                }
                            }
                        }
                    }
                    clientWriteSource.writeUTF(("You have entered a wrong email or password"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void Quit () {
            try {
                System.out.println("Closing connection with the client");
                clientWriteSource.writeUTF("221 "+name+" closing connection");
                clientWriteSource.close();
                clientReadSource.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Closed");
            }
        }

    } //End of class "ClientConnection"
}
