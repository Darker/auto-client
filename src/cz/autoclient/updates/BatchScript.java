/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.updates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public class BatchScript implements ScriptWithParameters {
  //HashMap<String, String> params = new HashMap();
  String data;
  // match for name is ([\\w]+)
  String basicPattern = "(^|\\n)set %NAME%=([^\\n]*)(?=\\n.*?rem ==END OF VARDEF==)";
  public BatchScript(String data) {
    this.data = data;
    
  }
  public static BatchScript fromResource(String path) throws FileNotFoundException {
    InputStream in = BatchScript.class.getResourceAsStream(path);
    if(in!=null)
      return new BatchScript(new Scanner(in, "UTF-8").useDelimiter("\\A").next());
    else 
      throw new FileNotFoundException("Resource file not found.");
  }
  @Override
  public void setParameter(String name, String value) {
    Matcher matcher = parameterMatcher(name);
    if(matcher.find())
      data = matcher.replaceFirst("$1set "+name+"="+value);
    else
      data = data.replaceFirst("(rem ==END OF VARDEF==)", "set "+name+"="+value+"\n$1");
      //throw new IllegalArgumentException("fdsigmf");
  }

  @Override
  public String getParameter(String name) {
    Matcher matcher = parameterMatcher(name);
    if(matcher.find())
      return matcher.group(2);
    else
      return null;
  }
  private Matcher parameterMatcher(String name) {
    Pattern pattern = Pattern.compile(basicPattern.replaceFirst("%NAME%", name), Pattern.DOTALL);
    Matcher matcher = pattern.matcher(data);
    return matcher;
  }
  @Override
  public String getRawData() {
    return data;
  }

  @Override
  public boolean saveToFile(File file) throws IOException {
    file.mkdirs();
    if(file.exists())
      file.delete();
    file.createNewFile();
    try (PrintWriter out = new PrintWriter(file)) {
      out.print(data);
    }
    return true;
  }
  
}
