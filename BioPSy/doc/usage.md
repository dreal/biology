How to Use BioPSy
=======

The BioPSy GUI is launched using the BioPSy JAR file ([Java JDK 6 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html) required):

```
java -jar BioPSy.jar
```

The GUI has options for the user to browse for a model file which should be in SBML format. Once the file is selected it will be displayed in the SBML tab.

Time-series data file contains experimental data in CSV (comma separated values) format. It should contain the same number of columns as number of species.

Important:

"Time" is a reserved word which is used to represents time values. Therefore, avoid using "Time" as a name for species in SBML file. 

Important:

The time series data should contain at least two non empty data rows. The first data row should be well defined (none of the places can be empty).

Once the file is selected, the time series will be displayed in the table in "Time series" tab. User is allowed to modify time series in the table and save changes. However, in order for changes to take place the file should be saved. "Save" button overwrites chosen CSV file and "Save as" button let's user specify the name of the file to write modified time series into.

"Parameters"
After the model file is parsed the list of parameters are displayed in "Parameters" tab.
Under the parameters tab, a user can select which parameters in the model he or she would like to synthesise.  

Important:
At least one parameter must be selected.

For synthesised parameters, the user is also able to define a lower bound and upper bound that is used to constrain the parameter search space. Also user can specify precision for the selected parameter (default value is 10^-3). Precision regulates the termination condition (the search will not be refined if the size of the interval is smaller than defined precision).

Note:
The changes will not take place if bounds and precision values are changed but parameter is not selected.

Note:
Both lower and upper bounds can be the same therefore allowing checking the parameter values instaed of performing synthesis on the interval. In this case the precision value will be ignored.

"Variables"
Similarly, the variables tab allows a user to give bounds on the acceptable values for each variable in the system. BioPSy calculates the minimum (min) and the maximum values (max) for each specises based on the time series data. By default the lower bound is equal to ```min - 0.5 * (max - min)``` and the upper bound is equal to ```max + 0.5 * (max - min)```. This is just a coarse approximation of variables bound and they should be verified by the user.

Important:
Choosing the bounds which do not bound the entire species ranges over the time series can cause incorrect results.

Important:
All the variable bounds must be defined.

"Log"
This tab displays all the events hapenning while the execution.


"Output"
Once the synthesis has started, the output tab displays the computation output in the table. Each entry of the table features the box (intervals for each parameters), the time value and the box type. Boxes are of three types: SAT the boxes satisfies parameter sysnthesis for all the time point starting from the initial one up to the current time point, UNSAT the boxes does not satisfy parameter synthesis for the current time point, UNDET it was not determined wheteher the box is one of the two above. UNSAT and UNDET boxes are not passed on for the next time point. Only SAT time boxes obtained for the last time boxes are guaranteed to satisfy the entire time series.

"Plot(2D only)"
This tab is enabled only when two parameters are chosen for synthesis. It contains graphical representation of parameter synthesis. Legend: "UNSAT" and "Undetermined" boxes have the same meaning as in the "Output" tab. "Currently explored" boxes (appear only while doing --full-synthsis) are the boxes which are SAT for some intermediate time point and will be passed on to the next time point. "SAT" boxes are the boxes satisfying the entire time series. "Unexplored" boxes (appear only with defaul algorithm) are the boxes which were not yet analysed by the SMT solver.

"Advanced options"
The advanced options button enables the user to specify the path to the dReal and ParSyn binary.

"dReal"
If the user has a different version of dReal installed, she or he can change the path to point to the binary of the different version. Also user is able to specify dReal option. By defualt only --precision=1e-3 option is used. For the full list of options run:
```
./dReal --help
```

"ParSyn"
If the user has a different version of ParSyn installed, she or he can change the path to point to the binary of the different version. By default the algorithm partitions the parameter space with the specified precision and terminates when a box satisfying the entire time series is found. Also user is able to specify ParSyn options:

-t <int> - number of CPU cores (default 4) (max 4)
--partition - partition the entire parameter space with the precision defined in BioPSy before 	evaluating. Used by default and is disabled by --full-synthsis.
--full-synthesis - performs full parameter synthesis in other words identifying all feasible sets in the defined parameter space. Can be used in combination with --partition.


