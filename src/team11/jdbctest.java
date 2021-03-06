package team11;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

import com.csvreader.CsvReader;
 
/**
 * Functional file to test DB connection.
 * Practical functions for operating the database are also included in this Class.
 * 
 * @author Shijian(Tim) Xu
 * @version 1.0
 */

public class jdbctest {
 
    public static void main(String[] args) throws Exception {
    	Connection conn = null;
        try {
        	conn = DBUtil.getConnection();
//        	jdbctest.migrateDataFromCSVtoDB(conn);
//        	jdbctest.getSpecificStudentsQuery(conn);

        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	DBUtil.closeConnection(conn);
        }
    }
    
    
/**
 * Loading data from CSV and save them to Database.
 * 
 * @param conn
 *            Connection object of current connection to database.
 *            
 * @throws Exception
 * 			  Error of connecting database.
 * 
 * @see CsvReader
 * @see ArrayList
 */
    public static void migrateDataFromCSVtoDB(Connection conn) throws Exception {
		String[] header = {};
		CsvReader reader = null;
		try {
			ArrayList<String> uid = new ArrayList<String>();
			ArrayList<String> data = new ArrayList<String>();
			ArrayList<String> domain = new ArrayList<String>();
			ArrayList<String> timestamp = new ArrayList<String>();
	    	reader = new CsvReader(
					"/Users/Tim/Desktop/research paper/CapstoneCourseSpring2017/data/finaldatasetpart2(Tim).csv");
	    	while (reader.readRecord()) {
				// save header of cvs
				if (reader.getCurrentRecord() == 0) {
					System.out.println("Its 0 row");
					header = reader.getValues();
				} else {
					uid.add(reader.getValues()[0]);
					data.add(reader.getValues()[1]);
					domain.add(reader.getValues()[2]);
					timestamp.add(reader.getValues()[3]);
//					count.add(reader.getValues()[4]);
					
					DBUtil.post(conn, "INSERT INTO `cs_project_swqc`.`dataset_new` (`student_id`, `search_query`, `domain`, `time`) VALUES (?, ?, ?, ?);", reader.getValues());
				}
			}
		} catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	DBUtil.closeConnection(conn);
			reader.close();
        }
    	
    }

//    public static void randomPickStudents() {
//		Object[] values = new Object[95];
//        Random random = new Random();
//        ArrayList<Integer> list = new ArrayList<Integer>();
//
//        for(int i = 0; i < values.length;i++){
//            int number = random.nextInt(917) + 1;
//            
//            if(!list.contains(number)){
//                list.add(number);
//            } else {
//            	i--;
//            }
//        }
//        
//        values = list.toArray();
//        PrintStream outRandomSid = null;
//        try {
//        	outRandomSid = new PrintStream(new File("randomSid.txt"));
//        	for(int i = 0; i < values.length;i++){
//        		outRandomSid.println(values[i]);
//        	}
//        } catch (FileNotFoundException e) {
//			e.printStackTrace();
//        } finally {
//        	outRandomSid.close();
//        }
//    }
    
/**
 * Loading some picked student id from randomSid.txt, and search their dataset from database.
 * 
 * @param conn
 *            Connection object of current connection to database.
 *            
 * @return ArrayList
 * 
 * @throws Exception
 * 			  Error of connecting database.
 * 
 * @see CsvReader
 * @see ArrayList
 */
    public static ArrayList<String[]> getSpecificStudentsQuery(Connection conn) throws Exception {
    	Scanner inRandomSid = null;
    	ArrayList<String[]> out = null;
    	try {
    		inRandomSid = new Scanner(new File("randomSid.txt"));
    		StringBuffer sqlQuery = new StringBuffer("SELECT * FROM cs_project_swqc.dataset WHERE student_id in (");
    		while(inRandomSid.hasNext()) {
    			String s = inRandomSid.nextLine();
    			switch (s.length()){
    			case 1:
    				s = "00"+s;
    				break;
    			case 2:
    				s = "0"+s;
    				break;
    			default:
    				break;
    			}
    			sqlQuery.append("'s"+s+"',");
    		}
    		sqlQuery.deleteCharAt(sqlQuery.length()-1);
    		sqlQuery.append(");");
    		String[] column = {"student_id","search_query"};
    		String[] subquery = {};
    		out = DBUtil.get(conn, sqlQuery.toString(), column, subquery );
    		System.out.println(out.get(0)[0]);
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		inRandomSid.close();
    	}
		return out;
    }
}