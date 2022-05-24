/*
 * Simple accumulate function with commandline usage
 *
 * could try using an ordered map and then sorting it at the end
 * or a more clever solution where we have an array with an appropriate size
*/

#include<iostream>
#include<string>
#include<map>
#include<fstream>
using namespace std;

int main (int argc, char *argv[]) {

	const string file(argv[1]);
	ifstream infile(file + ".ri");
	
	map <int,long> count;
	int number;

	// first number is the location of the zero
	infile >> number;

	while (infile >> number) {
		// skip negative numbers (only produced in error)	
		if (number >= 0) {
			auto search = count.find(number);
			if (search != count.end()) {
				search->second++;
	
			}
			else {
				count.insert(pair<int,long>(number,1));
			}
		}
	}
	infile.close();

	// print the counts
	ofstream outfile(file + ".ao");

	for (auto const& x : count) {
		outfile << x.second << ", ";
	}

	outfile.close();
}
