/*
 * Project: BeautyJ - Customizable Java Source Code Transformer
 * Class:   de.gulden.util.javasource.NamedIterator
 * Version: 1.0
 *
 * Date:    2002-10-27
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

import java.lang.Class;
import java.io.*;
import java.util.*;

/**
 * Tool class providing a mechanism to access elements from a set of named elements
 * either in sequence or by name.
 *  
 * @author  Jens Gulden
 * @version  1.0
 */
public class NamedIterator implements Serializable {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------
    /**
     * The data.
     */
    public Vector data;

    /**
     * The hash.
     */
    protected Hashtable hash;

    /**
     * The pos.
     */
    protected int pos;

    /**
     * The readonly.
     */
    protected boolean readonly=false;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance of NamedIterator.
     */
    public NamedIterator() {
        data=new Vector();
        hash=new Hashtable();
        reset();
    }

    /**
     * Create new NeamedIterator that gets populated with the value of Vector
     * <code>v</code>. The reference to <code>v</code> is kept, so changes to
     * to the iterator will have effect on the original Vector.
     */
    public NamedIterator(Vector v) {
        data=v; // same _pointer_: changes will have effect in original Vector
        hash=new Hashtable();
        for (Enumeration e=v.elements();e.hasMoreElements();) {
            Named n=(Named)e.nextElement();
            hash.put(n.getName(),n);
        }
        reset();
    }

    /**
     * Special constructor called from Class.
     */
    NamedIterator(Vector v, Object type, int modifierMask) {
        // Using type 'Object' instead of 'java.lang.Class' for parameter type
        // is a workaround to avoid having to generate fully qualified class names
        // when applying BeatyJ to this code. (Otherwise, would confuse with class
        // 'Class' in this package.)
        this();
        for (Enumeration e=v.elements();e.hasMoreElements();) {
            Member m=(Member)e.nextElement();
            int mod=m.getModifier();
            if (
            ((modifierMask&mod)!=0)
            &&(m.getClass().isAssignableFrom((java.lang.Class)type))
            ) {
                data.addElement(m);
            }
        }
        reset();
        lockReadonly(); // read-only in this mode
    }

    /**
     * Creates a new instance of NamedIterator.
     */
    private NamedIterator(boolean dummy) {
        // dummy constructor for cloning
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------
    public Object clone() {
        NamedIterator c=new NamedIterator(true);
        synchronized (this) {
            c.data=(Vector)data.clone();
            c.hash=(Hashtable)hash.clone();
            c.pos=pos;
            c.readonly=readonly;
        }
        return c;
    }

    /**
     * Get next element.
     *  
     * @return  The next element, or null if <code>hasMore()==false</code>.
     */
    public Named next() {
        if (hasMore()) {
            return (Named)data.elementAt(pos++);
        }
        else {
            return null;
        }
    }

    /**
     * Tests whether there are more elements that can beretrieved using <code>next()</code>.
     */
    public boolean hasMore() {
        return (pos<data.size());
    }

    /**
     * Get an element by name.
     */
    public Named find(String n) {
        return (Named)hash.get(n);
    }

    /**
     * Sets the iterator back to element position 0.
     */
    public void reset() {
        pos=0;
    }

    /**
     * Tests whether elements can be added to or removed from this iterator.
     *  
     * @see  #addHere
     * @see  #add
     * @see  #remove
     * @see  #removeAll
     */
    public boolean isReadOnly() {
        return readonly;
    }

    /**
     * Adds an element at the current position.
     */
    public void addHere(Named n) {
        if (!readonly) {
            synchronized (this) {
                data.insertElementAt(n,pos);
                hash.put(n.getName(),n);
            }
        }
    }

    /**
     * Adds an element at the end of the iterator-list.
     */
    public void add(Named n) {
        if (!readonly) {
            synchronized (this) {
                data.addElement(n);
                hash.put(n.getName(),n);
            }
        }
    }

    /**
     * Removes an element from the iterator-list.
     */
    public void remove(Named n) {
        if (!readonly) {
            synchronized (this) {
                data.addElement(n);
                hash.put(n.getName(),n);
            }
        }
    }

    /**
     * Removes all elements from the iterator-list.
     */
    public void removeAll() {
        if (!readonly) {
            synchronized (this) {
                data.removeAllElements();
                hash.clear();
            }
        }
    }

    /**
     * Adds all elements of the NamedIterator to this.
     */
    public void add(NamedIterator it) {
        it.reset();
        synchronized (this) {
            while (it.hasMore()) {
                data.addElement(it.next());
            }
        }
    }

    /**
     * Returns the number of elements in the iterator-list.
     */
    public int size() {
        return data.size();
    }

    /**
     * Set this iterator to read-only mode.
     */
    void lockReadonly() {
        readonly=true;
    }

    /**
     * Gets the orignal Vector that stores the iterator-list.
     */
    Vector getVector() {
        return (Vector)data.clone();
    }

} // end NamedIterator
