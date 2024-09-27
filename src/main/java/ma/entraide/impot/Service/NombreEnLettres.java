package ma.entraide.impot.Service;

import java.text.DecimalFormat;

public class NombreEnLettres {

    private static final String[] UNITS = {
            "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix",
            "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"
    };

    private static final String[] TENS = {
            "", "", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingt", "quatre-vingt-dix"
    };

    public static String convertir(double nombre) {
        // Séparer la partie entière et la partie décimale
        long partieEntiere = (long) nombre;
        int partieDecimale = (int) Math.round((nombre - partieEntiere) * 100); // convertir la partie décimale en centièmes

        String result = convertNombre(partieEntiere) + " dirhams";

        if (partieDecimale > 0) {
            result +=   convertNombre(partieDecimale) + "centimes";
        }

        return result;
    }

    private static String convertNombre(long nombre) {
        if (nombre == 0) {
            return "zéro";
        } else if (nombre < 20) {
            return UNITS[(int) nombre];
        } else if (nombre < 100) {
            return convertDoubleDigits((int) nombre);
        } else if (nombre < 1000) {
            return convertTripleDigits((int) nombre);
        } else {
            return convertThousandsAndMore(nombre);
        }
    }

    private static String convertDoubleDigits(int nombre) {
        if (nombre < 70) {
            if (nombre % 10 == 0) {
                return TENS[nombre / 10];
            } else {
                return TENS[nombre / 10] + "-" + UNITS[nombre % 10];
            }
        } else if (nombre < 80) {
            return "soixante-" + convertNombre(nombre - 60);
        } else {
            return "quatre-vingt" + (nombre % 20 == 0 ? "" : "-" + convertNombre(nombre % 20));
        }
    }

    private static String convertTripleDigits(int nombre) {
        if (nombre == 100) {
            return "cent";
        } else if (nombre < 200) {
            return "cent " + convertNombre(nombre - 100);
        } else {
            return UNITS[nombre / 100] + " cent" + (nombre % 100 == 0 ? "" : " " + convertNombre(nombre % 100));
        }
    }

    private static String convertThousandsAndMore(long nombre) {
        if (nombre < 2000) {
            return "mille " + convertNombre(nombre % 1000);
        } else if (nombre < 1000000) {
            return convertNombre(nombre / 1000) + " mille" + (nombre % 1000 == 0 ? "" : " " + convertNombre(nombre % 1000));
        } else if (nombre < 2000000) {
            return "un million " + convertNombre(nombre % 1000000);
        } else {
            return convertNombre(nombre / 1000000) + " millions " + (nombre % 1000000 == 0 ? "" : convertNombre(nombre % 1000000));
        }
    }

    /*
    public static void main(String[] args) {
        double nombre = 123456.78;
        System.out.println(new DecimalFormat("#,###.##").format(nombre) + " en lettres : " + convertir(nombre));
    }
    */
}

