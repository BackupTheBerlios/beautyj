/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.sourclet.standard.StandardSourclet
 * Version: 1.1
 *
 * Date:    2004-09-29
 *
 * Note:    Contains auto-generated Javadoc comments created by BeautyJ.
 *  
 * This is licensed under the GNU General Public License (GPL)
 * and comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Jens Gulden
 * Email:   beautyj@jensgulden.de
 */

package de.gulden.util.javasource.sourclet.standard;

import de.gulden.util.javasource.sourclet.*;
import de.gulden.util.javasource.Package;
import de.gulden.util.javasource.Class;
import de.gulden.util.javasource.Exception;
import de.gulden.util.javasource.*;
import de.gulden.util.Toolbox;
import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.reflect.Modifier;

/**
 * The StandardSourclet used by BeautyJ.
 * See documentation of Beauty and the Java Sourclet API.
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public class StandardSourclet extends AbstractSourclet {

    // ------------------------------------------------------------------------
    // --- static fields                                                    ---
    // ------------------------------------------------------------------------

    /**
     *  
     * @see  #getSpecialPrefix
     * @see  #hasSpecialPrefix
     */
    protected static String[] specialPrefixes = {"get","set","add","remove","is","init","parse","create","build"};

    /**
     * These defaults are also named for option doc.exception.texts in the configuration XML File.
     * But if they are overwritten by the user, they still get used, so keep here as constant and always add to user's texts.
     */
    protected static String defaultExceptionTexts = "IOException=if an i/o error occurs,SQLException=if a database error occurs,SAXException=if an XML parser error occurs,NumberFormatException=if the string cannot be parsed as a number";


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     * The exception texts.
     */
    protected Properties exceptionTexts = null;

    /**
     * The headerfile text.
     */
    protected String headerfileText = null;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Creates a new instance of StandardSourclet.
     */
    public StandardSourclet() {

    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Outputs the start part of a source object.
     * This is the part which comes before the 'normal' head
     * (e.g. before a method's signature), so usually this is
     * the place where to output Javadoc comments.
     *  
     * @throws IOException if an i/o error occurs
     */
    public void buildStartSource(OutputStream out, SourceObjectDeclared o) throws IOException {
        if (o instanceof Class) {
            Class clazz=(Class)o;
            if (!(clazz instanceof ClassInner)) {
                String headerfile=getHeaderfile();
                if (headerfile!=null) {
                    write(out,headerfile);
                } else {
                    String opt;
                    write(out,"/*"+nl);
                    opt=getOption("project.name");
                    if (opt!=null) {
                        write(out," * Project: "+opt+nl);
                    }
                    write(out," * Class:   "+clazz.getName()+nl);
                    opt=getOption("project.version");
                    if (opt!=null) {
                        write(out," * Version: "+opt+nl);
                    }
                    opt=getOption("project.date");
                    if (opt!=null) {
                        String op=opt.trim();
                        if (op.equalsIgnoreCase("now")||op.equalsIgnoreCase("today")) {
                            opt=isodate();
                        }
                        write(out," *"+nl);
                        write(out," * Date:    "+opt+nl);
                    }
                    opt=getOption("project.description");
                    if (opt!=null) {
                        opt=replace(opt,"\\n","\n"); // allow line breaks expressed as "\n"
                        write(out," *"+nl);
                        write(out,startWithStars(opt,""));
                    }
                    write(out," *"+nl);
                    opt=getOption("author.name");
                    if (opt!=null) {
                        write(out," * Author:  "+opt+nl);
                    }
                    opt=getOption("author.email");
                    if (opt!=null) {
                        write(out," * Email:   "+opt+nl);
                    }
                    write(out," */"+nl+nl);
                }
                // package
                if (!clazz.getPackage().isBasePackage()) {
                    write(out,"package "+clazz.getPackage().getName()+";"+nl);
                }

                // imports
                NamedIterator it=clazz.getImports();
                if (it.hasMore()) {
                    write(out,nl);
                    while (it.hasMore()) {
                        Import im=(Import)it.next();
                        write(out,"import "+im.getName()+";"+nl);
                    }
                }
                write(out,nl);
            }
        }

        // fields, methods, constructors, classes
        if ((o instanceof Member)||(o instanceof Class)) {
            String type=getTypeCode(o);

            String spaces;
            if (o instanceof Class) {
                spaces="";
            } else {
                spaces=indent(1);
            }

            DocumentationDeclared doc=(DocumentationDeclared)o.getDocumentation();
            String text=null;
            DocumentationDeclared supDoc=tryGetDocumentationFromSuperclass(o); // inheriting documentation doesn't work properly yet is still undocumented
            if ((doc!=null) // documentation exists
                &&(doc instanceof DocumentationDeclared)) {
                if (!hasOption( type+".remove.text", "description")) {
                    text=doc.getText();
                    if (text==null||text.equals("")) {
                        if (supDoc!=null) {
                            text=supDoc.getText();
                        }
                    }
                }
            } else { // documentation doesn't exist yet, try to find in a superclass
                if ((supDoc!=null)&&(!hasOption( type+".remove.text", "description"))) {
                    text=supDoc.getText();
                }
            }
            // if it's a dummy, remove if option is set to do so
            if ((text!=null)&&isDummy(text)&&(hasOption( type+".remove.dummy", "description"))) {
                text=null;
            }

            // if no documentation found by now (or has been removed by option), maybe auto-generate
            if (text==null) {
                String unqualifiedName=o.getUnqualifiedName();
                String specialPrefix=getSpecialPrefix(unqualifiedName);
                if (hasOption( type+".create.text", "description")) {
                    if ((o instanceof Constructor)&&hasOption("method.create.text", "description")) {
                        text="Creates a new instance of "+unqualifiedName+".";
                    } else if ((o instanceof Method)&&(specialPrefix!=null)&&hasOption("method.create.text", "description")) {
                        if (specialPrefix.equals("get")) {
                            text="Returns the "+toWords(unqualifiedName.substring(specialPrefix.length()))+".";
                        } else if (specialPrefix.equals("set")) {
                            text="Sets the "+toWords(unqualifiedName.substring(specialPrefix.length()))+".";
                        } else if (specialPrefix.equals("add")) {
                            text="Adds a "+toWords(unqualifiedName.substring(specialPrefix.length()))+".";
                        } else if (specialPrefix.equals("remove")) {
                            text="Removes a "+toWords(unqualifiedName.substring(specialPrefix.length()))+".";
                        } else if (specialPrefix.equals("init")) {
                            text="Inits the "+toWords(unqualifiedName.substring(specialPrefix.length()))+".";
                        } else if (specialPrefix.equals("parse")) {
                            text="Parses the "+toWords(unqualifiedName.substring(specialPrefix.length()))+".";
                        } else if (specialPrefix.equals("create")) {
                            text="Creates the "+toWords(unqualifiedName.substring(specialPrefix.length()))+".";
                        } else if (specialPrefix.equals("build")) {
                            text="Builds the "+toWords(unqualifiedName.substring(specialPrefix.length()))+".";
                        }
                    } else if ((o instanceof Field)&&hasOption("field.create.text", "description")) {
                        if (Modifier.isFinal(o.getModifier())) {
                            text="Constant "+o.getUnqualifiedName()+SourceParser.repeat("[]",(((Field)o).getType().getDimension()))+".";
                        } else {
                            text="The "+toWords(o.getUnqualifiedName())+(((Field)o).getType().getDimension()>0?" array":"")+".";
                        }
                    } else if ((o instanceof Class)&&hasOption("class.create.text", "description")) {
                        text="Class "+o.getUnqualifiedName()+".";
                    }
                }
                // if no text found or generated, maybe generate a dummy
                if ((text==null)&&hasOption( type+".create.dummy", "description")) {
                    if (specialPrefix!=null) {
                        if (specialPrefix.equals("is")) {
                            text="Tests if ...";
                        }
                    }
                    if (text==null) {
                        text="...";
                    }
                }
            }

            boolean headDone=false;

            if (text!=null) {
                write(out,spaces+"/**"+nl);
                headDone=true;
                write(out,startWithStars(text,spaces));
            }

            ByteArrayOutputStream buf=new ByteArrayOutputStream();
            buildTagDocumentation(buf,o,doc,supDoc);
            if (buf.size()>0) {
                if (!headDone) {
                    write(out,spaces+"/**"+nl);
                    headDone=true;
                }
                write(out,startWithStars(" ",spaces));
                buf.writeTo(out);
            }
            if (headDone) {
                write(out,spaces+" */"+nl);
            }
        }
    }

    /**
     * Outputs the head part of a source object.
     * This is the actual Java code that declares the source object,
     * for example a method's signature.
     *  
     * @throws IOException if an i/o error occurs
     */
    public void buildHeadSource(OutputStream out, SourceObjectDeclared o) throws IOException {
        boolean fullQualify=isOption("code.qualify");

        if (o instanceof Member) {
            write(out,indent(1));
        }

        String mod=Modifier.toString(o.getModifier());
        if (mod.length()>0) {
            write(out,mod+" ");
        }

        if (o instanceof Typed) { // Method, Field or Parameter
            Typed typed=(Typed)o;
            String t;
            if (fullQualify) {
            	t = typed.getType().getFullTypeName();
            } else {
            	t = typed.getType().getFullUnqualifiedTypeName();
            	String unqualifiedType = typed.getType().getUnqualifiedTypeName();
            	String qualifiedType = typed.getType().getTypeName();
            	Class declaringClass;
            	if ( ! (typed instanceof Parameter)) {
            		declaringClass = ((SourceObjectDeclared)typed).getDeclaringClass();
            	} else {
            		declaringClass = ((Parameter)typed).getMemberExecutable().getDeclaringClass();
            	}
            	if (makeSureIsQualifyable(declaringClass, unqualifiedType, qualifiedType).equals(qualifiedType)) {
                	t = typed.getType().getFullTypeName(); // 'fallback' to fully qualified
            	}
            	if (t.indexOf('.')!=-1) {
                	// Although unqualified, inner classes will be lead by classname-prefix.
                	// Remove this prefix if it is the current class.
                	if (declaringClass != null) {
                		String thisClassname = declaringClass.getUnqualifiedName();
                		if (t.startsWith(thisClassname+".")) {
                			t = t.substring(thisClassname.length()+1);
                		}
                	}
            	}
            }
            write(out,t+" ");
        }

        if (o instanceof Class) {
            Class clazz=(Class)o;
            write(out,clazz.isInterface()?"interface ":"class ");
        }

        // name
        write(out,o.getUnqualifiedName());

        if (o instanceof Class) {
            Class clazz=(Class)o;
            String sup=clazz.getSuperclassName();
            if (!sup.equals("java.lang.Object")) {
                String s=sup;
                if (!fullQualify) {
                    s = unqualifyClassname(clazz, s);
                }
                write(out," extends "+s);
            }

            // implemented interfaces
            Enumeration en=clazz.getInterfaceNames();
            if (en.hasMoreElements()) {
                if (!clazz.isInterface()) {
                    write(out," implements ");
                } else {
                    write(out," extends ");
                }
                while (en.hasMoreElements()) {
                    String in=(String)en.nextElement();
                    if (!fullQualify) {
                        in = unqualifyClassname(clazz, in);
                    }
                    write(out,in+(en.hasMoreElements()?(", "):""));
                }
            }
        }

        else if (o instanceof MemberExecutable) {
            MemberExecutable mex=(MemberExecutable)o;
            // parameters
            write(out,"(");
            for (NamedIterator it=mex.getParameters();it.hasMore();) {
                Parameter pa=(Parameter)it.next();
                buildSource(out,pa);
                if (it.hasMore()) {
                    write(out,", ");
                }
            }
            write(out,")");

            // exceptions
            NamedIterator it=mex.getExceptions();
            if (it.hasMore()) {
                write(out," throws ");
                while (it.hasMore()) {
                    Exception ex=(Exception)it.next();
                    String exx;
                    if (!fullQualify) {
                        exx=ex.getUnqualifiedName();
                        exx = makeSureIsQualifyable(mex.getDeclaringClass(), exx, ex.getName());
                    } else {
                        exx=ex.getName();
                    }

                    write(out,exx+(it.hasMore()?", ":""));
                }
            }
        }
    }

    /**
     * Outputs the body content of the source object. For example,
     * in case of methods this is Java code, in case of classes this recursively
     * contains other SourceObjects' code.
     *  
     * @throws IOException if an i/o error occurs
     */
    public void buildBodySource(OutputStream out, SourceObjectDeclared o) throws IOException {

        boolean bracesLinebreak = isOption("code.braces.linebreak");

        // --- class

           if (o instanceof Class) {
               Class clazz=(Class)o;
               NamedIterator it=clazz.getAllMembers();
           	if (bracesLinebreak) { // start with curly brace on individual line
                   write(out, nl + "{");
                   if ( ! isOption("code.separators")) {
                   	write(out, nl);
                   }
           	} else { // output curly brace at the end of the method's declaration
           		write(out," {"+nl);
           	}

               Vector todo=new Vector();

           	if (isOption("code.preserve.fields.order")) {

                   it.reset();
                   boolean firstField = true;
                   while (it.hasMore()) {
                       Member m=(Member)it.next();
                       if (m instanceof Field) {
                           todo.addElement(m);
                           //buildSourceAll(out,todo,(Modifier.isFinal(m.getModifier()) ? "final ":"") + "static field"); // will generate a header for each field, but that doesn't do any harm (called individually for each element to preserve order)
                       }
                   }

           		buildHeader(out,'-',"field", "fields", todo.size());

        		Vector singleTodo = new Vector();
           		for (Enumeration e = todo.elements(); e.hasMoreElements(); ) {
           			singleTodo.removeAllElements();
           			singleTodo.addElement( e.nextElement() );
                       buildSourceAll(out,singleTodo,null); // build single elements
           		}

           	} else { // normal beautification: order by final-static / static / non-static and public / protected / friendly / private

                   it.reset();
                   while (it.hasMore()) {
                       Member m=(Member)it.next();
                       if ( (m instanceof Field) && Modifier.isStatic(m.getModifier())  && Modifier.isFinal(m.getModifier()) ) {
                           todo.addElement(m);
                       }
                   }
                   buildSourceAll(out,todo,"final static field");

                   it.reset();
                   todo.removeAllElements();
                   while (it.hasMore()) {
                       Member m=(Member)it.next();
                       if ( (m instanceof Field) && Modifier.isStatic(m.getModifier() )  && (!Modifier.isFinal(m.getModifier()) ) ) {
                           todo.addElement(m);
                       }
                   }
                   buildSourceAll(out,todo,"static field");

                   it.reset();
                   todo.removeAllElements();
                   while (it.hasMore()) {
                       Member m=(Member)it.next();
                       if ((m instanceof Field)&&(!Modifier.isStatic(m.getModifier()))) {
                           todo.addElement(m);
                       }
                   }
                   buildSourceAll(out,todo,"field");

           	}

               // initializers (must appear after field declarations to avoid forward references)

               Code[] initializers = clazz.getStaticInitializers();
               if (initializers.length>0) {
                   buildHeader(out,'-',"static initializer", initializers.length);
                   for (int i = 0; i < initializers.length; i++) {
                       String code = initializers[i].getRaw();
                       code=applyCodeFormatting(code);
                       write(out,indent("static {",1));
                       write(out,nl);
                       write(out,code);
                       write(out,nl);
                       write(out,indent("}",1));
                       write(out,nl);
                   }
               }

               initializers = clazz.getInstanceInitializers();
               if (initializers.length>0) {
                   buildHeader(out,'-',"instance initializer", initializers.length);
                   for (int i = 0; i < initializers.length; i++) {
                       String code = initializers[i].getRaw();
                       code=applyCodeFormatting(code);
                       write(out,indent("{",1));
                       write(out,nl);
                       write(out,code);
                       write(out,nl);
                       write(out,indent("}",1));
                       write(out,nl);
                   }
               }


               it.reset();
               todo.removeAllElements();
               while (it.hasMore()) {
                   Member m=(Member)it.next();
                   if (m instanceof Constructor) {
                       todo.addElement(m);
                   }
               }
               buildSourceAll(out,todo,"constructor");

               it.reset();
               todo.removeAllElements();
               while (it.hasMore()) {
                   Member m=(Member)it.next();
                   if ((m instanceof Method)&&(!Modifier.isStatic(m.getModifier()))) {
                       todo.addElement(m);
                   }
               }
               buildSourceAll(out,todo,"method");

               it.reset();
               todo.removeAllElements();
               while (it.hasMore()) {
                   Member m=(Member)it.next();
                   if ((m instanceof Method)&&(Modifier.isStatic(m.getModifier()))) {
                       todo.addElement(m);
                   }
               }
               buildSourceAll(out,todo,"static method");


               // inner classes and interfaces

               it=clazz.getInnerClasses();
               if (it.size()>0) {
                   buildHeader(out,'*',(it.size()==1)?"inner class":"inner classes");
                   while (it.hasMore()) {
                       Class c=(Class)it.next();
                       ByteArrayOutputStream outInner=new ByteArrayOutputStream();
                       buildSource(outInner,c);
                       String shifted=indent(new String(outInner.toByteArray()),1);
                       write(out,shifted+nl);
                   }
               }

               write(out,"} // end "+clazz.getUnqualifiedName()+nl);
           }

           // --- method / field

        else if ((o instanceof MemberExecutable)||(o instanceof Field)) {
               Code code;
               if (o instanceof MemberExecutable) {
                   code=((MemberExecutable)o).getCode();
               }
               else {
                   code=((Field)o).getCode();
               }

               if (code!=null) {
                   String c=code.getRaw();
                   if (o instanceof MemberExecutable) {
                   	if (isOption("code.braces.linebreak")) { // start with curly brace on individual line
                           write(out, nl);
                           write(out,indent("{", 1)+nl);
                   	} else { // output curly brace at the end of the method's declaration
                   		write(out," {"+nl);
                   	}
                       c=applyCodeFormatting(c);
                       write(out,c+nl);
                       write(out,indent("}",1)+nl);
                   } else { // Field
                       write(out," = "+c+";"+nl);
                   }
               }
               else {
                   write(out,";"+nl);
               }
               write(out,nl);
           }
    }

    /**
     * Outputs everything that occurs after the SourceObject.
     *  
     * @throws IOException if an i/o error occurs
     */
    public void buildEndSource(OutputStream out, SourceObjectDeclared o) throws IOException {
        //nop
    }

    /**
     * Returns the associated description for the exception class name specified by <code>exc</code>.
     */
    protected String getExceptionText(String exc) {
        if (exceptionTexts==null) { // auto-init on first call
            exceptionTexts=new Properties();
            String t=defaultExceptionTexts+getOption("exception.texts"); // repeat defaultExceptionTexts to make sure that defaults are contained if the user sets own value, append in front to make user settings able to overwrite defaults
            StringTokenizer st=new StringTokenizer(t,",",false);
            while (st.hasMoreTokens()) {
                String s=st.nextToken();
                StringTokenizer st2=new StringTokenizer(s,"=",false);
                String exception=st2.nextToken();
                String text=st2.nextToken();
                if (!(exception==null||text==null||st2.hasMoreTokens())) {
                    exceptionTexts.setProperty(exception.trim(),text.trim());
                } else {
                    // illegal option format, ignore
                }
            }
        }
        return exceptionTexts.getProperty(exc);
    }

    /**
     * If a headerfile is specified, get the content as string.
     */
    protected String getHeaderfile() {
        if (headerfileText==null) {
            // auto-init from project.headerfile
            String filename=getOption("project.headerfile");
            if (filename!=null) {
                File file=new File(filename);
                if (file.isFile()) {
                    try {
                        char[] c=new char[(int)file.length()];
                        FileReader f=new FileReader(file);
                        f.read(c);
                        f.close();
                        headerfileText=new String(c);
                    } catch (IOException e) {
                        headerfileText="/* ERROR READING HEADERFILE '"+filename+"' */";
                    }
                }
            }
        }
        return headerfileText; // null if no headerfile
    }

    /**
     * Builds the tag documentation.
     *  
     * @throws IOException if an i/o error occurs
     */
    protected void buildTagDocumentation(OutputStream out, SourceObjectDeclared o, DocumentationDeclared doc, DocumentationDeclared supDoc) throws IOException {
        // parameters doc, supDoc may be null
        if (o instanceof Class) {
            Class clazz=(Class)o;
            // @author
            String opt;
            opt=getOption("author.name");
            if (opt!=null) {
                buildTagDocumentationSynthesize(out,"class","author",null,opt,doc,supDoc,"",true);
            }
            // @version
            opt=getOption("project.version");
            if (opt!=null) {
                buildTagDocumentationSynthesize(out,"class","version",null,opt,doc,supDoc,"",true);
            }
        } else if (o instanceof MemberExecutable) {
            MemberExecutable mex=(MemberExecutable)o;
            String unqualifiedName=o.getUnqualifiedName();
            String specialPrefix=getSpecialPrefix(unqualifiedName);

            // @param
            boolean createText=hasOption("method.create.text", "param");
            boolean createDummy=hasOption("method.create.dummy", "param");
            String type=getTypeCode(o);
            for (NamedIterator it=mex.getParameters();it.hasMore();) {
                Parameter pa=(Parameter)it.next();
                String def=null;
                if (createText&&(specialPrefix!=null)&&(specialPrefix.equals("set"))) {
                    def="The "+toWords(unqualifiedName.substring(specialPrefix.length()))+(pa.getType().getDimension()>0?" array":"")+".";
                } else if (createText) {
                    String n=pa.getName();
                    if (n.length()<3) { // use type name if too short identifier
                        n=pa.getType().getUnqualifiedTypeName();
                    }
                    def="The "+toWords(n)
                    +(pa.getType().getDimension()>0?" array":"")
                    +".";
                } else if (createDummy) {
                    def="The ...";
                }
                buildTagDocumentationSynthesize(out,type,"param",pa.getName(),def,doc,supDoc,indent(1),true);
            }

            // @throws
            createText=hasOption("method.create.text", "throws");
            createDummy=hasOption("method.create.dummy", "throws");
            for (NamedIterator it=mex.getExceptions();it.hasMore();) {
                Exception ex=(Exception)it.next();
                String exc=ex.getUnqualifiedName();
                // is there a text given for that particular exception?
                String def=getExceptionText(exc);
                // if no text given, synthesize dummy (if dummy option enabled)
                if (createDummy&&(def==null)) {
                    def="if ...";
                }
                buildTagDocumentationSynthesize(out,type,"throws",exc,def,doc,supDoc,indent(1),true);
            }

            // @return
            if (o instanceof Method) {
                Method met=(Method)o;
                createText=hasOption("method.create.text", "return");
                createDummy=hasOption("method.create.text", "return");
                if (!(met.getType().getFullTypeName().equals("void"))) {
                    // get default text
                    String def=null;
                    if (createText && (specialPrefix!=null) && specialPrefix.equals("get")) {
                        def="The "+toWords(unqualifiedName.substring(specialPrefix.length()))
                        +(met.getType().getDimension()>0?" array":"")
                        +".";
                    } else if (createText) {
                        def="The "+toWords(met.getType().getUnqualifiedTypeName())
                        +(met.getType().getDimension()>0?" array":"")
                        +".";
                    } else if (createDummy) {
                        def="The ...";
                    }
                    buildTagDocumentationSynthesize(out,"method","return",null,def,doc,supDoc,indent(1),true);
                }
            }
            // @see - not useful
        }
        // copy all other tags
        if (doc!=null) {
            for (Enumeration e=doc.getTags();e.hasMoreElements();) {
                DocumentationTagged dt=(DocumentationTagged)e.nextElement();
                if ((!dt.getTag().equals("@param"))
                &&(!dt.getTag().equals("@throws"))
                &&(!dt.getTag().equals("@return"))
                &&(!((o instanceof Class)&&(dt.getTag().equals("@author"))))
                &&(!((o instanceof Class)&&(dt.getTag().equals("@version"))))) {
                    buildDocumentationTagged(out,dt,(o instanceof Class)?"":indent(1));
                }
            }
        } else if (supDoc!=null) {
            for (Enumeration e=supDoc.getTags();e.hasMoreElements();) {
                DocumentationTagged dt=(DocumentationTagged)e.nextElement();
                if ((!dt.getTag().equals("@param"))
                &&(!dt.getTag().equals("@throws"))
                &&(!dt.getTag().equals("@return"))
                &&(!((o instanceof Class)&&(dt.getTag().equals("@author"))))
                &&(!((o instanceof Class)&&(dt.getTag().equals("@version"))))) {
                    buildDocumentationTagged(out,dt,(o instanceof Class)?"":indent(1));
                }
            }
        }
    }

    /**
     * Builds the tag documentation synthesize.
     *  
     * @throws IOException if an i/o error occurs
     */
    protected void buildTagDocumentationSynthesize(OutputStream out, String typeName, String tagName, String tagItem, String defaultValue, DocumentationDeclared doc, DocumentationDeclared supDoc, String spaces, boolean synthesize) throws IOException {
        // parameters defaultValue, doc, supDoc may be null
        DocumentationTagged tag=null;
        // tag already there?
        if (doc!=null) {
            if (!hasOption( typeName+".remove.text", tagName)) {
                tag=doc.findTag("@"+tagName,tagItem);
            }
            String text=null;
            if (tag!=null) {
                text=tag.getText();
            }
            if ((text==null)||(text.trim().length()==0)) {
                tag=null;
            }
            // remove dummy if activated
            if ((tag!=null)&&isDummy(text)&&hasOption( typeName+".remove.dummy", tagName)) {
                tag=null;
            }
        }
        // is there a matching documentation tag in superclass documentation?
        if (tag==null) {
            if (supDoc!=null) {
                tag=supDoc.findTag("@"+tagName,tagItem);
            }
            String text=null;
            if (tag!=null) {
                text=tag.getText();
            }
            if ((text==null)||(text.trim().length()==0)) {
                tag=null;
            }
            // remove dummy if activated
            if ((tag!=null)&&(isDummy(text)&&(hasOption( typeName+".remove.dummy", tagName)))) {
                tag=null;
            }
        }
        // auto-generate
        if ((tag==null) && synthesize && (defaultValue!=null)) {
            tag=new DocumentationTagged();
            tag.setTag("@"+tagName);
            tag.setItem(tagItem);
            tag.setText(defaultValue);
        }
        // output if something found or generated
        if (tag!=null) {
            buildDocumentationTagged(out,tag,spaces);
        }
    }

    /**
     * Builds all source objects in Vector <code>v</code>, sorted by
     * <ul>
     * <li><b>public</b></li>
     * <li><b>protected</b></li>
     * <li><b>package-private</b></li>
     * <li><b>private</b></li>
     * </ul>
     * modifiers.<br>
     * In front of the generated code, a header is attached which names the type
     * of the source objects, distinguishing between singular (if v.size()==1) and plural (f v.size()>1) form.<br>
     * Here, the plural form is derived from simply adding an 's', which works for
     * most english words.
     *  
     * @throws IOException if an i/o error occurs
     */
    protected void buildSourceAll(OutputStream out, Vector v, String header) throws IOException {
        //, InvalidOptionException
        String headerPlural;
        if (header != null) {
        headerPlural = header + "s";
        } else {
        headerPlural = null;
        }

        buildSourceAll( out, v, header, headerPlural );
    }

    /**
     * Builds all source objects in Vector <code>v</code>, sorted by
     * <ul>
     * <li><b>public</b></li>
     * <li><b>protected</b></li>
     * <li><b>package-private</b></li>
     * <li><b>private</b></li>
     * </ul>
     * modifiers.<br>
     * In front of the generated code, a header is attached which names the type
     * of the source objects, distinguishing between singular (if v.size()==1) and plural (f v.size()>1) form.
     *  
     * @throws IOException if an i/o error occurs
     */
    protected void buildSourceAll(OutputStream out, Vector v, String header, String headerPlural) throws IOException {
        if (!v.isEmpty()) {
        	if ( (header != null) && (headerPlural != null) ) {
        		buildHeader(out,'-',header, headerPlural, v.size());
        	}
            // public
            for (Enumeration e=v.elements();e.hasMoreElements();) {
                SourceObjectDeclared o=(SourceObjectDeclared)e.nextElement();
                if (Modifier.isPublic(o.getModifier())) {
                    buildSource(out,o);
                }
            }
            // protected
            for (Enumeration e=v.elements();e.hasMoreElements();) {
                SourceObjectDeclared o=(SourceObjectDeclared)e.nextElement();
                if (Modifier.isProtected(o.getModifier())) {
                    buildSource(out,o);
                }
            }
            // friendly
            for (Enumeration e=v.elements();e.hasMoreElements();) {
                SourceObjectDeclared o=(SourceObjectDeclared)e.nextElement();
                if ((!Modifier.isPublic(o.getModifier()))&&(!Modifier.isProtected(o.getModifier()))&&(!Modifier.isPrivate(o.getModifier()))) {
                    buildSource(out,o);
                }
            }
            // private
            for (Enumeration e=v.elements();e.hasMoreElements();) {
                SourceObjectDeclared o=(SourceObjectDeclared)e.nextElement();
                if (Modifier.isPrivate(o.getModifier())) {
                    buildSource(out,o);
                }
            }
        }
    }

    /**
     * Outputs a seperating header, if the corresponding option is set.
     *  
     * @throws IOException if an i/o error occurs
     */
    protected void buildHeader(OutputStream out, char mark, String header) throws IOException {
        if (isOption("code.separators")) {
            write(out,nl+indent("// "+chars(mark,72)+nl
            +"// "+chars(mark,3)+" "+header+spaces(65-header.length())+chars(mark,3)+nl
            +"// "+chars(mark,72)+nl,1)+nl);
        }
    }

    /**
     * Outputs a seperating header, if the corresponding option is set. Depending on <code>num</code>,
     * the plural or singular form is used (or nothing, if num==0).
     *  
     * @throws IOException if an i/o error occurs
     */
    protected void buildHeader(OutputStream out, char mark, String header, String headerPlural, int num) throws IOException {
        if (num>0) {
        	buildHeader(out, mark, (num!=1 ? headerPlural : header));
        }
    }

    /**
     * Outputs a seperating header, if the corresponding option is set. Depending on <code>num</code>,
     * a plural or singular form is used (or nothing, if num==0). The plural is auto-generated by adding an 's'.
     *  
     * @throws IOException if an i/o error occurs
     */
    protected void buildHeader(OutputStream out, char mark, String header, int num) throws IOException {
        buildHeader(out, mark, header, header+"s", num);
    }

    /**
     * Indents a given block of text by the given number of steps.
     * The size of each step in 'number of spaces' is set by option
     * code.indent.spaces.
     *  
     * @return  Indented text.
     */
    protected String indent(String text, int steps) {
        StringTokenizer st=new StringTokenizer(text,"\n\r", true);
        StringBuffer sb=new StringBuffer();
        int spaces=getIntOption("code.indent.spaces"); // TODO: optimize
        char lastBreak = (char)0;

        while (st.hasMoreTokens()) {
            String line=st.nextToken();
            char first = line.charAt(0);
            if (first=='\n' || first=='\r') { // delimiter token
            		if ( (lastBreak==(char)0) || lastBreak==first  ) {
            			sb.append(nl); // etxtra blank line
            			lastBreak = first;
            		}
            } else { // full line token
            	sb.append(spaces(steps*spaces));
            	sb.append(line);
            	lastBreak = (char)0;
            }
        }
        return sb.toString();
    }

    /**
     * Returns a string with spaces. The number is determined by steps*option('code.indent.spaces').
     */
    protected String indent(int steps) {
        int spaces=getIntOption("code.indent.spaces"); // TODO: optimize
        return spaces(steps*spaces);
    }

    /**
     * Apply code transformations according to options set by the user.
     */
    protected String applyCodeFormatting(String code) {
        if (isOption("code.clean")) {
            code=cleanCode(code);
        }
        if (isOption("code.format")) {
        	code = format(code);
        }
        code = indent(code, 2);
        return code;
    }

    /**
     * Format a code block according to standard auto-indentation rules.
     * This is a quick solution and could be done much more sophisticated.
     * (The current version of the StandardSourclet puts focus in the overall
     * organization of .java files, not primarily on the code blocks.)
     * Maybe later versions should incorporate mature code from other OpenSource
     * beautifier projects here - volunteers welcome!
     */
    protected String format(String code) {
        int codeLength=code.length();
        if (codeLength>0) {
            StringBuffer sb=new StringBuffer();
            StringBuffer line=new StringBuffer();
            char quoted=(char)0;
            char comment=(char)0;
            boolean escape=false;
            int indentLevel=0;
            int indentLevelDiff=0;
            boolean newline=false;
            boolean previousNewline=false;
            int i=0;
            char c=(char)0;
            char prevC;
            char nextC=code.charAt(0);

            while (i<codeLength) {
                prevC=c;
                c=nextC;
                if (i<codeLength-1) {
                    nextC=code.charAt(i+1);
                } else {
                    nextC=(char)0;
                }

                if (escape) { // escaped char
                   line.append(c);
                   escape=false;

                } else if (comment=='/') { // line comment
                    if (c=='\n') {
                        newline=true;
            			previousNewline = true;
                        comment=(char)0;
                    }
                    line.append(c);

                } else if (comment=='*') { // block comment
                    if (c=='/'&&prevC=='*') {
                        comment=(char)0;
                    }
                    line.append(c);

                } else if (quoted!=(char)0) { // quoted text, either ".." or '..'

                    if (c=='\\') {
                        escape=true;
                    } else if (quoted==c) { // quote char again: end quoting
                        quoted=(char)0;
                    }
                    line.append(c);

                } else { // normal code

                    switch (c) {
                        case '\\': escape=true;
                                   break;
                        case '/':  if (nextC=='/'||nextC=='*') {
                                       comment=nextC;
                                   }
                                   break;
                        case '\"':
                        case '\'': quoted=c;
                                   break;
                        case '\n':
                        			newline=previousNewline; // explicit newline only if no other (auto-generated) newline since last explicit newline
                        			previousNewline = true;
                                   break;
                        case ';':  String l=line.toString().trim();
                                   if (!l.startsWith("for")) { // special: no line break after ; in for-loop declaration
                                       newline=true;
                                   }
                                   break;
                        case '{':  newline=true;
                                   indentLevelDiff=+1;
                                   break;
                        case '}':  l=line.toString().trim();
                                   if (l.length()>0) { // already something there in current line: write as own line
                                       sb.append(indent(l,indentLevel)+nl);
                                       line=new StringBuffer();
                                   }
                                   indentLevel-=1;
                                   newline = (nextC=='\n'); // cheap trick  to make sure we don't loose blank lines after } (will fail if e.g. "} \n" )
                                   break;
                    }
                    line.append(c);

                }

                i++; // next char
                if (i>=codeLength) { // always output last line if end reached
                    newline=true;
                }
                if (newline) {
                    sb.append(indent(line.toString().trim(),indentLevel));
                    if (i < codeLength) { // prepare for next line if end not reached,also output nl only in that case
                    	sb.append(nl);
                    	indentLevel+=indentLevelDiff;
                    	indentLevelDiff=0;
                    	line=new StringBuffer();
                    	newline=false;
                    	previousNewline = false;
                    }
                }

            }
            return sb.toString();
        } else {
            return "";
        }
    }

    protected String unqualifyClassname(Class inClass, String name) {
        String result;
        if (name.indexOf('.')!=-1) { // not unqualified already
           	String u = unqualify(name);
        	String rest = name.substring(0, name.length()-u.length()-1);
        	// is parent-identifier a class? (not package) - then it is an inner class
        	if (Package.isSourcePackage(inClass.getPackage().getBasePackage(), rest)) { // optimization, but will not find a package if from classpath
        		result = u;
        	} else {
        		String outer;
        		try {
        			outer = inClass.qualify(rest);
        			result = unqualifyClassname(inClass, outer) + "." + u;
        		} catch (NoClassDefFoundError ncdfe) {
        			result = u;
        		}
        	}
        } else {
        	result = name;
        }
        return makeSureIsQualifyable(inClass, result, name);
    }

    /**
     * Tests if an unqulified class name can be fully qualified inside a class declaration.
     * If this is not the case, depending on the user-option '-code.auto.import' an import statement
     * is generated, or just a warning message is output.
     */
    protected String makeSureIsQualifyable(Class inClass, String unqualified, String qualified) {
            	try {
            		inClass.qualify(unqualified);
            		return unqualified;
            	} catch (NoClassDefFoundError ncdfe) {
            		if (isOption("code.qualify.auto")) {
            			// auto-generate import statement
            			return qualified;
            		} else {
            			System.err.println("warning: in class "+inClass.getName()+": outputting unqualified classname '"+unqualified+"', although it seems to be not fully qualifyable. If this leads to problems when compiling, try option -code.qualify.auto, or use -code.qualify to always use fully qualified classnames.");
            			return unqualified;
            		}
            	}
    }


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------

    /**
     * Appends a vertical line of star characters in front of <code>s</code>.
     * <code>s</code> may contain multiple lines, seperated by either \n or \r,
     * in that case a multi-line result is returned.
     */
    public static String startWithStars(String s, String spaces) {
        StringTokenizer st=new StringTokenizer(s,"\r\n");
        StringBuffer sb=new StringBuffer();
        while (st.hasMoreTokens()) {
            sb.append(spaces+" * "+st.nextToken()+nl);
        }
        return sb.toString();
    }

    /**
     * Return the name part of an identifier behind the last '.' occurrence.
     *  
     * @param n The qualified (or already unqualified) identifier name.
     * @return  The unqualified identifier name.
     */
    public static String unqualify(String n) {
        int pos=n.lastIndexOf('.');
        if (pos!=-1) {
            return n.substring(pos+1);
        }
        else {
            return n;
        }
    }

    /**
     * Returns a string repeating the character <code>mark</code>
     * for <code>count</code> times.
     */
    public static String chars(char mark, int count) {
        StringBuffer sb=new StringBuffer();
        for (int i=0;i<count;i++) {
            sb.append(mark);
        }
        return sb.toString();
    }

    /**
     * Returns a string repeating the space character
     * for <code>count</code> times.
     */
    public static String spaces(int count) {
        return chars(' ',count);
    }

    /**
     * Replaces all occurrences of string <code>find</code> in <code>s</code> by <code>repl</code>.
     */
    public static String replace(String s, String find, String repl) {
        int pos=s.indexOf(find);
        if (pos!=-1) {
            return s.substring(0,pos)+repl+replace(s.substring(pos+find.length()),find,repl);
        } else {
            return s;
        }
    }

    /**
     * Returns the current date in ISO date format yyyy-MM-dd.
     */
    public static String isodate() {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }

    /**
     * Returns the special prefix of the identifer, if there is any.
     * Otherwise <code>null</code> is returned.
     *  
     * @see  #specialPrefixes
     */
    public static String getSpecialPrefix(String id) {
        for (int i=0;i<specialPrefixes.length;i++) {
            int l=specialPrefixes[i].length();
            if ((id.length()>l)
               && id.startsWith(specialPrefixes[i])
               && Character.isUpperCase(id.charAt(l)) // next char after prefix must be upper case
               ) {
                return specialPrefixes[i];
            }
        }
        return null;
    }

    /**
     * Tests whether the identifier has a special prefix.
     *  
     * @see  #specialPrefixes
     */
    public static boolean hasSpecialPrefix(String id) {
        return getSpecialPrefix(id)!=null;
    }

    /**
     * Convert a string of format "xxxYyyyZzzz" to "xxx yyyy zzzz".
     * This is a simple mechanism to retrieve english language
     * for auto-generating Javadoc comments from semantically rich identifier names.
     *  
     * @param s The identifier name.
     * @return  English language fragment retrieved from identifier name.
     */
    public static String toWords(String s) {
        if (s.length()>0) {
            int end=1;
            while ((end<s.length())&&(!Character.isUpperCase(s.charAt(end)))) {
                end++;
            }
            if (end==s.length()) {
                return s.toLowerCase();
            } else {
                return s.substring(0,end).toLowerCase()+" "+toWords(s.substring(end)); // recursion
            }
        } else {
            return "";
        }
    }

    public static boolean isDummy(String text) {
        return text.indexOf("...")!=-1; // Strings like "The result is..." are also dummies.
    }

    /**
     * Removes out-commented dead lines of from a block of Java code.
     *  
     * @see  #isDeadCodeLine
     */
    public static String cleanCode(String c) {
        BufferedReader b=new BufferedReader(new StringReader(c));
        StringBuffer r=new StringBuffer();
        try {
            String l=b.readLine();
            while (l!=null) {
            	String nextL = b.readLine();
                if (!isDeadCodeLine(l)) {
                    r.append(l);
                    if (nextL != null) {
                    	r.append(nl);
                    }
                }
                l = nextL;
            }
        } catch (IOException io) {
            r.append("// *** ERROR: IOException while cleaning code");
        }
        return r.toString();
    }

    /**
     * Tests whether a line of Java code is an out-commented dead line of code.
     * This is the case when it starts with "//" and end with either ';', '{' or '}'.
     */
    public static boolean isDeadCodeLine(String l) {
        l=l.trim();
        return l.startsWith("//")
        && (l.endsWith(";")||l.endsWith("{")||l.endsWith("}"));
    }

    /**
     * Returns the canonical string representation of a SourceObject's type.
     */
    public static String getTypeCode(SourceObject o) {
        String c=unqualify(o.getClass().getName()).toLowerCase();
        if (c.equals("constructor")) {
            c="method"; // use same parameters for constructors and methods
        } else if (c.equals("classinner")) {
            c="class";
        }
        return c;
    }

    /**
     * Returns the parent part of a fully qualified identifier name.
     */
    protected static String getParentQualifier(String name) {
        int pos=name.lastIndexOf('.');
        if (pos!=-1) {
            name=name.substring(0,pos);
        } else {
            name=null;
        }
        return name;
    }

    protected static DocumentationDeclared tryGetDocumentationFromSuperclass(SourceObjectDeclared o) {
        if (o instanceof MemberExecutable) {
            MemberExecutable mex=(MemberExecutable)o;
            Class c=mex.getDeclaringClass();
            String supName=c.getSuperclassName();
            Class sup=c.getPackage().getBasePackage().findClass(supName);
            while (sup!=null) {
                for (NamedIterator it=sup.getAllMembers();it.hasMore();) {
                    Member m=(Member)it.next();
                    if (m instanceof MemberExecutable) {
                        if (m.equals(mex)) {
                            Documentation d=m.getDocumentation();
                            if ((d!=null)
                            &&(d instanceof DocumentationDeclared)) {
                                return (DocumentationDeclared)d;
                            }
                        }
                    }
                }
                if (!sup.getName().equals("java.lang.Object")) {
                    supName=sup.getSuperclassName();
                    sup=c.getPackage().getBasePackage().findClass(supName);
                } else {
                    sup=null;
                }
            }
        }
        return null;
    }

    /**
     * Builds the documentation tagged.
     *  
     * @throws IOException if an i/o error occurs
     */
    protected static void buildDocumentationTagged(OutputStream out, DocumentationTagged tag, String spaces) throws IOException {
        write(out,startWithStars(tag.getTag()+" "+(((tag.getItem()!=null)?tag.getItem():"")+" "+tag.getText()),spaces));
    }

} // end StandardSourclet
