package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Entity.Proprietaire;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;


public class ExcelFileGenerator {

    public static byte[] generateLocalsExcel(List<Local> locals) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Locals");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Adresse du local");
            headerRow.createCell(1).setCellValue("RIB");
            headerRow.createCell(2).setCellValue("Proprietaire");

            // Create data rows
            int rowNum = 1;
            for (Local local : locals) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(local.getAdresse());
                row.createCell(1).setCellValue(local.getRib());

                String proprietaireNames = local.getProprietaires().stream()
                        .map(Proprietaire::getNomComplet)
                        .collect(Collectors.joining(" et "));
                String proprietaireName = proprietaireNames.isEmpty() ? "N/A" : proprietaireNames;
                row.createCell(2).setCellValue(proprietaireName);
            }

            // Autosize columns
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}