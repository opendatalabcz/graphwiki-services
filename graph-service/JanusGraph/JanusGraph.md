# JanusGraph database

## Download
JanusGraph is available at https://github.com/JanusGraph/janusgraph/releases/download/v0.4.1/janusgraph-0.4.1-hadoop2.zip. Download and unzip this archive. 


## Configuration

#### Gremlin Server
Copy and replace [gremlin-server.yaml](./conf/gremlin-server.yaml) in **${JANUSGRAPH_DIRECTORY}/conf/gremlin-server/** directory.

#### Property files
Copy and replace [janusgraph-cql-es-server.properties](./conf/janusgraph-cql-es-server.properties) and [janusgraph-cql-es-server-test.properties](./conf/janusgraph-cql-es-server-test.properties) in **${JANUSGRAPH_DIRECTORY}/conf/gremlin-server/** directory.


#### Global graph bindings
Copy [empty-sample.groovy](./scripts/empty-sample.groovy) to **${JANUSGRAPH_DIRECTORY}/scripts/** directory.


## Usage
JanusGraph database can be managed with **janusgraph.sh** script located in **${JANUSGRAPH_DIRECTORY}/bin/** directory. 
```
Usage: bin/janusgraph.sh [options] {start|stop|status|clean}
 start:  fork Cassandra, ES, and Gremlin-Server processes
 stop:   kill running Cassandra, ES, and Gremlin-Server processes
 status: print Cassandra, ES, and Gremlin-Server process status
 clean:  permanently delete all graph data (run when stopped)
Options:
 -v      enable logging to console in addition to logfiles
```

Note that start command must not be executed by root user.


## Indexes
#### Fulltext search index creation
Follow instructions in [fulltext-search-index.md](./fulltext-search-index.md).


## Batch import
See [batch-import.md](./import/batch-import.md).
