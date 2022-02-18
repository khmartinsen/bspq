/*
 * Simple accumulate function with commandline usage
*/

#include<iostream>
#include<string>
#include<map>
#include<fstream>
using namespace std;

int main (int argc, char *argv[]) {

	ifstream infile(argv[1]);
	
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
	
	for (auto const& x : count) {
		cout << x.first << ", " << x.second << endl;
	}
}
