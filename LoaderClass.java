package Load;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LoaderClass {
	public static void main(String[] args) throws IOException {
		String excelFilePath = "C:\\Users\\jackg\\OneDrive\\Desktop\\Data_1999-10-23.xlsx";
		Workbook wb2007 = new XSSFWorkbook(excelFilePath);
		 
        Sheet firstSheet = wb2007.getSheetAt(0);
        Iterator<Row> rowIterator = firstSheet.iterator();
        
        String listAttribute = "";
        String listData = "";
        int count = 0;
        String primaryKey = "";
        String attribute="";
        while(rowIterator.hasNext()) {
        	Row fRow = rowIterator.next();
        	Iterator<Cell> cellIterator = fRow.iterator();
        	if(count==0) {
        		int i = 0;
        		while (cellIterator.hasNext()) {
        		Cell cell = cellIterator.next();
        		attribute += "`"+cell+"`"+","; 
        		if(i == 0) {
        			listAttribute += "`"+cell+"`"+" INT NOT NULL "+",";
        			primaryKey = cell.toString();
        			
        		}else {
        			listAttribute += "`"+cell+"`" +" VARCHAR(45) NULL "+",";
        		}
        		i++;
        		}
        	}else {
        		while(cellIterator.hasNext()) {
        			listData += "'"+ cellIterator.next()+"'"+","; 
        		}
        		listData+="\t";
        	}
        	count++;
        }
        String command = listAttribute.substring(0,listAttribute.length()-1);
        String attributeRight = attribute.substring(0, attribute.length()-1);
        String[] arrayString = listData.split("\t");
        String jdbcURL = "jdbc:mysql://localhost:3306/warehouse?useSSL=false&characterEncoding=utf8";
        String username = "root";
        String password = "1234";
        Connection connection = null;
        
        
        try {
        	connection = DriverManager.getConnection(jdbcURL, username, password);
			connection.setAutoCommit(false);
			String createDatabase = " CREATE TABLE `warehouse`.`data1` ("+command+");";
			String addPrimaryKey = "ALTER TABLE `warehouse`.`data1`ADD PRIMARY KEY (`"+primaryKey+"`);";
			PreparedStatement statement = connection.prepareStatement(createDatabase);
			statement.execute();
			PreparedStatement statement2 = connection.prepareStatement(addPrimaryKey);
			statement2.execute();
			int time = 0;
			while(time < arrayString.length-3) {
				String value = arrayString[time].substring(0,arrayString[time].length()-1);
				String insertDatabase = "INSERT INTO `warehouse`.`data1` ("+attributeRight+")"+"VALUES ("+value+")";
				System.out.println(insertDatabase);
				PreparedStatement state = connection.prepareStatement(insertDatabase);
				state.execute();
				time++;
			}
			  
			connection.commit();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
