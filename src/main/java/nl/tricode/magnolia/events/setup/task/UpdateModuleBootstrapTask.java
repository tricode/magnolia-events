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
package nl.tricode.magnolia.events.setup.task;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapResourcesTask;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.ImportUUIDBehavior;

public class UpdateModuleBootstrapTask extends BootstrapResourcesTask {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateModuleBootstrapTask.class);

	protected String modulename;
	protected String[] resources;

	/**
	 * Default constructor checking for standard resource paths "dialogs" and "templates" to update
	 *
	 * @param modulename name of the module.
	 */
	public UpdateModuleBootstrapTask(String modulename) {
		super("Bootstrap", "Replacing configuration for " + modulename + "", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
		this.modulename = modulename;
		this.resources = new String[]{"dialogs","templates"};
	}

	/**
	 * Default constructor checking for given resource paths to update
	 *
	 * @param modulename name of the module.
	 * @param resources Comma separated string of resources paths to check "dialogs, templates"
	 */
	public UpdateModuleBootstrapTask(String modulename, String resources) {
		super("Bootstrap", "Replacing configuration for " + modulename + "", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
		this.modulename = modulename;
		this.resources = StringUtils.split(resources, ",");
	}

	protected boolean acceptResource(InstallContext ctx, String resourceName) {
		for (String resource : resources) {
			boolean retval = resourceName.startsWith("/mgnl-bootstrap/" + modulename + "/" + resource + "/") && resourceName.endsWith(".xml");
			if (retval) {
				LOG.debug("Replacing configuration with " + resourceName);
				return true;
			}
		}

		LOG.debug("NOT replacing resource configuration " + resourceName);
		return false;
	}
}