package com.increff.invoice.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.increff.invoice.model.form.OrderForm;
import com.increff.invoice.model.form.OrderItemForm;
import com.increff.invoice.service.ApiException;
import lombok.extern.log4j.Log4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Log4j
public class XMLCreation {
    public static void createOrderXml(OrderForm orderForm) throws ApiException {
        log.warn("CAME HERE!!");
        try{
            log.warn("orderForm time " + orderForm.getInvoicedTime());
            Instant instant = Instant.ofEpochSecond(Long.parseLong(String.valueOf(orderForm.getInvoicedTime())));
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

            // Format ZonedDateTime to desired string format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String formattedDate = zonedDateTime.format(formatter);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Root element
            Element rootElement = doc.createElement("Order");
            doc.appendChild(rootElement);

            Element titleElement = doc.createElement("Title");
            titleElement.appendChild(doc.createTextNode("Increff Point of Sale"));
            rootElement.appendChild(titleElement);

            Element headingElement = doc.createElement("Heading");
            rootElement.appendChild(headingElement);

            Element logoElement = doc.createElement("Logo");
            logoElement.setAttribute("src", "/Users/satwikchowdary/Desktop/pos-app/invoice-app/src/main/resources");
            headingElement.appendChild(logoElement);

            Element addressElement = doc.createElement("Address");
            addressElement.appendChild(doc.createTextNode("Address"));
            headingElement.appendChild(addressElement);

            createElementWithText(doc, headingElement, "CustomerName", orderForm.getCustomerName());
            createElementWithText(doc, headingElement, "InvoicedTime", formattedDate);
            createElementWithText(doc, headingElement, "OrderId", orderForm.getOrderId().toString());

            OrderItemForm[] orderItems = orderForm.getOrderItems();
            // Iterate over each order item
            Double totalOrderPrice = new Double(0.0);
            for (OrderItemForm orderItemForm : orderItems) {
                String barcode = orderItemForm.getBarcode();
                String productName = orderItemForm.getProductName();
                String quantity = orderItemForm.getQuantity().toString();
                String sellingPrice = orderItemForm.getSellingPrice().toString();
                String totalPrice = orderItemForm.getTotalPrice().toString();
                totalOrderPrice += orderItemForm.getTotalPrice();

                // Order item element
                Element orderItemElement = doc.createElement("OrderItem");
                rootElement.appendChild(orderItemElement);

                // Order item details
                createElementWithText(doc, orderItemElement, "Barcode", barcode);
                createElementWithText(doc, orderItemElement, "ProductName", productName);
                createElementWithText(doc, orderItemElement, "Quantity", quantity);
                createElementWithText(doc, orderItemElement, "SellingPrice", sellingPrice);
                createElementWithText(doc, orderItemElement, "TotalPrice", totalPrice);
            }
            createElementWithText(doc, rootElement, "TotalOrderPrice", String.valueOf(totalOrderPrice));

            // Save XML document to a file
            File file = new File("orderItemForm.xml");
            FileOutputStream fos = new FileOutputStream(file);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(fos));
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createElementWithText(Document doc, Element parentElement, String tagName, String text) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(text));
        parentElement.appendChild(element);
    }
}
