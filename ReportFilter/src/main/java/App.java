package main.java;
import java.util.*;
import java.util.List;

/**
 * Programming Test
 * @author Song Yu
 * @version 1.0
 */
public class App {
    public static void main( String[] args )  {

        ReportUtils reportIn = new ReportUtils();
        //read csv, json and xml files to lists
        List<List<String>> csvList = reportIn.convertToList("data/reports.csv");
        List<List<String>> jsonList = reportIn.convertToList("data/reports.json");
        List<List<String>> xmlList = reportIn.convertToList("data/reports.xml");

        //combine the three lists into one list
        List<List<String>> reportList = new ArrayList<>();
        reportList.addAll(csvList);
        reportList.addAll(jsonList);
        reportList.addAll(xmlList);

        //filter out records with packets-serviced equal to 0
        List<List<String>> output = reportIn.filter(reportList, "packets-serviced", "0");

        // sort the result by request-time in ascending order
        Collections.sort(output, new Comparator<List<String>>() {
            public int compare(List<String> l1, List<String> l2) {
                return l1.get(2).compareTo(l2.get(2));
            }
        });

        //output the result to a combined CSV file
        reportIn.listToCsv(output, "data/output.csv");

        // print a summary showing the number of records in the output file associated with each service-guid
        reportIn.printSummary(output);
    }
}
