# magnolia-events
Event app for [Magnolia cms](http://www.magnolia-cms.com).

In this module we provide a content app to create/edit/delete events fast and easy. The events can be unpublished automatically by choosing a unpublish date at the moment you create the event.

Create your own website components with nl.tricode.magnolia.events.templates.EventsRenderableDefinition
to add to your Magnolia website.

## Prerequisites
* [git](http://git-scm.com/)
* [java 8](http://java.com)
* [Maven 3](http://maven.apache.org)

##License
Copyright (c) 2015 Tricode and contributors. Released under a [GNUv3 license](https://github.com/tricode/magnolia-events/blob/master/license.txt).


##Release notes 1.1.1
* Update to Java 7.
* Update to Magnolia 5.4.3.
* Update Magnolia scheduler module 2.2.2.
* Add IoC for syndicator in scheduled job 'Deactivate published events'
* Separation of Jcr queries.

##Release notes 1.1.2
* Update to Magnolia 5.5
* Update to Java 8

##Release notes 1.1.3
* JavaDoc fixes

##Release notes 1.1.4
* Prevent nesting of events within events (+ un-nest any incorrect encountered)
* Fix 'rename event folder' dialog
* Fix labels