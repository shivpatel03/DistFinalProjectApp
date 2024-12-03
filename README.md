## Project Information
- This is a portion of our final project for the Distributed Systems course
- The code within this repository includes our Android Wear OS application built to send real-time data to the server, that can be found [here](https://github.com/shivpatel03/DistFinalProjectServer)
- The goal of this project overall is to develop a health-monitoring system using off-the-shelf wearable devices, running Android's Wear OS

## Prerequisites
- Since this is an Android application, the easiest way to emulate the wearable is to use Android Studio
- To do this, download Android Studio from [here](https://developer.android.com/studio) and follow the installation steps
- Once in Android Studio, ensure you set up with all the latest SDK's
- Also, once you are done that, you can create a new device to emulate using the Device Manager on the right side of the screen
- In the device manager, create a new one, and follow these steps:
1. Under category, choose Wear OS
2. Choose Wear OS Square. then hit next
3. In the release name, make sure it is `UpsideDownCake`, if you don't already have it, install it from here
4. You can name it whatever you would like, then hit `finish`

You have now successfuly set up the required emulator to run this application

## Setup
- Clone this repository:
```
git clone git@github.com:shivpatel03/DistFinalProjectApp.git
```
- Wherever you saved it, open it with Android Studio
- Wait for gradle to sync (can check the bottom right to make sure it is done)
- On the top right, hit the green play button, this should start the application
- If you are running the server code found [here](https://github.com/shivpatel03/DistFinalProjectServer), ensure that it is running on your network, and this app will connect automatically

You have now connected the **App Portion** of this project!
To create more emulations to show more clients, simply follow the steps above to create a new device.
