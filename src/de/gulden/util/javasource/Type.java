/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.Type
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

package de.gulden.util.javasource;

import de.gulden.util.javasource.jjt.Node;
import de.gulden.util.javasource.jjt.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Represents a type.
 *  
 * @author  Jens Gulden
 * @version  1.1
 */
public class Type implements ParserTreeConstants, Serializable {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     * The type name.
     */
    protected String typeName;

    /**
     * The dimension.
     */
    protected int dimension;

    /**
     * The my member.
     */
    protected Member myMember;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Creates a new instance of Type.
     */
    public Type(Member member) {
        myMember=member;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the unqualified type name.
     */
    public String getUnqualifiedTypeName() {
        return SourceObject.unqualify(typeName, countOuterClasses(typeName));
    }

    /**
     * Returns the type name.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Returns the full type name.
     */
    public String getFullTypeName() {
        return getTypeName() + SourceParser.repeat("[]",dimension);
    }

    /**
     * Returns the full type name.
     */
    public String getFullUnqualifiedTypeName() {
        return getUnqualifiedTypeName() + SourceParser.repeat("[]",dimension);
    }

    /**
     * Returns the dimension.
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Sets the type name.
     */
    public void setTypeName(String n) {
        typeName=n;
    }

    /**
     * Sets the dimension.
     */
    public void setDimension(int d) {
        dimension=d;
    }

    /**
     * Output this object as XML.
     *  
     * @return  The XML tag.
     * @see  #initFromXML
     */
    public Element buildXML(Document d) {
        Element e=d.createElement("type");
        String typeName=getTypeName();
        e.setAttribute("name",typeName);
        e.setAttribute("unqualifiedName",SourceObject.unqualify(typeName));
        e.setAttribute("dimension",String.valueOf(getDimension()));
        e.setAttribute("fullName",String.valueOf(getFullTypeName()));
        return e;
    }

    /**
     * Initialize this object from XML.
     *  
     * @param element The XML tag.
     * @throws IOException if an i/o error occurs
     */
    public void initFromXML(Element element) throws IOException {
        String n=element.getAttribute("name");
        String d=element.getAttribute("dimension");
        if ((n!=null)&&(d!=null)) {
            try {
                typeName=n;
                dimension=Integer.parseInt(d);
            }
            catch (NumberFormatException nfe) {
                throw new IOException("'<type>' tag must have numeric attribute 'dimension'");
            }
        }
        else {
            throw new IOException("'<type>' tag must have attribute 'qualifiedName'");
        }
    }

    /**
     * Initialize this object from parsed Java code.
     * Special way of invoking.
     *  
     * @param fathernode A father node from where array-declarators are recursively counted.
     */
    void initFromAST(Node fathernode) {
        initFromAST(fathernode, null);
    }

    /**
     * Initialize this object from parsed Java code.
     * Special way of invoking.
     *  
     * @param fathernode A father node from where array-declarators are recursively counted. May be null.
     * @param varnode variable declarator node from where additional array-declarators are recursively counted. May be null.
     */
    void initFromAST(Node fathernode, Node varnode) {
        Node typenode=fathernode.getChild(JJT_TYPE);
        if (typenode.getChild(JJT_NAME)!=null) {
            String n=typenode.getName();
            typeName=myMember.getDeclaringClass().qualify(n);
        } else {
            typeName="void";
        }
        Node searchNode;
        if (varnode != null) {
        	searchNode = typenode;
        } else {
            searchNode=fathernode;
        }
        dimension = countArrayDimension(searchNode);
        if (varnode != null) {
        	dimension += countArrayDimension(varnode);
        }
    }

    private int countOuterClasses(String classname) {
        // is classname referencing source?
        Package basePackage = myMember.getPackage().getBasePackage();
        Class clazz = basePackage.findClass(classname);
        if (clazz != null) { // yes, known from source
        	int cnt = 0;
        	while (clazz instanceof ClassInner) {
        		cnt++;
        		clazz = ((ClassInner)clazz).getDeclaringClass();
        	}
        	return cnt;
        } else { // get from classpath
        	int cnt = 0;
        	boolean found = false;
        	do {
        		try {
        			//cl.loadClass(classname); // will fail if not inner class
        			java.lang.Class.forName(classname); // will throw exception if the name is a _valid_ inner class name, because inner classes must be seperated by '$' from their parents
        			found = true;
        		} catch (ClassNotFoundException cnfe) {
        			cnt++;
        			// remove last component, until a non-inner class is reached
        			int pos = classname.lastIndexOf('.');
        			if (pos == -1) { // should actually not happen as we assume parameter classname is aready qualified (and verified for existance by doing that)
        				return 0;
        			}
        			classname = classname.substring(0, pos);
        		}
        	} while(!found);
        	return cnt;
        }
    }


    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------

    /**
     * Traverse through sub-tree and count occurrences of _ISARRAY-node.
     */
    private static int countArrayDimension(Node n) {
        if (n.getId()==JJT_ISARRAY) {
          	return 1;
        } else if (n.getId()==JJT_CODE) {
            return 0; // do not recurse into code-section
        } else {
            int sum=0;
            Node[] children=n.getAllChildren();
            for (int i=0;i<children.length;i++) {
            	Node child = children[i];
            	if ( child.getId()!=JJT_PARAMETER ) { // don't recurse into parameter (if n if JJT_METHOD), testing here allows still the first call with n.getId()==JJT_PARAMETER when parsing parameters
            		sum+=countArrayDimension(children[i]); // recursion
            	}
            }
           	return sum;
        }
    }

} // end Type
