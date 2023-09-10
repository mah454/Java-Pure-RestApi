## Pure Java Core Http Service

implement simple http server with a html/css page and simple rest api 

### build :          
```shell
mvn clean compile package
```


### test api:

**Note:** _please install `httpie` package on linux_
```shell
# Get all persons: (GET)
http "http://localhost:8080/api/v1/person"

# Get special person (GET)
http "http://localhost:8080/api/v1/person?id=1"

# Add new person (POST)
http "http://localhost:8080/api/v1/person" name=javad family=nakhaee  

# Delete person (DELETE)
http DELETE "http://localhost:8080/api/v1/person?id=1"
```

### Compile for docker 
```text
Use GraalVM 17+
``` 