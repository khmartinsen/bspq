#!/usr/bin/env bash

# usage: bspqscript p q
echo "BSPQ master script"
P=$1
Q=$2
DIR=$(pwd)

#runs coset builder in another shell since we change the directory
builder() {
  ${DIR}/bin/cosetBuilder $P $Q $1 $2
}

accumulate() {
  ${DIR}/bin/accumulate #1
}

cd data/BS${P}_${Q}

builder mainline BTBT

