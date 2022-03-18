// code assumes there are data points around the relative 0 in the input file
// also data creation works for when p does not divide q, but may give negative values and the first zero location will be incorrect

#include<iostream>
#include<vector>
#include<fstream>
#include<string>
#include<algorithm>
using namespace std;

void between(vector<int>&, const int);
int parseOffset(const string);
vector<string> parsePath(const string);

int main(int argc, char *argv[]) {
	if (argc <= 3) {
		printf("Usage: cosetbuilder p q path moves\n");
		printf("Outputs: path+firstmove.ri iterately to path+moves.ri\n");
		printf("Example: cosetbuilder 2 4 BTbbt BT -> BTbbtBT.ri\n");
		return 0;
	}

	int p = atoi(argv[1]);
	int q = atoi(argv[2]);

	
	string inputName(argv[3]);
	
	ifstream infile(inputName + ".ri");
	if (!infile) {
		cout << "Could not open file: " << inputName << ".ri" << endl;
		return 1;
	}
	infile.close();

	
	vector<string> movesList = parsePath(argv[4]);

	for (string move : movesList) {
		string outputName;
		if (inputName == "mainline") {
			outputName = move;
		}
		else {
	       		outputName = inputName + move;
		}
		// check existence of files
		ifstream testfile(outputName + ".ri");
		if (testfile) {
			//file found already
			cout << outputName << " exists!" << endl;
			testfile.close();
			inputName = outputName;
			continue;
		}


		int offset = parseOffset(move);
		int outputGap; // for between function
		int inputGap; // for reading the input file

		if (move.at(move.length()-1) == 't') {
				outputGap = p;
				inputGap = q;
		}
		else if (move.at(move.length()-1) == 'T') {
				outputGap = q;
				inputGap = p;
		}
		else {
			// invalid move
			return 1;
		}

		ifstream infile(inputName + ".ri");

		// Read the input file
		int firstZero;
		infile >> firstZero; // first integer in the file is the first/left relative zero for the coset
		vector<int> inputCoset; //preallocate?
		int coordinate;

		while (infile >> coordinate) {
				inputCoset.push_back(coordinate);
		}
		infile.close();

		// create the output array
		// if the output size is at the max then the start array needs to be further in so the data is symmetrical
		// maybe it starts at 1000 blocks into the data, or we need to be using multiple arrays to go past the max
		int startIndex = (firstZero + offset + inputGap) % inputGap;
		int outputSize = ((inputCoset.size() - startIndex - 1) / inputGap) * outputGap + 1; // what if its bigger than int?
		vector<int> outputCoset(outputSize); //size scale like p/q or q/p

		int diff;
		if (firstZero + offset < 0) {
			diff = -offset;
		}
		else {
			diff = inputCoset[firstZero + offset]; //assuming that firstZero is non-negative
		}

		// iterate over the horobrick edges from the input coset to the output coset
		for (int i = startIndex, j = 0; i < inputCoset.size() && j < outputCoset.size(); i+=inputGap, j+=outputGap) {
			outputCoset[j] = inputCoset[i] - diff;
		}

		between(outputCoset, outputGap);

		// should this be firstZero - offset?
		// indicies in arrays start at 0 so we do not need to add +1
		firstZero = ((firstZero + offset) / inputGap) * outputGap;
		
		// write to the output file
		ofstream outfile(outputName + ".ri");
		outfile << firstZero << "\n";
		for (int i = 0; i < outputCoset.size(); i++) {
				outfile << outputCoset[i] << "\n";
		}	
		outfile.close();

		inputName = outputName;
    }

	return 0;
}

// fills in the coordinates between two horobrick edges
void between(vector<int>& coset, const int outputGap) {
	int i;
	int j;
	for (i=0; i < coset.size() - outputGap; i+=outputGap) {
		for (j=1; j<outputGap; j++) {
			if (coset[i]+j < coset[i+outputGap]+outputGap-j)
				coset[i+j] = coset[i]+j;
			else
				coset[i+j] = coset[i+outputGap]+outputGap-j;
		}
    }
}

vector<string> parsePath(const string path) {
	vector <string> moves;
	int start = 0;
	for (int i = 0; i < path.length(); i++) {
		if (path.at(i) == 't' || path.at(i) == 'T') {
			moves.push_back(path.substr(start, i + 1 - start));
			start = i + 1;
		}
	}
	return moves;
}

int parseOffset(const string move) {
	int offset = 0;
	for (int i = 0; i < move.length(); i++) {
		if (move.at(i) == 'b') { offset++; }
		else if (move.at(i) == 'B') { offset--; }
	}
	return offset;
}
