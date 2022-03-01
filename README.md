# Baumslag-Solitar Group - Helper Programs

The following tools are used to create and view coordinates on cosets in a given BS(p,q). \
It is recommended to use integers p and q such that p < q and p divides q.

## Data and File Structure Standards
Coset data files have extension .ri for raw input. Accumulated data files have extension .ao for accumulated output.
A coset data file contains the location of the left most relative zero on the first line. The folowing
lines contain the relative coordinates of the coset separated by a new line character.\
The file is named by its path in the tree of BS(p,q). The file is placed in a folder named BSP_Q.
Ex: The coset associated with BTBTbbt in BS(2,4) has the directory path "BS2_4/BTBTbbt.ri".

## Baumslag-Solitar Cayley Sheet Viewer
The graphical viewer is used to examine the coset data files in a Cayley graph style.
Enter the p and q for the desired group. Then enter the desired incremental paths, one move at a time (not not enter .ri file names). The program will 
automatically determine if the sheet is going up or down based on the second path inputted. \
For example if path 1 is BTbbt and path 2 is BTbbtBT then the direction is determined from the last BT and 
the sheet will display the data going down from the BT.

## Brick Laying Algorithm
This program creates the initial coset mainline for the group based on p and q.
Usage: ./bricklaying p q distance
Outputs: mainline.ri
Where distance is an integer value from the relative zero to the end of the data. However distance will increase to the next multiple of q.
The output data is symmetrical

## Accumulate
Accumulate creates a csv compatible file that contains the frequency of each coordinate in a file.
Usage: ./accumulate filename.ri
Outputs: filename.ao

Note that Windows is not supported due to file names be case-insensitve.
