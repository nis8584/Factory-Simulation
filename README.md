# Factory-Simulation
## Goals
This is a project with the goal to create a self adapting factory which can respond to changing parameters and tries to maximize it's capabilities.
Currently the goal is to just create a factory which works through its given tasks with basic scheduling methods (currently only FIFO).
## Usage Guide
### Specifying a facotry
In order to run the program you will need to specify a factory with a layout, tasks, steps to those tasks, individual factory nodes and the possible work steps each workstation can complete.
The specification for your factory is written in plain text and loaded during runtime by you.
The format for your factory should look like this:
#### Tasks and their steps
Format: `taskname:step1,step2,...,stepN-anotherTask:step3,step0,....`
#### A queue consisting of tasks
Format: `taskname,anotherTask,anotherTask,taskname,....`
#### Key, factory node type and graphical location
Format: `KeyTypeX,Y-KeyTypeX,Y-...`  **note:** Key should be a unique character, Type is either DispenserStation(`d`), WorkStation(`w`), DropOffBox(`b`), X and Y refer to a 5x5 grid in which the stations will be placed for animation purpuses (range 0-4 for each)
#### Connection between nodes
Format: `fromXtoXtoXto-fromYtoYto`  **note:** *from* and *to* are the previously established keys (single characters), X and Y are single digit integers depicting the travel cost between stations *from* and *to*
#### Task and cost specification for workstations
Format: `keyXstep1:Ystep2-keyYstep2:Xstep3:Ystep56` **note:** key as previouls established single character, X and Y as single intigers depicting cost to do the following step
### Selecting a level of concurrency
After successfully loading your facotory you can select the level of concurrency for the simulation. This number selects how many workstations can do work at the same time. If it is set to 2 that then means that the `Scheduler` will send out 2 new tasks at startup and then only send new tasks, once the old ones are finished and on their way to the `DropOffStation`.  
###S tart the Simulation
Now you can start your simulation and watch the dots zip around the factory as the tasks get completed! 
### Usage tips
Try to not do mistakes in specifying your factory, as current error feedback isn´t that great.
If the simulation speed is too fast or too slow you can change the time unit in `GlobalConstants`.
### Example
The factory I used for testing and developing is specified like this:\
`auto:drehen,malen-buch:schreiben,drucken`\
`buch,buch,auto,auto,buch,auto,buch,auto,buch,buch`\
`ad0,0-bw0,2-cw2,0-dw0,4-ew4,2-fb4,4-gw2,2`\
`a1b-b1c1d-c1a-d1f1g-f1e-e1c-g1e`\
`b3drehen:2schreiben-c1malen:5drucken-e3drehen:2schreiben-d1malen:5drucken-g1drehen:1drucken`
