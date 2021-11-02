package com.satsang.attendance.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.satsang.attendance.entity.attendanceEntity;
import com.satsang.attendance.repository.attendanceDataRepository;

@Service
public class attendanceDataService {

	@Autowired
	attendanceDataRepository adr;

	public List<attendanceEntity> getAllData(){
		return adr.findAll();
	}

	public List<attendanceEntity> getDataById(String uid){
		List<attendanceEntity> attendanceEntityList = new ArrayList<>();
		attendanceEntityList = adr.findById(uid);
		int sno=1;
		for(attendanceEntity ae: attendanceEntityList) {
			ae.setSno((long)sno++);
		}
		return attendanceEntityList;
	}

	public List<attendanceEntity> getDataByDate(String date){
		List<attendanceEntity> attendanceEntityList2 = new ArrayList<>();
		List<attendanceEntity> attendanceEntityList3 = new ArrayList<>();
		attendanceEntityList2 = adr.findAll();
		int sno=1;
		/*
		 * if(date.charAt(0)=='0') { date=date.substring(1); }
		 */
		for(attendanceEntity ae: attendanceEntityList2) {


			if(ae.getTime().contains(date)) {
				ae.setSno((long)sno++);
				attendanceEntityList3.add(ae);
			}
		}
		return attendanceEntityList3;
	}

	public String addData() throws GeneralSecurityException, IOException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		final String spreadsheetId = "1cYpKFrqsU58oUEMXP4EAmiPkbKrIUm80jqEv6-yyRTo";
		final String range = "Form Responses 1!A2:B";
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
			return "No data found";
		} else {
			System.out.println("Time, Barcode");
			for (List row : values) {
				try{
					attendanceEntity ae=new attendanceEntity();
					// Print columns A and E, which correspond to indices 0 and 4.
					if(!row.isEmpty()) {
						String barf= (String) row.get(1);
						System.out.printf( barf);
						JsonObject jsonObject = new JsonParser().parse(barf).getAsJsonObject();
						String id= jsonObject.get("id").getAsString();
						System.out.println(id);
						ae.setDoi(jsonObject.get("DOI").getAsString());
						ae.setId(jsonObject.get("id").getAsString());
						ae.setName(jsonObject.get("Name").getAsString());

						Date date = new Date();
						SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"); 
						Date date1=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(row.get(0).toString()); //
						String strDate= formatter.format(date1);

						
						ae.setTime(strDate);
						adr.save(ae);}
				}
				catch(Exception e) {
					return "Some Error Occured While Adding to Db";
				}
			}
			try {
				deleteDataFromgSheet();
			}
			catch (Exception e) {
				return "Some Error Occured While Deletion From Gsheet";
			}
			return "Data Saved";
		}
	}

	private void deleteDataFromgSheet() throws IOException, GeneralSecurityException {
		// The ID of the spreadsheet to update.
		String spreadsheetId = "1cYpKFrqsU58oUEMXP4EAmiPkbKrIUm80jqEv6-yyRTo"; // TODO: Update placeholder value.
		// The A1 notation of the values to clear.
		String range = "Form Responses 1!A2:B"; // TODO: Update placeholder value.
		// TODO: Assign values to desired fields of `requestBody`:
		DeleteDimensionRequest rb = new DeleteDimensionRequest();
		ClearValuesRequest requestBody = new ClearValuesRequest();
		Sheets sheetsService = createSheetsService();
		Sheets.Spreadsheets.Values.Clear request =sheetsService.spreadsheets().values().clear(spreadsheetId, range, requestBody);
		//Sheets.Spreadsheets.Create req = sheetsService.spreadsheets().values().;
		ClearValuesResponse response = request.execute();
		// TODO: Change code below to process the `response` object:
		System.out.println(response);
	}

	public static Sheets createSheetsService() throws IOException, GeneralSecurityException {
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		// TODO: Change placeholder below to generate authentication credentials. See
		// https://developers.google.com/sheets/quickstart/java#step_3_set_up_the_sample
		//
		// Authorize using one of the following scopes:
		//   "https://www.googleapis.com/auth/drive"
		//   "https://www.googleapis.com/auth/drive.file"
		//   "https://www.googleapis.com/auth/spreadsheets"
		return new Sheets.Builder(httpTransport, jsonFactory, getCredentialss(httpTransport)).setApplicationName("Google-SheetsSample/0.1").build();
	}

	private static Credential getCredentialss(final HttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = attendanceDataService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH))).setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	private static final String APPLICATION_NAME = "gSheetReader";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "/tokensss";
	

	/**
	 * Global instance of the scopes required by this quickstart.
	 * If modifying these scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private static final String CREDENTIALS_FILE_PATH = "/google-sheets-client-secret.json";

	/**
	 * Creates an authorized Credential object.
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = attendanceDataService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH))).setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	/**
	 * Prints the names and majors of students in a sample spreadsheet:
	 * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
	 */
}
