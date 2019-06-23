# CAPSTONE PROJECT

[Java](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html) version 11

[Maven](https://maven.apache.org/download.cgi) version 3.6.1

## External Sorting

- To run the app execute the commands and follow the instructions from the console:

````
mvn compile
````

```
mvn exec:java -Dexec.mainClass="com.griddynamics.external_sorting.AppCustomSorting" -Dexec.args=arg0 arg1 arg2 ...  
```


## Custom Thread pool service

- To run the app execute the commands:
```
mvn compile
```

```
mvn exec:java -Dexec.mainClass="com.griddynamics.custom_threadpool.AppCustomThreadPool" -Dexec.args=10000 
```
### Tests

- To run tests execute the command:

```
mvn test
```

