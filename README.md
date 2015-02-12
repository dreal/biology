Repository for Biology-related tools
=======

BioPSy
---------
BioPSy is a tool that performs parameter synthesis on biological models specified using the Systems Biology Markup Language (SBML) and corresponding time series data.  BioPSy utilises the Satisfiability Modulo Theories (SMT) solver, dReal, to determine the range of acceptable parameter values within a given domain.  A model using parameter sets computed with BioPSy is formally guaranteed to satisfy the desired behaviour.

### Download

The latest version of BioPSy including all required binaries can be downloaded from the [releases page](https://github.com/dreal/biology/releases).  BioPSy is a Java application and requires [Java JRE 6 or higher](https://www.java.com) to run.

### Compiling from Source

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

Before running BioPSy, add `BioPSy/bin` to your PATH:

```
export PATH=<BioPSy Directory Location>/BioPSy/bin:$PATH
```

To run BioPSy, execute `java -jar BioPSy.jar`.
