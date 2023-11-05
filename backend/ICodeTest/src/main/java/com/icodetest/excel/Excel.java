package com.icodetest.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.icodetest.database.Database;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class Excel {

	@Autowired
	Database database;

	public void generateExcelTemplate(String tableName, HttpServletResponse response) throws IOException {

		List<String> headers = database.getColumnNames(tableName, 1);

		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + tableName + "template" + ".xlsx";
		response.setHeader(headerKey, headerValue);
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			XSSFSheet sheet = workbook.createSheet("Template Sheet");
			Row headerRow = sheet.createRow(0);

			// Create header cells and set the header values
			for (int i = 0; i < headers.size(); i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers.get(i));
				// Auto-size columns for better visualization
				sheet.autoSizeColumn(i);
			}

			// Write the workbook to a file
			try (FileOutputStream fileOutputStream = new FileOutputStream(tableName)) {
				workbook.write(fileOutputStream);
			}
			System.out.println("Excel template generated successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ServletOutputStream stream = response.getOutputStream();
		workbook.write(stream);
		workbook.close();
		stream.close();
	}

	public ArrayList<ArrayList<String>> readExcelSheet(MultipartFile file) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		XSSFSheet sheet = workbook.getSheetAt(0);
		ArrayList<ArrayList<String>> list = new ArrayList<>();
		int length = 0;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (!String.valueOf(row.getCell(0)).trim().equals(null)) {
				length++;
			} else {
				break;
			}
		}
		System.out.println(length);
		for (int i = 0; i < length; i++) {
			Row row = sheet.getRow(i);
			ArrayList<String> arrayList = new ArrayList<>();
			for (Cell cell : row) {
				arrayList.add(cell.getStringCellValue());
			}
			list.add(arrayList);
		}
		workbook.close();
		return list;
	}

}
