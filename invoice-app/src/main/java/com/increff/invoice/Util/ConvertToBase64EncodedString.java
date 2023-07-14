package com.increff.invoice.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ConvertToBase64EncodedString {

    public static String convertPdf(){
        File pdffile = new File("out/Invoiced1.pdf");
        try {
            byte[] pdfBytes = Files.readAllBytes(pdffile.toPath());
            String base64String = Base64.getEncoder().encodeToString(pdfBytes);
            return base64String;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
