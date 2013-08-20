#! /bin/bash

git add . -A
<<<<<<< HEAD
git commit -m "$1"
git push origin 
git push backup 
=======
git commit -m $0
git push --set-upstream origin master
git push --set-upstream backup master
>>>>>>> 40ab269ccb1f9103374795214338660284b50288
