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
package nl.tricode.magnolia.events.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.ImportUUIDBehavior;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles installation and updates of the module.
 */
public class EventsVersionHandler extends DefaultModuleVersionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsVersionHandler.class);
    private static final String MODULE_NAME = "magnolia-events-module";

    /**
     * Constructor.
     * <p/>
     * Here you can register deltas for tasks that need to be run when UPDATING an EXISTING module.
     */
    public EventsVersionHandler() {
        register(DeltaBuilder.update("1.1.2", "Upgrading blog module to Magnolia 5.5")
                .addTask(new RemoveNodeTask("Remove old nodes", "/modules/" + MODULE_NAME + "/apps"))
                .addTask(new RemoveNodeTask("Remove old nodes", "/modules/" + MODULE_NAME + "/dialogs"))
        );
    }

    /**
     * Override this method when defining tasks that need to be executed when INITIALLY INSTALLING the module.
     *
     * @param installContext Context of the install, can be used to display messages
     * @return A list of tasks to execute on initial install
     */
    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        final List<Task> tasks = new ArrayList<>();
        tasks.addAll(super.getExtraInstallTasks(installContext));

        if (installContext.getHierarchyManager("config").isExist("/modules/tricode-tags")) {
            LOGGER.info("Bootstrapping optional Tricode Tags for Tricode Event Calendar");
            tasks.add(new BootstrapSingleResource("Tricode news optional Tags", "Bootstrap the optional tab for Tags", "/mgnl-bootstrap/optional/tricode-tags/config.modules.tricode-events.apps.event-calendar.subApps.detail.editor.form.tabs.tagstab.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING));
        }

        if (installContext.getHierarchyManager("config").isExist("/modules/tricode-categories")) {
            LOGGER.info("Bootstrapping optional Tricode Categories for Tricode Event Calendar");
            tasks.add(new BootstrapSingleResource("Tricode news optional Categories", "Bootstrap the optional tab for Categories", "/mgnl-bootstrap/optional/tricode-categories/config.modules.tricode-events.apps.event-calendar.subApps.detail.editor.form.tabs.categoriestab.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING));
        }

        return tasks;
    }

}