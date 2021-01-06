// Time-stamp: <2020-11-29 20:00:03 user>
// Assignment 2 - Iterative Socket Server
// Team 18 ScottAndersonn, Justin Odeln, Michael St Juste

import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.*;
import java.util.*;


public class ThreadedSocketServer {
  public static void main(String[] args) {
    ThreadedSocketServer myServer = new ThreadedSocketServer();
    String hostname;
    int port;

    if (args.length < 2) {
      Scanner in = new Scanner(System.in);
      System.out.println("Server address: ");
      hostname = in.nextLine();
      System.out.println("port: ");
      port = in.nextInt();
    } else {
      hostname = args[0];
      port = Integer.parseInt(args[1]);
    }

    try {
      ServerSocket serverSocket = new ServerSocket(port, 25, InetAddress.getByName(hostname));
      System.out.println("Server is listening on port " + port);
      String text;
      do {
        Socket socket = serverSocket.accept();
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        text = reader.readLine();
        SimpleDateFormat formatter = new SimpleDateFormat("YY:MM:dd HH:mm:ss:SS");
        System.out.println(formatter.format(new Date()) + " New client connected, command " + text);
        String command = "";
        String arg = "";

        if (text.equals("q")) {
          socket.close();
          return;
        }
        if (text.equals("u")) {
          command = "uptime";
          arg = "-p";
        }
        if (text.equals("d")) {
          command = "date";
          arg = "-Iseconds";
        }
        if (text.equals("p")) {
          command = "ps";
          arg = "-a";
        }
        if (text.equals("f")) command = "free";
        if (text.equals("w")) {
          command = "w";
          arg = "-o";
        }
        if (text.equals("n")) command = "netstat";

        if (command.equals("")) {
          System.out.println("error no command");
          socket.close();
          return;
        }
        System.out.println("command " + command + " " + arg);
        String results = null;
        try {
          Process p = new ProcessBuilder(command, arg).start();
          BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

          BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

          System.out.println("Here is the standard output of the command:");

          while ((results = stdInput.readLine()) != null) {
            System.out.println(results);
            writer.println(results);
          }

          results = stdError.readLine();
          if (results != null) {
            System.out.println("Here is the standard error of the command (if any):\n");
            System.out.println(results);
            while ((results = stdError.readLine()) != null) {
              System.out.println(results);
            }
          }
        } catch (IOException exception) {
          System.out.println("exception happened - here's what I know: ");
          exception.printStackTrace();
          System.exit(-1);
        }
        socket.close();
      } while (text != "q");
      return;
    } catch (Exception e) {
      SOP.print(e.getMessage());
    }
    return;
  }
}
