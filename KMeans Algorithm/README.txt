#################################################################################################################################################################
KMeans Algorithm for Points
Author : Sneha Bangar
#################################################################################################################################################################

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
External Library 
-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Open CSV is used for reading the data from CSV.
JAR details - opencsv-2.4.jar

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Class Details
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	

1. KMMain.Java
   This is the main class which calls different functions like kMeans,computeSSE

2. Cluster.Java
   This class implements the idea of Cluster and contains details about clusters like cluster id, cluster centroid etc.

3. Point.Java
   This class represents a Point in a dataset.This has attributes like x value,y value , Id of cluster it belongs to etc.

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Data File
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	
The data folder contains the following files
1)data.csv - sample set of points (x,y)

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Instructions to run the program Using Java source files and library jar files on UNIX/LINUX -	
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	
TO COMPILE ON UNIX/LINUX

1) Download the java files from src folder and copy them into a directory of your choice.
2) Copy opencsv-2.4.jar into same folder.
3) Navigate to the directory where all the files were copied.
4) TO compile the program use below command
	javac -cp opencsv-2.4.jar *.java
5) Run 
   We need to supply input to the program, and we give it as-> java <No of clusters> <data file path> <output file path>

   Run using the command ->

   java -cp "javax.json-1.0.2.jar:." KMMain 4 "data.csv" "output.txt"

-----------------------------------------------------------------------------------------------------------------------------------------------------------------
Output
-----------------------------------------------------------------------------------------------------------------------------------------------------------------	
Format 
<Cluster id> {List of points belonging to this cluster}
<SSE> <Value>

The results will be stored in the specified output file e.g. output.txt