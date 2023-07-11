<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <!-- Template matching rule for the root node -->
    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="invoice-page" page-height="11in" page-width="8.5in"
                                       margin-top="0.5in" margin-bottom="0.5in" margin-left="0.5in" margin-right="0.5in">
                    <fo:region-body margin-top="1.5in" margin-bottom="2in"/>
                    <fo:region-before extent="1in" region-name="header" display-align="center"/>
                    <fo:region-after extent="0.5in" region-name="footer" display-align="center"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="invoice-page">
                <fo:static-content flow-name="header">
                    <fo:block text-align="center" font-size="18pt" color="darkblue" font-weight="bold">
                        <xsl:value-of select="Order/Title"/>
                    </fo:block>
                </fo:static-content>
                <fo:static-content flow-name="footer">
                    <fo:block text-align="center" font-size="12pt" color="gray">
                        Page <fo:page-number/>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block-container width="100%">
                        <fo:block font-size="12pt" font-weight="italic" margin-bottom="10pt" width="50%">
                            <xsl:text>Customer Name: </xsl:text>
                            <xsl:value-of select="Order/Heading/CustomerName"/>
                        </fo:block>
<!--                        <fo:block-container text-align="end" margin-bottom="10pt" width="100%">-->
<!--                             <fo:block font-size="12pt" font-style="italic" width="50%" >-->
<!--                                <xsl:text>Address: </xsl:text>-->
<!--                                <xsl:value-of select="Order/Heading/Address"/>-->
<!--                             </fo:block>-->
<!--                         </fo:block-container>-->
                    </fo:block-container>
                    <fo:block font-size="12pt" font-style="italic" margin-bottom="10pt">
                        <xsl:text>Invoice Date: </xsl:text>
                        <xsl:value-of select="Order/Heading/InvoicedTime"/>
                    </fo:block>
                    <fo:block margin-bottom="20pt">
                        <xsl:text>Order ID: </xsl:text>
                        <xsl:value-of select="Order/Heading/OrderId"/>
                    </fo:block>
                    <fo:table table-layout="fixed" width="100%" border-collapse="collapse" border="1pt solid black" margin-bottom="20pt">
                        <fo:table-column column-width="proportional-column-width(2)"/>
                        <fo:table-column column-width="proportional-column-width(4)"/>
                        <fo:table-column column-width="proportional-column-width(2)"/>
                        <fo:table-column column-width="proportional-column-width(2)"/>
                        <fo:table-column column-width="proportional-column-width(2)"/>
                        <fo:table-header>
                            <fo:table-row>
                                <fo:table-cell background-color="#cccccc" padding="5pt" border="1pt solid black">
                                    <fo:block font-weight="bold">Barcode</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="#cccccc" padding="5pt" border="1pt solid black">
                                    <fo:block font-weight="bold">Product Name</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="#cccccc" padding="5pt" border="1pt solid black">
                                    <fo:block font-weight="bold">Quantity</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="#cccccc" padding="5pt" border="1pt solid black">
                                    <fo:block font-weight="bold">Selling Price</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="#cccccc" padding="5pt" border="1pt solid black">
                                    <fo:block font-weight="bold">Total Price</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="Order/OrderItem">
                                <fo:table-row>
                                    <fo:table-cell padding="5pt" border="1pt solid black">
                                        <fo:block><xsl:value-of select="Barcode"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="5pt" border="1pt solid black">
                                        <fo:block><xsl:value-of select="ProductName"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="5pt" border="1pt solid black">
                                        <fo:block><xsl:value-of select="Quantity"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="5pt" border="1pt solid black">
                                        <fo:block><xsl:value-of select="SellingPrice"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="5pt" border="1pt solid black">
                                        <fo:block><xsl:value-of select="TotalPrice"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    <fo:block margin-top="20pt" font-size="14pt" font-weight="bold">
                        Total Order Price: <xsl:value-of select="Order/TotalOrderPrice"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

</xsl:stylesheet>
