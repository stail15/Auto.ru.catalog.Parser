package jsoupParser.service;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StringUtil {

    /**
     * {@code Logger} object that logs the program execution process.
     */
    private static final Logger logger = Logger.getLogger(ResTransformer.class.getName());

    static Set<String> restyling;


    static {
        restyling = StringUtil.loadRestylingDescriptor();
    }


    private static Set<String> loadRestylingDescriptor(){
        File file = new File(StringUtil.class.getResource("/restyling_descriptor.properties").getFile());
        BufferedReader reader;
        Set<String> restyling = new TreeSet<>();

        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));

            while ((line=reader.readLine()) != null){

                restyling.add(line);

            }

            reader.close();
        }
        catch (IOException  ex){

        }

        return restyling;
    }


    public   static boolean compareCarGenerations(String name1,String name2){
        name1 = StringUtil.prepareString(name1);
        name2 = StringUtil.prepareString(name2);

        return name1.equals(name2);
    }

    private static String prepareString(String name){

        name=name.replace(" ", "");

        int idxOfOpenBrace = name.indexOf("(");
        int idxOfCloseBrace = name.indexOf(")");

        if(idxOfCloseBrace>idxOfOpenBrace){
            String toDelete = name.substring(idxOfOpenBrace, idxOfCloseBrace+1);
            name=name.replace(toDelete,"");

        }

        return name;
    }

    public static String clearFromRestyling(String carGeneration){

       // String rest = Arrays.asList(restyling).stream()
        String rest = restyling.stream()
                .filter(carGeneration::contains)
                                          .findFirst()
                .get();

        return carGeneration.replace(rest,"").trim();

    }

    public static boolean containsRestyling(String carGeneration){
        boolean  result = false;

        if(carGeneration!=null && carGeneration.length()>0){

       //     result = Arrays.asList(restyling).stream()
            result = restyling.stream()
                                         .anyMatch(carGeneration::contains);
        }

        return result;
    }

    public static String sortYears(String years){

        String[] yearsArray = years.split("/");

        Arrays.sort(yearsArray);

        StringBuilder stb = new StringBuilder();
        Arrays.asList(yearsArray).forEach(element -> stb.append(element).append("/"));
        stb.deleteCharAt(stb.lastIndexOf("/"));

        return stb.toString();
    }

}
