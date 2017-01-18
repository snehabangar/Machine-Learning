#################################################################################################################################################################
KMeans Algorithm for Tweets
#Author : Sneha Bangar
#################################################################################################################################################################

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
External Library 
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	
javax.json is used for parsing tweets from JSON file.
JAR details - javax.json-1.0.2.jar

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Class Details
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	
1) KMeans.Java
   This is the main class which calls different functions like kMeans , calculateJaccardDist etc

2) Cluster.Java
   This class implements the idea of Cluster and contains details about clusters like cluster id, cluster centroid etc..

3) Tweet.Java
   This class represents a Tweet in a dataset.This has attributes like tweet text,tweet Id,Id of cluster it belongs to.

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Data File
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	
The data folder contains the following files
1)data.json - sample set of tweets
2)seed.txt - initial set of seeds(tweet ids)

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Instructions to run the program Using Java source files and library jar files on UNIX/LINUX -	
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	

TO COMPILE ON UNIX/LINUX
1) Download the java files from src folder and copy them into a directory of your choice.
2) Copy javax.json-1.0.2.jar into same folder.
3) Navigate to the directory where all the files were copied.
4) TO compile the program use below command
	javac -cp javax.json-1.0.2.jar *.java
5) Run 
   We need to supply input to the program, and we give it as->java <No of clusters> <data file path> <initial seed file path> <output file path>

   Run using the command ->

   java -cp "javax.json-1.0.2.jar:." KMeans 10 "seed.txt" "data.json" "output.txt"

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Output
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	
Format 
<Cluster id> {List of tweets belonging to this cluster}
<SSE> <Value>

The results will be stored in the specified output file e.g. output.txt

