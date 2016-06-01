Dependencies on the app are mentioned in the build.gradle file in the root of /sangz_client/app folder:

	compile 'com.android.support:appcompat-v7:23.4.0'
	compile 'com.android.support:design:23.4.0'
	compile 'com.squareup.okhttp3:okhttp:3.2.0'
	compile 'com.google.code.gson:gson:2.4'
	compile 'com.android.support:support-v4:23.4.0'

However these do not need to be worried since the gradle build tool will take care of downloading all the necessary libraries if the source code was to be compiled.

The current version of the app is in the root of the project folder, called app-debug.apk

The .apk should be installable by simply copying it to a physical device and opening the .apk file, or by opening it on an android emulator, such as Genymotion or the Google's emulator.

The client needs the IP address of the host in format of http://IP_GOES_HERE:PORT_GOES_HERE

This should be supplied in the "login screen" when launching the app. The app does not respond in any way if there is not a response from the server, but it does complain if URL is not of proper format or if the user name supplied was not found from the server response.

An user name also needs to be supplied in order to continue from the first screen, Jokke could be usable since it should be in the database when the server is initially started.

No tests were made for the client.