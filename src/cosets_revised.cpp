/* Computes growth on cosets for BS(p,q)      *
 * using variants of the McCann-Schofield     *
 * Algorithm. Note that relative origins are  *
 * used. Clumsily coded in April 2008 by Eric *
 * Freden with debugging help by Jared Adams  *
 * and Mike Grady. Copyleft under the GPL.    */


// Feb 23, 2021 - Kevin Martinsen Modifications
// BT<b> input to BTbt<b> output


#include<iostream>
#include<vector>
#include<fstream>
#include<string>
#include<algorithm>
using namespace std;

void between(const int, const int, const int, const bool, vector<int>&);
// must use a pointer to pass the vector argument

int main() {
    long i, size;
    string line;
    int p,q;
    int offset, diff;
    int initcond = 0; // offsets the output vector for moves that create a new intial condtion zone, left of the vector
    bool up = true;


    string inputFile, outputFile, move;

    cout << "Input file name (with .input): ";
    cin >> inputFile;

    cout << "Output file name (with .input): ";
    cin >> outputFile;
    
    cout << "Number for p: ";
    cin >> p;
    cout << "Number for q: ";
    cin >> q;
    
    cout << "Input move: ";
    cin >> move;

    // TODO make general instead of just BS2,4. Change to read the characters B or b and then a T or t.
    if (move == "t") {
	    offset = 0;
	    diff = 0;

    }
    else if (move == "bt") {
	    offset = 1;
            diff = 1;
    }
   
    else if (move == "bbt") {
	    offset = 2;
            diff = 2;
	    initcond = 1;
    }
    
    else if (move == "u") {
	    offset = 2;
            diff = 0;
    }
    
    else if (move == "Bt") {
	    offset = 3;
            diff = 1;
	    initcond = 1;
    }
    
    else if (move == "T") {
	    offset = 0;
            diff = 0;
	    up = false;
    }
    
    else if (move == "BT") {
	    offset = 1;
            diff = 1;
	    initcond = 1;
	    up = false;
    }
    
    else {
	    cout << "Invalid move entered.\n";
	    return 0;
    }
    
    // Open input file for read
    ifstream infile (inputFile); // user can edit this name
    // Declare storage vector (normal arrays are too small)
    vector<int> b(10000002); // user must know size in advance? 

    i = 0;
    // Fill in the vector
    while (! infile.eof()) {
	getline (infile,line); // read the input numbers
      	b[i] = atoi(line.c_str()); // must changed to int type, etc
	i++;
    }

    size = i;
    infile.close();
 
    // Declare vectors for the p adjacent lower cosets
    vector<int> c(size);
    c[0] = 0; // only used when initcond = 1
    
    if (up == true)
    {
	//going up a level, every q we go up and insert it every p in the resulting coset
    	for (i=0; i<=size/q; i++) {
	c[p*(i + initcond)] = b[q*i + offset] - diff;
    	}	
    
    }
    else 
    {
	// going down a level, every p we go down and insert it at every q in coset c
    	for (i=0; i<=size/q; i++) {
	c[q*(i + initcond)] = b[p*i + offset] - diff;
    	}

    }

    between(p,q,size,up,c); // 4th input is true for lowercase t up
    // Write the vectors containing geodesic lengths
    // to the appropriate output files
    ofstream outfile; 
    outfile.open (outputFile);
    for (i=0; i<c.size(); i++ ) {
	outfile << c[i] << "\n";
    }
    outfile.close();
    
    return 0;
}

// Function to compute the geodesic lengths in between 
// the vertices already found.
void between(const int p, const int q, const int size, const bool t, vector<int>& k) {
    // Given all the geodesic lengths from the input coset K,
    // and the geodesic lengths for some of the vertices on
    // a coset immediately "below" K, let v be a vertex on
    // such a coset "below" K. A geodesic path for v
    // comes from either the left or right
    // so we calculate each length and choose
    // the shorter path length
    
    // True is going up (little t), false is going down (capital T)
    int gap;

    if (t == true) {
        gap = p;
    } else {
	gap = q;
    }
 
    long i;
    int j;
    for (i=0; i<size-gap; i+=gap) {
	for (j=1; j<gap; j++) {
	    if (k[i]+j < k[i+gap]+gap-j)
		k[i+j] = k[i]+j;
	    else
		k[i+j] = k[i+gap]+gap-j;
	}
    }
}
