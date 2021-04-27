package Project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
        //Member variables
        Scanner scan;
        Socket socket;
        DataInputStream othertReadSource;
        DataOutputStream otherWriteSource;
        InetAddress ip;
        String str;
        int port;

        //Constructor
        Client(int port) {
                try {
                        this.ip = InetAddress.getByName("localhost");
                        this.port = port;
                        //Connect to server
                        socket = new Socket(ip, port);
                        othertReadSource = new DataInputStream(socket.getInputStream());
                        otherWriteSource = new DataOutputStream(socket.getOutputStream());
                        scan = new Scanner(System.in);
                        String str = "";
                        System.out.println(othertReadSource.readUTF());
                        System.out.println(othertReadSource.readUTF());
                        str = scan.nextLine();
                        otherWriteSource.writeUTF(str);//Register or Login or Quit.

                        //Register is chosen
                        if (str.equalsIgnoreCase("Register")) {
                                System.out.println(othertReadSource.readUTF());
                                str = scan.nextLine();
                                otherWriteSource.writeUTF(str);//email
                                str = scan.nextLine();
                                otherWriteSource.writeUTF(str);//password
                                System.out.println(othertReadSource.readUTF());//Hello
                                System.out.println(othertReadSource.readUTF());//250 Hello
                                while (true) {
                                        //Sending mails
                                        sendEmail();
                                }

                        } else if (str.equalsIgnoreCase("Login")) {
                                while (true) {
                                        System.out.println(othertReadSource.readUTF());
                                        str = scan.nextLine();
                                        otherWriteSource.writeUTF(str);
                                        System.out.println(othertReadSource.readUTF());
                                        str = scan.nextLine();
                                        otherWriteSource.writeUTF(str);
                                        String logged = othertReadSource.readUTF();//Login Successful or not
                                        if (logged.equalsIgnoreCase("Login Successful")) {
                                                System.out.println(logged);
                                                while (true) {
                                                        //Sending mails
                                                        sendEmail();
                                                }
                                        }else{
                                                System.out.println(logged);
                                        }
                                }
                        } else {
                                System.out.println(othertReadSource.readUTF());
                                scan.close();
                                otherWriteSource.close();
                                othertReadSource.close();
                                socket.close();
                        }

                } catch (UnknownHostException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

        public void sendEmail() {
                try {
                        System.out.println(othertReadSource.readUTF());//Send or Quit
                        str = scan.nextLine();
                        if (str.equalsIgnoreCase("Send")) {
                                otherWriteSource.writeUTF(str);
                                System.out.println(othertReadSource.readUTF());
                                str = scan.nextLine();
                                otherWriteSource.writeUTF(str); //From
                                System.out.println(othertReadSource.readUTF());
                                str = scan.nextLine();
                                otherWriteSource.writeUTF(str); //To
                                System.out.println(othertReadSource.readUTF());
                                System.out.println(othertReadSource.readUTF());
                                System.out.println(othertReadSource.readUTF());//Please enter the body of your email
                                //Message till the input is &&&
                                while (true) {
                                        str = scan.nextLine();
                                        if (str.equals("&&&")) {
                                                otherWriteSource.writeUTF(str);
                                                System.out.println(othertReadSource.readUTF());
                                                break;
                                        } else {
                                                otherWriteSource.writeUTF(str);
                                        }
                                }

                        } else if (str.equalsIgnoreCase("Quit")) {
                                otherWriteSource.writeUTF(str);
                                System.out.println(othertReadSource.readUTF());
                                scan.close();
                                otherWriteSource.close();
                                othertReadSource.close();
                                socket.close();
                                System.exit(0);
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

        public static void main(String[] args) {
                Scanner in = new Scanner(System.in);
                System.out.println("Please enter server port number");
                int ort = in.nextInt();
                //Object
                Client client = new Client(ort); //Constructor

        }
}


