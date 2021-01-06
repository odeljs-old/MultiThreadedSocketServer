// Time-stamp:   <2020-11-24T10:19:12 /home/user/unf/cnt4504/ThreadedSocketClient.java user@arwen>
// Create:       <2020-09-09T11:13:45 /home/user/unf/cnt4504/ThreadedSocketClient.java user@arwen>

// Threaded Socket Client
// Team 18 Scott Anderson, Justin Odeln, Michael St Juste

// The most straightforward way to handle the time calculations is to start the clock before the first time you reach out to the server from the client, and stop it after the last result comes in.  That gives you the total time, and then simply divide by the number of iterations to give you the average time per thread.

import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Scanner;
//import java.time.Duration;
//import java.time.Instant;

class ThreadedSocket extends Thread {
  long start;
  long elapsed;
  String hostname;
  int port;
  String command;
  int i;

  ThreadedSocket(String chostname, int cport, int ci, String ccommand) {
    super("thread " + ci + " ");
    start();
    hostname = chostname;
    port = cport;
    command = ccommand;
    i = ci;
  }

  public void run() {
    try (Socket socket = new Socket(this.hostname, this.port)) {
      OutputStream output = socket.getOutputStream();
      PrintWriter writer = new PrintWriter(output, true);
      writer.println(this.command);
      InputStream input = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));

      String s = null;
      while ((s = reader.readLine()) != null) {
        System.out.println(s);
      }
      socket.close();
    } catch (UnknownHostException ex) {
      System.out.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
      System.out.println("I/O error: " + ex.getMessage());
    }
  }
}

class ThreadedSocketClient {
  public static void main(String args[]) {

    System.out.println("Assignment 1 - Iterative Socket Server - Client");

    String hostname;
    int port;
    String command;
    int n;
    boolean verboseFlag = false;

    // normal operation is through prompts.
    if (args.length < 3) {

      Scanner in = new Scanner(System.in);
      System.out.println("Server address: ");
      hostname = in.nextLine();
      System.out.println("port: ");
      port = in.nextInt();
      // Read the leftover new line
      in.nextLine();
      System.out.println("q for quit");
      System.out.println("u for uptime");
      System.out.println("d for date");
      System.out.println("p for ps");
      System.out.println("f for free");
      System.out.println("w for w");
      System.out.println("n for netstat");
      System.out.println("command: ");
      command = in.nextLine();
      System.out.println("iterations: ");
      n = in.nextInt();
    } else {
      // For testing purposes, command line args work.
      hostname = args[0];
      port = Integer.parseInt(args[1]);
      n = Integer.parseInt(args[2]);
      command = args[3];
    }

    ThreadedSocket[] arr;
    arr = new ThreadedSocket[n];

    int active = n;
    long startTime = System.nanoTime();

    for (int i = 0; i < n; i++) {
      arr[i] = new ThreadedSocket(hostname, port, i, command);
    }

    while (active > 0) {

      active = 0;
      for (int i = 0; i < n; i++) {
        if (arr[i].isAlive()) active++;
      }
    }
    long timeElapsed = (System.nanoTime() - startTime) / 1000000;
    System.out.println("Total Turn-around Time: " + timeElapsed);
    double timeAvg = timeElapsed / (double) n;
    System.out.format("Average Turn-around Time: %-10.3f%n", timeAvg);
  }
}
