# JanusGraph batch import
For data batch loading was used [data-importer tool](https://github.com/IBM/janusgraph-utils/blob/master/doc/users_guide.md#import-csv-file-to-janusgraph) from IBM.

## Usage
See [official documentation](https://github.com/IBM/janusgraph-utils/blob/master/README.md#4-load-schema-and-import-data). 

##### Data location
Directory with schema.json, datamapper.json and CSVs is located [here](./data).

##### Import command:
```
export JANUSGRAPH_HOME=~/janusgraph

./run.sh import $JANUSGRAPH_HOME/conf/gremlin-server/$PROPERTY_FILE ./data ./data/schema.json ./data/datamapper.json
```
