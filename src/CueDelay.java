import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CueDelay {
  private static BufferedReader inputFile = null;
  private static BufferedWriter outputFile = null;
  private static String line = "";
  private static String editedLine = "";
  private static int delay = 0;
  private static boolean minus = false;

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        System.out.println("Add meg a file-t!");
      } else if (args.length == 1) {
        delay = 1;
        fileReader(args[0]);
      } else if (args.length == 2) {
        if (args[1].indexOf("-") > -1) {
          delay = Integer.valueOf(args[1].substring(1)).intValue();
          minus = true;
        } else if (args[1].indexOf("+") > -1) { 
          delay = Integer.valueOf(args[1].substring(1)).intValue();
        } else 
          delay = Integer.valueOf(args[1]).intValue();
        fileReader(args[0]);
      } else
        System.out.println("Sok paraméter!");
    } catch (Exception e) {
      System.out.println("Hiba: " + e);
    }
  }

  private static void fileReader(String name) {
    try {
      inputFile = new BufferedReader(new FileReader(new File(name)));
    } catch (FileNotFoundException e) {
      System.out.println("Nincs meg a file!");
    }

    try {
      outputFile = new BufferedWriter(new FileWriter(new File(name + ".2")));
    } catch (IOException ie) {
      System.out.println("File létrehozása nem sikerült!");
    }

    while (line != null) {
      try {
        line = inputFile.readLine();
        if (line != null) {
          editedLine = lineCutter(line);
          outputFile.write(editedLine, 0, editedLine.length());
          outputFile.newLine();
        }
      } catch (IOException ioe) {
        System.out.println("Hiba a file írása közben!");
      }
    }

    try {
      inputFile.close();
      outputFile.close();
    } catch (IOException ioe) {
      System.out.println("Hiba a file-ok bezárásánál!");
    }
  }

  private static String lineCutter(String l) {
    try {

      int ii = l.indexOf("INDEX 01");
      if (ii == -1)
        return l;

      String[] t = l.substring(ii + 9, l.length()).split(":");

      // ignore first time stamp
      if ("00".equals(t[0]) && "00".equals(t[1]) && "00".equals(t[2]))
        return l;

      int newsec = (Integer.valueOf(t[1]).intValue()) + (minus ? -1*delay : delay); // second
      if (newsec > 59) { // increase minute
        newsec -= 60;
        int newmin = (Integer.valueOf(t[0]).intValue()) + 1;
        t[0] = new Integer(newmin).toString();
      }
      if (newsec < 0) { // decrease minute
        if (Integer.valueOf(t[0]).intValue() == 0)  // avoid minus
          newsec = 0;
        else {
          newsec += 60;
          int newmin = (Integer.valueOf(t[0]).intValue()) - 1;
          t[0] = newmin < 0 ? "00" : new Integer(newmin).toString();
        }
      }
      t[1] = new Integer(newsec).toString();

      if (t[0].length() == 1)
        t[0] = "0"+t[0];
      if (t[1].length() == 1)
        t[1] = "0"+t[1];

      return l.substring(0, ii + 9) + t[0] + ":" + t[1] + ":" + t[2];
    } catch (Exception ex) {
      System.out.println("Hiba: " + ex);
      return l;
    }
  }

}
