SharePoint 2013 SDK & Demo
=====================

Demonstrates how one can access Office 365 SP Lists API, Mail API and Files API from Android. Contacts API and Calendar API are a stretch goal.

Repository structure:
-	**lists-demo**
Application that demostrates Lists API usage. This is no longer supported. Check "office365-demo" for a multi-API demo. [*Not supported*]
-	**lists-sdk**
Core SDK providing Lists API and a small portion of Files API.
- **office365-demo**
Main demo application that demonstrates use of Mail API, Lists API and Files API. Is updated along with SDK updates.
-	**pabloz**
 -	office365-app
Simple demo app. Allows reading the list and creating the list entity.
 - office365-sdk
SDK library used as a backend by ‘office365-app’
-	**sp-list-odata-demo**
Initial demo app using ODataJClient library that contains CRUD functionality for lists and list items in the form of a single project. Has been refactored and separated into ‘lists-demo’ and ‘lists-sdk’. [*Not supported*]
