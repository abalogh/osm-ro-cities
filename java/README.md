# osm-ro-cities osmxmltojson java utility

A simple java utility that can be run from console, it is just one class so it can be easily compiled and run:
```
java eu.abalogh.osmxmltojson.OsmXmlToJson <file_input.osm> <file_output.json?> <cityname?>
```

Only required parameter is the input osm file, example: targu-mures_marosvasarhely.osm

The output will be the same name but as json, example: targu-mures_marosvasarhely.json

If the file name contains "_" it is considered a multilanguage city and street names will be added in json in both Romanian and Hungarian (if available).

The resulting json should be placed in streets folder, example: streets/ms/targu-mures_marosvasarhely.json