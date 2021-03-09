# osm-ro-cities

This project contains poly files for cities in Romania and street names in json format extracted 
from openstreetmap data.

NOTE: All content extracted from openstreetmap data is copyright: © OpenStreetMap contributors

## Steps to add more cities and streets to repository

1. Create the poly file 
	* Go to http://nominatim.openstreetmap.org/ and search for the city you'd like added and select details and locate the OSM relation number (example OSM	relation 8334767)
	* Go to http://polygons.openstreetmap.fr/ and use the relation id obtained from previous step 
	* Download the poly and geoJSON files and place it in the correct folder with a correct name, example: cities/ms/targu-mures_marosvasarhely.poly
	Always use the "_" as the city name separator if city has name in Romanian and Hungarian.
	
2. Extract the openstreetmap data relevant to a city using osmosis command line:
```
osmosis --read-pbf romania-latest.osm.pbf file="romania-latest.osm.pbf" --bounding-polygon file="targu-mures_marosvasarhely.poly" --write-xml file="targu-mures_marosvasarhely.osm"
```	
3. Use the provided java utility class [see usage](java/README.md)  that will generate the street names for a city and place it in the correct folder: streets/ms/targu-mures_marosvasarhely.json

## Steps to use the data in the repository

* You can use the geoJSON files, see: https://en.wikipedia.org/wiki/GeoJSON
* You can use the street json files to create street lookup functionality in your app

## Future roadmap

* Check if POI (point of interests) data can be also extracted the same way.



	

