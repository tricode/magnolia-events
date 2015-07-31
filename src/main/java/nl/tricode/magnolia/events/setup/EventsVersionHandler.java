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
package nl.tricode.magnolia.events.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.Task;
import info.magnolia.module.model.Version;
import nl.tricode.magnolia.events.setup.task.UpdateModuleBootstrapTask;

import javax.jcr.ImportUUIDBehavior;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is optional and lets you manager the versions of your module,
 * by registering "deltas" to maintain the module's configuration, or other type of content.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 */
public class EventsVersionHandler extends DefaultModuleVersionHandler {
	private final static String MODULE_NAME = "magnolia-events-module";

	@Override
	protected List<Task> getStartupTasks(InstallContext ctx) {
		List<Task> startupTasks = new ArrayList<Task>(0);
		startupTasks.addAll(super.getStartupTasks(ctx));

		startupTasks.addAll(getOptionalTasks(ctx));

		return startupTasks;
	}

	@Override
	protected List<Task> getDefaultUpdateTasks(Version forVersion) {
		final List<Task> tasks = new ArrayList<Task>();
		tasks.addAll(super.getDefaultUpdateTasks(forVersion));

		// Always update templates, resources no matter what version is updated!
		tasks.add(new UpdateModuleBootstrapTask(MODULE_NAME, "dialogs"));

		return tasks;
	}

	/**
	 * Method of installing optional Tasks
	 * @param ctx
	 * @return
	 */
	private List<Task> getOptionalTasks(InstallContext ctx) {
		List<Task> tasks = new ArrayList<Task>(0);

		if (ctx.getHierarchyManager("config").isExist("/modules/tricode-tags")) {
			log.info("Bootstrapping optional Tricode Tags for Tricode Event Calendar");
			tasks.add(new BootstrapSingleResource("Tricode news optional Tags", "Bootstrap the optional tab for Tags", "/mgnl-bootstrap/optional/tricode-tags/config.modules.tricode-events.apps.event-calendar.subApps.detail.editor.form.tabs.tagstab.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING));
		}

		if (ctx.getHierarchyManager("config").isExist("/modules/tricode-categories")) {
			log.info("Bootstrapping optional Tricode Categories for Tricode Event Calendar");
			tasks.add(new BootstrapSingleResource("Tricode news optional Categories", "Bootstrap the optional tab for Categories", "/mgnl-bootstrap/optional/tricode-categories/config.modules.tricode-events.apps.event-calendar.subApps.detail.editor.form.tabs.categoriestab.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING));
		}
		return tasks;
	}
}