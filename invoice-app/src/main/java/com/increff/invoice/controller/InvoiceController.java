package com.increff.invoice.controller;


import com.increff.invoice.dto.PdfDto;
import com.increff.invoice.model.form.OrderForm;
import com.increff.invoice.model.form.OrderItemForm;
import com.increff.invoice.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@CrossOrigin(origins = "http://0.0.0.0:9000")
public class InvoiceController {

    @Autowired
    private PdfDto pdfDto;

    @ApiOperation(value = "Generates pdf for an order")
    @RequestMapping(path = "/api/generate-pdf", method = RequestMethod.POST)
    public String generatePdf(@RequestBody OrderForm orderForm) throws ApiException {
        return pdfDto.generatePdf(orderForm);
    }

}
