#!/usr/bin/env bash

echo "BSPQ Accumulate CSV Creator"
echo -n "Enter p: "
read P
echo -n "Enter q: "
read Q
echo -n "Enter output file name: "
read OUT
echo -n "Enter cosets:"
read COSETS

DIR=$(pwd)

OUT=${DIR}/data/$OUT


bricklaying() {
  ${DIR}/bin/Bricklaying $P $Q 100000
}

#runs coset builder in another shell since we change the directory
builder() {
  ${DIR}/bin/cosetBuilder $P $Q $1 $2
}

accumulate() {
  ${DIR}/bin/accumulate $1
}

# writes the coset name followed by the data then starts a new line
writeCSV() {
	echo -n $1, >> $OUT
	cat ${1}.ao >> $OUT
	echo >> $OUT
}

cd data/BS${P}_${Q}

for C in $COSETS; do
	if [[ -f ${C}.ao ]]; then
		writeCSV $C
	elif [[ -f ${C}.ri ]]; then
		accumulate $C
		writeCSV $C
	else
	  if [[ -f mainline.ri ]]; then
	    bricklaying
		builder mainline $C
		accumulate $C
		writeCSV $C
	fi
done
