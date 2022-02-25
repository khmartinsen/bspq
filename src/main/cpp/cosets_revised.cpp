/* Computes growth on cosets for BS(p,q)      *
 * using variants of the McCann-Schofield     *
 * Algorithm. Note that relative origins are  *
 * used. Clumsily coded in April 2008 by Eric *
 * Freden with debugging help by Jared Adams  *
 * and Mike Grady. Copyleft under the GPL.    */


// Feb 23, 2021 - Kevin Martinsen Modifications
// BT<b> input to BTbt<b> output

// code assumes there are data points around the relative 0 in the input file


#include<iostream>
#include<vector>
#include<fstream>
#include<string>
#include<algorithm>
using namespace std;

// must use a pointer to pass the vector argument
void between(vector<int>&, const int); // change const bool to gap (p or q)
int parseOffset(const string);

int main(int argc, char *argv[]) {
	if (argc <= 3) {
		printf("Usage: cosetbuilder p q path.ri move\n");
		printf("Outputs: path+move.ri\n");
		printf("Example cosetbuilder 2 4 BT.ri BT outputs BTBT.ri\n");
		return 0;
	}

	int p = atoi(argv[1]);
	int q = atoi(argv[2]);

	ifstream infile(argv[3]);

	string startPath (argv[3]);
	startPath = startPath.substr(0,startPath.length() - 3);

	if (startPath == "mainline") {
		startPath = "";
	}
	
	string move = argv[4];
	int offset = parseOffset(move);
	int gap; // for between function
	int mod; // for reading the input file
	
	if (move.at(move.length()-1) == 't') {
			gap = p;
			mod = q;
	}
	else if (move.at(move.length()-1) == 'T') {
			gap = q;
			mod = p;
	}
	else {
		// invalid move
		return 1;
	}

	double scaling = (double)gap / (double)mod;

	// Read the input file
	int firstZero;
	infile >> firstZero;	

	vector<int> inputCoset; //preallocate?
	
	int coordinate;
	infile >> coordinate; //burn the second input of the file, the location of the last zero	

	while (infile >> coordinate) {
			inputCoset.push_back(coordinate);
	}
	infile.close();

	int startIndex = (firstZero + offset + mod) % mod; //could give a negative start index

	int outputSize = ((inputCoset.size() - startIndex) / mod) * gap + 1;
	//int outputSize = (int)((inputCoset.size() - 1) * scaling + 1);
	
	// is there a better way to make sure we dont create something too big or just max out the vector size every time?

	/*
	if (outputSize > inputCoset.max_size() || outputSize <= 0) {
		outputSize = inputCoset.max_size();
	}
	*/

	vector<int> outputCoset(outputSize); //size scale like p/q or q/p

	int diff;
	if (firstZero + offset < 0) {
		diff = -offset;
	}
	else {
		diff = inputCoset[firstZero + offset]; //assuming that is non-negative, maybe we can guarantee that?
	}

	for (int i = startIndex, j = 0; i < inputCoset.size(), j < outputCoset.size(); i+=mod, j+=gap) { // && start < outputCoset?
		outputCoset[j] = inputCoset[i] - diff;
	}

	// between function
	between(outputCoset, gap);
	// output to file
	ofstream outfile(startPath + move + ".ri");

	firstZero = ((firstZero - offset) / mod) * gap;

	
	// ADD first zero and last zero location
	outfile << firstZero << "\n";
	outfile << firstZero << "\n"; //placeholder

	for (int i = 0; i < outputCoset.size(); i++) {
			outfile << outputCoset[i] << "\n";
	}	

	outfile.close();

	return 0;
}

// fills in the coordinates between two horobrick edges
// needs to be smarter!
void between(vector<int>& coset, const int gap) {
	int i;
	int j;
	for (i=0; i < coset.size() - gap; i+=gap) {
		for (j=1; j<gap; j++) {
			if (coset[i]+j < coset[i+gap]+gap-j)
				coset[i+j] = coset[i]+j;
			else
				coset[i+j] = coset[i+gap]+gap-j;
		}
    }
}


int parseOffset(const string move) {
	int offset = 0;
	for (int i = 0; i < move.length(); i++) {
		if (move.at(i) == 'b') { offset++; }
		else if (move.at(i) == 'B') { offset--; }
	}
	return offset;
}
