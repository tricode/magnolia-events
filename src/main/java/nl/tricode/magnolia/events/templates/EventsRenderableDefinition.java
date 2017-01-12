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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.List;

public class EventsRenderableDefinition<RD extends RenderableDefinition> extends RenderingModelImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsRenderableDefinition.class);
    private static final int DEFAULT_LATEST_COUNT = 5;

    private final TemplatingFunctions templatingFunctions;
    private final WebContext webContext = MgnlContext.getWebContext();

    @Inject
    public EventsRenderableDefinition(Node content,
                                      RD definition,
                                      RenderingModel<?> parent,
                                      TemplatingFunctions templatingFunctions) {
        super(content, definition, parent);
        this.templatingFunctions = templatingFunctions;
    }

    @Override
    public String execute() {
        webContext.getResponse().setHeader("Cache-Control", "no-cache");
        return super.execute();
    }

    /**
     * @param path
     * @return
     * @throws RepositoryException
     */
    @SuppressWarnings("unused") //Used in freemarker components.
    public List<ContentMap> getEvents(final String path) throws RepositoryException {
        final String query = JcrUtils.buildQuery(path, EventNodeTypes.Event.NAME);

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

}