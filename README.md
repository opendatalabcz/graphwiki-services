# GraphWiki services

Backend services for GraphWiki project. 

This project includes following projects:
* comment-service
* graph-service
* task-service
* user-service



## User guide
See [final thesis](https://alfresco.fit.cvut.cz/share/proxy/alfresco/api/node/content/workspace/SpacesStore/42d34558-b22b-46b1-b418-6bc430e25b01).



## Getting Started

### Prerequisites
Following tools must be installed:

| Tool | Version |
| ------------- | ------------- |
| Java | 1.8 |
| PostgreSQL | 12.2 |
| JanusGraph | 0.4.1 |
| Docker | 19.03.8 |
| Jenkins |  2.204.5 |

For application configuration, see configuration files (*application\*.yml*).



## Usage

### Build
```
mvn clean package
```

### Test
```
mvn clean test
```

### Run
Application can be run with following profiles:
* **local** - environment for development
* **remote** - environment for VM deployment  
```
java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} ${JAR_FILE}
```



## Deployment

See [Jenkinsfile](./Jenkinsfile) and Dockerfiles ([comment-service](./comment-service/Dockerfile), [graph-service](./graph-service/Dockerfile), [task-service](./task-service/Dockerfile), [user-service](./user-service/Dockerfile))



## License
See [license](./LICENSE).
