#!/bin/bash
current_dir=$PWD
CLASSPATH="$current_dir"/target/classes/*:"$current_dir"/lib/*:"$current_dir"/etc/
java -cp $CLASSPATH main.java.irdp.demo.tutorialDA.SampleAppl -f "$current_dir"/etc/benchproc-"$3" -n "$4" -qos "$1" -ops 2000 -nodes "$3" -bench "$5" -obj "$2" -delay 1
