# Baumslag-Solitar Group - Helper Programs

The following tools are used to create and view coordinates on cosets in a given BS(p,q).

## Data and File Structure Standards
A coset data file contains relative coordinate integers separated by a new line character.\
The file is named by its path in the tree of BS(p,q). The file is placed in a folder named BSP_Q.

Ex: The coset associated with BTBTbbt in BS(2,4) has the directory path "BS2_4/BTBTbbt".

## Baumslag-Solitar Cayley Sheet Viewer
This program is used to view the coset data files in a Cayley graph style.
Enter the p and q for the desired group. Then enter the file names as described above. The program will 
automatically determine if the sheet is going up or down based on the second file's last move. \
For example if File 1 is BTbbt and file 2 is BTbbtBT then the direction is determined from the last BT and 
the sheet will display the data going down from the BT.


## Brick Laying Algorithm

This program creates the initial coset for the group based on p and q.

#### Algorithm Overview: In progress.

## Compiling Instructions (In progress)
Requires at least Java 11 and JavaFX 11.

#### Manually
add minimal instructions such as java --module-add

