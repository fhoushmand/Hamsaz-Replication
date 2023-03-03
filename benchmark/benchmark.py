import random
import sys, os
if __name__ == '__main__':

#     obj = "bank"
    obj = "courseware"
    newBench = True
    script_dir = os.path.dirname(os.path.realpath('__file__')) #<-- absolute dir the script is in
    balance = 0
    numberOfDeposits = 0
    numberOfQueries = 0
    numberOfWithdraws = 0
    numberOfOps = int(sys.argv[1])
    numberOfNodes = int(sys.argv[2])
    protocol = "block"
    op = "deposit"




    if obj == "bank":
        benchFileName = 'calls-'+str(numberOfNodes)+'-'+str(numberOfOps)+'.txt'
        if os.path.exists(os.path.join(script_dir, benchFileName)):
            newBench = False
        if newBench:
            f = open(os.path.join(script_dir, benchFileName),'w')
            for i in range(0,numberOfOps):

                if i == 0:
                    f.write("%s:call deposit(100)\n"%(i))
                    balance += 100
                    numberOfDeposits +=1
                    continue

                rand = random.randint(1,20)
                if rand < 15:
                    op = "deposit"
                    numberOfDeposits += 1
                elif rand > 15:
                    op = "withdraw"
                    numberOfWithdraws += 1
                else:
                    op = "query"
                    numberOfQueries += 1
                if op != "query":
                    amount = random.randint(10,20)
                    f.write("%s:call "%(i)+op+"(" + "%d" % (amount) +")\n")

                else:
                    f.write("%s:call "%(i)+op+"()\n")
                if op == "deposit":
                    balance += amount
                elif op == "withdraw":
                    balance -= amount

            f.write("exit-sim\n")
            f.write("#total balance: "+str(balance)+"\n")
            f.write("#total number of calls: "+str(int(sys.argv[1]))+"\n")
            f.write("#number of queries: "+str(numberOfQueries)+"\n")
            f.write("#number of deposits: "+str(numberOfDeposits)+"\n")
            f.write("#number of withdraws: "+str(numberOfWithdraws)+"\n")
            f.close()


    elif obj == "courseware":
        benchFileName = 'calls-courseware-'+str(numberOfNodes)+'-'+str(numberOfOps)+'.txt'
        if os.path.exists(os.path.join(script_dir, benchFileName)):
            newBench = False
        if newBench:
            f = open(os.path.join(script_dir, benchFileName),'w')
            for i in range(0,numberOfOps):
                oprand = random.randint(1,100)
                if oprand <= 50:
                    student = random.randint(1,20)
                    op = "register"
                    f.write("%s:call "%(i)+op+"(" + "%d" % (student) +")\n")
                elif oprand > 50 and oprand < 60:
                    course = random.randint(100,105)
                    op = "addCourse"
                    f.write("%s:call "%(i)+op+"(" + "%d" % (course) +")\n")
                elif oprand > 60 and oprand < 80:
                    student = random.randint(1,20)
                    course = random.randint(100,105)
                    op = "enroll"
                    f.write("%s:call "%(i)+op+"(" + "%d,%d" % (student,course) +")\n")
                elif oprand > 80 and oprand < 90:
                    course = random.randint(100,105)
                    op = "deleteCourse"
                    f.write("%s:call "%(i)+op+"(" + "%d" % (course) +")\n")
                elif oprand > 90 and oprand < 100:
                    course = random.randint(100,105)
                    op = "query"
                    f.write("%s:call "%(i)+op+"()\n")
            #f.write("#total balance: "+str(balance)+"\n")
            #f.write("#total number of calls: "+str(int(sys.argv[1]))+"\n")
            #f.write("#number of queries: "+str(numberOfQueries)+"\n")
            #f.write("#number of deposits: "+str(numberOfDeposits)+"\n")
            #f.write("#number of withdraws: "+str(numberOfWithdraws)+"\n")
            f.write("exit-sim\n")
            f.close()
            exit()

    procsFileName = 'benchproc-'+str(numberOfNodes)


    procs = open('\\mnt\\c\\Users\\farzi\\Documents\\Projects\\Hamsaz-Thesis\\CoordinationSynthesis\\etc\\'+procsFileName,'w')
    port  = 25000
    baseIP = 7
    for i in range(0, numberOfNodes):
        procs.write(str(i)+" localhost "+ str(port+100)+"\n")
        port += 10
        baseIP += 1
    procs.close()


    if obj == "bank":
        simDir = "\\mnt\\c\\Users\\farzi\\Documents\\Projects\\Hamsaz-Thesis\\CoordinationSynthesis\\"+"run-bank\\"+protocol+"\\"+str(numberOfNodes)+"\\"+str(numberOfOps)+"\\"
    elif obj == "courseware":
        simDir = "\\mnt\\c\\Users\\farzi\\Documents\\Projects\\Hamsaz-Thesis\\CoordinationSynthesis\\"+"run-courseware\\"+protocol+"\\"+str(numberOfNodes)+"\\"+str(numberOfOps)+"\\"

    if not os.path.exists(simDir):
        os.makedirs(simDir)


    runner  = open(simDir+'runner.sh','w')
    runner.write("for f in job*.sh; do\n")
    runner.write('''\t echo "sbatch $f &&"\n''')
    runner.write('''done''')
    runner.close()
    for i in range(0,numberOfNodes):
        runfilename = 'run'+str(i)+'-'+protocol+'-'+str(numberOfNodes)+'-'+str(numberOfOps)+'.sh'
        jonfilename = 'job'+str(i)+'-'+protocol+'-'+str(numberOfNodes)+'-'+str(numberOfOps)+'.sh'
        run = open(simDir+runfilename,'w')
        run.write("#!/bin/bash\n")
        run.write("CLASSPATH="+script_dir+":"+script_dir+"lib/appia-core-4.1.2.jar:"+script_dir+"lib/log4j-1.2.14.jar:"+script_dir+"etc/\n")
        run.write("java -cp $CLASSPATH irdp.demo.tutorialDA.SampleAppl -f "+script_dir+"etc/"+procsFileName+" -n " +str(i)+ " -qos "+ protocol+" -ops "+ str(numberOfOps) +" -nodes "+ str(numberOfNodes) +  " -bench "+script_dir+"etc/"+benchFileName)
        job = open(simDir+jonfilename,'w')
        job.write("#!/bin/bash\n")
        job.write("./"+runfilename+"\n")
        run.close()
        job.close()

    print(balance)

