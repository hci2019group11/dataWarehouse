package Load;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class LoadToDatabase {
		public static void main(String[] args) throws EncryptedDocumentException, IOException {
			LoadToDatabase ltd = new LoadToDatabase();
			ltd.appPart2();
		}
		public static void appPart2() throws EncryptedDocumentException, NumberFormatException, IOException {
			String list = getListFileLoad();
			String[] file = list.split("\n");
			for (int i = 0; i < file.length; i++) {
				loadToLocal(file[i]);
			}
		}
		public static String getListFileLoad() {
			String list ="";
			String jdbcURLRoot = "jdbc:mysql://localhost:3306/warehouse?useSSL=false&characterEncoding=utf8";
	        String usernameRoot = "root";
	        String passwordRoot = "1234";
	        int id =0;
	        String sourceConfig = "";
	        String delimiter ="";
	        String prefix_source ="";
			String desConfig = "";
			String user = "";
			String password_r = "";
			String tableName = "";
	        try {
				Connection connection_user = DriverManager.getConnection(jdbcURLRoot, usernameRoot, passwordRoot);
				connection_user.setAutoCommit(false);
				PreparedStatement stat = connection_user.prepareStatement("select * from configtab left join logtab on logtab.idconfig = configtab.idconfig where logtab.isReadyForWarehouse = 'false';");
				  stat.execute();
				ResultSet rs =  stat.getResultSet();
				while(rs.next()) {
				id = rs.getInt(1);
				sourceConfig = rs.getString(2);
				delimiter = rs.getString(3);
				prefix_source = rs.getString(4);
				desConfig = rs.getString(5);
				user = rs.getString(6);
				password_r = rs.getString(7);
				tableName = rs.getString(8);
				list = id+"\t"+sourceConfig+"\t"+delimiter+"\t"+prefix_source+"\t"+desConfig+"\t"+user+"\t"+password_r+"\t"+tableName+"\n";
				}
				connection_user.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return list;
		}
		public static void loadToLocal(String config) throws EncryptedDocumentException, NumberFormatException, IOException {
			String[] fields = config.split("\t");
			String log = getLogOf(1);
			String[] f_log = log.split(",");
			if(f_log[5].equals("true") && f_log[6].equals("false")) {
			switch (fields[3]) {
			case ".csv": {	
				loadFileCsv(Integer.parseInt(fields[0]),Integer.parseInt(f_log[0]),fields[1],fields[2], fields[4],fields[5], fields[6], fields[7]);
			}
			case ".xlsx": {
				loadFileExcel(Integer.parseInt(fields[0]),Integer.parseInt(f_log[0]),fields[1],fields[2], fields[4],fields[5], fields[6], fields[7]);
			}
			case ".txt": {
				loadFileTxt(Integer.parseInt(fields[0]),Integer.parseInt(f_log[0]),fields[1],fields[2], fields[4],fields[5], fields[6], fields[7]);
			}
				}
			}
		}
		
		public static void setReadyforWarehouse(int id,String value) {
			 try {
					Connection connection_user = DriverManager.getConnection("jdbc:mysql://localhost:3306/warehouse?useSSL=false&characterEncoding=utf8", "root", "1234");
					connection_user.setAutoCommit(false);
					PreparedStatement stat = connection_user.prepareStatement("UPDATE `warehouse`.`logtab` SET `isReadyForWarehouse` = 'true' WHERE (`id_logTab` = '"+id+"');");
					stat.execute();
					connection_user.commit();
					connection_user.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
		public static String getLogOf(int idConfig) {
			String log =null;
			String jdbcURLRoot = "jdbc:mysql://localhost:3306/warehouse?useSSL=false&characterEncoding=utf8";
	        String usernameRoot = "root";
	        String passwordRoot = "1234";
	        int id_logtab =0;
	        int idconfig = 0;
	        String sourcefile ="";
	        String filetype ="";
	        String table_name="";
	        String isReadyLoadLocal ="";
	        String isReadyForWarehouse ="";
	        String isSuccess ="";
	        String isError ="";
	        String isZip="";
	        try {
				Connection connection_user = DriverManager.getConnection(jdbcURLRoot, usernameRoot, passwordRoot);
				connection_user.setAutoCommit(false);
				PreparedStatement stat = connection_user.prepareStatement("select * from logtab left join configtab on logtab.idconfig = configtab.idconfig where logtab.idconfig="+idConfig+";");
				  stat.execute();
				ResultSet rs =  stat.getResultSet();
				while(rs.next()) {
					id_logtab =rs.getInt(1);
					idconfig = rs.getInt(2);
			        sourcefile =rs.getString(3);
			        filetype =rs.getString(4);
			        table_name=rs.getString(5);
			        isReadyLoadLocal =rs.getString(6);
			        isReadyForWarehouse =rs.getString(7);
			        isSuccess =rs.getString(8);
			        isError =rs.getString(9);
			        isZip=rs.getString(10);
			        log = id_logtab+","+idconfig+","+sourcefile+","+filetype+","+table_name+","+isReadyLoadLocal+","+isReadyForWarehouse+","+isSuccess+","+isError+","+isZip;
				}
				connection_user.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return log;
		}
		public static String convertToCsv(String path) throws EncryptedDocumentException, IOException {
			File file = new File(path);
			InputStream is = new FileInputStream(file);
			Workbook wb = WorkbookFactory.create(is);

	        Sheet sheet = wb.getSheetAt(0);
	        Iterator<Row> rowIterator = sheet.iterator();
	        String str = "";
	        while(rowIterator.hasNext()) {
	        	Row fRow = rowIterator.next();
	        	Iterator<Cell> cellIterator = fRow.iterator();
	        	while(cellIterator.hasNext()) {
	        		Cell cell = cellIterator.next();
	        		str += cell+",";
	        	}
	        	str += "\n";
	        }
	        File fileout = new File(file.getParent()+file.separator+file.getName().substring(0,file.getName().length()-5)+".csv");
	        FileOutputStream fos = new FileOutputStream(fileout);
	        fos.write(str.getBytes(StandardCharsets.UTF_8));
			return fileout.getAbsolutePath();
	        
		}
		public static void loadFileCsv(int idconfig,int id_log,String sourceConfig,String delimiter,String desConfig,String user,String password,String tableName) {
	        try {
	        	if(delimiter.equals("comma")) {
					delimiter =",";
				}
				Connection connection_user = DriverManager.getConnection(desConfig, user, password);
				connection_user.setAutoCommit(false);
				PreparedStatement stat = connection_user.prepareStatement("load data infile "+"'"+sourceConfig+"'"+" into table " +tableName+" CHARACTER SET latin1 FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\\n' IGNORE 1 LINES (STT,MSSV,HoLot,Ten,Ngaysinh,Malop,lop,sodienthoai,email,Quequan,ghichu);");
				stat.execute();
				setReadyforWarehouse(id_log, "true");
				connection_user.commit();
				connection_user.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	        
		}
		public static void loadFileTxt(int idconfig,int id_log,String sourceConfig,String delimiter,String desConfig,String user,String password,String tableName) {
			try {
				Connection connection_user = DriverManager.getConnection(desConfig, user, password);
				connection_user.setAutoCommit(false);
				if(delimiter.equals("comma")) {
					delimiter =",";
				}
				PreparedStatement stat = connection_user.prepareStatement("load data infile "+"'"+sourceConfig+"'"+" into table " +tableName+" CHARACTER SET latin1 FIELDS TERMINATED BY '"+delimiter+"' ENCLOSED BY '\"' LINES TERMINATED BY '\\r\\n' IGNORE 1 LINES (STT,MSSV,HoLot,Ten,Ngaysinh,Malop,lop,sodienthoai,email,Quequan,ghichu);");
				stat.execute();
				connection_user.commit();
				setReadyforWarehouse(id_log, "true");
				connection_user.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		public static void loadFileExcel(int idconfig,int id_log,String sourceConfig,String delimiter,String desConfig,String user,String password,String tableName) throws EncryptedDocumentException, IOException {
			try {
				if(delimiter.equals("comma")) {
					delimiter =",";
				}
				String afterConvert = convertToCsv(sourceConfig);
				File file = new File(afterConvert);
				String source = file.getParent()+"\\\\"+file.getName();
				Connection connection_user = DriverManager.getConnection(desConfig, user, password);
				connection_user.setAutoCommit(false);
				PreparedStatement stat = connection_user.prepareStatement("load data infile "+"'"+source+"'"+" into table " +tableName+" CHARACTER SET latin1 FIELDS TERMINATED BY '"+delimiter+"' ENCLOSED BY '\"' LINES TERMINATED BY '\\n' IGNORE 1 LINES (STT,MSSV,HoLot,Ten,Ngaysinh,Malop,lop,sodienthoai,email,Quequan,ghichu);");
				stat.execute();
				connection_user.commit();
				setReadyforWarehouse(id_log, "true");
				connection_user.close();
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
}
