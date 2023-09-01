import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";



// Type predicate for FeatureCollection
function isFeatureCollection(json: any): json is FeatureCollection {
    return json.type === "FeatureCollection"
}


export async function overlayData(json: any): Promise<GeoJSON.FeatureCollection | undefined> {

   if(isFeatureCollection(json))
     return json
   return undefined
  }


const URL = "http://localhost:3232/map?";
/**
 * Fetching the redlining data from the backend
 * @param minLat 
 * @param minLon 
 * @param maxLat 
 * @param maxLon 
 * @returns 
 */
export async function mapData(minLat: number, minLon: number, maxLat: number, maxLon: number): Promise<FeatureCollection | undefined> {
  let map_url: string = URL + `minLat=${minLat}&minLon=${minLon}$maxLat=${maxLat}&maxLon=${maxLon}`

 let redLining_data = await fetch(map_url)
  .then((resp) => resp.json())
  .then(json => {
      return json.data;
  });
  return redLining_data;
}

////////////////////////////////////

const propertyName = 'holc_grade';

export const geoLayer: FillLayer = {
    id: 'geo_data',
    type: 'fill',
    paint: {
        'fill-color': [
            'match',
            ['get', propertyName],
            'A',
            '#5bcc04',
            'B',
            '#04b8cc',
            'C',
            '#e9ed0e',
            'D',
            '#d11d1d',
            /* other */ '#ccc'
        ],
        'fill-opacity': 0.2
    }
};