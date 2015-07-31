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
package nl.tricode.magnolia.events.field;

import com.vaadin.ui.Table;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.workbench.column.AbstractColumnFormatter;
import info.magnolia.ui.workbench.column.definition.PropertyColumnDefinition;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class DurationColumnFormatter extends AbstractColumnFormatter<PropertyColumnDefinition> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(DurationColumnFormatter.class);

    public DurationColumnFormatter(PropertyColumnDefinition definition) {
        super(definition);
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        final Item jcrItem = getJcrItem(source, itemId);
        if (jcrItem != null && jcrItem.isNode()) {
            final Node node = (Node) jcrItem;

            try {
                if (NodeUtil.isNodeType(node, "mgnl:eventCalendarItem")) {
                	final DateTime startDate = new DateTime(PropertyUtil.getDate(node, "startDate"));
                	final DateTime endDate = new DateTime(PropertyUtil.getDate(node, "endDate"));
                    
                	if (startDate != null && endDate != null) {
                		PeriodFormatter formatter = new PeriodFormatterBuilder()
                					.appendMonths()
                					.appendSuffix(" month", " months")
                					.appendWeeks()
                					.appendSuffix(" week", " weeks")
                	     			.appendDays()
                	     			.appendSuffix(" day", " days")
                	     			.appendHours()
                	     			.appendSuffix(" hour", " hours")
                	     			.appendMinutes()
                	     			.appendSuffix(" minute", " minutes")
                	     			.appendSeconds()
                	     			.appendSuffix(" second", " seconds")
                	     			.toFormatter();
                		Period period = new Period(startDate, endDate);
                		
                		String formatted = formatter.print(period);
                		return formatted;
                	}
                }
            } catch (RepositoryException e) {
                log.warn("Unable to get duration based on startDate and endDate properties", e);
            }
        }
        return "N/A";
    }
}