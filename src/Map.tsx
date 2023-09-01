import React, { useEffect, useRef, useState } from 'react';
import Map, { MapLayerMouseEvent, Source, Layer, MapRef, PointLike} from 'react-map-gl'  
import {myKey} from './private/key'
import {overlayData, geoLayer} from './overlays' 

const TEXT_header_div = "header div"
const TEXT_map_elements = "map elements"
const TEXT_CITY = "city"
const TEXT_STATE = "State"
const TEXT_NAME = "name"
const TEXT_ERROR_MAP_REF = "MapReference is null"
const TEXT_ERROR_NO_PROPERTIES = "no properties for this area"
const TEXT_DATA_ERROR = 'Could not load data'





/**
 * Helper function for MapElements. It sets up the initial lat, lon, and zoom of
 * the map, and fetches data from the backend.
 * @param param0 
 * @returns 
 */
function MapData({setLocationInfo, setLocation}: {setLocationInfo: React.Dispatch<React.SetStateAction<string>>, setLocation: React.Dispatch<React.SetStateAction<string>>}) {

  // Providence is at {lat: 41.8245, long: -71.4129}
const [viewState, setViewState] = React.useState({
  longitude: -71.4129,
  latitude: 41.8245,
  zoom: 10,
  bearing: 0,
  pitch: 0,
  padding: {top: 1, bottom: 20, left: 1, right: 1}
});

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined);

   useEffect(() => {

    const result = async () => {
      const response = await fetch("http://localhost:3232/map?minLon=-180&maxLon=180&minLat=-90&maxLat=90")
      .then(r => {return r.json()})
      .then(responseObject => {console.log(responseObject.data); return responseObject.data})
      overlayData(response).then(data => {setOverlay(data)}) 
      .catch(error => console.error(TEXT_DATA_ERROR, error));
    }

    result()

  }, [])

  const mapRef = useRef<MapRef>(null);
  let map =  
  <Map 
    mapboxAccessToken={myKey}
    longitude = {viewState.longitude}
    latitude = {viewState.latitude}
    zoom = {viewState.zoom}
    bearing = { viewState.bearing }
    pitch = {viewState.pitch }
    padding = { viewState.padding }
    onMove = {event => setViewState(event.viewState)}
    onClick = { handleClick }
    style = {{width: window.innerWidth, height: window.innerHeight}}
    mapStyle = {'mapbox://styles/mapbox/light-v10'}
    children = {
      <Source id = "geo_data" type = "geojson" data = {overlay}>
      <Layer id = {geoLayer.id} type = {geoLayer.type} paint = {geoLayer.paint} />
      </Source> 
 }
 ref = {mapRef} />
      

 /**
  * Defines what happens when a user clicks anywhere on the map.
  * We expect a no map_ref error when the user clicks on a place that is not on
  * the map
  * We also expect a no properties error when the user clicks on a place on the
  * map with no properties.
  * @param e 
  */
  function handleClick(e: MapLayerMouseEvent) {
      const bbox: [PointLike, PointLike] = [
          [e.point.x, e.point.y],
          [e.point.x, e.point.y]
      ]
      if(mapRef.current === null) {
          console.log(TEXT_ERROR_MAP_REF)
      }
      else {
          const result = mapRef.current.queryRenderedFeatures(bbox)[0]['properties']
          if (result === null) {
              console.log(TEXT_ERROR_NO_PROPERTIES)
          }
          else {
              const state = result[TEXT_STATE];
              const city = result[TEXT_CITY];
              const name = result[TEXT_NAME];
              setLocationInfo("State: " + state + ", City: " + city + ", Name: " + name)
              setLocation("Latitude: " + viewState.latitude.toFixed(4)
               + " Longitude: " + viewState.longitude.toFixed(4)
               + " Zoom:" + viewState.zoom.toFixed(4)
               )
          }
      }
      
  }

  return map;
}


/**
 * Helper function that sets up the location stats(State, City, and Name) div
 * @param param0 
 * @returns 
 */
function LocationStats({locationInfo, location}: {locationInfo: string, location: string}) {
  return (
      <p className="App-header" aria-label = {TEXT_header_div}>
      {locationInfo}
      <br></br>
      {location}
    </p>
    
  )
}

/**
 * Exported function that sets up the elements of the map. 
 * @returns 
 */
export default function MapElements(){
  const [viewState] = React.useState({
    longitude: -71.4129,
    latitude: 41.8245,
    zoom: 10,
    bearing: 0,
    pitch: 0,
    padding: {top: 1, bottom: 20, left: 1, right: 1}
  });
  const [locationInfo, setLocationInfo] = useState<string>("Click for Location info!");
  const [location, setLocation] = useState<string>("Latitude: " + viewState.latitude.toFixed(4) + 
                                                  " Longitude : " + viewState.longitude.toFixed(4)+
                                                  "Zoom: " + viewState.zoom.toFixed(4))
return (
  <div className="Map-Elements" role = "map-elements" aria-label = {TEXT_map_elements} >
    <LocationStats locationInfo = {locationInfo} location = {location} />
    <MapData setLocationInfo = {setLocationInfo} setLocation = {setLocation}/>    
  </div>
);
}

export {MapData, MapElements}