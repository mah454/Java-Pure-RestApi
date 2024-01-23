## Pure Java Core Http Service

implement simple http server with a html/css page and simple rest api    

### Build Environmenr : 
```shell
> java -version 
openjdk version "21.0.1" 2023-10-17
OpenJDK Runtime Environment GraalVM CE 21.0.1+12.1 (build 21.0.1+12-jvmci-23.1-b19)
OpenJDK 64-Bit Server VM GraalVM CE 21.0.1+12.1 (build 21.0.1+12-jvmci-23.1-b19, mixed mode, sharing) 

> lsb_release -a 
No LSB modules are available.
Distributor ID: Ubuntu
Description:    Ubuntu 22.04 LTS
Release:        22.04
Codename:       jammy

> uname -a 
Linux internet 5.15.0-50-generic #56-Ubuntu SMP Tue Sep 20 13:23:26 UTC 2022 x86_64 x86_64 x86_64 GNU/Linux
```

### build :          
```shell
mvn clean compile package
```

### Run server (native-image):
```shell
   ./target/http-server
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
