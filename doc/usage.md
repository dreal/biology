How to Use BioPSy
=======

The BioPSy GUI is launched using the BioPSy JAR file ([Java JDK 6 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html) required):

```
java -jar BioPSy.jar
```
####SBML model

The user can browse for a model file which should be in `SBML` (System Biology Markup Language) format. Once the file is selected it will be displayed in the ***SBML*** tab. If there are errors parsing the model file they will be displayed.

####Time series
Time-series data file should contain experimental data in `CSV` (Comma Separated Values) format. All species defined in the `SBML` model must be present in the time series file.

**Important:**
Avoid using `Time` as a name for species in `SBML` file as it is a reserved word which is used to represents time values.

**Important:**
The time series data must contain ***at least*** two non empty data rows and the first data row must be ***well defined*** (none of the places can be empty).

Once the time series file is selected, it will be displayed in the table in ***Time series*** tab. The user is allowed to modify chosen time series. 

*Note:*
In order for changes to take place the file should be saved before the execution. 

**Save** button overwrites chosen `CSV` file and **Save as** button let's user specify the name of the file to save modified time series.

####Parameters

After the model file is parsed the list of parameters are displayed in ***Parameters*** tab. Here a user can select parameters in the model to synthesise.  

**Important:**
At least one parameter must be selected.

For each synthesised parameter, the user is also able to define a lower and upper bounds which used to constrain the parameter search space. Also user can specify ***precision*** for each selected parameter individually (default 10^-3). Precision regulates the termination condition (the search will not continue for the parameter if the size of the currently explored interval is smaller than defined precision).

*Note:*
The changes will not take place if bounds and precision values are changed but parameter is not selected.

*Note:*
Both lower and upper bounds can be the same therefore allowing checking the parameter values instaed of performing synthesis on the intervals. In this case the precision value will be ignored.

####Variables
Similarly, the ***Variables*** tab allows a user to give bounds on the explored ranges for each variable in the system. `BioPSy` calculates the minimum (`min`) and the maximum values (`max`) for each specises based on the time series data. By default the lower bound is equal to `min - 0.5 * (max - min)` and the upper bound is obtained as `max + 0.5 * (max - min)`. This is just a coarse approximation of variables bounds and they should be verified by the user and changed if necessary.

**Important:**
All the variable bounds must be defined.

**Important:**
Choosing the bounds which do not bound the entire species ranges over the chosen time series can cause incorrect results.

Also the user can specify a ***noise*** value for each species (defualt 1e-1).

####Log
All the events hapenning during the execution are displayed here.

####Output
Once the synthesis has started, the ***Output*** tab displays the computation output. Each entry of the table features the box (vector of intervals), the time value and the box type. Boxes are of three types:
+ `SAT` boxes satisfy parameter sysnthesis for all the time point starting from the initial one up to the ***current*** time point
+ `UNSAT` the boxes does not satisfy parameter synthesis for the current time point, 
+ `UNDET` it was not determined wheteher the box is one of the two above. 

*Note:*
Only `SAT` boxes obtained for the last time boxes are guaranteed to satisfy the entire time series.

####Plot(2D only)
This feature is enabled only when two parameters are chosen for synthesis. This tab contains graphical representation of parameter synthesis. Legend: 

|Name|Description|
|---|---|
|UNSAT| boxes not satisfying parameter synthesis|
|Undetermined| boxes for which it was not determined whether the box satisfies a time point from the series|
|Currently explored| boxes which are SAT for some intermediate time point and will be passed on to the next time point (appear only if `--full-synthsis` is specified)|
|SAT| boxes satisfying the entire time series|
|Unexplored| boxes which were not yet analysed by the SMT solver (appear only with defaul algorithm)|

####Advanced options
The **Advanced options** button allows the user to specify the path to the `dReal` and `ParSyn` binary.

#####dReal
If the user has a different version of `dReal` installed, she or he can change the path to point to the binary of the different version. Also user is able to specify `dReal` option. By default only `--precision=1e-3` option is used. For the full list of options run:
```
./dReal --help
```

#####ParSyn
`ParSyn` is the computation engine for `BioPSy`. If the user has a different version of `ParSyn` installed, she or he can change the path to point to the binary of the different version. By default the algorithm partitions the parameter space with the specified precision and terminates when a box satisfying the entire time series is found. Also user is able to specify following `ParSyn` options:

```
-t <int> - number of CPU cores (default 4) (max 4)
--partition - partitions the entire parameter space with the precision defined in BioPSy before evaluating. Used by default and is disabled by --full-synthesis.
--full-synthesis - performs full parameter synthesis. In other words, it identifies all feasible sets in the defined parameter space. Can be used in combination with --partition.
```

####Execution
Computation can be executed by pressing **Run** button.

