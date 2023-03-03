package main.java.analyser;

import main.java.utils.Constants;
import main.java.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class CVC4Lib {
   private static String CVC4 = Constants.LIB_PATH + Utils.configFile.getProperty("cvcFor" + Constants.osName);
   public static boolean solve(String usecaseName, String smtlib, String conjecture, String filename, String... variantAssertion) {
      String all = smtlib;
      //insert assertions about the arguments
      if(variantAssertion.length != 0){
         for(String vAssertion: variantAssertion){
            if(vAssertion.length() != 0){
               all += ("ASSERT " + vAssertion + ";\n");
            }
         }
      }

      all += "ASSERT NOT" + conjecture + "\n";
      String outfolder = Constants.TMP_PATH + usecaseName + System.getProperty("file.separator");
      String outfile = outfolder+filename+".cvc4";
      File f1 = new File(outfolder);
      if(!f1.exists())
         f1.mkdirs();
      File f = new File(outfile);
      try {
         FileWriter fileWriter = new FileWriter(f);
         fileWriter.write(all);
         fileWriter.write("CHECKSAT;");
         fileWriter.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
      String command = CVC4 + " --lang cvc4 " + f.getAbsolutePath();
      String output = "";
      try {
         Process child = Runtime.getRuntime().exec(command);
         InputStream in = child.getInputStream();
         Scanner scanner = new Scanner(in);
         while (scanner.hasNext()) {
            output += scanner.nextLine();
         }
         in.close();
      }
      catch (IOException e)
      {
         System.err.println("cannot run cvc4 exe file...");
      }
      return output.equals("unsat");
   }
}


