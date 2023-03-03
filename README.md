### Requirment
Make sure you have maven installed:
`mvn`

### Running an example
To run the server on each replica, use the following command:

`sh run.sh protocol usecase #nodes id dir/to/benchmark [dir/to/table]`

`protocol := "block" | "non-block"`

`usecase := "counter" | "register" | "2pset" | "bank" | "courseware" | "payroll"`


if the conflict table is provided,
the static analysis is bypassed and
the given information is used to
instantiate the protocol.

Specification of the existing 
replicated object are located in `robject.usecase`.
To add new usecases, implement the sequential object logic 
for the methods of the object along with the
guard for each method. The methods must be annotated with
their corresponding gurds. See the `BankAccountObj` under
`robject.usecase` package for an example of how to add a new usecase.



You can install the `dot` utility from `graphviz` in order to vizualize 
the result of the static analysis. As an example, below you can
see the generated conflict graph and the corresponding maximal cliques
for the courseware usecase.
https://www.graphviz.org/download/


The output of the analysis result is written to `tmp` directory.
It will contain conflict and dependency graphs (if `dot` is provided)
, as well as the analysis tables.

The following is an example of the generated conflict graph for the 
courseware usecase:
![Conflict Graph](/img/conflict.png)
Below, are two maximal cliques for the same usecase:
![Cliques](/img/cliques.png)


### Benchmark Format
Benchmarks are `.txt` files with the following format:

`#id:call method_name(args)`

`id` is the unique id for each call.

`args` is the list of comma separated arguments.

Test benchmarks are available in the `benchmark` directory.