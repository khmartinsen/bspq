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

	string file(argv[1]);
	ifstream infile(file + ".ao");
	
	map <int,long> count;
	int number;

	// first two lines are the location of the first and last zero
	infile >> number >> number;

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
	
	file = file.substr(0, file.length() - 3);

	ofstream outfile(file + ".ao");

	for (auto const& x : count) {
		outfile << x.first << ", " << x.second << endl;
	}

	outfile.close();
}
