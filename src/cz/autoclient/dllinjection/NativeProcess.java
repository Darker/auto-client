/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.dllinjection;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 *
 * @author Jakub
 */
public class NativeProcess {
  public static String readWholeOutput(Process p) {
    BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
    //Read output
    String line;
    StringBuilder b = new StringBuilder();
    try {
      while ((line = is.readLine()) != null) {
        //This makes it possible to cancel waiting on hung processes
        /*if(Thread.interrupted()) {
          break; 
        }*/
        b.append(line);
        b.append('\n');
      }
    }
    catch(IOException e) {}
    return b.toString();
  }
  public static String readWholeOutput(final Process p, long timeout) {
    ProcessReader rdr = new ProcessReader(p);
    rdr.start();
    try {
      rdr.join(timeout);
      rdr.interrupt();
    }
    catch(InterruptedException e) {
      return null; 
    }
    if(rdr.hasTerminatedOk())
      return rdr.result;
    else 
      return null;
  }
  public static String readWholeOutput(final String p, long timeout) throws IOException {
    return readWholeOutput(Runtime.getRuntime().exec(p), timeout);
  }
  
  public static class ProcessReader extends Thread {
    public final Process process;
    public String result;
    private boolean terminatedOk = false;
    public ProcessReader(Process p) {
      process = p;
    }
    @Override
    public void run() {
      result = readWholeOutput(process);
      terminatedOk = true;
    }
    public boolean hasTerminatedOk() {
      return terminatedOk;
    }
  }
  
  public static int getProcessId(String name) {
    int[] all = getProcessIDs(name, true, true);
    if(all==null||all.length==0)
      return -1;
    return all[0];
  }
	/**
	 * Return list of native process IDs by name. 
	 * 
	 * @param name the process name to be found, not window title!
   * @param strict require exact match, rather than substring match
   * @param breakOnFirst Once a match is found, immediatelly return it, rather than parsing whole output
	 * @return array of PID - id of the processes, null on failure
	 */
	public static final int[] getProcessIDs(final String name, boolean strict, boolean breakOnFirst) {

		if (name == null) {
			throw new IllegalArgumentException("Process name cannot be null!");
		}
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Process name cannot be empty!");
		}

		Process process = null;
		try {
			process = Runtime.getRuntime().exec("tasklist /fo csv");
			process.getOutputStream().close();
		} catch (IOException e) {
      //System.err.println("Error running tasklist: "+e.getMessage());
			return null;
		}
    Reader is = new InputStreamReader(process.getInputStream());
    CSVReader csv = new CSVReader(is);
    String[] nextLine;
    ArrayList<Integer> matches = new ArrayList<>();
    try {
      //Skip the first line with headers
      csv.readNext();
      while ((nextLine = csv.readNext()) != null) {
        //In case something changed,  just check for the first two columns.
        //But in windows, result should be 5 columns, 3 other are not needed
        if(nextLine.length>=2) {
          //System.out.println("Checking process '"+nextLine[0]+"' with PID "+nextLine[1]);
          if(strict&&nextLine[0].equalsIgnoreCase(name) || !strict&&nextLine[0].contains(name)) {
            if(breakOnFirst) {
              return new int[] {Integer.parseInt(nextLine[1])};
            }
            matches.add(Integer.parseInt(nextLine[1]));
          }
        }
      }
    }
    catch(IOException e) {
      //do nothing and parse anything that has been found before the exception
        //System.err.println("Error with stream from tasklist: "+e.getMessage());
    }
		/*int[] pids = new int[found.size()];
		for (int i = 0; i < found.size(); i++) {
			pids[i] = Integer.parseInt(found.get(i));
		}

		return pids;*/
    if(matches.isEmpty())
      return null;
    //BECAUSE JAVA IS FUCKING RETARDED, ALLWAYS WAS AND ALLWAYS WILL BE
    //signed byte ftw
    int[] ret = new int[matches.size()];
    for(int i=0,l=ret.length; i<l; i++) {
      ret[i] = matches.get(i);
    }
    return ret;
	}
  
  public static int[] getProcessIDs(String name) {
    return getProcessIDs(name, true, false); 
  }
  public static int[] getProcessIDs(String name, boolean strict) {
    return getProcessIDs(name, strict, false); 
  }
}
