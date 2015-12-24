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
package nl.tricode.magnolia.events.action;

import info.magnolia.commands.CommandsManager;
import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.location.LocationController;
import info.magnolia.ui.contentapp.browser.action.RestoreItemPreviousVersionAction;
import info.magnolia.ui.contentapp.browser.action.RestoreItemPreviousVersionActionDefinition;
import info.magnolia.ui.contentapp.detail.DetailLocation;
import info.magnolia.ui.contentapp.detail.DetailView;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import nl.tricode.magnolia.events.EventNodeTypes;

import javax.inject.Named;
import javax.jcr.Node;
import java.util.List;

/**
 * @author mvdmark
 */
public class RestorePreviousEvents extends RestoreItemPreviousVersionAction {
    private final LocationController locationController;

	 @SuppressWarnings("unused") //Constructor called by Magnolia.
    public RestorePreviousEvents(RestoreItemPreviousVersionActionDefinition definition, JcrItemAdapter item,
                               CommandsManager commandsManager, @Named("admincentral") EventBus eventBus, UiContext uiContext,
                               SimpleTranslator i18n, LocationController locationController) {

        super(definition, item, commandsManager, eventBus, uiContext, i18n);

        this.locationController = locationController;
    }

	 @SuppressWarnings("unused") //Constructor called by Magnolia.
    public RestorePreviousEvents(RestoreItemPreviousVersionActionDefinition definition, List<JcrItemAdapter> items,
                               CommandsManager commandsManager, @Named("admincentral") EventBus eventBus, UiContext uiContext,
                               SimpleTranslator i18n, LocationController locationController) {

        super(definition, items, commandsManager, eventBus, uiContext, i18n);

        this.locationController = locationController;
    }

    @Override
    protected void onPostExecute() throws Exception {
        super.onPostExecute();

        Node node = (Node) getCurrentItem().getJcrItem();

        boolean restoreMultiple = getItems().size() > 1 || NodeUtil.getNodes(node, EventNodeTypes.Event.NAME).iterator().hasNext();

        if (!restoreMultiple) {
            DetailLocation location = new DetailLocation("event-calendar", "detail", DetailView.ViewType.EDIT, node.getPath(), "");
            locationController.goTo(location);
        }
    }
}