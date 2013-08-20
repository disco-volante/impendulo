#! /bin/bash

git add . -A
git commit -m "$1"
git --set-upstream push origin master 
git --set-upstream push backup master
