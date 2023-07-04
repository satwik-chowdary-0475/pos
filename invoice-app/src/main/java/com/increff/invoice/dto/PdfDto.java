package com.increff.invoice.dto;

import com.increff.invoice.Util.ConvertToBase64EncodedString;
import com.increff.invoice.Util.XMLCreation;
import com.increff.invoice.model.form.OrderForm;
import com.increff.invoice.service.ApiException;
import lombok.extern.log4j.Log4j;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.OutputStream;

@Service
@Log4j
public class PdfDto {
    public String generatePdf(OrderForm orderForm) throws ApiException {
        XMLCreation.createOrderXml(orderForm);
        try {
            // Setup directories
            File baseDir = new File(".");
            File outDir = new File(baseDir, "out");
            outDir.mkdirs();

            // Setup input and output files
            File xmlfile = new File(baseDir, "orderItemForm.xml");
            File xsltfile = new File(baseDir, "orderItemForm.xsl");
            File pdffile = new File(outDir, "Invoiced.pdf");


            // configure fopFactory as desired
            final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired

            // Setup output
            OutputStream out = new java.io.FileOutputStream(pdffile);
            out = new java.io.BufferedOutputStream(out);

            try {
                // Construct fop with desired output format
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                // Setup XSLT
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

                // Set the value of a <param> in the stylesheet
                transformer.setParameter("versionParam", "2.0");

                // Setup input for XSLT transformation
                Source src = new StreamSource(xmlfile);

                // Resulting SAX events (the generated FO) must be piped through to FOP
                Result res = new SAXResult(fop.getDefaultHandler());

                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);
            } finally {
                out.close();
                return ConvertToBase64EncodedString.convertPdf();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }

        return null;
    }

}
