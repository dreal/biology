Compiling from Source
=======

BioPSy requires the following packages to compile from source:

- [Java JDK 6 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Ant](http://ant.apache.org/)

**Note:** BioPSy calls another tool, dReal, to perform parameter synthesis.  A binary for this tool is included in the `BioPSy/bin` directory; however, if you would like to compile it yourself, you can find its source code using the following link:

- [dReal](https://github.com/dreal/dreal)

To compile BioPSy, change to the BioPSy directory and execute ant:

```
cd BioPSy
ant
```

This will compile the application and produce a jar file in `BioPSy/bin`.

To run BioPSy, execute:

```
java -jar BioPSy.jar
```
