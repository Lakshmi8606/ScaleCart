package com.scalecart.report.service;

import com.scalecart.report.dto.InvoiceRequest;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.util.List;

public class InvoiceItemDataSource implements JRDataSource {

    private final List<InvoiceRequest.InvoiceItem> items;
    private int currentIndex = -1;  // starts before first row

    public InvoiceItemDataSource(List<InvoiceRequest.InvoiceItem> items) {
        this.items = items;
    }

    @Override
    public boolean next() throws JRException {
        currentIndex++;
        return currentIndex < items.size();
    }

    @Override
    public Object getFieldValue(JRField field) throws JRException {
        InvoiceRequest.InvoiceItem item = items.get(currentIndex);

        // field.getName() matches the <field name="..."> in JRXML
        return switch (field.getName()) {
            case "productName" -> item.getProductName();
            case "quantity"    -> item.getQuantity();
            case "price"       -> item.getPrice();
            case "lineTotal"   -> item.getLineTotal();
            default -> throw new JRException(
                    "Unknown field: " + field.getName());
        };
    }
}
