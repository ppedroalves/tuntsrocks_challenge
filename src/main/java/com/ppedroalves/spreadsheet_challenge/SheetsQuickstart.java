package com.ppedroalves.spreadsheet_challenge;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "Engenharia de Dados";

    private static Sheets sheetsService;

    private static Integer totalClass;

    private static final String SPREADSHEET_ID =  "19x4dNl5M_mSerXQQX163MHtf97XywVolMZOVfWHljCE";

    private static Credential auth() throws  IOException, GeneralSecurityException{
        InputStream in = SheetsQuickstart.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
              GsonFactory.getDefaultInstance(), new InputStreamReader(in)
        );

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");


    }

    public static Sheets getSheetsService() throws  IOException, GeneralSecurityException{

        Credential credential = auth();
        return  new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException{
        final String range = "A4:F";

        sheetsService = getSheetsService();

        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        ValueRange value = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "A2:H2").execute();
        totalClass = Integer.parseInt(value.getValues().get(0).toString().split(": ")[1].replace("]", ""));

        List<List<Object>> values = response.getValues();

         sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "A4:H", processStudentData(values))
                .setValueInputOption("RAW")
                .execute();

    }


    private static boolean isFailedByAttendance (Integer presentClass) {
        return presentClass > (totalClass * 0.25);
    }


    private static ValueRange processStudentData(List<List<Object>> values){
        for(List<Object> row : values){
            System.out.println("Processando dados do aluno: " + row.get(1));
            double averageNote = 0.0;
            if(!isFailedByAttendance (Integer.parseInt(row.get(2).toString()))){
                Double p1 = Double.parseDouble(row.get(3).toString());
                Double p2 = Double.parseDouble(row.get(4).toString());
                Double p3 = Double.parseDouble(row.get(5).toString());
                averageNote = Math.ceil(( p1 + p2 + p3) / 3);
                if(averageNote >= 70){
                    row.add(6, "APROVADO");
                }else if (averageNote < 50){
                    row.add(6, "REPROVADO POR NOTA");
                } else{
                    row.add(6, "EXAME FINAL");
                    Double finalExam = 100 - averageNote;
                    row.add(7, finalExam);
                }
            }else{
                row.add(6, "REPROVADO POR FALTA");
            }


        }

        return new ValueRange().setValues(values);
    }

}