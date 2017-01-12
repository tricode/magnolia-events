[#if (!ctx.parameters.name?has_content)]
    [#if (content.eventGroup?has_content) ]
        [#assign eventsGroupPath = cmsfn.contentById(content.eventGroup, "collaboration").@path /]
    [/#if]

    [#assign events = model.getEvents(eventsGroupPath!"/") /]

    [#if (events)?size > 0 ]
        [#assign pageLink = cmsfn.link(cmsfn.page(content)) /]

        <div class="event-summaries">
            [#list events as event]
                <article class="event-item">
                    <h2>${event.eventName!"No event name found"}</h2>

                    [#assign categories = model.getEventCategories(event) /]
                    [#if (categories)?size > 0 ]
                        <div class="postdetails">
                            <ul class="list-unstyled inline-list event-categories">
                                [#list categories as category]
                                    <li>
                                        <a href="${pageLink}?category=${category.@path}">
                                            <i class="fa fa-star">${category.@name}</i>
                                        </a>
                                    </li>
                                [/#list]
                            </ul>
                        </div>
                    [/#if]

                    <section>
                        ${event.summary!}
                    </section>

                    <div class="postdetails">
                        <ul class="list-unstyled inline-list event-info">
                            <li><i class="fa fa-calendar">${event.startDate}</i></li>
                            [#if event.performer?has_content]
                                [#assign performer = cmsfn.contentById(event.performer, "contacts" ) /]
                                <li><i class="fa fa-pencil">${performer.firstName} ${performer.lastName}</i></li>
                            [/#if]
                        </ul>
                    </div>
                </article>
            [/#list]
        </div>
    [#else]
        <div class="event-summaries">
            <p>No events available</p>
        </div>
    [/#if]
[/#if]