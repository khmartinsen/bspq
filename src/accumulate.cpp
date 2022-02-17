/*
 * Simple accumulate function with commandline usage
*/

#include<iostream>
#include<string>
#include<vector>
#include<fstream>
using namespace std;

int main (int argc, char *argv[]) {

	ifstream infile(argv[1]); 
	vector<unsigned long> count = {0};
	string line;
	
	while (! infile.eof()) {
		getline (infile,line);
		int number = atoi(line.c_str());
		if (number < count.size() - 1) {
			count[number]++;
		}
		else {
			count.push_back(1);
		}
	}
	infile.close();

	for (int i = 0; i < count.size(); i++){
		count[i] *= 2;
	}
	count[0] = 1;

	// print the counts
	for (int i = 0; i < count.size(); i++) {
		cout << i << ", " << count[i] << endl; 
	}	

}
