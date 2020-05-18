# Fulltext search index

#### Run Gremlin console
```
${JANUSGRAPH_DIRECTORY}/bin/gremlin.sh
```

#### Connect to JanusGraph Server
```
:remote connect tinkerpop.server conf/remote.yaml
```

#### Create fulltext search index
```
:>  mgmt = graph.openManagement();  \
    \
    givenName = mgmt.makePropertyKey('givenName').dataType(String.class).make();  \
    familyName = mgmt.makePropertyKey('familyName').dataType(String.class).make();  \
    officialName = mgmt.makePropertyKey('officialName').dataType(String.class).make();  \
    \
    mgmt.buildIndex('search', Vertex.class)  \
        .addKey(givenName, Mapping.TEXTSTRING.asParameter())  \
        .addKey(familyName, Mapping.TEXTSTRING.asParameter())  \
        .addKey(officialName, Mapping.TEXTSTRING.asParameter())  \
        .buildMixedIndex('search');  \
    \
    mgmt.commit();
```