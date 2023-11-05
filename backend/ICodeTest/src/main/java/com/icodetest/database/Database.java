package com.icodetest.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class Database {
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public void createDynamicTable(String tableName, List<Object> fieldNames) {
		String createTableSQL = "CREATE TABLE " + tableName + " (id BIGINT PRIMARY KEY AUTO_INCREMENT, ";

		for (Object fieldName : fieldNames) {
			createTableSQL += fieldName + " VARCHAR(255), ";
		}

		createTableSQL = createTableSQL.substring(0, createTableSQL.length() - 2); // Remove the trailing comma and
																					// space
		createTableSQL += ");";

		entityManager.createNativeQuery(createTableSQL).executeUpdate();

	}

	@Transactional
	public void createMapping(String tableName, Map<String, Object> mapping) {
		String createTableSQL = "CREATE TABLE " + tableName + "_mapping " + " (id BIGINT PRIMARY KEY AUTO_INCREMENT, ";

		createTableSQL += "template_field" + " VARCHAR(255), ";
		createTableSQL += "database_field" + " VARCHAR(255), ";

		createTableSQL = createTableSQL.substring(0, createTableSQL.length() - 2); // Remove the trailing comma and
																					// space
		createTableSQL += ");";

		entityManager.createNativeQuery(createTableSQL).executeUpdate();
		insertDataIntoMappingTable(tableName, mapping);
	}

	@Transactional
	public void insertDataIntoMappingTable(String tableName, Map<String, Object> data) {
		StringBuilder insertDataSQL = new StringBuilder(
				"INSERT INTO " + tableName + "_mapping (template_field, database_field) VALUES ");

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			insertDataSQL.append("(" + "'" + entry.getKey() + "'" + "," + "'" + entry.getValue() + "'" + "),");
		}

		insertDataSQL.deleteCharAt(insertDataSQL.length() - 1);
		insertDataSQL.append(";");
		Query query = entityManager.createNativeQuery(insertDataSQL.toString());
		query.executeUpdate();
	}

	public List<String> getColumnNames(String tableName, int index) {
		String query = "SELECT * FROM " + tableName;
		List<Object[]> rows = entityManager.createNativeQuery(query).getResultList();

		List<String> columnNames = new ArrayList<>();

		for (Object[] value : rows) {
			columnNames.add((String) value[index]);
		}

		System.out.println(columnNames);
		return columnNames;
	}

	@Transactional
	public void insertDataIntoTable(String tableName, ArrayList<ArrayList<String>> list, List<String> coloumnName) {

		String sqlKeys = coloumnName.stream().collect(Collectors.joining(","));

		StringBuilder insertDataSQL = new StringBuilder("INSERT INTO " + tableName + "(" + sqlKeys + ")" + " VALUES ");
		list.remove(0);
		for (ArrayList<String> arrayList : list) {
			StringBuilder sb = new StringBuilder("(");
			for (String string : arrayList) {
				sb.append("'" + string + "'" + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("), ");
			insertDataSQL.append(sb.toString());
		}

		insertDataSQL.deleteCharAt(insertDataSQL.length() - 2);
		insertDataSQL.append(";");
		Query query = entityManager.createNativeQuery(insertDataSQL.toString());
		query.executeUpdate();
	}
}
