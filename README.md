SharePoint 2013 SDK & Demo
=====================

Demonstrates how one can access Office 365 SP Lists API, Mail API and Files API from Android. Contacts API and Calendar API are a stretch goal.

Repository structure:
- **office365-demo**
Main demo application that demonstrates use of Mail API, Lists API and Files API. Is updated along with SDK updates.
- **office365-sdk**
 - sdk-core - 
Library containing core OData and networking loic for all API specific SDKs.
 - sdk-lists - 
SDK providing Lists API.
 - sdk-files - 
SDK providing Files API.
 - sdk-mail - 
SDK providing Mail API.
-	**pabloz**
 -	office365-app - 
Simple demo app. Allows reading the list and creating the list entity.
 - office365-sdk - 
Library used as a backend by ‘office365-app’
