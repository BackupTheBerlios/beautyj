/*
 * TextImage.java
 *
 * Created on 31. Mai 2001, 22:23
 */

package de.gulden.util.javasource.jjt;

import java.io.*;
import java.util.*;

/**
 * Added to this package by Jens.
 *
 * @author  me
 * @version 
 */
public class TextImage
{
  private final int INCREASE_SIZE=100;
  
  private Vector lines;
  private Line currentline;
  
  /** Creates new TextImage */
  public TextImage()
  {
//System.out.println("--- NEW FILE ------------------------------------------------------");
    lines=new Vector();
    nextLine();
  }
  
  public void write(char c)
  {
    currentline.write(c);
  }
  
  public void nextLine()
  {
//if (currentline!=null)
//System.out.print(new String(currentline.data,0,currentline.length));
    currentline=new Line();
    lines.addElement(currentline);
  }
  
  public String getRange(int startLine,int startColumn,int endLine,int endColumn)
  {
    startColumn--;
    endColumn--;
    startLine--;
    endLine--;
//System.out.println(startLine+" "+startColumn+" "+endLine+" "+endColumn);    
    // find maximum size of all lines involved to create a buffer big enough
    int bufsize=0;
    for (int line=startLine;line<=endLine;line++)
    {
      Line l=(Line)lines.elementAt(line);
      bufsize+=l.length;
    }
    
    char[] buf=new char[bufsize];
    bufsize=0;
    
    // first line
    Line l=(Line)lines.elementAt(startLine);
    int colStart=startColumn;
    int colEnd;
    if (startLine<endLine)
    {
        colEnd= l.length-1;
        //colEnd= colStart + l.length-1;
    }
    else // startLine==endLine
    {
      colEnd=endColumn;
    }
    int len=colEnd-colStart+1;
    //if (len < 0) {
    //	len = 0;
    //}
/*if ((len>=buf.length)||(colStart+len>=l.data.length)){
	// TODO remove
	System.out.println("####");
}*/
//try {
    
    System.arraycopy(l.data,colStart,buf,0,len);
    
//} catch (Throwable th) {
//	System.out.println("####");
//}
    
    bufsize=len;
    
    // middle lines
    for (int line=startLine+1;line<=endLine-1;line++)
    {
      l=(Line)lines.elementAt(line);
      len=l.length;
      System.arraycopy(l.data,0,buf,bufsize,len);
      bufsize+=len;
    }
    
    if (startLine<endLine)
    {
      // last line
      l=(Line)lines.elementAt(endLine);
      len=endColumn+1;
//System.out.print(new String(l.data,0,l.length));
      System.arraycopy(l.data,0,buf,bufsize,len);
      bufsize+=len;
    }
    
    return new String(buf,0,bufsize);
  }

  // -------------------------------------------------------
  
  class Line
  {
    char[] data;
    int length;

    Line()
    {
      data=new char[INCREASE_SIZE];
      length=0;
    }

    /**
     * Should be synchronized when used in a multithreaded environment.
     */
    void write(char c)
    {
      int maxlen=data.length;
      if (length==maxlen) // buffer full?
      {
        char[] olddata=data;
        int newlen=length+INCREASE_SIZE;
        data=new char[newlen];
        System.arraycopy(olddata,0,data,0,length);
      }
      data[length++]=c;
    }
  }
}
