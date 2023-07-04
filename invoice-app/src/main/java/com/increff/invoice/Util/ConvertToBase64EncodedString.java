package com.increff.invoice.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ConvertToBase64EncodedString {

    public static String convertPdf(){
        File pdffile = new File("out/Invoiced.pdf"); // Path to the generated PDF file
        try {
            // Read PDF file into a byte array
            byte[] pdfBytes = Files.readAllBytes(pdffile.toPath());
            // Encode the PDF bytes to Base64
            String base64String = Base64.getEncoder().encodeToString(pdfBytes);
            return base64String;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
