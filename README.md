# EventTracker

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app will let users save and sign up for events that they are interested in. The app will then store the data of events users sign up for in order to make this information available offline. The app will also let users create events and publish them.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category: Planning**
- **Mobile: Can view and post event listings and save events with offline details**
- **Story: Users can find and share events with each other in a centralized way**
- **Market: Individuals looking for interesting events and organizers looking to create interest in their events**
- **Habit: Users will use this when they are looking for something new or interesting to do. They will also use this app when they want to look up details of events they previously expressed interest in, typically around the time of those events**
- **Scope: Initially the app will support event sharing and saving. However, if there is time the app could also support following organizers, looking at who else is subscribed to events, and commenting on event pages**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users must be able to access accounts
    * Sign up
    * Sign in
    * Sign out
* Users can see a list of events others have created
* Users can see a details view for individual events
* Users can create, tag, and publish events
* Users can select events that they would like to save
    * Users can subscribe and unsubscribe
    * Users can access these events offline
* Users can search events by tag
* Users can view their saved events through a menu bar item
* Users can view their saved events offline

**Optional Nice-to-have Stories**

* Users can view details about event organizers
* Users can comment on event pages
* Users can see who else subscribes to an event
* Users can see trending events
* Users can see their events in a calendar-like view
* Users can delete events they created
* ...

### 2. Screen Archetypes

* Login screen
   * Users can log into the app
* Registration screen
    * Users can create a new account
* Timeline screen
   * Users can see a list of event listings
   * Users can scroll through listings
   * Users can subscribe to events
* Compose screen
    * Users can fill in required fields about event information to create a posting
* EventDetails screen
    * Users can see the date, time, and location of the event
    * Users can see a brief description of the event from the organizer
* PersonalEvents screen
    * Users can see a list of events they have subscribed to
    * Users can remove events from the personal events list

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Timeline
* PersonalEvents
* Compose

**Flow Navigation** (Screen to Screen)

* Login
   * Registration
   * Timeline
* Timeline
   * EventDetails
* Compose
    * Timeline

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://github.com/jaumep1/EventTracker/blob/main/IMG_9729.png" width=600>

## Schema 
[This section will be completed in Unit 9]
### Models
- Event
- User
### Networking
- Timeline:
    - Google Calendar API for geting master list of all events created and posting new events
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
