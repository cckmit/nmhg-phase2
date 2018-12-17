#!/bin/bash
# Demarcate all further settings as "local" to this batch file.
# This would ensure that any settings that we change here,
# such as the MAVEN_OPTS, won't get set permanently and would
# only last for the lifetime of this script.


# We use 'pushd' instead of 'cd', so as to store the 
# current directory so that we can navigate back 
# to it at the end of execution.

pushd domain > /dev/null
echo -------------------------------------------------------------------------------
echo                   Building Domain
echo -------------------------------------------------------------------------------
mvn $* clean install
echo -------------------------------------------------------------------------------

if [ $? -ne 0 ]
then
	echo {{{{{{{{{{{{ Module [domain] failed ! Skipping further modules.}}}}}}}}}}}}
	exit 1
fi

echo
popd > /dev/null

pushd engine > /dev/null
echo -------------------------------------------------------------------------------
echo                   Building Engine
echo -------------------------------------------------------------------------------
mvn $* clean install
echo -------------------------------------------------------------------------------

if [ $? -ne 0 ]
then
	echo {{{{{{{{{{{{ Module [engine] failed ! Skipping further modules.}}}}}}}}}}}}
	exit 1
fi
echo
popd > /dev/null

pushd integration > /dev/null
echo -------------------------------------------------------------------------------
echo      Building Integration Module \(Bods and Integration Layer only\)
echo -------------------------------------------------------------------------------
mvn $* clean install
echo -------------------------------------------------------------------------------

if [ $? -ne 0 ]
then
	echo {{{{{{{{{{{{ Module [integration] failed ! Skipping further modules.}}}}}}}}}}}}
	exit 1
fi
echo
popd > /dev/null

pushd process > /dev/null
MAVEN_OPTS=-Xmx256m -DupdatePolicy=never -Dant.target=install
echo -------------------------------------------------------------------------------
echo                   Building Process
echo -------------------------------------------------------------------------------
mvn $* clean deploy
echo -------------------------------------------------------------------------------

if [ $? -ne 0 ]
then
	echo {{{{{{{{{{{{ Module [process] failed ! Skipping further modules.}}}}}}}}}}}}
	exit 1
fi
echo
popd > /dev/null


pushd webapp > /dev/null
echo -------------------------------------------------------------------------------
echo                   Testing Webapp
echo -------------------------------------------------------------------------------
mvn $* clean test
echo -------------------------------------------------------------------------------

if [ $? -ne 0 ]
then
	echo {{{{{{{{{{{{ Module [webapp] failed ! Skipping further modules.}}}}}}}}}}}}
	exit 1
fi
echo

# Navigate back to the original directory from where this 
# script was run.
popd > /dev/null