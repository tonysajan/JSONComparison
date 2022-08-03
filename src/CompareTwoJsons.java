import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompareTwoJsons {




    public static void main(String[] args) throws IOException, JsonParseException {



            //variable to calculate runtime
            long start = System.currentTimeMillis();
            //Initialising JsonNode objects
            JsonNode fJSON = null, sJSON = null;

            //Initialising Deep compare and String Compare results
            boolean val1 = false, val2 = false;

            //Initialising ObjectMapper object
            ObjectMapper mapper = new ObjectMapper();

            //Initialising File A from File Directory
            File fileA = new File("src/Sample1.json");

            System.out.println("===============================================");
            //File Existence Check
            if (!fileA.exists()) {
                System.out.println("File A missing or incorrectly specified");
            }

            String fileName = fileA.toString();
            //Extracting extension
            String extension = Files.getFileExtension(fileName);
            System.out.println("Extension of File A: ."+extension);

            //Checking File Extension
            if((!extension.equals("json")) && (!extension.equals("geojson"))){
                System.out.println("File A: Not a valid Json or GeoJson file");
            }









            try{
                //Parsing File A as JsonNode
                fJSON = mapper.readTree(fileA);
                System.out.println("File A has a valid JSON syntax");
            } catch (JsonParseException e) {
                System.out.println("File A does not have a valid JSON syntax");
            }


            //Initialising File B from File Directory
            File fileB = new File("src/Sample2.json");

            //File B Existence Check
            if (!fileB.exists()) {
                System.out.println("File B missing or incorrectly specified");
            }

            fileName = fileB.toString();
            //Extracting extension of File B
            extension = Files.getFileExtension(fileName);
            System.out.println("Extension of File B: ."+extension);

            //Checking File B Extension
            if((!extension.equals("json")) && (!extension.equals("geojson"))){
                System.out.println("File B : Not a valid Json or GeoJson file");
            }



                try{
                    //Parsing File B as JsonNode
                    sJSON = mapper.readTree(fileB);
                    System.out.println("File B has a valid JSON syntax");
                } catch (JsonParseException e) {
                    System.out.println("File B does not have a valid JSON syntax");
                }
            


            //try {


                System.out.println("===============================================");

                //Storing deep comparison result in val1
                val1 = deepCompare(fJSON, sJSON);
                System.out.println("Deep Comparison Check Result: "+val1);

                //Storing String comparison result in val2
                val2 = fJSON.equals(sJSON);
                System.out.println("String Comparison Check Result: "+val2);

                //If both Deep and String comparison results returned true
                if (val1 && val2) {
                    System.out.println("Objects same and both in same order");
                }
                //If Deep comparison result is true and String Comparison result is false
                else if (val1 && !val2) {
                    System.out.println("Objects same but both not in same order");
                }
                //If both Deep and String Comparison results are false
                else if (!val1 && !val2) {
                    System.out.println("Objects not same");
                }

                System.out.println("===============================================");

                //Checking and displaying number of objects in File A and File B
                System.out.println("Number of objects in file A: "+fJSON.size());
                System.out.println("Number of objects in file B: "+sJSON.size());

                System.out.println("===============================================");

            //} catch (Exception e) {
            //    System.out.println("JSON files not valid for comparison");
           // }



        //Calculating runtime
        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        //Displaying runtime
        System.out.print("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");



    }

    public static boolean deepCompare(JsonNode firstJson, JsonNode secondJson) {

        //checking the equality of the root elements
        if(firstJson.getNodeType() != secondJson.getNodeType()) return false;

        //storing the node type of secondJson
        switch(secondJson.getNodeType()) {

            case BOOLEAN:
            case STRING:
                //The string value of the nodes compared with each other
                return firstJson.asText().equals(secondJson.asText());
            case NUMBER:
                //The double value of the nodes compared with each other
                return firstJson.asDouble() == secondJson.asDouble();
            case ARRAY:
                //First checked if both Array objects of same size
                if(firstJson.size() != secondJson.size()) return false;
                //Declaration and initialisation of Object Array
                List<JsonNode> objectArray = new ArrayList<>();
                //All nodes of secondJson array added to Object Array
                secondJson.forEach(objectArray::add);

                //Iterating through the Array elements of firstJson
                for(int i=0; i < firstJson.size(); ++i) {
                    //findEqual initialised as false
                    boolean findEqual = false;
                    //Each element of firstJson array checked against elements of Object Array
                    for(int j=0; j < objectArray.size(); ++j) {
                        //Checking for exact matches
                        if(deepCompare(firstJson.get(i), objectArray.get(j))) {
                            //If exact match found, findEqual flagged as true
                            findEqual = true;
                            //Matched element removed from Object Array
                            objectArray.remove(j);
                            break;
                        }
                    }
                    //If no matches found for any array element of fistJson, false returned
                    if(!findEqual) return false;
                }
                break;
            case OBJECT:
                //First checked if both objects of same size
                if(firstJson.size() != secondJson.size()) return false;

                //Iterator declared and initialised
                Iterator<String> objectIterator = firstJson.fieldNames();
                //Iterated until end of object elements
                while(objectIterator.hasNext()) {
                    //pointer declared to point to array objects
                    String nameField = objectIterator.next();
                    //if deepCompare() of objects with pointer nameField does not match,
                    //false returned
                    if(!deepCompare(firstJson.get(nameField), secondJson.get(nameField))) {
                        return false;
                    }
                }
                break;
            default:
                throw new IllegalStateException();
        }
        return true;
    }
}
