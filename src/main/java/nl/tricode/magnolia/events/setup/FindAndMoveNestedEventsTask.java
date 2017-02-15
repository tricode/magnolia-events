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
package nl.tricode.magnolia.events.setup;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.MoveNodeTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import nl.tricode.magnolia.events.EventNodeTypes;
import nl.tricode.magnolia.events.util.EventsRepositoryConstants;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;

/**
 * Due to a configuration error in this component, it was possible (until now) to create nested events within events.
 * This is not the designed behavior and therefore we chose to correct it. The component configuration has been fixed
 * and this task will seek out any nested event nodes and move them to the parent level.
 */
public class FindAndMoveNestedEventsTask implements Task {

    @Override
    public String getName() {
        return "Find and move nested events";
    }

    @Override
    public String getDescription() {
        return "Events were nested by accident, this task will fix that";
    }

    @Override
    public void execute(final InstallContext installContext) throws TaskExecutionException {
        try {
            final Session session = MgnlContext.getJCRSession(EventsRepositoryConstants.COLLABORATION);

            final Node rootNode = session.getRootNode();
            final int numberOfMovedNodes = processEvents(installContext, rootNode, 0);

            if (numberOfMovedNodes > 0) {
                installContext.info("Nested events were found and have been un-nested.");
                installContext.info("Note: If present, duplicate events now have a name that ends with '-duplicate'.");
                installContext.info("Please check all events and re-publish if needed.");
            }
        } catch (RepositoryException e) {
            throw new TaskExecutionException("Error while un-nesting event nodes", e);
        }
    }

    private static int processEvents(final InstallContext installContext, final Node rootNode, int numberOfMovedNodes)
            throws RepositoryException, TaskExecutionException {
        final List<Node> allEventNodes = NodeUtil.asList(NodeUtil.getNodes(rootNode, EventNodeTypes.Event.NAME));

        for (Node eventNode : allEventNodes) {
            if (eventNode.hasNodes()) {
                final NodeIterator subNodesIterator = eventNode.getNodes();

                while (subNodesIterator.hasNext()) {
                    final Node subNode = subNodesIterator.nextNode();

                    if ("image".equalsIgnoreCase(subNode.getName())) {
                        continue;
                    }

                    // Found a nested event node, let's move it to the rootNode
                    String targetPath = determineTargetPath(rootNode, subNode);

                    if (rootNode.getSession().nodeExists(targetPath)) {
                        // Target node exists! Do not overwrite it, but rename the old node before moving it
                        targetPath += "-duplicate";
                    }

                    // Moving node
                    new MoveNodeTask(
                            "Moving event node",
                            "Moving node '" + subNode.getName() + "' to: " + rootNode.getPath(),
                            EventsRepositoryConstants.COLLABORATION,
                            subNode.getPath(),
                            targetPath,
                            false
                    ).execute(installContext);

                    numberOfMovedNodes++;
                }
            }
        }

        // ALso check sub folders (if there are any)
        final List<Node> allFolderNodes = NodeUtil.asList(NodeUtil.getNodes(rootNode, EventNodeTypes.Folder.NAME));

        for (Node folderNode : allFolderNodes) {
            numberOfMovedNodes += processEvents(installContext, folderNode, numberOfMovedNodes);
        }

        return numberOfMovedNodes;
    }

    private static String determineTargetPath(final Node rootNode, final Node subNode) throws RepositoryException {
        final int lastSlashIndex = subNode.getPath().lastIndexOf("/");

        if ("/".equals(rootNode.getPath())) {
            // Prevent double '/'
            return subNode.getPath().substring(lastSlashIndex);
        } else {
            return rootNode.getPath() + subNode.getPath().substring(lastSlashIndex);
        }
    }

}