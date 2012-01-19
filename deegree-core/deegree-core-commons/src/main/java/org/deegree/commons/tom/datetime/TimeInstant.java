//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.commons.tom.datetime;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Base class for temporal primitives that represent a point in time.
 * <p>
 * A {@link TimeInstant} is a thin wrapper around {@link Calendar} that tracks whether the {@link TimeInstant} has been
 * created with our without explicit time zone information. If it has been constructed without an explicit time zone,
 * the default time zone ({@link TimeZone#getDefault()}) is used for the underlying calendar.
 * </p>
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public abstract class TimeInstant implements Comparable<TimeInstant> {

    protected final Calendar cal;

    protected final boolean isLocal;

    /**
     * Creates a new {@link TimeInstant} instance.
     * 
     * @param time
     * @param tz
     */
    TimeInstant( java.util.Date time, TimeZone tz ) {
        Calendar cal = null;
        if ( tz != null ) {
            cal = Calendar.getInstance( tz );
            isLocal = false;
        } else {
            cal = Calendar.getInstance();
            isLocal = true;
        }
        this.cal = cal;
        cal.setTime( time );
    }

    /**
     * Creates a new {@link TimeInstant} instance.
     * 
     * @param cal
     *            calendar
     * @param tzOffset
     *            time zone offset relative to GMT in milliseconds, may be <code>null</code> (unknown time zone)
     */
    TimeInstant( Calendar cal, boolean isLocal ) {
        this.cal = cal;
        this.isLocal = isLocal;
    }

    protected static boolean isLocal( String s ) {
        if ( s.endsWith( "Z" ) ) {
            return false;
        }
        int len = s.length();
        if ( len < 6 ) {
            return true;
        }
        if ( s.charAt( len - 3 ) == ':' && ( s.charAt( len - 6 ) == '-' ) || ( s.charAt( len - 6 ) == '+' ) ) {
            return false;
        }
        return true;
    }

    /**
     * Returns this time instant as a {@link Calendar}.
     * 
     * @return calendar, never <code>null</code>
     */
    public Calendar getCalendar() {
        return cal;
    }

    /**
     * Returns this time instant as a {@link Date}.
     * <p>
     * The actually returned subtype depends on the subtype of {@link TimeInstant}.
     * </p>
     * 
     * @return calendar, never <code>null</code>
     */
    public abstract Date getSQLDate();

    /**
     * Returns this time instant in milliseconds.
     * 
     * @return time instant as UTC milliseconds from the epoch
     */
    public long getTimeInMilliseconds() {
        return cal.getTimeInMillis();
    }

    /**
     * Returns whether this time instant has been created without explicit time zone information.
     * 
     * @return <code>true</code>, if this time instant has been created without explicit time zone, <code>false</code>
     *         otherwise
     */
    public boolean isLocal() {
        return isLocal;
    }

    /**
     * Creates a new {@link Date} instance from the given <code>xs:date</code>, <code>xs:dateTime</code> or
     * <code>xs:Time</code> encoded value.
     * 
     * @param xsValue
     * @return
     * @throws IllegalArgumentException
     */
    public static TimeInstant valueOf( String xsValue )
                            throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean equals( Object o ) {
        if ( !( o instanceof TimeInstant ) ) {
            return false;
        }
        TimeInstant that = (TimeInstant) o;
        return this.cal.equals( that.cal );
    }

    @Override
    public int compareTo( TimeInstant that ) {
        return this.cal.compareTo( that.cal );
    }
}