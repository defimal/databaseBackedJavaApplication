package com.vgb;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import java.io.*;
import java.util.*;

public class DataConverter {
	
    /**
     * 
     * @author sbumhe2 
     * Main class used to call classes like serialize to xml and json
     * 
     */
    
    public static void main(String[] args) {
    	// Load Data
    	Map<UUID, Person> persons = CSVReaderUtil.readPersons("data/Persons.csv");
    	Map<UUID, Company> companies = CSVReaderUtil.readCompanies("data/Companies.csv", persons);
    	Map<UUID, Item> items = CSVReaderUtil.readItems("data/Items.csv", companies);

        // Change the maps to lists
        List<Person> list_person = new ArrayList<>(persons.values());
        List<Company> list_company = new ArrayList<>(companies.values());
        List<Item> list_item = new ArrayList<>(items.values());
        
        // Serialize to JSON
        serializeToJson(list_person, "data/Persons.json");
        serializeToJson(list_company, "data/Companies.json");
        serializeToJson(list_item, "data/Items.json");
        System.out.println(list_person);
        System.out.println(list_company);
        System.out.println(list_item);

        //Serialize to XML using XStream
        serializeToXml(persons, "data/Persons.xml");
        serializeToXml(companies, "data/Companies.xml");
        serializeToXml(items, "data/Items.xml");

        System.out.println("Serialization Complete!");
    }

    // Serialize to JSON using Jackson
    private static void serializeToJson(Object data, String filename) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Serialize to XML using XStream
    private static void serializeToXml(Object data, String filename) {
        try {
            XStream xstream = new XStream();
            xstream.alias("person", Person.class);
            xstream.alias("company", Company.class);
            xstream.alias("item", Item.class);
            
            String xml = xstream.toXML(data);
            try (FileWriter fileWriter = new FileWriter(filename)) {
                fileWriter.write(xml);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
