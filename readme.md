# Technology to Access App Private Data on Android (No Root)

## About

By default all Android apps save data in a so-called Sandbox – a storage area which other applications cannot access. This project provides a simple way to access private data of third-party applications without root permissions. 

WARNING: Android security policy directly states that access to private data can be illegal, which puts certain limitations on gathering such data, including the possibility of being prosecuted by law. Therefore, use this method to obtain data consciously and thoughtfully and make sure that you abide by laws and respect privacy of others.

## Implementation

The main solution idea is to parse a layout tree of an active page and extract the necessary data.

This solution works well starting with Android 4.3 and older (including Android N), but to get needed "clear" data you will need to take your time to study each target application. This code illustrates the approach for Firefox browser history on Android 6.0 and Skype instant messages.

For detailed implementation notes and code details, please review the [related article](https://www.apriorit.com/dev-blog/429-access-app-data-on-android-no-root).

## License

Licensed under the MIT license. © Apriorit.
