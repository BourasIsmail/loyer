package ma.entraide.impot.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import ma.entraide.impot.Entity.Paiement;
import ma.entraide.impot.Entity.Proprietaire;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static ma.entraide.impot.Service.NombreEnLettres.convertir;

public class PdfGenerator {
    public static byte[] generatePdfEtat(List<Paiement> paiements) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Create a PDF writer
        PdfWriter pdfWriter = new PdfWriter(baos);

        // Create a PdfDocument object
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);

        // Create a Document object
        Document document = new Document(pdfDocument);

        // Setting font of the text
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        String logoEntraide = "https://www.entraide.ma/attachements/63ee610678ddb300245a875a-enn.png";
        ImageData imgData = ImageDataFactory.create(logoEntraide);

        // Creating an Image object
        Image img = new Image(imgData);

        document.add(img);
        Paiement pm = paiements.get(0);


        Text t2 = new Text("ETAT DES LOYERS du mois "+pm.getMoisAnnee());
        t2.setFont(font);
        Paragraph para1 = new Paragraph(t2);
        para1.setTextAlignment(TextAlignment.CENTER);
        document.add(para1);


        String sousTitre = " COORDINATION DE " +pm.getLocal().getProvince().getRegion().getName();
        Text text = new Text(sousTitre);
        text.setFont(font);
        Paragraph para2 = new Paragraph(text);
        para2.setTextAlignment(TextAlignment.CENTER);
        document.add(para2);

        float [] pointColumnWidths = {100F, 150F, 50F, 50F, 50F, 50F, 50F};
        Table table = new Table(pointColumnWidths);

        table.addCell(new Cell().add(new Paragraph("Nom et Prenom")));
        table.addCell(new Cell().add(new Paragraph("RIB")));
        table.addCell(new Cell().add(new Paragraph("Delegation")));
        table.addCell(new Cell().add(new Paragraph("Taux de RAS")));
        table.addCell(new Cell().add(new Paragraph("Montant brute")));
        table.addCell(new Cell().add(new Paragraph("RAS")));
        table.addCell(new Cell().add(new Paragraph("Montant Net")));

        double totalNet= 0;
        double totalBrute = 0;
        double totalRas = 0;
        for(Paiement paiement: paiements) {
            if (paiement.getLocal().getEtat().equals("actif")) {
                totalNet += paiement.getNetMensuel();
                totalBrute += paiement.getBruteMensuel();
                totalRas += paiement.getRas();
                StringBuilder nomComplet = new StringBuilder();
                for (Proprietaire p : paiement.getLocal().getProprietaires()) {
                    nomComplet.append(p.getNomComplet()).append(" ");
                }
                Proprietaire p1 = paiement.getLocal().getProprietaires().get(0);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(nomComplet))));
                table.addCell(new Cell().add(new Paragraph(paiement.getLocal().getRib())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(paiement.getLocal().getProvince().getName()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(paiement.getPourcentageRAS()) + " %")));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(paiement.getBruteMensuel()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(paiement.getRas()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(paiement.getNetMensuel()))));
            }
        }
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("Total")));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(totalBrute))));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(totalRas))));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(totalNet))));
        document.add(table);

        String nombreEnLettre = convertir(totalNet);
        Paragraph message = new Paragraph("Arrêter cet état à la somme de: "+nombreEnLettre);
        document.add(message);
        document.close();
        return baos.toByteArray();

    }
}
