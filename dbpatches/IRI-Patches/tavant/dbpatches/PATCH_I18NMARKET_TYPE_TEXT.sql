--Purpose    : Static Internationalization of the MARKET_TYPE table
--Author     : shraddha.nanda
--Created On : 22-Aug-08
CREATE TABLE I18NMARKET_TYPE_TEXT 
(
  ID                   NUMBER(19)               NOT NULL,
  LOCALE               VARCHAR2(255 CHAR),
  TITLE		       VARCHAR2(255 CHAR),
  I18N_MARKET_TYPE          NUMBER(19)               NOT NULL  
)
/
ALTER TABLE 	I18NMARKET_TYPE_TEXT ADD CONSTRAINT  	I18NMARKET_TYPE_TEXT_PK	PRIMARY KEY( 

	ID	)
/
ALTER TABLE I18NMARKET_TYPE_TEXT ADD (
 CONSTRAINT I18NMARKET_TYPE_FK 
 FOREIGN KEY (I18N_MARKET_TYPE) 
 REFERENCES MARKET_TYPE(ID)
 )

/
CREATE SEQUENCE I18N_Market_Type_Text_Seq
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES 
(I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Agriculture, Forestry, Fishing and Hunting',1)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES 
(I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Agriculture, Forestry, Fishing and Hunting',1)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES 
(I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Agriculture, Forestry, Fishing and Hunting',1)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Agriculture, Forestry, Fishing and Hunting',1)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Mining',2) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Mining',2)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Mining',2)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Mining',2)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Utilities',3) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Utilities',3)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Utilities',3)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Utilities',3)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Construction',4) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Construction',4)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Construction',4)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Construction',4)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Wholesale Trade',5) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Wholesale Trade',5)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Wholesale Trade',5)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Wholesale Trade',5)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Information',6)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Information',6)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Information',6)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Information',6)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Finance and Insurance',7) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Finance and Insurance',7)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Finance and Insurance',7)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Finance and Insurance',7)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Real Estate and Rental and Leasing',8) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Real Estate and Rental and Leasing',8)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Real Estate and Rental and Leasing',8)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Real Estate and Rental and Leasing',8)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Professional, Scientific, and Technical Services',9) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Professional, Scientific, and Technical Services',9)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Professional, Scientific, and Technical Services',9)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Professional, Scientific, and Technical Services',9)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Management of Companies and Enterprises',10) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Management of Companies and Enterprises',10)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Management of Companies and Enterprises',10)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Management of Companies and Enterprises',10)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Administrative and Support and Waste Management and Remediation Services',11) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Administrative and Support and Waste Management and Remediation Services',11)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Administrative and Support and Waste Management and Remediation Services',11)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Administrative and Support and Waste Management and Remediation Services',11)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Educational Services',12) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Educational Services',12)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Educational Services',12)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Educational Services',12)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Health Care and Social Assistance',13) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Health Care and Social Assistance',13)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Health Care and Social Assistance',13)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Health Care and Social Assistance',13)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Arts, Entertainment, and Recreation',14) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Arts, Entertainment, and Recreation',14)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Arts, Entertainment, and Recreation',14)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Arts, Entertainment, and Recreation',14)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Accommodation and Food Services',15) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Accommodation and Food Services',15)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Accommodation and Food Services',15)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Accommodation and Food Services',15)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Other Services (except Public Administration)',16) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Other Services (except Public Administration)',16)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Other Services (except Public Administration)',16)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Other Services (except Public Administration)',16)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Public Administration',17) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Public Administration',17)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Public Administration',17)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Public Administration',17)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Manufacturing',18) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Manufacturing',18)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Manufacturing',18)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Manufacturing',18)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Retail Trade',19) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Retail Trade',19)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Retail Trade',19)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Retail Trade',19)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_US','Transportation and Warehousing',20) 
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'en_UK','Transportation and Warehousing',20)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'fr_FR','Transportation and Warehousing',20)
/
INSERT INTO I18NMARKET_TYPE_TEXT VALUES (I18N_Market_Type_Text_Seq.NEXTVAL,'de_DE','Transportation and Warehousing',20)
/
COMMIT
/
  
  