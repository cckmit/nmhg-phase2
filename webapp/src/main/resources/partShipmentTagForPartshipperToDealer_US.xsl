<?xml version="1.0"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
               xmlns:xsd="http://www.w3.org/1999/XMLSchema"
               xmlns:fo="http://www.w3.org/1999/XSL/Format"
               xmlns:fox="http://xml.apache.org/fop/extensions" xmlns:ssl="http://www.w3.org/1999/XSL/Transform"
               xmlns:Locale="java.util.Locale"
               xmlns:java="java">

    <xsl:variable name="language" select="shipmentTag/language"/>
    <xsl:variable name="bu" select="shipmentTag/businessUnit"/>
    <xsl:variable name="locale" select="Locale:new($language, '', $bu)"/>
    <xsl:variable name="resources"
                  select="java:util.ResourceBundle.getBundle('messages', $locale)"/>
    <xsl:variable name="header" select="shipmentTag/nmhgToDealerShipment"/>
    <xsl:output method="xml"/>



    <xsl:template name="emptyLine">
        <xsl:param name="numberOfCloumns"/>
        <fo:table-row>
            <fo:table-cell number-columns-spanned="$numberOfCloumns">
                <fo:block line-height="9pt" font-size="8pt" space-before.optimum="1.5pt"
                          space-after.optimum="1.5pt"
                          keep-together="auto" text-align="start">
                    <fo:leader end-indent="0cm" start-indent="0cm" space-after.optimum="0pt"
                               space-before.optimum="0pt"
                               rule-thickness="0pt" leader-pattern="rule"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

    <xsl:template name="displayPartDetail">
        <fo:table-row>
            <fo:table-cell>
                <fo:block>
                    <fo:table border="solid 0.5px black" border-width="100%" table-layout="fixed" text-align="start">
                        <fo:table-column column-width="4.88cm"/>
                        <fo:table-column column-width="14cm"/>
                        <fo:table-body>
                            <xsl:call-template name="emptyLine">
                                <xsl:with-param name="numberOfCloumns" select="'2'"/>
                            </xsl:call-template>
                              <xsl:if test="$bu='AMER'">
                            <fo:table-row>
                                <fo:table-cell >
                                    <fo:block>
                                        <fo:external-graphic src="url(servlet-context:/image/logo_NMHG.gif)"  content-height="0.40in" />

                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        <fo:inline font-size="14pt" font-weight="bold" text-decoration="underline"
                                                   left="60px" padding-start="30pt">                                                                                                  
                                             <xsl:choose>
	                                             <xsl:when test="$header='true'">
	                                             <xsl:value-of
	                                                    select="java:getString($resources,'label.partShipmentTag.DealerPartsReturn')"/>	
	                                             </xsl:when>
	                                             <xsl:otherwise>
	                                               <xsl:value-of
	                                                    select="java:getString($resources,'label.partShipmentTag.supplierPartsReturn')"/>	             
	                                             </xsl:otherwise>
                                              </xsl:choose>
                                              -
                                             <xsl:value-of select="claim/claimNumber"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                  </fo:table-row>
<!--                                 
                            <fo:table-row>
                                <fo:table-cell  padding-start="0.5cm" number-columns-spanned="2" width="80%" padding-end="0.5cm" >
                                    <fo:block border="solid 0.5px black" >
                                  
                                        <fo:inline font-size="9pt" font-weight="normal"  margin-left="1mm">
                                            <xsl:value-of
                                                    select="part/dueDateMessage" />
                                        </fo:inline>
                             
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row> -->
                              </xsl:if>

                            <xsl:call-template name="emptyLine">
                                <xsl:with-param name="numberOfCloumns" select="'1'"/>
                            </xsl:call-template>
                            <fo:table-row>
                                <fo:table-cell padding-start="0.5cm" number-columns-spanned="2"
                                               width="40%"><!-- Part Details, Receiver Instructions and claim comments goes here -->
                                    <fo:table>
                                        <fo:table-column column-width="9cm"/>
                                        <fo:table-column column-width="14cm"/>
                                        <fo:table-body>
                                            <fo:table-cell>
                                                <fo:block font-family="sans-seriff">
                                                    <fo:table>
                                                        <fo:table-body>
                                                            <fo:table-row><!-- Part Details row -->
                                                                <fo:table-cell>
                                                                    <fo:block border="solid 0.5px black">
                                                                        <fo:table><!-- Part Details table -->
                                                                            <fo:table-body>
                                                                                <xsl:call-template name="emptyLine">
                                                                                    <xsl:with-param
                                                                                            name="numberOfCloumns"
                                                                                            select="'1'"/>
                                                                                </xsl:call-template>
                                                                                 <xsl:if test="part/partNumber">
                                                                                <fo:table-row>
                                                                                    <fo:table-cell padding-start="5pt">
                                                                                        <fo:block >
                                                                                            <fo:inline font-size="9pt" font-weight="bold"
                                                                                                    >
                                                                                                <xsl:value-of
                                                                                                        select="java:getString($resources,'label.common.partNumber')"/>:
                                                                                            </fo:inline>
                                                                                            <fo:inline font-size="9pt">
                                                                                                <xsl:value-of
                                                                                                        select="part/partNumber"/>
                                                                                            </fo:inline>
                                                                                        </fo:block>
                                                                                    </fo:table-cell>
                                                                                </fo:table-row>
          

                                                                                </xsl:if>
                                                                          <xsl:if test="part/componentSerialNumber">
                                                                           <fo:table-row>
                                                                                    <fo:table-cell padding-start="5pt">
                                                                                        <fo:block >
                                                                                            <fo:inline font-size="9pt" font-weight="bold"
                                                                                                    >
                                                                                                <xsl:value-of
                                                                                                        select="java:getString($resources,'label.majorComponent.componentSerialNo')"/>:
                                                                                            </fo:inline>
                                                                                            <fo:inline font-size="9pt">
                                                                                                <xsl:value-of
                                                                                                        select="part/componentSerialNumber"/>
                                                                                            </fo:inline>
                                                                                        </fo:block>
                                                                                    </fo:table-cell>
                                                                                </fo:table-row>
                                                                                             
                                                                                 </xsl:if>
                                                                                  <xsl:if test="part/description">
                                                                                <fo:table-row>
                                                                                    <fo:table-cell padding-start="5pt">
                                                                                        <fo:block font-size="9pt">
                                                                                            <fo:inline
                                                                                                    font-weight="bold">
                                                                                                <xsl:value-of
                                                                                                        select="java:getString($resources,'label.common.description')"/>:
                                                                                            </fo:inline>
                                                                                            <fo:inline font-size="9pt">
                                                                                                <xsl:value-of
                                                                                                        select="part/description"/>
                                                                                            </fo:inline>
                                                                                        </fo:block>
                                                                                    </fo:table-cell>
                                                                                </fo:table-row>
                                                                              

                                                                                </xsl:if>    
<!--                                                                                 <xsl:if test="part/dueDate">                                                               
                                                                                <fo:table-row>
                                                                                    <fo:table-cell padding-start="5pt">
                                                                                        <fo:block 
                                                                                                 >
                                                                                            <fo:inline font-size="9pt" font-weight="bold">
                                                                                                <xsl:value-of
                                                                                                        select="java:getString($resources,'label.partShipmentTag.returnDueDate')"/>:
                                                                                            </fo:inline>
                                                                                            <fo:inline font-size="9pt">
                                                                                                <xsl:value-of
                                                                                                        select="part/dueDate"/>
                                                                                                    
                                                                                            </fo:inline>
                                                                                        </fo:block>
                                                                                    </fo:table-cell>
                                                                                </fo:table-row>

                                                                                </xsl:if>   -->
                                                                                  <xsl:if test="part/rmaNumber">  
                                                                                <fo:table-row>
                                                                                    <fo:table-cell padding-start="5pt">
                                                                                        <fo:block 
                                                                                                 >
                                                                                            <fo:inline font-size="9pt"  font-weight="bold" >
                                                                                                <xsl:value-of
                                                                                                        select="java:getString($resources,'label.partReturnConfiguration.rmaNumber')"/>:
                                                                                            </fo:inline>
                                                                                            <fo:inline  font-size="9pt">
                                                                                                <xsl:value-of
                                                                                                        select="part/rmaNumber"/>
                                                                                            </fo:inline>
                                                                                        </fo:block>
                                                                                    </fo:table-cell>
                                                                                </fo:table-row>                                                                                
                                                                       </xsl:if>
                                                                            </fo:table-body>
                                                                        </fo:table>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                            </fo:table-row>
                                                        </fo:table-body>
                                                    </fo:table>
                                                </fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding-start="0.5cm"
                                                           ><!-- claim number, rma number goes here -->
                                                <fo:block font-family="sans-seriff">
                                                    <fo:table>
                                                        <fo:table-body>
                                                            <fo:table-row>
                                                                <fo:table-cell>
                                                                    <fo:block>
                                                                        <fo:inline font-size="9pt" font-weight="bold"><xsl:value-of
                                                                                select="java:getString($resources,'columnTitle.common.claimNo')"/>:
                                                                        </fo:inline>
                                                                        <fo:inline font-size="9pt" 
                                                                                  >
                                                                            <xsl:value-of select="claim/claimNumber"/>
                                                                        </fo:inline>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                           </fo:table-row>
                                                          <xsl:if test="claim/recoveryClaimNumber">
                                                          <fo:table-row>
                                                                <fo:table-cell>
                                                                    <fo:block >
                                                                        <fo:inline font-size="9pt" font-weight="bold"><xsl:value-of
                                                                                select="java:getString($resources,'columnTitle.common.recClaimNo')"/>:
                                                                        </fo:inline>
                                                                        <fo:inline font-size="9pt"
                                                                                   >
                                                                            <xsl:value-of select="claim/recoveryClaimNumber"/>
                                                                        </fo:inline>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                            </fo:table-row> 
                                                            </xsl:if>
                                                            
                                                         </fo:table-body>
                                                    </fo:table>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-body>
                                    </fo:table>
                                </fo:table-cell>
                            </fo:table-row>
                                <xsl:call-template name="emptyLine">
                                <xsl:with-param name="numberOfCloumns" select="'1'"/>
                            </xsl:call-template>
                            <fo:table-row>
                            <fo:table-cell>
                              <fo:block font-family="sans-seriff">
                              <fo:table border-width="100%" table-layout="fixed" text-align="start" >
                            <fo:table-column column-width="6.88cm"/>
                            <fo:table-column column-width="10cm"/>
                           <fo:table-body>
                                 <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold" >
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partReturnConfiguration.supplierInstructions')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block font-family="sans-seriff">
				                    <fo:inline font-size="9pt" >
		
											
				                                                                                                     <xsl:value-of
                                                                                            select="part/shipperComments"/>	
				                          
				                      </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                                                                        <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:leader leader-length="100%"
                                                                                           leader-pattern="space">
                                                                                    &#x00A0;</fo:leader>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                            <xsl:if test="claim/shippingInstruction">
                            <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block font-family="sans-seriff">
                                        <fo:inline font-size="9pt" font-weight="bold" >
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partReturnConfiguration.shippingInstructions')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block font-family="sans-seriff">
				                    <fo:inline font-size="9pt" >

				                                                                                                     <xsl:value-of
                                                                                            select="claim/shippingInstruction"/>
				                      </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </xsl:if>
                              </fo:table-body>
                              </fo:table>
                              </fo:block>
                            </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:block>
                <fo:block>
                    <fo:leader leader-pattern="space" leader-length="230pt"
                               border="solid 0.1px blue"/>
                    <fo:inline text-align="center" font-style="italic"
                               font-size="6pt">
                        <xsl:value-of select="java:getString($resources,'label.partShipmentTag.cutAlongLine')"/>
                    </fo:inline>
                    <fo:leader leader-pattern="space" leader-length="200pt"
                               border="solid 0.1px blue"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

    <xsl:template name="displayWarrantySummery">
        <fo:table-row>
            <fo:table-cell>
                <fo:block page-break-after="always" font-family="sans-seriff">
                 <fo:table border="solid 0.5px black" border-width="100%" table-layout="fixed" text-align="start">
                  <fo:table-column column-width="18.88cm"/>
              <fo:table-body>
               <fo:table-cell>
                       <fo:table table-layout="fixed">
                        <fo:table-column column-width="8.88cm"/>
                        <fo:table-column column-width="10cm"/>
                        <fo:table-body>
                            <xsl:call-template name="emptyLine">
                                <xsl:with-param name="numberOfCloumns" select="'1'"/>
                            </xsl:call-template>
                            
                       
                                 <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'columnTitle.common.claimType')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block >
                                        <fo:inline font-size="9pt">
                                            <xsl:variable name="Claimtest" select="claim/claimType"/>
                                             <xsl:if test="$Claimtest='Campaign'">
                                             <fo:inline font-size="9pt">
                                              Field Product Improvement
                                             </fo:inline>
                                              </xsl:if>
                                               <xsl:if test="$Claimtest='Machine'">
                                                <fo:inline font-size="9pt">
                                                Unit
                                                    </fo:inline>
                                               </xsl:if>
                                                <xsl:if test="$Claimtest='Parts'"> 
                                                <fo:inline font-size="9pt"> <xsl:value-of
                                                select="claim/claimType"/>
                                                   </fo:inline>
                                                </xsl:if>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                                 <xsl:if test="claim/machineSerialNumber">                          
                             <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'columnTitle.common.serialNo')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="claim/machineSerialNumber"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            </xsl:if>
                                <xsl:variable name="test" select="claim/claimType"/>
                                 <xsl:if test="$test='Campaign'"> 
                             <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.claimSearch.campaignCode')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="claim/fpiNumber"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                              </xsl:if>  
                                <xsl:variable name="testA" select="claim/claimType"/>   
                               <xsl:if test="$testA!='Parts'">
                                 <xsl:if test="claim/hourOnMeter">                      
                              <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partShipmentTag.hour.meter')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="claim/hourOnMeter"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </xsl:if>
                              </xsl:if> 
                                            <xsl:variable name="testB" select="claim/claimType"/>  
                                 <xsl:if test="$testB!='Parts'">
                                 <xsl:if test="claim/installDate">                             
		                               <fo:table-row>
		                                <fo:table-cell padding-start="0.5cm">
		                                    <fo:block>
		                                        <fo:inline font-size="9pt" font-weight="bold">
		                                            <xsl:value-of
		                                                    select="java:getString($resources,'label.partShipmentTag.installDate')"/>:
		                                        </fo:inline>
		                                    </fo:block>
		                                </fo:table-cell>
		                                <fo:table-cell padding-start="0.1cm">
		                                    <fo:block>
		                                        <fo:inline font-size="9pt"> <xsl:value-of
		                                                select="claim/installDate"/>
		                                        </fo:inline>
		                                    </fo:block>
		                                </fo:table-cell>   
		                            </fo:table-row>
                              </xsl:if>
                            </xsl:if>
                                 <xsl:if test="part/partNumber">  
                            <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partShipmentTag.problemPartNumber')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="part/partNumber"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </xsl:if> 
                                 <xsl:if test="part/vendorPartNumber">                              
                            <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partShipmentTag.vendorPartNumber')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="part/vendorPartNumber"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            </xsl:if> 
                               
                                 <xsl:if test="part/description"> 
                                 <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.common.causalPartDescription')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="part/description"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            </xsl:if>
                                <xsl:variable name="testC" select="claim/claimType"/> 
                              <xsl:if test="$testC='Parts'">
                                 <xsl:if test="claim/hourOnPart"> 
                             <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.common.hoursOnPart')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="claim/hourOnPart"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                             </xsl:if>
                            </xsl:if>
                        <xsl:variable name="testD" select="claim/claimType"/> 
                            <xsl:if test="$testD='Parts'">
                              <xsl:if test="claim/partFittedDate">
	                             <fo:table-row>
	                                <fo:table-cell padding-start="0.5cm">
	                                    <fo:block>
	                                        <fo:inline font-size="9pt" font-weight="bold">
	                                         Part Installed Date:
	                                        </fo:inline>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell padding-start="0.1cm">
	                                    <fo:block>
	                                        <fo:inline font-size="9pt"> <xsl:value-of
	                                                select="claim/partFittedDate"/>
	                                        </fo:inline>
	                                    </fo:block>
	                                </fo:table-cell>
	                            </fo:table-row>
                           </xsl:if>
                            </xsl:if>  
                                 <xsl:if test="claim/workOrderNumber">   
                                 <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partShipmentTag.jobNumber')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="claim/workOrderNumber"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            </xsl:if>  
                        <xsl:if test="claim/failureDate">
                             <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.warrantyAdmin.failureDate')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt"> <xsl:value-of
                                                select="claim/failureDate"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                                </xsl:if>
                </fo:table-body>
                </fo:table>
                                 
                <fo:table table-layout="fixed">
                <fo:table-column column-width="18.88cm"/>
                        
                        <fo:table-body>
                                 <fo:table-row>
     
                                    <fo:table-cell>
                                        <fo:block text-align="center">
                                            <fo:inline font-size="10pt" font-weight="bold" >
                                                <xsl:value-of
                                                        select="java:getString($resources,'label.partShipmentTag.problem.desc')"/>
                                            </fo:inline>
                                        </fo:block>
                       
                                    </fo:table-cell>
                                </fo:table-row> 
                </fo:table-body>
                </fo:table>
                      <fo:table table-layout="fixed">
                        <fo:table-column column-width="8.88cm"/>
                        <fo:table-column column-width="10cm"/>
                        <fo:table-body>
                                            
                            <xsl:call-template name="emptyLine">
                               <xsl:with-param name="numberOfCloumns" select="'1'"/>
                           </xsl:call-template> 
                            <xsl:if test="claim/fault">
                           <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partShipmentTag.fault')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding-start="0.1cm" height="1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" >

												<xsl:value-of
                                                select="claim/fault"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <xsl:call-template name="emptyLine">
                               <xsl:with-param name="numberOfCloumns" select="'1'"/>
                           </xsl:call-template>
                        </xsl:if>
                   <xsl:if test="claim/workPerformed">
                           <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.newClaim.workPerformed')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                 <fo:table-cell padding-start="0.1cm" height="1cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" >

																								<xsl:value-of
                                                select="claim/workPerformed"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <xsl:call-template name="emptyLine">
                               <xsl:with-param name="numberOfCloumns" select="'1'"/>
                           </xsl:call-template>  
                     </xsl:if>
                      <xsl:if test="claim/additionalNotes">
                           <fo:table-row>
                                <fo:table-cell padding-start="0.5cm">
                                    <fo:block>
                                        <fo:inline font-size="9pt" font-weight="bold">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partShipmentTag.cause')"/>:
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                                   <fo:table-cell padding-start="0.1cm" height="1cm">
                                    <fo:block>
                                     <fo:inline font-size="9pt" >
            
												<xsl:value-of
                                                select="claim/additionalNotes"/>
										</fo:inline>
                                
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <xsl:call-template name="emptyLine">
                               <xsl:with-param name="numberOfCloumns" select="'1'"/>
                           </xsl:call-template> 
                           </xsl:if>
                                
                        </fo:table-body>
                    </fo:table>
                    </fo:table-cell>
          </fo:table-body>
      </fo:table>
                      <fo:block>
                       <fo:inline font-size="9pt" font-weight="bold" text-decoration="underline" font-style="red"
                                                  >
                           <xsl:value-of     select="java:getString($resources,'label.partShipmentTag.attention')"/> :
                         </fo:inline>
                       </fo:block> 
                         <fo:block>
                    <fo:inline font-size="9pt">
                     <xsl:value-of
                             select="java:getString($resources,'label.partShipmentTag.dealerAdttionNotes')"/>
                     </fo:inline>
                     </fo:block>
                 </fo:block>
            
            </fo:table-cell>
        </fo:table-row>

    </xsl:template>
      
      <xsl:template name="pageLayout">
    
        <fo:layout-master-set>
            <fo:simple-page-master master-name="page">
                <fo:region-body region-name="body" margin-top="0.5in"
                                margin-bottom="0.5in" margin-left="0.36in" margin-right="0.5in"
                                background-image="watermark.png" background-repeat="no-repeat"
                                background-position-horizontal="left"
                                background-position-vertical="top"/>
                                  <fo:region-after extent="10mm"/>
            </fo:simple-page-master>
        </fo:layout-master-set>


    </xsl:template>
    <xsl:template name="displayShipmentTag">
        <fo:table-row>
            <fo:table-cell>
                <fo:block font-family="Calibri">
                    <!-- warranty parts return table starts  -->
                    <fo:table border="solid 0.5px black" border-width="100%" table-layout="fixed" text-align="start">
                        <fo:table-column column-width="4.88cm"/>
                        <fo:table-column column-width="14cm"/>
                        <fo:table-body>
                            <xsl:call-template name="emptyLine">
                                <xsl:with-param name="numberOfCloumns" select="'2'"/>
                            </xsl:call-template>                 
                            <fo:table-row>
                                <fo:table-cell >
                                    <fo:block>
                                        <fo:external-graphic src="url(servlet-context:/image/logo_NMHG.gif)"
                                                           content-height="0.40in" />
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell >
                                    <fo:block>
                                        <fo:inline font-size="14pt" font-weight="bold" text-decoration="underline"
                                                   left="80px" padding-start="50pt">
                                            <xsl:value-of
                                                    select="java:getString($resources,'label.partShipmentTag.shipmentTag')"/>
                                        </fo:inline>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>                               
                            <fo:table-row>
                                <fo:table-cell padding-start="0.5cm" number-columns-spanned="2"
                                               width="40%">
                                    <fo:block font-family="Calibri">
                                        <fo:table>
                                            <fo:table-column column-width="7cm"/>
                                            <fo:table-column column-width="3cm"/>
                                            <fo:table-column column-width="8cm"/>
                                            <!-- from and sender comments goes here -->
                                            <fo:table-body>
                                                <fo:table-row>
                                                    <fo:table-cell  >
                                                        <fo:block>
                                                            <fo:table><!-- from table -->
                                                                <fo:table-body>
                                                                    <fo:table-row>
                                                                        <fo:table-cell >
                                                                          <!--   <fo:block border="solid 0.5px black">
                                                                                <fo:table>
                                                                                    <fo:table-body>

                                                                                        <fo:table-row>
                                                                                            <fo:table-cell text-align="center">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            font-weight="bold"
                                                                                                            text-decoration="underline"
                                                                                                            >
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.returnToAddress')"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                     
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block font-size="9pt"
                                                                                                          font-weight="bold">
                                                                                                    <xsl:value-of
                                                                                                            select="from/name"/>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt">
                                                                                                        <xsl:value-of
                                                                                                                select="from/addressLine1"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt">
                                                                                                        <xsl:value-of
                                                                                                                select="from/addressLine2"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            >
                                                                                                        <xsl:if test="from/city">    
                                                                                                        <xsl:value-of
                                                                                                                select="from/city"/>,
                                                                                                        </xsl:if>
                                                                                                        <xsl:if test="from/state">         
                                                                                                        <xsl:value-of
                                                                                                                select="from/state"/>,
                                                                                                        </xsl:if>        
                                                                                                        <xsl:value-of
                                                                                                                select="from/zipCode"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            >
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.partShipmentTag.dealerNumber')"/>:
                                                                                                    </fo:inline>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                           >
                                                                                                        <xsl:value-of
                                                                                                                select="dealerNumber"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <xsl:call-template name="emptyLine">
                                                                                            <xsl:with-param name="numberOfCloumns" select="'1'"/>
                                                                                        </xsl:call-template>
                                                                                    </fo:table-body>
                                                                                </fo:table>
                                                                            </fo:block>
                                                                            
                                                                            
                                                                            
 -->                                                                       
 																  <fo:block font-family="Calibri">
                                                                                <fo:table
                                                                                        border="solid 0.5px black">
                                                                                    return to address and shipment details goes here
                                                                                    <fo:table-body>
                                                                                        <xsl:call-template
                                                                                                name="emptyLine">
                                                                                            <xsl:with-param
                                                                                                    name="numberOfCloumns" select="'1'"/>
                                                                                        </xsl:call-template>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell
                                                                                                    text-decoration="underline"
                                                                                                    text-align="center">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            font-weight="bold">
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.common.from')"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                      

                                                                                      <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            font-weight="bold">
                                                                                                        <xsl:value-of
                                                                                                                select="shipment/location"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>  
                                                                                                                                         
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt">
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/addressLine1"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt">
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/addressLine2"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                           >
                                                                                                        <xsl:if test="returnToAddress/address/city">    
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/city"/>,
                                                                                                        </xsl:if>
                                                                                                        <xsl:if test="returnToAddress/address/state">            
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/state"/>,
                                                                                                       </xsl:if>         
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/zipCode"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>

                                                                                        <xsl:call-template name="emptyLine">
                                                                                            <xsl:with-param name="numberOfCloumns" select="'1'"/>
                                                                                        </xsl:call-template>
                                                                                    </fo:table-body>
                                                                                </fo:table>
                                                                            </fo:block>

 																 </fo:table-cell>
                                                                    </fo:table-row>
                                                                    <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:leader leader-length="100%"
                                                                                           leader-pattern="space">
                                                                                    &#x00A0;</fo:leader>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                    <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:inline font-size="9pt"
                                                                                           font-weight="bold"
                                                                                          >
                                                                                    <xsl:value-of
                                                                                            select="java:getString($resources,'label.partShipmentTag.senderComments')"/>:
                                                                                </fo:inline>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>                                                                   
                                                                    <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block font-size="9pt">
        
                                                                           <xsl:value-of
                                                                                            select="shipment/senderComments"/>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                     <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:leader leader-length="100%"
                                                                                           leader-pattern="space">
                                                                                    &#x00A0;</fo:leader>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                </fo:table-body>
                                                            </fo:table>
                                                         <xsl:if test="$bu='AMER'"> 
                                                               <fo:table  border="solid 0.5px black">
                                                                <fo:table-column column-width="7cm"/>
                                                                <fo:table-body>
                                                                  
                                                                    <fo:table-row>
                                                                        <fo:table-cell padding-start="5pt">
                                                                            <fo:block>
                                                                                <fo:inline font-family="arial" font-size="9pt">
                                                                                    <xsl:value-of
                                                                                            select="java:getString($resources,'label.partShipmentTag.shipment.printDate')"/>:
                                                                                </fo:inline>
                                                                                <fo:inline font-family="arial" font-size="9pt">
                                                                                    <xsl:value-of
                                                                                            select="shipment/printDate"/>
                                                                                </fo:inline>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                </fo:table-body>
                                                            </fo:table>
                                                              <fo:table>
                                                                <fo:table-body>
                                                                 <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:leader leader-length="100%"
                                                                                           leader-pattern="space">
                                                                                    &#x00A0;</fo:leader>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                </fo:table-body>
                                                              </fo:table>  
<!--                                                                <fo:table  border="solid 0.5px black "  >
                                                               <fo:table-column column-width="7cm"  />
                                                               
                                                                <fo:table-body>
                                                                   <fo:table-row >
                                                                     <fo:table-cell  >
                                                                      <fo:block>
                                                                     <fo:inline  font-family="arial" font-size="9pt">
                                                                      <xsl:value-of
                                                                          select="java:getString($resources,'label.partShipmentTag.shipment.country')"/>:
                                                                     
                                                                     </fo:inline>
                                                                            <fo:inline></fo:inline>
                                                                     </fo:block>
                                                                     </fo:table-cell>
                                                                 </fo:table-row> 
                                                                           <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:leader leader-length="100%"
                                                                                           leader-pattern="space">
                                                                                    &#x00A0;</fo:leader>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>  
                                                                </fo:table-body>
                                                              </fo:table> -->
   
                                                        </xsl:if>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell>
                                                        <fo:block>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell >
                                                        <fo:block>
                                                            <fo:table>
                                                                <fo:table-body>
                                                                    <fo:table-row>
                                                                        <fo:table-cell >
                                                                        <fo:block border="solid 0.5px black">
                                                                                <fo:table>
                                                                                    <fo:table-body>

                                                                                        <fo:table-row>
                                                                                            <fo:table-cell text-align="center">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            font-weight="bold"
                                                                                                            text-decoration="underline"
                                                                                                            >
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.returnToAddress')"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                     
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block font-size="9pt"
                                                                                                          font-weight="bold">
                                                                                                    <xsl:value-of
                                                                                                            select="from/name"/>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt">
                                                                                                        <xsl:value-of
                                                                                                                select="from/addressLine1"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt">
                                                                                                        <xsl:value-of
                                                                                                                select="from/addressLine2"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            >
                                                                                                        <xsl:if test="from/city">    
                                                                                                        <xsl:value-of
                                                                                                                select="from/city"/>,
                                                                                                        </xsl:if>
                                                                                                        <xsl:if test="from/state">         
                                                                                                        <xsl:value-of
                                                                                                                select="from/state"/>,
                                                                                                        </xsl:if>        
                                                                                                        <xsl:value-of
                                                                                                                select="from/zipCode"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            >
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.partShipmentTag.dealerNumber')"/>:
                                                                                                    </fo:inline>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                           >
                                                                                                        <xsl:value-of
                                                                                                                select="dealerNumber"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <xsl:call-template name="emptyLine">
                                                                                            <xsl:with-param name="numberOfCloumns" select="'1'"/>
                                                                                        </xsl:call-template>
                                                                                    </fo:table-body>
                                                                                </fo:table>
                                                                            </fo:block>
                                                                          <!--   <fo:block font-family="Calibri">
                                                                                <fo:table
                                                                                        border="solid 0.5px black">
                                                                                    return to address and shipment details goes here
                                                                                    <fo:table-body>
                                                                                        <xsl:call-template
                                                                                                name="emptyLine">
                                                                                            <xsl:with-param
                                                                                                    name="numberOfCloumns" select="'1'"/>
                                                                                        </xsl:call-template>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell
                                                                                                    text-decoration="underline"
                                                                                                    text-align="center">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            font-weight="bold">
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.common.from')"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                      
                                                                                          <xsl:choose>
                                                                                          <xsl:when test="partDetails/partDetail/claim/supplierName">
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            font-weight="bold">
                                                                                                        <xsl:value-of
                                                                                                                select="partDetails/partDetail/claim/supplierName"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        </xsl:when>
                                                                                        <xsl:otherwise>
                                                                                      <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            font-weight="bold">
                                                                                                        <xsl:value-of
                                                                                                                select="shipment/location"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>  
                                                                                        </xsl:otherwise>
                                                                                        </xsl:choose>                                                                                     
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt">
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/addressLine1"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt">
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/addressLine2"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                           >
                                                                                                        <xsl:if test="returnToAddress/address/city">    
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/city"/>,
                                                                                                        </xsl:if>
                                                                                                        <xsl:if test="returnToAddress/address/state">            
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/state"/>,
                                                                                                       </xsl:if>         
                                                                                                        <xsl:value-of
                                                                                                                select="returnToAddress/address/zipCode"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>

                                                                                        <xsl:call-template name="emptyLine">
                                                                                            <xsl:with-param name="numberOfCloumns" select="'1'"/>
                                                                                        </xsl:call-template>
                                                                                    </fo:table-body>
                                                                                </fo:table>
                                                                            </fo:block>
 -->                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                    <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:leader leader-length="100%"
                                                                                           leader-pattern="space">
                                                                                    &#x00A0;</fo:leader>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                    <fo:table-row>
                                                                        <fo:table-cell >
                                                                            <fo:block font-family="sans-seriff">
                                                                                <fo:table border="solid 0.5px black">
                                                                                    <!-- return to address and shipment details goes here -->
                                                                                    <!--<fo:table-column column-width="100%" padding-start=".4cm" />-->
                                                                                    <fo:table-body>
                                                                                        <xsl:call-template name="emptyLine">
                                                                                            <xsl:with-param name="numberOfCloumns" select="'1'"/>
                                                                                        </xsl:call-template>
                                                                                          
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            text-align="center" font-weight="bold">
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.partReturnConfiguration.rmaNumber')"/>(s):
                                                                                                    </fo:inline>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                        
                                                                                                            text-align="center">
                                                                                                                                      <xsl:for-each select="partDetails/partDetail">
                                                                                                                                      <xsl:value-of
                                                                                                                select="part/rmaNumber"/>
																												<xsl:if test="position() != last()">
																													<xsl:text> , </xsl:text>
																												</xsl:if>
                               
                                                                                                </xsl:for-each>
                                                                                                        
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>  
                                                                                                                                                                 
                                                                                        <xsl:call-template name="emptyLine">
                                                                                            <xsl:with-param name="numberOfCloumns" select="'1'"/>
                                                                                        </xsl:call-template>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                            text-align="center" font-weight="bold">
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.partReturnConfiguration.shipmentNumber')"/>:
                                                                                                    </fo:inline>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"
                                                                                                          
                                                                                                            text-align="center">
                                                                                                        <xsl:value-of
                                                                                                                select="shipment/shipmentNumber"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell >
                                                                                                <fo:block>
                                                                                                    <fo:leader
                                                                                                            leader-length="100%"
                                                                                                            leader-pattern="space">
                                                                                                        &#x00A0;</fo:leader>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                         <xsl:if test="shipment/shipmentDate">
                                                                                        <fo:table-row>                                                                                               
                                                                                              <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                          font-weight="bold"
                                                                                                            font-size="9pt"
                                                                                                            text-align="center">
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'label.shipmentDate')"/>:
                                                                                                    </fo:inline>
                                                                                                    <fo:inline
                                                                                                          
                                                                                                            font-size="9pt"                                                                                                           
                                                                                                            text-align="center">
                                                                                                        <xsl:value-of
                                                                                                            select="shipment/shipmentDate" />
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                         </xsl:if>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell>
                                                                                                <fo:block>
                                                                                                    <fo:leader
                                                                                                            leader-length="100%"
                                                                                                            leader-pattern="space">
                                                                                                        &#x00A0;</fo:leader>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                             <xsl:if test="shipment/tackingNumber">
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                    font-weight="bold"
                                                                                                            font-size="9pt"
                                                                                                            text-align="center">
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'tracking.number')"/>:
                                                                                                    </fo:inline>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"  text-align="center">
                                                                                                        <xsl:value-of
                                                                                                                select="shipment/tackingNumber"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell>
                                                                                                <fo:block>
                                                                                                    <fo:leader
                                                                                                            leader-length="100%"
                                                                                                            leader-pattern="space">
                                                                                                        &#x00A0;</fo:leader>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>                                                                                        
                                                                                        </xsl:if>
                                                                                          <xsl:if test="shipment/carrier">
                                                                                        <fo:table-row>
                                                                                            <fo:table-cell padding-start="0.3cm">
                                                                                                <fo:block>
                                                                                                    <fo:inline
                                                                                                    font-weight="bold"
                                                                                                            font-size="9pt"
                                                                                                            text-align="center">
                                                                                                        <xsl:value-of
                                                                                                                select="java:getString($resources,'columnTitle.duePartsReceipt.carrier')"/>:
                                                                                                    </fo:inline>
                                                                                                    <fo:inline
                                                                                                            font-size="9pt"  text-align="center">
                                                                                                        <xsl:value-of
                                                                                                                select="shipment/carrier"/>
                                                                                                    </fo:inline>
                                                                                                </fo:block>
                                                                                            </fo:table-cell>
                                                                                        </fo:table-row>                                                                                        
                                                                                        </xsl:if>
                                                                               <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:leader leader-length="100%"
                                                                                           leader-pattern="space">
                                                                                    &#x00A0;</fo:leader>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                        
                                                                    </fo:table-row>
                                                                                                                                       
                                                                                        
                                                                                    </fo:table-body>
                                                                                </fo:table>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                           <fo:table-row>
                                                                        <fo:table-cell>
                                                                            <fo:block>
                                                                                <fo:leader leader-length="100%"
                                                                                           leader-pattern="space">
                                                                                    &#x00A0;</fo:leader>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                        
                                                                    </fo:table-row>       
                                                                </fo:table-body>
                                                            </fo:table>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-body>
                                        </fo:table>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>

                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

    <xsl:template match="shipmentTag">
        <fo:root>
            <xsl:call-template name="pageLayout"/>
            	
            <fo:page-sequence master-reference="page">
		
				<fo:static-content flow-name="xsl-region-after">
        		<fo:block font-size="9pt" >
        		<fo:inline padding-start="10cm">
          			<xsl:text>Page </xsl:text>
          			<fo:page-number/>   of   <fo:page-number-citation ref-id="TheVeryLastPage"/>
          			</fo:inline>
          			</fo:block>
                   </fo:static-content>
                <fo:flow flow-name="body">
                    <fo:table table-layout="fixed" text-align="start">
                        <fo:table-body>
                            <xsl:for-each select="partDetails/partDetail">
                                <xsl:call-template name="displayPartDetail"/>
                                <xsl:call-template name="displayWarrantySummery"/>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    <fo:block page-break-before="always">
                        <fo:table >
                            <fo:table-body >
                                <xsl:call-template name="displayShipmentTag"/>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:block><!-- List of parts block starts -->
                        <fo:table>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>
                                            <fo:leader leader-length="100%" leader-pattern="space"> &#x00A0;</fo:leader>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block text-align="center">
                                            <fo:inline font-size="10pt" font-weight="bold">
                                                <xsl:value-of
                                                        select="java:getString($resources,'label.partShipmentTag.listOfParts')"/>
                                            </fo:inline>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                               
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>
                                            <fo:table border-style="solid" border-width="0.02cm">
                                                 <fo:table-column column-width="26mm" />
                                                <fo:table-column column-width="28mm"/>
                                                <fo:table-column column-width="23mm"/>
                                                <fo:table-column column-width="30mm"/>
                                               <xsl:if test="$bu='AMER'"> 
                                                <fo:table-column column-width="22mm"/> 
                                                </xsl:if>
                                                <fo:table-column column-width="25mm"/>
                                                <fo:table-column column-width="15mm"/>                                                          
                                                <fo:table-column column-width="25mm"/>
                                   
                                               <fo:table-header>
                                                    <fo:table-cell border-before-style="solid"   padding-start="3mm"
                                                                   border-before-width="0.02cm"
                                                                   border-right-style="solid"
                                                                   border-right-width="0.02cm">
                                                        <fo:block font-family="Calibri" >
                                                            <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                <xsl:value-of
                                                                        select="java:getString($resources,'columnTitle.common.claimNo')"/>
                                                            </fo:inline>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell border-before-style="solid"   padding-start="3mm"
                                                                   border-before-width="0.02cm"
                                                                   border-right-style="solid"
                                                                   border-right-width="0.02cm">
                                                        <fo:block font-family="Calibri" >
                                                            <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                <xsl:value-of
                                                                        select="java:getString($resources,'label.partShipmentTag.modelNo')"/>
                                                            </fo:inline>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell border-before-style="solid"       padding-start="3mm"
                                                                   border-before-width="0.02cm"
                                                                   border-right-style="solid"
                                                                   border-right-width="0.02cm">
                                                        <fo:block font-family="Calibri" >
                                                            <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                <xsl:value-of
                                                                        select="java:getString($resources,'columnTitle.common.serialNo')"/>
                                                            </fo:inline>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell border-before-style="solid"     padding-start="3mm"
                                                                   border-before-width="0.02cm"
                                                                   border-right-style="solid"
                                                                   border-right-width="0.02cm">
                                                        <fo:block font-family="Calibri" >
                                                            <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                <xsl:value-of
                                                                        select="java:getString($resources,'label.common.partNumber')"/>
                                                            </fo:inline>
                                                        </fo:block>
                                                          </fo:table-cell>
                                                     
                                                     <fo:table-cell border-before-style="solid"     padding-start="3mm"
                                                                   border-before-width="0.02cm"
                                                                   border-right-style="solid"
                                                                   border-right-width="0.02cm">
                                                        <fo:block font-family="Calibri" >
                                                            <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                <xsl:value-of
                                                                        select="java:getString($resources,'label.partShipmentTag.vendorPartNumber')"/>
                                                            </fo:inline>
                                                        </fo:block>
                                                    </fo:table-cell>  
                                                    
                                                    <fo:table-cell border-before-style="solid"    padding-start="3mm"
                                                                   border-before-width="0.02cm"
                                                                   border-right-style="solid"
                                                                   border-right-width="0.02cm">
                                                        <fo:block font-family="Calibri" >
                                                            <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                <xsl:value-of
                                                                        select="java:getString($resources,'label.common.description')"/>
                                                            </fo:inline>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell border-before-style="solid"      padding-start="3mm"
                                                                   border-before-width="0.02cm"
                                                                   border-right-style="solid"
                                                                   border-right-width="0.02cm">
                                                        <fo:block font-family="Calibri" >
                                                            <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                <xsl:value-of
                                                                        select="java:getString($resources,'columnTitle.common.quantity')"/>
                                                            </fo:inline>
                                                        </fo:block>
                                                    </fo:table-cell>
                                                   
                                                    <fo:table-cell border-before-style="solid"      padding-start="3mm"
                                                                   border-before-width="0.02cm"
                                                                   border-right-style="solid"
                                                                   border-right-width="0.02cm">
                                                        <fo:block font-family="Calibri" >
                                                            <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                <xsl:value-of
                                                                        select="java:getString($resources,'label.partReturnConfiguration.rmaNumber')"/>
                                                            </fo:inline>
                                                        </fo:block>
                                                    </fo:table-cell>  
                                                                                           
                                                </fo:table-header>
                                                <fo:table-body>
                                                    <xsl:for-each select="partDetails/partDetail">
                                                        <fo:table-row>
                                                            <fo:table-cell border-before-style="solid"     padding-start="3mm"
                                                                           border-before-width="0.02cm"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.02cm">
                                                                <fo:block font-family="Calibri" >
                                                                    <fo:inline font-size="8pt" font-weight="bold" line-height="12pt" >
                                                                        <xsl:value-of select="claim/claimNumber"/>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell border-before-style="solid"         padding-start="3mm"
                                                                           border-before-width="0.02cm"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.02cm">
                                                                <fo:block font-family="Calibri" >
                                                                    <fo:inline font-size="8pt" line-height="12pt" >
                                                                        <xsl:value-of select="claim/model"/>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell border-before-style="solid"           padding-start="3mm"
                                                                           border-before-width="0.02cm"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.02cm">
                                                                <fo:block font-family="Calibri">
                                                                    <fo:inline font-size="8pt" line-height="12pt" >
                                                                        <xsl:value-of
                                                                                select="claim/machineSerialNumber"/>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell border-before-style="solid"            padding-start="3mm"
                                                                           border-before-width="0.02cm"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.02cm">
                                                                <fo:block font-family="Calibri">
                                                                    <fo:inline font-size="8pt" line-height="12pt" >
                                                                        <xsl:value-of select="part/partNumber"/>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                              <xsl:if test="$bu='AMER'"> 
                                                            <fo:table-cell border-before-style="solid"            padding-start="3mm"
                                                                           border-before-width="0.02cm"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.02cm">
                                                                <fo:block font-family="Calibri">
                                                                    <fo:inline font-size="8pt" line-height="12pt" >
                                                                        <xsl:value-of select="part/vendorPartNumber"/>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>                                                                       
                                                              </xsl:if>
                                                            <fo:table-cell border-before-style="solid"           padding-start="3mm"
                                                                           border-before-width="0.02cm"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.02cm">
                                                                <fo:block font-family="Calibri">
                                                                    <fo:inline font-size="8pt" line-height="12pt" >
                                                                        <xsl:value-of select="part/description"/>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell border-before-style="solid"           padding-start="3mm"
                                                                           border-before-width="0.02cm"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.02cm">
                                                                <fo:block font-family="Calibri">
                                                                    <fo:inline font-size="8pt" line-height="12pt" >
                                                                        <xsl:value-of select="part/numberOfParts"/>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                    
                                                            <fo:table-cell border-before-style="solid"               padding-start="3mm"
                                                                           border-before-width="0.02cm"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.02cm">
                                                                <fo:block font-family="Calibri">
                                                                    <fo:inline font-size="8pt" line-height="12pt" >
                                                                        <xsl:value-of select="part/rmaNumber"/>
                                                                    </fo:inline>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                   
                                                    </fo:table-row>
                                                    </xsl:for-each>
                                                </fo:table-body>
                                            </fo:table>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                       
                    </fo:block>
                      <fo:block id="TheVeryLastPage"> </fo:block>
                </fo:flow>
            </fo:page-sequence>

              </fo:root>
    </xsl:template>

    <xsl:template match="shipment/barcode">
        <xsl:variable name="message" select="."/>
        <fo:instream-foreign-object>
            <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
                             message="{$message}">
                <barcode:code128>
                    <barcode:height>15mm</barcode:height>
                </barcode:code128>
            </barcode:barcode>
        </fo:instream-foreign-object>
    </xsl:template>
    
</xsl:transform>