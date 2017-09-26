# PhotoPosing
An android application that takes user photo at tourist destination and recommend popular photo poses of that destination using vision APIs and stores user photos to Firebase database for future suggestions.

This project is hosted on Google cloud Platform in order to use it please change the credential and server address.

Folder by name 'app' have Android application code.
Folder by name 'backend' have Google App Engine code.
Folder by name 'PhotoPosing' consits of entire project. In order to build this code follow the following steps:-
	1) Import PhotoPosing in AndroidStudio.
	2) Install the required dependencies.
	3) Change build configuration to 'app' and install the apk.
	4) Change build configuration to 'backend' and press on run.
	5) Go to Build and click on deploy module to App Engine.

How to check?
	1) Uplaod a photo with landmark and user.
	2) The App Engine will detect face expression, landmark and suggest similar images.
	3) The image is saved to firebase storage and added to firebase database.
	4) Android app will show a) Images suggested by our database, b) images suggested by google, c) face expression, d) map to the detected landmark

