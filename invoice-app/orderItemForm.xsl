<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="invoice-page" page-height="11in" page-width="8.5in"
                                       margin-top="0.5in" margin-bottom="0.5in" margin-left="0.5in" margin-right="0.5in">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="invoice-page">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-family="Arial" font-size="18pt" text-align="center" margin-bottom="20pt">
                        <fo:inline font-weight="bold">Invoice</fo:inline>
                    </fo:block>
                    <fo:block font-family="Arial" font-size="14pt" font-weight="bold" margin-bottom="10pt">
                        <fo:inline>Customer Name: </fo:inline>
                        <xsl:value-of select="Order/Heading/CustomerName"/>
                    </fo:block>
                    <fo:block font-family="Arial" font-size="14pt" font-weight="bold" margin-bottom="10pt">
                        <fo:inline>Order Invoiced Time: </fo:inline>
                        <xsl:value-of select="Order/Heading/OrderInvoicedTime"/>
                    </fo:block>
                    <fo:block font-family="Arial" font-size="14pt" font-weight="bold" margin-bottom="20pt">
                        <fo:inline>Order ID: </fo:inline>
                        <xsl:value-of select="Order/Heading/OrderId"/>
                    </fo:block>
                    <fo:table table-layout="fixed" width="100%" border-collapse="collapse" margin-bottom="20pt">
                        <fo:table-column column-width="proportional-column-width(1)"/>
                        <fo:table-column column-width="proportional-column-width(1)"/>
                        <fo:table-column column-width="proportional-column-width(1)"/>
                        <fo:table-column column-width="proportional-column-width(1)"/>
                        <fo:table-column column-width="proportional-column-width(1)"/>
                        <fo:table-header>
                            <fo:table-row>
                                <fo:table-cell background-color="#4CAF50" color="white" padding="6pt" text-align="center">
                                    <fo:block font-weight="bold">Barcode</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="#4CAF50" color="white" padding="6pt" text-align="center">
                                    <fo:block font-weight="bold">Product Name</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="#4CAF50" color="white" padding="6pt" text-align="center">
                                    <fo:block font-weight="bold">Quantity</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="#4CAF50" color="white" padding="6pt" text-align="center">
                                    <fo:block font-weight="bold">Selling Price</fo:block>
                                </fo:table-cell>
                                <fo:table-cell background-color="#4CAF50" color="white" padding="6pt" text-align="center">
                                    <fo:block font-weight="bold">Total Price</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="Order/OrderItem">
                                <fo:table-row>
                                    <fo:table-cell padding="4pt" text-align="left" border="solid 1px #CCCCCC">
                                        <fo:block><xsl:value-of select="Barcode"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="4pt" text-align="left" border="solid 1px #CCCCCC">
                                        <fo:block><xsl:value-of select="ProductName"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="4pt" text-align="right" border="solid 1px #CCCCCC">
                                        <fo:block><xsl:value-of select="Quantity"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="4pt" text-align="right" border="solid 1px #CCCCCC">
                                        <fo:block><xsl:value-of select="SellingPrice"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="4pt" text-align="right" border="solid 1px #CCCCCC">
                                        <fo:block><xsl:value-of select="TotalPrice"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                            <fo:table-row>
                                <fo:table-cell number-columns-spanned="4" padding="4pt" text-align="right" background-color="#4CAF50" color="white">
                                    <fo:block font-weight="bold">Total Order Price:</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="4pt" text-align="right" background-color="#4CAF50" color="white">
                                    <fo:block font-weight="bold"><xsl:value-of select="Order/TotalOrderPrice"/></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                    <fo:block text-align="right">
                        <xsl:apply-templates select="Order/Heading/Address"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

</xsl:stylesheet>
