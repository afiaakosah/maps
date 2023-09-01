# Integration Map
# Project Details
## Project Name: Integration Maps
## Team Members:
               Catherine M Kim - ckim167
               Lynda Winnie Umuhoza - lumuhoza
               Afia Akosah - aakosah
## Link to repo: https://github.com/cs0320-f2022/integration-aakosahb-ckim167-lumuhoza
### Estimated hours spent: ~16
# High Level Design Choices:
###  Backend:
We added our backend code from Sprint 2. Added a MapHandler for
handling the functionality of the map endpoint. With the user’s given input of
boundaries, which in this case are minimum and maximum longitude, minimum and
maximum latitude. With this, redlining data for regions within those geographical
boundaries are provided to the user. We also created a RedlineData class that outlines
the format of the GeoJSON file.It has attributes of the type and features.
### Frontend:
 
The frontend part of our code mainly defines the user interface and what
the user will interact with when using our app. Here, we created a map from
react-map-gl. We initially set the location of the map to be Providence by
entering the specific latitude and longitude of Providence.
All this is defined in one class - Map.tsx.
S-DIST - We defined what the user gets when they click on a specific
location on the map. We expect the user to get the State, City, and Name
of the location.
## User Stories:
### 1.
The end-user stakeholder can view a map of the area they are interested in
at the zoom level they want.
### 2.
The end-user stakeholder can view an overlay of historical redlining data
atop the area of the map they are interested in. We also successfully
implemented the S-DIST part of this user story by allowing the user to
click on an area in the redlining overlay, and get the State, City, and Name of that area.
### 3.
The developer stakeholder can use our API server to access the redlining
GeoJSON dataset. They can also filter the data they receive geographically
by providing a bounding bow in their query.
### 4.
The developer stakeholder can understand our program because we added descriptive
comments to our functions both in the backend and frontend.
The developer stakeholder can also read our README which explains our design
choices and what our classes are doing.
## ERRORS/BUGS: No known bugs
 
## TESTS:
### Detailed descriptions for what the tests do can be found in the respective test suites.
All our tests can be found in the back end. We created a maptest directory that has three testing suites. 

### TestMapAPIHandler: 
This test suite handles **integration testing** for the API handler.It has tests that test when
the user doesn’t enter a Latitude or Longitude or both, when the user enters wrong parameters,
when the user gives invalid bounds, closed bounds, valid bounds and valid bounds but out of scope.  

### UnitTestMap: 
This test suite handles **unit testing** for the Map API Handler. It tests for specific methods in the MapHandler, and RedlineData class. The 		methods include; HandleRedlineData for different types of requests, checkCoordinates(method that checks whether coordinates are in bounds) for 		coordinates in bounds, and coordinates not in bounds. We also do **fuzzTesting** in this test suite, where we randomly generate minLon, maxLon, 	minLat, and maxLat values. This ensures that we cover a large number of cases, and that our program does not crush. We also test with mocks in this 	    suite by creating a **mock** JSON and mock RedlineRequests. 

### TestMapResponses: 
This test suite tests for different map responses and checks if they are being converted into JSON correctly. This is where we mostly test with 	**mocks**. We create a mock JSON with result, input, coordinates, and data fields. We then check to see whether our MapRecord with specific fields 	is equal to the json. We test for valid inputs and also for specific errors(error_data_source, and error_bad_request). 
For the frontend, we test our App class to see if the map elements and header are in the respective divs.
				
## HOW TO:
### Tests:
To run the tests, in the terminal, navigate to the src directory, run npm test.
### Run the Program:
To interact with the map, in the terminal, navigate to the src directory,
run npm start. Make sure that npm is installed, you can do this by running npm install.
