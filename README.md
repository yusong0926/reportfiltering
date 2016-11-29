## Overview

Read the 3 input files reports.json, reports.csv, reports.xml and output a combined CSV file with the following characteristics:

The same column order and formatting as reports.csv
All report records with packets-serviced equal to zero should be excluded
records should be sorted by request-time in ascending order
Additionally, the application should print a summary showing the number of records in the output file associated with each service-guid.


## Descriptions

1. Read csv, json, xml files to three List of List<String>, and combine them to one list. 
2. Filtering.
3. Sorting.
4. Output the result to a CSV file.
5. Traverse the result, use hashmap to aggregate and print the summary.

Time Complexity O(nlogn))
Space Complexity O(n)

## How to Run

Copy the input files to data folder. In ReportFilter folder, Run java -jar target/report-filter-1.0-SNAPSHOT.jar  


## Dependencies

Java SDK 1.8

Opencsv
A very simple csv parser library for Java. Easy to use to read and write a csv file.

json-simple
A simple Java library for JSON processing, read and write JSON data and full compliance with JSON specification.

Jdom
Java-based solution for accessing, manipulating, and outputting XML data from Java code. 


