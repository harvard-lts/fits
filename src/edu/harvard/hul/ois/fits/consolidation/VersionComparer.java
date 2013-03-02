/* 
 * Copyright 2009 Harvard University Library
 * 
 * This file is part of FITS (File Information Tool Set).
 * 
 * FITS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FITS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FITS.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.hul.ois.fits.consolidation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * This class represents a plug-in version identifier.
 * <br>
 * @version $Id: VersionComparer.java,v 1.2 2009/07/28 18:04:11 spencer Exp $
 */
public final class VersionComparer implements Serializable, Comparable<VersionComparer> {
    private static final long serialVersionUID = -3054349171116917643L;

    /**
     * Version identifier parts separator.
     */
    public static final char SEPARATOR = '.';
    
    /**
     * Parses given string as version identifier. All missing parts will be
     * initialized to 0 or empty string. Parsing starts from left side of the
     * string.
     * @param str version identifier as string
     * @return version identifier object
     */
    public static VersionComparer parse(final String str) {
        VersionComparer result = new VersionComparer();
        result.parseString(str);
        return result;
    }

    private transient int major;
    private transient int minor;
    private transient int build;
    private transient String name;
    private transient String asString;
    
    private VersionComparer() {
        // no-op
    }
    
    private void parseString(final String str) {
        major = 0;
        minor = 0;
        build = 0;
        name = ""; //$NON-NLS-1$
        StringTokenizer st = new StringTokenizer(str, "" + SEPARATOR, false); //$NON-NLS-1$
        // major segment
        if (!st.hasMoreTokens()) {
            return;
        }
        String token = st.nextToken();
        try {
            major = Integer.parseInt(token, 10);
        } catch (NumberFormatException nfe) {
            name = token;
            while (st.hasMoreTokens()) {
                name += st.nextToken();
            }
            return;
        }
        // minor segment
        if (!st.hasMoreTokens()) {
            return;
        }
        token = st.nextToken();
        try {
            minor = Integer.parseInt(token, 10);
        } catch (NumberFormatException nfe) {
            name = token;
            while (st.hasMoreTokens()) {
                name += st.nextToken();
            }
            return;
        }
        // build segment
        if (!st.hasMoreTokens()) {
            return;
        }
        token = st.nextToken();
        try {
            build = Integer.parseInt(token, 10);
        } catch (NumberFormatException nfe) {
            name = token;
            while (st.hasMoreTokens()) {
                name += st.nextToken();
            }
            return;
        }
        // name segment
        if (st.hasMoreTokens()) {
            name = st.nextToken();
            while (st.hasMoreTokens()) {
                name += st.nextToken();
            }
        }
    }
    
    /**
     * Creates version identifier object from given parts. No validation
     * performed during object instantiation, all values become parts of
     * version identifier as they are.
     * @param aMajor major version number
     * @param aMinor minor version number
     * @param aBuild build number
     * @param aName build name, <code>null</code> value becomes empty string
     */
    public VersionComparer(final int aMajor, final int aMinor, final int aBuild,
            final String aName) {
        major = aMajor;
        minor = aMinor;
        build = aBuild;
        name = (aName == null) ? "" : aName; //$NON-NLS-1$
    }

    /**
     * @return build number
     */
    public int getBuild() {
        return build;
    }

    /**
     * @return major version number
     */
    public int getMajor() {
        return major;
    }

    /**
     * @return minor version number
     */
    public int getMinor() {
        return minor;
    }
    
    /**
     * @return build name
     */
    public String getName() {
        return name;
    }

    /**
     * Compares two version identifiers to see if this one is
     * greater than or equal to the argument.
     * <p>
     * A version identifier is considered to be greater than or equal
     * if its major component is greater than the argument major 
     * component, or the major components are equal and its minor component
     * is greater than the argument minor component, or the
     * major and minor components are equal and its build component is
     * greater than the argument build component, or all components are equal.
     * </p>
     *
     * @param other the other version identifier
     * @return <code>true</code> if this version identifier
     *         is compatible with the given version identifier, and
     *         <code>false</code> otherwise
     */
    public boolean isGreaterOrEqualTo(final VersionComparer other) {
        if (other == null) {
            return false;
        }
        if (major > other.major) {
            return true;
        }
        if ((major == other.major) && (minor > other.minor)) {
            return true;
        }
        if ((major == other.major) && (minor == other.minor)
                && (build > other.build)) {
            return true;
        }
        if ((major == other.major) && (minor == other.minor)
                && (build == other.build)
                && name.equalsIgnoreCase(other.name)) {
            return true;
        }
        return false;
    }

    /**
     * Compares two version identifiers for compatibility.
     * <p>
     * A version identifier is considered to be compatible if its major 
     * component equals to the argument major component, and its minor component
     * is greater than or equal to the argument minor component.
     * If the minor components are equal, than the build component of the
     * version identifier must be greater than or equal to the build component
     * of the argument identifier.
     * </p>
     *
     * @param other the other version identifier
     * @return <code>true</code> if this version identifier
     *         is compatible with the given version identifier, and
     *         <code>false</code> otherwise
     */
    public boolean isCompatibleWith(final VersionComparer other) {
        if (other == null) {
            return false;
        }
        if (major != other.major) {
            return false;
        }
        if (minor > other.minor) {
            return true;
        }
        if (minor < other.minor) {
            return false;
        }
        if (build >= other.build) {
            return true;
        }
        return false;
    }

    /**
     * Compares two version identifiers for equivalency.
     * <p>
     * Two version identifiers are considered to be equivalent if their major 
     * and minor components equal and are at least at the same build level 
     * as the argument.
     * </p>
     *
     * @param other the other version identifier
     * @return <code>true</code> if this version identifier
     *         is equivalent to the given version identifier, and
     *         <code>false</code> otherwise
     */
    public boolean isEquivalentTo(final VersionComparer other) {
        if (other == null) {
            return false;
        }
        if (major != other.major) {
            return false;
        }
        if (minor != other.minor) {
            return false;
        }
        if (build >= other.build) {
            return true;
        }
        return false;
    }

    /**
     * Compares two version identifiers for order using multi-decimal
     * comparison. 
     *
     * @param other the other version identifier
     * @return <code>true</code> if this version identifier
     *         is greater than the given version identifier, and
     *         <code>false</code> otherwise
     */
    public boolean isGreaterThan(final VersionComparer other) {
        if (other == null) {
            return false;
        }
        if (major > other.major) {
            return true;
        }
        if (major < other.major) {
            return false;
        }
        if (minor > other.minor) {
            return true;
        }
        if (minor < other.minor) {
            return false;
        }
        if (build > other.build) {
            return true;
        }
        return false;

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VersionComparer)) {
            return false;
        }
        VersionComparer other = (VersionComparer) obj;
        if ((major != other.major) || (minor != other.minor)
                || (build != other.build)
                || !name.equalsIgnoreCase(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the string representation of this version identifier. 
     * The result satisfies
     * <code>version.equals(new Version(version.toString()))</code>.
     * @return the string representation of this version identifier
     */
    @Override
    public String toString() {
        if (asString == null) {
            asString = "" + major + SEPARATOR + minor + SEPARATOR + build //$NON-NLS-1$
                + (name.length() == 0 ? "" : SEPARATOR + name); //$NON-NLS-1$
        }
        return asString;
    }

    /**
     * @param obj version to compare this instance with
     * @return comparison result
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final VersionComparer obj) {
        if (equals(obj)) {
            return 0;
        }
        if (major != obj.major) {
            return major - obj.major;
        }
        if (minor != obj.minor) {
            return minor - obj.minor;
        }
        if (build != obj.build) {
            return build - obj.build;
        }
        return name.toLowerCase(Locale.ENGLISH).compareTo(
                obj.name.toLowerCase(Locale.ENGLISH));
    }
    
    // Serialization related stuff.
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeUTF(toString());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException {
        parseString(in.readUTF());
    }
}
