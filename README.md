# HopReviews

Surveys indicated that Hopkins students desired and perferred to view reviews from other Hopkins students. Hopreviews is an android app that allows Hopkins student view, like, and review Hopkins specific locations such as the BLC and Hopkins Cafe and popular locations around Hopkins campus. 

### Log in Information:

User: smodare1@jhu.edu Password: 123456

User: mchakra9@jhu.edu Password: xxxxxxxx

User:bluejay@jhu.edu Password: 123456



Please note that due to the nature of emulators, their location is automatically 
at GooglePlex in California. You need to change the location manually by going to
the extended controls of the emulator and change it to wherever you would like it to be.
As a result of this, when you allow live location for the first time, the emulator
does not find the current location; however, this issue is fixed if you close and reopen the
app because that location will be stored as "last known location."

Also, if you are planning to use the add photo feature of the app, make sure your
emulator is capable of doing so. Many emulators with APIs less than 30 do not allow
users to open or even access the Photos app! Any attempt to open the photos app will
crash the emulator.

Due to the social media nature of our app, users may not use it in horizontal orientation.

## Sprint 2
We implemented all the promised/required features for sprint 2 and even more! These features include:
- Populate the feed list for all of the users from the database
- Making list items clickable to navigate to specific location activities
- Add photo feature to individual reviews
- Implement profile activity and allow users to modify their profile information
- Add more locations to the map for more options that satisfy broader demographics
- Add an individual like and dislike feature to reviews to allow other students to "upvote" or "downvote" a review. 
- Create favorites activity and populate its list accordingly
- Allow students to favorite locations using the start button in the action bar
- Test that all features work through many different navigation sequences as the various screens are launched, back buttons are pressed, activities are closed, etc.
- Test that the operation of our app screens is smoothly consistent after interruptions such as orientation changes, potential interruptions, etc.
- If the size of the device causes disruption in the app, create alternative layouts.
- Users can post anonymous reviews.
- Display reviews of user in their profile
- Users can edit their profile information. 
- Extra: remember me functionality for ease of logging in

The state of the app: Our frontend and backend parts are fully functional complete. Under no
circumstances should our app crash. We have rigorously tested our application on different
devices, orientations, and APIs. 

To sum up this sprint, we have completed all functionality required by our product backlog:
- Revise the app design given the course staff's feedback
- Create a GitHub repository for HopReviews
- Split and delegate tasks to team members
- Creating the layout for all the activities including log-in, sign-up, map, feed, favorites, and account
- Create the menu fragment layout
- Create a database with modeling for users, reviews, and locations
- Create the data members and initializations necessary to connect the database records to the collection view(s).
- Populate and refresh the list(s) from the database records whenever necessary.
- Connect the map activity to Google Map API for real-time updates
- Create a list of locations we want to add to the map interface (ex. Brody Reading Room, Gilman Atrium, etc.)
- Allows users to leave anonymous users as required by our market research
- Handle and test error-checking for fraud account information
- Test that all features work through many different navigation sequences as the various screens are launched, back buttons are pressed, activities are closed, etc.
- Test that the operation of our app screens is smoothly consistent after interruptions such as orientation changes, potential interruptions, etc.
- Add a voting feature on feed reviews so students can agree or disagree with other students’ reviews
- Add photo feature to reviews



## Sprint 1

We implemented all the promised/required features for sprint 1. These features include:
- Create a list of locations to add to the map interface that may not exist in Google Maps (ex. Brody Reading Room, Gilman Atrium, etc.)
- Finalize a list of tags that users can search for and label reviews with
- Set up back-end firebase to store user login information, locations, and reviews
- Populate and refresh the list(s) from the back-end database whenever necessary
- Create login and sign-up pages to allow users to enter the app
- Handle and test error-checking for fraud account information
 - Create map feature
- Connect the map activity to Google Map API for the app home page and central feature
- Allow users to click on the map to get specifics about places near them
- Display locations not in Google Maps API that are important to students (ex:b Brody reading room)
- Create add review feature and test functionality
- Add like and dislike feature
- Add a more lengthy review feature (written, no photos yet)
- Save reviews in the database

The state of the app: Our backend is almost fully complete. For sprint 2, we need to render
data that is in our database for the feed and favorites. Our app should not crash at any point.
All error handling for adding a review and signing up/logging in is completed.

In addition, we are currently working on adding to favorite (clicking on the star button
actually works on the backend side but we need to render it on the frontend during sprint 2).
We are also working on editing the profile and showing the profile page. This submitted version
includes some but not all the code for this feature which will be implemented in sprint 2.
