###############################################################################################
# AUTHOR 	:: Priyank Gupta
# DATE     		:: 20 September 2008
# E-MAIL  		:: priyank.gupta@tavant.com
#DESCRIPTION 	:: This is a perl script which will read properties from a properties file and will archive every  WAR built by continuum; so that it can be deployed if so required.
############################################################################################### 

#_______________________________________________________________________________________________________________________
#Used to use functionality like dirname and basename

use File::Basename;     # Standard module to extract the file basename

#________________________________________________________________________________________________________________________
# global variables to be used in program.

#log file variables
$logFileToUse = ".\\WarFileCopy.log";

#Property file related properties
$propertyFilePath = ".\\aVersion.properties";
$isPropFileOpen = 1;
%propertyHash = ();

#Success Exit Variable
$cleanExit = "NOERROR";

#logger related variables
$isLogFileOpen = 1;
$end_marker = "***********************************************END************************************************";


#________________________________________________________________________________________________________________________
#This method is the logger method which will log anything and everything passed to it as a parameter from anywhere in code. Hail to logger boy!
#________________________________________________________________________________________________________________________
sub loggIt
{
	$currLogMessage = "[LOG] ::::: $_[0]\n";
	if($isLogFileOpen == 1)
	{
		print LOGFILE $currLogMessage;
	}
	else
	{
		print $currLogMessage;
	}
}

#________________________________________________________________________________________________________________________
#This method will open the log file to be written into
#________________________________________________________________________________________________________________________
sub openLogFile
{
	open (LOGFILE, ">$logFileToUse") or $isLogFileOpen = 0;
	if ($isLogFileOpen == 0)
	{
		print "Could not open log file to log activity; countinuing without log file.\n";
	}
}

#________________________________________________________________________________________________________________________
#This method will open the properties file from which properties were picked from
#________________________________________________________________________________________________________________________
sub openPropertiesFile
{
	open (PROPFILE, "<$propertyFilePath") or $isPropFileOpen = 0;
	
	#Since we could not open properties file; it's pointless to continue so lets quit the program.
	if($isPropFileOpen == 0)
	{
		&terminateProgram("Cannot Archive; no properties file.");
	}
}

#________________________________________________________________________________________________________________________
#This method will close the log file in which logs were written into
#________________________________________________________________________________________________________________________
sub closeLogFile
{
	if($isLogFileOpen == 1)
	{
		&loggIt("Closing Log File!\n$end_marker");
		close LOGFILE;
	}
}

#________________________________________________________________________________________________________________________
#This method will close the properties file 
#________________________________________________________________________________________________________________________
sub closePropertiesFile
{
	if($isPropFileOpen == 1)
	{
		&loggIt("Closing Properties File!");
		close PROPFILE;
	}
}

#________________________________________________________________________________________________________________________
#This method will return the value for the key passed to this method from the property cache
#________________________________________________________________________________________________________________________
sub getPropertyValue
{
	return $propertyHash{lc($_[0])};
}

#________________________________________________________________________________________________________________________
# This method opens up the properties file and loads it in a hashmap so that program has faster access to properties. Everything is stored in lower case in hashmap to
# remove any case sensitiveness
#________________________________________________________________________________________________________________________
sub loadProperties
{
	#first open the properties file.
	&openPropertiesFile;
	
	#initialize the property hash first
	%propertyHash = ();
	my @keyValuePair;
	my $currentValue;
	
	#until we reach the end of the property file
	while(<PROPFILE>)
	{	
		#put current value in a variable and then chomp it's new line characters so that we can see if it is a blank line or comment line in properties file.
		$currentValue = $_;
		chomp($currentValue);
		
		#if the current line is not starting with hash then load the current like as property and value pair in hashmap
		if($currentValue !~ /^#/ && length($currentValue) > 0)
		{	
			@keyValuePair = split('=', $currentValue);
			$propertyHash{ lc($keyValuePair[0]) } = lc($keyValuePair[1]);
			loggIt("Adding $keyValuePair[0] :: $keyValuePair[1] as key-value pair");
		}
	}
	
	#close the properties file.
	&closePropertiesFile;
}

#________________________________________________________________________________________________________________________
# This method either terminates program abnormally with respective error message or ends it gracefully with success code 0
#________________________________________________________________________________________________________________________
sub terminateProgram
{
	#check if there is  any error and thats the reason we are terminating or it's a normal termination of program. Since we should terminate program with correct message.
	if ($_[0] eq $cleanExit)
	{
		&closeLogFile;
		exit "WAR FILE ARCHIVED!!";
	}
	else
	{
		loggIt("$_[0]");
		&closePropertiesFile;
		&closeLogFile;
		exit $_[0];
	}
}

#________________________________________________________________________________________________________________________
# This method ensures that all the directories passed to it in a path exist before they are used in other methods.
#________________________________________________________________________________________________________________________
sub makeDirStruct
{
	my $countVal = 0;
	my $currentPathToCreate = "";
	$dirStructToCreate = $_[0];
	loggIt("Creating directory structure :: $dirStructToCreate");
	@allDirNames=  split(/\\/,$dirStructToCreate);
	foreach(@allDirNames)
	{
		# if it's the drive name in directory structure then just create the drive name
		if ($countVal == 0)
		{
			$currentPathToCreate = $allDirNames[$countVal];
		}
		else
		{
			$currentPathToCreate = $currentPathToCreate."\\".$allDirNames[$countVal];
		}
		
		&loggIt("Currently creating :: $currentPathToCreate");
		
		if(-d $currentPathToCreate)
		{
			#Do nothing as directory structure already exists.
		}
		else
		{
			mkdir($currentPathToCreate) or logIt("Could not create directory structure :: $currentPathToCreate");				
		}		
		#move to next level in directory structure; so increment the count.
		$countVal = $countVal + 1;
	}	
}

#________________________________________________________________________________________________________________________
# This is the method that ensures that war file is picked up from correct source and is copied to archiving Directory such that first folder is date of build and second
#folder is time of the build completion or copying
#________________________________________________________________________________________________________________________
sub archiveWar
{
	my @currDateAndTime;
	my $finalCopyPath;
	my $finalSourcePath;
	my $warFilePath;
	my $warFileName;
	#get the date and time so that version could be assigned to war file
	@currDateAndTime = split(":",&getTimeAndDate);
	
	#first create the directory structure
	$finalCopyPath = &getPropertyValue("archive.directory")."\\".$currDateAndTime[1]."\\".$currDateAndTime[0];
	&makeDirStruct($finalCopyPath);
	
	#now that directory structure is created then get the source path for the war file from properties file. 
	$finalSourcePath = &getPropertyValue("war.source.directory");
	
	&loggIt("checking if source path $finalSourcePath exists");
	#source path should also exist 
	if (-d dirname($finalSourcePath))
	{
		#now assigne the path with file name also.
		$finalSourcePath = &getPropertyValue("war.source.directory")."//".&getPropertyValue("war.name");
		#since path exist lets open file in readonly mode
		&loggIt("Opening war file $finalSourcePath");
		open (WARFILE, "<$finalSourcePath ") or terminateProgram("Couldn't open source war file $finalSourcePath");
		binmode(WARFILE);
		
		$finalCopyPath = $finalCopyPath."\\".&getPropertyValue("war.name");
		&loggIt("Opening destination copy of war file $finalCopyPath");
		open OUTFILE, ">$finalCopyPath" or terminateProgram("Could not open destination war file $finalCopyPath");
		binmode(OUTFILE);
		
		while(<WARFILE>)
		{
			print OUTFILE $_;			
		}
		
		close WARFILE;
		close OUTFILE;
		&loggIt("War file copied successfully; so closing both source and destination file");
	}
	else
	{
		terminateProgram("Source path doesn't exist:: $finalSourcePath");
	}
}

#________________________________________________________________________________________________________________________
#This method returns the date and time which is used in creating respective structure for archiving directories
#________________________________________________________________________________________________________________________
sub getTimeAndDate 
{	
	#this method returns the time and date in formate hh-mm:MM-dd-yyyy
   @_ = localtime(shift || time); 
   return (sprintf("%02d-%02d:%02d-%02d-%04d", @_[2,1], $_[4]+1, $_[3], $_[5]+1900)); 
} 

#________________________________________________________________________________________________________________________
# the calls to individual subroutines in pre-defined order.
#________________________________________________________________________________________________________________________
&openLogFile;
&loadProperties;
&archiveWar;
&terminateProgram($cleanExit);

#____________________________________   END OF BEAUTIFUL CODE ____________________________________________________
