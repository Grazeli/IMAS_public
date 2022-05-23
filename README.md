# IMAS_TEAM07
## How to execute the code
The easiest way is to use the already compiled IMAS_07.jar that can be found in the project folder. To execute it open a terminal in the IMAS_TEAM07 folder and simply run:

`java -jar IMAS_07.jar -gui "manager:agents.ManagerAgent;user:agents.UserAgent"`

Check the available  [commands](#commands)
> Note: Java Version >= 1.8

## Compilation instructions
If you don't want to use the distributed .jar you can also compile it yourself. First it is necessary to move insided the IMAS_TEAM07 folder. Then,  open a terminal and execute the following commands corresponding to your operating system:

### Instructions for Linux/macOS
1.  Compile the project:
`javac -cp lib/commons-codec-1.3.jar:lib/jade.jar:lib/jFuzzyLogic.jar:lib/jFuzzyLogic_core.jar -d bin/ src/agents/*.java src/behaviours/*.java src/others/*.java  src/format/*.java`

2.  Execute Jade with user and manager agents:
`java -Dfile.encoding=UTF-8 -cp bin/:lib/commons-codec-1.3.jar:lib/jade.jar:lib/jFuzzyLogic.jar:lib/jFuzzyLogic_core.jar jade.Boot -gui "user:agents.UserAgent;manager:agents.ManagerAgent"`

Alternatively, you can just run `./script.sh` once located in the project file to build and run the project with a single script.

### Instructions for Windows
1.  Compile the project:
`javac -cp lib\commons-codec-1.3.jar;lib\jade.jar;lib\jFuzzyLogic.jar;lib\jFuzzyLogic_core.jar -d bin\ src\agents\*.java src\behaviours\*.java src\others\*.java src\format\*.java`

2.  Execute Jade with user and manager agents:
`java -Dfile.encoding=UTF-8 -cp bin\;lib\commons-codec-1.3.jar;lib\jade.jar;lib\jFuzzyLogic.jar;lib\jFuzzyLogic_core.jar jade.Boot -gui "user:agents.UserAgent;manager:agents.ManagerAgent"`

Alternatively, you can just run `.\script.bat` once located in the project file to build and run the project with a single script.

## Commands

Once the code is executed, the user is prompted to input a new command. The available commands are:
- **I**: set a configuration file. Afterwards the user is prompted to input the configuration filename.
- **D**: make a request. Afterwards the user is prompted to input the requests filename. An application must have been confgured with *I* before running *D*
- **L**: set the debug level (0-3). As the debug level increases, more information is printed in the console.
- **Q**: quit the program execution.
- **H**: show a help menu with information about the available commands.

> ***Note:*** configuration and request files must be inside the **files** folder
