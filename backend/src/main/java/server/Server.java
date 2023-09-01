package server;

import static spark.Spark.after;

import csv.GetCSVHandler;
import csv.LoadCSVHandler;
import map.MapHandler;
import weather.WeatherHandler;
import spark.Spark;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various
 * handlers. There are three endpoints: loadcsv, getcsv, and weather.
 */
public class Server {
    public static void main(String[] args) {
        Spark.port(3232);

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "*");
        });
        // sets /loadcsv, /getcsv, and /weather endpoints
        Spark.get("loadcsv", new LoadCSVHandler());
        Spark.get("getcsv", new GetCSVHandler());
        Spark.get("weather", new WeatherHandler());
        Spark.get("map", new MapHandler());
        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Server started.");
    }
}