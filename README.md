sharepoint-api-lists-demo
=========================

Demonstrates how one can access Office 365 SP Lists API from Android.

-	**lists-demo**
Recently added demo application that uses ‘lists-sdk’ as a library. Both lists-demo and lists-sdk are in progress of migration of functionality from ‘sp-list-odata-demo’ and updating with a top level wrapper/abstraction for the developer to use.
-	**lists-sdk**
Backend used by ‘lists-demo’. Work in progress to merge logic from ‘sp-list-odata-demo’.
-	**pabloz**
 -	office365-app
Simple demo app. Allows reading the list and creating the list entity.
 - office365-sdk
SDK library used as a backend by ‘office365-app’
-	**sp-list-odata-demo**
Initial demo app using ODataJClient library that contains CRUD functionality for lists and list items in the form of a single project. This is now being separated into ‘lists-demo’ and ‘lists-sdk’
