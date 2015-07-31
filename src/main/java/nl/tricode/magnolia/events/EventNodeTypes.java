/**
 *      Tricode Event module
 *      Is a Event module for Magnolia CMS.
 *      Copyright (C) 2015  Tricode Business Integrators B.V.
 *
 * 	  This program is free software: you can redistribute it and/or modify
 *		  it under the terms of the GNU General Public License as published by
 *		  the Free Software Foundation, either version 3 of the License, or
 *		  (at your option) any later version.
 *
 *		  This program is distributed in the hope that it will be useful,
 *		  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *		  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *		  GNU General Public License for more details.
 *
 *		  You should have received a copy of the GNU General Public License
 *		  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.tricode.magnolia.events;

import info.magnolia.jcr.util.NodeTypes;

/**
 * Created by mvdmark on 26-11-2014.
 */
public class EventNodeTypes {
    /**
     * Represents the nodeType mgnl:eventCalendarItem.
     */
    public static class Event {
        // Node Type Name
        public static final String NAME = NodeTypes.MGNL_PREFIX + "eventCalendarItem";

        // Node Type Folder
        public static final String FOLDER = NodeTypes.MGNL_PREFIX + "eventsFolder";

        // Property Name
        public static final String PROPERTY_EVENTNAME = "eventName";
        public static final String PROPERTY_SUMMARY = "summary";
        public static final String PROPERTY_STARTDATE = "startDate";
        public static final String PROPERTY_DESCRIPTION = "description";
    }
}