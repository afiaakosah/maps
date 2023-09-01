
## Project name:
API Proxy

## Team members:
Catherine Kim (ckim167), Joe Maffa (jmaffa)

## Link to repo: ##
https://github.com/cs0320-f2022/sprint-2-ckim167-jmaffa.git

## Design: ##
Server: the top-level class, which starts Spark and runs the various handlers. There are three
endpoints: loadcsv, getcsv, and weather. 

MapSerializer: contains record that serializes the key, values in the results map to Json after a 
successful query.

FailureResponse: contains record that serializes the key, value in the failure map to Json after an
unsuccessful query.

LoadCSVHandler: functionality for the loadcsv endpoint. Takes in a boolean parameter corresponding
to whether the CSV file has a header or not. Using the QueryParamsMap of the request passed in, class
returns appropriate error messages via the record in FailureResponse (if no queries or CSV file is given, 
if file is not in data folder, if file does not exist). If there are no errors with the query parameters, 
the class contains an instance of the CSVParser to parse the CSV. The filepath and data of the valid 
CSV file is stored in CSVDataHolderRecord (shared state between loadcsv, getcsv). The results are 
added to the results map in MapRecord and serialized. If someone wants to use this handler with their
own CSV data, the data must be inside the /data folder of the project, otherwise it will not be 
accessible and an error indicating the unreachability of the file will be displayed on the server. 
This class implements Handler interface. 

GetCSVHandler: functionality for the getcsv endpoint. The class returns appropriate error 
messages via the record in FailureResponse (if query parameters are given by the user, if getcsv is 
called before loadcsv, if there is an error with the Json). If there are no errors with calling 
getcsv, the results map is updated with the valid filepath and parsed CSV data, and is serialized 
by MapRecord. This class implements Handler interface.

WeatherHandler: functionality for the weather endpoint. The class returns appropriate error
messages via the record in FailureResponse (if there is an error with the NWS API due to the given 
lon, lat, if there is an error with the Json). If there are no errors with calling weather, the results
map is updated with the current temperature with the given lon, lat and is serialized by MapRecord.
This class implements Handler interface.

Handler Interface: An interface that declares handle, successResponse, and failureResponse methods 
for API Handler classes. This was done so that if we were to add more handlers to the program, they
would be given a "blueprint" as to what methods it should implement. We use the MapRecord and the 
FailureRecord for these methods for the loadcsv, getcsv, and weather handlers, but if a developer wanted
to add another Handler that required different serialization methods, this could be done.

CSVDataHolder: represents the shared state between loadcsv and getcsv. Has variables corresponding 
to the name of the most recent valid CSV file that was loaded and the respective parsed CSV data.

GridData: Object that is created from Moshi reading of NWS API data, holds the GridID, GridX and GridY.

TempData: Object that is created from Moshi reading of NWS API data, holds the forecasts for a given location.

BadJSONException: Exception that is raised when Moshi is unable to read a JSON file.

DatasourceException: Exception that is raised when data is not properly passed to NWS API, or it cannot
find the data for that location

## Errors/Bugs: ## 
- None

## Tests: ##
#### Test loadcsv API Handler ####
This testing suite tests a variety of scenarios for the loadcsv API handler. For example, 
when only loadcsv is called with no CSV file, when the filepath query is spelled incorrectly,
when a CSV file in the data folder is given but does not exist, a CSV folder outside the data 
folder is given, and when a valid CSV in the data folder is given. It also tests that if an
invalid CSV file is asked to be loaded afterwards, the most recent filepath and CSV data variables 
do not change, but when another valid CSV file is asked to be loaded, the most recent filepath and
CSV data variables change accordingly.
#### Test loadcsv Responses (Serialization) ####
This testing suite tests that the FailureRecord and MapRecord returns the correct serializations
for a given input by the user. Tests serializations for when a valid CSV file is given by the user,
when the CSV file does not exist in the data folder (error_datasource_file_not_in_data), and when 
the CSV file is not in the data folder (error_datasource_unreachable_file).

#### Test getcsv API Handler ####
This testing suite tests a variety of scenarios for the getcsv API handler. For example,
when getcsv is called before loading a valid CSV file, a query parameter is given with getcsv,
and when getcsv is called after loading a valid CSV file. It also tests that if another valid 
CSV file is loaded after, that the updated filepath and CSV data is returned to the user when
getcsv is called again.
#### Test getcsv Responses (Serialization) ####
This testing suite tests that the FailureRecord and MapRecord return the correct serializations
for a given input by the user. Tests serializations for when getcsv is called after a valid CSV file 
was loaded for various CSV files, when getcsv is called with query parameters (error_bad_request), and
when getcsv is called before loading a valid CSV file (error_bad_request_no_loaded_csv).

#### Test weather API Handler ####
This testing suite tests a variety of scenarios for the weather API handler. For example,
when only "weather" is called with no query parameters, no lon and/or lat query parameters
are given, invalid points are given for lon, lat (lon, lat contains letters or more than 4 digits
after the decimal point), the lon and/or lat queries are spelled incorrectly (ln, lt), and when 
a valid lon, lat point is given to the API.
#### Test weather Responses (Serialization) ####
This testing suite tests that the different responses are being serialized correctly by MapRecord
and FailureRecord to the user. For example, it tests that the error messages are being serialized
correctly for a given user input (error_bad_request, error_datasource) and that the results map 
is serialized correctly for a valid weather input.
#### Test weather API Calls ####
This testing suite contains our unit tests for the NWS API Weather handling. It tests the GridDataCreator
and TestDataCreator methods in the WeatherHandler class. Note that these methods are not solely dependent
on the API data, and can be used with any passed JSON, so we tested the example JSONS to see how Moshi 
creates these objects from them.

#### Note about testing CSVParser ####
We did not include CSVParser testing as that was done in Sprint 0.
## How to: ##
#### Build and run the program: ####
To run the program, go to the Server class and press the green play button to start the Server. The 
terminal will print "Server started". In a browser, type in "http://localhost:3232/" to access the
server. There are three endpoints for this API: loadcsv, getcsv, weather. If you wish to retrieve the 
contents of a CSV file, first call loadcsv with a query parameter corresponding to a valid CSV file 
in the data folder (ex: loadcsv?filepath=data/stars/ten-star.csv). Then call getcsv to retrieve the 
parsed CSV data (ex: getcsv). Repeat if you'd like to retrieve the contents of a different valid CSV 
file. If you wish to retrieve the current temperature at a valid longitude, latitude, call weather 
with query parameters lon, lat (ex: weather?lon=41.8268&lat=-71.4029). The longitude, latitude query
parameters cannot contain more than 4 digits after the decimal points due to the functionality of the
NWS API.

#### Run the tests that are written: ####
To run tests, go to the terminal and type "mvn package" (without the quotation marks). If 
violations are present, type "mvn spotless:apply". You will be able to see the results of each 
testing suite: the name of the testing suite, the number of tests run, failures, errors, 
skipped, and the time elapsed while running the suites. The provided testing suites 
(TestGetCSVAPIHandler, TestGetCSVResponses, TestLoadCSVAPIHandler, TestLoadCSVResponses, 
TestWeatherAPIHandler, TestWeatherResponses) can also be run individually in IntelliJ.

#### Integrate a new Handler into the project ####
1) Create your class that implements the Handler interface
2) Add an endpoint in the Server with Spark.get("<endpoint>", new <YourClass>())
3) Implement Handler methods in your class. If you want to serialize a Map from String to Object, you
can take advantage of the MapSerializer Class. The FailureResponse class also provides a general template
for how to send error response to the server. Regardless of how you choose to display your data and 
what kind of data you want to serialize, you must serialize something to the server and return a String
in the handle method for it to be printed to the server.

