# PlanIT
**PlanIT** is an app for creating, organizing, browsing, and in all other ways planning events. The app uses a ParsePlatform database to store all of the event data but also makes use of a local cache to make the app faster and enable some offline functionality. The app also is integrated with Google Calendar for exporting events to personal calendars.

Time spent: **4** weeks in total

## User Stories

The following **required** functionality is completed:

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

The following **stretch** features are implemented:

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

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='TODO' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](https://licecap.en.softonic.com/).

## Notes

While building this app one of the main challenges I faced was in configuring the credentials and OAuth scopes for the Google API client. While it was challenging to figure out how to store the authentication tokens and configure them with the necessary scopes I was eventually able to find the solution in the Google API documentation.

Another challenge I encountered was with caching event data since I had difficulty loading the events from the cache once they were stored. I ended up storing the events in a JSONArray since that allowed me to parse them in the easiest way without taking up too much space in the cache and enabled me to append new data as it is loaded.

For full details about this app's scopes, wireframes, models, networking, and more see the AppSpecifications.md file.

## Open-source libraries used

- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android
- [Parse-SDK](https://github.com/parse-community/Parse-SDK-Android) - Database for backend operations
- [Parceler](https://github.com/johncarl81/parceler) - Enables passing objects throgh intents
- [Google Calendar API](https://developers.google.com/calendar) - Used for exporting events to calendar
- [Google Drive API](https://developers.google.com/drive) - Used for attaching images to events
- [Slidr](https://github.com/r0adkll/Slidr) - Activity view to activity view transitions
- [FragmentTransactionExtended](https://github.com/DesarrolloAntonio/FragmentTransactionExtended) - Fragment to fragment transitions
- [WaveSwipeRefreshLayout](https://github.com/recruit-lifestyle/WaveSwipeRefreshLayout) - Refresh layout for refreshing events feed
- [Transition Button Android](https://github.com/roynx98/transition-button-android) - Buttons

## License

    Copyright 2021 Jaume Pujadas

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
