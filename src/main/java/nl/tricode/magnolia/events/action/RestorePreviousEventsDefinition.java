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

import info.magnolia.ui.contentapp.browser.action.RestoreItemPreviousVersionActionDefinition;

/**
 * Created by mvdmark on 25-11-2014.
 */
public class RestorePreviousEventsDefinition extends RestoreItemPreviousVersionActionDefinition {
    private boolean showPreview = true;

    public RestorePreviousEventsDefinition() {
        setImplementationClass(RestorePreviousEvents.class);
    }

    public RestorePreviousEventsDefinition(boolean showPreview) {
        this.showPreview = showPreview;
        setImplementationClass(RestorePreviousEvents.class);
    }

    public boolean isShowPreview() {
        return showPreview;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }
}