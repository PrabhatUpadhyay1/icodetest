package com.icodetest.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.icodetest.database.Database;
import com.icodetest.excel.Excel;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class API {

	@Autowired
	Excel excel;

	@Autowired
	Database database;

	@GetMapping("/tempelate")
	public void generateExcel(HttpServletResponse response) throws IOException {
		excel.generateExcelTemplate("icodetest_mapping", response);
	}

	@PostMapping("/database/mapping")
	public ResponseEntity<Map<String, String>> generateTableAndMapping(@RequestBody HashMap<String, Object> mapping)
			throws IOException {
		try {
			List<Object> fields = mapping.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
			System.out.println(fields);
			database.createDynamicTable("icode_test", fields);
			database.createMapping("icode_test", mapping);

			// Return a JSON response
			Map<String, String> response = new HashMap<>();
			response.put("message", "Upload successful");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// Handle the error and return an error response
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Upload failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@PostMapping("/template/upload")
	public ResponseEntity<String> uploadData(@RequestParam MultipartFile file) throws IOException {
		try {
			ArrayList<ArrayList<String>> list = excel.readExcelSheet(file);
			List<String> columnName = database.getColumnNames("icodetest_mapping", 2);
			database.insertDataIntoTable("icodetest", list, columnName);

			return ResponseEntity.ok("Upload successful");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
		}
	}
}
