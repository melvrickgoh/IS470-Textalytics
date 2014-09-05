package textalytics.dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.postgresql.ssl.NonValidatingFactory;
import org.postgresql.ds.PGPoolingDataSource;

public class pgDAO {
	private String HOST = "ec2-23-21-170-57.compute-1.amazonaws.com";
	private String PORT = "5432";
	private String NAME = "d2t3trkm3dvedc";
	private String USERNAME = "lxkdnxglxsqpfd";
	private String PASSWORD = "i652h8UFdyfV-hRnADKlJDBWit";
	private String DBURL = "postgresql://lxkdnxglxsqpfd:i652h8UFdyfV-hRnADKlJDBWit@ec2-23-21-170-57.compute-1.amazonaws.com:5432/d2t3trkm3dvedc";
	private PGPoolingDataSource source;
	
	public pgDAO(){
		connectionPoolingSetup();
		bootstrap();
	}
	
	private void bootstrap(){
		testTableCreation();
	}
	
	private void testTableCreation(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("name", "melmel");
		map.put("pk","id");
		map.put("pk_generated", "true");
		map.put("attributes", "1");
		map.put("attribute_1_name","demo" );
		map.put("attribute_1_type","varchar(50)" );
		map.put("attribute_1_compulsory","true" );
		
		createTable(map);
	}
	
	private void connectionPoolingSetup(){
		source = new PGPoolingDataSource();
		source.setDataSourceName("Heroku Postgres");
		source.setServerName(HOST+":"+PORT);
		source.setDatabaseName(NAME);
		source.setUser(USERNAME);
		source.setPassword(PASSWORD);
		source.setMaxConnections(8);
		source.setSsl(true);
		source.setSslfactory("org.postgresql.ssl.NonValidatingFactory");
	}
	
	//DELEGATES THE CONNECTION CLOSURE TO METHODS CALLING IT
	//ONCE CLOSED, ITS NOT DESTROYED BUT RETURNED TO THE POOL
	public Connection getPooledConnection(){
		Connection conn = null;
		try{
		    conn = source.getConnection();
		    //System.out.println("Getting POOLED CONNECTION >> " + conn);
		}catch (SQLException e){
		    // log error
			e.printStackTrace();
		}finally{
		    //returning so do nothing
		}
		return conn;
	}
	
	public Connection getConnection() throws URISyntaxException, SQLException {

	    return DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
	}
	
	public void dropTable (String tablename){
		exec("DROP TABLE \""+tablename+"\";");
	}
	
	public void truncateTable(String tablename){
		exec("TRUNCATE TABLE \""+tablename+"\";");
	}
	
	public HashMap<String,Object> selectAll(String tablename){
		return execQ("select * from \""+tablename+"\";");
	}
	
	public void createTable (HashMap<String,String> tableDetails){
		exec(createTableSQL(tableDetails));
	}
	
	/*Details of Hashmap
	 * name : table name
	 *  pk : pk name
	 *  pk_generated : is it serial id?
	 *  pk_type : if not serial, whats the type of this id?
	 *  attributes : int of the number of other attributes
	 *  attribute_i_name : name of other attribute
	 *  attribute_i_type : data type of other attribute
	 *  attribute_i_compulsory : is this attribute compulsory
	*/
	private String createTableSQL(HashMap<String,String> map){
		String query = "CREATE TABLE IF NOT EXISTS \"";
		query += map.get("name") + "\" (";
		query += map.get("pk") + " ";
		
		if (map.get("pk_generated") != null && map.get("pk_generated").equalsIgnoreCase("true")){
			query += "SERIAL PRIMARY KEY,";
		}else{
			query += map.get("pk_type") + " PRIMARY KEY,";
		}
		
		int noAttributes = Integer.parseInt(map.get("attributes"));
		//iterate through attributes
		for (int i = 0; i<noAttributes; i++){
			String name = map.get("attribute_"+(i+1)+"_name");
			String type = map.get("attribute_"+(i+1)+"_type");
			String isCompulsory = map.get("attribute_"+(i+1)+"_compulsory");
			query += " " + name + " " + type;
			if (isCompulsory!=null && isCompulsory.equalsIgnoreCase("true")){
				if (i == (noAttributes-1)){
					query += " NOT NULL";
				}else{
					query += " NOT NULL,";
				}
			}else{
				//System.out.println(i + " vs. " + (noAttributes-1));
				if (i == (noAttributes-1)){
					//do nothing, gonna end query le
				}else{
					query += ",";
				}
			}
		}
		
		query += ");";
		//System.out.println(query);
		return query;
	}
	
	public HashMap<String,Object> select(HashMap<String,String> map){
		//System.out.println(createSelectSQL(map));
		HashMap<String,Object> mapRS = execQ(createSelectSQL(map));
		return mapRS;
	}
	
	/*
	 * name: table name
	 * distinct: select only distinct items
	 * attributes: the number of attributes selected
	 * conditions: number of conditions to handle
	 * attribute_i: attr to pull
	 * condition_i: conditions to fulfill in SQL
	 * order: sql order
	 * limit: number of results to limit
	 * nested: is this a nested sql query
	 */
	private String createSelectSQL(HashMap<String,String> map){
		String query = "SELECT ",
		isDistinct = map.get("distinct"),
		name = map.get("name"),
		attributes = map.get("attributes"),
		conditions = map.get("conditions"),
		nested = map.get("nested");
		
		//selectively unique?
		if (isDistinct != null && isDistinct.equalsIgnoreCase("true")){
			query += "DISTINCT ";
		}
				
		//generating form for attributes to select
		if (attributes==null){
			String tableName;
			if (nested!=null && nested.equalsIgnoreCase("true")){
				tableName = map.get("nested_name");
			}else{
				tableName = name;
			}
			query += "* FROM " + tableName + " ";
		}else{
			int attributesNo = Integer.parseInt(attributes);
			for (int i = 0; i<attributesNo; i++){
				String attr = map.get("attribute_"+(i+1));
				if (i == attributesNo-1){
					query += attr + " FROM " + name + " ";
				}else{
					query += attr + ", ";	
				}
			}
		}

		//generating matching conditions
		if (conditions!=null && Integer.parseInt(conditions)>0 ){
			int conditionsNo = Integer.parseInt(conditions);
			query += "WHERE ";
			for (int i=0; i<conditionsNo; i++){
				String condition = map.get("condition_"+(i+1));
				if (i == conditionsNo-1){
					query += ""+condition + " ";
				}else{
					query += ""+condition + " AND ";
				}
			}
		}

		//adding in order
		String order = map.get("order");
		if (order!=null){ // && details.order.length > 0){
			query += "ORDER BY " + order + " ";
		}

		//adding in limits
		String limit = map.get("limit");
		if (limit!=null){
			query += "LIMIT " + limit;
		}

		//nesting of select statement? used by other clauses like delete
		if (nested!=null && nested.equalsIgnoreCase("true")){
			return query;
		}
		return query += ";";
	}
	
	public HashMap<String,Object> update(HashMap<String,String> map){
		String sql = createUpdateSQL(map);
		//System.out.println(sql);
		return execQ(sql);
	}
	
	/*
	 * name: table name
	 * 
	 */
	private String createUpdateSQL(HashMap<String,String> map){
		String query = "UPDATE " + map.get("name") + " SET ";

		//setting up the values
		int valuesNo = Integer.parseInt(map.get("values"));
		for (int i = 0; i<valuesNo; i++){
			String attr = map.get("values_"+(i+1)+"_attr"),
			value = map.get("values_"+(i+1)),
			type = map.get("values_"+(i+1)+"_type");

			query += attr + " = " + generateSQLWrappers(value,type);

			if (i == valuesNo-1){
				query+=" ";
			}else{
				query+=", ";
			}
		}

		//conditions
		String conditions = map.get("conditions");
		if (conditions!=null && Integer.parseInt(conditions) > 0){
			int conditionsNo = Integer.parseInt(conditions);
			query += "WHERE ";
			for (int i = 0; i<conditionsNo; i++){
				String condition = map.get("condition_"+(i+1));
				if (i == conditionsNo-1){
					query += condition + " ";
				}else{
					query += condition + " AND ";
				}
			}
		}

		//returning
		String returning = map.get("returns");
		if (returning!=null && Integer.parseInt(returning) > 0){
			query += "RETURNING ";
			int returnNo = Integer.parseInt(returning);
			for (int i = 0; i<returnNo; i++){
				String returningAttr = map.get("return_"+(i+1));
				if (i == returnNo-1){
					query += returningAttr;
				}else{
					query += returningAttr + ", ";
				}
			}
		}

		return query += ";";
	}
	
	public void delete(HashMap<String,String> map){
		exec(createDeleteSQL(map));
	}
	
	/*
	 * name: table name
	 * other_table: boolean if another table needs to be referenced
	 * common_attribute: common attr to ref with nested select
	 * nested_name: name of nested table
	 * conditions: number of conditions
	 * condition_i: i-th condition to be chained
	 */
	private String createDeleteSQL(HashMap<String,String> map){
		String query = "DELETE FROM " + map.get("name") + " WHERE ";
		
		String otherTable = map.get("other_table"),
		commonAttribute = map.get("common_attribute");
		if (otherTable!=null && otherTable.equalsIgnoreCase("true")){
			query += commonAttribute + " IN (";

			query += createSelectSQL(map);

			query += ");";
		}else{
			int conditionsNo = Integer.parseInt(map.get("conditions"));
			
			for (int i = 0; i<conditionsNo; i++){
				String condition = map.get("condition_"+(i+1));
				if (i == conditionsNo-1){
					query += condition + ";";
				}else{
					query += condition + " AND ";
				}
			}
		}

		return query;
	}
	
	public void insert(HashMap<String,String> map){
		String sql = createInsertSQL(map);
		System.out.println(sql);
		exec(sql);
	}
	
	/*
	 * name: table name
	 * attributes: number of attributes in each valueset
	 * values: number of values to insert
	 * values_i_child_j_value: i-th valueset value object, j-th attribute's VALUE
	 * values_i_child_j_type: i-th valueset value object, j-th attribute's VALUE TYPE
	 */
	private String createInsertSQL(HashMap<String,String> map){
		String query = "INSERT INTO " + map.get("name") + " (";
		//pumping in the attributes to be inserted: ORDER is IMPORTANT
		int attributesNo = Integer.parseInt(map.get("attributes"));
		
		for (int i = 0; i<attributesNo; i++){
			String attributeName = map.get("attribute_"+(i+1)+"_name");
			if (i==attributesNo-1){
				query += attributeName + ") VALUES ";
			}else{
				query += attributeName + ", ";
			}
		}
		
		int valuesNo = Integer.parseInt(map.get("values"));
		//setting up the values
		for (int i = 0; i<valuesNo; i++){

			query+="(";
			
			for (int j = 0; j<attributesNo; j++){
				String attr = map.get("values_"+(i+1)+"_child_"+(j+1)+"_value");
				String type = map.get("values_"+(i+1)+"_child_"+(j+1)+"_type");
				
				if (j==attributesNo-1){
					query += generateSQLWrappers(attr,type);
				}else{
					query += generateSQLWrappers(attr,type) +", ";
				}
			}
			
			if (i==valuesNo-1){
				query+=");";
			}else{
				query+="), ";
			}
		}
		return query;
	}
	
	private String generateSQLWrappers(String attr, String type) {
		String convertedValue;
		switch(type.toLowerCase()){
			case "string":
				convertedValue="'"+attr+"'";
				break;
			default:
				convertedValue = attr;
		}
		return convertedValue;
	}

	private void exec(String statement){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getPooledConnection();
			ps = conn.prepareStatement(statement);

			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null){
				try {ps.close();} catch (SQLException e) {e.printStackTrace();}
			}
			
			if (conn != null){
				try {conn.close();} catch (SQLException e) {e.printStackTrace();}
			}
		}
	}
	
	private HashMap<String,Object> execQ (String statement){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getPooledConnection();
			ps = conn.prepareStatement(statement);
			
			rs = ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null){
				//try {ps.close();} catch (SQLException e) {e.printStackTrace();}
			}
		}
		
		HashMap<String,Object> results = new HashMap<String,Object>();
		results.put("ResultSet",rs);
		results.put("Connection", conn);
		return results;
	}
	
}
