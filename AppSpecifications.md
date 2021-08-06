# PlanIT

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app will let users save and sign up for events that they are interested in. The app will also let users create their own events, including uploading event posters. Users will also be able to see a details view of events, in which they can see who is going and export the event to their Google Calendar. Users will also be able to see events offline as long as they have been loaded before disconnecting from a network. Lastly, users will have their own profile of events they created and will be able to see the events that any other user has created or is going to.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category: Networking**
- **Mobile: Can view and post event listings, see details of event listings, and see what events others are making/attending**
- **Story: Users can find and share events with each other in a centralized way**
- **Market: Individuals looking for interesting events and organizers looking to find others interest in their events**
- **Habit: Users will use this when they are looking for something new or interesting to do. They will also use this app when they want to look up details of events they previously expressed interest in, typically around the time of those events**
- **Scope: Initially the app will support event sharing and subscribing. The app will also support looking at who else is subscribed to events and exporting the events to Google Calendar. Additionally, the app will support browsing events and event details offline. If there is time the app could also support subscribing to organizers for when they make new events and commenting on event pages**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* [x] Users must be able to access accounts
    * [x] Sign up
    * [x] Sign in
    * [x] Sign out
* [x] Users can see a list of events others have created
* [x] Users can see a details view for individual events
* [x] Users can create, tag, and publish events
* [x] Users can select events that they would like to save
    * [x] Users can subscribe and unsubscribe
* [x] Users can search events
    * [x] Search events by query
    * [x] Filter feed by tag
* [x] Users can view their saved events through a tab bar item
* [x] Users can export events to calendar
* [x] Users can swipe to refresh the events feed 

**Optional Nice-to-have Stories**

* [x] View orients to landscape and portrait mode
* [x] Users can view their own profile page
    * [x] Users can set/change their profile image
* [x] Users can view details about event organizers
* [x] Users can see who else subscribes to an event
* [x] Users can view details about event attendees
* [x] Users can swipe to navigate across views
* [x] Event data is cached across sessions so that it only reloads upon creating the main activity and refreshing the feed
* [x] Users can delete events they created
* [x] Users can browse events and their subscriptions offline
* [x] Users can see a curated feed events (mix of newness and chronology)
* [x] Users can load events using infinite scrolling pagination
* [ ] Users can recover their passwords securely
* [ ] Users can get directions to events
* [ ] Users can comment on event pages
* [ ] Users can sign in with Facebook
* ...

### 2. Screen Archetypes

* Login screen
    * Users can log into the app
    * Users can navigate to the Signup screen
* Signup screen
    * Users can create a new account
* EventsFeed screen
    * Users can see a list of event listings
    * Users can scroll through listings
    * Users can search/sort events
* Compose screen
    * Users can fill in required fields about event information to create a posting
    * Users can add images to events from their Google Drive
* EventDetails screen
    * Users can see the date, time, and location of the event
    * Users can see a brief description of the event from the organizer
    * Users can add the event to their Google Calendar
    * Users can subscribe/unsubscribe from events
* PersonalEvents screen
    * Users can see a list of events they have subscribed to
    * Users can remove events from their personal events list
* PersonalProfile screen
    * Users can see their own profile
    * Users can set/change their profile image
    * Users can see the events that they have created
* UserProfile screen
    * Users can see others' profiles
    * Users can see events that they have created and subscribed to
* ImagePicker
    * User can select an image from Google Drive to upload with an event

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* EventsFeed
* PersonalEvents
* Compose
* PersonalProfile

**Flow Navigation** (Screen to Screen)

* Login
   * Signup
   * Timeline
* Signup
    * Login
    * Timeline
* Timeline
   * EventDetails
* Personal Events
   * EventDetails
* Compose
    * Timeline
    * Image Picker
* Image Picker
    * Compose

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://github.com/jaumep1/EventTracker/blob/main/IMG_9729.png?raw=true" width=600>

## Schema 
[This section will be completed in Unit 9]
### Models
- Event
    - String objectId
    - String createdAt
    - String createdBy
    - String eventTime
    - String eventName
    - String eventDescription
    - File eventPoster
    - ArrayList\<Tag\> tags
    - ArrayList\<User\> attendees
- User
    - String objectId
    - String name
    - String password
    - File profileImage
    - ArrayList\<Event\> subscriptions
- Tag
    - String tag
### Networking
- Timeline:
    - (Read/GET) Query most recent events in chronological order of creation
       ```
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                //Do stuff 
            }
        });
        ```
- Compose
    - (Create/POST) Create a new event object
        ```
        Event e = new Event(); //Make sure Event is a Parse class
        //Add event details
        
        e.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG ,"Error while saving post", e);
                }
                Log.i(TAG, "Post save was successful!");
            }
        });
        ```
        
    - Use Google Drive API to attach images to events (see Google Drive API documentation here: https://developers.google.com/drive)
- Personal Events
    - (Read/GET) Query most events a user is subscribed to in chronological order
        ```
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereContainedIn("objectId", ParseUser.getCurrenParseUser().subscriptions)
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                //Do stuff 
            }
        });
        ```
    - (Delete) Delete a subscription to an event
        ```
        ParseUser user = ParseUser.getCurrentParseUser();
        user.unsubscribe(event.objectId);
        ```
- EventDetails
     - (Create/POST) Create a new subscription to an event
        ```
        ParseUser user = (User) ParseUser.getCurrentParseUser();
        user.subscribe(event);
        event.subscribe(user);
        
        ```
    - (Delete) Delete a subscription to an event
        ```
        ParseUser user = (User) ParseUser.getCurrentParseUser();
        user.unsubscribe(event);
        event.unsubscribe(user);
         ```
    - Use Google Calendar API to export events (see Google Calendar API documentation here: https://developers.google.com/calendar)
