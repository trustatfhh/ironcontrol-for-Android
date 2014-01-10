ironcontrol-for-Android
=======================

Ironcontrol is an IF-MAP client on a Android Smartphone device. It designed as a tool for administrative tasks in an IF-MAP environment. It allows the user to send requests such as publish, search and subscriptions to a MAP server and save these requests and also merge several requests into one via certain operations.


Prerequisites
=============
Prior to build ironcontrol-for-Android, you must download and install the following software:

* [Git][git]
* [Java JDK (1.7)][java]
* [Android SDK][android_sdk]
* [Maven 3][maven]

Before you begin, make sure you set the ANDROID_HOME environment variable to the appropriate value, e.g.:

    ANDROID_HOME =.../adt-bundle-linux-x86_64-20131030/sdk

You should also add $ANDROID_HOME/tools and $ANDROID_HOME/platform-tools to you $PATH.

Libraries
=========
ironcontrol-for-Android needs...

* Android API 16
* ifmapj-0.1.5


Building
========
To build ironcontrol, download it via 

    $ git clone https://github.com/trustatfhh/ironcontrol-for-Android.git

and run maven

    $ mvn package
	
When finished you find the ironcontrol-for-android.apk is in the target directory.


Deploy
======

Own Device
----------
You can install the apk file on a Android device direct. Copy it on the SD-Card and install it by touch.

AVD(Android emulator)
---------------------
With Maven:

    $ mvn android:deploy
	
Direct over adb(Android Debug Bridge)

    $ adb install [apk-file]

To connect on a local MAP-Server use IP 10.0.2.2

Create a new AVD
================
From commandline

    $ android create avd -n AVD -t 1 --abi x86 -c 16M

or you use the AVD Manager. Ironcontrol is developed for Android 4.1.2, use target API level 16 and make a SD Card. Then you have a log file on the SD Card an you can import other certificates. 



Adding new certificates
=======================
On first start ironcontrol creates two folders on the SD card:

    1. /storage/sdcard0/ironcontrol/certificates

The x.509 certificates from the IF-MAP server should be copied in this folder. "irond" is already integrated.

    2. /storage/sdcard0/ironcontrol/keystore

In this folder the keystore (ironcontrol.bks) and the ironcontrol certificate (ironcontrol.pem) are stored.

To add a new certificate just copy the appropriate x.509 certificate into the folder "ironcontrol/certificates" on your SD card.
At every startup ironcontrol checks the folder "ironcontrol/certificates" and automatically adds new certificates to the ironcontrol's keystore. You can also load certificates manually see chapter 3.3.2 Connections in "User manual to "ironcontrol for Android".pdf.

If no SD-card is present or mounted an internal keystore is used. In this case the only possible connection is to the irond-server.


Feedback
========
If you have any questions, problems or comments, please contact

<trust@f4-i.fh-hannover.de>


LICENSE
=======
Ironcontrol is licensed under the [Apache License, Version 2.0] [apache_license].


Changelog
=========

1.0
-----

* Initial beta release


[git]: http://git-scm.com/
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[android_sdk]: http://developer.android.com/sdk/index.html
[maven]: http://maven.apache.org/
[apache_license]: http://www.apache.org/licenses/LICENSE-2.0.html
