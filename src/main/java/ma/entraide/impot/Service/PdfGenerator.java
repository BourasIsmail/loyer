package ma.entraide.impot.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import ma.entraide.impot.Entity.*;
import org.apache.commons.lang3.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ma.entraide.impot.Service.NombreEnLettres.convertir;

public class PdfGenerator {
    public static byte[] generatePdfEtat(List<Paiement> paiements) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Create a PDF writer
        PdfWriter pdfWriter = new PdfWriter(baos);

        // Create a PdfDocument object
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);

        pdfDocument.setDefaultPageSize(PageSize.A3);

        // Create a Document object
        Document document = new Document(pdfDocument);

        // Setting font of the text
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        String logoEntraide = "https://www.entraide.ma/attachements/63ee610678ddb300245a875a-enn.png";
        ImageData imgData = ImageDataFactory.create(logoEntraide);

        // Creating an Image object
        Image img = new Image(imgData);
        // Set the image alignment to center
        img.setMarginLeft(80);
        document.add(img);
        Paiement pm = paiements.get(0);
        // Generate QR Code
        String qrCodeContent = "ETAT DES LOYERS du mois " + pm.getMoisAnnee() +
                "\nCOORDINATION DE " + pm.getLocal().getProvince().getRegion().getName() ;
        try {
            byte[] qrCodeImage = QRCodeGenerator.generateQRCodeImage(qrCodeContent);
            ImageData qrImageData = ImageDataFactory.create(qrCodeImage);
            Image qrCode = new Image(qrImageData);
            qrCode.setWidth(100);
            qrCode.setHeight(100);
            qrCode.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(qrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        float [] pointColumnWidths = {200F, 120F, 80, 80F, 80F, 80F};
        Table table = new Table(pointColumnWidths);

        table.addCell(new Cell().add(new Paragraph("Nom et Prénom")));
        table.addCell(new Cell().add(new Paragraph("Délégation")));
        table.addCell(new Cell().add(new Paragraph("Taux de RAS")));
        table.addCell(new Cell().add(new Paragraph("Montant brut")));
        table.addCell(new Cell().add(new Paragraph("RAS")));
        table.addCell(new Cell().add(new Paragraph("Montant Net")));

        double totalNet= 0.00;
        double totalBrute = 0.00;
        double totalRas = 0.00;
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
                table.addCell(new Cell().add(new Paragraph(String.valueOf(paiement.getLocal().getProvince().getName()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(paiement.getPourcentageRAS()) + " %")));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f",paiement.getBruteMensuel()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f",paiement.getRas()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f",paiement.getNetMensuel()))));
            }
        }
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("Total")));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f", totalBrute))));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f", totalRas))));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f", totalNet))));

        document.add(table);

        String nombreEnLettre = convertir(totalBrute);
        Paragraph message = new Paragraph("Arrêté cet état à la somme de: "+nombreEnLettre);
        document.add(message);

        document.close();
        return baos.toByteArray();
    }

    public static byte[] generateOV(List<Paiement> paiements, String nOrdre, String nOP, String date, ComptePayement c, String mode) throws IOException {
        Date dateSignature = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(dateSignature);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Create a PDF writer
        String nCompte = c.getNumCompte();
        String nom = c.getNom();
        int a = 1;
        if (mode.equals("trimestre")) {
             a = 3;
        }
        PdfWriter pdfWriter = new PdfWriter(baos);

        // Create a PdfDocument object
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);

        pdfDocument.setDefaultPageSize(PageSize.A3);

        // Create a Document object
        Document document = new Document(pdfDocument);

        // Setting font of the text
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        String logoEntraide = "https://www.entraide.ma/attachements/63ee610678ddb300245a875a-enn.png";
        ImageData imgData = ImageDataFactory.create(logoEntraide);

        // Creating an Image object
        Image img = new Image(imgData);
        // Set the image alignment to center
        img.setMarginLeft(90);
        document.add(img);
        Paiement pm = paiements.get(0);
        String n = String.valueOf(nOrdre);

        Text t1 = new  Text("Ordre de virement N°"+nOrdre);
        t1.setFont(font);
        Paragraph para1 = new Paragraph(t1);
        document.add(para1);

        Text t2 = new Text("A MONSIEUR LE CHEF DE L'AGENCE BANCAIRE CENTRALE DE "+nom+" Rabat");
        t2.setFont(font);
        Paragraph para2 = new Paragraph(t2);
        para2.setTextAlignment(TextAlignment.CENTER);
        document.add(para2);

        Text t3 = new Text("J'ai l'honneur de vous demander de bien vouloir faire procéder aux virements, désignés ci-après" +
                "  par le débit du compte n°\n"+nCompte+"\n ouvert à "+nom+", au nom de l'Entraide " +
                "Nationale. \n Réglement du loyer du mois de "+pm.getMoisAnnee()+".("+pm.getLocal().getProvince().getRegion().getName()+")\n" +
                "Veuillez agréer, Monsieur, l'expression de mes salutations distinguées.\n OP "+nOP+" du "+date);
        Paragraph para3 = new Paragraph(t3);
        para3.setTextAlignment(TextAlignment.CENTER);
        document.add(para3);

        float [] pointColumnWidths = {30F,300F,80F, 300F};
        Table table = new Table(pointColumnWidths);

        table.addCell(new Cell().add(new Paragraph("N°")));
        table.addCell(new Cell().add(new Paragraph("Nom et Prénom")));
        table.addCell(new Cell().add(new Paragraph("Montant du loyer")));
        table.addCell(new Cell().add(new Paragraph("RIB")));
        double totalNet= 0.00;
        int i = 1;
        for(Paiement paiement: paiements) {
            if (paiement.getLocal().getEtat().equals("actif")) {
                double pa = paiement.getNetMensuel()*a;
                totalNet += pa;
                StringBuilder nomComplet = new StringBuilder();
                for (Proprietaire p : paiement.getLocal().getProprietaires()) {
                    nomComplet.append(p.getNomComplet()).append(" ");
                }
                table.addCell(new Cell().add(new Paragraph(String.valueOf(i++))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(nomComplet))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f",(pa)))));
                table.addCell(new Cell().add(new Paragraph(paiement.getRib())));
            }
        }

        document.add(table);
        Text espace = new Text("\n");
        Paragraph paraEspace = new Paragraph(espace);
        document.add(paraEspace);

        float [] pointColumnWidths1 = {100F, 200F};
        Table table1 = new Table(pointColumnWidths1);
        table1.addCell(new Cell().add(new Paragraph("Total")));
        table1.addCell(new Cell().add(new Paragraph(String.format("%.2f", totalNet))));
        document.add(table1);

        String nombreEnLettre = convertir(totalNet);
        Paragraph message = new Paragraph("Arrêté cet état à la somme de: "+nombreEnLettre);
        document.add(message);
        Text dateSign = new Text("Rabat le :   "+formattedDate);
        Paragraph paraDateSign = new Paragraph(dateSign);
        paraDateSign.setTextAlignment(TextAlignment.RIGHT);
        document.add(paraDateSign);
        document.close();
        return baos.toByteArray();
    }

    public static byte[] generateProprietaireConfirmedPaymentsPdf(Proprietaire proprietaire, List<ConfirmedPayment> confirmedPayments, int year) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(baos);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A3);
        Document document = new Document(pdfDocument);

        String logoEntraide = "https://www.entraide.ma/attachements/63ee610678ddb300245a875a-enn.png";
        ImageData imgData = ImageDataFactory.create(logoEntraide);

        // Creating an Image object
        Image img = new Image(imgData);
        // Set the image alignment to center
        img.setMarginLeft(80);
        document.add(img);

        // Add title
        Paragraph title = new Paragraph("Etat annuel des retenues effectuées pour le propriétaire  " + proprietaire.getNomComplet() + " - année " + year)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16);
        document.add(title);
        String qrCodeContent = proprietaire.toString();
        try {
            byte[] qrCodeImage = QRCodeGenerator.generateQRCodeImage(qrCodeContent);
            ImageData qrImageData = ImageDataFactory.create(qrCodeImage);
            Image qrCode = new Image(qrImageData);
            qrCode.setWidth(100);
            qrCode.setHeight(100);
            qrCode.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(qrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Add proprietaire details
        document.add(new Paragraph("Détails du proprietaire :"));
        document.add(new Paragraph("Nom Complet: " + proprietaire.getNomComplet()));
        if(proprietaire.getCin() != null){
            document.add(new Paragraph("CIN: " + proprietaire.getCin()));
        }
        if(proprietaire.getTelephone() != null){
            document.add(new Paragraph("Telephone: " + proprietaire.getTelephone()));
        }
        if(proprietaire.getAdresse() != null ){
            document.add(new Paragraph("adresse: " + proprietaire.getAdresse()));
        }
        // Create table
        float [] pointColumnWidths1 = {300F, 30F, 30F, 30F, 30F, 200F};
        Table table = new Table(pointColumnWidths1);
        table.addHeaderCell("Adresse du Local");
        table.addHeaderCell("Mois");
        table.addHeaderCell("Montant Brut");
        table.addHeaderCell("Montant Net");
        table.addHeaderCell("RAS");
        table.addHeaderCell("RIB");

        double totalGross = 0;
        double totalNet = 0;
        double totalRas = 0;

        for (ConfirmedPayment payment : confirmedPayments) {
            Local local = payment.getLocal();
            if (local.getProprietaires().contains(proprietaire)) {
                if (payment.getYear() == 2024 && payment.getMois() < 8) {
                    continue;
                }
                    table.addCell(String.valueOf(local.getAdresse()+ " - "+local.getProvince().getName()));
                    table.addCell(String.valueOf(payment.getMois()));
                    table.addCell(String.format("%.2f", payment.getMontantBrute()));
                    table.addCell(String.format("%.2f", payment.getMontantNetPaye()));
                    table.addCell(String.format("%.2f", payment.getRas()));
                    table.addCell(payment.getRib());

                    totalGross += payment.getMontantBrute();
                    totalNet += payment.getMontantNetPaye();
                    totalRas += payment.getRas();

            }
        }
        // Add totals
        table.addCell("Total");
        table.addCell("");
        table.addCell(String.format("%.2f", totalGross));
        table.addCell(String.format("%.2f", totalNet));
        table.addCell(String.format("%.2f", totalRas));
        table.addCell("");

        document.add(table);

        // Add summary
        document.add(new Paragraph("Sommaire de l'année " + year + " :"));
        document.add(new Paragraph("Total montant brut : " + String.format("%.2f", totalGross) +" DH"));
        document.add(new Paragraph("Total montant net : " + String.format("%.2f", totalNet) +" DH"));
        document.add(new Paragraph("Total RAS : " + String.format("%.2f", totalRas) +" DH"));

        document.close();
        return baos.toByteArray();
    }
}
