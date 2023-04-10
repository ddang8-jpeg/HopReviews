# TeamD-HopReviews

Log in Information:
User: smodare1@jhu.edu Password: 123456
User: mchakra9@jhu.edu Password: xxxxxxxx

Please note that due to the nature of emulators, their location is automatically 
at GooglePlex in California. You need to change the location manually by going to
the extended controls of the emulator and change it to wherever you would like it to be.
As a result of this, when you allow live location for the first time, the emulator
does not find the current location; however, this issue is fixed if you close and reopen the
app because that location will be stored as "last known location."

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
