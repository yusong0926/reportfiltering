package main.java;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by syu on 11/28/16.
 */
public class ReportUtils {
    private List<String> headers = new ArrayList<>();  /* report header */

    /**
     * Construtor
     * hardcode the default headers in construtor, the content and order of headers will be changed based on csv file
     */
    public ReportUtils() {
        headers.add("client-address");
        headers.add("client-guid");
        headers.add("request-time");
        headers.add("service-guid");
        headers.add("retries-request");
        headers.add("packets-requested");
        headers.add("packets-serviced");
        headers.add("max-hole-size");
    }

    /**
     * general function to convert report file to list
     * @param fn file path
     * @return list of records
     */
    public List<List<String>> convertToList (String fn) {

        List<List<String>> list = new ArrayList<>();
        try {
            String extension = getFileExtension(fn);
            if (extension.equals("csv")) {
                list = csvToList(fn);
            } else if (extension.equals("json")) {
                list = jsonToList(fn);
            } else if (extension.equals("xml")) {
                list = xmlToList(fn);
            } else {
                System.err.println("Invalid file extension");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Invalid file extension: " + e.getMessage());
            System.exit(1);
        }
        return list;
    }

    /**
     * read csv file to list
     * @param fn file path
     * @return list of records
     */
    private List<List<String>> csvToList(String fn) {

        List<List<String>> csvList = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(fn) , ',');
            String [] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                if (nextLine != null) {
                    csvList.add(new ArrayList<>(Arrays.asList(nextLine)));
                }
            }
            this.headers = csvList.get(0); /* change headers based on the csv file */
            csvList.remove(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        }
        return csvList;
    }

    /**
     * read json file to list
     * @param fn file path
     * @return list of records
     */
    private List<List<String>> jsonToList(String fn) {

        List<List<String>> jsonList = new ArrayList<>();

        JSONParser parser = new JSONParser();
        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader(fn));

            for (Object o : a) {
                JSONObject record = (JSONObject) o;
                List<String> list = new ArrayList<>();
                for (String label : headers) {
                    if (label.equals("request-time")) {
                        list.add(epochToDate((long)record.get(label)));
                    } else {
                        list.add(record.get(label).toString());
                    }
                }
                jsonList.add(list);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return jsonList;
    }

    /**
     * read xml file to list
     * @param fn file path
     * @return list of records
     */
    private List<List<String>> xmlToList(String fn) {

        List<List<String>> xmlList = new ArrayList<>();
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(fn);

        try {
            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            List list_xml = rootNode.getChildren("report");

            for (int i = 0; i < list_xml.size(); i++) {
                Element node = (Element) list_xml.get(i);
                ArrayList list = new ArrayList<>();
                for (String label : headers) {
                    list.add(node.getChildText(label).toString());
                }
                xmlList.add(list);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        } catch (JDOMException jdomex) {
            System.out.println(jdomex.getMessage());
            System.exit(1);
        }
        return xmlList;
    }

    /**
     * get file extension
     * @param fn file path
     * @return file extension string
     */
    private String getFileExtension(String fn) throws Exception {

        String extension = "";
        int i = fn.lastIndexOf('.');
        if (i == -1) {
            throw new Exception("Invalid File Extension");
        }
        int p = Math.max(fn.lastIndexOf('/'), fn.lastIndexOf('\\'));

        if (i > p) {
            extension = fn.substring(i+1);
        }
        return extension;
    }

    /**
     * covert epochtime to formated datetime
     * @param epochTime epochtime
     * @return formated datetime with timezone
     */
    private String epochToDate(Long epochTime){

        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
        format.setTimeZone(TimeZone.getTimeZone("Canada/Atlantic"));
        String dateTime = format.format(date);
        return dateTime;
    }

    /**
     *
     * @param list records
     * @param header report header need to be filtered
     * @param value  value of the header need to filtered
     * @return new report list after filter
     */
    public List<List<String>> filter(List<List<String>> list, String header, String value) {

        //find the index of the header
        int index = -1;
        for (int i = 0; i < headers.size(); i++) {
            if(headers.get(i).equals(header)) {
                index = i;
            }
        }
        if (index == -1) {
            System.err.println("Filter header is not found!");
            System.exit(1);
        }
        List<List<String>> newList = new ArrayList<>();
        for (List<String> record : list) {
            if (!record.get(index).equals(value)) {
                newList.add(record);
            }
        }
        return newList;
    }

    /**
     *
     * @param list report list
     * @param fn csv file path where the report list is going to be output
     */
    public void listToCsv(List<List<String>> list, String fn) {

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(fn), ',' , CSVWriter.NO_QUOTE_CHARACTER);
            // feed in your array (or convert your data to an array)
            writer.writeNext(headers.toArray(new String[headers.size()]));
            for (List<String> record : list) {
                String[] strarray = record.toArray(new String[record.size()]);
                writer.writeNext(strarray);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * print a summary showing the number of records in the output file associated with each service-guid
     * @param list report list
     */
    public void printSummary(List<List<String>> list) {

        HashMap<String, Integer> map = new HashMap<>();
        for (List<String> record : list) {
            if (map.containsKey(record.get(1))) {
                map.put(record.get(1), map.get(record.get(1)) + 1);
            } else {
                map.put(record.get(1), 1);
            }
        }
        System.out.println("Report Summary");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("service-guid: " + entry.getKey() + " number of records: " + entry.getValue());
        }
    }
}
