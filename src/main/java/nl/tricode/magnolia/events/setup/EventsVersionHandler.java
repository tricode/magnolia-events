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

import com.google.common.collect.Lists;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.MoveNodeTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles installation and updates of the module.
 */
public class EventsVersionHandler extends DefaultModuleVersionHandler {

    private static final String MODULE_NAME = "magnolia-events-module";

    /**
     * Constructor.
     * <p>
     * Here you can register deltas for tasks that need to be run when UPDATING an EXISTING module.
     */
    public EventsVersionHandler() {
        register(DeltaBuilder.update("1.1.2", "Upgrading events module to Magnolia 5.5")
                .addTask(new RemoveNodeTask("Remove old nodes", "/modules/" + MODULE_NAME + "/apps"))
                .addTask(new RemoveNodeTask("Remove old nodes", "/modules/" + MODULE_NAME + "/dialogs"))
        );

        register(DeltaBuilder.update("1.1.4", "Un-nesting events")
                .addTask(new FindAndMoveNestedEventsTask())
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
        final List<Task> tasks = Lists.newArrayList(super.getExtraInstallTasks(installContext));

        return tasks;
    }

}