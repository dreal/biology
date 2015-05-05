How to Use BioPSy
=======

The BioPSy GUI is launched using the BioPSy JAR file ([Java JDK 6 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html) required):

```
java -jar BioPSy.jar
```

The GUI has options for the user to browse for a model file and a time-series data file to perform parameter synthesis on as well as to select a value for an acceptable amount of noise in the time-series data.  Once selected, these files are shown in the SBML and time-series tabs, respectively.  The files are also parsed, and the data is displayed in the parameters and variables tabs.

Under the parameters tab, a user can select which parameters in the model he or she would like to synthesise.  For synthesised parameters, the user is also able to define a lower bound and upper bound that is used to constrain the parameter search space.  Similarly, the variables tab allows a user to give bounds on the acceptable values for each variable in the system.  Once the bounds are specified, a user can click the run button to perform the synthesis.  The advanced options button enables the user to specify the path to the dReal binary as well as the desired level of precision, used by dReal (the default value is 0.001).  If the user has a different version of dReal installed, she or he can change the path to point to the binary of the different version.  Once the synthesis has started, the output tab displays the output file as it is being produced allowing a user to watch as the infeasible ranges, feasible ranges, and undetermined ranges are generated for each time point in the data.
