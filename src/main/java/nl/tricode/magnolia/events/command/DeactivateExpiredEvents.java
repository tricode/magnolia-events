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
package nl.tricode.magnolia.events.command;

import info.magnolia.cms.exchange.ExchangeException;
import info.magnolia.cms.exchange.Syndicator;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.Rule;
import info.magnolia.commands.impl.BaseRepositoryCommand;
import info.magnolia.context.Context;
import nl.tricode.magnolia.events.EventNodeTypes;
import nl.tricode.magnolia.events.util.EventsJcrUtils;
import nl.tricode.magnolia.events.util.EventsRepositoryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;

public class DeactivateExpiredEvents extends BaseRepositoryCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateExpiredEvents.class);
    private static final String DEACTIVATE_PROPERTY = "unpublishDate";

    private Syndicator syndicator;

    @Inject
    public DeactivateExpiredEvents(Syndicator syndicator) {
        this.syndicator = syndicator;
    }

    @Override
    public boolean execute(Context context) {
        try {
            // Get a list of all expired event nodes
            List<Node> expiredNodes = EventsJcrUtils.getWrappedNodesFromQuery(
                    EventsJcrUtils.buildQuery(EventNodeTypes.Event.NAME, DEACTIVATE_PROPERTY, false), EventNodeTypes.Event.NAME, EventsRepositoryConstants.COLLABORATION);
            LOGGER.debug("eventNodes size [{}].", expiredNodes.size());

            // Unpublish expired nodes.
            unpublishExpiredNodes(context, expiredNodes);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    private void unpublishExpiredNodes(Context context, List<Node> expiredNodes) {
        // Syndicator init method still needed because there is no other way to set user and workspace.
        // Magnolia does the same in their activation module.
        syndicator.init(context.getUser(), this.getRepository(), EventsRepositoryConstants.COLLABORATION, new Rule());

        try {
            // Looping the nodes to unpublish
            for (Node expiredNode : expiredNodes) {
                // Saving the removal of the propery on the session because on the node is deprecated.
                syndicator.deactivate(ContentUtil.asContent(expiredNode));

                LOGGER.debug("Node [" + expiredNode.getName() + "  " + expiredNode.getPath() + "] unpublished.");
            }
        } catch (RepositoryException | ExchangeException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}