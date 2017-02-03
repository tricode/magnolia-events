/*
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
package nl.tricode.magnolia.events.templates;

import com.google.common.collect.Maps;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;
import nl.tricode.magnolia.events.EventNodeTypes;
import nl.tricode.magnolia.events.util.EventsRepositoryConstants;
import nl.tricode.magnolia.events.util.JcrUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EventsRenderableDefinition<RD extends RenderableDefinition> extends RenderingModelImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsRenderableDefinition.class);
    private static final int DEFAULT_LATEST_COUNT = 5;
    private static final String PARAM_CATEGORY = "category";
    private static final String PARAM_AUTHOR = "author";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_YEAR = "year";
    private static final String PARAM_MONTH = "month";
    private static final List<String> WHITELISTED_PARAMETERS = Arrays.asList(PARAM_CATEGORY, PARAM_AUTHOR, PARAM_PAGE, PARAM_YEAR, PARAM_MONTH);

    private final TemplatingFunctions templatingFunctions;
    private final WebContext webContext = MgnlContext.getWebContext();

    private final Map<String, String> filter;

    @Inject
    public EventsRenderableDefinition(Node content,
                                      RD definition,
                                      RenderingModel<?> parent,
                                      TemplatingFunctions templatingFunctions) {
        super(content, definition, parent);
        this.templatingFunctions = templatingFunctions;

        filter = Maps.newHashMap();

        final Iterator<Map.Entry<String, String>> it = MgnlContext.getWebContext().getParameters().entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, String> pairs = it.next();
            if (WHITELISTED_PARAMETERS.contains(pairs.getKey()) && StringUtils.isNotEmpty(pairs.getValue())) {
                filter.put(pairs.getKey(), pairs.getValue());
                LOGGER.debug("Added to filter: {}", pairs.getKey());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    @Override
    public String execute() {
        webContext.getResponse().setHeader("Cache-Control", "no-cache");
        return super.execute();
    }

    /**
     * @param path Start node path in hierarchy
     * @return List of event nodes
     * @throws RepositoryException
     */
    @SuppressWarnings("unused") //Used in freemarker components.
    public List<ContentMap> getEvents(final String path) throws RepositoryException {
        final String query = JcrUtils.buildQuery(path, EventNodeTypes.Event.NAME, false);

        return templatingFunctions.asContentMapList(
                JcrUtils.getWrappedNodesFromQuery(query, DEFAULT_LATEST_COUNT, 1, EventNodeTypes.Event.NAME)
        );
    }

    /**
     * Get categories for given event node
     *
     * @param event ContentMap of event.
     * @return List of category nodes
     */
    @SuppressWarnings("unused") //Used in freemarker components.
    public List<ContentMap> getEventCategories(final ContentMap event) {
        final List<ContentMap> categories = new ArrayList<>(0);

        try {
            final Value[] values = event.getJCRNode().getProperty(EventNodeTypes.Event.PROPERTY_CATEGORIES).getValues();
            if (values != null) {
                for (Value value : values) {
                    categories.add(templatingFunctions.contentById(value.getString(), EventsRepositoryConstants.CATEGORY));
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception while getting categories: {}", e.getMessage());
        }
        return categories;
    }

    /**
     * Get latest nodes of type mgnl:eventCalendarItem.
     *
     * @param path          Start node path in hierarchy
     * @param maxResultSize Number of items to return. When empty <code>5</code> will be used.
     * @param publishedEventsOnly Check if the result list should be filled only with public events
     * @return List of event nodes sorted by date created in descending order
     * @throws RepositoryException
     */
    @SuppressWarnings("unused") //Used in freemarker components.
    public List<ContentMap> getLatestEvents(String path, String maxResultSize, boolean publishedEventsOnly) throws RepositoryException {
        return getLatest(path, maxResultSize, EventNodeTypes.Event.NAME, getPageNumber(), EventNodeTypes.Event.NAME, publishedEventsOnly);
    }

    /**
     * @param path          Repository path
     * @param maxResultSize the result size that is returned
     * @param categoryUuid  the category uuid to take only the events from this category
     * @return a list of event nodes sorted by date created in descending order for the specified maxResultSize parameter
     * @throws RepositoryException
     */
    @SuppressWarnings("unused") //Used in freemarker components.
    public List<ContentMap> getLatestEvents(String path, String maxResultSize, String categoryUuid) throws RepositoryException {
        int resultSize = DEFAULT_LATEST_COUNT;
        if (StringUtils.isNumeric(maxResultSize)) {
            resultSize = Integer.parseInt(maxResultSize);
        }
        StringBuilder queryString = formQueryString(new StringBuilder(), categoryUuid);
        return templatingFunctions.asContentMapList(JcrUtils.getWrappedNodesFromQuery(
                "SELECT p.* from [mgnl:event] AS p WHERE ISDESCENDANTNODE(p,'/') AND CONTAINS(p.categories, '" +
                        categoryUuid + "') " + queryString + " ORDER BY p.[mgnl:created] desc",
                resultSize, 1, EventNodeTypes.Event.NAME));
    }

    @SuppressWarnings("unused") //Used in freemarker components.
    public int getPageNumber() {
        int pageNumber = 1;
        if (filter.containsKey(PARAM_PAGE)) {
            pageNumber = Integer.parseInt(filter.get(PARAM_PAGE));
        }
        return pageNumber;
    }

    /**
     * Forms a query string like this "OR CONTAINS(p.categories, '"uuid"')" and appends it to each other.
     *
     * @param query        a new StringBuilder to keep the content on recursive calls
     * @param categoryUuid the uuid of the category
     * @return a query string used to filter the blogs by categories
     * @throws RepositoryException
     */
    private StringBuilder formQueryString(StringBuilder query, String categoryUuid) throws RepositoryException {
        List<ContentMap> childCategories = templatingFunctions.children(templatingFunctions.contentById(categoryUuid, EventsRepositoryConstants.CATEGORY));

        for (ContentMap childCategory : childCategories) {
            if (!templatingFunctions.children(childCategory).isEmpty()) {
                formQueryString(query, childCategory.getJCRNode().getIdentifier());
            }
            query.append("OR CONTAINS(p.categories, '").append(childCategory.getJCRNode().getIdentifier()).append("') ");
        }
        return query;
    }

    private List<ContentMap> getLatest(String path, String maxResultSize, String nodeType, int pageNumber, String nodeTypeName, boolean publishedEventsOnly) throws RepositoryException {
        int resultSize = DEFAULT_LATEST_COUNT;
        if (StringUtils.isNumeric(maxResultSize)) {
            resultSize = Integer.parseInt(maxResultSize);
        }
        final String sqlBlogItems = JcrUtils.buildQuery(path, nodeType, publishedEventsOnly);
        return templatingFunctions.asContentMapList(JcrUtils.getWrappedNodesFromQuery(sqlBlogItems, resultSize, pageNumber, nodeTypeName));
    }

}