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
package nl.tricode.magnolia.events.templates;

import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;
import nl.tricode.magnolia.events.util.JcrUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.*;

public class EventsRenderableDefinition<RD extends RenderableDefinition> extends RenderingModelImpl {
	private final String EVENT_NODE_TYPE = "mgnl:eventCalendarItem";
	private static final String PARAM_PAGE = "page";
	private static final int DEFAULT_LATEST_COUNT = 5;
	private final Map<String, String> filter;
	private final WebContext webContext = MgnlContext.getWebContext();

	protected final TemplatingFunctions templatingFunctions;

	@Inject
	public EventsRenderableDefinition(Node content, RD definition, RenderingModel<?> parent, TemplatingFunctions templatingFunctions) {
		super(content, definition, parent);
		this.templatingFunctions = templatingFunctions;
		filter = new HashMap<>();
	}

	@Override
	public String execute() {
		webContext.getResponse().setHeader("Cache-Control", "no-cache");
		return super.execute();
	}

	public List<ContentMap> getLatestEvents(String path, String maxResultSize) throws RepositoryException {
		return getLatest(path, maxResultSize, EVENT_NODE_TYPE, getPageNumber(), EVENT_NODE_TYPE);
	}

	private int getPageNumber() {
		int pageNumber = 1;
		if (filter.containsKey(PARAM_PAGE)) {
			pageNumber = Integer.parseInt(filter.get(PARAM_PAGE));
		}
		return pageNumber;
	}

	public TemplatingFunctions getTemplatingFunctions() {
		return templatingFunctions;
	}

	public List<ContentMap> getLatest(String path, String maxResultSize, String nodeType, int pageNumber, String nodeTypeName) throws RepositoryException {
		int resultSize = DEFAULT_LATEST_COUNT;
		if (StringUtils.isNumeric(maxResultSize)) {
			resultSize = Integer.parseInt(maxResultSize);
		}
		final String sqlBlogItems = JcrUtils.buildQuery(path, nodeType);
		return templatingFunctions.asContentMapList(JcrUtils.getWrappedNodesFromQuery(sqlBlogItems, resultSize, pageNumber, nodeTypeName));
	}
}