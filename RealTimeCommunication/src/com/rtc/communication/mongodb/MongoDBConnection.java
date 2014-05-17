package com.rtc.communication.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/* 
 * Specific database connection layer 
 * 
 * It's uses singleton pattern, if instances is created, return instances, create it otherwise 
 */ 
public class MongoDBConnection { 
	private static MongoClient mongoClient; 
	private static DB db; 
	boolean auth; 
	
	public MongoDBConnection(String ip, int port, String dbName) throws UnknownHostException {
		if (mongoClient == null) {
			try {
				mongoClient = new MongoClient(ip, port); 
				db = mongoClient.getDB(dbName);
				// auth = db.authenticate(username, password); // TODO  
			} catch (UnknownHostException e) {
				throw e;  
			} 
		} 
	} 
	
	/* 
	 * Given a @collection and @id get single document from database 
	 */
	public DBObject getDocumentById(String collection, String id) throws Exception { 
		BasicDBObject bdb = new BasicDBObject("_id",new ObjectId(id));
		DBObject result = null;  
		if (db.collectionExists(collection)) {
			DBCollection coll = db.getCollection(collection); 
			result = coll.findOne(bdb);
		} else {
			throw new Exception("Collection doesn't exist"); 
		} 
		return result; 
	} 
	
	/* 
	 * Given a @collection get single document by @field and @value of that field (like sql query SELECT * FROM TABLE WHERE field=value) 
	 */
	public DBObject getDocumentByField(String collection, String field, String value) throws Exception {
		BasicDBObject bdb = new BasicDBObject(); 
		if (field.equals("_id")) {
			bdb.put(field, new ObjectId(value)); 
		} else {
			bdb.put(field, value); 
		} 
		DBObject result = null; 
		if (existsCollection(collection)) {
			DBCollection coll = db.getCollection(collection); 
			result = coll.findOne(bdb);
		} else {
			throw new Exception("Collection doesn't exist"); 
		}
		return result; 
	} 
	
	/* 
	 * Given a @collection get multiple documents by @field and a @value 
	 */
	public ArrayList<String> getDocumentsByField(String collection, String field, String value) throws Exception {
		BasicDBObject bdb = new BasicDBObject(); 
		if (field.equals("_id")) {
			bdb.put(field, new ObjectId(value)); 
		} else {
			bdb.put(field, value); 
		} 
		ArrayList<String> result = new ArrayList<String>(); 
		DBCursor dbc;  
		if (existsCollection(collection)) {
			DBCollection coll = db.getCollection(collection); 
			dbc = coll.find(bdb);
			while (dbc.hasNext()) {
				DBObject dbo = dbc.next();
				result.add(dbo.get("_id").toString()); 
			}
		} else {
			throw new Exception("Collection doesn't exist"); 
		}
		return result; 
	} 
	
	/* 
	 * Given a @collection and list of @fields and @values, get an array of documents   
	 */
	public ArrayList<String> getDocumentsByFields(String collection, Object[] fields, Object[] values) throws Exception {
		BasicDBObject bdb = new BasicDBObject(); 
		for (int i=0; i<fields.length; i++) {
			String field = (String)fields[i]; 
			String value = (String)values[i]; 
			if (field.equals("_id")){				// if filter field is ID, then convert it to "ObjectId" type
				bdb.put(field,new ObjectId(value));  	
			} else {								// if filter field is boolean type, convert the value to boolean
				if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
					bdb.put(field,Boolean.valueOf(value));   
			} else { 								// if filter field is string or number, simply add (for later revisions it will be need to add any type that database have)
					bdb.put(field,value);
				}
			}
		}  
		// Add all documents ID to array list 
		ArrayList<String> result = new ArrayList<String>(); 
		DBCursor dbc;  
		if (existsCollection(collection)) {
			DBCollection coll = db.getCollection(collection); 
			dbc = coll.find(bdb);
			while (dbc.hasNext()) {
				DBObject dbo = dbc.next();
				result.add(dbo.get("_id").toString()); 
			}
		} else {
			throw new Exception("Collection doesn't exist"); 
		}
		return result; 
	} 
	
	/* 
	 * Given a @collection and list of @fields and @values, get a single document  
	 */ 
	public DBObject getDocumentByFields(String collection, Object[] fields, Object[] values) throws Exception {
		BasicDBObject bdb = new BasicDBObject();
		for (int i=0; i<fields.length; i++) {
			String field = (String)fields[i]; 
			String value = (String)values[i];
			if (field.equals("_id")){
				bdb.put(field,new ObjectId(value));
			} else {
				bdb.put(field,value);
			}
		} 
		DBObject result = null; 
		if (existsCollection(collection)) {
			DBCollection coll = db.getCollection(collection); 
			result = coll.findOne(bdb); 
		} else {
			throw new Exception("Collection doesn't exist"); 
		}
		return result; 
	}
	
	/* 
	 * Given a @collection and a document of type DBObject, insert to database   
	 */
	public boolean insertDocument(String collection, BasicDBObject document) throws Exception {
		WriteResult result = null; 
		if (existsCollection(collection)) {
			DBCollection coll = db.getCollection(collection); 
			result = coll.insert(document); 
		} else {
			throw new Exception("Collection doesn't exist"); 
		}
		return result.getLastError().ok(); 
	} 
	
	
	/* 
	 * Given a @collection and a DB Object @document, update it 
	 * 
	 * @document has and _id field, that is used to perform query of update 
	 */
	public boolean updateDocumentById(String collection, BasicDBObject document) throws Exception {
		WriteResult result = null; 
		BasicDBObject query = new BasicDBObject("_id",document.get("_id")); 
		if (existsCollection(collection)){
			DBCollection coll = db.getCollection(collection); 
			result = coll.update(query, document); 
		}  else {
			throw new Exception("Collection doesn't exist"); 
		}
		return result.getLastError().ok(); 
	}
	
	/* 
	 * Given a @collection and a DB Object @document, delete it from database 
	 */
	public boolean deleteDocumentById(String collection, BasicDBObject document) throws Exception {
		WriteResult result = null; 
		if (existsCollection(collection)){
			DBCollection coll = db.getCollection(collection); 
			result = coll.remove(document); 
		}  else {
			throw new Exception("Collection doesn't exist"); 
		}
		return result.getLastError().ok();  
	} 
	
	/* 
	 * Given a @collection and a DB Object @document, delete it from database by @field and @value 
	 */
	public boolean deleteDocumentByField(String collection, String field, String value) throws Exception{
		BasicDBObject bdb = new BasicDBObject(); 
		if (field.equals("_id")) {
			bdb.put(field, new ObjectId(value)); 
		} else {
			bdb.put(field, value); 
		} 
		WriteResult result = null;  
		if (existsCollection(collection)) {
			DBCollection coll = db.getCollection(collection); 
			result = coll.remove(bdb);
		} else {
			throw new Exception("Collection doesn't exist"); 
		}
		return result.getLastError().ok();  
	} 
	
	/* 
	 * Given a @collection check if it exists  
	 */
	public boolean existsCollection(String collection) throws Exception {
		try {
		if (db.collectionExists(collection)) {
			return true; 
		}
		return false;
		} catch (Exception e) {
			throw e; 
		} 
	}  
	
	public boolean deleteAllDocumentsByCollection(String collection) throws Exception {
		try {
			if (db.collectionExists(collection)){ 
				DBCollection coll = db.getCollection(collection);
				coll.remove(new BasicDBObject()); 
				return true; 
			}
		} catch (Exception e) {
			throw e; 
		}
		return false;  
	}
} 
