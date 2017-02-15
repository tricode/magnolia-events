/*
 * Tricode Event module
 * Is a Event module for Magnolia CMS.
 * Copyright (C) 2015  Tricode Business Integrators B.V.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.tricode.magnolia.events.util;

import com.google.common.collect.Lists;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class EventsJcrUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsJcrUtils.class);

    private EventsJcrUtils() {
        // Util class, prevent instantiating
    }

    /**
     * Query event items using JCR SQL2 syntax.
     *
     * @param query         Query string
     * @param maxResultSize Max results returned
     * @param pageNumber    paging number
     * @param nodeTypeName  Name of the node type
     * @return List of event nodes
     * @throws javax.jcr.RepositoryException In case of query error
     */
    public static List<Node> getWrappedNodesFromQuery(final String query,
                                                      final int maxResultSize,
                                                      final int pageNumber,
                                                      final String nodeTypeName)
            throws RepositoryException {
        final NodeIterator items = QueryUtil.search(EventsRepositoryConstants.COLLABORATION, query, Query.JCR_SQL2, nodeTypeName);

        // Paging result set
        final int startRow = (maxResultSize * (pageNumber - 1));
        if (startRow > 0) {
            try {
                items.skip(startRow);
            } catch (NoSuchElementException e) {
                LOGGER.error("No more events found beyond this item number: {}", startRow);
            }
        }

        int count = 1;
        final List<Node> itemsListPaged = Lists.newArrayListWithCapacity(0);
        while (items.hasNext() && count <= maxResultSize) {
            itemsListPaged.add(new I18nNodeWrapper(items.nextNode()));
            count++;
        }
        return itemsListPaged;
    }

    public static List<Node> getWrappedNodesFromQuery(final String query,
                                                      final String nodeTypeName,
                                                      final String workspace)
            throws RepositoryException {
        final NodeIterator items = QueryUtil.search(workspace, query, Query.JCR_SQL2, nodeTypeName);

        final List<Node> itemsListPaged = Lists.newArrayListWithCapacity(0);
        while (items.hasNext()) {
            itemsListPaged.add(new I18nNodeWrapper(items.nextNode()));
        }
        return itemsListPaged;
    }

    public static String buildQuery(String path, String contentType, boolean publishedEventsOnly) {
        final StringBuilder query = new StringBuilder("SELECT p.* FROM [")
                .append(contentType).append("] AS p ")
                .append("WHERE ISDESCENDANTNODE(p, '")
                .append(StringUtils.defaultIfEmpty(path, "/"))
                .append("') ");

        if (publishedEventsOnly) {
            query.append("AND ( p.unpublishDate = '' OR ( p.unpublishDate <> '' AND p.unpublishDate > CAST('")
                    .append(LocalDateTime.now()).append("' AS DATE)))");
        }

        query.append("ORDER BY p.[mgnl:created] desc");

        LOGGER.debug("BuildQuery: {}", query.toString());
        return query.toString();
    }

}