-- MySQL dump 10.10
--
-- Host: localhost    Database: warranty
-- ------------------------------------------------------
-- Server version	5.0.16-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `warranty`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `warranty` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `warranty`;

--
-- Table structure for table `acceptance_reason`
--

DROP TABLE IF EXISTS `acceptance_reason`;
CREATE TABLE `acceptance_reason` (
  `code` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `state` varchar(255) default NULL,
  PRIMARY KEY  (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `acceptance_reason`
--


/*!40000 ALTER TABLE `acceptance_reason` DISABLE KEYS */;
LOCK TABLES `acceptance_reason` WRITE;
INSERT INTO `acceptance_reason` VALUES ('R1','Special Warr Consid Settled in Full','active'),('R10','Claim Automatically Settled Partially','active'),('R11','Appeal - Settled in Full','active'),('R12','Appeal - Settled Partially','active'),('R2','Special Warr Consid Settled Partially','active'),('R3','Serviceman Error - Settled in Full','active'),('R4','Serviceman Error - Settled Partially','active'),('R5','Policy/Goodwill - Settled in Full','active'),('R6','Policy/Goodwill - Settled Partially','active'),('R7','Claim Settled in Full','active'),('R8','Claim Settled Partially','active'),('R9','Claim Automatically Settled in Full','active');
UNLOCK TABLES;
/*!40000 ALTER TABLE `acceptance_reason` ENABLE KEYS */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL auto_increment,
  `address_line1` varchar(255) default NULL,
  `address_line2` varchar(255) default NULL,
  `city` varchar(255) default NULL,
  `contact_person_name` varchar(255) default NULL,
  `country` varchar(255) default NULL,
  `email` varchar(255) default NULL,
  `phone` varchar(255) default NULL,
  `secondary_email` varchar(255) default NULL,
  `secondary_phone` varchar(255) default NULL,
  `state` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `zip_code` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `address`
--


/*!40000 ALTER TABLE `address` DISABLE KEYS */;
LOCK TABLES `address` WRITE;
INSERT INTO `address` VALUES (1,'2302 CREEKVIEW DRIVE, #1',NULL,'ANKENY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12345'),(2,'632 WEST AVENUE',NULL,'MILFORD',NULL,'USA',NULL,NULL,NULL,NULL,'MA',NULL,'12345'),(3,'4399 CLARY BLVD',NULL,'KANSAS CITY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12346'),(4,'4390 COMMERCE DRIVE',NULL,'WHITEHALL',NULL,'USA',NULL,NULL,NULL,NULL,'MN',NULL,'12345'),(5,'4399 CLARY BLVD',NULL,'KANSAS CITY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12346'),(6,'632 WEST AVENUE',NULL,'MILFORD',NULL,'USA',NULL,NULL,NULL,NULL,'CT',NULL,'12345'),(7,'123 Harbor Boulevard',NULL,'Orange County',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12345'),(8,'632 WEST AVENUE',NULL,'MILFORD',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12345'),(9,'4399 CLARY BLVD',NULL,'KANSAS CITY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12346'),(10,'94 Enzo Blvd',NULL,'Maranello',NULL,'France',NULL,NULL,NULL,NULL,'Toulouse',NULL,'43455'),(11,'94 Enzo Blvd',NULL,'Maranello',NULL,'France',NULL,NULL,NULL,NULL,'Paris',NULL,'43455'),(12,'94 Enzo Blvd',NULL,'Maranello',NULL,'UK',NULL,NULL,NULL,NULL,'London',NULL,'43455'),(13,'94 Enzo Blvd',NULL,'Maranello',NULL,'UK',NULL,NULL,NULL,NULL,'Southampton',NULL,'43455'),(14,'94 Enzo Blvd',NULL,'Maranello',NULL,'Italy',NULL,NULL,NULL,NULL,'Milan',NULL,'43455'),(15,'94 Enzo Blvd',NULL,'Maranello',NULL,'Italy',NULL,NULL,NULL,NULL,'Venice',NULL,'43455'),(16,'94 Enzo Blvd',NULL,'Maranello',NULL,'Italy',NULL,NULL,NULL,NULL,'Vatican',NULL,'43455'),(17,'4399 CLARY BLVD',NULL,'KANSAS CITY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12346'),(18,'4399 CLARY BLVD',NULL,'KANSAS CITY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12346'),(19,'4399 CLARY BLVD',NULL,'KANSAS CITY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12346'),(20,'4399 CLARY BLVD',NULL,'KANSAS CITY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12346'),(21,'4399 CLARY BLVD',NULL,'KANSAS CITY',NULL,'USA',NULL,NULL,NULL,NULL,'KN',NULL,'12346');
UNLOCK TABLES;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;

--
-- Table structure for table `applicable_policy`
--

DROP TABLE IF EXISTS `applicable_policy`;
CREATE TABLE `applicable_policy` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date default NULL,
  `till_date` date default NULL,
  `registered_policy` bigint(20) default NULL,
  `policy_definition` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK85868DD2B22FB4EA` (`registered_policy`),
  KEY `FK85868DD2183553AC` (`policy_definition`),
  CONSTRAINT `FK85868DD2183553AC` FOREIGN KEY (`policy_definition`) REFERENCES `policy_definition` (`id`),
  CONSTRAINT `FK85868DD2B22FB4EA` FOREIGN KEY (`registered_policy`) REFERENCES `policy` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `applicable_policy`
--


/*!40000 ALTER TABLE `applicable_policy` DISABLE KEYS */;
LOCK TABLES `applicable_policy` WRITE;
INSERT INTO `applicable_policy` VALUES (1,NULL,NULL,NULL,114450),(2,NULL,NULL,NULL,114600);
UNLOCK TABLES;
/*!40000 ALTER TABLE `applicable_policy` ENABLE KEYS */;

--
-- Table structure for table `assemblies_included`
--

DROP TABLE IF EXISTS `assemblies_included`;
CREATE TABLE `assemblies_included` (
  `assembly_included` bigint(20) NOT NULL,
  `in_policy` bigint(20) NOT NULL,
  PRIMARY KEY  (`assembly_included`,`in_policy`),
  KEY `FK7C68A997BFCAB821` (`assembly_included`),
  KEY `FK7C68A997128E0DB6` (`in_policy`),
  CONSTRAINT `FK7C68A997128E0DB6` FOREIGN KEY (`in_policy`) REFERENCES `assembly` (`id`),
  CONSTRAINT `FK7C68A997BFCAB821` FOREIGN KEY (`assembly_included`) REFERENCES `policy_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `assemblies_included`
--


/*!40000 ALTER TABLE `assemblies_included` DISABLE KEYS */;
LOCK TABLES `assemblies_included` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `assemblies_included` ENABLE KEYS */;

--
-- Table structure for table `assembly`
--

DROP TABLE IF EXISTS `assembly`;
CREATE TABLE `assembly` (
  `id` bigint(20) NOT NULL auto_increment,
  `tread_able` bit(1) default NULL,
  `assembly_definition` bigint(20) default NULL,
  `is_part_of_assembly` bigint(20) default NULL,
  `fault_code` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKE9BE3DE6356C269` (`assembly_definition`),
  KEY `FKE9BE3DE6A011755` (`fault_code`),
  KEY `FKE9BE3DE6E2E51BA1` (`is_part_of_assembly`),
  CONSTRAINT `FKE9BE3DE6E2E51BA1` FOREIGN KEY (`is_part_of_assembly`) REFERENCES `assembly` (`id`),
  CONSTRAINT `FKE9BE3DE6356C269` FOREIGN KEY (`assembly_definition`) REFERENCES `assembly_definition` (`id`),
  CONSTRAINT `FKE9BE3DE6A011755` FOREIGN KEY (`fault_code`) REFERENCES `fault_code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `assembly`
--


/*!40000 ALTER TABLE `assembly` DISABLE KEYS */;
LOCK TABLES `assembly` WRITE;
INSERT INTO `assembly` VALUES (1,'\0',14,NULL,5),(2,'',34,1,6),(3,'\0',4,NULL,NULL),(4,'\0',36,3,4),(5,'\0',3,NULL,NULL),(6,'\0',30,5,1),(7,'\0',38,5,NULL),(8,'\0',57,7,2),(9,'\0',11,NULL,3),(10,'\0',14,NULL,11),(11,'',34,10,12),(12,'\0',4,NULL,NULL),(13,'\0',36,12,10),(14,'\0',3,NULL,NULL),(15,'\0',30,14,7),(16,'\0',38,14,NULL),(17,'\0',57,16,8),(18,'\0',11,NULL,9);
UNLOCK TABLES;
/*!40000 ALTER TABLE `assembly` ENABLE KEYS */;

--
-- Table structure for table `assembly_definition`
--

DROP TABLE IF EXISTS `assembly_definition`;
CREATE TABLE `assembly_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `level` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK75EBC14C4F6FB4FE` (`level`),
  CONSTRAINT `FK75EBC14C4F6FB4FE` FOREIGN KEY (`level`) REFERENCES `assembly_level` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `assembly_definition`
--


/*!40000 ALTER TABLE `assembly_definition` DISABLE KEYS */;
LOCK TABLES `assembly_definition` WRITE;
INSERT INTO `assembly_definition` VALUES (1,'AA','CONTROL PANEL',1),(2,'AB','AIR SYSTEM',1),(3,'AC','BASE PLATE',1),(4,'AD','CMC',1),(5,'AE','COOLER',1),(6,'AF','DRIVE COMPONENTS',1),(7,'AG','MAIN MOTOR',1),(8,'AH','MOTOR',1),(9,'AI','PIPING',1),(10,'AJ','REGULATION',1),(11,'AK','DRIVE END',1),(12,'AL','TANKS',1),(13,'AM','ADMINISTRATIVE',1),(14,'AN','ADDITIONAL REAR',1),(15,'AO','AUGER',1),(16,'AP','ELECTRICAL',1),(17,'AQ','AIREND',1),(18,'AR','COMBUSTOR',1),(19,'AS','DRYER',1),(20,'AT','COOLING',1),(21,'AU','HUMIDIFICATION',1),(22,'AV','MISCELLANEOUS',1),(23,'100','ELECTRICAL',2),(24,'101','NO LOWER LEVEL',2),(25,'102','MECHANICAL LINKAGE',2),(26,'103','PUMP',2),(27,'104','VALVE',2),(28,'105','FILTER',2),(29,'106','GAUGE',2),(30,'107','BEARINGS',2),(31,'108','DRAIN VALVES',2),(32,'109','MEMBRANE',2),(33,'110','NO LOWER LEVEL',2),(34,'111','AIREND FAILURE',2),(35,'112','NO LOWER LEVEL',2),(36,'113','ALIGN BELTS AND/OR SHEAVE (PULLEY)',2),(37,'114','BEARINGS',2),(38,'115','CONNECTING ROD',2),(39,'116','CRANK SHAFT',2),(40,'117','CROSSHEAD',2),(41,'118','DRIVE',2),(42,'119','SCRAPER RINGS',2),(43,'120','FLYWHEEL',2),(44,'121','GEARS/BELTS/IDLERS/GAS SPRINGS',2),(45,'122','COUPLING DRIVE',2),(46,'123','HALL EFFECT SENSOR',2),(47,'124','STATOR',2),(48,'125','CONTACTORS',2),(49,'126','HOSES',2),(50,'100','3/6 WAY',3),(51,'101','A-ARM ASSY',3),(52,'102','POWER TURBINE',3),(53,'103','GASIFIER',3),(54,'104','MANIFOLD',3),(55,'105','CONTROL VALVE',3),(56,'106','900 LBS',3),(57,'107','ACCELERATOR CABLE',3),(58,'108','AUTO VOLTAGE REGULATOR',3),(59,'109','DISPLAY',3),(60,'110','CONTROLLER',3),(61,'111','ELEMENT',3),(62,'112','DP INDICATOR',3),(63,'113','REFRIGERANT',3),(64,'114','PUMP',3),(65,'115','FAN',3),(66,'116','TANK',3),(67,'117','CONDENSOR',3),(68,'100','BICS BLOCK',4),(69,'101','ACCUMULATOR',4),(70,'102','CAP',4),(71,'103','BELT',4),(72,'104','CONDITION INDICATOR',4),(73,'105','RESISTOR',4),(74,'106','LIQUID DRIER/SIGHTGLASS',4),(75,'107','BLADE',4),(76,'108','GASKET',4),(77,'109','WATER COOLED',4),(78,'110','EMS',4),(79,'111','HUMIDISTAT',4),(80,'112','GASKET',4),(81,'113','SUB',4),(82,'114','PURGE',4),(83,'115','GASKET',4),(84,'116','PRESSURE/DP GAUGE',4),(85,'117','TEMPERATURE GAUGE',4),(86,'118','VACUUM GAUGE',4),(87,'119','TEMPERATURE SWITCH',4);
UNLOCK TABLES;
/*!40000 ALTER TABLE `assembly_definition` ENABLE KEYS */;

--
-- Table structure for table `assembly_level`
--

DROP TABLE IF EXISTS `assembly_level`;
CREATE TABLE `assembly_level` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` varchar(255) default NULL,
  `generator_type` int(11) default NULL,
  `level` int(11) NOT NULL,
  `name` varchar(255) default NULL,
  `next_code_value` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `assembly_level`
--


/*!40000 ALTER TABLE `assembly_level` DISABLE KEYS */;
LOCK TABLES `assembly_level` WRITE;
INSERT INTO `assembly_level` VALUES (1,'Product system',0,1,'PRODUCT-SYSTEM','AV'),(2,'Product subsystem',1,2,'PRODUCT-SUB-SYSTEM','126'),(3,'Prodcut component',1,3,'PRODUCT-COMPONENT','117'),(4,'Product subcomponent',1,4,'PRODUCT-SUB-COMPONENT','119');
UNLOCK TABLES;
/*!40000 ALTER TABLE `assembly_level` ENABLE KEYS */;

--
-- Table structure for table `assignment_rule_action`
--

DROP TABLE IF EXISTS `assignment_rule_action`;
CREATE TABLE `assignment_rule_action` (
  `id` bigint(20) NOT NULL,
  `user_cluster` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKCA86A671FC3FBA1` (`user_cluster`),
  KEY `FKCA86A67149A447D` (`id`),
  CONSTRAINT `FKCA86A67149A447D` FOREIGN KEY (`id`) REFERENCES `domain_rule_action` (`id`),
  CONSTRAINT `FKCA86A671FC3FBA1` FOREIGN KEY (`user_cluster`) REFERENCES `user_cluster` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `assignment_rule_action`
--


/*!40000 ALTER TABLE `assignment_rule_action` DISABLE KEYS */;
LOCK TABLES `assignment_rule_action` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `assignment_rule_action` ENABLE KEYS */;

--
-- Table structure for table `attribute`
--

DROP TABLE IF EXISTS `attribute`;
CREATE TABLE `attribute` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `value` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `attribute`
--


/*!40000 ALTER TABLE `attribute` DISABLE KEYS */;
LOCK TABLES `attribute` WRITE;
INSERT INTO `attribute` VALUES (1,'Language','English'),(2,'Language','German'),(3,'Language','Spanish'),(4,'Language','French'),(5,'Product Type','Excavator'),(6,'Country','USA'),(7,'Claim Type','Machine'),(8,'Available','false'),(9,'Default','true'),(10,'Group Type','processor'),(11,'Group Type','dsm'),(12,'Claim Type','Parts');
UNLOCK TABLES;
/*!40000 ALTER TABLE `attribute` ENABLE KEYS */;

--
-- Table structure for table `base_part_return`
--

DROP TABLE IF EXISTS `base_part_return`;
CREATE TABLE `base_part_return` (
  `id` bigint(20) NOT NULL auto_increment,
  `due_date` date default NULL,
  `status` varchar(255) default NULL,
  `returned_by` bigint(20) default NULL,
  `oem_part_replaced` bigint(20) default NULL,
  `return_location` bigint(20) default NULL,
  `payment_condition` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK75BC28E8AF31DCD` (`payment_condition`),
  KEY `FK75BC28E49D794B0` (`returned_by`),
  KEY `FK75BC28E819AF18F` (`return_location`),
  KEY `FK75BC28EF24C52E7` (`oem_part_replaced`),
  CONSTRAINT `FK75BC28EF24C52E7` FOREIGN KEY (`oem_part_replaced`) REFERENCES `oem_part_replaced` (`id`),
  CONSTRAINT `FK75BC28E49D794B0` FOREIGN KEY (`returned_by`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FK75BC28E819AF18F` FOREIGN KEY (`return_location`) REFERENCES `location` (`id`),
  CONSTRAINT `FK75BC28E8AF31DCD` FOREIGN KEY (`payment_condition`) REFERENCES `payment_condition` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `base_part_return`
--


/*!40000 ALTER TABLE `base_part_return` DISABLE KEYS */;
LOCK TABLES `base_part_return` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `base_part_return` ENABLE KEYS */;

--
-- Table structure for table `base_price_value`
--

DROP TABLE IF EXISTS `base_price_value`;
CREATE TABLE `base_price_value` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date NOT NULL,
  `till_date` date NOT NULL,
  `price` decimal(19,2) NOT NULL,
  `parent` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK41C07E8D98E57505` (`parent`),
  CONSTRAINT `FK41C07E8D98E57505` FOREIGN KEY (`parent`) REFERENCES `item_base_price` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `base_price_value`
--


/*!40000 ALTER TABLE `base_price_value` DISABLE KEYS */;
LOCK TABLES `base_price_value` WRITE;
INSERT INTO `base_price_value` VALUES (1,'2000-01-01','2010-12-31','1.00',1),(2,'2000-01-01','2010-12-31','2.00',2),(3,'2000-01-01','2010-12-31','3.00',3),(4,'2000-01-01','2010-12-31','4.00',4),(5,'2000-01-01','2010-12-31','5.00',5),(6,'2000-01-01','2010-12-31','6.00',6),(7,'2000-01-01','2010-12-31','7.00',7),(8,'2000-01-01','2010-12-31','8.00',8),(9,'2000-01-01','2010-12-31','9.00',9),(10,'2000-01-01','2010-12-31','10.00',10),(11,'2000-01-01','2010-12-31','11.00',11),(12,'2000-01-01','2010-12-31','12.00',12),(13,'2000-01-01','2010-12-31','13.00',13),(14,'2000-01-01','2010-12-31','14.00',14),(15,'2000-01-01','2010-12-31','15.00',15),(16,'2000-01-01','2010-12-31','16.00',16),(17,'2000-01-01','2010-12-31','17.00',17),(18,'2000-01-01','2010-12-31','18.00',18),(19,'2000-01-01','2010-12-31','19.00',19),(20,'2000-01-01','2010-12-31','20.00',20),(21,'2000-01-01','2010-12-31','21.00',21),(22,'2000-01-01','2010-12-31','22.00',22),(23,'2000-01-01','2010-12-31','23.00',23),(24,'2000-01-01','2010-12-31','24.00',24),(25,'2000-01-01','2010-12-31','25.00',25),(26,'2000-01-01','2010-12-31','26.00',26),(27,'2000-01-01','2010-12-31','27.00',27),(28,'2000-01-01','2010-12-31','28.00',28),(29,'2000-01-01','2010-12-31','29.00',29),(30,'2000-01-01','2010-12-31','30.00',30),(31,'2000-01-01','2010-12-31','31.00',31),(32,'2000-01-01','2010-12-31','32.00',32),(33,'2000-01-01','2010-12-31','33.00',33),(34,'2000-01-01','2010-12-31','34.00',34),(35,'2000-01-01','2010-12-31','35.00',35),(36,'2000-01-01','2010-12-31','36.00',36),(37,'2000-01-01','2010-12-31','37.00',37),(38,'2000-01-01','2010-12-31','38.00',38),(39,'2000-01-01','2010-12-31','39.00',39);
UNLOCK TABLES;
/*!40000 ALTER TABLE `base_price_value` ENABLE KEYS */;

--
-- Table structure for table `campaign`
--

DROP TABLE IF EXISTS `campaign`;
CREATE TABLE `campaign` (
  `id` bigint(20) NOT NULL auto_increment,
  `build_from_date` date default NULL,
  `build_till_date` date default NULL,
  `code` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `from_date` date default NULL,
  `notifiy_dealer_by_email` bit(1) NOT NULL,
  `notify_customer` bit(1) NOT NULL,
  `notify_dealer` bit(1) NOT NULL,
  `till_date` date default NULL,
  `campaign_coverage` bigint(20) default NULL,
  `campaign_class` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKF7A90110C2496178` (`campaign_coverage`),
  KEY `FKF7A901101516FE48` (`campaign_class`),
  CONSTRAINT `FKF7A901101516FE48` FOREIGN KEY (`campaign_class`) REFERENCES `campaign_class` (`code`),
  CONSTRAINT `FKF7A90110C2496178` FOREIGN KEY (`campaign_coverage`) REFERENCES `campaign_coverage` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign`
--


/*!40000 ALTER TABLE `campaign` DISABLE KEYS */;
LOCK TABLES `campaign` WRITE;
INSERT INTO `campaign` VALUES (1,NULL,NULL,'CC001','Description ','2010-05-01','','','\0','2010-05-10',1,'Product'),(2,NULL,NULL,'CC002','Description ','2010-05-01','','','\0','2010-05-10',2,'Security'),(3,'2005-03-01','2005-03-10','CC003','Description ','2010-05-01','','\0','\0','2010-05-10',3,'Product'),(4,'2005-03-10','2005-03-20','CC004','Description ','2010-05-01','','\0','\0','2010-05-10',4,'Security'),(5,NULL,NULL,'CC005','Description ','2010-05-01','\0','\0','\0','2010-05-10',5,'Product'),(6,NULL,NULL,'CC006','Description ','2010-05-01','\0','\0','\0','2010-05-10',6,'Security');
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign` ENABLE KEYS */;

--
-- Table structure for table `campaign_class`
--

DROP TABLE IF EXISTS `campaign_class`;
CREATE TABLE `campaign_class` (
  `code` varchar(255) NOT NULL,
  `name` varchar(200) default NULL,
  PRIMARY KEY  (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_class`
--


/*!40000 ALTER TABLE `campaign_class` DISABLE KEYS */;
LOCK TABLES `campaign_class` WRITE;
INSERT INTO `campaign_class` VALUES ('Product','Product'),('Security','Security');
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_class` ENABLE KEYS */;

--
-- Table structure for table `campaign_coverage`
--

DROP TABLE IF EXISTS `campaign_coverage`;
CREATE TABLE `campaign_coverage` (
  `id` bigint(20) NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_coverage`
--


/*!40000 ALTER TABLE `campaign_coverage` DISABLE KEYS */;
LOCK TABLES `campaign_coverage` WRITE;
INSERT INTO `campaign_coverage` VALUES (1),(2),(3),(4),(5),(6);
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_coverage` ENABLE KEYS */;

--
-- Table structure for table `campaign_coverage_items`
--

DROP TABLE IF EXISTS `campaign_coverage_items`;
CREATE TABLE `campaign_coverage_items` (
  `campaign_coverage` bigint(20) NOT NULL,
  `items` bigint(20) NOT NULL,
  KEY `FKFB7BDDB8C2496178` (`campaign_coverage`),
  KEY `FKFB7BDDB8924068C8` (`items`),
  CONSTRAINT `FKFB7BDDB8924068C8` FOREIGN KEY (`items`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FKFB7BDDB8C2496178` FOREIGN KEY (`campaign_coverage`) REFERENCES `campaign_coverage` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_coverage_items`
--


/*!40000 ALTER TABLE `campaign_coverage_items` DISABLE KEYS */;
LOCK TABLES `campaign_coverage_items` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_coverage_items` ENABLE KEYS */;

--
-- Table structure for table `campaign_labor_detail`
--

DROP TABLE IF EXISTS `campaign_labor_detail`;
CREATE TABLE `campaign_labor_detail` (
  `id` bigint(20) NOT NULL auto_increment,
  `hours_spent` int(11) default NULL,
  `use_labor_standards` bit(1) NOT NULL,
  `service_procedure` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKCBFA20F303197AB` (`service_procedure`),
  CONSTRAINT `FKCBFA20F303197AB` FOREIGN KEY (`service_procedure`) REFERENCES `service_procedure` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_labor_detail`
--


/*!40000 ALTER TABLE `campaign_labor_detail` DISABLE KEYS */;
LOCK TABLES `campaign_labor_detail` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_labor_detail` ENABLE KEYS */;

--
-- Table structure for table `campaign_nonoemparts_to_replace`
--

DROP TABLE IF EXISTS `campaign_nonoemparts_to_replace`;
CREATE TABLE `campaign_nonoemparts_to_replace` (
  `campaign` bigint(20) NOT NULL,
  `nonoemparts_to_replace` bigint(20) NOT NULL,
  UNIQUE KEY `nonoemparts_to_replace` (`nonoemparts_to_replace`),
  KEY `FK84DDF7285B5DF949` (`campaign`),
  KEY `FK84DDF72860EC5A2C` (`nonoemparts_to_replace`),
  CONSTRAINT `FK84DDF72860EC5A2C` FOREIGN KEY (`nonoemparts_to_replace`) REFERENCES `non_oem_part_to_replace` (`id`),
  CONSTRAINT `FK84DDF7285B5DF949` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_nonoemparts_to_replace`
--


/*!40000 ALTER TABLE `campaign_nonoemparts_to_replace` DISABLE KEYS */;
LOCK TABLES `campaign_nonoemparts_to_replace` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_nonoemparts_to_replace` ENABLE KEYS */;

--
-- Table structure for table `campaign_notification`
--

DROP TABLE IF EXISTS `campaign_notification`;
CREATE TABLE `campaign_notification` (
  `id` bigint(20) NOT NULL auto_increment,
  `campaign` bigint(20) default NULL,
  `item` bigint(20) default NULL,
  `dealership` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK24B72BBAEE19ABBE` (`dealership`),
  KEY `FK24B72BBA5B5DF949` (`campaign`),
  KEY `FK24B72BBA8C73FC1B` (`item`),
  CONSTRAINT `FK24B72BBA8C73FC1B` FOREIGN KEY (`item`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FK24B72BBA5B5DF949` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `FK24B72BBAEE19ABBE` FOREIGN KEY (`dealership`) REFERENCES `dealership` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_notification`
--


/*!40000 ALTER TABLE `campaign_notification` DISABLE KEYS */;
LOCK TABLES `campaign_notification` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_notification` ENABLE KEYS */;

--
-- Table structure for table `campaign_oem_parts_to_replace`
--

DROP TABLE IF EXISTS `campaign_oem_parts_to_replace`;
CREATE TABLE `campaign_oem_parts_to_replace` (
  `campaign` bigint(20) NOT NULL,
  `oem_parts_to_replace` bigint(20) NOT NULL,
  UNIQUE KEY `oem_parts_to_replace` (`oem_parts_to_replace`),
  KEY `FK4DDDFD66CC2D8CAF` (`oem_parts_to_replace`),
  KEY `FK4DDDFD665B5DF949` (`campaign`),
  CONSTRAINT `FK4DDDFD665B5DF949` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`),
  CONSTRAINT `FK4DDDFD66CC2D8CAF` FOREIGN KEY (`oem_parts_to_replace`) REFERENCES `oem_part_to_replace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_oem_parts_to_replace`
--


/*!40000 ALTER TABLE `campaign_oem_parts_to_replace` DISABLE KEYS */;
LOCK TABLES `campaign_oem_parts_to_replace` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_oem_parts_to_replace` ENABLE KEYS */;

--
-- Table structure for table `campaign_range_coverage`
--

DROP TABLE IF EXISTS `campaign_range_coverage`;
CREATE TABLE `campaign_range_coverage` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK20C8ADB9AD5C965C` (`id`),
  CONSTRAINT `FK20C8ADB9AD5C965C` FOREIGN KEY (`id`) REFERENCES `campaign_coverage` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_range_coverage`
--


/*!40000 ALTER TABLE `campaign_range_coverage` DISABLE KEYS */;
LOCK TABLES `campaign_range_coverage` WRITE;
INSERT INTO `campaign_range_coverage` VALUES (1),(2),(3),(4),(5),(6);
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_range_coverage` ENABLE KEYS */;

--
-- Table structure for table `campaign_range_coverage_ranges`
--

DROP TABLE IF EXISTS `campaign_range_coverage_ranges`;
CREATE TABLE `campaign_range_coverage_ranges` (
  `campaign_range_coverage` bigint(20) NOT NULL,
  `ranges` bigint(20) NOT NULL,
  UNIQUE KEY `ranges` (`ranges`),
  KEY `FK94C70C1CABCE5486` (`ranges`),
  KEY `FK94C70C1C89445045` (`campaign_range_coverage`),
  CONSTRAINT `FK94C70C1C89445045` FOREIGN KEY (`campaign_range_coverage`) REFERENCES `campaign_range_coverage` (`id`),
  CONSTRAINT `FK94C70C1CABCE5486` FOREIGN KEY (`ranges`) REFERENCES `campaign_serial_range` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_range_coverage_ranges`
--


/*!40000 ALTER TABLE `campaign_range_coverage_ranges` DISABLE KEYS */;
LOCK TABLES `campaign_range_coverage_ranges` WRITE;
INSERT INTO `campaign_range_coverage_ranges` VALUES (1,1),(2,3),(3,2),(4,5),(5,6),(6,4);
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_range_coverage_ranges` ENABLE KEYS */;

--
-- Table structure for table `campaign_serial_range`
--

DROP TABLE IF EXISTS `campaign_serial_range`;
CREATE TABLE `campaign_serial_range` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_serial_number` varchar(255) default NULL,
  `to_serial_number` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `campaign_serial_range`
--


/*!40000 ALTER TABLE `campaign_serial_range` DISABLE KEYS */;
LOCK TABLES `campaign_serial_range` WRITE;
INSERT INTO `campaign_serial_range` VALUES (1,'12345','12350'),(2,'CA111','CA120'),(3,'A123','A165'),(4,'X122','X129'),(5,'12890','13000'),(6,'PR5100','PR5150');
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_serial_range` ENABLE KEYS */;

--
-- Table structure for table `carrier`
--

DROP TABLE IF EXISTS `carrier`;
CREATE TABLE `carrier` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `address` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK210ADEF83F73AC54` (`address`),
  CONSTRAINT `FK210ADEF83F73AC54` FOREIGN KEY (`address`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `carrier`
--


/*!40000 ALTER TABLE `carrier` DISABLE KEYS */;
LOCK TABLES `carrier` WRITE;
INSERT INTO `carrier` VALUES (1,'312','FedEx',1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `carrier` ENABLE KEYS */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `category`
--


/*!40000 ALTER TABLE `category` DISABLE KEYS */;
LOCK TABLES `category` WRITE;
INSERT INTO `category` VALUES (1,'No other work?','Frequent Claimants'),(2,'Or so they \"claim\"! ;-)','Frivolous Claimants'),(3,'Going..going..gone!','Deprecated Policies'),(4,'Crash!','Experimental Policies'),(5,'Standard Policies','Standard Policies');
UNLOCK TABLES;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;

--
-- Table structure for table `claim`
--

DROP TABLE IF EXISTS `claim`;
CREATE TABLE `claim` (
  `claimtype` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL auto_increment,
  `claim_number` varchar(255) default NULL,
  `condition_found` text,
  `external_comment` varchar(255) default NULL,
  `failure_date` date default NULL,
  `filed_on_date` date default NULL,
  `hours_in_service` int(11) default NULL,
  `installation_date` date default NULL,
  `internal_comment` varchar(255) default NULL,
  `item_reference_serialized` bit(1) default NULL,
  `last_updated_on_date` date default NULL,
  `no_of_resubmits` int(11) default NULL,
  `other_comments` text,
  `probable_cause` text,
  `processed_automatically` bit(1) default NULL,
  `purchase_date` date default NULL,
  `repair_date` date default NULL,
  `service_manager_accepted` bit(1) NOT NULL,
  `service_manager_request` bit(1) NOT NULL,
  `state` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `work_order_number` varchar(255) default NULL,
  `work_performed` text,
  `part_installed` bit(1) default NULL,
  `part_item_reference_serialized` bit(1) default NULL,
  `service_information` bigint(20) default NULL,
  `item_reference_referred_item` bigint(20) default NULL,
  `acceptance_reason` varchar(255) default NULL,
  `rejection_reason` varchar(255) default NULL,
  `reason_for_service_manager_request` varchar(255) default NULL,
  `part_item_reference_referred_item` bigint(20) default NULL,
  `applicable_policy` bigint(20) default NULL,
  `for_dealer` bigint(20) default NULL,
  `part_item_reference_referred_inventory_item` bigint(20) default NULL,
  `part_item_reference_unserialized_item` bigint(20) default NULL,
  `item_reference_unserialized_item` bigint(20) default NULL,
  `item_reference_referred_inventory_item` bigint(20) default NULL,
  `filed_by` bigint(20) default NULL,
  `last_updated_by` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK5A5A83CD527B238` (`for_dealer`),
  KEY `FK5A5A83CF1037990` (`reason_for_service_manager_request`),
  KEY `FK5A5A83CD68EED4A` (`part_item_reference_referred_item`),
  KEY `FK5A5A83C44BD208A` (`applicable_policy`),
  KEY `FK5A5A83CDE37AA83` (`last_updated_by`),
  KEY `FK5A5A83C7C0B7AA0` (`service_information`),
  KEY `FK5A5A83CC2028BB9` (`rejection_reason`),
  KEY `FK5A5A83C32DF75B8` (`part_item_reference_referred_inventory_item`),
  KEY `FK5A5A83C4DE6F615` (`acceptance_reason`),
  KEY `FK5A5A83C68A5D3EC` (`item_reference_referred_inventory_item`),
  KEY `FK5A5A83C3B57BC7E` (`item_reference_referred_item`),
  KEY `FK5A5A83C64306C6D` (`filed_by`),
  KEY `FK5A5A83C6C2C0F32` (`item_reference_unserialized_item`),
  KEY `FK5A5A83C4385F9FE` (`part_item_reference_unserialized_item`),
  CONSTRAINT `FK5A5A83C4385F9FE` FOREIGN KEY (`part_item_reference_unserialized_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK5A5A83C32DF75B8` FOREIGN KEY (`part_item_reference_referred_inventory_item`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FK5A5A83C3B57BC7E` FOREIGN KEY (`item_reference_referred_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK5A5A83C44BD208A` FOREIGN KEY (`applicable_policy`) REFERENCES `applicable_policy` (`id`),
  CONSTRAINT `FK5A5A83C4DE6F615` FOREIGN KEY (`acceptance_reason`) REFERENCES `acceptance_reason` (`code`),
  CONSTRAINT `FK5A5A83C64306C6D` FOREIGN KEY (`filed_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK5A5A83C68A5D3EC` FOREIGN KEY (`item_reference_referred_inventory_item`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FK5A5A83C6C2C0F32` FOREIGN KEY (`item_reference_unserialized_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK5A5A83C7C0B7AA0` FOREIGN KEY (`service_information`) REFERENCES `service_information` (`id`),
  CONSTRAINT `FK5A5A83CC2028BB9` FOREIGN KEY (`rejection_reason`) REFERENCES `rejection_reason` (`code`),
  CONSTRAINT `FK5A5A83CD527B238` FOREIGN KEY (`for_dealer`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FK5A5A83CD68EED4A` FOREIGN KEY (`part_item_reference_referred_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK5A5A83CDE37AA83` FOREIGN KEY (`last_updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK5A5A83CF1037990` FOREIGN KEY (`reason_for_service_manager_request`) REFERENCES `smr_reason` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `claim`
--


/*!40000 ALTER TABLE `claim` DISABLE KEYS */;
LOCK TABLES `claim` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `claim` ENABLE KEYS */;

--
-- Table structure for table `claim_audit`
--

DROP TABLE IF EXISTS `claim_audit`;
CREATE TABLE `claim_audit` (
  `id` bigint(20) NOT NULL auto_increment,
  `external_comments` varchar(255) default NULL,
  `internal` bit(1) NOT NULL,
  `internal_comments` varchar(255) default NULL,
  `previous_claim_snapshot_as_string` mediumtext,
  `previous_state` varchar(255) default NULL,
  `updated_on` bigint(20) default NULL,
  `for_claim` bigint(20) default NULL,
  `updated_by` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK9A678A182295C95B` (`for_claim`),
  KEY `FK9A678A187E64413A` (`updated_by`),
  CONSTRAINT `FK9A678A187E64413A` FOREIGN KEY (`updated_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK9A678A182295C95B` FOREIGN KEY (`for_claim`) REFERENCES `claim` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `claim_audit`
--


/*!40000 ALTER TABLE `claim_audit` DISABLE KEYS */;
LOCK TABLES `claim_audit` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `claim_audit` ENABLE KEYS */;

--
-- Table structure for table `claim_rule_failures`
--

DROP TABLE IF EXISTS `claim_rule_failures`;
CREATE TABLE `claim_rule_failures` (
  `claim` bigint(20) NOT NULL,
  `rule_failures` bigint(20) NOT NULL,
  PRIMARY KEY  (`claim`,`rule_failures`),
  UNIQUE KEY `rule_failures` (`rule_failures`),
  KEY `FKB9081749B61B5213` (`rule_failures`),
  KEY `FKB9081749D40C5451` (`claim`),
  CONSTRAINT `FKB9081749D40C5451` FOREIGN KEY (`claim`) REFERENCES `claim` (`id`),
  CONSTRAINT `FKB9081749B61B5213` FOREIGN KEY (`rule_failures`) REFERENCES `rule_failure` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `claim_rule_failures`
--


/*!40000 ALTER TABLE `claim_rule_failures` DISABLE KEYS */;
LOCK TABLES `claim_rule_failures` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `claim_rule_failures` ENABLE KEYS */;

--
-- Table structure for table `claim_user_process_comments`
--

DROP TABLE IF EXISTS `claim_user_process_comments`;
CREATE TABLE `claim_user_process_comments` (
  `claim` bigint(20) NOT NULL,
  `user_process_comments` bigint(20) NOT NULL,
  PRIMARY KEY  (`claim`,`user_process_comments`),
  UNIQUE KEY `user_process_comments` (`user_process_comments`),
  KEY `FKBD35B7F5FA00B0E5` (`user_process_comments`),
  KEY `FKBD35B7F5D40C5451` (`claim`),
  CONSTRAINT `FKBD35B7F5D40C5451` FOREIGN KEY (`claim`) REFERENCES `claim` (`id`),
  CONSTRAINT `FKBD35B7F5FA00B0E5` FOREIGN KEY (`user_process_comments`) REFERENCES `user_comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `claim_user_process_comments`
--


/*!40000 ALTER TABLE `claim_user_process_comments` DISABLE KEYS */;
LOCK TABLES `claim_user_process_comments` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `claim_user_process_comments` ENABLE KEYS */;

--
-- Table structure for table `compensation_term`
--

DROP TABLE IF EXISTS `compensation_term`;
CREATE TABLE `compensation_term` (
  `id` bigint(20) NOT NULL auto_increment,
  `section` bigint(20) default NULL,
  `recovery_formula` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK6952C1311D816E9E` (`section`),
  KEY `FK6952C131BBFD719E` (`recovery_formula`),
  CONSTRAINT `FK6952C131BBFD719E` FOREIGN KEY (`recovery_formula`) REFERENCES `recovery_formula` (`id`),
  CONSTRAINT `FK6952C1311D816E9E` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `compensation_term`
--


/*!40000 ALTER TABLE `compensation_term` DISABLE KEYS */;
LOCK TABLES `compensation_term` WRITE;
INSERT INTO `compensation_term` VALUES (1,1,1),(2,2,2),(3,3,3),(4,4,4),(5,1,5),(6,2,6),(7,3,7),(8,4,8);
UNLOCK TABLES;
/*!40000 ALTER TABLE `compensation_term` ENABLE KEYS */;

--
-- Table structure for table `competition_type`
--

DROP TABLE IF EXISTS `competition_type`;
CREATE TABLE `competition_type` (
  `type` varchar(255) NOT NULL,
  PRIMARY KEY  (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `competition_type`
--


/*!40000 ALTER TABLE `competition_type` DISABLE KEYS */;
LOCK TABLES `competition_type` WRITE;
INSERT INTO `competition_type` VALUES ('Competition'),('Own models');
UNLOCK TABLES;
/*!40000 ALTER TABLE `competition_type` ENABLE KEYS */;

--
-- Table structure for table `contract`
--

DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` varchar(255) default NULL,
  `due_days` int(11) default NULL,
  `name` varchar(255) default NULL,
  `physical_shipment_required` bit(1) default NULL,
  `from_date` date default NULL,
  `till_date` date default NULL,
  `coverage_condition` bigint(20) default NULL,
  `location` bigint(20) default NULL,
  `supplier` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKDE3511127A020CAC` (`supplier`),
  KEY `FKDE3511127F0A2760` (`location`),
  KEY `FKDE351112B0E81E28` (`coverage_condition`),
  CONSTRAINT `FKDE351112B0E81E28` FOREIGN KEY (`coverage_condition`) REFERENCES `coverage_condition` (`id`),
  CONSTRAINT `FKDE3511127A020CAC` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FKDE3511127F0A2760` FOREIGN KEY (`location`) REFERENCES `location` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contract`
--


/*!40000 ALTER TABLE `contract` DISABLE KEYS */;
LOCK TABLES `contract` WRITE;
INSERT INTO `contract` VALUES (1,'Northwind Contract',5,'Contract for Northwind','','2001-05-01','2009-05-01',1,5,31),(2,'Truckdrove Contract',5,'Contract for Truckdrove','','2001-05-01','2009-05-01',2,6,34);
UNLOCK TABLES;
/*!40000 ALTER TABLE `contract` ENABLE KEYS */;

--
-- Table structure for table `contract_applicability_terms`
--

DROP TABLE IF EXISTS `contract_applicability_terms`;
CREATE TABLE `contract_applicability_terms` (
  `contract` bigint(20) NOT NULL,
  `applicability_terms` bigint(20) NOT NULL,
  UNIQUE KEY `applicability_terms` (`applicability_terms`),
  KEY `FK44DCFAE055A60473` (`contract`),
  KEY `FK44DCFAE0EA821F99` (`applicability_terms`),
  CONSTRAINT `FK44DCFAE0EA821F99` FOREIGN KEY (`applicability_terms`) REFERENCES `domain_rule` (`id`),
  CONSTRAINT `FK44DCFAE055A60473` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contract_applicability_terms`
--


/*!40000 ALTER TABLE `contract_applicability_terms` DISABLE KEYS */;
LOCK TABLES `contract_applicability_terms` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `contract_applicability_terms` ENABLE KEYS */;

--
-- Table structure for table `contract_compensation_terms`
--

DROP TABLE IF EXISTS `contract_compensation_terms`;
CREATE TABLE `contract_compensation_terms` (
  `contract` bigint(20) NOT NULL,
  `compensation_terms` bigint(20) NOT NULL,
  UNIQUE KEY `compensation_terms` (`compensation_terms`),
  KEY `FKDF880DAF55A60473` (`contract`),
  KEY `FKDF880DAF47086897` (`compensation_terms`),
  CONSTRAINT `FKDF880DAF47086897` FOREIGN KEY (`compensation_terms`) REFERENCES `compensation_term` (`id`),
  CONSTRAINT `FKDF880DAF55A60473` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contract_compensation_terms`
--


/*!40000 ALTER TABLE `contract_compensation_terms` DISABLE KEYS */;
LOCK TABLES `contract_compensation_terms` WRITE;
INSERT INTO `contract_compensation_terms` VALUES (1,1),(1,2),(1,3),(1,4),(2,5),(2,6),(2,7),(2,8);
UNLOCK TABLES;
/*!40000 ALTER TABLE `contract_compensation_terms` ENABLE KEYS */;

--
-- Table structure for table `contract_items_covered`
--

DROP TABLE IF EXISTS `contract_items_covered`;
CREATE TABLE `contract_items_covered` (
  `contract` bigint(20) NOT NULL,
  `items_covered` bigint(20) NOT NULL,
  KEY `FK8469B8AA55A60473` (`contract`),
  KEY `FK8469B8AACAA41634` (`items_covered`),
  CONSTRAINT `FK8469B8AACAA41634` FOREIGN KEY (`items_covered`) REFERENCES `item` (`id`),
  CONSTRAINT `FK8469B8AA55A60473` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contract_items_covered`
--


/*!40000 ALTER TABLE `contract_items_covered` DISABLE KEYS */;
LOCK TABLES `contract_items_covered` WRITE;
INSERT INTO `contract_items_covered` VALUES (1,2),(1,20),(1,7),(2,3),(2,21),(2,13);
UNLOCK TABLES;
/*!40000 ALTER TABLE `contract_items_covered` ENABLE KEYS */;

--
-- Table structure for table `cost_category`
--

DROP TABLE IF EXISTS `cost_category`;
CREATE TABLE `cost_category` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cost_category`
--


/*!40000 ALTER TABLE `cost_category` DISABLE KEYS */;
LOCK TABLES `cost_category` WRITE;
INSERT INTO `cost_category` VALUES (1,'OEM_PARTS','OEM Parts','OEM Parts'),(2,'NON_OEM_PARTS','Non OEM Parts','Non OEM Parts'),(3,'LABOR','Labor','Labor'),(4,'TRAVEL','Travel','Travel'),(5,'FREIGHT_DUTY','Item Freight Duty','Item Freight Duty'),(6,'MEALS','Meals','Meals'),(7,'PARKING','Parking','Parking');
UNLOCK TABLES;
/*!40000 ALTER TABLE `cost_category` ENABLE KEYS */;

--
-- Table structure for table `cost_line_item`
--

DROP TABLE IF EXISTS `cost_line_item`;
CREATE TABLE `cost_line_item` (
  `id` bigint(20) NOT NULL auto_increment,
  `cost_amt` decimal(19,2) default NULL,
  `cost_curr` varchar(255) default NULL,
  `contract_cost_amt` decimal(19,2) default NULL,
  `contract_cost_curr` varchar(255) default NULL,
  `recovered_cost_amt` decimal(19,2) default NULL,
  `recovered_cost_curr` varchar(255) default NULL,
  `section` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKEC316A8C1D816E9E` (`section`),
  CONSTRAINT `FKEC316A8C1D816E9E` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cost_line_item`
--


/*!40000 ALTER TABLE `cost_line_item` DISABLE KEYS */;
LOCK TABLES `cost_line_item` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `cost_line_item` ENABLE KEYS */;

--
-- Table structure for table `coverage_condition`
--

DROP TABLE IF EXISTS `coverage_condition`;
CREATE TABLE `coverage_condition` (
  `id` bigint(20) NOT NULL auto_increment,
  `compared_with` varchar(255) default NULL,
  `number_of_months` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `coverage_condition`
--


/*!40000 ALTER TABLE `coverage_condition` DISABLE KEYS */;
LOCK TABLES `coverage_condition` WRITE;
INSERT INTO `coverage_condition` VALUES (1,'DATE_OF_MANUFACTURE',5),(2,'DATE_OF_MANUFACTURE',5);
UNLOCK TABLES;
/*!40000 ALTER TABLE `coverage_condition` ENABLE KEYS */;

--
-- Table structure for table `credit_memo`
--

DROP TABLE IF EXISTS `credit_memo`;
CREATE TABLE `credit_memo` (
  `id` bigint(20) NOT NULL auto_increment,
  `claim_number` varchar(255) default NULL,
  `credit_memo_date` date default NULL,
  `credit_memo_number` int(11) default NULL,
  `tax_amount_amt` decimal(19,2) default NULL,
  `tax_amount_curr` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `credit_memo`
--


/*!40000 ALTER TABLE `credit_memo` DISABLE KEYS */;
LOCK TABLES `credit_memo` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `credit_memo` ENABLE KEYS */;

--
-- Table structure for table `criteria_based_value`
--

DROP TABLE IF EXISTS `criteria_based_value`;
CREATE TABLE `criteria_based_value` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date NOT NULL,
  `till_date` date NOT NULL,
  `value` double NOT NULL,
  `parent` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKAFBC1D055253B609` (`parent`),
  CONSTRAINT `FKAFBC1D055253B609` FOREIGN KEY (`parent`) REFERENCES `payment_modifier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `criteria_based_value`
--


/*!40000 ALTER TABLE `criteria_based_value` DISABLE KEYS */;
LOCK TABLES `criteria_based_value` WRITE;
INSERT INTO `criteria_based_value` VALUES (1,'2006-01-01','2009-01-01',1,1),(2,'2006-01-01','2009-01-01',2,2),(3,'2006-01-01','2009-01-01',3,3),(4,'2006-01-01','2009-01-01',4,4),(5,'2006-01-01','2009-01-01',5,5),(6,'2006-01-01','2009-01-01',6,6),(7,'2006-01-01','2009-01-01',7,7),(8,'2006-01-01','2009-01-01',8,8),(9,'2006-01-01','2009-01-01',9,9),(10,'2006-01-01','2009-01-01',10,10),(11,'2006-01-01','2009-01-01',11,11),(12,'2006-01-01','2009-01-01',12,12),(13,'2006-01-01','2009-01-01',13,13),(14,'2006-01-01','2009-01-01',14,14),(15,'2006-01-01','2009-01-01',15,15),(16,'2006-01-01','2009-01-01',16,16),(17,'2006-01-01','2009-01-01',10,17),(18,'2006-01-01','2009-01-01',10,18),(19,'2006-01-01','2009-01-01',10,19),(20,'2006-01-01','2009-01-01',10,20),(21,'2006-01-01','2009-01-01',10,21),(22,'2006-01-01','2009-01-01',10,22),(23,'2006-01-01','2009-01-01',10,23);
UNLOCK TABLES;
/*!40000 ALTER TABLE `criteria_based_value` ENABLE KEYS */;

--
-- Table structure for table `criteria_evaluation_precedence`
--

DROP TABLE IF EXISTS `criteria_evaluation_precedence`;
CREATE TABLE `criteria_evaluation_precedence` (
  `id` bigint(20) NOT NULL auto_increment,
  `for_data` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_data` (`for_data`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `criteria_evaluation_precedence`
--


/*!40000 ALTER TABLE `criteria_evaluation_precedence` DISABLE KEYS */;
LOCK TABLES `criteria_evaluation_precedence` WRITE;
INSERT INTO `criteria_evaluation_precedence` VALUES (5,'Claim Bonus'),(2,'Dealer Unit Rate List'),(14,'Item Freight Duty Discount'),(12,'Labor Discount'),(9,'Labor Rate Price List'),(15,'Meals Discount'),(11,'Non OEM Parts Discount'),(6,'OEM Parts Discount'),(16,'Parking Discount'),(7,'Part Return Configuration'),(1,'Parts Price List'),(13,'Travel Discount'),(10,'Travel Rate Price List');
UNLOCK TABLES;
/*!40000 ALTER TABLE `criteria_evaluation_precedence` ENABLE KEYS */;

--
-- Table structure for table `currency_conversion_factor`
--

DROP TABLE IF EXISTS `currency_conversion_factor`;
CREATE TABLE `currency_conversion_factor` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date NOT NULL,
  `till_date` date NOT NULL,
  `factor` decimal(19,6) NOT NULL,
  `parent` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK313043EA9EA41F6C` (`parent`),
  CONSTRAINT `FK313043EA9EA41F6C` FOREIGN KEY (`parent`) REFERENCES `currency_exchange_rate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `currency_conversion_factor`
--


/*!40000 ALTER TABLE `currency_conversion_factor` DISABLE KEYS */;
LOCK TABLES `currency_conversion_factor` WRITE;
INSERT INTO `currency_conversion_factor` VALUES (1,'2000-01-01','2010-12-31','1.296008',1),(2,'2000-01-01','2010-12-31','1.963664',2),(3,'2000-01-01','2010-12-31','0.774653',3),(4,'2000-01-01','2010-12-31','0.197496',4),(5,'2000-01-01','2010-12-31','0.022673',5);
UNLOCK TABLES;
/*!40000 ALTER TABLE `currency_conversion_factor` ENABLE KEYS */;

--
-- Table structure for table `currency_exchange_rate`
--

DROP TABLE IF EXISTS `currency_exchange_rate`;
CREATE TABLE `currency_exchange_rate` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_currency` varchar(255) NOT NULL,
  `to_currency` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `currency_exchange_rate`
--


/*!40000 ALTER TABLE `currency_exchange_rate` DISABLE KEYS */;
LOCK TABLES `currency_exchange_rate` WRITE;
INSERT INTO `currency_exchange_rate` VALUES (1,'EUR','USD'),(2,'GBP','USD'),(3,'AUD','USD'),(4,'FRF','USD'),(5,'INR','USD');
UNLOCK TABLES;
/*!40000 ALTER TABLE `currency_exchange_rate` ENABLE KEYS */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL,
  `company_name` varchar(255) default NULL,
  `corporate_name` varchar(255) default NULL,
  `customer_id` varchar(255) default NULL,
  `individual` bit(1) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK24217FDE9000BBFA` (`id`),
  CONSTRAINT `FK24217FDE9000BBFA` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customer`
--


/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
LOCK TABLES `customer` WRITE;
INSERT INTO `customer` VALUES (26,'HP','HP',NULL,'\0'),(27,'Dell','Dell',NULL,'\0'),(28,'Microsoft','Microsoft',NULL,'\0'),(29,NULL,NULL,NULL,''),(30,NULL,NULL,NULL,'');
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;

--
-- Table structure for table `customer_addresses`
--

DROP TABLE IF EXISTS `customer_addresses`;
CREATE TABLE `customer_addresses` (
  `customer` bigint(20) NOT NULL,
  `addresses` bigint(20) NOT NULL,
  UNIQUE KEY `addresses` (`addresses`),
  KEY `FK9D433881B7FC8C02` (`addresses`),
  KEY `FK9D4338816564D7C3` (`customer`),
  CONSTRAINT `FK9D4338816564D7C3` FOREIGN KEY (`customer`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK9D433881B7FC8C02` FOREIGN KEY (`addresses`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customer_addresses`
--


/*!40000 ALTER TABLE `customer_addresses` DISABLE KEYS */;
LOCK TABLES `customer_addresses` WRITE;
INSERT INTO `customer_addresses` VALUES (26,17),(27,18),(28,19),(29,20),(30,21);
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer_addresses` ENABLE KEYS */;

--
-- Table structure for table `customer_associated_organizations`
--

DROP TABLE IF EXISTS `customer_associated_organizations`;
CREATE TABLE `customer_associated_organizations` (
  `customer` bigint(20) NOT NULL,
  `associated_organizations` bigint(20) NOT NULL,
  PRIMARY KEY  (`customer`,`associated_organizations`),
  KEY `FKA3822768CAD87A6E` (`associated_organizations`),
  KEY `FKA38227686564D7C3` (`customer`),
  CONSTRAINT `FKA38227686564D7C3` FOREIGN KEY (`customer`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKA3822768CAD87A6E` FOREIGN KEY (`associated_organizations`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customer_associated_organizations`
--


/*!40000 ALTER TABLE `customer_associated_organizations` DISABLE KEYS */;
LOCK TABLES `customer_associated_organizations` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer_associated_organizations` ENABLE KEYS */;

--
-- Table structure for table `dealer_category`
--

DROP TABLE IF EXISTS `dealer_category`;
CREATE TABLE `dealer_category` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK64241C64D9BE9114` (`id`),
  CONSTRAINT `FK64241C64D9BE9114` FOREIGN KEY (`id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dealer_category`
--


/*!40000 ALTER TABLE `dealer_category` DISABLE KEYS */;
LOCK TABLES `dealer_category` WRITE;
INSERT INTO `dealer_category` VALUES (1),(2);
UNLOCK TABLES;
/*!40000 ALTER TABLE `dealer_category` ENABLE KEYS */;

--
-- Table structure for table `dealer_category_dealership`
--

DROP TABLE IF EXISTS `dealer_category_dealership`;
CREATE TABLE `dealer_category_dealership` (
  `dealer_category` bigint(20) NOT NULL,
  `dealership` bigint(20) NOT NULL,
  PRIMARY KEY  (`dealer_category`,`dealership`),
  KEY `FK59AE1690EE19ABBE` (`dealership`),
  KEY `FK59AE16906F9F1156` (`dealer_category`),
  CONSTRAINT `FK59AE16906F9F1156` FOREIGN KEY (`dealer_category`) REFERENCES `dealer_category` (`id`),
  CONSTRAINT `FK59AE1690EE19ABBE` FOREIGN KEY (`dealership`) REFERENCES `dealership` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dealer_category_dealership`
--


/*!40000 ALTER TABLE `dealer_category_dealership` DISABLE KEYS */;
LOCK TABLES `dealer_category_dealership` WRITE;
INSERT INTO `dealer_category_dealership` VALUES (1,7),(1,10),(1,20),(1,21),(2,22),(2,23),(2,24),(2,25);
UNLOCK TABLES;
/*!40000 ALTER TABLE `dealer_category_dealership` ENABLE KEYS */;

--
-- Table structure for table `dealer_group`
--

DROP TABLE IF EXISTS `dealer_group`;
CREATE TABLE `dealer_group` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` varchar(255) default NULL,
  `name` varchar(255) NOT NULL,
  `depth` int(11) NOT NULL,
  `lft` int(11) NOT NULL,
  `rgt` int(11) NOT NULL,
  `tree_id` bigint(20) NOT NULL,
  `is_part_of` bigint(20) default NULL,
  `scheme` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKEAC40D9C9BE3DE0` (`is_part_of`),
  KEY `FKEAC40D93085ADD7` (`scheme`),
  CONSTRAINT `FKEAC40D93085ADD7` FOREIGN KEY (`scheme`) REFERENCES `dealer_scheme` (`id`),
  CONSTRAINT `FKEAC40D9C9BE3DE0` FOREIGN KEY (`is_part_of`) REFERENCES `dealer_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dealer_group`
--


/*!40000 ALTER TABLE `dealer_group` DISABLE KEYS */;
LOCK TABLES `dealer_group` WRITE;
INSERT INTO `dealer_group` VALUES (1,'Group 1','Group 1',1,1,4,1,NULL,2),(2,'Group 2','Group 2',2,2,3,1,1,2);
UNLOCK TABLES;
/*!40000 ALTER TABLE `dealer_group` ENABLE KEYS */;

--
-- Table structure for table `dealer_scheme`
--

DROP TABLE IF EXISTS `dealer_scheme`;
CREATE TABLE `dealer_scheme` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dealer_scheme`
--


/*!40000 ALTER TABLE `dealer_scheme` DISABLE KEYS */;
LOCK TABLES `dealer_scheme` WRITE;
INSERT INTO `dealer_scheme` VALUES (1,'Org Hierarchy'),(2,'Claim Dealer Unit Rates');
UNLOCK TABLES;
/*!40000 ALTER TABLE `dealer_scheme` ENABLE KEYS */;

--
-- Table structure for table `dealer_scheme_purposes`
--

DROP TABLE IF EXISTS `dealer_scheme_purposes`;
CREATE TABLE `dealer_scheme_purposes` (
  `dealer_scheme` bigint(20) NOT NULL,
  `purposes` bigint(20) NOT NULL,
  PRIMARY KEY  (`dealer_scheme`,`purposes`),
  UNIQUE KEY `purposes` (`purposes`),
  KEY `FK1426CE49CFD0EB05` (`purposes`),
  KEY `FK1426CE494123AEBD` (`dealer_scheme`),
  CONSTRAINT `FK1426CE494123AEBD` FOREIGN KEY (`dealer_scheme`) REFERENCES `dealer_scheme` (`id`),
  CONSTRAINT `FK1426CE49CFD0EB05` FOREIGN KEY (`purposes`) REFERENCES `purpose` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dealer_scheme_purposes`
--


/*!40000 ALTER TABLE `dealer_scheme_purposes` DISABLE KEYS */;
LOCK TABLES `dealer_scheme_purposes` WRITE;
INSERT INTO `dealer_scheme_purposes` VALUES (2,7),(1,8);
UNLOCK TABLES;
/*!40000 ALTER TABLE `dealer_scheme_purposes` ENABLE KEYS */;

--
-- Table structure for table `dealers_in_group`
--

DROP TABLE IF EXISTS `dealers_in_group`;
CREATE TABLE `dealers_in_group` (
  `dealer_group` bigint(20) NOT NULL,
  `dealer` bigint(20) NOT NULL,
  PRIMARY KEY  (`dealer_group`,`dealer`),
  KEY `FKDC72CC0AAE40EF2B` (`dealer_group`),
  KEY `FKDC72CC0A52828602` (`dealer`),
  CONSTRAINT `FKDC72CC0A52828602` FOREIGN KEY (`dealer`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FKDC72CC0AAE40EF2B` FOREIGN KEY (`dealer_group`) REFERENCES `dealer_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dealers_in_group`
--


/*!40000 ALTER TABLE `dealers_in_group` DISABLE KEYS */;
LOCK TABLES `dealers_in_group` WRITE;
INSERT INTO `dealers_in_group` VALUES (2,7),(2,20);
UNLOCK TABLES;
/*!40000 ALTER TABLE `dealers_in_group` ENABLE KEYS */;

--
-- Table structure for table `dealership`
--

DROP TABLE IF EXISTS `dealership`;
CREATE TABLE `dealership` (
  `id` bigint(20) NOT NULL,
  `dealer_number` varchar(255) default NULL,
  `preferred_currency` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK4BF8C3D5B2F46962` (`id`),
  CONSTRAINT `FK4BF8C3D5B2F46962` FOREIGN KEY (`id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dealership`
--


/*!40000 ALTER TABLE `dealership` DISABLE KEYS */;
LOCK TABLES `dealership` WRITE;
INSERT INTO `dealership` VALUES (7,'COMP-123','USD'),(10,'FER-302','FRF'),(20,'Dealer 1','EUR'),(21,'Dealer 2','INR'),(22,'Dealer 3','GBP'),(23,'Dealer 4','FRF'),(24,'Dealer 5','EUR'),(25,'Dealer 6','USD');
UNLOCK TABLES;
/*!40000 ALTER TABLE `dealership` ENABLE KEYS */;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `id` bigint(20) NOT NULL,
  `content` mediumblob,
  `content_type` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `file_name` varchar(255) default NULL,
  `size` int(11) NOT NULL,
  `uploaded_on` bigint(20) default NULL,
  `uploaded_by` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK335CD11BCFC86575` (`uploaded_by`),
  CONSTRAINT `FK335CD11BCFC86575` FOREIGN KEY (`uploaded_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `document`
--


/*!40000 ALTER TABLE `document` DISABLE KEYS */;
LOCK TABLES `document` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `document` ENABLE KEYS */;

--
-- Table structure for table `domain_predicate`
--

DROP TABLE IF EXISTS `domain_predicate`;
CREATE TABLE `domain_predicate` (
  `id` bigint(20) NOT NULL auto_increment,
  `context` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `predicate_asxml` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `domain_predicate`
--


/*!40000 ALTER TABLE `domain_predicate` DISABLE KEYS */;
LOCK TABLES `domain_predicate` WRITE;
INSERT INTO `domain_predicate` VALUES (1,'ClaimRules','Failure Date is before May 1, 2007','<all isForOneToOne=\"false\">\n  <predicates>\n    <tavant.twms.domain.rules.IsAfter>\n      <lhs class=\"domainVariable\">\n        <accessedFromType>Claim</accessedFromType>\n        <fieldName>claim.failureDate</fieldName>\n      </lhs>\n      <rhs class=\"constant\" literal=\"05/01/2007\" type=\"date\"/>\n    </tavant.twms.domain.rules.IsAfter>\n  </predicates>\n</all>\n');
UNLOCK TABLES;
/*!40000 ALTER TABLE `domain_predicate` ENABLE KEYS */;

--
-- Table structure for table `domain_predicate_refers_to_predicates`
--

DROP TABLE IF EXISTS `domain_predicate_refers_to_predicates`;
CREATE TABLE `domain_predicate_refers_to_predicates` (
  `domain_predicate` bigint(20) NOT NULL,
  `refers_to_predicates` bigint(20) NOT NULL,
  PRIMARY KEY  (`domain_predicate`,`refers_to_predicates`),
  KEY `FKC041F6F7C1CAF8A3` (`domain_predicate`),
  KEY `FKC041F6F7AB443F3B` (`refers_to_predicates`),
  CONSTRAINT `FKC041F6F7AB443F3B` FOREIGN KEY (`refers_to_predicates`) REFERENCES `domain_predicate` (`id`),
  CONSTRAINT `FKC041F6F7C1CAF8A3` FOREIGN KEY (`domain_predicate`) REFERENCES `domain_predicate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `domain_predicate_refers_to_predicates`
--


/*!40000 ALTER TABLE `domain_predicate_refers_to_predicates` DISABLE KEYS */;
LOCK TABLES `domain_predicate_refers_to_predicates` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `domain_predicate_refers_to_predicates` ENABLE KEYS */;

--
-- Table structure for table `domain_rule`
--

DROP TABLE IF EXISTS `domain_rule`;
CREATE TABLE `domain_rule` (
  `id` bigint(20) NOT NULL auto_increment,
  `context` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `failure_message` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `action` bigint(20) default NULL,
  `predicate` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK636D597BFC9B598` (`action`),
  KEY `FK636D5975A364E5E` (`predicate`),
  CONSTRAINT `FK636D5975A364E5E` FOREIGN KEY (`predicate`) REFERENCES `domain_predicate` (`id`),
  CONSTRAINT `FK636D597BFC9B598` FOREIGN KEY (`action`) REFERENCES `domain_rule_action` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `domain_rule`
--


/*!40000 ALTER TABLE `domain_rule` DISABLE KEYS */;
LOCK TABLES `domain_rule` WRITE;
INSERT INTO `domain_rule` VALUES (1,'ClaimRules','Failure Date is before May 1, 2007','Failure Date falls before May 1, 2007','Failure Date is before May 1, 2007',3,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `domain_rule` ENABLE KEYS */;

--
-- Table structure for table `domain_rule_action`
--

DROP TABLE IF EXISTS `domain_rule_action`;
CREATE TABLE `domain_rule_action` (
  `id` bigint(20) NOT NULL auto_increment,
  `context` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `state` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `domain_rule_action`
--


/*!40000 ALTER TABLE `domain_rule_action` DISABLE KEYS */;
LOCK TABLES `domain_rule_action` WRITE;
INSERT INTO `domain_rule_action` VALUES (1,'ClaimRules','Reject Claim','rejected'),(2,'ClaimRules','Put Claim On Hold','on hold'),(3,'ClaimRules','Send Claim For Manual Review','manual review'),(4,'EntryValidationRules','Show Warning','warning'),(5,'EntryValidationRules','Show Error','error');
UNLOCK TABLES;
/*!40000 ALTER TABLE `domain_rule_action` ENABLE KEYS */;

--
-- Table structure for table `eval_precedence_properties`
--

DROP TABLE IF EXISTS `eval_precedence_properties`;
CREATE TABLE `eval_precedence_properties` (
  `for_criteria` bigint(20) NOT NULL,
  `properties_element_domain_name` varchar(255) default NULL,
  `properties_element_property_expression` varchar(255) default NULL,
  `precedence` int(11) NOT NULL,
  PRIMARY KEY  (`for_criteria`,`precedence`),
  KEY `FKF00FAB257CAADEE8` (`for_criteria`),
  CONSTRAINT `FKF00FAB257CAADEE8` FOREIGN KEY (`for_criteria`) REFERENCES `criteria_evaluation_precedence` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `eval_precedence_properties`
--


/*!40000 ALTER TABLE `eval_precedence_properties` DISABLE KEYS */;
LOCK TABLES `eval_precedence_properties` WRITE;
INSERT INTO `eval_precedence_properties` VALUES (1,'Warranty Type','warrantyType',0),(2,'Dealer','dealerCriterion',0),(2,'Claim Type','claimType',1),(2,'Warranty Type','warrantyType',2),(2,'Product Type','productType',3),(5,'Dealer','dealerCriterion',0),(5,'Claim Type','claimType',1),(5,'Warranty Type','warrantyType',2),(5,'Product Type','productType',3),(6,'Dealer','dealerCriterion',0),(6,'Claim Type','claimType',1),(6,'Warranty Type','warrantyType',2),(6,'Product Type','productType',3),(7,'Dealer','dealerCriterion',0),(7,'Claim Type','claimType',1),(7,'Warranty Type','warrantyType',2),(7,'Product Type','productType',3),(9,'Dealer','dealerCriterion',0),(9,'Claim Type','claimType',1),(9,'Warranty Type','warrantyType',2),(9,'Product Type','productType',3),(10,'Dealer','dealerCriterion',0),(10,'Claim Type','claimType',1),(10,'Warranty Type','warrantyType',2),(10,'Product Type','productType',3);
UNLOCK TABLES;
/*!40000 ALTER TABLE `eval_precedence_properties` ENABLE KEYS */;

--
-- Table structure for table `failure_cause`
--

DROP TABLE IF EXISTS `failure_cause`;
CREATE TABLE `failure_cause` (
  `id` bigint(20) NOT NULL auto_increment,
  `failure_type_id` bigint(20) default NULL,
  `definition_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK9F6CB9748659467D` (`definition_id`),
  KEY `FK9F6CB97431ECB9AB` (`failure_type_id`),
  CONSTRAINT `FK9F6CB97431ECB9AB` FOREIGN KEY (`failure_type_id`) REFERENCES `failure_type` (`id`),
  CONSTRAINT `FK9F6CB9748659467D` FOREIGN KEY (`definition_id`) REFERENCES `failure_cause_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `failure_cause`
--


/*!40000 ALTER TABLE `failure_cause` DISABLE KEYS */;
LOCK TABLES `failure_cause` WRITE;
INSERT INTO `failure_cause` VALUES (207117200,207117150,128550),(207117300,207117250,128650),(207117400,207117350,128700),(207117500,207117450,128800),(207117600,207117550,129000),(207117700,207117650,129200),(207117800,207117750,129700),(207117900,207117850,129800),(207118000,207117950,130150),(207118100,207118050,130500),(207118200,207118150,128750),(207118300,207118250,129250),(207118400,207118350,130500),(207118500,207118450,130450),(207118600,207118550,130500),(207118700,207118650,130500),(207118800,207118750,128550),(207118900,207118850,128650),(207119000,207118950,128700),(207119100,207119050,128750),(207119200,207119150,128800),(207119300,207119250,129000),(207119400,207119350,129200),(207119500,207119450,129250),(207119600,207119550,129700),(207119700,207119650,129800),(207119800,207119750,130050),(207119900,207119850,130150),(207120000,207119950,130450),(207120100,207120050,130500),(207120200,207120150,157050),(207120300,207120250,130500),(207120400,207120350,130500),(207120500,207120450,128550),(207120600,207120550,128650),(207120700,207120650,128700),(207120800,207120750,128750),(207120900,207120850,128800),(207121000,207120950,129000),(207121100,207121050,129200),(207121200,207121150,129250),(207121300,207121250,129700),(207121400,207121350,129800),(207121500,207121450,130050),(207121600,207121550,130150),(207121700,207121650,130450),(207121800,207121750,130500),(207121900,207121850,157050),(207122000,207121950,128750),(207122100,207122050,128800),(207122200,207122150,129250),(207122300,207122250,130500),(207122400,207122350,128550),(207122500,207122450,128650),(207122600,207122550,128700),(207122700,207122650,128750),(207122800,207122750,128800),(207122900,207122850,129000),(207123000,207122950,129200),(207123100,207123050,129250),(207123200,207123150,129700),(207123300,207123250,129800),(207123400,207123350,130050),(207123500,207123450,130150),(207123600,207123550,130450),(207123700,207123650,130500),(207123800,207123750,157050),(207123900,207123850,128550),(207124000,207123950,128650),(207124100,207124050,128700),(207124200,207124150,128750),(207124300,207124250,128800),(207124400,207124350,129000),(207124500,207124450,129200),(207124600,207124550,129250),(207124700,207124650,129700),(207124800,207124750,129800),(207124900,207124850,130050),(207125000,207124950,130150),(207125100,207125050,130450),(207125200,207125150,130500),(207125300,207125250,157050),(207125400,207125350,130500),(207237150,207237100,134100),(238437696,238437695,128700),(238437698,238437697,128700);
UNLOCK TABLES;
/*!40000 ALTER TABLE `failure_cause` ENABLE KEYS */;

--
-- Table structure for table `failure_cause_definition`
--

DROP TABLE IF EXISTS `failure_cause_definition`;
CREATE TABLE `failure_cause_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `failure_cause_definition`
--


/*!40000 ALTER TABLE `failure_cause_definition` DISABLE KEYS */;
LOCK TABLES `failure_cause_definition` WRITE;
INSERT INTO `failure_cause_definition` VALUES (128550,'C01','Bad Crimp','BAD CRIMP'),(128650,'C03','Rubbed Through','RUBBED THROUGH'),(128700,'C04','Blown/Burst','BLOWN/BURST'),(128750,'C05','Cross Threaded','CROSS THREADED'),(128800,'C06','Improper Fit','IMPROPER FIT'),(128900,'C08','Under Torque','UNDER TORQUE'),(129000,'C10','Bad Pump','BAD PUMP'),(129100,'C12','Sensor/Sendor','SENSOR/SENDOR'),(129200,'C14','Obstruction','OBSTRUCTION'),(129250,'C15','Improper Routing/Pinched','IMPROPER ROUTING/PINCHED'),(129350,'C17','Bad Injector','BAD INJECTOR'),(129400,'C18','Bad Hydraulic Pump','BAD HYDRAULIC PUMP'),(129450,'C19','Bad Relief Valve','BAD RELIEF VALVE'),(129550,'C21','Bad Battery','BAD BATTERY'),(129600,'C22','Bad Alternator','BAD ALTERNATOR'),(129700,'C24','Short','SHORT'),(129800,'C26','Not Connected','NOT CONNECTED'),(129900,'C28','Corrosion','CORROSION'),(130050,'C31','Vibration','VIBRATION'),(130100,'C32','Bad Weld','BAD WELD'),(130150,'C33','Rust','RUST'),(130200,'C34','Chipped','CHIPPED'),(130450,'C39','Shipped Incorrectly','SHIPPED INCORRECTLY'),(130500,'C40','Improper Installation','IMPROPER INSTALLATION'),(134100,'NA','Not Available','Not Available'),(157050,'C41','Other','Others'),(158000,'C53','Error - Customer','ERROR - CUSTOMER');
UNLOCK TABLES;
/*!40000 ALTER TABLE `failure_cause_definition` ENABLE KEYS */;

--
-- Table structure for table `failure_reason`
--

DROP TABLE IF EXISTS `failure_reason`;
CREATE TABLE `failure_reason` (
  `code` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `state` varchar(255) default NULL,
  PRIMARY KEY  (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `failure_reason`
--


/*!40000 ALTER TABLE `failure_reason` DISABLE KEYS */;
LOCK TABLES `failure_reason` WRITE;
INSERT INTO `failure_reason` VALUES ('COE','Customer Operation Error','active'),('EDEF','Engineering Defect','active'),('NCF','Non Causal Failure','active'),('QDEF','Quality Defect','active');
UNLOCK TABLES;
/*!40000 ALTER TABLE `failure_reason` ENABLE KEYS */;

--
-- Table structure for table `failure_structure`
--

DROP TABLE IF EXISTS `failure_structure`;
CREATE TABLE `failure_structure` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `for_item_group` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK9D9974FEF0FA52EB` (`for_item_group`),
  CONSTRAINT `FK9D9974FEF0FA52EB` FOREIGN KEY (`for_item_group`) REFERENCES `item_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `failure_structure`
--


/*!40000 ALTER TABLE `failure_structure` DISABLE KEYS */;
LOCK TABLES `failure_structure` WRITE;
INSERT INTO `failure_structure` VALUES (1,'FS-11',14),(2,'FS-12',15),(3,'FS-13',16),(4,'FS-14',17),(5,'FS-15',18),(6,'FS-16',19),(7,'FS-17',20),(8,'FS-18',21),(9,'FS-19',22);
UNLOCK TABLES;
/*!40000 ALTER TABLE `failure_structure` ENABLE KEYS */;

--
-- Table structure for table `failure_structure_assemblies`
--

DROP TABLE IF EXISTS `failure_structure_assemblies`;
CREATE TABLE `failure_structure_assemblies` (
  `failure_structure` bigint(20) NOT NULL,
  `assemblies` bigint(20) NOT NULL,
  PRIMARY KEY  (`failure_structure`,`assemblies`),
  KEY `FKEDFCA2E5AC03C92E` (`assemblies`),
  KEY `FKEDFCA2E5C339864B` (`failure_structure`),
  CONSTRAINT `FKEDFCA2E5C339864B` FOREIGN KEY (`failure_structure`) REFERENCES `failure_structure` (`id`),
  CONSTRAINT `FKEDFCA2E5AC03C92E` FOREIGN KEY (`assemblies`) REFERENCES `assembly` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `failure_structure_assemblies`
--


/*!40000 ALTER TABLE `failure_structure_assemblies` DISABLE KEYS */;
LOCK TABLES `failure_structure_assemblies` WRITE;
INSERT INTO `failure_structure_assemblies` VALUES (1,1),(2,1),(3,1),(5,1),(6,1),(7,1),(8,1),(9,1),(1,3),(2,3),(3,3),(5,3),(6,3),(7,3),(8,3),(9,3),(1,5),(2,5),(3,5),(5,5),(6,5),(7,5),(8,5),(9,5),(1,9),(2,9),(3,9),(5,9),(6,9),(7,9),(8,9),(9,9),(4,10),(4,12),(4,14),(4,18);
UNLOCK TABLES;
/*!40000 ALTER TABLE `failure_structure_assemblies` ENABLE KEYS */;

--
-- Table structure for table `failure_type`
--

DROP TABLE IF EXISTS `failure_type`;
CREATE TABLE `failure_type` (
  `id` bigint(20) NOT NULL auto_increment,
  `for_item_group_id` bigint(20) default NULL,
  `definition_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKF4A87CCFAB24AC1A` (`definition_id`),
  KEY `FKF4A87CCF4B15F4F3` (`for_item_group_id`),
  CONSTRAINT `FKF4A87CCF4B15F4F3` FOREIGN KEY (`for_item_group_id`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FKF4A87CCFAB24AC1A` FOREIGN KEY (`definition_id`) REFERENCES `failure_type_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `failure_type`
--


/*!40000 ALTER TABLE `failure_type` DISABLE KEYS */;
LOCK TABLES `failure_type` WRITE;
INSERT INTO `failure_type` VALUES (207117150,5,125950),(207117250,5,125950),(207117350,5,125950),(207117450,5,125950),(207117550,5,126100),(207117650,5,126100),(207117750,5,126100),(207117850,5,126100),(207117950,5,126100),(207118050,5,126100),(207118150,5,126200),(207118250,5,126200),(207118350,5,126200),(207118450,5,126400),(207118550,5,126400),(207118650,5,126450),(207118750,5,126550),(207118850,5,126550),(207118950,5,126550),(207119050,5,126550),(207119150,5,126550),(207119250,5,126550),(207119350,5,126550),(207119450,5,126550),(207119550,5,126550),(207119650,5,126550),(207119750,5,126550),(207119850,5,126550),(207119950,5,126550),(207120050,5,126550),(207120150,5,126550),(207120250,5,127200),(207120350,5,127400),(207120450,5,127500),(207120550,5,127500),(207120650,5,127500),(207120750,5,127500),(207120850,5,127500),(207120950,5,127500),(207121050,5,127500),(207121150,5,127500),(207121250,5,127500),(207121350,5,127500),(207121450,5,127500),(207121550,5,127500),(207121650,5,127500),(207121750,5,127500),(207121850,5,127500),(207121950,5,127750),(207122050,5,127750),(207122150,5,127750),(207122250,5,127750),(207122350,5,127800),(207122450,5,127800),(207122550,5,127800),(207122650,5,127800),(207122750,5,127800),(207122850,5,127800),(207122950,5,127800),(207123050,5,127800),(207123150,5,127800),(207123250,5,127800),(207123350,5,127800),(207123450,5,127800),(207123550,5,127800),(207123650,5,127800),(207123750,5,127800),(207123850,5,128300),(207123950,5,128300),(207124050,5,128300),(207124150,5,128300),(207124250,5,128300),(207124350,5,128300),(207124450,5,128300),(207124550,5,128300),(207124650,5,128300),(207124750,5,128300),(207124850,5,128300),(207124950,5,128300),(207125050,5,128300),(207125150,5,128300),(207125250,5,128300),(207125350,5,128450),(207237100,5,127800),(238437695,5,126500),(238437697,5,126500);
UNLOCK TABLES;
/*!40000 ALTER TABLE `failure_type` ENABLE KEYS */;

--
-- Table structure for table `failure_type_definition`
--

DROP TABLE IF EXISTS `failure_type_definition`;
CREATE TABLE `failure_type_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `failure_type_definition`
--


/*!40000 ALTER TABLE `failure_type_definition` DISABLE KEYS */;
LOCK TABLES `failure_type_definition` WRITE;
INSERT INTO `failure_type_definition` VALUES (125950,'F01','Leaked','LEAKED'),(126100,'F04','Lack of Power/No Drive','LACK OF POWER/NO DRIVE'),(126150,'F05','Bad Circuit/Poor Connection','BAD CIRCUIT/POOR CONNECTION'),(126200,'F06','Loose','LOOSE'),(126400,'F11','Missing or Incomplete','MISSING OR INCOMPLETE'),(126450,'F12','Adjustment, Out of','ADJUSTMENT, OUT OF'),(126500,'F13','Bent/Twisted','BENT/TWISTED'),(126550,'F14','Blown/Burst','BLOWN/BURST'),(126600,'F15','Broke/Cracked','BROKE/CRACKED'),(126650,'F16','Burned Out/Up','BURNED OUT/UP'),(126700,'F17','Cavitation','CAVITATION'),(126750,'F18','Collapsed','COLLAPSED'),(127200,'F27','Routing','ROUTING'),(127400,'F31','Misalignment','MISALIGNMENT'),(127500,'F33','Stuck/Seized','STUCK/SEIZED'),(127600,'F35','Undertorqued','UNDERTORQUED'),(127700,'F37','Worn','WORN'),(127750,'F38','Cross Threaded','CROSS THREADED'),(127800,'F39','Other','OTHER'),(128150,'F46','Torgue/Under','TORGUE/UNDER'),(128300,'F49','Oil Consumption','OIL CONSUMPTION'),(128450,'F52','Undersized','UNDERSIZED'),(128500,'F53','Run Out','RUN OUT'),(159700,'F67','Blow Hole','BLOW HOLE');
UNLOCK TABLES;
/*!40000 ALTER TABLE `failure_type_definition` ENABLE KEYS */;

--
-- Table structure for table `fault_code`
--

DROP TABLE IF EXISTS `fault_code`;
CREATE TABLE `fault_code` (
  `id` bigint(20) NOT NULL auto_increment,
  `tread_bucket` varchar(255) default NULL,
  `definition` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK199731EAE8AA271` (`definition`),
  KEY `FK199731EACAE598F` (`tread_bucket`),
  CONSTRAINT `FK199731EACAE598F` FOREIGN KEY (`tread_bucket`) REFERENCES `tread_bucket` (`code`),
  CONSTRAINT `FK199731EAE8AA271` FOREIGN KEY (`definition`) REFERENCES `fault_code_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `fault_code`
--


/*!40000 ALTER TABLE `fault_code` DISABLE KEYS */;
LOCK TABLES `fault_code` WRITE;
INSERT INTO `fault_code` VALUES (1,'01',1),(2,'02',2),(3,'03',3),(4,'04',4),(5,'05',5),(6,'06',6),(7,'07',1),(8,'08',2),(9,'09',3),(10,'10',4),(11,'11',5),(12,'12',6),(13,NULL,NULL),(14,NULL,NULL),(15,NULL,NULL),(16,NULL,NULL),(17,NULL,NULL),(18,NULL,NULL),(19,NULL,NULL),(20,NULL,NULL),(21,NULL,NULL),(22,NULL,NULL),(23,NULL,NULL),(24,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `fault_code` ENABLE KEYS */;

--
-- Table structure for table `fault_code_definition`
--

DROP TABLE IF EXISTS `fault_code_definition`;
CREATE TABLE `fault_code_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `fault_code_definition`
--


/*!40000 ALTER TABLE `fault_code_definition` DISABLE KEYS */;
LOCK TABLES `fault_code_definition` WRITE;
INSERT INTO `fault_code_definition` VALUES (1,'AC-107'),(2,'AC-115-107'),(3,'AK'),(4,'AD-113'),(5,'AN'),(6,'AN-111');
UNLOCK TABLES;
/*!40000 ALTER TABLE `fault_code_definition` ENABLE KEYS */;

--
-- Table structure for table `fault_code_definition_components`
--

DROP TABLE IF EXISTS `fault_code_definition_components`;
CREATE TABLE `fault_code_definition_components` (
  `fault_code_definition` bigint(20) NOT NULL,
  `components` bigint(20) NOT NULL,
  KEY `FK4281E8ED72BF8433` (`components`),
  KEY `FK4281E8ED2A7A4FE6` (`fault_code_definition`),
  CONSTRAINT `FK4281E8ED2A7A4FE6` FOREIGN KEY (`fault_code_definition`) REFERENCES `fault_code_definition` (`id`),
  CONSTRAINT `FK4281E8ED72BF8433` FOREIGN KEY (`components`) REFERENCES `assembly_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `fault_code_definition_components`
--


/*!40000 ALTER TABLE `fault_code_definition_components` DISABLE KEYS */;
LOCK TABLES `fault_code_definition_components` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `fault_code_definition_components` ENABLE KEYS */;

--
-- Table structure for table `inspection_result`
--

DROP TABLE IF EXISTS `inspection_result`;
CREATE TABLE `inspection_result` (
  `id` bigint(20) NOT NULL auto_increment,
  `accepted` bit(1) NOT NULL,
  `comments` varchar(255) default NULL,
  `failure_reason` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK44426D485A61F0D1` (`failure_reason`),
  CONSTRAINT `FK44426D485A61F0D1` FOREIGN KEY (`failure_reason`) REFERENCES `failure_reason` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `inspection_result`
--


/*!40000 ALTER TABLE `inspection_result` DISABLE KEYS */;
LOCK TABLES `inspection_result` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `inspection_result` ENABLE KEYS */;

--
-- Table structure for table `inventory_item`
--

DROP TABLE IF EXISTS `inventory_item`;
CREATE TABLE `inventory_item` (
  `id` bigint(20) NOT NULL,
  `built_on` date default NULL,
  `delivery_date` date default NULL,
  `hours_on_machine` int(11) default NULL,
  `registration_date` date default NULL,
  `serial_number` varchar(255) default NULL,
  `shipment_date` date default NULL,
  `type` varchar(255) default NULL,
  `condition_type` varchar(255) default NULL,
  `of_type` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_inventory_item` (`serial_number`,`of_type`,`condition_type`),
  KEY `FKFE019416FBCC41BF` (`of_type`),
  KEY `FKFE019416EF1C88D1` (`condition_type`),
  KEY `FKFE0194168C7E2469` (`type`),
  CONSTRAINT `FKFE0194168C7E2469` FOREIGN KEY (`type`) REFERENCES `inventory_type` (`type`),
  CONSTRAINT `FKFE019416EF1C88D1` FOREIGN KEY (`condition_type`) REFERENCES `inventory_item_condition` (`item_condition`),
  CONSTRAINT `FKFE019416FBCC41BF` FOREIGN KEY (`of_type`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `inventory_item`
--


/*!40000 ALTER TABLE `inventory_item` DISABLE KEYS */;
LOCK TABLES `inventory_item` WRITE;
INSERT INTO `inventory_item` VALUES (200,'2002-12-01','2003-01-01',1,NULL,'1234500','2002-12-25','STOCK','NEW',28),(201,'2002-12-01','2003-01-01',1,NULL,'1234501','2002-12-25','STOCK','NEW',55),(202,'2002-12-01','2003-01-01',1,NULL,'1234502','2002-12-25','STOCK','NEW',60),(203,'2002-12-01','2003-01-01',1,NULL,'1234503','2002-12-25','STOCK','NEW',60),(204,'2002-12-01','2003-01-01',1,NULL,'1234504','2002-12-25','STOCK','NEW',60),(205,'2002-12-01','2003-01-01',1,NULL,'1234505','2002-12-25','STOCK','NEW',60),(206,'2002-12-01','2003-01-01',1,NULL,'1234506','2002-12-25','STOCK','NEW',60),(207,'2002-12-01','2003-01-01',1,NULL,'1234507','2002-12-25','STOCK','NEW',60),(208,'2002-12-01','2003-01-01',1,NULL,'1234508','2002-12-25','STOCK','NEW',61),(209,'2002-12-01','2003-01-01',1,NULL,'1234509','2002-12-25','STOCK','NEW',77),(210,'2002-12-01','2003-01-01',1,NULL,'1234510','2002-12-25','STOCK','NEW',77),(211,'2002-12-01','2003-01-01',1,NULL,'1234511','2002-12-25','STOCK','NEW',77),(212,'2002-12-01','2003-01-01',1,NULL,'1234512','2002-12-25','STOCK','NEW',77),(213,'2002-12-01','2003-01-01',1,NULL,'1234513','2002-12-25','STOCK','NEW',77),(214,'2002-12-01','2003-01-01',1,NULL,'1234514','2002-12-25','STOCK','NEW',77),(215,'2002-12-01','2003-01-01',1,NULL,'1234515','2002-12-25','STOCK','NEW',77),(216,'2002-12-01','2003-01-01',1,NULL,'1234516','2002-12-25','STOCK','NEW',77),(217,'2002-12-01','2003-01-01',1,NULL,'1234517','2002-12-25','STOCK','NEW',77),(218,'2002-12-01','2003-01-01',1,NULL,'1234518','2002-12-25','STOCK','NEW',77),(219,'2002-12-01','2003-01-01',1,NULL,'1234519','2002-12-25','STOCK','NEW',77),(220,'2002-12-01','2003-01-01',1,NULL,'1234520','2002-12-25','STOCK','NEW',61),(221,'2002-12-01','2003-01-01',1,NULL,'1234521','2002-12-25','STOCK','NEW',77),(222,'2002-12-01','2003-01-01',1,NULL,'1234522','2002-12-25','STOCK','NEW',77),(223,'2002-12-01','2003-01-01',1,NULL,'1234523','2002-12-25','STOCK','NEW',77),(224,'2002-12-01','2003-01-01',1,NULL,'1234524','2002-12-25','STOCK','NEW',77),(225,'2002-12-01','2003-01-01',1,NULL,'1234525','2002-12-25','STOCK','NEW',77),(226,'2002-12-01','2003-01-01',1,NULL,'1234526','2002-12-25','STOCK','NEW',77),(227,'2002-12-01','2003-01-01',1,NULL,'1234527','2002-12-25','STOCK','NEW',77),(228,'2002-12-01','2003-01-01',1,NULL,'1234528','2002-12-25','STOCK','NEW',77),(229,'2002-12-01','2003-01-01',1,NULL,'1234529','2002-12-25','STOCK','NEW',77),(230,'2002-12-01','2003-01-01',1,NULL,'1234530','2002-12-25','STOCK','NEW',77),(231,'2002-12-01','2003-01-01',1,NULL,'1234531','2002-12-25','STOCK','NEW',77),(232,'2002-12-01','2003-01-01',1,NULL,'1234532','2002-12-25','STOCK','NEW',61),(233,'2002-12-01','2003-01-01',1,NULL,'1234533','2002-12-25','STOCK','NEW',77),(234,'2002-12-01','2003-01-01',1,NULL,'1234534','2002-12-25','STOCK','NEW',77),(235,'2002-12-01','2003-01-01',1,NULL,'1234535','2002-12-25','STOCK','NEW',77),(236,'2002-12-01','2003-01-01',1,NULL,'1234536','2002-12-25','STOCK','NEW',77),(237,'2002-12-01','2003-01-01',1,NULL,'1234537','2002-12-25','STOCK','NEW',77),(238,'2002-12-01','2003-01-01',1,NULL,'1234538','2002-12-25','STOCK','NEW',77),(239,'2002-12-01','2003-01-01',1,NULL,'1234539','2002-12-25','STOCK','NEW',77),(240,'2002-12-01','2003-01-01',1,NULL,'1234540','2002-12-25','STOCK','NEW',77),(241,'2002-12-01','2003-01-01',1,NULL,'1234541','2002-12-25','STOCK','NEW',77),(242,'2002-12-01','2003-01-01',1,NULL,'1234542','2002-12-25','STOCK','NEW',77),(243,'2002-12-01','2003-01-01',1,NULL,'1234543','2002-12-25','STOCK','NEW',77),(244,'2002-12-01','2003-01-01',1,NULL,'1234544','2002-12-25','STOCK','NEW',61),(245,'2002-12-01','2003-01-01',1,NULL,'1234545','2002-12-25','STOCK','NEW',77),(246,'2002-12-01','2003-01-01',1,NULL,'1234546','2002-12-25','STOCK','NEW',77),(247,'2002-12-01','2003-01-01',1,NULL,'1234547','2002-12-25','STOCK','NEW',77),(248,'2002-12-01','2003-01-01',1,NULL,'1234548','2002-12-25','STOCK','NEW',77),(249,'2002-12-01','2003-01-01',1,NULL,'1234549','2002-12-25','STOCK','NEW',77),(250,'2002-12-01','2003-01-01',1,NULL,'1234550','2002-12-25','STOCK','NEW',77),(251,'2002-12-01','2003-01-01',1,NULL,'1234551','2002-12-25','STOCK','NEW',77),(252,'2002-12-01','2003-01-01',1,NULL,'1234552','2002-12-25','STOCK','NEW',77),(253,'2002-12-01','2003-01-01',1,NULL,'1234553','2002-12-25','STOCK','NEW',77),(254,'2002-12-01','2003-01-01',1,NULL,'1234554','2002-12-25','STOCK','NEW',77),(255,'2002-12-01','2003-01-01',1,NULL,'1234555','2002-12-25','STOCK','NEW',77),(256,'2002-12-01','2003-01-01',1,NULL,'1234556','2002-12-25','STOCK','NEW',61),(257,'2002-12-01','2003-01-01',1,NULL,'1234557','2002-12-25','STOCK','NEW',77),(258,'2002-12-01','2003-01-01',1,NULL,'1234558','2002-12-25','STOCK','NEW',77),(259,'2002-12-01','2003-01-01',1,NULL,'1234559','2002-12-25','STOCK','NEW',77),(260,'2002-12-01','2003-01-01',1,NULL,'1234560','2002-12-25','STOCK','NEW',77),(261,'2002-12-01','2003-01-01',1,NULL,'1234561','2002-12-25','STOCK','NEW',77),(262,'2002-12-01','2003-01-01',1,NULL,'1234562','2002-12-25','STOCK','NEW',77),(263,'2002-12-01','2003-01-01',1,NULL,'1234563','2002-12-25','STOCK','NEW',77),(264,'2002-12-01','2003-01-01',1,NULL,'1234564','2002-12-25','STOCK','NEW',77),(265,'2002-12-01','2003-01-01',1,NULL,'1234565','2002-12-25','STOCK','NEW',77),(266,'2002-12-01','2003-01-01',1,NULL,'1234566','2002-12-25','STOCK','NEW',77),(267,'2002-12-01','2003-01-01',1,NULL,'1234567','2002-12-25','STOCK','NEW',77),(268,'2002-12-01','2003-01-01',1,NULL,'1234568','2002-12-25','STOCK','NEW',61),(269,'2002-12-01','2003-01-01',1,NULL,'1234569','2002-12-25','STOCK','NEW',77),(270,'2002-12-01','2003-01-01',1,NULL,'1234570','2002-12-25','STOCK','NEW',77),(271,'2002-12-01','2003-01-01',1,NULL,'1234571','2002-12-25','STOCK','NEW',77),(272,'2002-12-01','2003-01-01',1,NULL,'1234572','2002-12-25','STOCK','NEW',77),(273,'2002-12-01','2003-01-01',1,NULL,'1234573','2002-12-25','STOCK','NEW',77),(274,'2002-12-01','2003-01-01',1,NULL,'1234574','2002-12-25','STOCK','NEW',77),(275,'2002-12-01','2003-01-01',1,NULL,'1234575','2002-12-25','STOCK','NEW',77),(276,'2002-12-01','2003-01-01',1,NULL,'1234576','2002-12-25','STOCK','NEW',77),(277,'2002-12-01','2003-01-01',1,NULL,'1234577','2002-12-25','STOCK','NEW',77),(278,'2002-12-01','2003-01-01',1,NULL,'1234578','2002-12-25','STOCK','NEW',77),(279,'2002-12-01','2003-01-01',1,NULL,'1234579','2002-12-25','STOCK','NEW',77),(280,'2002-12-01','2003-01-01',1,NULL,'1234580','2002-12-25','STOCK','NEW',63),(281,'2002-12-01','2003-01-01',1,NULL,'1234581','2002-12-25','STOCK','NEW',63),(282,'2002-12-01','2003-01-01',1,NULL,'1234582','2002-12-25','STOCK','NEW',63),(283,'2002-12-01','2003-01-01',1,NULL,'1234583','2002-12-25','STOCK','NEW',63),(284,'2002-12-01','2003-01-01',1,NULL,'1234584','2002-12-25','STOCK','NEW',63),(285,'2002-12-01','2003-01-01',1,NULL,'1234585','2002-12-25','STOCK','NEW',63),(286,'2002-12-01','2003-01-01',1,NULL,'1234586','2002-12-25','STOCK','NEW',63),(287,'2002-12-01','2003-01-01',1,NULL,'1234587','2002-12-25','STOCK','NEW',63),(288,'2002-12-01','2003-01-01',1,NULL,'1234588','2002-12-25','STOCK','NEW',63),(289,'2002-12-01','2003-01-01',1,NULL,'1234589','2002-12-25','STOCK','NEW',63),(290,'2002-12-01','2003-01-01',1,NULL,'1234590','2002-12-25','STOCK','NEW',63),(291,'2002-12-01','2003-01-01',1,NULL,'1234591','2002-12-25','STOCK','NEW',62),(292,'2002-12-01','2003-01-01',1,NULL,'1234592','2002-12-25','STOCK','NEW',62),(293,'2002-12-01','2003-01-01',1,NULL,'1234593','2002-12-25','STOCK','NEW',62),(294,'2002-12-01','2003-01-01',1,NULL,'1234594','2002-12-25','STOCK','NEW',62),(295,'2002-12-01','2003-01-01',1,NULL,'1234595','2002-12-25','STOCK','NEW',62),(296,'2002-12-01','2003-01-01',1,NULL,'1234596','2002-12-25','STOCK','NEW',62),(297,'2002-12-01','2003-01-01',1,NULL,'1234597','2002-12-25','STOCK','NEW',62),(298,'2002-12-01','2003-01-01',1,NULL,'1234598','2002-12-25','STOCK','NEW',62),(299,'2002-12-01','2003-01-01',1,NULL,'1234599','2002-12-25','STOCK','NEW',62),(300,'2002-12-01','2003-01-01',1,NULL,'1234600','2002-12-25','STOCK','NEW',62),(301,'2002-12-01','2003-01-01',1,NULL,'1234601','2002-12-25','STOCK','NEW',62),(302,'2002-12-01','2003-01-01',1,NULL,'1234602','2002-12-25','STOCK','NEW',62),(303,'2002-12-01','2003-01-01',1,NULL,'1234603','2002-12-25','STOCK','NEW',62),(304,'2002-12-01','2003-01-01',1,NULL,'1234604','2002-12-25','STOCK','NEW',62),(305,'2002-12-01','2003-01-01',1,NULL,'1234605','2002-12-25','STOCK','NEW',62),(306,'2002-12-01','2003-01-01',1,NULL,'1234606','2002-12-25','STOCK','NEW',62),(307,'2002-12-01','2003-01-01',1,NULL,'1234607','2002-12-25','STOCK','NEW',62),(308,'2002-12-01','2003-01-01',1,NULL,'1234608','2002-12-25','STOCK','NEW',62),(309,'2002-12-01','2003-01-01',1,NULL,'1234609','2002-12-25','STOCK','NEW',62),(310,'2002-12-01','2003-01-01',1,NULL,'1234610','2002-12-25','STOCK','NEW',62),(311,'2002-12-01','2003-01-01',1,NULL,'1234611','2002-12-25','STOCK','NEW',62),(312,'2002-12-01','2003-01-01',1,NULL,'1234612','2002-12-25','STOCK','NEW',62),(313,'2002-12-01','2003-01-01',1,NULL,'1234613','2002-12-25','STOCK','NEW',62),(314,'2002-12-01','2003-01-01',1,NULL,'1234614','2002-12-25','STOCK','NEW',62),(315,'2002-12-01','2003-01-01',1,NULL,'1234615','2002-12-25','STOCK','NEW',62),(316,'2002-12-01','2003-01-01',1,NULL,'1234616','2002-12-25','STOCK','NEW',62),(317,'2002-12-01','2003-01-01',1,NULL,'1234617','2002-12-25','STOCK','NEW',62),(318,'2002-12-01','2003-01-01',1,NULL,'1234618','2002-12-25','STOCK','NEW',62),(319,'2002-12-01','2003-01-01',1,NULL,'1234619','2002-12-25','STOCK','NEW',62),(320,'2002-12-01','2003-01-01',1,NULL,'1234620','2002-12-25','STOCK','NEW',62),(321,'2002-12-01','2003-01-01',1,NULL,'1234621','2002-12-25','STOCK','NEW',62),(322,'2002-12-01','2003-01-01',1,NULL,'1234622','2002-12-25','STOCK','NEW',62),(323,'2002-12-01','2003-01-01',1,NULL,'1234623','2002-12-25','STOCK','NEW',62),(324,'2002-12-01','2003-01-01',1,NULL,'1234624','2002-12-25','STOCK','NEW',62),(325,'2002-12-01','2003-01-01',1,NULL,'1234625','2002-12-25','STOCK','NEW',62),(326,'2002-12-01','2003-01-01',1,NULL,'1234626','2002-12-25','STOCK','NEW',62),(327,'2002-12-01','2003-01-01',1,NULL,'1234627','2002-12-25','STOCK','NEW',62),(328,'2002-12-01','2003-01-01',1,NULL,'1234628','2002-12-25','STOCK','NEW',62),(329,'2002-12-01','2003-01-01',1,NULL,'1234629','2002-12-25','STOCK','NEW',62),(330,'2002-12-01','2003-01-01',1,NULL,'1234630','2002-12-25','STOCK','NEW',62),(331,'2002-12-01','2003-01-01',1,NULL,'1234631','2002-12-25','STOCK','NEW',62),(332,'2002-12-01','2003-01-01',1,NULL,'1234632','2002-12-25','STOCK','NEW',62),(333,'2002-12-01','2003-01-01',1,NULL,'1234633','2002-12-25','STOCK','NEW',62),(334,'2002-12-01','2003-01-01',1,NULL,'1234634','2002-12-25','STOCK','NEW',62),(335,'2002-12-01','2003-01-01',1,NULL,'1234635','2002-12-25','STOCK','NEW',62),(336,'2002-12-01','2003-01-01',1,NULL,'1234636','2002-12-25','STOCK','NEW',62),(337,'2002-12-01','2003-01-01',1,NULL,'1234637','2002-12-25','STOCK','NEW',62),(338,'2002-12-01','2003-01-01',1,NULL,'1234638','2002-12-25','STOCK','NEW',62),(339,'2002-12-01','2003-01-01',1,NULL,'1234639','2002-12-25','STOCK','NEW',62),(340,'2002-12-01','2003-01-01',1,NULL,'1234640','2002-12-25','STOCK','NEW',62),(341,'2002-12-01','2003-01-01',1,NULL,'1234641','2002-12-25','STOCK','NEW',62),(342,'2002-12-01','2003-01-01',1,NULL,'1234642','2002-12-25','STOCK','NEW',62),(343,'2002-12-01','2003-01-01',1,NULL,'1234643','2002-12-25','STOCK','NEW',62),(344,'2002-12-01','2003-01-01',1,NULL,'1234644','2002-12-25','STOCK','NEW',62),(345,'2002-12-01','2003-01-01',1,NULL,'1234645','2002-12-25','STOCK','NEW',62),(346,'2002-12-01','2003-01-01',1,NULL,'1234646','2002-12-25','STOCK','NEW',62),(347,'2002-12-01','2003-01-01',1,NULL,'1234647','2002-12-25','STOCK','NEW',62),(348,'2002-12-01','2003-01-01',1,NULL,'1234648','2002-12-25','STOCK','NEW',62),(349,'2002-12-01','2003-01-01',1,NULL,'1234649','2002-12-25','STOCK','NEW',62),(350,'2002-12-01','2003-01-01',1,NULL,'1234650','2002-12-25','STOCK','NEW',62),(351,'2002-12-01','2003-01-01',1,NULL,'1234651','2002-12-25','STOCK','NEW',62),(352,'2002-12-01','2003-01-01',1,NULL,'1234652','2002-12-25','STOCK','NEW',62),(353,'2002-12-01','2003-01-01',1,NULL,'1234653','2002-12-25','STOCK','NEW',62),(354,'2002-12-01','2003-01-01',1,NULL,'1234654','2002-12-25','STOCK','NEW',62),(355,'2002-12-01','2003-01-01',1,NULL,'1234655','2002-12-25','STOCK','NEW',62),(356,'2002-12-01','2003-01-01',1,NULL,'1234656','2002-12-25','STOCK','NEW',62),(357,'2002-12-01','2003-01-01',1,NULL,'1234657','2002-12-25','STOCK','NEW',57),(358,'2002-12-01','2003-01-01',1,NULL,'1234658','2002-12-25','STOCK','NEW',67),(359,'2002-12-01','2003-01-01',1,NULL,'1234659','2002-12-25','STOCK','NEW',67),(360,'2002-12-01','2003-01-01',1,NULL,'1234660','2002-12-25','STOCK','NEW',67),(361,'2002-12-01','2003-01-01',1,NULL,'1234661','2002-12-25','STOCK','NEW',67),(362,'2002-12-01','2003-01-01',1,NULL,'1234662','2002-12-25','STOCK','NEW',67),(363,'2002-12-01','2003-01-01',1,NULL,'1234663','2002-12-25','STOCK','NEW',67),(364,'2002-12-01','2003-01-01',1,NULL,'1234664','2002-12-25','STOCK','NEW',67),(365,'2002-12-01','2003-01-01',1,NULL,'1234665','2002-12-25','STOCK','NEW',67),(366,'2002-12-01','2003-01-01',1,NULL,'1234666','2002-12-25','STOCK','NEW',67),(367,'2002-12-01','2003-01-01',1,NULL,'1234667','2002-12-25','STOCK','NEW',67),(368,'2002-12-01','2003-01-01',1,NULL,'1234668','2002-12-25','STOCK','NEW',67),(369,'2002-12-01','2003-01-01',1,NULL,'1234669','2002-12-25','STOCK','NEW',69),(370,'2002-12-01','2003-01-01',1,NULL,'1234670','2002-12-25','STOCK','NEW',69),(371,'2002-12-01','2003-01-01',1,NULL,'1234671','2002-12-25','STOCK','NEW',69),(372,'2002-12-01','2003-01-01',1,NULL,'1234672','2002-12-25','STOCK','NEW',69),(373,'2002-12-01','2003-01-01',1,NULL,'1234673','2002-12-25','STOCK','NEW',69),(374,'2002-12-01','2003-01-01',1,NULL,'1234674','2002-12-25','STOCK','NEW',69),(375,'2002-12-01','2003-01-01',1,NULL,'1234675','2002-12-25','STOCK','NEW',69),(376,'2002-12-01','2003-01-01',1,NULL,'1234676','2002-12-25','STOCK','NEW',69),(377,'2002-12-01','2003-01-01',1,NULL,'1234677','2002-12-25','STOCK','NEW',69),(378,'2002-12-01','2003-01-01',1,NULL,'1234678','2002-12-25','STOCK','NEW',69),(379,'2002-12-01','2003-01-01',1,NULL,'1234679','2002-12-25','STOCK','NEW',69),(380,'2002-12-01','2003-01-01',1,NULL,'1234680','2002-12-25','STOCK','NEW',11),(381,'2002-12-01','2003-01-01',1,NULL,'1234681','2002-12-25','STOCK','NEW',11),(382,'2002-12-01','2003-01-01',1,NULL,'1234682','2002-12-25','STOCK','NEW',11),(383,'2002-12-01','2003-01-01',1,NULL,'1234683','2002-12-25','STOCK','NEW',11),(384,'2002-12-01','2003-01-01',1,NULL,'1234684','2002-12-25','STOCK','NEW',11),(385,'2002-12-01','2003-01-01',1,NULL,'1234685','2002-12-25','STOCK','NEW',11),(386,'2002-12-01','2003-01-01',1,NULL,'1234686','2002-12-25','STOCK','NEW',11),(387,'2002-12-01','2003-01-01',1,NULL,'1234687','2002-12-25','STOCK','NEW',11),(388,'2002-12-01','2003-01-01',1,NULL,'1234688','2002-12-25','STOCK','NEW',11),(389,'2002-12-01','2003-01-01',1,NULL,'1234689','2002-12-25','STOCK','NEW',11),(390,'2002-12-01','2003-01-01',1,NULL,'1234690','2002-12-25','STOCK','NEW',11),(391,'2002-12-01','2003-01-01',1,NULL,'1234691','2002-12-25','STOCK','NEW',56),(392,'2002-12-01','2003-01-01',1,NULL,'1234692','2002-12-25','STOCK','NEW',65),(393,'2002-12-01','2003-01-01',1,NULL,'1234693','2002-12-25','STOCK','NEW',65),(394,'2002-12-01','2003-01-01',1,NULL,'1234694','2002-12-25','STOCK','NEW',65),(395,'2002-12-01','2003-01-01',1,NULL,'1234695','2002-12-25','STOCK','NEW',65),(396,'2002-12-01','2003-01-01',1,NULL,'1234696','2002-12-25','STOCK','NEW',65),(397,'2002-12-01','2003-01-01',1,NULL,'1234697','2002-12-25','STOCK','NEW',65),(398,'2002-12-01','2003-01-01',1,NULL,'1234698','2002-12-25','STOCK','NEW',65),(399,'2002-12-01','2003-01-01',1,NULL,'1234699','2002-12-25','STOCK','NEW',65),(400,'2002-12-01','2003-01-01',1,NULL,'1234700','2002-12-25','STOCK','NEW',65),(401,'2002-12-01','2003-01-01',1,NULL,'1234701','2002-12-25','STOCK','NEW',65),(402,'2002-12-01','2003-01-01',1,NULL,'1234702','2002-12-25','STOCK','NEW',65),(403,'2002-12-01','2003-01-01',1,NULL,'1234703','2002-12-25','STOCK','NEW',64),(404,'2002-12-01','2003-01-01',1,NULL,'1234704','2002-12-25','STOCK','NEW',64),(405,'2002-12-01','2003-01-01',1,NULL,'1234705','2002-12-25','STOCK','NEW',64),(406,'2002-12-01','2003-01-01',1,NULL,'1234706','2002-12-25','STOCK','NEW',64),(407,'2002-12-01','2003-01-01',1,NULL,'1234707','2002-12-25','STOCK','NEW',64),(408,'2002-12-01','2003-01-01',1,NULL,'1234708','2002-12-25','STOCK','NEW',64),(409,'2002-12-01','2003-01-01',1,NULL,'1234709','2002-12-25','STOCK','NEW',64),(410,'2002-12-01','2003-01-01',1,NULL,'1234710','2002-12-25','STOCK','NEW',64),(411,'2002-12-01','2003-01-01',1,NULL,'1234711','2002-12-25','STOCK','NEW',64),(412,'2002-12-01','2003-01-01',1,NULL,'1234712','2002-12-25','STOCK','NEW',64),(413,'2002-12-01','2003-01-01',1,NULL,'1234713','2002-12-25','STOCK','NEW',64),(414,'2002-12-01','2003-01-01',1,NULL,'1234714','2002-12-25','STOCK','NEW',56),(415,'2002-12-01','2003-01-01',1,NULL,'1234715','2002-12-25','STOCK','NEW',65),(416,'2002-12-01','2003-01-01',1,NULL,'1234716','2002-12-25','STOCK','NEW',65),(417,'2002-12-01','2003-01-01',1,NULL,'1234717','2002-12-25','STOCK','NEW',65),(418,'2002-12-01','2003-01-01',1,NULL,'1234718','2002-12-25','STOCK','NEW',65),(419,'2002-12-01','2003-01-01',1,NULL,'1234719','2002-12-25','STOCK','NEW',65),(420,'2002-12-01','2003-01-01',1,NULL,'1234720','2002-12-25','STOCK','NEW',65),(421,'2002-12-01','2003-01-01',1,NULL,'1234721','2002-12-25','STOCK','NEW',65),(422,'2002-12-01','2003-01-01',1,NULL,'1234722','2002-12-25','STOCK','NEW',65),(423,'2002-12-01','2003-01-01',1,NULL,'1234723','2002-12-25','STOCK','NEW',65),(424,'2002-12-01','2003-01-01',1,NULL,'1234724','2002-12-25','STOCK','NEW',65),(425,'2002-12-01','2003-01-01',1,NULL,'1234725','2002-12-25','STOCK','NEW',65),(426,'2002-12-01','2003-01-01',1,NULL,'1234726','2002-12-25','STOCK','NEW',64),(427,'2002-12-01','2003-01-01',1,NULL,'1234727','2002-12-25','STOCK','NEW',64),(428,'2002-12-01','2003-01-01',1,NULL,'1234728','2002-12-25','STOCK','NEW',64),(429,'2002-12-01','2003-01-01',1,NULL,'1234729','2002-12-25','STOCK','NEW',64),(430,'2002-12-01','2003-01-01',1,NULL,'1234730','2002-12-25','STOCK','NEW',64),(431,'2002-12-01','2003-01-01',1,NULL,'1234731','2002-12-25','STOCK','NEW',64),(432,'2002-12-01','2003-01-01',1,NULL,'1234732','2002-12-25','STOCK','NEW',64),(433,'2002-12-01','2003-01-01',1,NULL,'1234733','2002-12-25','STOCK','NEW',64),(434,'2002-12-01','2003-01-01',1,NULL,'1234734','2002-12-25','STOCK','NEW',64),(435,'2002-12-01','2003-01-01',1,NULL,'1234735','2002-12-25','STOCK','NEW',64),(436,'2002-12-01','2003-01-01',1,NULL,'1234736','2002-12-25','STOCK','NEW',64),(437,'2002-12-01','2003-01-01',1,NULL,'1234737','2002-12-25','STOCK','NEW',6),(438,'2002-12-01','2003-01-01',1,NULL,'1234738','2002-12-25','STOCK','NEW',6),(439,'2002-12-01','2003-01-01',1,NULL,'1234739','2002-12-25','STOCK','NEW',6),(440,'2002-12-01','2003-01-01',1,NULL,'1234740','2002-12-25','STOCK','NEW',6),(441,'2002-12-01','2003-01-01',1,NULL,'1234741','2002-12-25','STOCK','NEW',6),(442,'2002-12-01','2003-01-01',1,NULL,'1234742','2002-12-25','STOCK','NEW',6),(443,'2002-12-01','2003-01-01',1,NULL,'1234743','2002-12-25','STOCK','NEW',6),(444,'2002-12-01','2003-01-01',1,NULL,'1234744','2002-12-25','STOCK','NEW',6),(445,'2002-12-01','2003-01-01',1,NULL,'1234745','2002-12-25','STOCK','NEW',6),(446,'2002-12-01','2003-01-01',1,NULL,'1234746','2002-12-25','STOCK','NEW',6),(447,'2002-12-01','2003-01-01',1,NULL,'1234747','2002-12-25','STOCK','NEW',6),(448,'2002-12-01','2003-01-01',1,NULL,'1234748','2002-12-25','STOCK','NEW',1),(449,'2002-12-01','2003-01-01',1,NULL,'1234749','2002-12-25','STOCK','NEW',1),(450,'2002-12-01','2003-01-01',1,NULL,'1234750','2002-12-25','STOCK','NEW',1),(451,'2002-12-01','2003-01-01',1,NULL,'1234751','2002-12-25','STOCK','NEW',1),(452,'2002-12-01','2003-01-01',1,NULL,'1234752','2002-12-25','STOCK','NEW',1),(453,'2002-12-01','2003-01-01',1,NULL,'1234753','2002-12-25','STOCK','NEW',1),(454,'2002-12-01','2003-01-01',1,NULL,'1234754','2002-12-25','STOCK','NEW',1),(455,'2002-12-01','2003-01-01',1,NULL,'1234755','2002-12-25','STOCK','NEW',1),(456,'2002-12-01','2003-01-01',1,NULL,'1234756','2002-12-25','STOCK','NEW',1),(457,'2002-12-01','2003-01-01',1,NULL,'1234757','2002-12-25','STOCK','NEW',1),(458,'2002-12-01','2003-01-01',1,NULL,'1234758','2002-12-25','STOCK','NEW',1),(459,'2002-12-01','2003-01-01',1,NULL,'1234759','2002-12-25','STOCK','NEW',29),(460,'2002-12-01','2003-01-01',1,NULL,'1234760','2002-12-25','STOCK','NEW',56),(461,'2002-12-01','2003-01-01',1,NULL,'1234761','2002-12-25','STOCK','NEW',65),(462,'2002-12-01','2003-01-01',1,NULL,'1234762','2002-12-25','STOCK','NEW',65),(463,'2002-12-01','2003-01-01',1,NULL,'1234763','2002-12-25','STOCK','NEW',65),(464,'2002-12-01','2003-01-01',1,NULL,'1234764','2002-12-25','STOCK','NEW',65),(465,'2002-12-01','2003-01-01',1,NULL,'1234765','2002-12-25','STOCK','NEW',65),(466,'2002-12-01','2003-01-01',1,NULL,'1234766','2002-12-25','STOCK','NEW',65),(467,'2002-12-01','2003-01-01',1,NULL,'1234767','2002-12-25','STOCK','NEW',65),(468,'2002-12-01','2003-01-01',1,NULL,'1234768','2002-12-25','STOCK','NEW',65),(469,'2002-12-01','2003-01-01',1,NULL,'1234769','2002-12-25','STOCK','NEW',65),(470,'2002-12-01','2003-01-01',1,NULL,'1234770','2002-12-25','STOCK','NEW',65),(471,'2002-12-01','2003-01-01',1,NULL,'1234771','2002-12-25','STOCK','NEW',65),(472,'2002-12-01','2003-01-01',1,NULL,'1234772','2002-12-25','STOCK','NEW',64),(473,'2002-12-01','2003-01-01',1,NULL,'1234773','2002-12-25','STOCK','NEW',64),(474,'2002-12-01','2003-01-01',1,NULL,'1234774','2002-12-25','STOCK','NEW',64),(475,'2002-12-01','2003-01-01',1,NULL,'1234775','2002-12-25','STOCK','NEW',64),(476,'2002-12-01','2003-01-01',1,NULL,'1234776','2002-12-25','STOCK','NEW',64),(477,'2002-12-01','2003-01-01',1,NULL,'1234777','2002-12-25','STOCK','NEW',64),(478,'2002-12-01','2003-01-01',1,NULL,'1234778','2002-12-25','STOCK','NEW',64),(479,'2002-12-01','2003-01-01',1,NULL,'1234779','2002-12-25','STOCK','NEW',64),(480,'2002-12-01','2003-01-01',1,NULL,'1234780','2002-12-25','STOCK','NEW',64),(481,'2002-12-01','2003-01-01',1,NULL,'1234781','2002-12-25','STOCK','NEW',64),(482,'2002-12-01','2003-01-01',1,NULL,'1234782','2002-12-25','STOCK','NEW',64),(483,'2002-12-01','2003-01-01',1,NULL,'1234783','2002-12-25','STOCK','NEW',6),(484,'2002-12-01','2003-01-01',1,NULL,'1234784','2002-12-25','STOCK','NEW',6),(485,'2002-12-01','2003-01-01',1,NULL,'1234785','2002-12-25','STOCK','NEW',6),(486,'2002-12-01','2003-01-01',1,NULL,'1234786','2002-12-25','STOCK','NEW',6),(487,'2002-12-01','2003-01-01',1,NULL,'1234787','2002-12-25','STOCK','NEW',6),(488,'2002-12-01','2003-01-01',1,NULL,'1234788','2002-12-25','STOCK','NEW',6),(489,'2002-12-01','2003-01-01',1,NULL,'1234789','2002-12-25','STOCK','NEW',6),(490,'2002-12-01','2003-01-01',1,NULL,'1234790','2002-12-25','STOCK','NEW',6),(491,'2002-12-01','2003-01-01',1,NULL,'1234791','2002-12-25','STOCK','NEW',6),(492,'2002-12-01','2003-01-01',1,NULL,'1234792','2002-12-25','STOCK','NEW',6),(493,'2002-12-01','2003-01-01',1,NULL,'1234793','2002-12-25','STOCK','NEW',6),(494,'2002-12-01','2003-01-01',1,NULL,'1234794','2002-12-25','STOCK','NEW',1),(495,'2002-12-01','2003-01-01',1,NULL,'1234795','2002-12-25','STOCK','NEW',1),(496,'2002-12-01','2003-01-01',1,NULL,'1234796','2002-12-25','STOCK','NEW',1),(497,'2002-12-01','2003-01-01',1,NULL,'1234797','2002-12-25','STOCK','NEW',1),(498,'2002-12-01','2003-01-01',1,NULL,'1234798','2002-12-25','STOCK','NEW',1),(499,'2002-12-01','2003-01-01',1,NULL,'1234799','2002-12-25','STOCK','NEW',1),(500,'2002-12-01','2003-01-01',1,NULL,'1234800','2002-12-25','STOCK','NEW',1),(501,'2002-12-01','2003-01-01',1,NULL,'1234801','2002-12-25','STOCK','NEW',1),(502,'2002-12-01','2003-01-01',1,NULL,'1234802','2002-12-25','STOCK','NEW',1),(503,'2002-12-01','2003-01-01',1,NULL,'1234803','2002-12-25','STOCK','NEW',1),(504,'2002-12-01','2003-01-01',1,NULL,'1234804','2002-12-25','STOCK','NEW',1),(505,'2002-12-01','2003-01-01',1,NULL,'1234805','2002-12-25','STOCK','NEW',58),(506,'2002-12-01','2003-01-01',1,NULL,'1234806','2002-12-25','STOCK','NEW',58),(507,'2002-12-01','2003-01-01',1,NULL,'1234807','2002-12-25','STOCK','NEW',58),(508,'2002-12-01','2003-01-01',1,NULL,'1234808','2002-12-25','STOCK','NEW',58),(509,'2002-12-01','2003-01-01',1,NULL,'1234809','2002-12-25','STOCK','NEW',58),(510,'2002-12-01','2003-01-01',1,NULL,'1234810','2002-12-25','STOCK','NEW',58),(511,'2002-12-01','2003-01-01',1,NULL,'1234811','2002-12-25','STOCK','NEW',58),(512,'2002-12-01','2003-01-01',1,NULL,'1234812','2002-12-25','STOCK','NEW',58),(513,'2002-12-01','2003-01-01',1,NULL,'1234813','2002-12-25','STOCK','NEW',58),(514,'2002-12-01','2003-01-01',1,NULL,'1234814','2002-12-25','STOCK','NEW',58),(515,'2002-12-01','2003-01-01',1,NULL,'1234815','2002-12-25','STOCK','NEW',58),(516,'2002-12-01','2003-01-01',1,NULL,'1234816','2002-12-25','STOCK','NEW',55),(517,'2002-12-01','2003-01-01',1,NULL,'1234817','2002-12-25','STOCK','NEW',60),(518,'2002-12-01','2003-01-01',1,NULL,'1234818','2002-12-25','STOCK','NEW',60),(519,'2002-12-01','2003-01-01',1,NULL,'1234819','2002-12-25','STOCK','NEW',60),(520,'2002-12-01','2003-01-01',1,NULL,'1234820','2002-12-25','STOCK','NEW',60),(521,'2002-12-01','2003-01-01',1,NULL,'1234821','2002-12-25','STOCK','NEW',60),(522,'2002-12-01','2003-01-01',1,NULL,'1234822','2002-12-25','STOCK','NEW',60),(523,'2002-12-01','2003-01-01',1,NULL,'1234823','2002-12-25','STOCK','NEW',61),(524,'2002-12-01','2003-01-01',1,NULL,'1234824','2002-12-25','STOCK','NEW',77),(525,'2002-12-01','2003-01-01',1,NULL,'1234825','2002-12-25','STOCK','NEW',77),(526,'2002-12-01','2003-01-01',1,NULL,'1234826','2002-12-25','STOCK','NEW',77),(527,'2002-12-01','2003-01-01',1,NULL,'1234827','2002-12-25','STOCK','NEW',77),(528,'2002-12-01','2003-01-01',1,NULL,'1234828','2002-12-25','STOCK','NEW',77),(529,'2002-12-01','2003-01-01',1,NULL,'1234829','2002-12-25','STOCK','NEW',77),(530,'2002-12-01','2003-01-01',1,NULL,'1234830','2002-12-25','STOCK','NEW',77),(531,'2002-12-01','2003-01-01',1,NULL,'1234831','2002-12-25','STOCK','NEW',77),(532,'2002-12-01','2003-01-01',1,NULL,'1234832','2002-12-25','STOCK','NEW',77),(533,'2002-12-01','2003-01-01',1,NULL,'1234833','2002-12-25','STOCK','NEW',77),(534,'2002-12-01','2003-01-01',1,NULL,'1234834','2002-12-25','STOCK','NEW',77),(535,'2002-12-01','2003-01-01',1,NULL,'1234835','2002-12-25','STOCK','NEW',61),(536,'2002-12-01','2003-01-01',1,NULL,'1234836','2002-12-25','STOCK','NEW',77),(537,'2002-12-01','2003-01-01',1,NULL,'1234837','2002-12-25','STOCK','NEW',77),(538,'2002-12-01','2003-01-01',1,NULL,'1234838','2002-12-25','STOCK','NEW',77),(539,'2002-12-01','2003-01-01',1,NULL,'1234839','2002-12-25','STOCK','NEW',77),(540,'2002-12-01','2003-01-01',1,NULL,'1234840','2002-12-25','STOCK','NEW',77),(541,'2002-12-01','2003-01-01',1,NULL,'1234841','2002-12-25','STOCK','NEW',77),(542,'2002-12-01','2003-01-01',1,NULL,'1234842','2002-12-25','STOCK','NEW',77),(543,'2002-12-01','2003-01-01',1,NULL,'1234843','2002-12-25','STOCK','NEW',77),(544,'2002-12-01','2003-01-01',1,NULL,'1234844','2002-12-25','STOCK','NEW',77),(545,'2002-12-01','2003-01-01',1,NULL,'1234845','2002-12-25','STOCK','NEW',77),(546,'2002-12-01','2003-01-01',1,NULL,'1234846','2002-12-25','STOCK','NEW',77),(547,'2002-12-01','2003-01-01',1,NULL,'1234847','2002-12-25','STOCK','NEW',61),(548,'2002-12-01','2003-01-01',1,NULL,'1234848','2002-12-25','STOCK','NEW',77),(549,'2002-12-01','2003-01-01',1,NULL,'1234849','2002-12-25','STOCK','NEW',77),(550,'2002-12-01','2003-01-01',1,NULL,'1234850','2002-12-25','STOCK','NEW',77),(551,'2002-12-01','2003-01-01',1,NULL,'1234851','2002-12-25','STOCK','NEW',77),(552,'2002-12-01','2003-01-01',1,NULL,'1234852','2002-12-25','STOCK','NEW',77),(553,'2002-12-01','2003-01-01',1,NULL,'1234853','2002-12-25','STOCK','NEW',77),(554,'2002-12-01','2003-01-01',1,NULL,'1234854','2002-12-25','STOCK','NEW',77),(555,'2002-12-01','2003-01-01',1,NULL,'1234855','2002-12-25','STOCK','NEW',77),(556,'2002-12-01','2003-01-01',1,NULL,'1234856','2002-12-25','STOCK','NEW',77),(557,'2002-12-01','2003-01-01',1,NULL,'1234857','2002-12-25','STOCK','NEW',77),(558,'2002-12-01','2003-01-01',1,NULL,'1234858','2002-12-25','STOCK','NEW',77),(559,'2002-12-01','2003-01-01',1,NULL,'1234859','2002-12-25','STOCK','NEW',61),(560,'2002-12-01','2003-01-01',1,NULL,'1234860','2002-12-25','STOCK','NEW',77),(561,'2002-12-01','2003-01-01',1,NULL,'1234861','2002-12-25','STOCK','NEW',77),(562,'2002-12-01','2003-01-01',1,NULL,'1234862','2002-12-25','STOCK','NEW',77),(563,'2002-12-01','2003-01-01',1,NULL,'1234863','2002-12-25','STOCK','NEW',77),(564,'2002-12-01','2003-01-01',1,NULL,'1234864','2002-12-25','STOCK','NEW',77),(565,'2002-12-01','2003-01-01',1,NULL,'1234865','2002-12-25','STOCK','NEW',77),(566,'2002-12-01','2003-01-01',1,NULL,'1234866','2002-12-25','STOCK','NEW',77),(567,'2002-12-01','2003-01-01',1,NULL,'1234867','2002-12-25','STOCK','NEW',77),(568,'2002-12-01','2003-01-01',1,NULL,'1234868','2002-12-25','STOCK','NEW',77),(569,'2002-12-01','2003-01-01',1,NULL,'1234869','2002-12-25','STOCK','NEW',77),(570,'2002-12-01','2003-01-01',1,NULL,'1234870','2002-12-25','STOCK','NEW',77),(571,'2002-12-01','2003-01-01',1,NULL,'1234871','2002-12-25','STOCK','NEW',61),(572,'2002-12-01','2003-01-01',1,NULL,'1234872','2002-12-25','STOCK','NEW',77),(573,'2002-12-01','2003-01-01',1,NULL,'1234873','2002-12-25','STOCK','NEW',77),(574,'2002-12-01','2003-01-01',1,NULL,'1234874','2002-12-25','STOCK','NEW',77),(575,'2002-12-01','2003-01-01',1,NULL,'1234875','2002-12-25','STOCK','NEW',77),(576,'2002-12-01','2003-01-01',1,NULL,'1234876','2002-12-25','STOCK','NEW',77),(577,'2002-12-01','2003-01-01',1,NULL,'1234877','2002-12-25','STOCK','NEW',77),(578,'2002-12-01','2003-01-01',1,NULL,'1234878','2002-12-25','STOCK','NEW',77),(579,'2002-12-01','2003-01-01',1,NULL,'1234879','2002-12-25','STOCK','NEW',77),(580,'2002-12-01','2003-01-01',1,NULL,'1234880','2002-12-25','STOCK','NEW',77),(581,'2002-12-01','2003-01-01',1,NULL,'1234881','2002-12-25','STOCK','NEW',77),(582,'2002-12-01','2003-01-01',1,NULL,'1234882','2002-12-25','STOCK','NEW',77),(583,'2002-12-01','2003-01-01',1,NULL,'1234883','2002-12-25','STOCK','NEW',61),(584,'2002-12-01','2003-01-01',1,NULL,'1234884','2002-12-25','STOCK','NEW',77),(585,'2002-12-01','2003-01-01',1,NULL,'1234885','2002-12-25','STOCK','NEW',77),(586,'2002-12-01','2003-01-01',1,NULL,'1234886','2002-12-25','STOCK','NEW',77),(587,'2002-12-01','2003-01-01',1,NULL,'1234887','2002-12-25','STOCK','NEW',77),(588,'2002-12-01','2003-01-01',1,NULL,'1234888','2002-12-25','STOCK','NEW',77),(589,'2002-12-01','2003-01-01',1,NULL,'1234889','2002-12-25','STOCK','NEW',77),(590,'2002-12-01','2003-01-01',1,NULL,'1234890','2002-12-25','STOCK','NEW',77),(591,'2002-12-01','2003-01-01',1,NULL,'1234891','2002-12-25','STOCK','NEW',77),(592,'2002-12-01','2003-01-01',1,NULL,'1234892','2002-12-25','STOCK','NEW',77),(593,'2002-12-01','2003-01-01',1,NULL,'1234893','2002-12-25','STOCK','NEW',77),(594,'2002-12-01','2003-01-01',1,NULL,'1234894','2002-12-25','STOCK','NEW',77),(595,'2002-12-01','2003-01-01',1,NULL,'1234895','2002-12-25','STOCK','NEW',63),(596,'2002-12-01','2003-01-01',1,NULL,'1234896','2002-12-25','STOCK','NEW',63),(597,'2002-12-01','2003-01-01',1,NULL,'1234897','2002-12-25','STOCK','NEW',63),(598,'2002-12-01','2003-01-01',1,NULL,'1234898','2002-12-25','STOCK','NEW',63),(599,'2002-12-01','2003-01-01',1,NULL,'1234899','2002-12-25','STOCK','NEW',63),(600,'2002-12-01','2003-01-01',1,NULL,'1234900','2002-12-25','STOCK','NEW',63),(601,'2002-12-01','2003-01-01',1,NULL,'1234901','2002-12-25','STOCK','NEW',63),(602,'2002-12-01','2003-01-01',1,NULL,'1234902','2002-12-25','STOCK','NEW',63),(603,'2002-12-01','2003-01-01',1,NULL,'1234903','2002-12-25','STOCK','NEW',63),(604,'2002-12-01','2003-01-01',1,NULL,'1234904','2002-12-25','STOCK','NEW',63),(605,'2002-12-01','2003-01-01',1,NULL,'1234905','2002-12-25','STOCK','NEW',63),(606,'2002-12-01','2003-01-01',1,NULL,'1234906','2002-12-25','STOCK','NEW',62),(607,'2002-12-01','2003-01-01',1,NULL,'1234907','2002-12-25','STOCK','NEW',62),(608,'2002-12-01','2003-01-01',1,NULL,'1234908','2002-12-25','STOCK','NEW',62),(609,'2002-12-01','2003-01-01',1,NULL,'1234909','2002-12-25','STOCK','NEW',62),(610,'2002-12-01','2003-01-01',1,NULL,'1234910','2002-12-25','STOCK','NEW',62),(611,'2002-12-01','2003-01-01',1,NULL,'1234911','2002-12-25','STOCK','NEW',62),(612,'2002-12-01','2003-01-01',1,NULL,'1234912','2002-12-25','STOCK','NEW',62),(613,'2002-12-01','2003-01-01',1,NULL,'1234913','2002-12-25','STOCK','NEW',62),(614,'2002-12-01','2003-01-01',1,NULL,'1234914','2002-12-25','STOCK','NEW',62),(615,'2002-12-01','2003-01-01',1,NULL,'1234915','2002-12-25','STOCK','NEW',62),(616,'2002-12-01','2003-01-01',1,NULL,'1234916','2002-12-25','STOCK','NEW',62),(617,'2002-12-01','2003-01-01',1,NULL,'1234917','2002-12-25','STOCK','NEW',62),(618,'2002-12-01','2003-01-01',1,NULL,'1234918','2002-12-25','STOCK','NEW',62),(619,'2002-12-01','2003-01-01',1,NULL,'1234919','2002-12-25','STOCK','NEW',62),(620,'2002-12-01','2003-01-01',1,NULL,'1234920','2002-12-25','STOCK','NEW',62),(621,'2002-12-01','2003-01-01',1,NULL,'1234921','2002-12-25','STOCK','NEW',62),(622,'2002-12-01','2003-01-01',1,NULL,'1234922','2002-12-25','STOCK','NEW',62),(623,'2002-12-01','2003-01-01',1,NULL,'1234923','2002-12-25','STOCK','NEW',62),(624,'2002-12-01','2003-01-01',1,NULL,'1234924','2002-12-25','STOCK','NEW',62),(625,'2002-12-01','2003-01-01',1,NULL,'1234925','2002-12-25','STOCK','NEW',62),(626,'2002-12-01','2003-01-01',1,NULL,'1234926','2002-12-25','STOCK','NEW',62),(627,'2002-12-01','2003-01-01',1,NULL,'1234927','2002-12-25','STOCK','NEW',62),(628,'2002-12-01','2003-01-01',1,NULL,'1234928','2002-12-25','STOCK','NEW',62),(629,'2002-12-01','2003-01-01',1,NULL,'1234929','2002-12-25','STOCK','NEW',62),(630,'2002-12-01','2003-01-01',1,NULL,'1234930','2002-12-25','STOCK','NEW',62),(631,'2002-12-01','2003-01-01',1,NULL,'1234931','2002-12-25','STOCK','NEW',62),(632,'2002-12-01','2003-01-01',1,NULL,'1234932','2002-12-25','STOCK','NEW',62),(633,'2002-12-01','2003-01-01',1,NULL,'1234933','2002-12-25','STOCK','NEW',62),(634,'2002-12-01','2003-01-01',1,NULL,'1234934','2002-12-25','STOCK','NEW',62),(635,'2002-12-01','2003-01-01',1,NULL,'1234935','2002-12-25','STOCK','NEW',62),(636,'2002-12-01','2003-01-01',1,NULL,'1234936','2002-12-25','STOCK','NEW',62),(637,'2002-12-01','2003-01-01',1,NULL,'1234937','2002-12-25','STOCK','NEW',62),(638,'2002-12-01','2003-01-01',1,NULL,'1234938','2002-12-25','STOCK','NEW',62),(639,'2002-12-01','2003-01-01',1,NULL,'1234939','2002-12-25','STOCK','NEW',62),(640,'2002-12-01','2003-01-01',1,NULL,'1234940','2002-12-25','STOCK','NEW',62),(641,'2002-12-01','2003-01-01',1,NULL,'1234941','2002-12-25','STOCK','NEW',62),(642,'2002-12-01','2003-01-01',1,NULL,'1234942','2002-12-25','STOCK','NEW',62),(643,'2002-12-01','2003-01-01',1,NULL,'1234943','2002-12-25','STOCK','NEW',62),(644,'2002-12-01','2003-01-01',1,NULL,'1234944','2002-12-25','STOCK','NEW',62),(645,'2002-12-01','2003-01-01',1,NULL,'1234945','2002-12-25','STOCK','NEW',62),(646,'2002-12-01','2003-01-01',1,NULL,'1234946','2002-12-25','STOCK','NEW',62),(647,'2002-12-01','2003-01-01',1,NULL,'1234947','2002-12-25','STOCK','NEW',62),(648,'2002-12-01','2003-01-01',1,NULL,'1234948','2002-12-25','STOCK','NEW',62),(649,'2002-12-01','2003-01-01',1,NULL,'1234949','2002-12-25','STOCK','NEW',62),(650,'2002-12-01','2003-01-01',1,NULL,'1234950','2002-12-25','STOCK','NEW',62),(651,'2002-12-01','2003-01-01',1,NULL,'1234951','2002-12-25','STOCK','NEW',62),(652,'2002-12-01','2003-01-01',1,NULL,'1234952','2002-12-25','STOCK','NEW',62),(653,'2002-12-01','2003-01-01',1,NULL,'1234953','2002-12-25','STOCK','NEW',62),(654,'2002-12-01','2003-01-01',1,NULL,'1234954','2002-12-25','STOCK','NEW',62),(655,'2002-12-01','2003-01-01',1,NULL,'1234955','2002-12-25','STOCK','NEW',62),(656,'2002-12-01','2003-01-01',1,NULL,'1234956','2002-12-25','STOCK','NEW',62),(657,'2002-12-01','2003-01-01',1,NULL,'1234957','2002-12-25','STOCK','NEW',62),(658,'2002-12-01','2003-01-01',1,NULL,'1234958','2002-12-25','STOCK','NEW',62),(659,'2002-12-01','2003-01-01',1,NULL,'1234959','2002-12-25','STOCK','NEW',62),(660,'2002-12-01','2003-01-01',1,NULL,'1234960','2002-12-25','STOCK','NEW',62),(661,'2002-12-01','2003-01-01',1,NULL,'1234961','2002-12-25','STOCK','NEW',62),(662,'2002-12-01','2003-01-01',1,NULL,'1234962','2002-12-25','STOCK','NEW',62),(663,'2002-12-01','2003-01-01',1,NULL,'1234963','2002-12-25','STOCK','NEW',62),(664,'2002-12-01','2003-01-01',1,NULL,'1234964','2002-12-25','STOCK','NEW',62),(665,'2002-12-01','2003-01-01',1,NULL,'1234965','2002-12-25','STOCK','NEW',62),(666,'2002-12-01','2003-01-01',1,NULL,'1234966','2002-12-25','STOCK','NEW',62),(667,'2002-12-01','2003-01-01',1,NULL,'1234967','2002-12-25','STOCK','NEW',62),(668,'2002-12-01','2003-01-01',1,NULL,'1234968','2002-12-25','STOCK','NEW',62),(669,'2002-12-01','2003-01-01',1,NULL,'1234969','2002-12-25','STOCK','NEW',62),(670,'2002-12-01','2003-01-01',1,NULL,'1234970','2002-12-25','STOCK','NEW',62),(671,'2002-12-01','2003-01-01',1,NULL,'1234971','2002-12-25','STOCK','NEW',62),(672,'2002-12-01','2003-01-01',1,NULL,'1234972','2002-12-25','STOCK','NEW',57),(673,'2002-12-01','2003-01-01',1,NULL,'1234973','2002-12-25','STOCK','NEW',67),(674,'2002-12-01','2003-01-01',1,NULL,'1234974','2002-12-25','STOCK','NEW',67),(675,'2002-12-01','2003-01-01',1,NULL,'1234975','2002-12-25','STOCK','NEW',67),(676,'2002-12-01','2003-01-01',1,NULL,'1234976','2002-12-25','STOCK','NEW',67),(677,'2002-12-01','2003-01-01',1,NULL,'1234977','2002-12-25','STOCK','NEW',67),(678,'2002-12-01','2003-01-01',1,NULL,'1234978','2002-12-25','STOCK','NEW',67),(679,'2002-12-01','2003-01-01',1,NULL,'1234979','2002-12-25','STOCK','NEW',67),(680,'2002-12-01','2003-01-01',1,NULL,'1234980','2002-12-25','STOCK','NEW',67),(681,'2002-12-01','2003-01-01',1,NULL,'1234981','2002-12-25','STOCK','NEW',67),(682,'2002-12-01','2003-01-01',1,NULL,'1234982','2002-12-25','STOCK','NEW',67),(683,'2002-12-01','2003-01-01',1,NULL,'1234983','2002-12-25','STOCK','NEW',67),(684,'2002-12-01','2003-01-01',1,NULL,'1234984','2002-12-25','STOCK','NEW',69),(685,'2002-12-01','2003-01-01',1,NULL,'1234985','2002-12-25','STOCK','NEW',69),(686,'2002-12-01','2003-01-01',1,NULL,'1234986','2002-12-25','STOCK','NEW',69),(687,'2002-12-01','2003-01-01',1,NULL,'1234987','2002-12-25','STOCK','NEW',69),(688,'2002-12-01','2003-01-01',1,NULL,'1234988','2002-12-25','STOCK','NEW',69),(689,'2002-12-01','2003-01-01',1,NULL,'1234989','2002-12-25','STOCK','NEW',69),(690,'2002-12-01','2003-01-01',1,NULL,'1234990','2002-12-25','STOCK','NEW',69),(691,'2002-12-01','2003-01-01',1,NULL,'1234991','2002-12-25','STOCK','NEW',69),(692,'2002-12-01','2003-01-01',1,NULL,'1234992','2002-12-25','STOCK','NEW',69),(693,'2002-12-01','2003-01-01',1,NULL,'1234993','2002-12-25','STOCK','NEW',69),(694,'2002-12-01','2003-01-01',1,NULL,'1234994','2002-12-25','STOCK','NEW',69),(695,'2002-12-01','2003-01-01',1,NULL,'1234995','2002-12-25','STOCK','NEW',57),(696,'2002-12-01','2003-01-01',1,NULL,'1234996','2002-12-25','STOCK','NEW',67),(697,'2002-12-01','2003-01-01',1,NULL,'1234997','2002-12-25','STOCK','NEW',67),(698,'2002-12-01','2003-01-01',1,NULL,'1234998','2002-12-25','STOCK','NEW',67),(699,'2002-12-01','2003-01-01',1,NULL,'1234999','2002-12-25','STOCK','NEW',67),(700,'2002-12-01','2003-01-01',1,NULL,'1235000','2002-12-25','STOCK','NEW',67),(701,'2002-12-01','2003-01-01',1,NULL,'1235001','2002-12-25','STOCK','NEW',67),(702,'2002-12-01','2003-01-01',1,NULL,'1235002','2002-12-25','STOCK','NEW',67),(703,'2002-12-01','2003-01-01',1,NULL,'1235003','2002-12-25','STOCK','NEW',67),(704,'2002-12-01','2003-01-01',1,NULL,'1235004','2002-12-25','STOCK','NEW',67),(705,'2002-12-01','2003-01-01',1,NULL,'1235005','2002-12-25','STOCK','NEW',67),(706,'2002-12-01','2003-01-01',1,NULL,'1235006','2002-12-25','STOCK','NEW',67),(707,'2002-12-01','2003-01-01',1,NULL,'1235007','2002-12-25','STOCK','NEW',69),(708,'2002-12-01','2003-01-01',1,NULL,'1235008','2002-12-25','STOCK','NEW',69),(709,'2002-12-01','2003-01-01',1,NULL,'1235009','2002-12-25','STOCK','NEW',69),(710,'2002-12-01','2003-01-01',1,NULL,'1235010','2002-12-25','STOCK','NEW',69),(711,'2002-12-01','2003-01-01',1,NULL,'1235011','2002-12-25','STOCK','NEW',69),(712,'2002-12-01','2003-01-01',1,NULL,'1235012','2002-12-25','STOCK','NEW',69),(713,'2002-12-01','2003-01-01',1,NULL,'1235013','2002-12-25','STOCK','NEW',69),(714,'2002-12-01','2003-01-01',1,NULL,'1235014','2002-12-25','STOCK','NEW',69),(715,'2002-12-01','2003-01-01',1,NULL,'1235015','2002-12-25','STOCK','NEW',69),(716,'2002-12-01','2003-01-01',1,NULL,'1235016','2002-12-25','STOCK','NEW',69),(717,'2002-12-01','2003-01-01',1,NULL,'1235017','2002-12-25','STOCK','NEW',69),(718,'2002-12-01','2003-01-01',1,NULL,'1235018','2002-12-25','STOCK','NEW',11),(719,'2002-12-01','2003-01-01',1,NULL,'1235019','2002-12-25','STOCK','NEW',11),(720,'2002-12-01','2003-01-01',1,NULL,'1235020','2002-12-25','STOCK','NEW',11),(721,'2002-12-01','2003-01-01',1,NULL,'1235021','2002-12-25','STOCK','NEW',11),(722,'2002-12-01','2003-01-01',1,NULL,'1235022','2002-12-25','STOCK','NEW',11),(723,'2002-12-01','2003-01-01',1,NULL,'1235023','2002-12-25','STOCK','NEW',11),(724,'2002-12-01','2003-01-01',1,NULL,'1235024','2002-12-25','STOCK','NEW',11),(725,'2002-12-01','2003-01-01',1,NULL,'1235025','2002-12-25','STOCK','NEW',11),(726,'2002-12-01','2003-01-01',1,NULL,'1235026','2002-12-25','STOCK','NEW',11),(727,'2002-12-01','2003-01-01',1,NULL,'1235027','2002-12-25','STOCK','NEW',11),(728,'2002-12-01','2003-01-01',1,NULL,'1235028','2002-12-25','STOCK','NEW',11),(729,'2002-12-01','2003-01-01',1,NULL,'1235029','2002-12-25','STOCK','NEW',30),(730,'2002-12-01','2003-01-01',1,NULL,'1235030','2002-12-25','STOCK','NEW',6),(731,'2002-12-01','2003-01-01',1,NULL,'1235031','2002-12-25','STOCK','NEW',6),(732,'2002-12-01','2003-01-01',1,NULL,'1235032','2002-12-25','STOCK','NEW',6),(733,'2002-12-01','2003-01-01',1,NULL,'1235033','2002-12-25','STOCK','NEW',6),(734,'2002-12-01','2003-01-01',1,NULL,'1235034','2002-12-25','STOCK','NEW',6),(735,'2002-12-01','2003-01-01',1,NULL,'1235035','2002-12-25','STOCK','NEW',6),(736,'2002-12-01','2003-01-01',1,NULL,'1235036','2002-12-25','STOCK','NEW',6),(737,'2002-12-01','2003-01-01',1,NULL,'1235037','2002-12-25','STOCK','NEW',6),(738,'2002-12-01','2003-01-01',1,NULL,'1235038','2002-12-25','STOCK','NEW',6),(739,'2002-12-01','2003-01-01',1,NULL,'1235039','2002-12-25','STOCK','NEW',6),(740,'2002-12-01','2003-01-01',1,NULL,'1235040','2002-12-25','STOCK','NEW',6),(741,'2002-12-01','2003-01-01',1,NULL,'1235041','2002-12-25','STOCK','NEW',1),(742,'2002-12-01','2003-01-01',1,NULL,'1235042','2002-12-25','STOCK','NEW',1),(743,'2002-12-01','2003-01-01',1,NULL,'1235043','2002-12-25','STOCK','NEW',1),(744,'2002-12-01','2003-01-01',1,NULL,'1235044','2002-12-25','STOCK','NEW',1),(745,'2002-12-01','2003-01-01',1,NULL,'1235045','2002-12-25','STOCK','NEW',1),(746,'2002-12-01','2003-01-01',1,NULL,'1235046','2002-12-25','STOCK','NEW',1),(747,'2002-12-01','2003-01-01',1,NULL,'1235047','2002-12-25','STOCK','NEW',1),(748,'2002-12-01','2003-01-01',1,NULL,'1235048','2002-12-25','STOCK','NEW',1),(749,'2002-12-01','2003-01-01',1,NULL,'1235049','2002-12-25','STOCK','NEW',1),(750,'2002-12-01','2003-01-01',1,NULL,'1235050','2002-12-25','STOCK','NEW',1),(751,'2002-12-01','2003-01-01',1,NULL,'1235051','2002-12-25','STOCK','NEW',1),(752,'2002-12-01','2003-01-01',1,NULL,'1235052','2002-12-25','STOCK','NEW',11),(753,'2002-12-01','2003-01-01',1,NULL,'1235053','2002-12-25','STOCK','NEW',11),(754,'2002-12-01','2003-01-01',1,NULL,'1235054','2002-12-25','STOCK','NEW',11),(755,'2002-12-01','2003-01-01',1,NULL,'1235055','2002-12-25','STOCK','NEW',11),(756,'2002-12-01','2003-01-01',1,NULL,'1235056','2002-12-25','STOCK','NEW',11),(757,'2002-12-01','2003-01-01',1,NULL,'1235057','2002-12-25','STOCK','NEW',11),(758,'2002-12-01','2003-01-01',1,NULL,'1235058','2002-12-25','STOCK','NEW',11),(759,'2002-12-01','2003-01-01',1,NULL,'1235059','2002-12-25','STOCK','NEW',11),(760,'2002-12-01','2003-01-01',1,NULL,'1235060','2002-12-25','STOCK','NEW',11),(761,'2002-12-01','2003-01-01',1,NULL,'1235061','2002-12-25','STOCK','NEW',11),(762,'2002-12-01','2003-01-01',1,NULL,'1235062','2002-12-25','STOCK','NEW',11),(763,'2002-12-01','2003-01-01',1,NULL,'1235063','2002-12-25','STOCK','NEW',55),(764,'2002-12-01','2003-01-01',1,NULL,'1235064','2002-12-25','STOCK','NEW',60),(765,'2002-12-01','2003-01-01',1,NULL,'1235065','2002-12-25','STOCK','NEW',60),(766,'2002-12-01','2003-01-01',1,NULL,'1235066','2002-12-25','STOCK','NEW',60),(767,'2002-12-01','2003-01-01',1,NULL,'1235067','2002-12-25','STOCK','NEW',60),(768,'2002-12-01','2003-01-01',1,NULL,'1235068','2002-12-25','STOCK','NEW',60),(769,'2002-12-01','2003-01-01',1,NULL,'1235069','2002-12-25','STOCK','NEW',60),(770,'2002-12-01','2003-01-01',1,NULL,'1235070','2002-12-25','STOCK','NEW',61),(771,'2002-12-01','2003-01-01',1,NULL,'1235071','2002-12-25','STOCK','NEW',77),(772,'2002-12-01','2003-01-01',1,NULL,'1235072','2002-12-25','STOCK','NEW',77),(773,'2002-12-01','2003-01-01',1,NULL,'1235073','2002-12-25','STOCK','NEW',77),(774,'2002-12-01','2003-01-01',1,NULL,'1235074','2002-12-25','STOCK','NEW',77),(775,'2002-12-01','2003-01-01',1,NULL,'1235075','2002-12-25','STOCK','NEW',77),(776,'2002-12-01','2003-01-01',1,NULL,'1235076','2002-12-25','STOCK','NEW',77),(777,'2002-12-01','2003-01-01',1,NULL,'1235077','2002-12-25','STOCK','NEW',77),(778,'2002-12-01','2003-01-01',1,NULL,'1235078','2002-12-25','STOCK','NEW',77),(779,'2002-12-01','2003-01-01',1,NULL,'1235079','2002-12-25','STOCK','NEW',77),(780,'2002-12-01','2003-01-01',1,NULL,'1235080','2002-12-25','STOCK','NEW',77),(781,'2002-12-01','2003-01-01',1,NULL,'1235081','2002-12-25','STOCK','NEW',77),(782,'2002-12-01','2003-01-01',1,NULL,'1235082','2002-12-25','STOCK','NEW',61),(783,'2002-12-01','2003-01-01',1,NULL,'1235083','2002-12-25','STOCK','NEW',77),(784,'2002-12-01','2003-01-01',1,NULL,'1235084','2002-12-25','STOCK','NEW',77),(785,'2002-12-01','2003-01-01',1,NULL,'1235085','2002-12-25','STOCK','NEW',77),(786,'2002-12-01','2003-01-01',1,NULL,'1235086','2002-12-25','STOCK','NEW',77),(787,'2002-12-01','2003-01-01',1,NULL,'1235087','2002-12-25','STOCK','NEW',77),(788,'2002-12-01','2003-01-01',1,NULL,'1235088','2002-12-25','STOCK','NEW',77),(789,'2002-12-01','2003-01-01',1,NULL,'1235089','2002-12-25','STOCK','NEW',77),(790,'2002-12-01','2003-01-01',1,NULL,'1235090','2002-12-25','STOCK','NEW',77),(791,'2002-12-01','2003-01-01',1,NULL,'1235091','2002-12-25','STOCK','NEW',77),(792,'2002-12-01','2003-01-01',1,NULL,'1235092','2002-12-25','STOCK','NEW',77),(793,'2002-12-01','2003-01-01',1,NULL,'1235093','2002-12-25','STOCK','NEW',77),(794,'2002-12-01','2003-01-01',1,NULL,'1235094','2002-12-25','STOCK','NEW',61),(795,'2002-12-01','2003-01-01',1,NULL,'1235095','2002-12-25','STOCK','NEW',77),(796,'2002-12-01','2003-01-01',1,NULL,'1235096','2002-12-25','STOCK','NEW',77),(797,'2002-12-01','2003-01-01',1,NULL,'1235097','2002-12-25','STOCK','NEW',77),(798,'2002-12-01','2003-01-01',1,NULL,'1235098','2002-12-25','STOCK','NEW',77),(799,'2002-12-01','2003-01-01',1,NULL,'1235099','2002-12-25','STOCK','NEW',77),(800,'2002-12-01','2003-01-01',1,NULL,'1235100','2002-12-25','STOCK','NEW',77),(801,'2002-12-01','2003-01-01',1,NULL,'1235101','2002-12-25','STOCK','NEW',77),(802,'2002-12-01','2003-01-01',1,NULL,'1235102','2002-12-25','STOCK','NEW',77),(803,'2002-12-01','2003-01-01',1,NULL,'1235103','2002-12-25','STOCK','NEW',77),(804,'2002-12-01','2003-01-01',1,NULL,'1235104','2002-12-25','STOCK','NEW',77),(805,'2002-12-01','2003-01-01',1,NULL,'1235105','2002-12-25','STOCK','NEW',77),(806,'2002-12-01','2003-01-01',1,NULL,'1235106','2002-12-25','STOCK','NEW',61),(807,'2002-12-01','2003-01-01',1,NULL,'1235107','2002-12-25','STOCK','NEW',77),(808,'2002-12-01','2003-01-01',1,NULL,'1235108','2002-12-25','STOCK','NEW',77),(809,'2002-12-01','2003-01-01',1,NULL,'1235109','2002-12-25','STOCK','NEW',77),(810,'2002-12-01','2003-01-01',1,NULL,'1235110','2002-12-25','STOCK','NEW',77),(811,'2002-12-01','2003-01-01',1,NULL,'1235111','2002-12-25','STOCK','NEW',77),(812,'2002-12-01','2003-01-01',1,NULL,'1235112','2002-12-25','STOCK','NEW',77),(813,'2002-12-01','2003-01-01',1,NULL,'1235113','2002-12-25','STOCK','NEW',77),(814,'2002-12-01','2003-01-01',1,NULL,'1235114','2002-12-25','STOCK','NEW',77),(815,'2002-12-01','2003-01-01',1,NULL,'1235115','2002-12-25','STOCK','NEW',77),(816,'2002-12-01','2003-01-01',1,NULL,'1235116','2002-12-25','STOCK','NEW',77),(817,'2002-12-01','2003-01-01',1,NULL,'1235117','2002-12-25','STOCK','NEW',77),(818,'2002-12-01','2003-01-01',1,NULL,'1235118','2002-12-25','STOCK','NEW',61),(819,'2002-12-01','2003-01-01',1,NULL,'1235119','2002-12-25','STOCK','NEW',77),(820,'2002-12-01','2003-01-01',1,NULL,'1235120','2002-12-25','STOCK','NEW',77),(821,'2002-12-01','2003-01-01',1,NULL,'1235121','2002-12-25','STOCK','NEW',77),(822,'2002-12-01','2003-01-01',1,NULL,'1235122','2002-12-25','STOCK','NEW',77),(823,'2002-12-01','2003-01-01',1,NULL,'1235123','2002-12-25','STOCK','NEW',77),(824,'2002-12-01','2003-01-01',1,NULL,'1235124','2002-12-25','STOCK','NEW',77),(825,'2002-12-01','2003-01-01',1,NULL,'1235125','2002-12-25','STOCK','NEW',77),(826,'2002-12-01','2003-01-01',1,NULL,'1235126','2002-12-25','STOCK','NEW',77),(827,'2002-12-01','2003-01-01',1,NULL,'1235127','2002-12-25','STOCK','NEW',77),(828,'2002-12-01','2003-01-01',1,NULL,'1235128','2002-12-25','STOCK','NEW',77),(829,'2002-12-01','2003-01-01',1,NULL,'1235129','2002-12-25','STOCK','NEW',77),(830,'2002-12-01','2003-01-01',1,NULL,'1235130','2002-12-25','STOCK','NEW',61),(831,'2002-12-01','2003-01-01',1,NULL,'1235131','2002-12-25','STOCK','NEW',77),(832,'2002-12-01','2003-01-01',1,NULL,'1235132','2002-12-25','STOCK','NEW',77),(833,'2002-12-01','2003-01-01',1,NULL,'1235133','2002-12-25','STOCK','NEW',77),(834,'2002-12-01','2003-01-01',1,NULL,'1235134','2002-12-25','STOCK','NEW',77),(835,'2002-12-01','2003-01-01',1,NULL,'1235135','2002-12-25','STOCK','NEW',77),(836,'2002-12-01','2003-01-01',1,NULL,'1235136','2002-12-25','STOCK','NEW',77),(837,'2002-12-01','2003-01-01',1,NULL,'1235137','2002-12-25','STOCK','NEW',77),(838,'2002-12-01','2003-01-01',1,NULL,'1235138','2002-12-25','STOCK','NEW',77),(839,'2002-12-01','2003-01-01',1,NULL,'1235139','2002-12-25','STOCK','NEW',77),(840,'2002-12-01','2003-01-01',1,NULL,'1235140','2002-12-25','STOCK','NEW',77),(841,'2002-12-01','2003-01-01',1,NULL,'1235141','2002-12-25','STOCK','NEW',77),(842,'2002-12-01','2003-01-01',1,NULL,'1235142','2002-12-25','STOCK','NEW',63),(843,'2002-12-01','2003-01-01',1,NULL,'1235143','2002-12-25','STOCK','NEW',63),(844,'2002-12-01','2003-01-01',1,NULL,'1235144','2002-12-25','STOCK','NEW',63),(845,'2002-12-01','2003-01-01',1,NULL,'1235145','2002-12-25','STOCK','NEW',63),(846,'2002-12-01','2003-01-01',1,NULL,'1235146','2002-12-25','STOCK','NEW',63),(847,'2002-12-01','2003-01-01',1,NULL,'1235147','2002-12-25','STOCK','NEW',63),(848,'2002-12-01','2003-01-01',1,NULL,'1235148','2002-12-25','STOCK','NEW',63),(849,'2002-12-01','2003-01-01',1,NULL,'1235149','2002-12-25','STOCK','NEW',63),(850,'2002-12-01','2003-01-01',1,NULL,'1235150','2002-12-25','STOCK','NEW',63),(851,'2002-12-01','2003-01-01',1,NULL,'1235151','2002-12-25','STOCK','NEW',63),(852,'2002-12-01','2003-01-01',1,NULL,'1235152','2002-12-25','STOCK','NEW',63),(853,'2002-12-01','2003-01-01',1,NULL,'1235153','2002-12-25','STOCK','NEW',62),(854,'2002-12-01','2003-01-01',1,NULL,'1235154','2002-12-25','STOCK','NEW',62),(855,'2002-12-01','2003-01-01',1,NULL,'1235155','2002-12-25','STOCK','NEW',62),(856,'2002-12-01','2003-01-01',1,NULL,'1235156','2002-12-25','STOCK','NEW',62),(857,'2002-12-01','2003-01-01',1,NULL,'1235157','2002-12-25','STOCK','NEW',62),(858,'2002-12-01','2003-01-01',1,NULL,'1235158','2002-12-25','STOCK','NEW',62),(859,'2002-12-01','2003-01-01',1,NULL,'1235159','2002-12-25','STOCK','NEW',62),(860,'2002-12-01','2003-01-01',1,NULL,'1235160','2002-12-25','STOCK','NEW',62),(861,'2002-12-01','2003-01-01',1,NULL,'1235161','2002-12-25','STOCK','NEW',62),(862,'2002-12-01','2003-01-01',1,NULL,'1235162','2002-12-25','STOCK','NEW',62),(863,'2002-12-01','2003-01-01',1,NULL,'1235163','2002-12-25','STOCK','NEW',62),(864,'2002-12-01','2003-01-01',1,NULL,'1235164','2002-12-25','STOCK','NEW',62),(865,'2002-12-01','2003-01-01',1,NULL,'1235165','2002-12-25','STOCK','NEW',62),(866,'2002-12-01','2003-01-01',1,NULL,'1235166','2002-12-25','STOCK','NEW',62),(867,'2002-12-01','2003-01-01',1,NULL,'1235167','2002-12-25','STOCK','NEW',62),(868,'2002-12-01','2003-01-01',1,NULL,'1235168','2002-12-25','STOCK','NEW',62),(869,'2002-12-01','2003-01-01',1,NULL,'1235169','2002-12-25','STOCK','NEW',62),(870,'2002-12-01','2003-01-01',1,NULL,'1235170','2002-12-25','STOCK','NEW',62),(871,'2002-12-01','2003-01-01',1,NULL,'1235171','2002-12-25','STOCK','NEW',62),(872,'2002-12-01','2003-01-01',1,NULL,'1235172','2002-12-25','STOCK','NEW',62),(873,'2002-12-01','2003-01-01',1,NULL,'1235173','2002-12-25','STOCK','NEW',62),(874,'2002-12-01','2003-01-01',1,NULL,'1235174','2002-12-25','STOCK','NEW',62),(875,'2002-12-01','2003-01-01',1,NULL,'1235175','2002-12-25','STOCK','NEW',62),(876,'2002-12-01','2003-01-01',1,NULL,'1235176','2002-12-25','STOCK','NEW',62),(877,'2002-12-01','2003-01-01',1,NULL,'1235177','2002-12-25','STOCK','NEW',62),(878,'2002-12-01','2003-01-01',1,NULL,'1235178','2002-12-25','STOCK','NEW',62),(879,'2002-12-01','2003-01-01',1,NULL,'1235179','2002-12-25','STOCK','NEW',62),(880,'2002-12-01','2003-01-01',1,NULL,'1235180','2002-12-25','STOCK','NEW',62),(881,'2002-12-01','2003-01-01',1,NULL,'1235181','2002-12-25','STOCK','NEW',62),(882,'2002-12-01','2003-01-01',1,NULL,'1235182','2002-12-25','STOCK','NEW',62),(883,'2002-12-01','2003-01-01',1,NULL,'1235183','2002-12-25','STOCK','NEW',62),(884,'2002-12-01','2003-01-01',1,NULL,'1235184','2002-12-25','STOCK','NEW',62),(885,'2002-12-01','2003-01-01',1,NULL,'1235185','2002-12-25','STOCK','NEW',62),(886,'2002-12-01','2003-01-01',1,NULL,'1235186','2002-12-25','STOCK','NEW',62),(887,'2002-12-01','2003-01-01',1,NULL,'1235187','2002-12-25','STOCK','NEW',62),(888,'2002-12-01','2003-01-01',1,NULL,'1235188','2002-12-25','STOCK','NEW',62),(889,'2002-12-01','2003-01-01',1,NULL,'1235189','2002-12-25','STOCK','NEW',62),(890,'2002-12-01','2003-01-01',1,NULL,'1235190','2002-12-25','STOCK','NEW',62),(891,'2002-12-01','2003-01-01',1,NULL,'1235191','2002-12-25','STOCK','NEW',62),(892,'2002-12-01','2003-01-01',1,NULL,'1235192','2002-12-25','STOCK','NEW',62),(893,'2002-12-01','2003-01-01',1,NULL,'1235193','2002-12-25','STOCK','NEW',62),(894,'2002-12-01','2003-01-01',1,NULL,'1235194','2002-12-25','STOCK','NEW',62),(895,'2002-12-01','2003-01-01',1,NULL,'1235195','2002-12-25','STOCK','NEW',62),(896,'2002-12-01','2003-01-01',1,NULL,'1235196','2002-12-25','STOCK','NEW',62),(897,'2002-12-01','2003-01-01',1,NULL,'1235197','2002-12-25','STOCK','NEW',62),(898,'2002-12-01','2003-01-01',1,NULL,'1235198','2002-12-25','STOCK','NEW',62),(899,'2002-12-01','2003-01-01',1,NULL,'1235199','2002-12-25','STOCK','NEW',62),(900,'2002-12-01','2003-01-01',1,NULL,'1235200','2002-12-25','STOCK','NEW',62),(901,'2002-12-01','2003-01-01',1,NULL,'1235201','2002-12-25','STOCK','NEW',62),(902,'2002-12-01','2003-01-01',1,NULL,'1235202','2002-12-25','STOCK','NEW',62),(903,'2002-12-01','2003-01-01',1,NULL,'1235203','2002-12-25','STOCK','NEW',62),(904,'2002-12-01','2003-01-01',1,NULL,'1235204','2002-12-25','STOCK','NEW',62),(905,'2002-12-01','2003-01-01',1,NULL,'1235205','2002-12-25','STOCK','NEW',62),(906,'2002-12-01','2003-01-01',1,NULL,'1235206','2002-12-25','STOCK','NEW',62),(907,'2002-12-01','2003-01-01',1,NULL,'1235207','2002-12-25','STOCK','NEW',62),(908,'2002-12-01','2003-01-01',1,NULL,'1235208','2002-12-25','STOCK','NEW',62),(909,'2002-12-01','2003-01-01',1,NULL,'1235209','2002-12-25','STOCK','NEW',62),(910,'2002-12-01','2003-01-01',1,NULL,'1235210','2002-12-25','STOCK','NEW',62),(911,'2002-12-01','2003-01-01',1,NULL,'1235211','2002-12-25','STOCK','NEW',62),(912,'2002-12-01','2003-01-01',1,NULL,'1235212','2002-12-25','STOCK','NEW',62),(913,'2002-12-01','2003-01-01',1,NULL,'1235213','2002-12-25','STOCK','NEW',62),(914,'2002-12-01','2003-01-01',1,NULL,'1235214','2002-12-25','STOCK','NEW',62),(915,'2002-12-01','2003-01-01',1,NULL,'1235215','2002-12-25','STOCK','NEW',62),(916,'2002-12-01','2003-01-01',1,NULL,'1235216','2002-12-25','STOCK','NEW',62),(917,'2002-12-01','2003-01-01',1,NULL,'1235217','2002-12-25','STOCK','NEW',62),(918,'2002-12-01','2003-01-01',1,NULL,'1235218','2002-12-25','STOCK','NEW',62),(919,'2002-12-01','2003-01-01',1,NULL,'1235219','2002-12-25','STOCK','NEW',56),(920,'2002-12-01','2003-01-01',1,NULL,'1235220','2002-12-25','STOCK','NEW',65),(921,'2002-12-01','2003-01-01',1,NULL,'1235221','2002-12-25','STOCK','NEW',65),(922,'2002-12-01','2003-01-01',1,NULL,'1235222','2002-12-25','STOCK','NEW',65),(923,'2002-12-01','2003-01-01',1,NULL,'1235223','2002-12-25','STOCK','NEW',65),(924,'2002-12-01','2003-01-01',1,NULL,'1235224','2002-12-25','STOCK','NEW',65),(925,'2002-12-01','2003-01-01',1,NULL,'1235225','2002-12-25','STOCK','NEW',65),(926,'2002-12-01','2003-01-01',1,NULL,'1235226','2002-12-25','STOCK','NEW',65),(927,'2002-12-01','2003-01-01',1,NULL,'1235227','2002-12-25','STOCK','NEW',65),(928,'2002-12-01','2003-01-01',1,NULL,'1235228','2002-12-25','STOCK','NEW',65),(929,'2002-12-01','2003-01-01',1,NULL,'1235229','2002-12-25','STOCK','NEW',65),(930,'2002-12-01','2003-01-01',1,NULL,'1235230','2002-12-25','STOCK','NEW',65),(931,'2002-12-01','2003-01-01',1,NULL,'1235231','2002-12-25','STOCK','NEW',64),(932,'2002-12-01','2003-01-01',1,NULL,'1235232','2002-12-25','STOCK','NEW',64),(933,'2002-12-01','2003-01-01',1,NULL,'1235233','2002-12-25','STOCK','NEW',64),(934,'2002-12-01','2003-01-01',1,NULL,'1235234','2002-12-25','STOCK','NEW',64),(935,'2002-12-01','2003-01-01',1,NULL,'1235235','2002-12-25','STOCK','NEW',64),(936,'2002-12-01','2003-01-01',1,NULL,'1235236','2002-12-25','STOCK','NEW',64),(937,'2002-12-01','2003-01-01',1,NULL,'1235237','2002-12-25','STOCK','NEW',64),(938,'2002-12-01','2003-01-01',1,NULL,'1235238','2002-12-25','STOCK','NEW',64),(939,'2002-12-01','2003-01-01',1,NULL,'1235239','2002-12-25','STOCK','NEW',64),(940,'2002-12-01','2003-01-01',1,NULL,'1235240','2002-12-25','STOCK','NEW',64),(941,'2002-12-01','2003-01-01',1,NULL,'1235241','2002-12-25','STOCK','NEW',64),(942,'2002-12-01','2003-01-01',1,NULL,'1235242','2002-12-25','STOCK','NEW',56),(943,'2002-12-01','2003-01-01',1,NULL,'1235243','2002-12-25','STOCK','NEW',65),(944,'2002-12-01','2003-01-01',1,NULL,'1235244','2002-12-25','STOCK','NEW',65),(945,'2002-12-01','2003-01-01',1,NULL,'1235245','2002-12-25','STOCK','NEW',65),(946,'2002-12-01','2003-01-01',1,NULL,'1235246','2002-12-25','STOCK','NEW',65),(947,'2002-12-01','2003-01-01',1,NULL,'1235247','2002-12-25','STOCK','NEW',65),(948,'2002-12-01','2003-01-01',1,NULL,'1235248','2002-12-25','STOCK','NEW',65),(949,'2002-12-01','2003-01-01',1,NULL,'1235249','2002-12-25','STOCK','NEW',65),(950,'2002-12-01','2003-01-01',1,NULL,'1235250','2002-12-25','STOCK','NEW',65),(951,'2002-12-01','2003-01-01',1,NULL,'1235251','2002-12-25','STOCK','NEW',65),(952,'2002-12-01','2003-01-01',1,NULL,'1235252','2002-12-25','STOCK','NEW',65),(953,'2002-12-01','2003-01-01',1,NULL,'1235253','2002-12-25','STOCK','NEW',65),(954,'2002-12-01','2003-01-01',1,NULL,'1235254','2002-12-25','STOCK','NEW',64),(955,'2002-12-01','2003-01-01',1,NULL,'1235255','2002-12-25','STOCK','NEW',64),(956,'2002-12-01','2003-01-01',1,NULL,'1235256','2002-12-25','STOCK','NEW',64),(957,'2002-12-01','2003-01-01',1,NULL,'1235257','2002-12-25','STOCK','NEW',64),(958,'2002-12-01','2003-01-01',1,NULL,'1235258','2002-12-25','STOCK','NEW',64),(959,'2002-12-01','2003-01-01',1,NULL,'1235259','2002-12-25','STOCK','NEW',64),(960,'2002-12-01','2003-01-01',1,NULL,'1235260','2002-12-25','STOCK','NEW',64),(961,'2002-12-01','2003-01-01',1,NULL,'1235261','2002-12-25','STOCK','NEW',64),(962,'2002-12-01','2003-01-01',1,NULL,'1235262','2002-12-25','STOCK','NEW',64),(963,'2002-12-01','2003-01-01',1,NULL,'1235263','2002-12-25','STOCK','NEW',64),(964,'2002-12-01','2003-01-01',1,NULL,'1235264','2002-12-25','STOCK','NEW',64),(965,'2002-12-01','2003-01-01',1,NULL,'1235265','2002-12-25','STOCK','NEW',59),(966,'2002-12-01','2003-01-01',1,NULL,'1235266','2002-12-25','STOCK','NEW',61),(967,'2002-12-01','2003-01-01',1,NULL,'1235267','2002-12-25','STOCK','NEW',77),(968,'2002-12-01','2003-01-01',1,NULL,'1235268','2002-12-25','STOCK','NEW',77),(969,'2002-12-01','2003-01-01',1,NULL,'1235269','2002-12-25','STOCK','NEW',77),(970,'2002-12-01','2003-01-01',1,NULL,'1235270','2002-12-25','STOCK','NEW',77),(971,'2002-12-01','2003-01-01',1,NULL,'1235271','2002-12-25','STOCK','NEW',77),(972,'2002-12-01','2003-01-01',1,NULL,'1235272','2002-12-25','STOCK','NEW',77),(973,'2002-12-01','2003-01-01',1,NULL,'1235273','2002-12-25','STOCK','NEW',77),(974,'2002-12-01','2003-01-01',1,NULL,'1235274','2002-12-25','STOCK','NEW',77),(975,'2002-12-01','2003-01-01',1,NULL,'1235275','2002-12-25','STOCK','NEW',77),(976,'2002-12-01','2003-01-01',1,NULL,'1235276','2002-12-25','STOCK','NEW',77),(977,'2002-12-01','2003-01-01',1,NULL,'1235277','2002-12-25','STOCK','NEW',77),(978,'2002-12-01','2003-01-01',1,NULL,'1235278','2002-12-25','STOCK','NEW',61),(979,'2002-12-01','2003-01-01',1,NULL,'1235279','2002-12-25','STOCK','NEW',77),(980,'2002-12-01','2003-01-01',1,NULL,'1235280','2002-12-25','STOCK','NEW',77),(981,'2002-12-01','2003-01-01',1,NULL,'1235281','2002-12-25','STOCK','NEW',77),(982,'2002-12-01','2003-01-01',1,NULL,'1235282','2002-12-25','STOCK','NEW',77),(983,'2002-12-01','2003-01-01',1,NULL,'1235283','2002-12-25','STOCK','NEW',77),(984,'2002-12-01','2003-01-01',1,NULL,'1235284','2002-12-25','STOCK','NEW',77),(985,'2002-12-01','2003-01-01',1,NULL,'1235285','2002-12-25','STOCK','NEW',77),(986,'2002-12-01','2003-01-01',1,NULL,'1235286','2002-12-25','STOCK','NEW',77),(987,'2002-12-01','2003-01-01',1,NULL,'1235287','2002-12-25','STOCK','NEW',77),(988,'2002-12-01','2003-01-01',1,NULL,'1235288','2002-12-25','STOCK','NEW',77),(989,'2002-12-01','2003-01-01',1,NULL,'1235289','2002-12-25','STOCK','NEW',77),(990,'2002-12-01','2003-01-01',1,NULL,'1235290','2002-12-25','STOCK','NEW',63),(991,'2002-12-01','2003-01-01',1,NULL,'1235291','2002-12-25','STOCK','NEW',63),(992,'2002-12-01','2003-01-01',1,NULL,'1235292','2002-12-25','STOCK','NEW',63),(993,'2002-12-01','2003-01-01',1,NULL,'1235293','2002-12-25','STOCK','NEW',63),(994,'2002-12-01','2003-01-01',1,NULL,'1235294','2002-12-25','STOCK','NEW',63),(995,'2002-12-01','2003-01-01',1,NULL,'1235295','2002-12-25','STOCK','NEW',63),(996,'2002-12-01','2003-01-01',1,NULL,'1235296','2002-12-25','STOCK','NEW',63),(997,'2002-12-01','2003-01-01',1,NULL,'1235297','2002-12-25','STOCK','NEW',63),(998,'2002-12-01','2003-01-01',1,NULL,'1235298','2002-12-25','STOCK','NEW',63),(999,'2002-12-01','2003-01-01',1,NULL,'1235299','2002-12-25','STOCK','NEW',63),(1000,'2002-12-01','2003-01-01',1,NULL,'1235300','2002-12-25','STOCK','NEW',63),(1001,'2002-12-01','2003-01-01',1,NULL,'1235301','2002-12-25','STOCK','NEW',63),(1002,'2002-12-01','2003-01-01',1,NULL,'1235302','2002-12-25','STOCK','NEW',63),(1003,'2002-12-01','2003-01-01',1,NULL,'1235303','2002-12-25','STOCK','NEW',63),(1004,'2002-12-01','2003-01-01',1,NULL,'1235304','2002-12-25','STOCK','NEW',63),(1005,'2002-12-01','2003-01-01',1,NULL,'1235305','2002-12-25','STOCK','NEW',63),(1006,'2002-12-01','2003-01-01',1,NULL,'1235306','2002-12-25','STOCK','NEW',63),(1007,'2002-12-01','2003-01-01',1,NULL,'1235307','2002-12-25','STOCK','NEW',63),(1008,'2002-12-01','2003-01-01',1,NULL,'1235308','2002-12-25','STOCK','NEW',63),(1009,'2002-12-01','2003-01-01',1,NULL,'1235309','2002-12-25','STOCK','NEW',63),(1010,'2002-12-01','2003-01-01',1,NULL,'1235310','2002-12-25','STOCK','NEW',63),(1011,'2002-12-01','2003-01-01',1,NULL,'1235311','2002-12-25','STOCK','NEW',63),(1012,'2002-12-01','2003-01-01',1,NULL,'1235312','2002-12-25','STOCK','NEW',57),(1013,'2002-12-01','2003-01-01',1,NULL,'1235313','2002-12-25','STOCK','NEW',67),(1014,'2002-12-01','2003-01-01',1,NULL,'1235314','2002-12-25','STOCK','NEW',67),(1015,'2002-12-01','2003-01-01',1,NULL,'1235315','2002-12-25','STOCK','NEW',67),(1016,'2002-12-01','2003-01-01',1,NULL,'1235316','2002-12-25','STOCK','NEW',67),(1017,'2002-12-01','2003-01-01',1,NULL,'1235317','2002-12-25','STOCK','NEW',67),(1018,'2002-12-01','2003-01-01',1,NULL,'1235318','2002-12-25','STOCK','NEW',67),(1019,'2002-12-01','2003-01-01',1,NULL,'1235319','2002-12-25','STOCK','NEW',67),(1020,'2002-12-01','2003-01-01',1,NULL,'1235320','2002-12-25','STOCK','NEW',67),(1021,'2002-12-01','2003-01-01',1,NULL,'1235321','2002-12-25','STOCK','NEW',67),(1022,'2002-12-01','2003-01-01',1,NULL,'1235322','2002-12-25','STOCK','NEW',67),(1023,'2002-12-01','2003-01-01',1,NULL,'1235323','2002-12-25','STOCK','NEW',67),(1024,'2002-12-01','2003-01-01',1,NULL,'1235324','2002-12-25','STOCK','NEW',69),(1025,'2002-12-01','2003-01-01',1,NULL,'1235325','2002-12-25','STOCK','NEW',69),(1026,'2002-12-01','2003-01-01',1,NULL,'1235326','2002-12-25','STOCK','NEW',69),(1027,'2002-12-01','2003-01-01',1,NULL,'1235327','2002-12-25','STOCK','NEW',69),(1028,'2002-12-01','2003-01-01',1,NULL,'1235328','2002-12-25','STOCK','NEW',69),(1029,'2002-12-01','2003-01-01',1,NULL,'1235329','2002-12-25','STOCK','NEW',69),(1030,'2002-12-01','2003-01-01',1,NULL,'1235330','2002-12-25','STOCK','NEW',69),(1031,'2002-12-01','2003-01-01',1,NULL,'1235331','2002-12-25','STOCK','NEW',69),(1032,'2002-12-01','2003-01-01',1,NULL,'1235332','2002-12-25','STOCK','NEW',69),(1033,'2002-12-01','2003-01-01',1,NULL,'1235333','2002-12-25','STOCK','NEW',69),(1034,'2002-12-01','2003-01-01',1,NULL,'1235334','2002-12-25','STOCK','NEW',69),(14675556,'2005-03-13','2004-01-15',0,'2004-01-22','CA2646','2004-01-01','RETAIL','NEW',28),(14937806,'2005-03-13','2004-11-12',16000,'2004-11-07','CA2617','2004-10-11','RETAIL','NEW',28),(15009556,'2005-03-13','2004-11-02',0,'2004-11-07','CA2613','2004-10-02','RETAIL','NEW',28),(15009606,'2005-03-13','2004-11-02',30020,'2004-11-07','CA2614','2004-09-02','RETAIL','NEW',28),(15081406,'2005-03-13','2004-05-22',0,'2004-05-27','CA2642','2004-03-22','RETAIL','NEW',28),(15081456,'2005-03-13','2004-05-22',0,'2004-05-27','CA2643','2004-03-22','RETAIL','NEW',28),(15390206,'2005-03-13','2004-06-25',30001,'2004-06-30','CA2389','2004-06-01','RETAIL','NEW',28),(15517006,'2005-03-13','2004-11-02',0,'2004-11-07','CA2336','2004-10-02','RETAIL','NEW',28),(15639806,'2005-03-13','2005-01-15',0,'2005-01-20','CA2509','2005-01-01','RETAIL','NEW',28),(17797106,'2005-03-13','2005-11-17',0,'2005-11-22','CA2942','2005-09-27','RETAIL','NEW',28),(17797156,'2005-03-13','2005-11-17',0,'2005-11-22','CA2941','2005-09-27','RETAIL','NEW',28),(17808406,'2005-03-13','2005-03-09',0,'2005-03-14','CA2896','2005-02-09','RETAIL','NEW',28),(17816256,'2005-03-13','2005-09-22',0,'2005-09-27','CA3014','2005-06-22','RETAIL','NEW',28),(17816806,'2005-03-13','2005-09-08',0,'2005-09-13','CA2989','2005-07-08','RETAIL','NEW',28),(17874156,'2005-03-13','2005-10-20',0,'2005-10-25','CA3034','2005-10-20','RETAIL','NEW',28),(17888856,'2005-03-13','2005-10-23',0,'2005-10-28','CA3013','2005-08-23','RETAIL','NEW',28),(17916706,'2005-03-13','2005-02-15',0,'2005-02-20','CA2876','2005-01-01','RETAIL','NEW',28),(17935306,'2005-03-13','2005-11-02',0,'2005-11-07','CA2977','2005-01-01','RETAIL','NEW',28),(17937106,'2005-03-13','2005-02-23',0,'2005-02-28','CA2893','2005-02-01','RETAIL','NEW',28),(17975956,'2005-03-13','2005-06-15',0,'2005-06-20','CA2972','2005-01-01','RETAIL','NEW',28),(17978706,'2005-03-13','2005-01-26',0,'2005-01-31','CA2865','2005-01-01','RETAIL','NEW',28),(17989706,'2005-03-13','2005-04-01',0,'2005-04-06','CA2924','2005-01-01','RETAIL','NEW',28),(18012856,'2005-03-13',NULL,0,NULL,'CA2980','2004-01-22','STOCK','NEW',28),(18016356,'2005-03-13',NULL,0,NULL,'CA3048','2004-11-07','STOCK','NEW',28),(18046406,'2005-03-13',NULL,0,NULL,'CA3039','2004-11-07','STOCK','NEW',28),(18049156,'2005-03-13',NULL,0,NULL,'CA2943','2004-11-07','STOCK','NEW',28),(18049506,'2005-03-13',NULL,0,NULL,'CA2906','2004-05-27','STOCK','NEW',28),(18061806,'2005-03-13',NULL,0,NULL,'CA2995','2004-05-27','STOCK','NEW',28),(18087756,'2005-03-13',NULL,0,NULL,'CA2927','2004-06-30','STOCK','NEW',28),(18154306,'2005-03-13',NULL,0,NULL,'CA2873','2004-11-07','STOCK','NEW',28),(18154406,'2005-03-13',NULL,0,NULL,'CA2875','2005-01-20','STOCK','NEW',28),(18154456,'2005-03-13',NULL,0,NULL,'CA2874','2005-11-22','STOCK','NEW',28),(18202356,'2005-03-13',NULL,0,NULL,'CA3062','2005-11-22','STOCK','NEW',29),(18259506,'2005-03-13',NULL,0,NULL,'CA3001','2005-03-14','STOCK','NEW',29),(18891806,'2005-03-13',NULL,0,NULL,'CA2681','2005-09-27','STOCK','NEW',29),(18891856,'2005-03-13',NULL,0,NULL,'CA2695','2005-09-13','STOCK','NEW',29),(18892056,'2005-03-13',NULL,0,NULL,'CA2714','2005-10-25','STOCK','NEW',29),(18892106,'2005-03-13',NULL,0,NULL,'CA2716','2005-10-28','STOCK','NEW',29),(18892156,'2005-03-13',NULL,0,NULL,'CA2717','2005-02-20','STOCK','NEW',29),(18892206,'2005-03-13',NULL,0,NULL,'CA2724','2005-11-07','STOCK','NEW',29),(18892256,'2005-03-13',NULL,0,NULL,'CA2732','2005-02-28','STOCK','NEW',29),(18892306,'2005-03-13',NULL,0,NULL,'CA2740','2005-06-20','STOCK','NEW',29),(18892356,'2005-03-13',NULL,0,NULL,'CA2744','2005-01-31','STOCK','NEW',29),(18892606,'2005-03-13',NULL,0,NULL,'CA2777','2005-04-06','STOCK','NEW',29),(18892656,'2005-03-13',NULL,0,NULL,'CA2778','2004-01-22','STOCK','NEW',29),(18892806,'2005-03-13',NULL,0,NULL,'CA2784','2004-11-07','STOCK','NEW',29),(18900556,'2005-03-13',NULL,0,NULL,'CA2822','2004-11-07','STOCK','NEW',29),(18900606,'2005-03-13',NULL,0,NULL,'CA2823','2004-11-07','STOCK','NEW',29),(18900706,'2005-03-13',NULL,0,NULL,'CA2826','2004-05-27','STOCK','NEW',29),(18900856,'2005-03-13',NULL,0,NULL,'CA2835','2004-05-27','STOCK','NEW',29),(18900906,'2005-03-13',NULL,0,NULL,'CA2836','2004-06-30','STOCK','NEW',29),(18900956,'2005-03-13',NULL,0,NULL,'CA2837','2004-11-07','STOCK','NEW',29),(18901256,'2005-03-13',NULL,0,NULL,'CA2864','2005-01-20','STOCK','NEW',29),(18901306,'2005-03-13',NULL,0,NULL,'CA2872','2005-11-22','STOCK','NEW',29),(23311806,'2005-03-13',NULL,0,NULL,'CA2537','2005-11-22','STOCK','NEW',29),(23311906,'2005-03-13',NULL,0,NULL,'CA2549','2005-03-14','STOCK','NEW',29),(23312056,'2005-03-13',NULL,0,NULL,'CA2567','2005-09-27','STOCK','NEW',29),(23312156,'2005-03-13',NULL,0,NULL,'CA2574','2005-09-13','STOCK','NEW',29),(23312306,'2005-03-13',NULL,0,NULL,'CA2596','2005-10-25','STOCK','NEW',29),(23312456,'2005-03-13',NULL,0,NULL,'CA2603','2005-10-28','STOCK','NEW',29),(23312656,'2005-03-13',NULL,0,NULL,'CA2621','2005-02-20','STOCK','NEW',29),(23312906,'2005-03-13',NULL,0,NULL,'CA2639','2005-11-07','STOCK','NEW',29),(23313206,'2005-03-13',NULL,0,NULL,'CA2666','2005-02-28','STOCK','NEW',29),(23313356,'2005-03-13',NULL,0,NULL,'CA2674','2005-06-20','STOCK','NEW',29),(25702056,'2005-03-13',NULL,0,NULL,'CA2859','2005-01-31','STOCK','NEW',29),(25702106,'2005-03-13',NULL,0,NULL,'CA2867','2005-04-06','STOCK','NEW',30),(25702156,'2005-03-13',NULL,0,NULL,'CA2885','2005-01-01','STOCK','NEW',30),(25702356,'2005-03-13',NULL,0,NULL,'CA2895','2005-01-01','STOCK','NEW',30),(25702406,'2005-03-13',NULL,0,NULL,'CA2901','2005-01-01','STOCK','NEW',30),(25702656,'2005-03-13',NULL,0,NULL,'CA2920','2005-01-01','STOCK','NEW',30),(25702756,'2005-03-13',NULL,0,NULL,'CA2922','2005-01-01','STOCK','NEW',30),(25702956,'2005-03-13',NULL,0,NULL,'CA2938','2005-01-01','STOCK','NEW',30),(25703006,'2005-03-13',NULL,0,NULL,'CA2939','2005-01-01','STOCK','NEW',30),(25703156,'2005-03-13',NULL,0,NULL,'CA2959','2005-01-01','STOCK','NEW',30),(25703206,'2005-03-13',NULL,0,NULL,'CA2960','2005-01-01','STOCK','NEW',30),(25703606,'2005-03-13',NULL,0,NULL,'CA2975','2005-01-01','STOCK','NEW',30),(25703656,'2005-03-13',NULL,0,NULL,'CA2979','2005-01-01','STOCK','NEW',30),(25703806,'2005-03-13',NULL,0,NULL,'CA2993','2005-01-01','STOCK','NEW',30),(25711356,'2005-03-13',NULL,0,NULL,'CA3017','2005-01-01','STOCK','NEW',30),(25711456,'2005-03-13',NULL,0,NULL,'CA3020','2005-01-01','STOCK','NEW',30),(25712106,'2005-03-13',NULL,0,NULL,'CA3066','2005-01-01','STOCK','NEW',30),(26622956,'2005-03-13',NULL,0,NULL,'CA1571','2005-01-01','STOCK','NEW',30),(26623256,'2005-03-13',NULL,0,NULL,'CA2702','2005-01-01','STOCK','NEW',30),(26623356,'2005-03-13',NULL,0,NULL,'CA2711','2005-01-01','STOCK','NEW',30),(26623456,'2005-03-13',NULL,0,NULL,'CA2713','2005-01-01','STOCK','NEW',30),(26623556,'2005-03-13',NULL,0,NULL,'CA2723','2005-01-01','STOCK','NEW',30),(26623606,'2005-03-13',NULL,0,NULL,'CA2729','2005-01-01','STOCK','NEW',30),(26623856,'2005-03-13',NULL,0,NULL,'CA2759','2005-01-01','STOCK','NEW',30),(26623956,'2005-03-13',NULL,0,NULL,'CA2761','2005-01-01','STOCK','NEW',30),(26624006,'2005-03-13',NULL,0,NULL,'CA2762','2005-01-01','STOCK','NEW',30),(26624056,'2005-03-13',NULL,0,NULL,'CA2763','2005-01-01','STOCK','NEW',30),(26624156,'2005-03-13',NULL,0,NULL,'CA2770','2005-01-01','STOCK','NEW',30),(26624356,'2005-03-13',NULL,0,NULL,'CA2788','2005-01-01','STOCK','NEW',30),(26624506,'2005-03-13',NULL,0,NULL,'CA2792','2005-01-01','STOCK','NEW',30),(26624556,'2005-03-13',NULL,0,NULL,'CA2793','2005-01-01','STOCK','NEW',30),(26624706,'2005-03-13',NULL,0,NULL,'CA2799','2005-01-01','STOCK','NEW',30),(26632656,'2005-03-13',NULL,0,NULL,'CA2844','2005-01-01','STOCK','NEW',30),(26632706,'2005-03-13',NULL,0,NULL,'CA2845','2005-01-01','STOCK','NEW',30),(26632906,'2005-03-13',NULL,0,NULL,'CA2863','2005-01-01','STOCK','NEW',30),(26677506,'2005-03-13',NULL,0,NULL,'F8731','2005-01-01','STOCK','NEW',30),(257121061,'2005-03-13','2005-12-15',0,'2005-12-15','PS30661','2005-12-15','RETAIL','NEW',1),(266229561,'2005-03-13','2004-12-20',0,'2004-12-20','PS15711','2004-12-20','RETAIL','NEW',1),(266232561,'2005-03-13','2004-03-19',0,'2004-03-19','PS27021','2004-03-19','RETAIL','NEW',1),(266233561,'2005-03-13','2004-03-22',0,'2004-03-22','PS27111','2004-03-22','RETAIL','NEW',1),(266234561,'2005-03-13','2005-04-29',0,'2005-04-29','PS27131','2005-04-29','RETAIL','NEW',6),(266235561,'2005-03-13','2004-05-28',0,'2004-05-28','PS27231','2004-05-28','RETAIL','NEW',6),(266236061,'2005-03-13','2004-07-28',0,'2004-07-28','PS27291','2004-07-28','RETAIL','NEW',6),(266238561,'2005-03-13','2004-07-27',0,'2004-07-27','PS2759Z','2004-07-27','RETAIL','NEW',6),(266239561,'2005-03-13','2004-07-09',0,'2004-07-09','PS2761Z','2004-07-09','RETAIL','NEW',11),(266240061,'2005-03-13','2004-11-10',0,'2004-11-10','PS2762Z','2004-11-10','RETAIL','NEW',11),(266240561,'2005-03-13','2004-11-10',0,'2004-11-10','PS2763Z','2004-11-10','RETAIL','NEW',11),(266241561,'2005-03-13','2004-06-30',0,'2004-06-30','PS2770Z','2004-06-30','RETAIL','NEW',11),(266243561,'2005-03-13','2004-07-29',0,'2004-07-29','PS2788Z','2004-07-29','RETAIL','NEW',19),(266245061,'2005-03-13','2004-09-14',0,'2004-09-14','PS2792Z','2004-09-14','RETAIL','NEW',19),(266245561,'2005-03-13','2004-09-15',0,'2004-09-15','PS2793Z','2004-09-15','RETAIL','NEW',19),(266247061,'2005-03-13','2004-08-30',0,'2004-08-30','PS2799Z','2004-08-30','RETAIL','NEW',19);
UNLOCK TABLES;
/*!40000 ALTER TABLE `inventory_item` ENABLE KEYS */;

--
-- Table structure for table `inventory_item_composition`
--

DROP TABLE IF EXISTS `inventory_item_composition`;
CREATE TABLE `inventory_item_composition` (
  `id` bigint(20) NOT NULL,
  `part_of` bigint(20) NOT NULL,
  `part` bigint(20) NOT NULL,
  `based_on` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKEC718B015CFF26AB` (`part_of`),
  KEY `FKEC718B018C76E4FB` (`part`),
  KEY `FKEC718B01A054F238` (`based_on`),
  CONSTRAINT `FKEC718B01A054F238` FOREIGN KEY (`based_on`) REFERENCES `item_composition` (`id`),
  CONSTRAINT `FKEC718B015CFF26AB` FOREIGN KEY (`part_of`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FKEC718B018C76E4FB` FOREIGN KEY (`part`) REFERENCES `inventory_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `inventory_item_composition`
--


/*!40000 ALTER TABLE `inventory_item_composition` DISABLE KEYS */;
LOCK TABLES `inventory_item_composition` WRITE;
INSERT INTO `inventory_item_composition` VALUES (200,200,201,60),(201,200,357,62),(202,200,380,21),(203,200,391,61),(204,200,414,61),(205,200,437,11),(206,200,448,1),(207,201,202,71),(208,201,203,71),(209,201,204,71),(210,201,205,71),(211,201,206,71),(212,201,207,71),(213,201,208,72),(214,201,220,72),(215,201,232,72),(216,201,244,72),(217,201,256,72),(218,201,268,72),(219,201,280,74),(220,201,291,73),(221,201,302,73),(222,201,313,73),(223,201,324,73),(224,201,335,73),(225,201,346,73),(226,208,209,94),(227,220,221,94),(228,232,233,94),(229,244,245,94),(230,256,257,94),(231,268,269,94),(232,357,358,78),(233,357,369,80),(234,391,392,76),(235,391,403,75),(236,414,415,76),(237,414,426,75),(238,459,460,64),(239,459,483,12),(240,459,494,2),(241,459,505,66),(242,459,516,63),(243,459,672,65),(244,459,695,65),(245,459,718,22),(246,460,461,76),(247,460,472,75),(248,516,517,71),(249,516,518,71),(250,516,519,71),(251,516,520,71),(252,516,521,71),(253,516,522,71),(254,516,523,72),(255,516,535,72),(256,516,547,72),(257,516,559,72),(258,516,571,72),(259,516,583,72),(260,516,595,74),(261,516,606,73),(262,516,617,73),(263,516,628,73),(264,516,639,73),(265,516,650,73),(266,516,661,73),(267,523,524,94),(268,535,536,94),(269,547,548,94),(270,559,560,94),(271,571,572,94),(272,583,584,94),(273,672,673,78),(274,672,684,80),(275,695,696,78),(276,695,707,80),(277,729,730,13),(278,729,741,3),(279,729,752,23),(280,729,763,67),(281,729,919,68),(282,729,942,68),(283,729,965,70),(284,729,1012,69),(285,763,764,71),(286,763,765,71),(287,763,766,71),(288,763,767,71),(289,763,768,71),(290,763,769,71),(291,763,770,72),(292,763,782,72),(293,763,794,72),(294,763,806,72),(295,763,818,72),(296,763,830,72),(297,763,842,74),(298,763,853,73),(299,763,864,73),(300,763,875,73),(301,763,886,73),(302,763,897,73),(303,763,908,73),(304,770,771,94),(305,782,783,94),(306,794,795,94),(307,806,807,94),(308,818,819,94),(309,830,831,94),(310,919,920,76),(311,919,931,75),(312,942,943,76),(313,942,954,75),(314,965,966,84),(315,965,978,84),(316,965,990,83),(317,965,1001,83),(318,966,967,94),(319,978,979,94),(320,1012,1013,78),(321,1012,1024,80);
UNLOCK TABLES;
/*!40000 ALTER TABLE `inventory_item_composition` ENABLE KEYS */;

--
-- Table structure for table `inventory_item_condition`
--

DROP TABLE IF EXISTS `inventory_item_condition`;
CREATE TABLE `inventory_item_condition` (
  `item_condition` varchar(255) NOT NULL,
  PRIMARY KEY  (`item_condition`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `inventory_item_condition`
--


/*!40000 ALTER TABLE `inventory_item_condition` DISABLE KEYS */;
LOCK TABLES `inventory_item_condition` WRITE;
INSERT INTO `inventory_item_condition` VALUES ('NEW'),('REFURBISHED'),('USED');
UNLOCK TABLES;
/*!40000 ALTER TABLE `inventory_item_condition` ENABLE KEYS */;

--
-- Table structure for table `inventory_transaction`
--

DROP TABLE IF EXISTS `inventory_transaction`;
CREATE TABLE `inventory_transaction` (
  `id` bigint(20) NOT NULL auto_increment,
  `invoice_date` date default NULL,
  `invoice_number` varchar(255) default NULL,
  `sales_order_number` varchar(255) default NULL,
  `transaction_date` date default NULL,
  `transacted_item` bigint(20) default NULL,
  `seller` bigint(20) default NULL,
  `buyer` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8CD6207BF0629391` (`transacted_item`),
  KEY `FK8CD6207B7562BC05` (`buyer`),
  KEY `FK8CD6207B39C609D1` (`seller`),
  CONSTRAINT `FK8CD6207B39C609D1` FOREIGN KEY (`seller`) REFERENCES `party` (`id`),
  CONSTRAINT `FK8CD6207B7562BC05` FOREIGN KEY (`buyer`) REFERENCES `party` (`id`),
  CONSTRAINT `FK8CD6207BF0629391` FOREIGN KEY (`transacted_item`) REFERENCES `inventory_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `inventory_transaction`
--


/*!40000 ALTER TABLE `inventory_transaction` DISABLE KEYS */;
LOCK TABLES `inventory_transaction` WRITE;
INSERT INTO `inventory_transaction` VALUES (1,'2004-01-15','IN-12456','SO-22876','2004-01-15',14675556,8,7),(2,'2004-11-12','IN-23654','SO-23767','2004-11-12',14937806,8,7),(3,'2004-11-02','IN-25765','SO-34256','2004-11-02',15009556,8,7),(4,'2004-11-02','IN-27865','SO-47256','2004-11-02',15009606,8,7),(5,'2004-05-22','IN-28765','SO-65432','2004-05-22',15081406,8,7),(6,'2004-05-22','IN-33456','SO-76543','2004-05-22',15081456,8,7),(7,'2004-06-25','IN-33457','SO-76544','2004-06-25',15390206,8,7),(8,'2004-11-02','IN-33458','SO-76545','2004-11-02',15517006,8,7),(9,'2005-01-15','IN-33459','SO-76546','2005-01-15',15639806,8,7),(10,'2005-11-17','IN-33460','SO-76547','2005-11-17',17797106,8,7),(11,'2005-11-17','IN-33461','SO-76548','2005-11-17',17797156,8,7),(12,'2005-03-09','IN-33462','SO-76549','2005-03-09',17808406,8,7),(13,'2005-09-22','IN-33463','SO-76550','2005-09-22',17816256,8,7),(14,'2005-09-08','IN-33464','SO-76551','2005-09-08',17816806,8,7),(15,'2005-10-20','IN-33465','SO-76552','2005-10-20',17874156,8,7),(16,'2005-10-23','IN-33466','SO-76553','2005-10-23',17888856,8,7),(17,'2005-02-15','IN-33467','SO-76554','2005-02-15',17916706,8,7),(18,'2005-11-02','IN-33468','SO-76555','2005-11-02',17935306,8,7),(19,'2005-02-23','IN-33469','SO-76556','2005-02-23',17937106,8,7),(20,'2005-06-15','IN-33470','SO-76557','2005-06-15',17975956,8,7),(21,'2005-01-26','IN-33471','SO-76558','2005-01-26',17978706,8,7),(22,'2005-04-01','IN-33472','SO-76559','2005-04-01',17989706,8,7),(23,'2004-01-22','ALL-IN-213456','ALL-SO-113425','2004-01-22',14675556,7,9),(24,'2004-11-07','ALL-IN-324567','ALL-SO-114563','2004-11-07',14937806,7,26),(25,'2004-11-07','ALL-IN-432345','ALL-SO-134567','2004-11-07',15009556,7,27),(26,'2004-11-07','ALL-IN-346745','ALL-SO-345263','2004-11-07',15009606,7,28),(27,'2004-05-27','ALL-IN-346746','ALL-SO-345264','2004-05-27',15081406,7,29),(28,'2004-05-27','ALL-IN-346747','ALL-SO-345265','2004-05-27',15081456,7,30),(29,'2004-06-30','ALL-IN-346748','ALL-SO-345266','2004-06-30',15390206,7,9),(30,'2004-11-07','ALL-IN-346749','ALL-SO-345267','2004-11-07',15517006,7,26),(31,'2005-01-20','ALL-IN-346750','ALL-SO-345268','2005-01-20',15639806,7,27),(32,'2005-11-22','ALL-IN-346751','ALL-SO-345269','2005-11-22',17797106,7,28),(33,'2005-11-22','ALL-IN-346752','ALL-SO-345270','2005-11-22',17797156,7,29),(34,'2005-03-14','ALL-IN-346753','ALL-SO-345271','2005-03-14',17808406,7,30),(35,'2005-09-27','ALL-IN-346754','ALL-SO-345272','2005-09-27',17816256,7,9),(36,'2005-09-13','ALL-IN-346755','ALL-SO-345273','2005-09-13',17816806,7,26),(37,'2005-10-25','ALL-IN-346756','ALL-SO-345274','2005-10-25',17874156,7,27),(38,'2005-10-28','ALL-IN-346757','ALL-SO-345275','2005-10-28',17888856,7,28),(39,'2005-02-20','ALL-IN-346758','ALL-SO-345276','2005-02-20',17916706,7,29),(40,'2005-11-07','ALL-IN-346759','ALL-SO-345277','2005-11-07',17935306,7,30),(41,'2005-02-28','ALL-IN-346760','ALL-SO-345278','2005-02-28',17937106,7,9),(42,'2005-06-20','ALL-IN-346761','ALL-SO-345279','2005-06-20',17975956,7,26),(43,'2005-01-31','ALL-IN-346762','ALL-SO-345280','2005-01-31',17978706,7,27),(44,'2005-04-06','ALL-IN-346763','ALL-SO-345281','2005-04-06',17989706,7,28),(45,'2004-01-22','IN-12456','SO-22876','2004-01-22',18012856,8,7),(46,'2004-11-07','IN-23654','SO-23767','2004-11-07',18016356,8,7),(47,'2004-11-07','IN-25765','SO-34256','2004-11-07',18046406,8,7),(48,'2004-11-07','IN-27865','SO-47256','2004-11-07',18049156,8,7),(49,'2004-05-27','IN-28765','SO-65432','2004-05-27',18049506,8,7),(50,'2004-05-27','IN-33456','SO-76543','2004-05-27',18061806,8,7),(51,'2004-06-30','IN-33457','SO-76544','2004-06-30',18087756,8,7),(52,'2004-11-07','IN-33458','SO-76545','2004-11-07',18154306,8,7),(53,'2005-01-20','IN-33459','SO-76546','2005-01-20',18154406,8,7),(54,'2005-11-22','IN-33460','SO-76547','2005-11-22',18154456,8,7),(55,'2005-11-22','IN-33461','SO-76548','2005-11-22',18202356,8,7),(56,'2005-03-14','IN-33462','SO-76549','2005-03-14',18259506,8,7),(57,'2005-09-27','IN-33463','SO-76550','2005-09-27',18891806,8,7),(58,'2005-09-13','IN-33464','SO-76551','2005-09-13',18891856,8,7),(59,'2005-10-25','IN-33465','SO-76552','2005-10-25',18892056,8,7),(60,'2005-10-28','IN-33466','SO-76553','2005-10-28',18892106,8,7),(61,'2005-02-20','IN-33467','SO-76554','2005-02-20',18892156,8,7),(62,'2005-11-07','IN-33468','SO-76555','2005-11-07',18892206,8,7),(63,'2005-02-28','IN-33469','SO-76556','2005-02-28',18892256,8,7),(64,'2005-06-20','IN-33470','SO-76557','2005-06-20',18892306,8,7),(65,'2005-01-31','IN-33471','SO-76558','2005-01-31',18892356,8,7),(66,'2005-04-06','IN-33472','SO-76559','2005-04-06',18892606,8,7);
UNLOCK TABLES;
/*!40000 ALTER TABLE `inventory_transaction` ENABLE KEYS */;

--
-- Table structure for table `inventory_type`
--

DROP TABLE IF EXISTS `inventory_type`;
CREATE TABLE `inventory_type` (
  `type` varchar(255) NOT NULL,
  PRIMARY KEY  (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `inventory_type`
--


/*!40000 ALTER TABLE `inventory_type` DISABLE KEYS */;
LOCK TABLES `inventory_type` WRITE;
INSERT INTO `inventory_type` VALUES ('RETAIL'),('STOCK');
UNLOCK TABLES;
/*!40000 ALTER TABLE `inventory_type` ENABLE KEYS */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` bigint(20) NOT NULL auto_increment,
  `cost_amt` decimal(19,2) default NULL,
  `cost_curr` varchar(255) default NULL,
  `description` varchar(255) NOT NULL,
  `make` varchar(255) NOT NULL,
  `model` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `number` varchar(255) NOT NULL,
  `serialized` bit(1) NOT NULL,
  `owned_by` bigint(20) NOT NULL,
  `product_type` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK317B13D2E1FA83` (`owned_by`),
  KEY `FK317B138B4E6FEC` (`product_type`),
  CONSTRAINT `FK317B138B4E6FEC` FOREIGN KEY (`product_type`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FK317B13D2E1FA83` FOREIGN KEY (`owned_by`) REFERENCES `party` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item`
--


/*!40000 ALTER TABLE `item` DISABLE KEYS */;
LOCK TABLES `item` WRITE;
INSERT INTO `item` VALUES (1,'1.00','USD','valve 1','Ingersoll Rand','Valve',' valve 1','PRTVLV1','',8,NULL),(2,'1.00','USD','valve 1 Supplied By NorthWind','North Wind','Valve',' valve1 Supplied By NorthWind','PRTVLV1-NW','',31,NULL),(3,'1.00','USD','valve 1 Supplied By TruckDrove','TruckDrove','Valve',' valve1 Supplied By TruckDrove','PRTVLV1-TD','',34,NULL),(4,'1.00','USD','valve 1 Supplied By Backyard','Backyard','Valve',' valve1 Supplied By Backyard','PRTVLV1-BY','',35,NULL),(5,'1.00','USD','valve 1 Supplied By ASG','ASG','Valve',' valve1 Supplied By ASG','PRTVLV1-ASG','',36,NULL),(6,'2.00','USD','valve 2','Ingersoll Rand','Valve',' valve 2','PRTVLV2','',8,NULL),(7,'1.00','USD','valve 2 Supplied By NorthWind','North Wind','Valve',' valve2 Supplied By NorthWind','PRTVLV2-NW','',31,NULL),(8,'1.00','USD','valve 2 Supplied By TruckDrove','TruckDrove','Valve',' valve2 Supplied By TruckDrove','PRTVLV2-TD','',34,NULL),(9,'1.00','USD','valve 2 Supplied By Backyard','Backyard','Valve',' valve2 Supplied By Backyard','PRTVLV2-BY','',35,NULL),(10,'1.00','USD','valve 2 Supplied By ASG','ASG','Valve',' valve2 Supplied By ASG','PRTVLV2-ASG','',36,NULL),(11,'1.00','USD','valve 3','Ingersoll Rand','Valve',' valve 3','PRTVLV3','',8,NULL),(12,'1.00','USD','valve 3 Supplied By NorthWind','North Wind','Valve',' valve3 Supplied By NorthWind','PRTVLV3-NW','',31,NULL),(13,'1.00','USD','valve 3 Supplied By TruckDrove','TruckDrove','Valve',' valve3 Supplied By TruckDrove','PRTVLV3-TD','',34,NULL),(14,'1.00','USD','valve 3 Supplied By Backyard','Backyard','Valve',' valve3 Supplied By Backyard','PRTVLV3-BY','',35,NULL),(15,'1.00','USD','valve 3 Supplied By ASG','ASG','Valve',' valve3 Supplied By ASG','PRTVLV3-ASG','',36,NULL),(16,'1.00','USD','hose 1','Ingersoll Rand','Hose',' hose 1','PRTHOSE1','\0',8,NULL),(17,'2.00','USD','hose 2','Ingersoll Rand','Hose',' hose 2','PRTHOSE2','\0',8,NULL),(18,'1.00','USD','hose 3','Ingersoll Rand','Hose',' hose 3','PRTHOSE3','\0',8,NULL),(19,'1.00','USD','UNIGY_50HZ1','Ingersoll Rand','UNIGY_50HZ1','UNIGY_50HZ 1','ATTCH-UNIGY-50-HZ-1','',8,8),(20,'2.00','USD','UNIGY_50HZ2','Ingersoll Rand','UNIGY_50HZ2','UNIGY_50HZ 2','ATTCH-UNIGY-50-HZ-2','',8,8),(21,'1.00','USD','UNIGY_50HZ3','Ingersoll Rand','UNIGY_50HZ3','UNIGY_50HZ 3','ATTCH-UNIGY-50-HZ-3','',8,8),(22,'1.00','USD','UNIGY_60HZ1','Ingersoll Rand','UNIGY_60HZ1','UNIGY_60HZ 1','ATTCH-UNIGY-60-HZ-1','',8,8),(23,'2.00','USD','UNIGY_60HZ2','Ingersoll Rand','UNIGY_60HZ2','UNIGY_60HZ 2','ATTCH-UNIGY-60-HZ-2','',8,8),(24,'1.00','USD','UNIGY_60HZ3','Ingersoll Rand','UNIGY_60HZ3','UNIGY_60HZ 3','ATTCH-UNIGY-60-HZ-3','',8,8),(25,'1.00','USD','UNIGY_70HZ1','Ingersoll Rand','UNIGY_70HZ1','UNIGY_70HZ 1','ATTCH-UNIGY-70-HZ-1','',8,8),(26,'2.00','USD','UNIGY_70HZ2','Ingersoll Rand','UNIGY_70HZ2','UNIGY_70HZ 2','ATTCH-UNIGY-70-HZ-2','',8,8),(27,'1.00','USD','UNIGY_70HZ3','Ingersoll Rand','UNIGY_70HZ3','UNIGY_70HZ 3','ATTCH-UNIGY-70-HZ-3','',8,8),(28,'1.00','USD','COUGAR_50HZ1','Ingersoll Rand','COUGAR_50HZ1','COUGAR_50HZ 1','MC-COUGAR-50-HZ-1','',8,5),(29,'2.00','USD','COUGAR_50HZ2','Ingersoll Rand','COUGAR_50HZ2','COUGAR_50HZ 2','MC-COUGAR-50-HZ-2','',8,5),(30,'1.00','USD','COUGAR_50HZ3','Ingersoll Rand','COUGAR_50HZ3','COUGAR_50HZ 3','MC-COUGAR-50-HZ-3','',8,5),(31,'1.00','USD','COUGAR_60HZ1','Ingersoll Rand','COUGAR_60HZ1','COUGAR_60HZ 1','MC-COUGAR-60-HZ-1','',8,5),(32,'2.00','USD','COUGAR_60HZ2','Ingersoll Rand','COUGAR_60HZ2','COUGAR_60HZ 2','MC-COUGAR-60-HZ-2','',8,5),(33,'1.00','USD','COUGAR_60HZ3','Ingersoll Rand','COUGAR_60HZ3','COUGAR_60HZ 3','MC-COUGAR-60-HZ-3','',8,5),(34,'1.00','USD','COUGAR_70HZ1','Ingersoll Rand','COUGAR_70HZ1','COUGAR_70HZ 1','MC-COUGAR-70-HZ-1','',8,5),(35,'2.00','USD','COUGAR_70HZ2','Ingersoll Rand','COUGAR_70HZ2','COUGAR_70HZ 2','MC-COUGAR-70-HZ-2','',8,5),(36,'1.00','USD','COUGAR_70HZ3','Ingersoll Rand','COUGAR_70HZ3','COUGAR_70HZ 3','MC-COUGAR-70-HZ-3','',8,5),(37,'1.00','USD','PEGASUS_50HZ1','Ingersoll Rand','PEGASUS_50HZ1','PEGASUS_50HZ 1','MC-PEGASUS-50-HZ-1','',8,6),(38,'2.00','USD','PEGASUS_50HZ2','Ingersoll Rand','PEGASUS_50HZ2','PEGASUS_50HZ 2','MC-PEGASUS-50-HZ-2','',8,6),(39,'1.00','USD','PEGASUS_50HZ3','Ingersoll Rand','PEGASUS_50HZ3','PEGASUS_50HZ 3','MC-PEGASUS-50-HZ-3','',8,6),(40,'1.00','USD','PEGASUS_60HZ1','Ingersoll Rand','PEGASUS_60HZ1','PEGASUS_60HZ 1','MC-PEGASUS-60-HZ-1','',8,6),(41,'2.00','USD','PEGASUS_60HZ2','Ingersoll Rand','PEGASUS_60HZ2','PEGASUS_60HZ 2','MC-PEGASUS-60-HZ-2','',8,6),(42,'1.00','USD','PEGASUS_60HZ3','Ingersoll Rand','PEGASUS_60HZ3','PEGASUS_60HZ 3','MC-PEGASUS-60-HZ-3','',8,6),(43,'1.00','USD','PEGASUS_70HZ1','Ingersoll Rand','PEGASUS_70HZ1','PEGASUS_70HZ 1','MC-PEGASUS-70-HZ-1','',8,6),(44,'2.00','USD','PEGASUS_70HZ2','Ingersoll Rand','PEGASUS_70HZ2','PEGASUS_70HZ 2','MC-PEGASUS-70-HZ-2','',8,6),(45,'1.00','USD','PEGASUS_70HZ3','Ingersoll Rand','PEGASUS_70HZ3','PEGASUS_70HZ 3','MC-PEGASUS-70-HZ-3','',8,6),(46,'1.00','USD','DRYER_50HZ1','Ingersoll Rand','DRYER_50HZ1','DRYER_50HZ 1','MC-DRYER-50-HZ-1','',8,7),(47,'2.00','USD','DRYER_50HZ2','Ingersoll Rand','DRYER_50HZ2','DRYER_50HZ 2','MC-DRYER-50-HZ-2','',8,7),(48,'1.00','USD','DRYER_50HZ3','Ingersoll Rand','DRYER_50HZ3','DRYER_50HZ 3','MC-DRYER-50-HZ-3','',8,7),(49,'1.00','USD','DRYER_60HZ1','Ingersoll Rand','DRYER_60HZ1','DRYER_60HZ 1','MC-DRYER-60-HZ-1','',8,7),(50,'2.00','USD','DRYER_60HZ2','Ingersoll Rand','DRYER_60HZ2','DRYER_60HZ 2','MC-DRYER-60-HZ-2','',8,7),(51,'1.00','USD','DRYER_60HZ3','Ingersoll Rand','DRYER_60HZ3','DRYER_60HZ 3','MC-DRYER-60-HZ-3','',8,7),(52,'1.00','USD','DRYER_70HZ1','Ingersoll Rand','DRYER_70HZ1','DRYER_70HZ 1','MC-DRYER-70-HZ-1','',8,7),(53,'2.00','USD','DRYER_70HZ2','Ingersoll Rand','DRYER_70HZ2','DRYER_70HZ 2','MC-DRYER-70-HZ-2','',8,7),(54,'1.00','USD','DRYER_70HZ3','Ingersoll Rand','DRYER_70HZ3','DRYER_70HZ 3','MC-DRYER-70-HZ-3','',8,7),(55,'1.00','USD','COMP_20L_1','Ingersoll Rand','COMP_20L_1','COMP_20L_1','PRTCOMP20L1','',8,NULL),(56,'1.00','USD','CONT_AZ_1','Ingersoll Rand','CONT_AZ_1','COMP_AZ_1','PRTCONTAZ1','',8,NULL),(57,'1.00','USD','MOTOR_6000RPM_Z','Ingersoll Rand','MOTOR_6000RPM_Z','MOTOR_6000RPM_Z','PRTMOTOR6000RPMZ','',8,NULL),(58,'1.00','USD','HEATER_20W','Ingersoll Rand','HEATER_20W','HEATER_20W','PRTHEATER20W','',8,NULL),(59,'1.00','USD','ENGINE_81BHP','Ingersoll Rand','ENGINE_81BHP','ENGINE_81BHP','PRTENGINE81BHP','',8,NULL),(60,'1.00','USD','CYL_80CC','Ingersoll Rand','CYL_80CC','CYL_80CC','PRTCYL80CC','',8,NULL),(61,'1.00','USD','PISTON_10MM','Ingersoll Rand','PISTON_10MM','PISTON_10MM','PRTPISTON10MM','',8,NULL),(62,'1.00','USD','DISC_10MM','Ingersoll Rand','DISC_10MM','DISC_10MM','PRTDISC10MM','',8,NULL),(63,'1.00','USD','CSHAFT_10MM','Ingersoll Rand','CSHAFT_10MM','CSHAFT_10MM','PRTCSHAFT10MM','',8,NULL),(64,'1.00','USD','Mother Board','Ingersoll Rand','INTEL 505','Intel 505 MotherBoard','PRTMBINTEL505','',8,NULL),(65,'1.00','USD','LCD Display','Ingersoll Rand','LG 45 LCD Display','LG 45 LCD Display','PRTLCDDLG45','',8,NULL),(66,'1.00','USD','Solenoid','Ingersoll Rand','Solenoid','Solenoid','PRTSLND10','\0',8,NULL),(67,'1.00','USD','Shaft','Ingersoll Rand','Shaft','Shaft','PRTSHAFT10','',8,NULL),(68,'1.00','USD','Wiring','Ingersoll Rand','Wiring','Wiring','PRTWIRE10','\0',8,NULL),(69,'1.00','USD','Axle','Ingersoll Rand','Axle','Axle','PRTAXLE10','',8,NULL),(70,'1.00','USD','Connector','Ingersoll Rand','Connector','Connector','PRTCONCTR10','\0',8,NULL),(71,'1.00','USD','Casing','Ingersoll Rand','Casing','Casing','PRTCASE10','\0',8,NULL),(72,'1.00','USD','Connecting Rod','Ingersoll Rand','Connecting Rod','Connecting Rod','PRTCNTRD10','\0',8,NULL),(73,'1.00','USD','Spark plug','Ingersoll Rand','Spark plug','Spark plug','PRTSPLUG10','\0',8,NULL),(74,'1.00','USD','Nut','Ingersoll Rand','Nut','Nut','PRTNUT10','\0',8,NULL),(75,'1.00','USD','Bolt','Ingersoll Rand','Bolt','Bolt','PRTBOLT10','\0',8,NULL),(76,'1.00','USD','Washer','Ingersoll Rand','Washer','Washer','PRTWASHER10','\0',8,NULL),(77,'1.00','USD','Disc 10\'\'','Ingersoll Rand','Disc 10\'\'','Disc 10\'\'','PRTDISC10I','',8,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `item` ENABLE KEYS */;

--
-- Table structure for table `item_base_price`
--

DROP TABLE IF EXISTS `item_base_price`;
CREATE TABLE `item_base_price` (
  `id` bigint(20) NOT NULL auto_increment,
  `for_item` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `for_item` (`for_item`),
  KEY `FKB8353B0773F935E6` (`for_item`),
  CONSTRAINT `FKB8353B0773F935E6` FOREIGN KEY (`for_item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_base_price`
--


/*!40000 ALTER TABLE `item_base_price` DISABLE KEYS */;
LOCK TABLES `item_base_price` WRITE;
INSERT INTO `item_base_price` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10),(11,11),(12,12),(13,13),(14,14),(15,15),(16,16),(17,17),(18,18),(19,19),(20,20),(21,21),(22,22),(23,23),(24,24),(25,25),(26,26),(27,27),(28,28),(29,29),(30,30),(31,31),(32,32),(33,33),(34,34),(35,35),(36,36),(37,37),(38,38),(39,39);
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_base_price` ENABLE KEYS */;

--
-- Table structure for table `item_composition`
--

DROP TABLE IF EXISTS `item_composition`;
CREATE TABLE `item_composition` (
  `id` bigint(20) NOT NULL,
  `quantity` int(11) default NULL,
  `item` bigint(20) NOT NULL,
  `part_of` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8F75DE7E29359EC0` (`part_of`),
  KEY `FK8F75DE7E58AA7430` (`item`),
  CONSTRAINT `FK8F75DE7E58AA7430` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK8F75DE7E29359EC0` FOREIGN KEY (`part_of`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_composition`
--


/*!40000 ALTER TABLE `item_composition` DISABLE KEYS */;
LOCK TABLES `item_composition` WRITE;
INSERT INTO `item_composition` VALUES (1,1,1,28),(2,1,1,29),(3,1,1,30),(4,1,1,31),(5,1,1,32),(6,1,1,33),(7,1,1,34),(8,1,1,35),(9,1,1,36),(11,1,6,28),(12,1,6,29),(13,1,6,30),(14,1,6,31),(15,1,6,32),(16,1,6,33),(17,1,6,34),(18,1,6,35),(19,1,6,36),(21,1,11,28),(22,1,11,29),(23,1,11,30),(24,1,11,31),(25,1,11,32),(26,1,11,33),(27,1,11,34),(28,1,11,35),(29,1,11,36),(31,1,16,28),(32,1,16,29),(33,1,16,30),(34,1,16,31),(35,1,16,32),(36,1,16,33),(37,1,16,34),(38,1,16,35),(39,1,16,36),(41,1,17,28),(42,1,17,29),(43,1,17,30),(44,1,17,31),(45,1,17,32),(46,1,17,33),(47,1,17,34),(48,1,17,35),(49,1,17,36),(51,1,18,28),(52,1,18,29),(53,1,18,30),(54,1,18,31),(55,1,18,32),(56,1,18,33),(57,1,18,34),(58,1,18,35),(59,1,18,36),(60,1,55,28),(61,2,56,28),(62,1,57,28),(63,1,55,29),(64,1,56,29),(65,2,57,29),(66,1,58,29),(67,1,55,30),(68,2,56,30),(69,1,57,30),(70,1,59,30),(71,6,60,55),(72,6,61,55),(73,6,62,55),(74,1,63,55),(75,1,64,56),(76,1,65,56),(77,1,66,57),(78,1,67,57),(79,1,68,57),(80,1,69,57),(81,1,70,57),(82,1,71,59),(83,2,63,59),(84,2,61,59),(85,2,72,59),(86,1,73,59),(87,1,71,60),(88,10,74,60),(89,10,75,60),(90,10,76,60),(91,4,74,61),(92,4,75,61),(93,4,76,61),(94,1,77,61);
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_composition` ENABLE KEYS */;

--
-- Table structure for table `item_group`
--

DROP TABLE IF EXISTS `item_group`;
CREATE TABLE `item_group` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` varchar(255) NOT NULL,
  `group_code` varchar(255) default NULL,
  `item_group_type` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `depth` int(11) NOT NULL,
  `lft` int(11) NOT NULL,
  `rgt` int(11) NOT NULL,
  `tree_id` bigint(20) NOT NULL,
  `scheme` bigint(20) default NULL,
  `is_part_of` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8AFCFA5378FEC6F0` (`is_part_of`),
  KEY `FK8AFCFA53695646C7` (`scheme`),
  CONSTRAINT `FK8AFCFA53695646C7` FOREIGN KEY (`scheme`) REFERENCES `item_scheme` (`id`),
  CONSTRAINT `FK8AFCFA5378FEC6F0` FOREIGN KEY (`is_part_of`) REFERENCES `item_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_group`
--


/*!40000 ALTER TABLE `item_group` DISABLE KEYS */;
LOCK TABLES `item_group` WRITE;
INSERT INTO `item_group` VALUES (1,' ASG Division','ASG','DIVISION','ASG',1,1,46,1,2,NULL),(2,' Machine Products','Machine','PRODUCT TYPE','Machine',2,2,27,1,2,1),(3,' Attachment Products','Attachment','PRODUCT TYPE','Attachment',2,28,37,1,2,1),(4,' Parts Products','Parts','PRODUCT TYPE','Parts',2,38,45,1,2,1),(5,'Machine Product 1','COUGAR','PRODUCT','COUGAR',3,3,10,1,2,2),(6,'Machine Product 2','PEGASUS','PRODUCT','PEGASUS',3,11,18,1,2,2),(7,'Machine Product 3','DRYER','PRODUCT','DRYER',3,19,26,1,2,2),(8,'Attachment Product 1','UNIGY','PRODUCT','UNIGY',3,29,36,1,2,3),(9,'Valve parts','Valve','MODEL','Valve',3,39,40,1,2,4),(10,'Hose parts','Hose','MODEL','Hose',3,41,42,1,2,4),(11,'UNIGY Model 50 HZ','UNIGY_50HZ','MODEL','UNIGY_50HZ',4,30,31,1,2,8),(12,'UNIGY Model 60 HZ','UNIGY_60HZ','MODEL','UNIGY_60HZ',4,32,33,1,2,8),(13,'UNIGY Model 70 HZ','UNIGY_70HZ','MODEL','UNIGY_70HZ',4,34,35,1,2,8),(14,'COUGAR Model 50 HZ','COUGAR_50HZ','MODEL','COUGAR_50HZ',4,4,5,1,2,5),(15,'COUGAR Model 60 HZ','COUGAR_60HZ','MODEL','COUGAR_60HZ',4,6,7,1,2,5),(16,'COUGAR Model 70 HZ','COUGAR_70HZ','MODEL','COUGAR_70HZ',4,8,9,1,2,5),(17,'PEGASUS Model 50 HZ','PEGASUS_50HZ','MODEL','PEGASUS_50HZ',4,12,13,1,2,6),(18,'PEGASUS Model 60 HZ','PEGASUS_60HZ','MODEL','PEGASUS_60HZ',4,14,15,1,2,6),(19,'PEGASUS Model 70 HZ','PEGASUS_70HZ','MODEL','PEGASUS_70HZ',4,16,17,1,2,6),(20,'DRYER Model 50 HZ','DRYER_50HZ','MODEL','DRYER_50HZ',4,20,21,1,2,7),(21,'DRYER Model 60 HZ','DRYER_60HZ','MODEL','DRYER_60HZ',4,22,23,1,2,7),(22,'DRYER Model 70 HZ','DRYER_70HZ','MODEL','DRYER_70HZ',4,24,25,1,2,7),(23,'All Other Parts','All Parts','MODEL','All Other Parts',3,43,44,1,2,4);
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_group` ENABLE KEYS */;

--
-- Table structure for table `item_lines`
--

DROP TABLE IF EXISTS `item_lines`;
CREATE TABLE `item_lines` (
  `for_group` bigint(20) NOT NULL,
  `line_items` bigint(20) NOT NULL,
  UNIQUE KEY `line_items` (`line_items`),
  KEY `FK8B3F52D3694DCA4B` (`line_items`),
  KEY `FK8B3F52D3F03D8912` (`for_group`),
  CONSTRAINT `FK8B3F52D3F03D8912` FOREIGN KEY (`for_group`) REFERENCES `line_item_group` (`id`),
  CONSTRAINT `FK8B3F52D3694DCA4B` FOREIGN KEY (`line_items`) REFERENCES `line_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_lines`
--


/*!40000 ALTER TABLE `item_lines` DISABLE KEYS */;
LOCK TABLES `item_lines` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_lines` ENABLE KEYS */;

--
-- Table structure for table `item_mapping`
--

DROP TABLE IF EXISTS `item_mapping`;
CREATE TABLE `item_mapping` (
  `id` bigint(20) NOT NULL auto_increment,
  `to_item` bigint(20) default NULL,
  `from_item` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKE01677A25D434FA5` (`from_item`),
  KEY `FKE01677A213A1F374` (`to_item`),
  CONSTRAINT `FKE01677A213A1F374` FOREIGN KEY (`to_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FKE01677A25D434FA5` FOREIGN KEY (`from_item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_mapping`
--


/*!40000 ALTER TABLE `item_mapping` DISABLE KEYS */;
LOCK TABLES `item_mapping` WRITE;
INSERT INTO `item_mapping` VALUES (1,2,1),(2,3,1),(3,7,6),(4,13,11),(5,20,19),(6,21,19);
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_mapping` ENABLE KEYS */;

--
-- Table structure for table `item_price_criteria`
--

DROP TABLE IF EXISTS `item_price_criteria`;
CREATE TABLE `item_price_criteria` (
  `id` bigint(20) NOT NULL auto_increment,
  `claim_type` varchar(255) default NULL,
  `relevance_score` bigint(20) default NULL,
  `warranty_type` varchar(255) default NULL,
  `for_criteria_product_type` bigint(20) default NULL,
  `item_criterion_item` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer_group` bigint(20) default NULL,
  `item_criterion_item_group` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK41F0CC41DE6125BA` (`item_criterion_item`),
  KEY `FK41F0CC411BC64DD6` (`for_criteria_product_type`),
  KEY `FK41F0CC41BDF2C045` (`for_criteria_dealer_criterion_dealer_group`),
  KEY `FK41F0CC41721C13BF` (`item_criterion_item_group`),
  KEY `FK41F0CC413BC5B29C` (`for_criteria_dealer_criterion_dealer`),
  CONSTRAINT `FK41F0CC413BC5B29C` FOREIGN KEY (`for_criteria_dealer_criterion_dealer`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FK41F0CC411BC64DD6` FOREIGN KEY (`for_criteria_product_type`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FK41F0CC41721C13BF` FOREIGN KEY (`item_criterion_item_group`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FK41F0CC41BDF2C045` FOREIGN KEY (`for_criteria_dealer_criterion_dealer_group`) REFERENCES `dealer_group` (`id`),
  CONSTRAINT `FK41F0CC41DE6125BA` FOREIGN KEY (`item_criterion_item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_price_criteria`
--


/*!40000 ALTER TABLE `item_price_criteria` DISABLE KEYS */;
LOCK TABLES `item_price_criteria` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_price_criteria` ENABLE KEYS */;

--
-- Table structure for table `item_price_modifier`
--

DROP TABLE IF EXISTS `item_price_modifier`;
CREATE TABLE `item_price_modifier` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date NOT NULL,
  `till_date` date NOT NULL,
  `scaling_factor` decimal(19,2) default NULL,
  `parent` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKA8E6EFB973E32D0F` (`parent`),
  CONSTRAINT `FKA8E6EFB973E32D0F` FOREIGN KEY (`parent`) REFERENCES `item_price_criteria` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_price_modifier`
--


/*!40000 ALTER TABLE `item_price_modifier` DISABLE KEYS */;
LOCK TABLES `item_price_modifier` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_price_modifier` ENABLE KEYS */;

--
-- Table structure for table `item_scheme`
--

DROP TABLE IF EXISTS `item_scheme`;
CREATE TABLE `item_scheme` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_scheme`
--


/*!40000 ALTER TABLE `item_scheme` DISABLE KEYS */;
LOCK TABLES `item_scheme` WRITE;
INSERT INTO `item_scheme` VALUES (1,'Item Group Scheme'),(2,'Prod Struct Scheme');
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_scheme` ENABLE KEYS */;

--
-- Table structure for table `item_scheme_purposes`
--

DROP TABLE IF EXISTS `item_scheme_purposes`;
CREATE TABLE `item_scheme_purposes` (
  `item_scheme` bigint(20) NOT NULL,
  `purposes` bigint(20) NOT NULL,
  PRIMARY KEY  (`item_scheme`,`purposes`),
  UNIQUE KEY `purposes` (`purposes`),
  KEY `FKA6A17743CFD0EB05` (`purposes`),
  KEY `FKA6A1774387BABD73` (`item_scheme`),
  CONSTRAINT `FKA6A1774387BABD73` FOREIGN KEY (`item_scheme`) REFERENCES `item_scheme` (`id`),
  CONSTRAINT `FKA6A17743CFD0EB05` FOREIGN KEY (`purposes`) REFERENCES `purpose` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `item_scheme_purposes`
--


/*!40000 ALTER TABLE `item_scheme_purposes` DISABLE KEYS */;
LOCK TABLES `item_scheme_purposes` WRITE;
INSERT INTO `item_scheme_purposes` VALUES (1,1),(1,2),(2,6);
UNLOCK TABLES;
/*!40000 ALTER TABLE `item_scheme_purposes` ENABLE KEYS */;

--
-- Table structure for table `items_in_group`
--

DROP TABLE IF EXISTS `items_in_group`;
CREATE TABLE `items_in_group` (
  `item_group` bigint(20) NOT NULL,
  `item` bigint(20) NOT NULL,
  PRIMARY KEY  (`item_group`,`item`),
  KEY `FKE6763884D9D231B5` (`item_group`),
  KEY `FKE676388458AA7430` (`item`),
  CONSTRAINT `FKE676388458AA7430` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FKE6763884D9D231B5` FOREIGN KEY (`item_group`) REFERENCES `item_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `items_in_group`
--


/*!40000 ALTER TABLE `items_in_group` DISABLE KEYS */;
LOCK TABLES `items_in_group` WRITE;
INSERT INTO `items_in_group` VALUES (9,1),(9,2),(9,4),(9,5),(9,6),(9,7),(9,9),(9,10),(9,11),(9,12),(9,14),(9,15),(10,16),(10,17),(10,18),(11,19),(11,20),(11,21),(12,22),(12,23),(12,24),(13,25),(13,26),(13,27),(14,28),(14,29),(14,30),(15,31),(15,32),(15,33),(16,34),(16,35),(16,36),(17,37),(17,38),(17,39),(18,40),(18,41),(18,42),(19,43),(19,44),(19,45),(20,46),(20,47),(20,48),(21,49),(21,50),(21,51),(22,52),(22,53),(22,54),(23,55),(23,56),(23,57),(23,58),(23,59),(23,60),(23,61),(23,62),(23,63),(23,64),(23,65),(23,66),(23,67),(23,68),(23,69),(23,70),(23,71),(23,72),(23,73),(23,74),(23,75),(23,76),(23,77);
UNLOCK TABLES;
/*!40000 ALTER TABLE `items_in_group` ENABLE KEYS */;

--
-- Table structure for table `jbpm_action`
--

DROP TABLE IF EXISTS `jbpm_action`;
CREATE TABLE `jbpm_action` (
  `id_` bigint(20) NOT NULL auto_increment,
  `class` char(1) NOT NULL,
  `name_` varchar(255) default NULL,
  `ispropagationallowed_` bit(1) default NULL,
  `actionexpression_` varchar(255) default NULL,
  `isasync_` bit(1) default NULL,
  `referencedaction_` bigint(20) default NULL,
  `actiondelegation_` bigint(20) default NULL,
  `event_` bigint(20) default NULL,
  `processdefinition_` bigint(20) default NULL,
  `timername_` varchar(255) default NULL,
  `duedate_` varchar(255) default NULL,
  `repeat_` varchar(255) default NULL,
  `transitionname_` varchar(255) default NULL,
  `timeraction_` bigint(20) default NULL,
  `expression_` text,
  `eventindex_` int(11) default NULL,
  `exceptionhandler_` bigint(20) default NULL,
  `exceptionhandlerindex_` int(11) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_ACTION_EVENT` (`event_`),
  KEY `FK_ACTION_EXPTHDL` (`exceptionhandler_`),
  KEY `FK_ACTION_PROCDEF` (`processdefinition_`),
  KEY `FK_CRTETIMERACT_TA` (`timeraction_`),
  KEY `FK_ACTION_ACTNDEL` (`actiondelegation_`),
  KEY `FK_ACTION_REFACT` (`referencedaction_`),
  CONSTRAINT `FK_ACTION_REFACT` FOREIGN KEY (`referencedaction_`) REFERENCES `jbpm_action` (`id_`),
  CONSTRAINT `FK_ACTION_ACTNDEL` FOREIGN KEY (`actiondelegation_`) REFERENCES `jbpm_delegation` (`id_`),
  CONSTRAINT `FK_ACTION_EVENT` FOREIGN KEY (`event_`) REFERENCES `jbpm_event` (`id_`),
  CONSTRAINT `FK_ACTION_EXPTHDL` FOREIGN KEY (`exceptionhandler_`) REFERENCES `jbpm_exceptionhandler` (`id_`),
  CONSTRAINT `FK_ACTION_PROCDEF` FOREIGN KEY (`processdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`),
  CONSTRAINT `FK_CRTETIMERACT_TA` FOREIGN KEY (`timeraction_`) REFERENCES `jbpm_action` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_action`
--


/*!40000 ALTER TABLE `jbpm_action` DISABLE KEYS */;
LOCK TABLES `jbpm_action` WRITE;
INSERT INTO `jbpm_action` VALUES (1,'S',NULL,'',NULL,'\0',NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,'part.getActivePartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.SHIPMENT_GENERATED)',0,NULL,NULL),(2,'A',NULL,'',NULL,'\0',NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'C',NULL,'',NULL,'\0',NULL,NULL,2,NULL,'Due Parts','20 business hours',NULL,NULL,2,NULL,0,NULL,NULL),(4,'A',NULL,'',NULL,'\0',NULL,3,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),(5,'I',NULL,'',NULL,'\0',NULL,NULL,4,NULL,'Due Parts',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),(6,'S',NULL,'',NULL,'\0',NULL,NULL,5,NULL,NULL,NULL,NULL,NULL,NULL,'part.getActivePartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.SHIPMENT_GENERATED)',0,NULL,NULL),(7,'S',NULL,'',NULL,'\0',NULL,NULL,6,NULL,NULL,NULL,NULL,NULL,NULL,'part.getActivePartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_SHIPPED)',0,NULL,NULL),(8,'S',NULL,'',NULL,'\0',NULL,NULL,7,NULL,NULL,NULL,NULL,NULL,NULL,'part.getActivePartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_TO_BE_SHIPPED)',0,NULL,NULL),(9,'S',NULL,'',NULL,'\0',NULL,NULL,8,NULL,NULL,NULL,NULL,NULL,NULL,'part.getPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_RECEIVED)',0,NULL,NULL),(10,'S',NULL,'',NULL,'\0',NULL,NULL,9,NULL,NULL,NULL,NULL,NULL,NULL,'part.getPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_RECEIVED)',0,NULL,NULL),(11,'S',NULL,'',NULL,'\0',NULL,NULL,10,NULL,NULL,NULL,NULL,NULL,NULL,'part.getPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_SHIPPED)',0,NULL,NULL),(12,'S',NULL,'',NULL,'\0',NULL,NULL,11,NULL,NULL,NULL,NULL,NULL,NULL,'part.getPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.CLOSE)',0,NULL,NULL),(13,'S',NULL,'',NULL,'\0',NULL,NULL,12,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_TO_BE_SHIPPED)',0,NULL,NULL),(14,'S',NULL,'',NULL,'\0',NULL,NULL,13,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_SHIPPED)',0,NULL,NULL),(15,'S',NULL,'',NULL,'\0',NULL,NULL,14,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_ACCEPTED)',0,NULL,NULL),(16,'S',NULL,'',NULL,'\0',NULL,NULL,15,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_REJECTED)',0,NULL,NULL),(17,'S',NULL,'',NULL,'\0',NULL,NULL,16,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.SHIPMENT_GENERATED)',0,NULL,NULL),(18,'S',NULL,'',NULL,'\0',NULL,NULL,17,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_SHIPPED)',0,NULL,NULL),(19,'S',NULL,'',NULL,'\0',NULL,NULL,18,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_TO_BE_SHIPPED)',0,NULL,NULL),(20,'S',NULL,'',NULL,'\0',NULL,NULL,19,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_ACCEPTED)',0,NULL,NULL),(21,'S',NULL,'',NULL,'\0',NULL,NULL,20,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_REJECTED)',0,NULL,NULL),(22,'S',NULL,'',NULL,'\0',NULL,NULL,21,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_TO_BE_SHIPPED)',0,NULL,NULL),(23,'S',NULL,'',NULL,'\0',NULL,NULL,22,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_ACCEPTED)',0,NULL,NULL),(24,'S',NULL,'',NULL,'\0',NULL,NULL,23,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_REJECTED)',0,NULL,NULL),(25,'S',NULL,'',NULL,'\0',NULL,NULL,24,NULL,NULL,NULL,NULL,NULL,NULL,'part.getSupplierPartReturn().setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_TO_BE_SHIPPED)',0,NULL,NULL),(26,'S',NULL,'',NULL,'\0',NULL,NULL,25,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DRAFT)',0,NULL,NULL),(27,'S',NULL,'',NULL,'\0',NULL,NULL,26,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DRAFT_DELETED);',0,NULL,NULL),(28,'S',NULL,'',NULL,'\0',NULL,NULL,27,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.SUBMITTED);',0,NULL,NULL),(29,'S',NULL,'',NULL,'\0',NULL,NULL,28,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.SERVICE_MANAGER_REVIEW);',0,NULL,NULL),(30,'S',NULL,'',NULL,'\0',NULL,NULL,29,NULL,NULL,NULL,NULL,NULL,NULL,'claim.serviceManagerAccepted = false; claim.setState(tavant.twms.domain.claim.ClaimState.SERVICE_MANAGER_RESPONSE);',0,NULL,NULL),(31,'S',NULL,'',NULL,'\0',NULL,NULL,30,NULL,NULL,NULL,NULL,NULL,NULL,'claim.serviceManagerAccepted = true; claim.setState(tavant.twms.domain.claim.ClaimState.SERVICE_MANAGER_RESPONSE);',0,NULL,NULL),(32,'S',NULL,'',NULL,'\0',NULL,NULL,31,NULL,NULL,NULL,NULL,NULL,NULL,'claim.noOfResubmits = claim.noOfResubmits + 1; claim.setState(tavant.twms.domain.claim.ClaimState.SERVICE_MANAGER_REVIEW);',0,NULL,NULL),(33,'S',NULL,'',NULL,'\0',NULL,NULL,32,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DELETED);',0,NULL,NULL),(34,'S',NULL,'',NULL,'\0',NULL,NULL,33,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.SUBMITTED);',0,NULL,NULL),(35,'S',NULL,'',NULL,'\0',NULL,NULL,34,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.WAITING_FOR_PART_RETURNS); claim.setProcessedAutomatically();',0,NULL,NULL),(36,'S',NULL,'',NULL,'\0',NULL,NULL,35,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.PROCESSOR_REVIEW);',0,NULL,NULL),(37,'S',NULL,'',NULL,'\0',NULL,NULL,36,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DENIED); claim.setProcessedAutomatically();',0,NULL,NULL),(38,'S',NULL,'',NULL,'\0',NULL,NULL,37,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ON_HOLD);',0,NULL,NULL),(39,'S',NULL,'',NULL,'\0',NULL,NULL,38,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.REPLIES);',0,NULL,NULL),(40,'S',NULL,'',NULL,'\0',NULL,NULL,39,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.WAITING_FOR_PART_RETURNS);',0,NULL,NULL),(41,'S',NULL,'',NULL,'\0',NULL,NULL,40,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DENIED);',0,NULL,NULL),(42,'S',NULL,'',NULL,'\0',NULL,NULL,41,NULL,NULL,NULL,NULL,NULL,NULL,'claim.getRuleFailures().clear()',0,NULL,NULL),(43,'S',NULL,'',NULL,'\0',NULL,NULL,42,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ON_HOLD);',0,NULL,NULL),(44,'S',NULL,'',NULL,'\0',NULL,NULL,43,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.FORWARDED);',0,NULL,NULL),(45,'S',NULL,'',NULL,'\0',NULL,NULL,44,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.TRANSFERRED);',0,NULL,NULL),(46,'S',NULL,'',NULL,'\0',NULL,NULL,45,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ADVICE_REQUEST);',0,NULL,NULL),(47,'S',NULL,'',NULL,'\0',NULL,NULL,46,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.WAITING_FOR_PART_RETURNS);',0,NULL,NULL),(48,'S',NULL,'',NULL,'\0',NULL,NULL,47,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DENIED);',0,NULL,NULL),(49,'S',NULL,'',NULL,'\0',NULL,NULL,48,NULL,NULL,NULL,NULL,NULL,NULL,'claim.getRuleFailures().clear()',0,NULL,NULL),(50,'S',NULL,'',NULL,'\0',NULL,NULL,49,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ON_HOLD);',0,NULL,NULL),(51,'S',NULL,'',NULL,'\0',NULL,NULL,50,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.FORWARDED);',0,NULL,NULL),(52,'S',NULL,'',NULL,'\0',NULL,NULL,51,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.TRANSFERRED);',0,NULL,NULL),(53,'S',NULL,'',NULL,'\0',NULL,NULL,52,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ADVICE_REQUEST);',0,NULL,NULL),(54,'S',NULL,'',NULL,'\0',NULL,NULL,53,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.WAITING_FOR_PART_RETURNS);',0,NULL,NULL),(55,'S',NULL,'',NULL,'\0',NULL,NULL,54,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DENIED);',0,NULL,NULL),(56,'S',NULL,'',NULL,'\0',NULL,NULL,55,NULL,NULL,NULL,NULL,NULL,NULL,'claim.getRuleFailures().clear()',0,NULL,NULL),(57,'S',NULL,'',NULL,'\0',NULL,NULL,56,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.FORWARDED);',0,NULL,NULL),(58,'S',NULL,'',NULL,'\0',NULL,NULL,57,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.TRANSFERRED);',0,NULL,NULL),(59,'S',NULL,'',NULL,'\0',NULL,NULL,58,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ADVICE_REQUEST);',0,NULL,NULL),(60,'S',NULL,'',NULL,'\0',NULL,NULL,59,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.WAITING_FOR_PART_RETURNS);',0,NULL,NULL),(61,'S',NULL,'',NULL,'\0',NULL,NULL,60,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DENIED);',0,NULL,NULL),(62,'S',NULL,'',NULL,'\0',NULL,NULL,61,NULL,NULL,NULL,NULL,NULL,NULL,'claim.getRuleFailures().clear()',0,NULL,NULL),(63,'S',NULL,'',NULL,'\0',NULL,NULL,62,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ON_HOLD);',0,NULL,NULL),(64,'S',NULL,'',NULL,'\0',NULL,NULL,63,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.FORWARDED);',0,NULL,NULL),(65,'S',NULL,'',NULL,'\0',NULL,NULL,64,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.TRANSFERRED);',0,NULL,NULL),(66,'S',NULL,'',NULL,'\0',NULL,NULL,65,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ADVICE_REQUEST);',0,NULL,NULL),(67,'S',NULL,'',NULL,'\0',NULL,NULL,66,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.WAITING_FOR_PART_RETURNS);',0,NULL,NULL),(68,'S',NULL,'',NULL,'\0',NULL,NULL,67,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.DENIED);',0,NULL,NULL),(69,'S',NULL,'',NULL,'\0',NULL,NULL,68,NULL,NULL,NULL,NULL,NULL,NULL,'claim.getRuleFailures().clear()',0,NULL,NULL),(70,'S',NULL,'',NULL,'\0',NULL,NULL,69,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ON_HOLD);',0,NULL,NULL),(71,'S',NULL,'',NULL,'\0',NULL,NULL,70,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.FORWARDED);',0,NULL,NULL),(72,'S',NULL,'',NULL,'\0',NULL,NULL,71,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.TRANSFERRED);',0,NULL,NULL),(73,'S',NULL,'',NULL,'\0',NULL,NULL,72,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ADVICE_REQUEST);',0,NULL,NULL),(74,'S',NULL,'',NULL,'\0',NULL,NULL,73,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.REPLIES);',0,NULL,NULL),(75,'S',NULL,'',NULL,'\0',NULL,NULL,74,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.REJECTED_PART_RETURN);',0,NULL,NULL),(76,'S',NULL,'',NULL,'\0',NULL,NULL,75,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.PENDING_PAYMENT_RESPONSE);',0,NULL,NULL),(77,'S',NULL,'',NULL,'\0',NULL,NULL,76,NULL,NULL,NULL,NULL,NULL,NULL,'claim.setState(tavant.twms.domain.claim.ClaimState.ACCEPTED);',0,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_action` ENABLE KEYS */;

--
-- Table structure for table `jbpm_bytearray`
--

DROP TABLE IF EXISTS `jbpm_bytearray`;
CREATE TABLE `jbpm_bytearray` (
  `id_` bigint(20) NOT NULL auto_increment,
  `name_` varchar(255) default NULL,
  `filedefinition_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_BYTEARR_FILDEF` (`filedefinition_`),
  CONSTRAINT `FK_BYTEARR_FILDEF` FOREIGN KEY (`filedefinition_`) REFERENCES `jbpm_moduledefinition` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_bytearray`
--


/*!40000 ALTER TABLE `jbpm_bytearray` DISABLE KEYS */;
LOCK TABLES `jbpm_bytearray` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_bytearray` ENABLE KEYS */;

--
-- Table structure for table `jbpm_byteblock`
--

DROP TABLE IF EXISTS `jbpm_byteblock`;
CREATE TABLE `jbpm_byteblock` (
  `processfile_` bigint(20) NOT NULL,
  `bytes_` blob,
  `index_` int(11) NOT NULL,
  PRIMARY KEY  (`processfile_`,`index_`),
  KEY `FK_BYTEBLOCK_FILE` (`processfile_`),
  CONSTRAINT `FK_BYTEBLOCK_FILE` FOREIGN KEY (`processfile_`) REFERENCES `jbpm_bytearray` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_byteblock`
--


/*!40000 ALTER TABLE `jbpm_byteblock` DISABLE KEYS */;
LOCK TABLES `jbpm_byteblock` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_byteblock` ENABLE KEYS */;

--
-- Table structure for table `jbpm_comment`
--

DROP TABLE IF EXISTS `jbpm_comment`;
CREATE TABLE `jbpm_comment` (
  `id_` bigint(20) NOT NULL auto_increment,
  `version_` int(11) NOT NULL,
  `actorid_` varchar(255) default NULL,
  `time_` datetime default NULL,
  `message_` text,
  `token_` bigint(20) default NULL,
  `taskinstance_` bigint(20) default NULL,
  `tokenindex_` int(11) default NULL,
  `taskinstanceindex_` int(11) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_COMMENT_TOKEN` (`token_`),
  KEY `FK_COMMENT_TSK` (`taskinstance_`),
  CONSTRAINT `FK_COMMENT_TSK` FOREIGN KEY (`taskinstance_`) REFERENCES `jbpm_taskinstance` (`id_`),
  CONSTRAINT `FK_COMMENT_TOKEN` FOREIGN KEY (`token_`) REFERENCES `jbpm_token` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_comment`
--


/*!40000 ALTER TABLE `jbpm_comment` DISABLE KEYS */;
LOCK TABLES `jbpm_comment` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_comment` ENABLE KEYS */;

--
-- Table structure for table `jbpm_decisionconditions`
--

DROP TABLE IF EXISTS `jbpm_decisionconditions`;
CREATE TABLE `jbpm_decisionconditions` (
  `decision_` bigint(20) NOT NULL,
  `transitionname_` varchar(255) default NULL,
  `expression_` varchar(255) default NULL,
  `index_` int(11) NOT NULL,
  PRIMARY KEY  (`decision_`,`index_`),
  KEY `FK_DECCOND_DEC` (`decision_`),
  CONSTRAINT `FK_DECCOND_DEC` FOREIGN KEY (`decision_`) REFERENCES `jbpm_node` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_decisionconditions`
--


/*!40000 ALTER TABLE `jbpm_decisionconditions` DISABLE KEYS */;
LOCK TABLES `jbpm_decisionconditions` WRITE;
INSERT INTO `jbpm_decisionconditions` VALUES (2,'No','#{!part.partToBeReturned}',0),(2,'ShipmentToSupplier','#{part.partMarkedForDirectSupplierRecovery}',1),(2,'ShipmentToOEM','#{part.partToBeReturned}',2),(4,'Contract Is Known','#{part.supplierPartReturn.contract != null}',0),(4,'Contract Is Not Known','#{part.supplierPartReturn.contract == null}',1),(6,'Physical','#{part.supplierPartReturn.contract.physicalShipmentRequired}',0),(6,'Logical','#{!part.supplierPartReturn.contract.physicalShipmentRequired}',1),(12,'Yes','#{part.partReturn.isPartOverDue == true}',0),(12,'No','#{part.partReturn.isPartOverDue == false}',1),(13,'ShipmentToSupplier','#{part.partMarkedForDirectSupplierRecovery}',0),(13,'ShipmentToOEM','#{part.partToBeReturned}',1),(19,'Send to Clone','#{part.partReturn.paymentCondition.code == \"PAY_ON_RETURN\" and transition==\"Send for Inspection\"}',0),(19,'Send for Inspection','#{part.partReturn.paymentCondition.code != \"PAY_ON_RETURN\" and transition==\"Send for Inspection\"}',1),(19,'Part Not Received','#{transition==\"Part Not Received\"}',2),(19,'Part Closed','#{transition==\"Part Closed\"}',3),(22,'Yes','#{part.partRecoveredFromSupplier == true}',0),(22,'No','#{part.partRecoveredFromSupplier == false}',1),(24,'Physical','#{part.supplierPartReturn.contract.physicalShipmentRequired}',0),(24,'Logical','#{!part.supplierPartReturn.contract.physicalShipmentRequired}',1),(45,'Accept','#{part.supplierPartReturn.status.status == \"Part Accepted\"}',0),(45,'Reject','#{part.supplierPartReturn.status.status == \"Part Rejected\"}',1),(45,'Not Received','#{part.supplierPartReturn.status.status == \"Part to be shipped\"}',2),(46,'NotReceivedNotifyToDealer','#{part.partMarkedForDirectSupplierRecovery}',0),(46,'NotReceivedNotifyToPartShipper','#{!part.partMarkedForDirectSupplierRecovery}',1),(49,'No','#{claim.type==\"Parts\" || claim.itemReference.serialized==false}',0),(49,'Yes','#{claim.type!=\"Parts\" and claim.itemReference.serialized}',1),(59,'Yes','#{claim.type==\"Machine\" || claim.type==\"Attachment\"}',0),(62,'Yes','#{claim.itemReference.referredInventoryItem.type.type==\"RETAIL\"}',0),(64,'Yes','#{claim.itemReference.referredInventoryItem.type.type==\"STOCK\"}',0),(71,'Yes','#{claim.serviceManagerRequest}',0),(81,'Accept','#{claimState==null}',0),(81,'ReferForManualReview','#{claimState==\"manual review\"}',1),(81,'DenyClaim','#{claimState==\"rejected\"}',2),(81,'PutClaimOnHold','#{claimState==\"on hold\"}',3),(99,'ClaimEligibleForPayment','#{isEligibleForPayment}',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_decisionconditions` ENABLE KEYS */;

--
-- Table structure for table `jbpm_delegation`
--

DROP TABLE IF EXISTS `jbpm_delegation`;
CREATE TABLE `jbpm_delegation` (
  `id_` bigint(20) NOT NULL auto_increment,
  `classname_` text,
  `configuration_` text,
  `configtype_` varchar(255) default NULL,
  `processdefinition_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_DELEGATION_PRCD` (`processdefinition_`),
  CONSTRAINT `FK_DELEGATION_PRCD` FOREIGN KEY (`processdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_delegation`
--


/*!40000 ALTER TABLE `jbpm_delegation` DISABLE KEYS */;
LOCK TABLES `jbpm_delegation` WRITE;
INSERT INTO `jbpm_delegation` VALUES (1,'tavant.twms.jbpm.infra.ServiceBeanInvoker','\n  	<transition name=\"\" to=\"IsUniqueSupplier\"/>\n    <beanName>contractService</beanName>\n    <methodName>updateSupplierPartReturnIfUniqueSupplierFound</methodName>\n    <parameters><element>claim</element><element>part</element></parameters>\n    <postProcess/>\n  ','bean',1),(2,'tavant.twms.jbpm.action.TaskInstanceEndAction','\n          <transition>Part Overdue</transition>\n        ',NULL,1),(3,'tavant.twms.jbpm.action.OverduePartAction',NULL,NULL,1),(4,'tavant.twms.jbpm.assignment.ExpressionAssignmentHandler','\n      <expression>actor=ognl{part.supplierPartReturn.supplier.users.first().name}</expression>\n    ',NULL,1),(5,'tavant.twms.jbpm.assignment.ScriptAssignmentHandler','\n		<beanName>warehouseService</beanName>\n		<methodName>getReceiverAtLocation</methodName>\n		<parameters><parameter>part.partReturn.returnLocation</parameter></parameters>\n	',NULL,1),(6,'tavant.twms.jbpm.assignment.ExpressionAssignmentHandler','\n      <expression>actor=ognl{claim.filedBy.name}</expression>\n    ',NULL,1),(7,'tavant.twms.jbpm.assignment.ExpressionAssignmentHandler','\n      <expression>actor=sra</expression>\n    ',NULL,1),(8,'tavant.twms.jbpm.assignment.ScriptAssignmentHandler','\n		<beanName>warehouseService</beanName>\n		<methodName>getInspectorAtLocation</methodName>\n		<parameters><parameter>part.partReturn.returnLocation</parameter></parameters>\n	',NULL,1),(9,'tavant.twms.jbpm.assignment.ScriptAssignmentHandler','\n		<beanName>warehouseService</beanName>\n		<methodName>getPartShipperAtLocation</methodName>\n		<parameters><parameter>part.partReturn.returnLocation</parameter></parameters>\n	',NULL,1),(10,'tavant.twms.jbpm.infra.ServiceBeanInvoker','    \n    <beanName>policyService</beanName>\n    <methodName>findApplicablePolicy</methodName>\n    <parameters><variable>claim</variable></parameters>\n    <postProcess>if (result != null) claim.setApplicablePolicy(result)</postProcess>\n    <transition name=\"\" to=\"ComputePayment\"></transition>\n  ','bean',2),(11,'tavant.twms.jbpm.infra.ServiceBeanInvoker','    \n    <beanName>paymentService</beanName>\n    <methodName>calculatePaymentForClaim</methodName>\n    <parameters><variable>claim</variable></parameters>\n    <postProcess>claim.setPayment(result)</postProcess>\n    <transition name=\"\" to=\"End\"/>\n  ','bean',2),(12,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>IncompleteInformationChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.filedBy</variable><variable>claim.forDealer</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.laborPerformed</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable><variable>claim.serviceInformation.serviceDetail.oEMPartsReplaced</variable><variable>claim.serviceInformation.serviceDetail.nonOEMPartsReplaced</variable><variable>claim.payment</variable><variable>claim.payment.paymentComponents</variable></input>\n		<transition name=\"\" to=\"SetupChecks\"/>\n	','bean',3),(13,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>SetupChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.filedBy</variable><variable>claim.forDealer</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.laborPerformed</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable><variable>claim.serviceInformation.serviceDetail.oEMPartsReplaced</variable><variable>claim.serviceInformation.serviceDetail.nonOEMPartsReplaced</variable><variable>claim.payment</variable><variable>claim.payment.paymentComponents</variable></input>\n		<transition name=\"\" to=\"ValidityChecks\"/>\n	','bean',3),(14,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>ClaimValidityChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.filedBy</variable><variable>claim.forDealer</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.laborPerformed</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable><variable>claim.serviceInformation.serviceDetail.oEMPartsReplaced</variable><variable>claim.serviceInformation.serviceDetail.nonOEMPartsReplaced</variable><variable>claim.payment</variable><variable>claim.payment.paymentComponents</variable></input>\n		<transition name=\"\" to=\"ReviewChecks\"/>\n	','bean',3),(15,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>ReviewChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.filedBy</variable><variable>claim.forDealer</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.laborPerformed</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable><variable>claim.serviceInformation.serviceDetail.oEMPartsReplaced</variable><variable>claim.serviceInformation.serviceDetail.nonOEMPartsReplaced</variable><variable>claim.payment</variable><variable>claim.payment.paymentComponents</variable></input>\n		<transition name=\"ShouldCheckDuplicates\" to=\"ClaimDuplicityChecks\"><condition>#{claim.itemReference.referredInventoryItem != null}</condition></transition>\n		<transition name=\"\" to=\"IsMachineOrAttachmentClaim\"><condition>#{claim.itemReference.referredInventoryItem == null}</condition></transition>\n	','bean',3),(16,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>ClaimDuplicityChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.itemReference.referredInventoryItem</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable></input>\n		<transition name=\"\" to=\"IsMachineOrAttachmentClaim\"/>\n	','bean',3),(17,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>ClaimUnderWarrantyValidityChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.filedBy</variable><variable>claim.forDealer</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.laborPerformed</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable><variable>claim.serviceInformation.serviceDetail.oEMPartsReplaced</variable><variable>claim.serviceInformation.serviceDetail.nonOEMPartsReplaced</variable><variable>claim.payment</variable><variable>claim.payment.paymentComponents</variable></input>\n		<transition name=\"ShouldCheckDuplicates\" to=\"ClaimUnderWarrantyDuplicityChecks\"><condition>#{claim.itemReference.referredInventoryItem != null}</condition></transition>\n		<transition name=\"\" to=\"IsInventoryItemRetailed\"><condition>#{claim.itemReference.referredInventoryItem == null}</condition></transition>\n	','bean',3),(18,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>ClaimUnderWarrantyDuplicityChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.itemReference.referredInventoryItem</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.causalPart</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable></input>\n		<transition name=\"\" to=\"IsInventoryItemRetailed\"/>\n	','bean',3),(19,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>RetailedInventoryItemChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.filedBy</variable><variable>claim.forDealer</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.laborPerformed</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable><variable>claim.serviceInformation.serviceDetail.oEMPartsReplaced</variable><variable>claim.serviceInformation.serviceDetail.nonOEMPartsReplaced</variable><variable>claim.payment</variable><variable>claim.payment.paymentComponents</variable></input>\n		<transition name=\"\" to=\"IsInventoryItemStocked\"/>\n	','bean',3),(20,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>StockedInventoryItemChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.filedBy</variable><variable>claim.forDealer</variable><variable>claim.serviceInformation</variable><variable>claim.serviceInformation.serviceDetail</variable><variable>claim.serviceInformation.serviceDetail.laborPerformed</variable><variable>claim.serviceInformation.serviceDetail.travelDetails</variable><variable>claim.serviceInformation.serviceDetail.oEMPartsReplaced</variable><variable>claim.serviceInformation.serviceDetail.nonOEMPartsReplaced</variable><variable>claim.payment</variable><variable>claim.payment.paymentComponents</variable></input>\n		<transition name=\"\" to=\"HighValueClaimChecks\"/>\n	','bean',3),(21,'tavant.twms.jbpm.nodes.ExpressionEvaluatorHandler','\n		<ruleSet>HighValueClaimChecks</ruleSet>\n		<input><variable>claim</variable><variable>claim.payment</variable><variable>claim.payment.paymentComponents</variable><variable>claim.payment.paymentComponents.{forCategory}</variable></input>\n		<transition name=\"\" to=\"End\"/>\n	','bean',3),(22,'tavant.twms.jbpm.infra.ServiceBeanInvoker','    \n    <beanName>partReturnService</beanName>\n    <methodName>updatePartReturnsForClaim</methodName>\n    <parameters><variable>claim</variable></parameters>\n    <transition name=\"\" to=\"UpdateSupplierPartReturns\"/>\n  ','bean',4),(23,'tavant.twms.jbpm.infra.ServiceBeanInvoker','\n    <transition name=\"\" to=\"ForkPartReturns\"/>\n    <beanName>contractService</beanName>\n    <methodName>updateSupplierPartReturns</methodName>\n    <parameters><element>claim</element></parameters>\n    <postProcess/>\n  ','bean',4),(24,'tavant.twms.jbpm.infra.ServiceBeanInvoker','    \n    <beanName>ruleAdministrationService</beanName>\n    <methodName>executeClaimAutoProcessingRules</methodName>\n    <parameters><variable>claim</variable></parameters>    \n    <outputName>claimState</outputName>\n    <transition name=\"\" to=\"IsProcessorReviewNeeded\"/>    \n  ','bean',4),(25,'tavant.twms.jbpm.infra.ServiceBeanInvoker','    \n    <beanName>partReturnService</beanName>\n    <methodName>isEligibleForPayment</methodName>\n    <parameters><element>claim</element></parameters>\n    <outputName>isEligibleForPayment</outputName>\n    <transition name=\"\" to=\"isClaimEligibleForPaymentAfterPartReturn\"/>\n  ','bean',4),(26,'tavant.twms.jbpm.infra.ServiceBeanInvoker','\n    <transition name=\"\" to=\"WaitForPaymentResponse\"><script>claim.setState(tavant.twms.domain.claim.ClaimState.PENDING_PAYMENT_RESPONSE);</script></transition>\n    <beanName>paymentAsyncService</beanName>\n    <methodName>startAsyncPayment</methodName>\n    <parameters><element>claim</element></parameters>\n    <postProcess/>\n  ','bean',4),(27,'tavant.twms.jbpm.infra.ServiceBeanInvoker','\n    <transition name=\"\" to=\"End\"/>\n    <beanName>emailNotificationAction</beanName>\n    <methodName>notifyUser</methodName>\n    <parameters><element>claim.filedBy</element><element>claim</element></parameters>\n    <postProcess/>\n  ','bean',4),(28,'tavant.twms.jbpm.assignment.RuleBasedAssignmentHandler','\n      <ruleSet>DsmAssignment</ruleSet>\n      <variables><variable>claim</variable><variable>claim.forDealer</variable><variable>claim.forDealer.address</variable></variables>\n    ',NULL,4),(29,'tavant.twms.jbpm.assignment.ExpressionAssignmentHandler','\n      <expression>actor=ognl{claim.filedBy.name}</expression>\n    ',NULL,4),(30,'tavant.twms.jbpm.assignment.RuleBasedAssignmentHandler','\n      <ruleSet>ProcessorAssignment</ruleSet>\n      <variables><variable>claim</variable><variable>claim.forDealer</variable><variable>claim.forDealer.address</variable></variables>\n    ',NULL,4);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_delegation` ENABLE KEYS */;

--
-- Table structure for table `jbpm_event`
--

DROP TABLE IF EXISTS `jbpm_event`;
CREATE TABLE `jbpm_event` (
  `id_` bigint(20) NOT NULL auto_increment,
  `eventtype_` varchar(255) default NULL,
  `type_` char(1) default NULL,
  `graphelement_` bigint(20) default NULL,
  `transition_` bigint(20) default NULL,
  `task_` bigint(20) default NULL,
  `node_` bigint(20) default NULL,
  `processdefinition_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_EVENT_PROCDEF` (`processdefinition_`),
  KEY `FK_EVENT_NODE` (`node_`),
  KEY `FK_EVENT_TRANS` (`transition_`),
  KEY `FK_EVENT_TASK` (`task_`),
  CONSTRAINT `FK_EVENT_TASK` FOREIGN KEY (`task_`) REFERENCES `jbpm_task` (`id_`),
  CONSTRAINT `FK_EVENT_NODE` FOREIGN KEY (`node_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_EVENT_PROCDEF` FOREIGN KEY (`processdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`),
  CONSTRAINT `FK_EVENT_TRANS` FOREIGN KEY (`transition_`) REFERENCES `jbpm_transition` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_event`
--


/*!40000 ALTER TABLE `jbpm_event` DISABLE KEYS */;
LOCK TABLES `jbpm_event` WRITE;
INSERT INTO `jbpm_event` VALUES (1,'transition','T',16,16,NULL,NULL,NULL),(2,'task-create','A',3,NULL,3,NULL,NULL),(3,'timer-create','A',3,NULL,3,NULL,NULL),(4,'task-end','A',3,NULL,3,NULL,NULL),(5,'transition','T',17,17,NULL,NULL,NULL),(6,'transition','T',18,18,NULL,NULL,NULL),(7,'transition','T',19,19,NULL,NULL,NULL),(8,'transition','T',32,32,NULL,NULL,NULL),(9,'transition','T',33,33,NULL,NULL,NULL),(10,'transition','T',34,34,NULL,NULL,NULL),(11,'transition','T',35,35,NULL,NULL,NULL),(12,'transition','T',42,42,NULL,NULL,NULL),(13,'transition','T',51,51,NULL,NULL,NULL),(14,'transition','T',52,52,NULL,NULL,NULL),(15,'transition','T',53,53,NULL,NULL,NULL),(16,'transition','T',61,61,NULL,NULL,NULL),(17,'transition','T',62,62,NULL,NULL,NULL),(18,'transition','T',63,63,NULL,NULL,NULL),(19,'transition','T',68,68,NULL,NULL,NULL),(20,'transition','T',69,69,NULL,NULL,NULL),(21,'transition','T',70,70,NULL,NULL,NULL),(22,'transition','T',74,74,NULL,NULL,NULL),(23,'transition','T',75,75,NULL,NULL,NULL),(24,'transition','T',76,76,NULL,NULL,NULL),(25,'transition','T',108,108,NULL,NULL,NULL),(26,'transition','T',110,110,NULL,NULL,NULL),(27,'transition','T',111,111,NULL,NULL,NULL),(28,'transition','T',114,114,NULL,NULL,NULL),(29,'transition','T',115,115,NULL,NULL,NULL),(30,'transition','T',116,116,NULL,NULL,NULL),(31,'transition','T',118,118,NULL,NULL,NULL),(32,'transition','T',119,119,NULL,NULL,NULL),(33,'transition','T',120,120,NULL,NULL,NULL),(34,'transition','T',128,128,NULL,NULL,NULL),(35,'transition','T',129,129,NULL,NULL,NULL),(36,'transition','T',130,130,NULL,NULL,NULL),(37,'transition','T',131,131,NULL,NULL,NULL),(38,'transition','T',136,136,NULL,NULL,NULL),(39,'transition','T',137,137,NULL,NULL,NULL),(40,'transition','T',138,138,NULL,NULL,NULL),(41,'transition','T',139,139,NULL,NULL,NULL),(42,'transition','T',140,140,NULL,NULL,NULL),(43,'transition','T',141,141,NULL,NULL,NULL),(44,'transition','T',142,142,NULL,NULL,NULL),(45,'transition','T',143,143,NULL,NULL,NULL),(46,'transition','T',144,144,NULL,NULL,NULL),(47,'transition','T',145,145,NULL,NULL,NULL),(48,'transition','T',146,146,NULL,NULL,NULL),(49,'transition','T',147,147,NULL,NULL,NULL),(50,'transition','T',148,148,NULL,NULL,NULL),(51,'transition','T',149,149,NULL,NULL,NULL),(52,'transition','T',150,150,NULL,NULL,NULL),(53,'transition','T',152,152,NULL,NULL,NULL),(54,'transition','T',153,153,NULL,NULL,NULL),(55,'transition','T',154,154,NULL,NULL,NULL),(56,'transition','T',155,155,NULL,NULL,NULL),(57,'transition','T',156,156,NULL,NULL,NULL),(58,'transition','T',157,157,NULL,NULL,NULL),(59,'transition','T',158,158,NULL,NULL,NULL),(60,'transition','T',159,159,NULL,NULL,NULL),(61,'transition','T',160,160,NULL,NULL,NULL),(62,'transition','T',161,161,NULL,NULL,NULL),(63,'transition','T',162,162,NULL,NULL,NULL),(64,'transition','T',163,163,NULL,NULL,NULL),(65,'transition','T',164,164,NULL,NULL,NULL),(66,'transition','T',165,165,NULL,NULL,NULL),(67,'transition','T',166,166,NULL,NULL,NULL),(68,'transition','T',167,167,NULL,NULL,NULL),(69,'transition','T',168,168,NULL,NULL,NULL),(70,'transition','T',169,169,NULL,NULL,NULL),(71,'transition','T',170,170,NULL,NULL,NULL),(72,'transition','T',171,171,NULL,NULL,NULL),(73,'transition','T',177,177,NULL,NULL,NULL),(74,'transition','T',180,180,NULL,NULL,NULL),(75,'transition','T',182,182,NULL,NULL,NULL),(76,'transition','T',183,183,NULL,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_event` ENABLE KEYS */;

--
-- Table structure for table `jbpm_exceptionhandler`
--

DROP TABLE IF EXISTS `jbpm_exceptionhandler`;
CREATE TABLE `jbpm_exceptionhandler` (
  `id_` bigint(20) NOT NULL auto_increment,
  `exceptionclassname_` text,
  `type_` char(1) default NULL,
  `graphelement_` bigint(20) default NULL,
  `transition_` bigint(20) default NULL,
  `graphelementindex_` int(11) default NULL,
  `task_` bigint(20) default NULL,
  `node_` bigint(20) default NULL,
  `processdefinition_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_exceptionhandler`
--


/*!40000 ALTER TABLE `jbpm_exceptionhandler` DISABLE KEYS */;
LOCK TABLES `jbpm_exceptionhandler` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_exceptionhandler` ENABLE KEYS */;

--
-- Table structure for table `jbpm_form_nodes`
--

DROP TABLE IF EXISTS `jbpm_form_nodes`;
CREATE TABLE `jbpm_form_nodes` (
  `form_task_node_form_id` bigint(20) NOT NULL,
  `form_value` varchar(255) default NULL,
  `form_type` varchar(255) NOT NULL,
  PRIMARY KEY  (`form_task_node_form_id`,`form_type`),
  KEY `FK9C30A4C0A18E7D64` (`form_task_node_form_id`),
  CONSTRAINT `FK9C30A4C0A18E7D64` FOREIGN KEY (`form_task_node_form_id`) REFERENCES `jbpm_node` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_form_nodes`
--


/*!40000 ALTER TABLE `jbpm_form_nodes` DISABLE KEYS */;
LOCK TABLES `jbpm_form_nodes` WRITE;
INSERT INTO `jbpm_form_nodes` VALUES (5,'supplierRecoveryAdminDirectShipment','actionUrl'),(9,'dueParts','actionUrl'),(10,'OverdueParts','actionUrl'),(11,'shipmentGenerated','actionUrl'),(16,'duePartsShipped','actionUrl'),(17,'duePartsReceipt','actionUrl'),(21,'duePartsInspection','actionUrl'),(23,'supplierRecoveryAdmin','actionUrl'),(28,'supplierRecoveryAdminReview','actionUrl'),(32,'claimsSentToShipper','actionUrl'),(33,'claimsSentToSupplier','actionUrl'),(35,'partShipperGenerateShipment','actionUrl'),(36,'partShipperUpdateTag','actionUrl'),(39,'partShipperPartsShipped','actionUrl'),(40,'supplierRecovery','actionUrl'),(42,'supplierRecoveryDisputed','actionUrl'),(43,'supplierRecoveryInProgress','actionUrl'),(69,'draft_claim','inputForm'),(72,'service_manager_review','inputForm'),(74,'service_manager_response','inputForm'),(83,'advice_request','inputForm'),(84,'processor_forwarded','inputForm'),(86,'processor_review','inputForm'),(87,'processor_review','inputForm'),(89,'processor_review','inputForm'),(90,'processor_review','inputForm'),(91,'processor_review','inputForm'),(93,'forwarded','inputForm'),(94,'processor_forwarded','inputForm');
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_form_nodes` ENABLE KEYS */;

--
-- Table structure for table `jbpm_log`
--

DROP TABLE IF EXISTS `jbpm_log`;
CREATE TABLE `jbpm_log` (
  `id_` bigint(20) NOT NULL auto_increment,
  `class_` char(1) NOT NULL,
  `index_` int(11) default NULL,
  `date_` datetime default NULL,
  `token_` bigint(20) default NULL,
  `parent_` bigint(20) default NULL,
  `child_` bigint(20) default NULL,
  `swimlaneinstance_` bigint(20) default NULL,
  `exception_` text,
  `action_` bigint(20) default NULL,
  `variableinstance_` bigint(20) default NULL,
  `message_` text,
  `taskactorid_` varchar(255) default NULL,
  `node_` bigint(20) default NULL,
  `enter_` datetime default NULL,
  `leave_` datetime default NULL,
  `duration_` bigint(20) default NULL,
  `taskinstance_` bigint(20) default NULL,
  `taskoldactorid_` varchar(255) default NULL,
  `transition_` bigint(20) default NULL,
  `sourcenode_` bigint(20) default NULL,
  `destinationnode_` bigint(20) default NULL,
  `newlongvalue_` bigint(20) default NULL,
  `oldlongvalue_` bigint(20) default NULL,
  `oldstringvalue_` text,
  `newstringvalue_` text,
  `olddatevalue_` datetime default NULL,
  `newdatevalue_` datetime default NULL,
  `oldstringidclass_` varchar(255) default NULL,
  `oldstringidvalue_` varchar(255) default NULL,
  `newstringidclass_` varchar(255) default NULL,
  `newstringidvalue_` varchar(255) default NULL,
  `oldlongidclass_` varchar(255) default NULL,
  `oldlongidvalue_` bigint(20) default NULL,
  `newlongidclass_` varchar(255) default NULL,
  `newlongidvalue_` bigint(20) default NULL,
  `olddoublevalue_` double default NULL,
  `newdoublevalue_` double default NULL,
  `oldbytearray_` bigint(20) default NULL,
  `newbytearray_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_LOG_SOURCENODE` (`sourcenode_`),
  KEY `FK_LOG_TOKEN` (`token_`),
  KEY `FK_LOG_OLDBYTES` (`oldbytearray_`),
  KEY `FK_LOG_NEWBYTES` (`newbytearray_`),
  KEY `FK_LOG_CHILDTOKEN` (`child_`),
  KEY `FK_LOG_DESTNODE` (`destinationnode_`),
  KEY `FK_LOG_TASKINST` (`taskinstance_`),
  KEY `FK_LOG_SWIMINST` (`swimlaneinstance_`),
  KEY `FK_LOG_PARENT` (`parent_`),
  KEY `FK_LOG_NODE` (`node_`),
  KEY `FK_LOG_ACTION` (`action_`),
  KEY `FK_LOG_VARINST` (`variableinstance_`),
  KEY `FK_LOG_TRANSITION` (`transition_`),
  CONSTRAINT `FK_LOG_TRANSITION` FOREIGN KEY (`transition_`) REFERENCES `jbpm_transition` (`id_`),
  CONSTRAINT `FK_LOG_ACTION` FOREIGN KEY (`action_`) REFERENCES `jbpm_action` (`id_`),
  CONSTRAINT `FK_LOG_CHILDTOKEN` FOREIGN KEY (`child_`) REFERENCES `jbpm_token` (`id_`),
  CONSTRAINT `FK_LOG_DESTNODE` FOREIGN KEY (`destinationnode_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_LOG_NEWBYTES` FOREIGN KEY (`newbytearray_`) REFERENCES `jbpm_bytearray` (`id_`),
  CONSTRAINT `FK_LOG_NODE` FOREIGN KEY (`node_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_LOG_OLDBYTES` FOREIGN KEY (`oldbytearray_`) REFERENCES `jbpm_bytearray` (`id_`),
  CONSTRAINT `FK_LOG_PARENT` FOREIGN KEY (`parent_`) REFERENCES `jbpm_log` (`id_`),
  CONSTRAINT `FK_LOG_SOURCENODE` FOREIGN KEY (`sourcenode_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_LOG_SWIMINST` FOREIGN KEY (`swimlaneinstance_`) REFERENCES `jbpm_swimlaneinstance` (`id_`),
  CONSTRAINT `FK_LOG_TASKINST` FOREIGN KEY (`taskinstance_`) REFERENCES `jbpm_taskinstance` (`id_`),
  CONSTRAINT `FK_LOG_TOKEN` FOREIGN KEY (`token_`) REFERENCES `jbpm_token` (`id_`),
  CONSTRAINT `FK_LOG_VARINST` FOREIGN KEY (`variableinstance_`) REFERENCES `jbpm_variableinstance` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_log`
--


/*!40000 ALTER TABLE `jbpm_log` DISABLE KEYS */;
LOCK TABLES `jbpm_log` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_log` ENABLE KEYS */;

--
-- Table structure for table `jbpm_message`
--

DROP TABLE IF EXISTS `jbpm_message`;
CREATE TABLE `jbpm_message` (
  `id_` bigint(20) NOT NULL auto_increment,
  `class_` char(1) NOT NULL,
  `destination_` varchar(255) default NULL,
  `exception_` text,
  `issuspended_` bit(1) default NULL,
  `token_` bigint(20) default NULL,
  `transitionname_` varchar(255) default NULL,
  `taskinstance_` bigint(20) default NULL,
  `node_` bigint(20) default NULL,
  `action_` bigint(20) default NULL,
  `text_` varchar(255) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_MSG_TOKEN` (`token_`),
  KEY `FK_CMD_NODE` (`node_`),
  KEY `FK_CMD_ACTION` (`action_`),
  KEY `FK_CMD_TASKINST` (`taskinstance_`),
  CONSTRAINT `FK_CMD_TASKINST` FOREIGN KEY (`taskinstance_`) REFERENCES `jbpm_taskinstance` (`id_`),
  CONSTRAINT `FK_CMD_ACTION` FOREIGN KEY (`action_`) REFERENCES `jbpm_action` (`id_`),
  CONSTRAINT `FK_CMD_NODE` FOREIGN KEY (`node_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_MSG_TOKEN` FOREIGN KEY (`token_`) REFERENCES `jbpm_token` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_message`
--


/*!40000 ALTER TABLE `jbpm_message` DISABLE KEYS */;
LOCK TABLES `jbpm_message` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_message` ENABLE KEYS */;

--
-- Table structure for table `jbpm_moduledefinition`
--

DROP TABLE IF EXISTS `jbpm_moduledefinition`;
CREATE TABLE `jbpm_moduledefinition` (
  `id_` bigint(20) NOT NULL auto_increment,
  `class_` char(1) NOT NULL,
  `name_` text,
  `processdefinition_` bigint(20) default NULL,
  `starttask_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_TSKDEF_START` (`starttask_`),
  KEY `FK_MODDEF_PROCDEF` (`processdefinition_`),
  CONSTRAINT `FK_MODDEF_PROCDEF` FOREIGN KEY (`processdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`),
  CONSTRAINT `FK_TSKDEF_START` FOREIGN KEY (`starttask_`) REFERENCES `jbpm_task` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_moduledefinition`
--


/*!40000 ALTER TABLE `jbpm_moduledefinition` DISABLE KEYS */;
LOCK TABLES `jbpm_moduledefinition` WRITE;
INSERT INTO `jbpm_moduledefinition` VALUES (1,'C','org.jbpm.context.def.ContextDefinition',1,NULL),(2,'T','org.jbpm.taskmgmt.def.TaskMgmtDefinition',1,NULL),(3,'C','org.jbpm.context.def.ContextDefinition',2,NULL),(4,'T','org.jbpm.taskmgmt.def.TaskMgmtDefinition',2,NULL),(5,'C','org.jbpm.context.def.ContextDefinition',3,NULL),(6,'T','org.jbpm.taskmgmt.def.TaskMgmtDefinition',3,NULL),(7,'C','org.jbpm.context.def.ContextDefinition',4,NULL),(8,'T','org.jbpm.taskmgmt.def.TaskMgmtDefinition',4,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_moduledefinition` ENABLE KEYS */;

--
-- Table structure for table `jbpm_moduleinstance`
--

DROP TABLE IF EXISTS `jbpm_moduleinstance`;
CREATE TABLE `jbpm_moduleinstance` (
  `id_` bigint(20) NOT NULL auto_increment,
  `class_` char(1) NOT NULL,
  `processinstance_` bigint(20) default NULL,
  `taskmgmtdefinition_` bigint(20) default NULL,
  `name_` varchar(255) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_TASKMGTINST_TMD` (`taskmgmtdefinition_`),
  KEY `FK_MODINST_PRCINST` (`processinstance_`),
  CONSTRAINT `FK_MODINST_PRCINST` FOREIGN KEY (`processinstance_`) REFERENCES `jbpm_processinstance` (`id_`),
  CONSTRAINT `FK_TASKMGTINST_TMD` FOREIGN KEY (`taskmgmtdefinition_`) REFERENCES `jbpm_moduledefinition` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_moduleinstance`
--


/*!40000 ALTER TABLE `jbpm_moduleinstance` DISABLE KEYS */;
LOCK TABLES `jbpm_moduleinstance` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_moduleinstance` ENABLE KEYS */;

--
-- Table structure for table `jbpm_node`
--

DROP TABLE IF EXISTS `jbpm_node`;
CREATE TABLE `jbpm_node` (
  `id_` bigint(20) NOT NULL auto_increment,
  `class_` char(1) NOT NULL,
  `name_` varchar(255) default NULL,
  `processdefinition_` bigint(20) default NULL,
  `isasync_` bit(1) default NULL,
  `action_` bigint(20) default NULL,
  `superstate_` bigint(20) default NULL,
  `task_names_to_end` varchar(255) default NULL,
  `signal_` int(11) default NULL,
  `createtasks_` bit(1) default NULL,
  `endtasks_` bit(1) default NULL,
  `decisiondelegation` bigint(20) default NULL,
  `decisionexpression_` varchar(255) default NULL,
  `end_transition` varchar(255) default NULL,
  `normal_transition` varchar(255) default NULL,
  `subprocessdefinition_` bigint(20) default NULL,
  `nodecollectionindex_` int(11) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_PROCST_SBPRCDEF` (`subprocessdefinition_`),
  KEY `FK_NODE_PROCDEF` (`processdefinition_`),
  KEY `FK_NODE_ACTION` (`action_`),
  KEY `FK_DECISION_DELEG` (`decisiondelegation`),
  KEY `FK_NODE_SUPERSTATE` (`superstate_`),
  CONSTRAINT `FK_NODE_SUPERSTATE` FOREIGN KEY (`superstate_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_DECISION_DELEG` FOREIGN KEY (`decisiondelegation`) REFERENCES `jbpm_delegation` (`id_`),
  CONSTRAINT `FK_NODE_ACTION` FOREIGN KEY (`action_`) REFERENCES `jbpm_action` (`id_`),
  CONSTRAINT `FK_NODE_PROCDEF` FOREIGN KEY (`processdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`),
  CONSTRAINT `FK_PROCST_SBPRCDEF` FOREIGN KEY (`subprocessdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_node`
--


/*!40000 ALTER TABLE `jbpm_node` DISABLE KEYS */;
LOCK TABLES `jbpm_node` WRITE;
INSERT INTO `jbpm_node` VALUES (1,'R','Start',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0),(2,'D','IsPartToBeReturned',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(3,'X','Update Supplier If Possible',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,2),(4,'D','IsUniqueSupplier',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3),(5,'A','SRA Shipment from Dealer',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,4),(6,'D','IsDealerPhysicalShipmentNeeded',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,5),(7,'A','Dealer Logical Shipment Wait',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,6),(8,'V','DirectSupplierFork',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,7),(9,'A','Due Parts for Shipment',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,8),(10,'A','Overdue Parts for Shipment',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,9),(11,'A','Shipment Generated',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,10),(12,'D','CheckIfOverdue',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,11),(13,'D','SubmitShipment',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,12),(14,'V','SupplierPartsShippedFork',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13),(15,'V','PartsShippedFork',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,14),(16,'A','Due Parts Shipped',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,15),(17,'A','Due Parts Received',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,16),(18,'B','SingleTokenJoin',1,'\0',NULL,NULL,'Parts Shipped',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,17),(19,'D','Check Receiver Action',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,18),(20,'W','Clone',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Notify Payment On Receipt','Send for Inspection',NULL,19),(21,'A','Due Parts For Inspection',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,20),(22,'D','CheckSupplierRecovery',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,21),(23,'A','Supplier Recovery Admin',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,22),(24,'D','Check If Physical Shipment Needed',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,23),(25,'V','LogicalClaimsDueForRecoveryFork',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,24),(26,'A','Logical Shipment Wait',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,25),(27,'V','ClaimsDueForRecoveryFork',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,26),(28,'A','Supplier Recovery Admin Review',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,27),(29,'B','SendToSupplierSingleTokenJoin',1,'\0',NULL,NULL,'Disputed',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,28),(30,'B','SupplierClaimsSingleTokenJoin',1,'\0',NULL,NULL,'Disputed',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,29),(31,'V','SupplierRecoveryAdminReviewFork',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,30),(32,'A','Claims Sent To Shipper Tag',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,31),(33,'A','Claims Sent To Supplier',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,32),(34,'B','claimsSentToShipperSingleTokenJoin',1,'\0',NULL,NULL,'Awaiting Supplier Response, Parts Shipped',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,33),(35,'A','Part Shipper Generate Shipment',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,34),(36,'A','Part Shipper Update Tag',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,35),(37,'B','claimSentToSupplierSingleTokenJoin',1,'\0',NULL,NULL,'Awaiting Shipment',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,36),(38,'V','PartShipperShipmentGeneratedFork',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,37),(39,'A','Part Shipper Parts Shipped Tag',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,38),(40,'A','Supplier Contract',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,39),(41,'V','SupplierRejectFork',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,40),(42,'A','Disputed Supplier Claims',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,41),(43,'A','In Progress Supplier Claims',1,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,42),(44,'B','PartsShipperSingleTokenJoin',1,'\0',NULL,NULL,'Supplier Parts Shipped, Parts Shipped',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,43),(45,'D','Supplier Contract Parts Claimed Tag',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,44),(46,'D','Check',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,45),(47,'E','End',1,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,46),(48,'R','Start',2,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0),(49,'D','IsPolicyComputationRequired',2,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(50,'X','ComputePolicy',2,'\0',NULL,NULL,NULL,NULL,NULL,NULL,10,NULL,NULL,NULL,NULL,2),(51,'X','ComputePayment',2,'\0',NULL,NULL,NULL,NULL,NULL,NULL,11,NULL,NULL,NULL,NULL,3),(52,'E','End',2,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4),(53,'R','Start',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0),(54,'P','IncompleteInformationChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,12,NULL,NULL,NULL,NULL,1),(55,'P','SetupChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,13,NULL,NULL,NULL,NULL,2),(56,'P','ValidityChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,14,NULL,NULL,NULL,NULL,3),(57,'Q','ReviewChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,15,NULL,NULL,NULL,NULL,4),(58,'P','ClaimDuplicityChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,16,NULL,NULL,NULL,NULL,5),(59,'D','IsMachineOrAttachmentClaim',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6),(60,'Q','WarrantyOnlyChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,17,NULL,NULL,NULL,NULL,7),(61,'P','ClaimUnderWarrantyDuplicityChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,18,NULL,NULL,NULL,NULL,8),(62,'D','IsInventoryItemRetailed',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,9),(63,'P','RetailedInventoryItemChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,19,NULL,NULL,NULL,NULL,10),(64,'D','IsInventoryItemStocked',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,11),(65,'P','StockedInventoryItemChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,20,NULL,NULL,NULL,NULL,12),(66,'P','HighValueClaimChecks',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,21,NULL,NULL,NULL,NULL,13),(67,'E','End',3,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,14),(68,'R','Start',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0),(69,'A','DraftClaim',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,1),(70,'C','PolicyAndPaymentComputation',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,2),(71,'D','IsServiceManagerReviewRequested',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3),(72,'A','ServiceManagerReview',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,4),(73,'C','PolicyAndPaymentComputationAfterSmrUpdate',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,5),(74,'A','ServiceManagerResponse',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,6),(75,'C','PolicyAndPaymentComputationAfterSMRAcceptance',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,7),(76,'X','UpdatePartReturnInformation',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,22,NULL,NULL,NULL,NULL,8),(77,'X','UpdateSupplierPartReturns',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,23,NULL,NULL,NULL,NULL,9),(78,'V','ForkPartReturns',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,10),(79,'C','BranchPartsReturnNodes',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,11),(80,'X','ClaimAutoAdjudication',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,24,NULL,NULL,NULL,NULL,12),(81,'D','IsProcessorReviewNeeded',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,13),(82,'V','ForkForAdviceRequest',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,14),(83,'A','AdviceRequest',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,15),(84,'A','ForwardedInternally',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,16),(85,'B','JoinAfterAdviceRequest',4,'\0',NULL,NULL,'Forwarded Internally',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,17),(86,'A','ProcessorReview',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,18),(87,'A','RejectedPartReturn',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,19),(88,'C','ReProcessAfterProcessorAcceptance',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,20),(89,'A','OnHold',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,21),(90,'A','Replies',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,22),(91,'A','Transferred',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,23),(92,'V','ForkForForwarded',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,24),(93,'A','Forwarded',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,25),(94,'A','ForwardedExternally',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,26),(95,'B','JoinAfterForwarded',4,'\0',NULL,NULL,'Forwarded Externally',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,27),(96,'C','PolicyAndPaymentProcessorUpdate',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,28),(97,'J','JoinPointForPartsReturn',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,29),(98,'X','GetPartReturnService',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,25,NULL,NULL,NULL,NULL,30),(99,'D','isClaimEligibleForPaymentAfterPartReturn',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,31),(100,'X','NotifyPayment',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,26,NULL,NULL,NULL,NULL,32),(101,'A','WaitForPaymentResponse',4,'\0',NULL,NULL,NULL,4,'','\0',NULL,NULL,NULL,NULL,NULL,33),(102,'X','MailPaymentInfo',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,27,NULL,NULL,NULL,NULL,34),(103,'E','End',4,'\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,35);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_node` ENABLE KEYS */;

--
-- Table structure for table `jbpm_pooledactor`
--

DROP TABLE IF EXISTS `jbpm_pooledactor`;
CREATE TABLE `jbpm_pooledactor` (
  `id_` bigint(20) NOT NULL auto_increment,
  `actorid_` varchar(255) default NULL,
  `swimlaneinstance_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `IDX_PLDACTR_ACTID` (`actorid_`),
  KEY `FK_POOLEDACTOR_SLI` (`swimlaneinstance_`),
  CONSTRAINT `FK_POOLEDACTOR_SLI` FOREIGN KEY (`swimlaneinstance_`) REFERENCES `jbpm_swimlaneinstance` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_pooledactor`
--


/*!40000 ALTER TABLE `jbpm_pooledactor` DISABLE KEYS */;
LOCK TABLES `jbpm_pooledactor` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_pooledactor` ENABLE KEYS */;

--
-- Table structure for table `jbpm_processdefinition`
--

DROP TABLE IF EXISTS `jbpm_processdefinition`;
CREATE TABLE `jbpm_processdefinition` (
  `id_` bigint(20) NOT NULL auto_increment,
  `name_` varchar(255) default NULL,
  `version_` int(11) default NULL,
  `isterminationimplicit_` bit(1) default NULL,
  `startstate_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_PROCDEF_STRTSTA` (`startstate_`),
  CONSTRAINT `FK_PROCDEF_STRTSTA` FOREIGN KEY (`startstate_`) REFERENCES `jbpm_node` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_processdefinition`
--


/*!40000 ALTER TABLE `jbpm_processdefinition` DISABLE KEYS */;
LOCK TABLES `jbpm_processdefinition` WRITE;
INSERT INTO `jbpm_processdefinition` VALUES (1,'PartsReturn',1,'\0',1),(2,'PolicyAndPaymentComputationProcess',1,'\0',48),(3,'ClaimAutoAdjudicationProcess',1,'\0',53),(4,'ClaimSubmission',1,'\0',68);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_processdefinition` ENABLE KEYS */;

--
-- Table structure for table `jbpm_processinstance`
--

DROP TABLE IF EXISTS `jbpm_processinstance`;
CREATE TABLE `jbpm_processinstance` (
  `id_` bigint(20) NOT NULL auto_increment,
  `version_` int(11) NOT NULL,
  `start_` datetime default NULL,
  `end_` datetime default NULL,
  `issuspended_` bit(1) default NULL,
  `processdefinition_` bigint(20) default NULL,
  `roottoken_` bigint(20) default NULL,
  `superprocesstoken_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_PROCIN_PROCDEF` (`processdefinition_`),
  KEY `FK_PROCIN_ROOTTKN` (`roottoken_`),
  KEY `FK_PROCIN_SPROCTKN` (`superprocesstoken_`),
  CONSTRAINT `FK_PROCIN_SPROCTKN` FOREIGN KEY (`superprocesstoken_`) REFERENCES `jbpm_token` (`id_`),
  CONSTRAINT `FK_PROCIN_PROCDEF` FOREIGN KEY (`processdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`),
  CONSTRAINT `FK_PROCIN_ROOTTKN` FOREIGN KEY (`roottoken_`) REFERENCES `jbpm_token` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_processinstance`
--


/*!40000 ALTER TABLE `jbpm_processinstance` DISABLE KEYS */;
LOCK TABLES `jbpm_processinstance` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_processinstance` ENABLE KEYS */;

--
-- Table structure for table `jbpm_runtimeaction`
--

DROP TABLE IF EXISTS `jbpm_runtimeaction`;
CREATE TABLE `jbpm_runtimeaction` (
  `id_` bigint(20) NOT NULL auto_increment,
  `version_` int(11) NOT NULL,
  `eventtype_` varchar(255) default NULL,
  `type_` char(1) default NULL,
  `graphelement_` bigint(20) default NULL,
  `processinstance_` bigint(20) default NULL,
  `action_` bigint(20) default NULL,
  `processinstanceindex_` int(11) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_RTACTN_PROCINST` (`processinstance_`),
  KEY `FK_RTACTN_ACTION` (`action_`),
  CONSTRAINT `FK_RTACTN_ACTION` FOREIGN KEY (`action_`) REFERENCES `jbpm_action` (`id_`),
  CONSTRAINT `FK_RTACTN_PROCINST` FOREIGN KEY (`processinstance_`) REFERENCES `jbpm_processinstance` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_runtimeaction`
--


/*!40000 ALTER TABLE `jbpm_runtimeaction` DISABLE KEYS */;
LOCK TABLES `jbpm_runtimeaction` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_runtimeaction` ENABLE KEYS */;

--
-- Table structure for table `jbpm_swimlane`
--

DROP TABLE IF EXISTS `jbpm_swimlane`;
CREATE TABLE `jbpm_swimlane` (
  `id_` bigint(20) NOT NULL auto_increment,
  `name_` varchar(255) default NULL,
  `actoridexpression_` varchar(255) default NULL,
  `pooledactorsexpression_` varchar(255) default NULL,
  `assignmentdelegation_` bigint(20) default NULL,
  `taskmgmtdefinition_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_SWL_ASSDEL` (`assignmentdelegation_`),
  KEY `FK_SWL_TSKMGMTDEF` (`taskmgmtdefinition_`),
  CONSTRAINT `FK_SWL_TSKMGMTDEF` FOREIGN KEY (`taskmgmtdefinition_`) REFERENCES `jbpm_moduledefinition` (`id_`),
  CONSTRAINT `FK_SWL_ASSDEL` FOREIGN KEY (`assignmentdelegation_`) REFERENCES `jbpm_delegation` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_swimlane`
--


/*!40000 ALTER TABLE `jbpm_swimlane` DISABLE KEYS */;
LOCK TABLES `jbpm_swimlane` WRITE;
INSERT INTO `jbpm_swimlane` VALUES (1,'supplier',NULL,NULL,4,2),(2,'receiver',NULL,NULL,5,2),(3,'dealer',NULL,NULL,6,2),(4,'sra',NULL,NULL,7,2),(5,'inspector',NULL,NULL,8,2),(6,'partshipper',NULL,NULL,9,2),(7,'dsm',NULL,NULL,28,8),(8,'dealer',NULL,NULL,29,8),(9,'processor',NULL,NULL,30,8);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_swimlane` ENABLE KEYS */;

--
-- Table structure for table `jbpm_swimlaneinstance`
--

DROP TABLE IF EXISTS `jbpm_swimlaneinstance`;
CREATE TABLE `jbpm_swimlaneinstance` (
  `id_` bigint(20) NOT NULL auto_increment,
  `name_` varchar(255) default NULL,
  `actorid_` varchar(255) default NULL,
  `swimlane_` bigint(20) default NULL,
  `taskmgmtinstance_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_SWIMLANEINST_TM` (`taskmgmtinstance_`),
  KEY `FK_SWIMLANEINST_SL` (`swimlane_`),
  CONSTRAINT `FK_SWIMLANEINST_SL` FOREIGN KEY (`swimlane_`) REFERENCES `jbpm_swimlane` (`id_`),
  CONSTRAINT `FK_SWIMLANEINST_TM` FOREIGN KEY (`taskmgmtinstance_`) REFERENCES `jbpm_moduleinstance` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_swimlaneinstance`
--


/*!40000 ALTER TABLE `jbpm_swimlaneinstance` DISABLE KEYS */;
LOCK TABLES `jbpm_swimlaneinstance` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_swimlaneinstance` ENABLE KEYS */;

--
-- Table structure for table `jbpm_task`
--

DROP TABLE IF EXISTS `jbpm_task`;
CREATE TABLE `jbpm_task` (
  `id_` bigint(20) NOT NULL auto_increment,
  `name_` varchar(255) default NULL,
  `processdefinition_` bigint(20) default NULL,
  `description_` text,
  `isblocking_` bit(1) default NULL,
  `issignalling_` bit(1) default NULL,
  `duedate_` varchar(255) default NULL,
  `actoridexpression_` varchar(255) default NULL,
  `pooledactorsexpression_` varchar(255) default NULL,
  `taskmgmtdefinition_` bigint(20) default NULL,
  `tasknode_` bigint(20) default NULL,
  `startstate_` bigint(20) default NULL,
  `assignmentdelegation_` bigint(20) default NULL,
  `swimlane_` bigint(20) default NULL,
  `taskcontroller_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_TSK_TSKCTRL` (`taskcontroller_`),
  KEY `FK_TASK_ASSDEL` (`assignmentdelegation_`),
  KEY `FK_TASK_TASKNODE` (`tasknode_`),
  KEY `FK_TASK_PROCDEF` (`processdefinition_`),
  KEY `FK_TASK_STARTST` (`startstate_`),
  KEY `FK_TASK_TASKMGTDEF` (`taskmgmtdefinition_`),
  KEY `FK_TASK_SWIMLANE` (`swimlane_`),
  CONSTRAINT `FK_TASK_SWIMLANE` FOREIGN KEY (`swimlane_`) REFERENCES `jbpm_swimlane` (`id_`),
  CONSTRAINT `FK_TASK_ASSDEL` FOREIGN KEY (`assignmentdelegation_`) REFERENCES `jbpm_delegation` (`id_`),
  CONSTRAINT `FK_TASK_PROCDEF` FOREIGN KEY (`processdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`),
  CONSTRAINT `FK_TASK_STARTST` FOREIGN KEY (`startstate_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_TASK_TASKMGTDEF` FOREIGN KEY (`taskmgmtdefinition_`) REFERENCES `jbpm_moduledefinition` (`id_`),
  CONSTRAINT `FK_TASK_TASKNODE` FOREIGN KEY (`tasknode_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_TSK_TSKCTRL` FOREIGN KEY (`taskcontroller_`) REFERENCES `jbpm_taskcontroller` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_task`
--


/*!40000 ALTER TABLE `jbpm_task` DISABLE KEYS */;
LOCK TABLES `jbpm_task` WRITE;
INSERT INTO `jbpm_task` VALUES (1,'Shipment from Dealer',1,NULL,'\0','',NULL,NULL,NULL,2,5,NULL,NULL,4,NULL),(2,'Dealer Wait for Timer',1,NULL,'\0','',NULL,NULL,NULL,2,7,NULL,NULL,NULL,NULL),(3,'Due Parts',1,NULL,'\0','',NULL,NULL,NULL,2,9,NULL,NULL,3,NULL),(4,'Overdue Parts',1,NULL,'\0','',NULL,NULL,NULL,2,10,NULL,NULL,3,NULL),(5,'Shipment Generated',1,NULL,'\0','',NULL,NULL,NULL,2,11,NULL,NULL,3,NULL),(6,'Parts Shipped',1,NULL,'\0','',NULL,NULL,NULL,2,16,NULL,NULL,3,NULL),(7,'Due Parts Receipt',1,NULL,'\0','',NULL,NULL,NULL,2,17,NULL,NULL,2,NULL),(8,'Due Parts Inspection',1,NULL,'\0','',NULL,NULL,NULL,2,21,NULL,NULL,5,NULL),(9,'Shipment from Warehouse',1,NULL,'\0','',NULL,NULL,NULL,2,23,NULL,NULL,4,NULL),(10,'Wait for Timer',1,NULL,'\0','',NULL,NULL,NULL,2,26,NULL,NULL,NULL,NULL),(11,'Supplier Response',1,NULL,'\0','',NULL,NULL,NULL,2,28,NULL,NULL,4,NULL),(12,'Awaiting Shipment',1,NULL,'\0','',NULL,NULL,NULL,2,32,NULL,NULL,4,NULL),(13,'Awaiting Supplier Response',1,NULL,'\0','',NULL,NULL,NULL,2,33,NULL,NULL,4,NULL),(14,'Supplier Parts Claimed',1,NULL,'\0','',NULL,NULL,NULL,2,35,NULL,NULL,6,NULL),(15,'Supplier Shipment Generated',1,NULL,'\0','',NULL,NULL,NULL,2,36,NULL,NULL,6,NULL),(16,'Supplier Parts Shipped',1,NULL,'\0','',NULL,NULL,NULL,2,39,NULL,NULL,6,NULL),(17,'New',1,NULL,'\0','',NULL,NULL,NULL,2,40,NULL,NULL,1,NULL),(18,'Disputed',1,NULL,'\0','',NULL,NULL,NULL,2,42,NULL,NULL,1,NULL),(19,'In Progress',1,NULL,'\0','',NULL,NULL,NULL,2,43,NULL,NULL,1,NULL),(20,'Draft Claim',4,NULL,'\0','',NULL,NULL,NULL,8,69,NULL,NULL,8,NULL),(21,'Service Manager Review',4,NULL,'\0','',NULL,NULL,NULL,8,72,NULL,NULL,7,NULL),(22,'Service Manager Response',4,NULL,'\0','',NULL,NULL,NULL,8,74,NULL,NULL,8,NULL),(23,'Advice Request',4,NULL,'\0','',NULL,NULL,NULL,8,83,NULL,NULL,7,NULL),(24,'Forwarded Internally',4,NULL,'\0','',NULL,NULL,NULL,8,84,NULL,NULL,9,NULL),(25,'Processor Review',4,NULL,'\0','',NULL,NULL,NULL,8,86,NULL,NULL,9,NULL),(26,'Rejected Part Return',4,NULL,'\0','',NULL,NULL,NULL,8,87,NULL,NULL,9,NULL),(27,'On Hold',4,NULL,'\0','',NULL,NULL,NULL,8,89,NULL,NULL,9,NULL),(28,'Replies',4,NULL,'\0','',NULL,NULL,NULL,8,90,NULL,NULL,9,NULL),(29,'Transferred',4,NULL,'\0','',NULL,NULL,NULL,8,91,NULL,NULL,9,NULL),(30,'Forwarded',4,NULL,'\0','',NULL,NULL,NULL,8,93,NULL,NULL,8,NULL),(31,'Forwarded Externally',4,NULL,'\0','',NULL,NULL,NULL,8,94,NULL,NULL,9,NULL),(32,'PaymentWaitTask',4,NULL,'\0','',NULL,NULL,NULL,8,101,NULL,NULL,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_task` ENABLE KEYS */;

--
-- Table structure for table `jbpm_taskactorpool`
--

DROP TABLE IF EXISTS `jbpm_taskactorpool`;
CREATE TABLE `jbpm_taskactorpool` (
  `pooledactor_` bigint(20) NOT NULL,
  `taskinstance_` bigint(20) NOT NULL,
  PRIMARY KEY  (`taskinstance_`,`pooledactor_`),
  KEY `FK_TSKACTPOL_PLACT` (`pooledactor_`),
  KEY `FK_TASKACTPL_TSKI` (`taskinstance_`),
  CONSTRAINT `FK_TASKACTPL_TSKI` FOREIGN KEY (`taskinstance_`) REFERENCES `jbpm_taskinstance` (`id_`),
  CONSTRAINT `FK_TSKACTPOL_PLACT` FOREIGN KEY (`pooledactor_`) REFERENCES `jbpm_pooledactor` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_taskactorpool`
--


/*!40000 ALTER TABLE `jbpm_taskactorpool` DISABLE KEYS */;
LOCK TABLES `jbpm_taskactorpool` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_taskactorpool` ENABLE KEYS */;

--
-- Table structure for table `jbpm_taskcontroller`
--

DROP TABLE IF EXISTS `jbpm_taskcontroller`;
CREATE TABLE `jbpm_taskcontroller` (
  `id_` bigint(20) NOT NULL auto_increment,
  `taskcontrollerdelegation_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_TSKCTRL_DELEG` (`taskcontrollerdelegation_`),
  CONSTRAINT `FK_TSKCTRL_DELEG` FOREIGN KEY (`taskcontrollerdelegation_`) REFERENCES `jbpm_delegation` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_taskcontroller`
--


/*!40000 ALTER TABLE `jbpm_taskcontroller` DISABLE KEYS */;
LOCK TABLES `jbpm_taskcontroller` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_taskcontroller` ENABLE KEYS */;

--
-- Table structure for table `jbpm_taskinstance`
--

DROP TABLE IF EXISTS `jbpm_taskinstance`;
CREATE TABLE `jbpm_taskinstance` (
  `id_` bigint(20) NOT NULL auto_increment,
  `class_` char(1) NOT NULL,
  `name_` varchar(255) default NULL,
  `description_` text,
  `actorid_` varchar(255) default NULL,
  `create_` datetime default NULL,
  `start_` datetime default NULL,
  `end_` datetime default NULL,
  `duedate_` datetime default NULL,
  `priority_` int(11) default NULL,
  `iscancelled_` bit(1) default NULL,
  `issuspended_` bit(1) default NULL,
  `isopen_` bit(1) default NULL,
  `issignalling_` bit(1) default NULL,
  `isblocking_` bit(1) default NULL,
  `task_` bigint(20) default NULL,
  `token_` bigint(20) default NULL,
  `swimlaninstance_` bigint(20) default NULL,
  `taskmgmtinstance_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `IDX_TASK_ACTORID` (`actorid_`),
  KEY `FK_TASKINST_TMINST` (`taskmgmtinstance_`),
  KEY `FK_TASKINST_TOKEN` (`token_`),
  KEY `FK_TASKINST_SLINST` (`swimlaninstance_`),
  KEY `FK_TASKINST_TASK` (`task_`),
  CONSTRAINT `FK_TASKINST_TASK` FOREIGN KEY (`task_`) REFERENCES `jbpm_task` (`id_`),
  CONSTRAINT `FK_TASKINST_SLINST` FOREIGN KEY (`swimlaninstance_`) REFERENCES `jbpm_swimlaneinstance` (`id_`),
  CONSTRAINT `FK_TASKINST_TMINST` FOREIGN KEY (`taskmgmtinstance_`) REFERENCES `jbpm_moduleinstance` (`id_`),
  CONSTRAINT `FK_TASKINST_TOKEN` FOREIGN KEY (`token_`) REFERENCES `jbpm_token` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_taskinstance`
--


/*!40000 ALTER TABLE `jbpm_taskinstance` DISABLE KEYS */;
LOCK TABLES `jbpm_taskinstance` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_taskinstance` ENABLE KEYS */;

--
-- Table structure for table `jbpm_timer`
--

DROP TABLE IF EXISTS `jbpm_timer`;
CREATE TABLE `jbpm_timer` (
  `id_` bigint(20) NOT NULL auto_increment,
  `name_` varchar(255) default NULL,
  `duedate_` datetime default NULL,
  `repeat_` varchar(255) default NULL,
  `transitionname_` varchar(255) default NULL,
  `exception_` text,
  `issuspended_` bit(1) default NULL,
  `action_` bigint(20) default NULL,
  `token_` bigint(20) default NULL,
  `processinstance_` bigint(20) default NULL,
  `taskinstance_` bigint(20) default NULL,
  `graphelementtype_` varchar(255) default NULL,
  `graphelement_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_TIMER_TOKEN` (`token_`),
  KEY `FK_TIMER_PRINST` (`processinstance_`),
  KEY `FK_TIMER_ACTION` (`action_`),
  KEY `FK_TIMER_TSKINST` (`taskinstance_`),
  CONSTRAINT `FK_TIMER_TSKINST` FOREIGN KEY (`taskinstance_`) REFERENCES `jbpm_taskinstance` (`id_`),
  CONSTRAINT `FK_TIMER_ACTION` FOREIGN KEY (`action_`) REFERENCES `jbpm_action` (`id_`),
  CONSTRAINT `FK_TIMER_PRINST` FOREIGN KEY (`processinstance_`) REFERENCES `jbpm_processinstance` (`id_`),
  CONSTRAINT `FK_TIMER_TOKEN` FOREIGN KEY (`token_`) REFERENCES `jbpm_token` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_timer`
--


/*!40000 ALTER TABLE `jbpm_timer` DISABLE KEYS */;
LOCK TABLES `jbpm_timer` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_timer` ENABLE KEYS */;

--
-- Table structure for table `jbpm_token`
--

DROP TABLE IF EXISTS `jbpm_token`;
CREATE TABLE `jbpm_token` (
  `id_` bigint(20) NOT NULL auto_increment,
  `version_` int(11) NOT NULL,
  `name_` varchar(255) default NULL,
  `start_` datetime default NULL,
  `end_` datetime default NULL,
  `nodeenter_` datetime default NULL,
  `nextlogindex_` int(11) default NULL,
  `isabletoreactivateparent_` bit(1) default NULL,
  `isterminationimplicit_` bit(1) default NULL,
  `issuspended_` bit(1) default NULL,
  `node_` bigint(20) default NULL,
  `processinstance_` bigint(20) default NULL,
  `parent_` bigint(20) default NULL,
  `subprocessinstance_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_TOKEN_PARENT` (`parent_`),
  KEY `FK_TOKEN_NODE` (`node_`),
  KEY `FK_TOKEN_PROCINST` (`processinstance_`),
  KEY `FK_TOKEN_SUBPI` (`subprocessinstance_`),
  CONSTRAINT `FK_TOKEN_SUBPI` FOREIGN KEY (`subprocessinstance_`) REFERENCES `jbpm_processinstance` (`id_`),
  CONSTRAINT `FK_TOKEN_NODE` FOREIGN KEY (`node_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_TOKEN_PARENT` FOREIGN KEY (`parent_`) REFERENCES `jbpm_token` (`id_`),
  CONSTRAINT `FK_TOKEN_PROCINST` FOREIGN KEY (`processinstance_`) REFERENCES `jbpm_processinstance` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_token`
--


/*!40000 ALTER TABLE `jbpm_token` DISABLE KEYS */;
LOCK TABLES `jbpm_token` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_token` ENABLE KEYS */;

--
-- Table structure for table `jbpm_tokenvariablemap`
--

DROP TABLE IF EXISTS `jbpm_tokenvariablemap`;
CREATE TABLE `jbpm_tokenvariablemap` (
  `id_` bigint(20) NOT NULL auto_increment,
  `token_` bigint(20) default NULL,
  `contextinstance_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_TKVARMAP_CTXT` (`contextinstance_`),
  KEY `FK_TKVARMAP_TOKEN` (`token_`),
  CONSTRAINT `FK_TKVARMAP_TOKEN` FOREIGN KEY (`token_`) REFERENCES `jbpm_token` (`id_`),
  CONSTRAINT `FK_TKVARMAP_CTXT` FOREIGN KEY (`contextinstance_`) REFERENCES `jbpm_moduleinstance` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_tokenvariablemap`
--


/*!40000 ALTER TABLE `jbpm_tokenvariablemap` DISABLE KEYS */;
LOCK TABLES `jbpm_tokenvariablemap` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_tokenvariablemap` ENABLE KEYS */;

--
-- Table structure for table `jbpm_transition`
--

DROP TABLE IF EXISTS `jbpm_transition`;
CREATE TABLE `jbpm_transition` (
  `id_` bigint(20) NOT NULL auto_increment,
  `name_` varchar(255) default NULL,
  `processdefinition_` bigint(20) default NULL,
  `from_` bigint(20) default NULL,
  `to_` bigint(20) default NULL,
  `fromindex_` int(11) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_TRANSITION_TO` (`to_`),
  KEY `FK_TRANS_PROCDEF` (`processdefinition_`),
  KEY `FK_TRANSITION_FROM` (`from_`),
  CONSTRAINT `FK_TRANSITION_FROM` FOREIGN KEY (`from_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_TRANSITION_TO` FOREIGN KEY (`to_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_TRANS_PROCDEF` FOREIGN KEY (`processdefinition_`) REFERENCES `jbpm_processdefinition` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_transition`
--


/*!40000 ALTER TABLE `jbpm_transition` DISABLE KEYS */;
LOCK TABLES `jbpm_transition` WRITE;
INSERT INTO `jbpm_transition` VALUES (1,'',1,1,2,0),(2,'No',1,2,47,0),(3,'ShipmentToSupplier',1,2,3,1),(4,'ShipmentToOEM',1,2,9,2),(5,'',1,3,4,0),(6,'Contract Is Known',1,4,6,0),(7,'Contract Is Not Known',1,4,5,1),(8,'Send To Dealer',1,5,6,0),(9,'Accept',1,5,47,1),(10,'Physical',1,6,9,0),(11,'Logical',1,6,7,1),(12,'',1,7,8,0),(13,'Sra',1,8,33,0),(14,'Supplier',1,8,40,1),(15,'Part Overdue',1,9,10,0),(16,'Generate Shipment',1,9,11,1),(17,'Generate Shipment',1,10,11,0),(18,'Submit',1,11,13,0),(19,'Remove Part',1,11,12,1),(20,'Yes',1,12,10,0),(21,'No',1,12,9,1),(22,'ShipmentToSupplier',1,13,14,0),(23,'ShipmentToOEM',1,13,15,1),(24,'Parts Shipped',1,14,16,0),(25,'Sra',1,14,33,1),(26,'Supplier',1,14,40,2),(27,'Parts Shipped',1,15,16,0),(28,'Parts Received',1,15,17,1),(29,'To SingleTokenJoin',1,16,47,0),(30,'Received Due Parts',1,17,18,0),(31,NULL,1,18,19,0),(32,'Send to Clone',1,19,20,0),(33,'Send for Inspection',1,19,21,1),(34,'Part Not Received',1,19,9,2),(35,'Part Closed',1,19,47,3),(36,'Notify Payment On Receipt',1,20,47,0),(37,'Send for Inspection',1,20,21,1),(38,'Mark for Supplier Recovery',1,21,22,0),(39,'Mark for Reuse or Scrap',1,21,47,1),(40,'Yes',1,22,23,0),(41,'No',1,22,47,1),(42,'Send To Supplier',1,23,24,0),(43,'Accept',1,23,47,1),(44,'Physical',1,24,27,0),(45,'Logical',1,24,25,1),(46,'Claims Sent To Supplier',1,25,33,0),(47,'Send To Supplier',1,25,26,1),(48,'',1,26,40,0),(49,'Claims Sent To Shipper',1,27,32,0),(50,'Send To Supplier',1,27,35,1),(51,'Send To Supplier',1,28,29,0),(52,'Recovered Submit',1,28,30,1),(53,'Unrecovered Submit',1,28,30,2),(54,NULL,1,29,31,0),(55,NULL,1,30,47,0),(56,'Claims Sent To Supplier',1,31,33,0),(57,'In Progress Supplier Claims',1,31,43,1),(58,'Claims Sent To Supplier',1,32,33,0),(59,'Claims Sent',1,33,47,0),(60,NULL,1,34,45,0),(61,'Generate Shipment',1,35,36,0),(62,'Update',1,36,37,0),(63,'Remove',1,36,35,1),(64,NULL,1,37,38,0),(65,'Part Shipper Parts Shipped',1,38,39,0),(66,'Update',1,38,40,1),(67,'Parts Shipped',1,39,47,0),(68,'Accept',1,40,44,0),(69,'Reject',1,40,44,1),(70,'Not Received',1,40,44,2),(71,'Disputed Supplier Claims',1,41,42,0),(72,'Supplier Reject',1,41,28,1),(73,'Accept',1,42,47,0),(74,'Accept',1,43,44,0),(75,'Reject',1,43,44,1),(76,'Not Received',1,43,44,2),(77,NULL,1,44,34,0),(78,'Accept',1,45,28,0),(79,'Reject',1,45,41,1),(80,'Not Received',1,45,46,2),(81,'NotReceivedNotifyToDealer',1,46,9,0),(82,'NotReceivedNotifyToPartShipper',1,46,35,1),(83,'',2,48,49,0),(84,'No',2,49,51,0),(85,'Yes',2,49,50,1),(86,'',2,50,51,0),(87,'',2,51,52,0),(88,'',3,53,54,0),(89,'',3,54,55,0),(90,'',3,55,56,0),(91,'',3,56,57,0),(92,'ShouldCheckDuplicates',3,57,58,0),(93,'',3,57,59,1),(94,'',3,58,59,0),(95,'Yes',3,59,60,0),(96,'No',3,59,66,1),(97,'ShouldCheckDuplicates',3,60,61,0),(98,'',3,60,62,1),(99,'',3,61,62,0),(100,'No',3,62,64,0),(101,'Yes',3,62,63,1),(102,'',3,63,64,0),(103,'No',3,64,66,0),(104,'Yes',3,64,65,1),(105,'',3,65,66,0),(106,'',3,66,67,0),(107,'Submit',4,68,70,0),(108,'Draft',4,68,69,1),(109,'Save Draft',4,69,69,0),(110,'Delete Draft',4,69,103,1),(111,'Submit Claim',4,69,70,2),(112,'',4,70,71,0),(113,'No',4,71,76,0),(114,'Yes',4,71,72,1),(115,'Reject',4,72,73,0),(116,'Accept',4,72,73,1),(117,'',4,73,74,0),(118,'Re-requests for SMR',4,74,72,0),(119,'Delete',4,74,103,1),(120,'Submit',4,74,75,2),(121,'',4,75,76,0),(122,'',4,76,77,0),(123,'',4,77,78,0),(124,NULL,4,78,80,0),(125,'ForkPaths',4,78,79,1),(126,NULL,4,79,97,0),(127,'',4,80,81,0),(128,'Accept',4,81,97,0),(129,'ReferForManualReview',4,81,86,1),(130,'DenyClaim',4,81,103,2),(131,'PutClaimOnHold',4,81,89,3),(132,'Advice Request',4,82,83,0),(133,'Forwarded Internally',4,82,84,1),(134,'Advice',4,83,85,0),(135,'JoinAfterForwardedInternally',4,84,103,0),(136,NULL,4,85,90,0),(137,'Accept',4,86,97,0),(138,'Deny',4,86,103,1),(139,'Re-process',4,86,88,2),(140,'Hold',4,86,89,3),(141,'Forward to Dealer',4,86,92,4),(142,'Transfer',4,86,91,5),(143,'Seek Advice',4,86,82,6),(144,'Accept',4,87,97,0),(145,'Deny',4,87,103,1),(146,'Re-process',4,87,88,2),(147,'Hold',4,87,89,3),(148,'Forward to Dealer',4,87,92,4),(149,'Transfer',4,87,91,5),(150,'Seek Advice',4,87,82,6),(151,'',4,88,80,0),(152,'Accept',4,89,97,0),(153,'Deny',4,89,103,1),(154,'Re-process',4,89,88,2),(155,'Forward to Dealer',4,89,92,3),(156,'Transfer',4,89,91,4),(157,'Seek Advice',4,89,82,5),(158,'Accept',4,90,97,0),(159,'Deny',4,90,103,1),(160,'Re-process',4,90,88,2),(161,'Hold',4,90,89,3),(162,'Forward to Dealer',4,90,92,4),(163,'Transfer',4,90,91,5),(164,'Seek Advice',4,90,82,6),(165,'Accept',4,91,97,0),(166,'Deny',4,91,103,1),(167,'Re-process',4,91,88,2),(168,'Hold',4,91,89,3),(169,'Forward to Dealer',4,91,92,4),(170,'Transfer',4,91,91,5),(171,'Seek Advice',4,91,82,6),(172,'Forward',4,92,93,0),(173,'Forwarded Externally',4,92,94,1),(174,'Submit',4,93,95,0),(175,'JoinAfterForwardedInternally',4,94,103,0),(176,NULL,4,95,96,0),(177,'',4,96,90,0),(178,NULL,4,97,98,0),(179,'',4,98,99,0),(180,'ClaimNotEligibleForPayment',4,99,87,0),(181,'ClaimEligibleForPayment',4,99,100,1),(182,'',4,100,101,0),(183,'',4,101,102,0),(184,'',4,102,103,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_transition` ENABLE KEYS */;

--
-- Table structure for table `jbpm_variableaccess`
--

DROP TABLE IF EXISTS `jbpm_variableaccess`;
CREATE TABLE `jbpm_variableaccess` (
  `id_` bigint(20) NOT NULL auto_increment,
  `variablename_` varchar(255) default NULL,
  `access_` varchar(255) default NULL,
  `mappedname_` varchar(255) default NULL,
  `taskcontroller_` bigint(20) default NULL,
  `index_` int(11) default NULL,
  `processstate_` bigint(20) default NULL,
  `script_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_VARACC_TSKCTRL` (`taskcontroller_`),
  KEY `FK_VARACC_SCRIPT` (`script_`),
  KEY `FK_VARACC_PROCST` (`processstate_`),
  CONSTRAINT `FK_VARACC_PROCST` FOREIGN KEY (`processstate_`) REFERENCES `jbpm_node` (`id_`),
  CONSTRAINT `FK_VARACC_SCRIPT` FOREIGN KEY (`script_`) REFERENCES `jbpm_action` (`id_`),
  CONSTRAINT `FK_VARACC_TSKCTRL` FOREIGN KEY (`taskcontroller_`) REFERENCES `jbpm_taskcontroller` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_variableaccess`
--


/*!40000 ALTER TABLE `jbpm_variableaccess` DISABLE KEYS */;
LOCK TABLES `jbpm_variableaccess` WRITE;
INSERT INTO `jbpm_variableaccess` VALUES (1,'claim','read,write','claim',NULL,NULL,70,NULL),(2,'claim','read,write','claim',NULL,NULL,73,NULL),(3,'claim','read,write','claim',NULL,NULL,75,NULL),(4,'claim','read,write','claim',NULL,NULL,79,NULL),(5,'part','read','part',NULL,NULL,79,NULL),(6,'claim','read,write','claim',NULL,NULL,88,NULL),(7,'claim','read,write','claim',NULL,NULL,96,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_variableaccess` ENABLE KEYS */;

--
-- Table structure for table `jbpm_variableinstance`
--

DROP TABLE IF EXISTS `jbpm_variableinstance`;
CREATE TABLE `jbpm_variableinstance` (
  `id_` bigint(20) NOT NULL auto_increment,
  `class_` char(1) NOT NULL,
  `name_` varchar(255) default NULL,
  `converter_` char(1) default NULL,
  `token_` bigint(20) default NULL,
  `tokenvariablemap_` bigint(20) default NULL,
  `processinstance_` bigint(20) default NULL,
  `bytearrayvalue_` bigint(20) default NULL,
  `longvalue_` bigint(20) default NULL,
  `stringidclass_` varchar(255) default NULL,
  `stringvalue_` varchar(255) default NULL,
  `longidclass_` varchar(255) default NULL,
  `doublevalue_` double default NULL,
  `datevalue_` datetime default NULL,
  `taskinstance_` bigint(20) default NULL,
  PRIMARY KEY  (`id_`),
  KEY `FK_VARINST_TK` (`token_`),
  KEY `FK_VARINST_TKVARMP` (`tokenvariablemap_`),
  KEY `FK_VARINST_PRCINST` (`processinstance_`),
  KEY `FK_VAR_TSKINST` (`taskinstance_`),
  KEY `FK_BYTEINST_ARRAY` (`bytearrayvalue_`),
  CONSTRAINT `FK_BYTEINST_ARRAY` FOREIGN KEY (`bytearrayvalue_`) REFERENCES `jbpm_bytearray` (`id_`),
  CONSTRAINT `FK_VARINST_PRCINST` FOREIGN KEY (`processinstance_`) REFERENCES `jbpm_processinstance` (`id_`),
  CONSTRAINT `FK_VARINST_TK` FOREIGN KEY (`token_`) REFERENCES `jbpm_token` (`id_`),
  CONSTRAINT `FK_VARINST_TKVARMP` FOREIGN KEY (`tokenvariablemap_`) REFERENCES `jbpm_tokenvariablemap` (`id_`),
  CONSTRAINT `FK_VAR_TSKINST` FOREIGN KEY (`taskinstance_`) REFERENCES `jbpm_taskinstance` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jbpm_variableinstance`
--


/*!40000 ALTER TABLE `jbpm_variableinstance` DISABLE KEYS */;
LOCK TABLES `jbpm_variableinstance` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `jbpm_variableinstance` ENABLE KEYS */;

--
-- Table structure for table `job`
--

DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `id` bigint(20) NOT NULL auto_increment,
  `job_definition` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK19BBD2C4D26DE` (`job_definition`),
  CONSTRAINT `FK19BBD2C4D26DE` FOREIGN KEY (`job_definition`) REFERENCES `job_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `job`
--


/*!40000 ALTER TABLE `job` DISABLE KEYS */;
LOCK TABLES `job` WRITE;
INSERT INTO `job` VALUES (1,1),(2,2),(3,3),(4,4);
UNLOCK TABLES;
/*!40000 ALTER TABLE `job` ENABLE KEYS */;

--
-- Table structure for table `job_definition`
--

DROP TABLE IF EXISTS `job_definition`;
CREATE TABLE `job_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  `expected_number_of_hours` int(11) NOT NULL,
  `labor_rate_amt` decimal(19,2) default NULL,
  `labor_rate_curr` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `job_definition`
--


/*!40000 ALTER TABLE `job_definition` DISABLE KEYS */;
LOCK TABLES `job_definition` WRITE;
INSERT INTO `job_definition` VALUES (1,'PP-001-replaced',2,'15.00','USD','replaced'),(2,'HD-001-repaired',4,'20.00','USD','repaired'),(3,'PP-001-repaired',4,'20.00','USD','replaced'),(4,'HD-001-replaced',4,'15.00','USD','repaired');
UNLOCK TABLES;
/*!40000 ALTER TABLE `job_definition` ENABLE KEYS */;

--
-- Table structure for table `labor_detail`
--

DROP TABLE IF EXISTS `labor_detail`;
CREATE TABLE `labor_detail` (
  `id` bigint(20) NOT NULL auto_increment,
  `additional_labor_hours` int(11) default NULL,
  `hours_spent` int(11) NOT NULL,
  `labor_rate_amt` decimal(19,2) default NULL,
  `labor_rate_curr` varchar(255) default NULL,
  `reason_for_additional_hours` varchar(255) default NULL,
  `service_procedure` bigint(20) default NULL,
  `job_performed` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKB82FF40303197AB` (`service_procedure`),
  KEY `FKB82FF408762F94` (`job_performed`),
  CONSTRAINT `FKB82FF408762F94` FOREIGN KEY (`job_performed`) REFERENCES `job` (`id`),
  CONSTRAINT `FKB82FF40303197AB` FOREIGN KEY (`service_procedure`) REFERENCES `service_procedure` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `labor_detail`
--


/*!40000 ALTER TABLE `labor_detail` DISABLE KEYS */;
LOCK TABLES `labor_detail` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `labor_detail` ENABLE KEYS */;

--
-- Table structure for table `labor_rate`
--

DROP TABLE IF EXISTS `labor_rate`;
CREATE TABLE `labor_rate` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date NOT NULL,
  `till_date` date NOT NULL,
  `amt` decimal(19,2) default NULL,
  `curr` varchar(255) default NULL,
  `labor_rates` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKB8E9238FF067FBB1` (`labor_rates`),
  CONSTRAINT `FKB8E9238FF067FBB1` FOREIGN KEY (`labor_rates`) REFERENCES `labor_rates` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `labor_rate`
--


/*!40000 ALTER TABLE `labor_rate` DISABLE KEYS */;
LOCK TABLES `labor_rate` WRITE;
INSERT INTO `labor_rate` VALUES (1,'2001-01-01','2010-01-01','1.00','USD',1),(2,'2001-01-01','2010-01-01','2.00','USD',2),(3,'2001-01-01','2010-01-01','3.00','USD',3),(4,'2001-01-01','2010-01-01','4.00','USD',4),(5,'2001-01-01','2010-01-01','5.00','USD',5),(6,'2001-01-01','2010-01-01','6.00','USD',6),(7,'2001-01-01','2010-01-01','7.00','USD',7),(8,'2001-01-01','2010-01-01','8.00','USD',8),(9,'2001-01-01','2010-01-01','9.00','USD',9),(10,'2001-01-01','2010-01-01','10.00','USD',10),(11,'2001-01-01','2010-01-01','11.00','USD',11),(12,'2001-01-01','2010-01-01','12.00','USD',12),(13,'2001-01-01','2010-01-01','13.00','USD',13),(14,'2001-01-01','2010-01-01','14.00','USD',14),(15,'2001-01-01','2010-01-01','15.00','USD',15),(16,'2001-01-01','2010-01-01','16.00','USD',16),(17,'2001-01-01','2010-01-01','17.00','USD',17),(18,'2001-01-01','2010-01-01','18.00','USD',18),(19,'2001-01-01','2010-01-01','19.00','USD',19),(20,'2001-01-01','2010-01-01','20.00','USD',20),(21,'2001-01-01','2010-01-01','21.00','USD',21),(22,'2001-01-01','2010-01-01','22.00','USD',22),(23,'2001-01-01','2010-01-01','23.00','USD',23),(24,'2001-01-01','2010-01-01','24.00','USD',24),(25,'2001-01-01','2010-01-01','25.00','USD',25),(26,'2001-01-01','2010-01-01','26.00','USD',26),(27,'2001-01-01','2010-01-01','27.00','USD',27),(28,'2001-01-01','2010-01-01','28.00','USD',28),(29,'2001-01-01','2010-01-01','29.00','USD',29),(30,'2001-01-01','2010-01-01','30.00','USD',30),(31,'2001-01-01','2010-01-01','31.00','USD',31),(32,'2001-01-01','2010-01-01','32.00','USD',32);
UNLOCK TABLES;
/*!40000 ALTER TABLE `labor_rate` ENABLE KEYS */;

--
-- Table structure for table `labor_rates`
--

DROP TABLE IF EXISTS `labor_rates`;
CREATE TABLE `labor_rates` (
  `id` bigint(20) NOT NULL auto_increment,
  `claim_type` varchar(255) default NULL,
  `relevance_score` bigint(20) default NULL,
  `warranty_type` varchar(255) default NULL,
  `for_criteria_product_type` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer_group` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK643B4EC41BC64DD6` (`for_criteria_product_type`),
  KEY `FK643B4EC4BDF2C045` (`for_criteria_dealer_criterion_dealer_group`),
  KEY `FK643B4EC43BC5B29C` (`for_criteria_dealer_criterion_dealer`),
  CONSTRAINT `FK643B4EC43BC5B29C` FOREIGN KEY (`for_criteria_dealer_criterion_dealer`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FK643B4EC41BC64DD6` FOREIGN KEY (`for_criteria_product_type`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FK643B4EC4BDF2C045` FOREIGN KEY (`for_criteria_dealer_criterion_dealer_group`) REFERENCES `dealer_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `labor_rates`
--


/*!40000 ALTER TABLE `labor_rates` DISABLE KEYS */;
LOCK TABLES `labor_rates` WRITE;
INSERT INTO `labor_rates` VALUES (1,NULL,0,NULL,NULL,NULL,NULL),(2,NULL,1,NULL,5,NULL,NULL),(3,NULL,2,'STANDARD',NULL,NULL,NULL),(4,NULL,3,'STANDARD',5,NULL,NULL),(5,'Machine',4,NULL,NULL,NULL,NULL),(6,'Machine',5,NULL,5,NULL,NULL),(7,'Machine',6,'STANDARD',NULL,NULL,NULL),(8,'Machine',7,'STANDARD',5,NULL,NULL),(9,NULL,8,NULL,NULL,NULL,2),(10,NULL,9,NULL,5,NULL,2),(11,NULL,10,'STANDARD',NULL,NULL,2),(12,NULL,11,'STANDARD',5,NULL,2),(13,'Machine',12,NULL,NULL,NULL,2),(14,'Machine',13,NULL,5,NULL,2),(15,'Machine',14,'STANDARD',NULL,NULL,2),(16,'Machine',15,'STANDARD',5,NULL,2),(17,NULL,8,NULL,NULL,NULL,1),(18,NULL,9,NULL,5,NULL,1),(19,NULL,10,'STANDARD',NULL,NULL,1),(20,NULL,11,'STANDARD',5,NULL,1),(21,'Machine',12,NULL,NULL,NULL,1),(22,'Machine',13,NULL,5,NULL,1),(23,'Machine',14,'STANDARD',NULL,NULL,1),(24,'Machine',15,'STANDARD',5,NULL,1),(25,NULL,16,NULL,NULL,7,NULL),(26,NULL,17,NULL,5,7,NULL),(27,NULL,18,'STANDARD',NULL,7,NULL),(28,NULL,19,'STANDARD',5,7,NULL),(29,'Machine',20,NULL,NULL,7,NULL),(30,'Machine',21,NULL,5,7,NULL),(31,'Machine',22,'STANDARD',NULL,7,NULL),(32,'Machine',23,'STANDARD',5,7,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `labor_rates` ENABLE KEYS */;

--
-- Table structure for table `line_item`
--

DROP TABLE IF EXISTS `line_item`;
CREATE TABLE `line_item` (
  `id` bigint(20) NOT NULL,
  `level` int(11) NOT NULL,
  `modifier_percentage` double NOT NULL,
  `name` varchar(255) NOT NULL,
  `amt` decimal(19,2) NOT NULL,
  `curr` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `line_item`
--


/*!40000 ALTER TABLE `line_item` DISABLE KEYS */;
LOCK TABLES `line_item` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `line_item` ENABLE KEYS */;

--
-- Table structure for table `line_item_group`
--

DROP TABLE IF EXISTS `line_item_group`;
CREATE TABLE `line_item_group` (
  `id` bigint(20) NOT NULL,
  `accepted_amt` decimal(19,2) NOT NULL,
  `accepted_curr` varchar(255) NOT NULL,
  `total_amt` decimal(19,2) NOT NULL,
  `total_curr` varchar(255) NOT NULL,
  `name` varchar(255) default NULL,
  `percentage_acceptance` decimal(19,2) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `line_item_group`
--


/*!40000 ALTER TABLE `line_item_group` DISABLE KEYS */;
LOCK TABLES `line_item_group` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `line_item_group` ENABLE KEYS */;

--
-- Table structure for table `line_item_groups`
--

DROP TABLE IF EXISTS `line_item_groups`;
CREATE TABLE `line_item_groups` (
  `for_payment` bigint(20) NOT NULL,
  `line_item_groups` bigint(20) NOT NULL,
  UNIQUE KEY `line_item_groups` (`line_item_groups`),
  KEY `FKDB1510B576E82E3E` (`line_item_groups`),
  KEY `FKDB1510B5F92E4347` (`for_payment`),
  CONSTRAINT `FKDB1510B5F92E4347` FOREIGN KEY (`for_payment`) REFERENCES `payment` (`id`),
  CONSTRAINT `FKDB1510B576E82E3E` FOREIGN KEY (`line_item_groups`) REFERENCES `line_item_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `line_item_groups`
--


/*!40000 ALTER TABLE `line_item_groups` DISABLE KEYS */;
LOCK TABLES `line_item_groups` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `line_item_groups` ENABLE KEYS */;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
CREATE TABLE `location` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  `address` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK714F9FB53F73AC54` (`address`),
  CONSTRAINT `FK714F9FB53F73AC54` FOREIGN KEY (`address`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `location`
--


/*!40000 ALTER TABLE `location` DISABLE KEYS */;
LOCK TABLES `location` WRITE;
INSERT INTO `location` VALUES (1,'Ankeny',1),(2,'Milford',2),(3,'Kansas',1),(4,'New Jersey',10),(5,'New York (Northwind)',11),(6,'New York (Truckdrove)',12),(7,'San Jose',13),(8,'North Carolina',14),(9,'New York (Backyard)',15),(10,'Ohio',16),(11,'Alabama',17);
UNLOCK TABLES;
/*!40000 ALTER TABLE `location` ENABLE KEYS */;

--
-- Table structure for table `market_type`
--

DROP TABLE IF EXISTS `market_type`;
CREATE TABLE `market_type` (
  `id` bigint(20) NOT NULL,
  `code` bigint(20) default NULL,
  `title` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `market_type`
--


/*!40000 ALTER TABLE `market_type` DISABLE KEYS */;
LOCK TABLES `market_type` WRITE;
INSERT INTO `market_type` VALUES (1,11,'Agriculture, Forestry, Fishing and Hunting'),(2,21,'Mining'),(3,22,'Utilities'),(4,23,'Construction'),(5,42,'Wholesale Trade'),(6,51,'Information'),(7,52,'Finance and Insurance'),(8,53,'Real Estate and Rental and Leasing'),(9,54,'Professional, Scientific, and Technical Services'),(10,55,'Management of Companies and Enterprises'),(11,56,'Administrative and Support and Waste Management and Remediation Services'),(12,61,'Educational Services'),(13,62,'Health Care and Social Assistance'),(14,71,'Arts, Entertainment, and Recreation'),(15,72,'Accommodation and Food Services'),(16,81,'Other Services (except Public Administration)'),(17,92,'Public Administration'),(18,31,'Manufacturing'),(19,44,'Retail Trade'),(20,48,'Transportation and Warehousing');
UNLOCK TABLES;
/*!40000 ALTER TABLE `market_type` ENABLE KEYS */;

--
-- Table structure for table `marketing_information`
--

DROP TABLE IF EXISTS `marketing_information`;
CREATE TABLE `marketing_information` (
  `id` bigint(20) NOT NULL auto_increment,
  `customer_first_time_owner` bit(1) NOT NULL,
  `months` int(11) default NULL,
  `years` int(11) default NULL,
  `sales_man` bigint(20) default NULL,
  `transaction_type` varchar(255) default NULL,
  `competition_type` varchar(255) default NULL,
  `market_type` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK18AE94936AD271F9` (`sales_man`),
  KEY `FK18AE9493AFA8053A` (`market_type`),
  KEY `FK18AE9493276FF56C` (`competition_type`),
  KEY `FK18AE9493520B936C` (`transaction_type`),
  CONSTRAINT `FK18AE9493520B936C` FOREIGN KEY (`transaction_type`) REFERENCES `transaction_type` (`type`),
  CONSTRAINT `FK18AE9493276FF56C` FOREIGN KEY (`competition_type`) REFERENCES `competition_type` (`type`),
  CONSTRAINT `FK18AE94936AD271F9` FOREIGN KEY (`sales_man`) REFERENCES `party` (`id`),
  CONSTRAINT `FK18AE9493AFA8053A` FOREIGN KEY (`market_type`) REFERENCES `market_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `marketing_information`
--


/*!40000 ALTER TABLE `marketing_information` DISABLE KEYS */;
LOCK TABLES `marketing_information` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `marketing_information` ENABLE KEYS */;

--
-- Table structure for table `model_process`
--

DROP TABLE IF EXISTS `model_process`;
CREATE TABLE `model_process` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` text,
  `name` varchar(255) NOT NULL,
  `path` text NOT NULL,
  `script` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `model_process`
--


/*!40000 ALTER TABLE `model_process` DISABLE KEYS */;
LOCK TABLES `model_process` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `model_process` ENABLE KEYS */;

--
-- Table structure for table `model_project_entity`
--

DROP TABLE IF EXISTS `model_project_entity`;
CREATE TABLE `model_project_entity` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` text,
  `name` varchar(255) NOT NULL,
  `path` text NOT NULL,
  `script` mediumtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `model_project_entity`
--


/*!40000 ALTER TABLE `model_project_entity` DISABLE KEYS */;
LOCK TABLES `model_project_entity` WRITE;
INSERT INTO `model_project_entity` VALUES (1,'Domain specific language for Claim','ClaimLanguage','ClaimLanguage.dsl','[when]For a Claims payment component=tavant.twms.domain.payment.PaymentComponent()\n[when]- with claimed amount greater than {amount}=a : claimedAmount -> ( a.isGreaterThan(com.domainlanguage.money.Money.dollars({amount})))\n[when]- with claimed amount less than {amount}=a : claimedAmount -> ( a.isLessThan(com.domainlanguage.money.Money.dollars({amount})))\n[when]- with claim category code \"{code}\"=c : forCategory -> ( c.getCode().equals(\"{code}\") )\n[when]For a Claim with unspecified labor rate=tavant.twms.domain.claim.LaborDetail( l : laborRate -> (l==null || l.isZero()) )\n[when]For a Claims travel details with invalid travel charges=tavant.twms.domain.claim.TravelDetail(dc : distanceCharge -> (dc == null || dc.isZero())) or tavant.twms.domain.claim.TravelDetail(trc : tripCharge -> (trc == null || trc.isZero())) or tavant.twms.domain.claim.TravelDetail(tic : timeCharge ->(tic == null || tic.isZero()))\n[when]For a Claim with travel hours greater than {hours}=tavant.twms.domain.claim.TravelDetail(h : hours -> ( h.intValue() > {hours}  ) )\n[when]For a Claim with more than {n} jobs performed=tavant.twms.domain.claim.Claim(si : serviceInformation -> (si.getServiceDetail().getLaborPerformed().size() > {n}))\n[when]For a Claims part replaced with invalid price=tavant.twms.domain.claim.PartReplaced( p : pricePerUnit ->(p==null || p.isZero()))\n[when]For a Claim=tavant.twms.domain.claim.Claim()\n[when]- declare filed on dates and repair dates=$filedOnDate : filedOnDate, $repairDate : repairDate\n[when]- with no service manager request on the claim=serviceManagerRequest==false\n[when]Filed date {n} days after repaired date=eval($filedOnDate.isAfter ($repairDate.plusDays({n})))\n[then]Accept=resultMap.put(\"accepted\", Boolean.TRUE);\n[then]MarkForManualReview=String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\"); if(  \"rejected\".equals(actionIdentifiedByAnotherRule) || \"on hold\".equals(actionIdentifiedByAnotherRule) ) { }  else {  resultMap.put(\"claimState\",\"manual review\");}\n');
UNLOCK TABLES;
/*!40000 ALTER TABLE `model_project_entity` ENABLE KEYS */;

--
-- Table structure for table `model_rule`
--

DROP TABLE IF EXISTS `model_rule`;
CREATE TABLE `model_rule` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` text,
  `name` varchar(255) NOT NULL,
  `path` text,
  `script` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `model_rule`
--


/*!40000 ALTER TABLE `model_rule` DISABLE KEYS */;
LOCK TABLES `model_rule` WRITE;
INSERT INTO `model_rule` VALUES (1,'validates the claim','ClaimValidationRule','ClaimValidationRule.drl','#created on: Jun 22, 2006\npackage twms.rules.claimprocess\n\nimport java.util.Map;\nimport com.domainlanguage.timeutil.Clock;\n\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.LaborDetail;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.claim.PartReplaced;\nimport tavant.twms.domain.watchlist.WatchListService;\n\nglobal Map resultMap;\nglobal WatchListService watchListService;\n\nrule \"Initialize result map\"\n salience 100\n when\n  eval(true);\n then\n  resultMap.put(\"accepted\",Boolean.TRUE);\n  resultMap.put(\"otherComments\",\"SomeCommentsFromRule\");\nend \n\nrule \"Failure Date can\'t be a future date\"\n when\n  claim : Claim(fd : failureDate -> (fd.isAfter(Clock.today())));\n then\n  resultMap.put(\"accepted\",Boolean.FALSE);\nend\n\nrule \"Repair date can\'t be before failure date\"\n when\n  claim : Claim(fd : failureDate, rd : repairDate)\n  eval(fd.isAfter(rd))\n then\n  resultMap.put(\"accepted\",Boolean.FALSE);  \nend\n \n\n\nrule \"Check if the travel details are valid\"\n when\n  $travelDetail : TravelDetail(speed > 100)\n  $serviceDetail : ServiceDetail(travelDetails == $travelDetail)\n  $serviceInformation : ServiceInformation(serviceDetail == $serviceDetail)\n  claim : Claim(serviceInformation == $serviceInformation)\n then\n  resultMap.put(\"accepted\",Boolean.FALSE);\nend \n\nrule \"Check if the labor performed is valid\"\n when\n  $laborDetail : LaborDetail(hoursSpent > 0 , jobPerformed == null)\n  $serviceDetail : ServiceDetail(laborPerformed contains $laborDetail)\n  $serviceInformation : ServiceInformation(serviceDetail == $serviceDetail)\n  claim : Claim(serviceInformation == $serviceInformation) \n then\n  resultMap.put(\"accepted\",Boolean.FALSE);\nend'),(2,'assigns a claim to a particular processor','ProcessorAssignment','ProcessorAssignment.drl','#created on: Aug 21, 2006\npackage tavant.twms.assignment\n\nimport tavant.twms.domain.orgmodel.Attribute;\nimport tavant.twms.domain.orgmodel.Dealership;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.orgmodel.UserGroup;\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.orgmodel.Address;\nimport tavant.twms.domain.catalog.Item;\nimport tavant.twms.domain.catalog.ItemGroup;\nimport tavant.twms.ruleexecutor.AssignableUsers;\n\nglobal AssignableUsers assignableUsers;\n\nrule \"Find the UserGroup specific to the Dealer\'s country\"\n when\n  $dealer_address : Address( $dealer_country : country )\n  $country_attr : Attribute( name == \"Country\", value == $dealer_country )\n  $processor_attr : Attribute( name == \"Group Type\", value == \"processor\")\n  $country_specific_group: UserGroup( attrs contains $country_attr )   \n  $country_specific_group: UserGroup( attrs contains $processor_attr )  \n then\n     assignableUsers.retainUsersOfGroup($country_specific_group);  \nend\n\nrule \"Find the UserGroup specific to a product type\"\n when\n  $item_group : ItemGroup( $product_type : name )\n  $product_type_attr : Attribute( name == \"Product type\", value == $product_type )\n  $processor_attr : Attribute( name == \"Group Type\", value == \"processor\")\n  $product_type_specific_group: UserGroup( attrs contains $product_type_attr )  \n  $product_type_specific_group: UserGroup( attrs contains $processor_attr )   \n then\n     assignableUsers.retainUsersOfGroup($product_type_specific_group);  \nend\n\nrule \"Find the UserGroup specific to a claim type\"\n when\n  $claim : Claim( $claim_type : type )\n  $claim_type_attr : Attribute( name == \"Claim Type\", value == $claim_type )\n  $processor_attr : Attribute( name == \"Group Type\", value == \"processor\")\n  $claim_type_specific_group : UserGroup( attrs contains $claim_type_attr ) \n  $claim_type_specific_group : UserGroup( attrs contains $processor_attr )    \n then\n     assignableUsers.retainUsersOfGroup($claim_type_specific_group);  \nend\n\nrule \"Remove Processors who are on vacation\"\n    salience -900 \n when\n  $unavailable_attr : Attribute( name == \"Available\", value == \"false\" )\n  $unavailable_user : User( attrs contains $unavailable_attr )\n then\n  assignableUsers.removeUser($unavailable_user);\nend\n\n# Not the right way. Need to figure this out later.\nrule \"Default Processor in the event no processors have been found\"\n    salience -1000\n when     \n     $default_attr : Attribute( name == \"Default\", value == \"true\" )\n     $default_user : User( attrs contains $default_attr )                 \n then\n     assignableUsers.addDefaultUser($default_user);  \nend\n\n\n\n\n\n\n\n\n\n\n\n\n\n'),(3,'assigns a claim to a particular dsm','DsmAssignment','DsmAssignment.drl','#created on: Sep 1, 2006\npackage tavant.twms.assignment\n\nimport tavant.twms.domain.orgmodel.Attribute;\nimport tavant.twms.domain.orgmodel.Dealership;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.orgmodel.UserGroup;\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.orgmodel.Address;\nimport tavant.twms.domain.catalog.Item;\nimport tavant.twms.domain.catalog.ItemGroup;\nimport tavant.twms.ruleexecutor.AssignableUsers;\n\nglobal AssignableUsers assignableUsers;\n\nrule \"Find the UserGroup specific to the Dealer\'s country\"\n when\n  $dealer_address : Address( $dealer_country : country )\n  $country_attr : Attribute( name == \"Country\", value == $dealer_country )\n  $processor_attr : Attribute( name == \"Group Type\", value == \"dsm\")\n  $country_specific_group: UserGroup( attrs contains $country_attr )   \n  $country_specific_group: UserGroup( attrs contains $processor_attr )  \n then\n     assignableUsers.retainUsersOfGroup($country_specific_group);  \nend\n\nrule \"Find the UserGroup specific to a product type\"\n when\n  $item_group : ItemGroup( $product_type : name )\n  $product_type_attr : Attribute( name == \"Product type\", value == $product_type )\n  $processor_attr : Attribute( name == \"Group Type\", value == \"dsm\")\n  $product_type_specific_group: UserGroup( attrs contains $product_type_attr )  \n  $product_type_specific_group: UserGroup( attrs contains $processor_attr )   \n then\n     assignableUsers.retainUsersOfGroup($product_type_specific_group);  \nend\n\nrule \"Find the UserGroup specific to a claim type\"\n when\n  $claim : Claim( $claim_type : type )\n  $claim_type_attr : Attribute( name == \"Claim Type\", value == $claim_type )\n  $processor_attr : Attribute( name == \"Group Type\", value == \"dsm\")\n  $claim_type_specific_group : UserGroup( attrs contains $claim_type_attr ) \n  $claim_type_specific_group : UserGroup( attrs contains $processor_attr )    \n then\n     assignableUsers.retainUsersOfGroup($claim_type_specific_group);  \nend\n\nrule \"Remove DSMs who are on vacation\"\n    salience -900 \n when\n  $unavailable_attr : Attribute( name == \"Available\", value == \"false\" )\n  $unavailable_user : User( attrs contains $unavailable_attr )\n then\n  assignableUsers.removeUser($unavailable_user);\nend\n\n# Not the right way. Need to figure this out later.\nrule \"Default Processor in the event no processors have been found\"\n    salience -1000\n when     \n     $default_attr : Attribute( name == \"Default\", value == \"true\" )\n     $default_user : User( attrs contains $default_attr )                 \n then\n     assignableUsers.addDefaultUser($default_user);  \nend'),(4,'Validates a claim for payment calculation','ClaimValidationRules','ClaimValidation.drl','#created on: Aug 30, 2006\npackage twms.rules.claimvalidation\n\nrule \"Price information for one of the replaced parts is not available.\"\n when\n  $part : OEMPartReplaced($pricePerUnit:pricePerUnit -> ( $pricePerUnit==null || $pricePerUnit.isZero() ))\n  $sd  : ServiceDetail( OEMPartsReplaced contains $part )\n  $si  : ServiceInformation( serviceDetail==$sd )\n  $claim  : Claim( serviceInformation==$si )  \n then\n  warn(resultMap,\"Price information for one or more replaced parts is not available.\");\nend\n\nrule \"The labor details are not valid (Job codes are specified without labor hours)\"\n when\n  $labor : LaborDetail( $hoursSpent: hoursSpent -> ( $hoursSpent != null &&  $hoursSpent.intValue() <= 0 ) )\n  $sd  : ServiceDetail( laborPerformed contains $labor )\n  $si  : ServiceInformation( serviceDetail==$sd )\n  $claim  : Claim( serviceInformation==$si )\n then\n  warn(resultMap,\"Job codes are specified without any labor hours.\");\nend\n\nrule \"The labor details are not valid (Labor hours specified  without Job codes)\"\n when\n  $labor : LaborDetail( $hoursSpent: hoursSpent -> ( $hoursSpent != null &&  $hoursSpent.intValue() > 0), \n      $jobPerformed : jobPerformed==null )\n  $sd  : ServiceDetail( laborPerformed contains $labor )\n  $si  : ServiceInformation( serviceDetail==$sd )\n  $claim  : Claim( serviceInformation==$si )  \n then\n  warn(resultMap,\"Labor hours are specified without any accompanying job codes.\");\nend\n\nrule \"Total claim amount is zero.\"\n when\n  $payment: Payment( $totalAmount : totalAmount -> ( $totalAmount==null || $totalAmount.isZero() ))\n  $claim  : Claim( payment==$payment )  \n then\n  error(resultMap,\"The total claim amount is zero.\");\nend\n\nrule \"Inventory item (for serial number specified) is not under warranty.\"\n when\n  $inv_type : InventoryType( type == \"RETAIL\" )\n  $item : InventoryItem( type == $inv_type)\n  $claim  : Claim( forItem == $item, applicablePolicy == null )\n then\n  warn(resultMap,\"The machine is out of warranty.\");\nend\n\nrule \"Serivce Hours of stock invetory item is greater than 50.\"\n when\n  $inv_type : InventoryType( type == \"STOCK\" )\n  $item : InventoryItem(type == $inv_type)\n  $claim  : Claim( forItem == $item, $hoursInService : hoursInService  -> ( $hoursInService.intValue() > 50 ))  \n then\n  error(resultMap,\"The machine is in stock and has been under operation for more than 50 hours.\");\nend\n\nrule \"Claim not filed promptly (within 5 days of repair) - submit it for SM review\"\n when\n  $claim  : Claim( $filedOnDate : filedOnDate!=null, $repairDate : repairDate!=null, serviceManagerRequest==false)\n  eval( $filedOnDate.isAfter( $repairDate.plusDays(5) ) )\n then\n  warn(resultMap,\"Claim was not filed within 5 days of repair and is also not approved by service manager\");\nend\n\nrule \"Failure Date can\'t be a future date\"\n when\n  $claim : Claim($fd : failureDate -> ($fd.isAfter(Clock.today())))\n then\n  error(resultMap,\"Failure Date can\'t be a future date\");\nend\n\nrule \"Repair Date can\'t be a future date\"\n when\n  $claim : Claim($rd : repairDate -> ($rd.isAfter(Clock.today())))\n then\n  error(resultMap,\"Repair Date can\'t be a future date\");\nend\n\nrule \"Repair date can\'t be before failure date\"\n when\n  $claim : Claim($fd : failureDate, $rd : repairDate)\n  eval($fd.isAfter($rd))\n then\n    error(resultMap,\"Repair date can\'t be before failure date\");\nend\n\nrule \"Travel hours is more than 8\"\n when\n  $travel : TravelDetail( $hours : hours  -> ( $hours.intValue() > 8 ) )  \n then\n  error(resultMap,\"You cannot claim more than 8 hour for travel on a claim\");\nend\n\n## Technicalities\n\nimport java.util.Map;\nimport java.util.Set;\nimport java.util.HashSet;\nimport com.domainlanguage.timeutil.Clock;\nimport com.domainlanguage.time.CalendarDate;\nimport com.domainlanguage.money.Money;\n\nimport tavant.twms.domain.inventory.InventoryItem;\nimport tavant.twms.domain.inventory.InventoryType;\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.LaborDetail;\nimport tavant.twms.domain.claim.OEMPartReplaced;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.claim.payment.Payment;\nimport tavant.twms.domain.claim.payment.CostCategory;\nimport tavant.twms.domain.claim.payment.PaymentComponent;\nimport tavant.twms.domain.watchlist.WatchListService;\nimport tavant.twms.web.ValidationMessages;\n\nglobal Map resultMap;\n\nfunction void warn(Map resultMap,String warningMessage) {\n ValidationMessages messages = (ValidationMessages)resultMap.get(\"validationMessages\");\n if( messages == null ) {\n  messages = new ValidationMessages();\n  resultMap.put(\"validationMessages\",messages);\n }\n messages.addWarningMessage(warningMessage);\n}\n\nfunction void error(Map resultMap,String errorMessage) {\n ValidationMessages messages = (ValidationMessages)resultMap.get(\"validationMessages\");\n if( messages == null ) {\n  messages = new ValidationMessages();\n  resultMap.put(\"validationMessages\",messages);  \n } \n messages.addErrorMessage(errorMessage);\n}'),(5,'Verifies if administrative data required for payment calculation is defined','SetupChecks','SetupChecks.drl','#created on: Sep 14, 2006\npackage twms.rules.claimprocess.setupchecks\nexpander ClaimLanguage.dsl\n\nrule \"The labor rate is not setup.\"\n when\n  For a Claim with unspecified labor rate\n then\n  MarkForManualReview\nend\n\nrule \"The travel rate is not setup.\"\n when\n  For a Claims travel details with invalid travel charges\n then\n  MarkForManualReview\nend\n\nrule \"The price for one or more items is not setup.\"\n when\n  For a Claims part replaced with invalid price \n then\n  MarkForManualReview\nend\n\n\nimport java.util.Map;\n#list any import classes here.\n\nglobal Map resultMap;\nglobal tavant.twms.domain.watchlist.WatchListService watchListService;\n#declare any global variables here'),(6,'Checks if any user input is incomplete.','IncompleteInformationChecks','IncompleteInformationChecks.drl','#created on: Aug 29, 2006\npackage twms.rules.claimprocess.incompleteinfochecks;\n\nrule \"Failure date is after repair date\"\n when\n  $claim : Claim($fd : failureDate, $rd : repairDate)  \n  eval( $fd.isAfter($rd) )\n then\n  markForManualReview(resultMap);\nend\n\nrule \"The claim is entered for a non-serialized item or a part\"\n when\n  $claim : Claim(forItem==null)  \n then\n  markForManualReview(resultMap);\nend\n\nrule \"Labor details are not supplied completely. One out of job codes and labor hours is supplied and the other is not.\"\n when\n  $laborDetail : LaborDetail(\n   $jobPerformed : jobPerformed, \n   $hoursSpent : hoursSpent\n   )\n  eval( \n   ($jobPerformed == null && $hoursSpent.intValue() > 0 ) ||\n   ($jobPerformed != null && $hoursSpent.intValue() == 0 ) \n  )\n then\n  markForManualReview(resultMap);\nend\n\nrule \"Travel details are not supplied completely. Trips have been specified without specifying the distance or hours.\"\n when\n  $travelDetail : TravelDetail(\n   $distance : distance,\n   $trips : trips,\n   $hours : hours\n   )\n  eval( \n   ( $trips.intValue() > 0 && \n     ( $distance.intValue() == 0 || $hours.intValue()==0 )\n   )\n  )\n then\n  markForManualReview(resultMap);\nend\n\nimport java.util.Map;\nimport java.util.Set;\nimport java.util.HashSet;\nimport com.domainlanguage.timeutil.Clock;\nimport com.domainlanguage.time.CalendarDate;\nimport com.domainlanguage.money.Money;\n\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.LaborDetail;\nimport tavant.twms.domain.claim.PartReplaced;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.watchlist.WatchListService;\n\n\nglobal Map resultMap;\nglobal WatchListService watchListService;\n\nfunction void reject(Map resultMap) {\n resultMap.put(\"claimState\",\"rejected\");\n}\n\nfunction void putOnHold(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ) {\n  //If already rejected then don\'t put it on hold.\n }  else {\n  resultMap.put(\"claimState\",\"on hold\");\n }\n}\n\nfunction void markForManualReview(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ||\n  \"on hold\".equals(actionIdentifiedByAnotherRule) ) {\n  //If rejected or put on hold, then no need for manual review.\n }  else {\n  resultMap.put(\"claimState\",\"manual review\");\n }\n}'),(7,'Makes claim validity checks.','ClaimValidityChecks','ClaimValidityChecks.drl','#created on: Aug 30, 2006\npackage twms.rules.claimprocess.validitychecks\nexpander ClaimLanguage.dsl;\nrule \"More than 10 hours of travel is claimed.\"\n when\n  For a Claim with travel hours greater than 10\n then\n  MarkForManualReview\nend\n\nrule \"More than 10 job codes have been claimed\"\n when\n  For a Claim with more than 10 jobs performed\n then\n  MarkForManualReview\nend\n\nrule \"Claim was not filed prompty within 5 days of repair and SMR review was also not requested\"\n when\n  For a Claim\n  - declare filed on dates and repair dates\n  - with no service manager request on the claim\n  Filed date 5 days after repaired date\n then\n  MarkForManualReview\nend\n\nimport java.util.Map;\n#list any import classes here.\n\nglobal Map resultMap;\nglobal tavant.twms.domain.watchlist.WatchListService watchListService;\n#declare any global variables here'),(8,'Checks if item or dealer or part is under watch list.','ReviewChecks','ReviewChecks.drl','#created on: Aug 30, 2006\npackage twms.rules.claimprocess.reviewchecks;\n\nrule \"One or more of the parts replaced are under watch.\"\n when\n  $oemPartReplaced : OEMPartReplaced( $itemReference : itemReference )\n  eval( watchListService.isPartInWatchList( $itemReference.getUnserializedItem() ) )\n then\n  markForManualReview(resultMap);\nend\n\nrule \"The dealer is under watch.\"\n when\n  $dealer : Dealership()\n  $claim : Claim( forDealer==$dealer )  \n  eval( watchListService.isDealerInWatchList( $dealer ) )\n then\n  markForManualReview(resultMap);\nend\n\nimport java.util.Map;\nimport java.util.Set;\nimport java.util.HashSet;\nimport com.domainlanguage.timeutil.Clock;\nimport com.domainlanguage.time.CalendarDate;\nimport com.domainlanguage.money.Money;\n\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.LaborDetail;\nimport tavant.twms.domain.claim.PartReplaced;\nimport tavant.twms.domain.claim.OEMPartReplaced;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.orgmodel.Dealership;\nimport tavant.twms.domain.watchlist.WatchListService;\n\n\nglobal Map resultMap;\nglobal WatchListService watchListService;\n\nfunction void reject(Map resultMap) {\n resultMap.put(\"claimState\",\"rejected\");\n}\n\nfunction void putOnHold(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ) {\n  //If already rejected then don\'t put it on hold.\n }  else {\n  resultMap.put(\"claimState\",\"on hold\");\n }\n}\n\nfunction void markForManualReview(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ||\n  \"on hold\".equals(actionIdentifiedByAnotherRule) ) {\n  //If rejected or put on hold, then no need for manual review.\n }  else {\n  resultMap.put(\"claimState\",\"manual review\");\n }\n}'),(9,'Checks if costs claimed are high.','HighValueClaimChecks','HighValueClaimChecks.drl','#created on: Jan 17, 2007\npackage twms.rules.claimprocess.highvalueclaimchecks\n\nrule \"OEM Parts replaced amount claimed is more than $1,000\" \n when\n     $oem_cost_cat : CostCategory( code == \"OEM_PARTS\" )\n     $pay_comp : PaymentComponent( forCategory == $oem_cost_cat, $claimedAmount : claimedAmount )\n     eval( $claimedAmount.isGreaterThan(Money.dollars(1000)) )\n  #conditions\n then \n  #actions\n  markForManualReview(resultMap);  \nend\n\nrule \"Labor amount claimed is more than $250\" \n when\n     $labor_cost_cat : CostCategory( code == \"LABOR\" )\n     $pay_comp : PaymentComponent( forCategory == $labor_cost_cat, $claimedAmount : claimedAmount )\n     eval( $claimedAmount.isGreaterThan(Money.dollars(250)) )\n  #conditions\n then \n  #actions\n  markForManualReview(resultMap);  \nend\n\nrule \"Travel amount claimed is more than $150\" \n when\n     $labor_cost_cat : CostCategory( code == \"TRAVEL\" )\n     $pay_comp : PaymentComponent( forCategory == $labor_cost_cat, $claimedAmount : claimedAmount )\n     eval( $claimedAmount.isGreaterThan(Money.dollars(150)) )\n  #conditions\n then \n  #actions\n  markForManualReview(resultMap);  \nend\n\n\n#list any import classes here.\nimport tavant.twms.domain.claim.payment.PaymentComponent;\nimport tavant.twms.domain.claim.payment.CostCategory;\nimport com.domainlanguage.money.Money;\nimport java.util.Map;\n\n#declare any global variables here\nglobal Map resultMap;\nglobal tavant.twms.domain.watchlist.WatchListService watchListService;\nfunction void reject(Map resultMap) {\n    resultMap.put(\"claimState\",\"rejected\");\n}\n\nfunction void putOnHold(Map resultMap) {\n    String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n    if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ) {\n    //If already rejected then don\'t put it on hold.\n    } else {\n        resultMap.put(\"claimState\",\"on hold\");\n    }\n}\n\nfunction void markForManualReview(Map resultMap) {\n    String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n    if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ||\n           \"on hold\".equals(actionIdentifiedByAnotherRule) ) {\n    //If rejected or put on hold, then no need for manual review.\n    } else {\n        resultMap.put(\"claimState\",\"manual review\");\n    }\n}\n'),(10,'Checks if the item is under warranty','ClaimUnderWarrantyValidityChecks','ClaimUnderWarrantyValidityChecks.drl','#created on: Aug 30, 2006\npackage twms.rules.claimprocess.warrantychecks;\n\nrule \"Repair was performed more than 30 days after failure date\"\n when\n  $claim : Claim( $repairDate : repairDate, $failureDate : failureDate )\n  eval( $repairDate.isAfter( $failureDate.plusDays(30) ) )\n then\n  markForManualReview(resultMap);\nend\n\nimport java.util.Map;\nimport java.util.Set;\nimport java.util.HashSet;\nimport com.domainlanguage.timeutil.Clock;\nimport com.domainlanguage.time.CalendarDate;\nimport com.domainlanguage.money.Money;\n\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.LaborDetail;\nimport tavant.twms.domain.claim.PartReplaced;\nimport tavant.twms.domain.claim.OEMPartReplaced;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.orgmodel.Dealership;\nimport tavant.twms.domain.watchlist.WatchListService;\n\n\nglobal Map resultMap;\nglobal WatchListService watchListService;\n\nfunction void reject(Map resultMap) {\n resultMap.put(\"claimState\",\"rejected\");\n}\n\nfunction void putOnHold(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ) {\n  //If already rejected then don\'t put it on hold.\n }  else {\n  resultMap.put(\"claimState\",\"on hold\");\n }\n}\n\nfunction void markForManualReview(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ||\n  \"on hold\".equals(actionIdentifiedByAnotherRule) ) {\n  //If rejected or put on hold, then no need for manual review.\n }  else {\n  resultMap.put(\"claimState\",\"manual review\");\n }\n}'),(11,'Checks to be made if inventory item is retailed.','RetailedInventoryItemChecks','RetailedInventoryItemChecks.drl','#created on: Aug 30, 2006\npackage twms.rules.claimprocess.retailedinventorychecks;\n\nrule \"The machine is out of warranty\"\n when\n  Claim( applicablePolicy==null )\n then\n  markForManualReview(resultMap);\nend\n\n\n\nimport java.util.Map;\nimport java.util.Set;\nimport java.util.HashSet;\nimport com.domainlanguage.timeutil.Clock;\nimport com.domainlanguage.time.CalendarDate;\nimport com.domainlanguage.money.Money;\n\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.LaborDetail;\nimport tavant.twms.domain.claim.PartReplaced;\nimport tavant.twms.domain.claim.OEMPartReplaced;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.orgmodel.Dealership;\nimport tavant.twms.domain.watchlist.WatchListService;\n\n\nglobal Map resultMap;\nglobal WatchListService watchListService;\n\nfunction void reject(Map resultMap) {\n resultMap.put(\"claimState\",\"rejected\");\n}\n\nfunction void putOnHold(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ) {\n  //If already rejected then don\'t put it on hold.\n }  else {\n  resultMap.put(\"claimState\",\"on hold\");\n }\n}\n\nfunction void markForManualReview(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ||\n  \"on hold\".equals(actionIdentifiedByAnotherRule) ) {\n  //If rejected or put on hold, then no need for manual review.\n }  else {\n  resultMap.put(\"claimState\",\"manual review\");\n }\n}'),(12,'Checks to be mode if the inventory item is still on stock.','StockedInventoryItemChecks','StockedInventoryItemChecks.drl','#created on: Aug 30, 2006\npackage twms.rules.claimprocess.stockedinventorychecks;\n\nrule \"The machine is in stock, has been under operation for more than 50 hours and the claim was not requested for SM review.\"\n when\n  Claim( hoursInService > 50 , serviceManagerRequest==false)\n then\n  markForManualReview(resultMap);\nend\n\n\n## Technicalities\nimport java.util.Map;\nimport java.util.Set;\nimport java.util.HashSet;\nimport com.domainlanguage.timeutil.Clock;\nimport com.domainlanguage.time.CalendarDate;\nimport com.domainlanguage.money.Money;\n\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.LaborDetail;\nimport tavant.twms.domain.claim.PartReplaced;\nimport tavant.twms.domain.claim.OEMPartReplaced;\nimport tavant.twms.domain.orgmodel.User;\nimport tavant.twms.domain.orgmodel.Dealership;\nimport tavant.twms.domain.watchlist.WatchListService;\n\nglobal Map resultMap;\nglobal WatchListService watchListService;\n\n\nfunction void reject(Map resultMap) {\n resultMap.put(\"claimState\",\"rejected\");\n}\n\nfunction void putOnHold(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ) {\n  //If already rejected then don\'t put it on hold.\n }  else {\n  resultMap.put(\"claimState\",\"on hold\");\n }\n}\n\nfunction void markForManualReview(Map resultMap) {\n String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ||\n  \"on hold\".equals(actionIdentifiedByAnotherRule) ) {\n  //If rejected or put on hold, then no need for manual review.\n }  else {\n  resultMap.put(\"claimState\",\"manual review\");\n }\n}'),(13,'Check for Duplicate claims','ClaimDuplicityChecks','ClaimDuplicityChecks.drl','#created on: Oct 11, 2006\npackage twms.rules.claimprocess.claimduplicitycheck\n\n\nrule \"Possible duplicate claim due to travel claimed for same machine on same date.\" \n when\n     $travelDetails1 : TravelDetail( hours > 0 )\n     $serviceDetails1 : ServiceDetail( travelDetails == $travelDetails1)\n     $serviceInformation1 : ServiceInformation( serviceDetail == $serviceDetails1 )\n     $inv : InventoryItem()\n  $claim1 : Claim( forItem == $inv, serviceInformation == $serviceInformation1 , $repairDate1 : repairDate )\n  $travelDetails2 : TravelDetail( hours > 0 )\n     $serviceDetails2 : ServiceDetail( travelDetails == $travelDetails2)\n     $serviceInformation2 : ServiceInformation( serviceDetail == $serviceDetails2 )  \n  $claim2 : Claim( forItem == $inv, repairDate ==  $repairDate1, serviceInformation == $serviceInformation2 )\n then \n  markForManualReview(resultMap);  \nend\n\n#list any import classes here.\nimport java.util.Map;\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.inventory.InventoryItem;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\n\n#declare any global variables here\nglobal Map resultMap;\n\nfunction void markForManualReview(Map resultMap) {\n    String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n    if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ||\n            \"on hold\".equals(actionIdentifiedByAnotherRule) ) {\n        //If rejected or put on hold, then no need for manual review.\n    }  else {\n        resultMap.put(\"claimState\",\"manual review\");\n    }\n}'),(14,'Check for Duplicate claims which are either of Machine or Attachement types','ClaimUnderWarrantyDuplicityChecks','ClaimUnderWarrantyDuplicityChecks.drl','#created on: Oct 11, 2006\npackage twms.rules.claimprocess.claimduplicitycheck\n\n\nrule \"Possible duplicate claim due to same causal part supplied for same machine\" \n when     \n     $serviceInformation1 : ServiceInformation( $causalPart : causalPart )\n     $inv : InventoryItem()\n  $claim1 : Claim( forItem == $inv, serviceInformation == $serviceInformation1)  \n     $serviceInformation2 : ServiceInformation( causalPart == $causalPart )  \n  $claim2 : Claim( forItem == $inv, serviceInformation == $serviceInformation2 )\n then \n  markForManualReview(resultMap);  \nend\n\nrule \"Possible duplicate claim due to same fault code supplied for same machine\" \n when     \n     $serviceInformation1 : ServiceInformation( $faultCode : faultCode )\n     $inv : InventoryItem( )\n  $claim1 : Claim( forItem == $inv, serviceInformation == $serviceInformation1)  \n     $serviceInformation2 : ServiceInformation( faultCode == $faultCode )  \n  $claim2 : Claim( forItem == $inv, serviceInformation == $serviceInformation2 )\n then \n  markForManualReview(resultMap);  \nend\n\n\n#list any import classes here.\nimport java.util.Map;\nimport tavant.twms.domain.claim.Claim;\nimport tavant.twms.domain.inventory.InventoryItem;\nimport tavant.twms.domain.claim.TravelDetail;\nimport tavant.twms.domain.claim.ServiceDetail;\nimport tavant.twms.domain.claim.ServiceInformation;\n\n#declare any global variables here\nglobal Map resultMap;\n\nfunction void markForManualReview(Map resultMap) {\n    String actionIdentifiedByAnotherRule = (String)resultMap.get(\"claimState\");\n    if(  \"rejected\".equals(actionIdentifiedByAnotherRule) ||\n            \"on hold\".equals(actionIdentifiedByAnotherRule) ) {\n        //If rejected or put on hold, then no need for manual review.\n    }  else {\n        resultMap.put(\"claimState\",\"manual review\");\n    }\n}');
UNLOCK TABLES;
/*!40000 ALTER TABLE `model_rule` ENABLE KEYS */;

--
-- Table structure for table `non_oem_part_replaced`
--

DROP TABLE IF EXISTS `non_oem_part_replaced`;
CREATE TABLE `non_oem_part_replaced` (
  `id` bigint(20) NOT NULL,
  `number_of_units` int(11) default NULL,
  `price_per_unit_amt` decimal(19,2) default NULL,
  `price_per_unit_curr` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `number` varchar(255) default NULL,
  `invoice` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK64AC692262D5FC60` (`invoice`),
  CONSTRAINT `FK64AC692262D5FC60` FOREIGN KEY (`invoice`) REFERENCES `document` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `non_oem_part_replaced`
--


/*!40000 ALTER TABLE `non_oem_part_replaced` DISABLE KEYS */;
LOCK TABLES `non_oem_part_replaced` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `non_oem_part_replaced` ENABLE KEYS */;

--
-- Table structure for table `non_oem_part_to_replace`
--

DROP TABLE IF EXISTS `non_oem_part_to_replace`;
CREATE TABLE `non_oem_part_to_replace` (
  `id` bigint(20) NOT NULL auto_increment,
  `no_of_units` int(11) NOT NULL,
  `description` varchar(255) default NULL,
  `part_number` varchar(255) NOT NULL,
  `price_per_unit_amt` decimal(19,2) default NULL,
  `price_per_unit_curr` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `non_oem_part_to_replace`
--


/*!40000 ALTER TABLE `non_oem_part_to_replace` DISABLE KEYS */;
LOCK TABLES `non_oem_part_to_replace` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `non_oem_part_to_replace` ENABLE KEYS */;

--
-- Table structure for table `oem_part_replaced`
--

DROP TABLE IF EXISTS `oem_part_replaced`;
CREATE TABLE `oem_part_replaced` (
  `id` bigint(20) NOT NULL,
  `number_of_units` int(11) default NULL,
  `price_per_unit_amt` decimal(19,2) default NULL,
  `price_per_unit_curr` varchar(255) default NULL,
  `item_reference_serialized` bit(1) default NULL,
  `part_to_be_returned` bit(1) NOT NULL,
  `supplier_shipment` bigint(20) default NULL,
  `shipment` bigint(20) default NULL,
  `part_return` bigint(20) default NULL,
  `item_reference_unserialized_item` bigint(20) default NULL,
  `supplier_part_return` bigint(20) default NULL,
  `item_reference_referred_item` bigint(20) default NULL,
  `item_reference_referred_inventory_item` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK3A6E0E145EDDCB2A` (`shipment`),
  KEY `FK3A6E0E1468A5D3EC` (`item_reference_referred_inventory_item`),
  KEY `FK3A6E0E143B57BC7E` (`item_reference_referred_item`),
  KEY `FK3A6E0E144701F4A5` (`supplier_part_return`),
  KEY `FK3A6E0E146C2C0F32` (`item_reference_unserialized_item`),
  KEY `FK3A6E0E142755483D` (`supplier_shipment`),
  KEY `FK3A6E0E143DCE5C75` (`part_return`),
  CONSTRAINT `FK3A6E0E143DCE5C75` FOREIGN KEY (`part_return`) REFERENCES `part_return` (`id`),
  CONSTRAINT `FK3A6E0E142755483D` FOREIGN KEY (`supplier_shipment`) REFERENCES `shipment` (`id`),
  CONSTRAINT `FK3A6E0E143B57BC7E` FOREIGN KEY (`item_reference_referred_item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK3A6E0E144701F4A5` FOREIGN KEY (`supplier_part_return`) REFERENCES `supplier_part_return` (`id`),
  CONSTRAINT `FK3A6E0E145EDDCB2A` FOREIGN KEY (`shipment`) REFERENCES `shipment` (`id`),
  CONSTRAINT `FK3A6E0E1468A5D3EC` FOREIGN KEY (`item_reference_referred_inventory_item`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FK3A6E0E146C2C0F32` FOREIGN KEY (`item_reference_unserialized_item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `oem_part_replaced`
--


/*!40000 ALTER TABLE `oem_part_replaced` DISABLE KEYS */;
LOCK TABLES `oem_part_replaced` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `oem_part_replaced` ENABLE KEYS */;

--
-- Table structure for table `oem_part_to_replace`
--

DROP TABLE IF EXISTS `oem_part_to_replace`;
CREATE TABLE `oem_part_to_replace` (
  `id` bigint(20) NOT NULL auto_increment,
  `no_of_units` int(11) NOT NULL,
  `item` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK69613DD458AA7430` (`item`),
  CONSTRAINT `FK69613DD458AA7430` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `oem_part_to_replace`
--


/*!40000 ALTER TABLE `oem_part_to_replace` DISABLE KEYS */;
LOCK TABLES `oem_part_to_replace` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `oem_part_to_replace` ENABLE KEYS */;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK4644ED336FC6C76D` (`id`),
  CONSTRAINT `FK4644ED336FC6C76D` FOREIGN KEY (`id`) REFERENCES `party` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `organization`
--


/*!40000 ALTER TABLE `organization` DISABLE KEYS */;
LOCK TABLES `organization` WRITE;
INSERT INTO `organization` VALUES (1),(7),(8),(10),(20),(21),(22),(23),(24),(25),(31),(34),(35),(36);
UNLOCK TABLES;
/*!40000 ALTER TABLE `organization` ENABLE KEYS */;

--
-- Table structure for table `ownership_state`
--

DROP TABLE IF EXISTS `ownership_state`;
CREATE TABLE `ownership_state` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ownership_state`
--


/*!40000 ALTER TABLE `ownership_state` DISABLE KEYS */;
LOCK TABLES `ownership_state` WRITE;
INSERT INTO `ownership_state` VALUES (1,'First Owner'),(2,'Preowned'),(3,'Both');
UNLOCK TABLES;
/*!40000 ALTER TABLE `ownership_state` ENABLE KEYS */;

--
-- Table structure for table `part_return`
--

DROP TABLE IF EXISTS `part_return`;
CREATE TABLE `part_return` (
  `id` bigint(20) NOT NULL,
  `action_taken` varchar(255) default NULL,
  `warehouse_location` varchar(255) default NULL,
  `inspection_result` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK36220EFC77CBD9EF` (`inspection_result`),
  KEY `FK36220EFC14608145` (`id`),
  CONSTRAINT `FK36220EFC14608145` FOREIGN KEY (`id`) REFERENCES `base_part_return` (`id`),
  CONSTRAINT `FK36220EFC77CBD9EF` FOREIGN KEY (`inspection_result`) REFERENCES `inspection_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `part_return`
--


/*!40000 ALTER TABLE `part_return` DISABLE KEYS */;
LOCK TABLES `part_return` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `part_return` ENABLE KEYS */;

--
-- Table structure for table `part_return_configuration`
--

DROP TABLE IF EXISTS `part_return_configuration`;
CREATE TABLE `part_return_configuration` (
  `id` bigint(20) NOT NULL auto_increment,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `value_causal_part` bit(1) NOT NULL,
  `value_due_days` int(11) NOT NULL,
  `part_return_definition` bigint(20) default NULL,
  `value_payment_condition` varchar(255) NOT NULL,
  `value_return_location` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8AEF6CF3FA557E7F` (`value_payment_condition`),
  KEY `FK8AEF6CF33721D6C1` (`value_return_location`),
  KEY `FK8AEF6CF313D32222` (`part_return_definition`),
  CONSTRAINT `FK8AEF6CF313D32222` FOREIGN KEY (`part_return_definition`) REFERENCES `part_return_definition` (`id`),
  CONSTRAINT `FK8AEF6CF33721D6C1` FOREIGN KEY (`value_return_location`) REFERENCES `location` (`id`),
  CONSTRAINT `FK8AEF6CF3FA557E7F` FOREIGN KEY (`value_payment_condition`) REFERENCES `payment_condition` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `part_return_configuration`
--


/*!40000 ALTER TABLE `part_return_configuration` DISABLE KEYS */;
LOCK TABLES `part_return_configuration` WRITE;
INSERT INTO `part_return_configuration` VALUES (1,'2001-01-01','2010-01-01','\0',5,1,'PAY_ON_RETURN',1),(6,'2001-01-01','2010-01-01','\0',6,6,'PAY_ON_INSPECTION',2),(16,'2001-01-01','2010-01-01','\0',6,16,'PAY_ON_INSPECTION',3);
UNLOCK TABLES;
/*!40000 ALTER TABLE `part_return_configuration` ENABLE KEYS */;

--
-- Table structure for table `part_return_definition`
--

DROP TABLE IF EXISTS `part_return_definition`;
CREATE TABLE `part_return_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `claim_type` varchar(255) default NULL,
  `relevance_score` bigint(20) default NULL,
  `warranty_type` varchar(255) default NULL,
  `for_criteria_product_type` bigint(20) default NULL,
  `item_criterion_item_group` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer_group` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer` bigint(20) default NULL,
  `item_criterion_item` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK4EEF8676DE6125BA` (`item_criterion_item`),
  KEY `FK4EEF86761BC64DD6` (`for_criteria_product_type`),
  KEY `FK4EEF8676BDF2C045` (`for_criteria_dealer_criterion_dealer_group`),
  KEY `FK4EEF8676721C13BF` (`item_criterion_item_group`),
  KEY `FK4EEF86763BC5B29C` (`for_criteria_dealer_criterion_dealer`),
  CONSTRAINT `FK4EEF86763BC5B29C` FOREIGN KEY (`for_criteria_dealer_criterion_dealer`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FK4EEF86761BC64DD6` FOREIGN KEY (`for_criteria_product_type`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FK4EEF8676721C13BF` FOREIGN KEY (`item_criterion_item_group`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FK4EEF8676BDF2C045` FOREIGN KEY (`for_criteria_dealer_criterion_dealer_group`) REFERENCES `dealer_group` (`id`),
  CONSTRAINT `FK4EEF8676DE6125BA` FOREIGN KEY (`item_criterion_item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `part_return_definition`
--


/*!40000 ALTER TABLE `part_return_definition` DISABLE KEYS */;
LOCK TABLES `part_return_definition` WRITE;
INSERT INTO `part_return_definition` VALUES (1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(6,NULL,NULL,NULL,NULL,NULL,NULL,NULL,6),(16,NULL,NULL,NULL,NULL,NULL,NULL,NULL,16);
UNLOCK TABLES;
/*!40000 ALTER TABLE `part_return_definition` ENABLE KEYS */;

--
-- Table structure for table `party`
--

DROP TABLE IF EXISTS `party`;
CREATE TABLE `party` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `address` bigint(20) default NULL,
  `is_part_of_organization` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK6581AE63F73AC54` (`address`),
  KEY `FK6581AE68F55A8B` (`is_part_of_organization`),
  CONSTRAINT `FK6581AE68F55A8B` FOREIGN KEY (`is_part_of_organization`) REFERENCES `organization` (`id`),
  CONSTRAINT `FK6581AE63F73AC54` FOREIGN KEY (`address`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `party`
--


/*!40000 ALTER TABLE `party` DISABLE KEYS */;
LOCK TABLES `party` WRITE;
INSERT INTO `party` VALUES (1,'dealer',1,NULL),(2,'dealer2',2,NULL),(3,'processor2',3,NULL),(4,'admin',4,NULL),(5,'processor3',5,NULL),(6,'processor4',6,NULL),(7,'A-L-L EQUIPMENT',7,NULL),(8,'OEM',8,NULL),(9,'jack',9,NULL),(10,'AIRDYNE INC',7,NULL),(11,'processor',10,NULL),(12,'alonso',2,NULL),(13,'inspector',10,NULL),(14,'dsm2',4,NULL),(15,'dsm',8,NULL),(16,'dsm3',5,NULL),(17,'receiver',10,NULL),(18,'dsm4',9,NULL),(19,'processor5',10,NULL),(20,'Dealer 1',11,NULL),(21,'Dealer 2',12,NULL),(22,'Dealer 3',13,NULL),(23,'Dealer 4',14,NULL),(24,'Dealer 5',15,NULL),(25,'Dealer 6',16,NULL),(26,'Customer 1',17,NULL),(27,'Customer 2',18,NULL),(28,'Customer 3',19,NULL),(29,'Customer 4',20,NULL),(30,'Customer 5',21,NULL),(31,'Northwind Supplies',16,NULL),(32,'northwind',14,NULL),(33,'sra',10,NULL),(34,'TruckDrove Supplies',14,NULL),(35,'Backyard Supplies',13,NULL),(36,'ASG Supplies',12,NULL),(37,'partshipper',12,NULL),(38,'truckdrove',15,NULL),(39,'backyard',16,NULL),(40,'asg',17,NULL),(41,'ankenyreceiver',17,NULL),(42,'ankenyinspector',17,NULL),(43,'ankenypartshipper',17,NULL),(44,'milfordreceiver',17,NULL),(45,'milfordinspector',17,NULL),(46,'milfordpartshipper',17,NULL),(47,'kansasreceiver',17,NULL),(48,'kansasinspector',17,NULL),(49,'kansaspartshipper',17,NULL),(50,'receiver1',17,NULL),(51,'receiver2',NULL,NULL),(52,'inspector1',NULL,NULL),(53,'inspector2',NULL,NULL),(54,'partshipper1',NULL,NULL),(55,'partshipper2',NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `party` ENABLE KEYS */;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint(20) NOT NULL,
  `claimed_amount_amt` decimal(19,2) default NULL,
  `claimed_amount_curr` varchar(255) default NULL,
  `total_amount_amt` decimal(19,2) default NULL,
  `total_amount_curr` varchar(255) default NULL,
  `for_claim` bigint(20) NOT NULL,
  `credit_memo` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `for_claim` (`for_claim`),
  KEY `FKD11C3206637F9842` (`credit_memo`),
  KEY `FKD11C32062295C95B` (`for_claim`),
  CONSTRAINT `FKD11C32062295C95B` FOREIGN KEY (`for_claim`) REFERENCES `claim` (`id`),
  CONSTRAINT `FKD11C3206637F9842` FOREIGN KEY (`credit_memo`) REFERENCES `credit_memo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment`
--


/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
LOCK TABLES `payment` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;

--
-- Table structure for table `payment_component`
--

DROP TABLE IF EXISTS `payment_component`;
CREATE TABLE `payment_component` (
  `id` bigint(20) NOT NULL,
  `claimed_amount_amt` decimal(19,2) default NULL,
  `claimed_amount_curr` varchar(255) default NULL,
  `for_category` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK9A31FA04A6AE298E` (`for_category`),
  CONSTRAINT `FK9A31FA04A6AE298E` FOREIGN KEY (`for_category`) REFERENCES `cost_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_component`
--


/*!40000 ALTER TABLE `payment_component` DISABLE KEYS */;
LOCK TABLES `payment_component` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_component` ENABLE KEYS */;

--
-- Table structure for table `payment_condition`
--

DROP TABLE IF EXISTS `payment_condition`;
CREATE TABLE `payment_condition` (
  `code` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_condition`
--


/*!40000 ALTER TABLE `payment_condition` DISABLE KEYS */;
LOCK TABLES `payment_condition` WRITE;
INSERT INTO `payment_condition` VALUES ('PAY','Pay without Part Return'),('PAY_ON_INSPECTION','Pay on Part Inspection'),('PAY_ON_RETURN','Pay on Part Return');
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_condition` ENABLE KEYS */;

--
-- Table structure for table `payment_definition`
--

DROP TABLE IF EXISTS `payment_definition`;
CREATE TABLE `payment_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date default NULL,
  `till_date` date default NULL,
  `criteria` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8A41F92C831D2EBC` (`criteria`),
  CONSTRAINT `FK8A41F92C831D2EBC` FOREIGN KEY (`criteria`) REFERENCES `policy_criteria` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_definition`
--


/*!40000 ALTER TABLE `payment_definition` DISABLE KEYS */;
LOCK TABLES `payment_definition` WRITE;
INSERT INTO `payment_definition` VALUES (2,'2000-01-01','2006-12-31',1),(3,'2007-01-01','2010-12-31',1),(4,'2007-01-01','2010-12-31',2);
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_definition` ENABLE KEYS */;

--
-- Table structure for table `payment_modifier`
--

DROP TABLE IF EXISTS `payment_modifier`;
CREATE TABLE `payment_modifier` (
  `id` bigint(20) NOT NULL auto_increment,
  `claim_type` varchar(255) default NULL,
  `relevance_score` bigint(20) default NULL,
  `warranty_type` varchar(255) default NULL,
  `for_payment_variable` bigint(20) NOT NULL,
  `for_criteria_product_type` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer_group` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKA0E9E8F01BC64DD6` (`for_criteria_product_type`),
  KEY `FKA0E9E8F0D258164F` (`for_payment_variable`),
  KEY `FKA0E9E8F0BDF2C045` (`for_criteria_dealer_criterion_dealer_group`),
  KEY `FKA0E9E8F03BC5B29C` (`for_criteria_dealer_criterion_dealer`),
  CONSTRAINT `FKA0E9E8F03BC5B29C` FOREIGN KEY (`for_criteria_dealer_criterion_dealer`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FKA0E9E8F01BC64DD6` FOREIGN KEY (`for_criteria_product_type`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FKA0E9E8F0BDF2C045` FOREIGN KEY (`for_criteria_dealer_criterion_dealer_group`) REFERENCES `dealer_group` (`id`),
  CONSTRAINT `FKA0E9E8F0D258164F` FOREIGN KEY (`for_payment_variable`) REFERENCES `payment_variable` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_modifier`
--


/*!40000 ALTER TABLE `payment_modifier` DISABLE KEYS */;
LOCK TABLES `payment_modifier` WRITE;
INSERT INTO `payment_modifier` VALUES (1,NULL,0,NULL,1,NULL,NULL,NULL),(2,NULL,1,NULL,1,5,NULL,NULL),(3,NULL,2,'STANDARD',1,NULL,NULL,NULL),(4,NULL,3,'STANDARD',1,5,NULL,NULL),(5,'Machine',4,NULL,1,NULL,NULL,NULL),(6,'Machine',5,NULL,1,5,NULL,NULL),(7,'Machine',6,'STANDARD',1,NULL,NULL,NULL),(8,'Machine',7,'STANDARD',1,5,NULL,NULL),(9,NULL,16,NULL,1,NULL,7,NULL),(10,NULL,17,NULL,1,5,7,NULL),(11,NULL,18,'STANDARD',1,NULL,7,NULL),(12,NULL,19,'STANDARD',1,5,7,NULL),(13,'Machine',20,NULL,1,NULL,7,NULL),(14,'Machine',21,NULL,1,5,7,NULL),(15,'Machine',22,'STANDARD',1,NULL,7,NULL),(16,'Machine',23,'STANDARD',1,5,7,NULL),(17,NULL,0,NULL,2,NULL,NULL,NULL),(18,NULL,0,NULL,3,NULL,NULL,NULL),(19,NULL,0,NULL,4,NULL,NULL,NULL),(20,NULL,0,NULL,5,NULL,NULL,NULL),(21,NULL,0,NULL,6,NULL,NULL,NULL),(22,NULL,0,NULL,7,NULL,NULL,NULL),(23,NULL,0,NULL,8,NULL,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_modifier` ENABLE KEYS */;

--
-- Table structure for table `payment_payment_components`
--

DROP TABLE IF EXISTS `payment_payment_components`;
CREATE TABLE `payment_payment_components` (
  `payment` bigint(20) NOT NULL,
  `payment_components` bigint(20) NOT NULL,
  PRIMARY KEY  (`payment`,`payment_components`),
  UNIQUE KEY `payment_components` (`payment_components`),
  KEY `FK95521C8272DE8BD` (`payment`),
  KEY `FK95521C894096AF5` (`payment_components`),
  CONSTRAINT `FK95521C894096AF5` FOREIGN KEY (`payment_components`) REFERENCES `payment_component` (`id`),
  CONSTRAINT `FK95521C8272DE8BD` FOREIGN KEY (`payment`) REFERENCES `payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_payment_components`
--


/*!40000 ALTER TABLE `payment_payment_components` DISABLE KEYS */;
LOCK TABLES `payment_payment_components` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_payment_components` ENABLE KEYS */;

--
-- Table structure for table `payment_section`
--

DROP TABLE IF EXISTS `payment_section`;
CREATE TABLE `payment_section` (
  `id` bigint(20) NOT NULL auto_increment,
  `section` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKCE63F1AC1D816E9E` (`section`),
  CONSTRAINT `FKCE63F1AC1D816E9E` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_section`
--


/*!40000 ALTER TABLE `payment_section` DISABLE KEYS */;
LOCK TABLES `payment_section` WRITE;
INSERT INTO `payment_section` VALUES (1,1),(6,1),(11,1),(22,1),(2,2),(7,2),(12,2),(23,2),(3,3),(8,3),(13,3),(24,3),(4,4),(9,4),(14,4),(25,4),(5,5),(10,5),(15,5),(26,5),(16,6),(18,6),(27,6),(17,7),(19,7),(28,7),(20,8),(21,8),(29,8);
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_section` ENABLE KEYS */;

--
-- Table structure for table `payment_section_var_levels`
--

DROP TABLE IF EXISTS `payment_section_var_levels`;
CREATE TABLE `payment_section_var_levels` (
  `payment_section` bigint(20) NOT NULL,
  `payment_variable_levels` bigint(20) NOT NULL,
  UNIQUE KEY `payment_variable_levels` (`payment_variable_levels`),
  KEY `FK7834E1FA85B35C57` (`payment_section`),
  KEY `FK7834E1FAC4AEBE07` (`payment_variable_levels`),
  CONSTRAINT `FK7834E1FAC4AEBE07` FOREIGN KEY (`payment_variable_levels`) REFERENCES `payment_variable_level` (`id`),
  CONSTRAINT `FK7834E1FA85B35C57` FOREIGN KEY (`payment_section`) REFERENCES `payment_section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_section_var_levels`
--


/*!40000 ALTER TABLE `payment_section_var_levels` DISABLE KEYS */;
LOCK TABLES `payment_section_var_levels` WRITE;
INSERT INTO `payment_section_var_levels` VALUES (6,9),(11,2),(12,3),(13,4),(14,5),(15,6),(18,7),(19,8),(20,10),(21,1),(22,11),(23,12),(29,13);
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_section_var_levels` ENABLE KEYS */;

--
-- Table structure for table `payment_variable`
--

DROP TABLE IF EXISTS `payment_variable`;
CREATE TABLE `payment_variable` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `section` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK7B1EB8951D816E9E` (`section`),
  CONSTRAINT `FK7B1EB8951D816E9E` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_variable`
--


/*!40000 ALTER TABLE `payment_variable` DISABLE KEYS */;
LOCK TABLES `payment_variable` WRITE;
INSERT INTO `payment_variable` VALUES (1,'Claim Bonus',8),(2,'OEM Parts Discount',1),(3,'Non OEM Parts Discount',2),(4,'Labor Discount',3),(5,'Travel Discount',4),(6,'Item Freight Duty Discount',5),(7,'Meals Discount',6),(8,'Parking Discount',7);
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_variable` ENABLE KEYS */;

--
-- Table structure for table `payment_variable_level`
--

DROP TABLE IF EXISTS `payment_variable_level`;
CREATE TABLE `payment_variable_level` (
  `id` bigint(20) NOT NULL auto_increment,
  `level` int(11) default NULL,
  `payment_variable` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK1973335AE2FC3599` (`payment_variable`),
  CONSTRAINT `FK1973335AE2FC3599` FOREIGN KEY (`payment_variable`) REFERENCES `payment_variable` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment_variable_level`
--


/*!40000 ALTER TABLE `payment_variable_level` DISABLE KEYS */;
LOCK TABLES `payment_variable_level` WRITE;
INSERT INTO `payment_variable_level` VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,1,5),(6,1,6),(7,1,7),(8,1,8),(9,1,2),(10,1,1),(11,2,2),(12,2,3),(13,2,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `payment_variable_level` ENABLE KEYS */;

--
-- Table structure for table `policy`
--

DROP TABLE IF EXISTS `policy`;
CREATE TABLE `policy` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date default NULL,
  `till_date` date default NULL,
  `policy_definition` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKC56DA532183553AC` (`policy_definition`),
  CONSTRAINT `FKC56DA532183553AC` FOREIGN KEY (`policy_definition`) REFERENCES `policy_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `policy`
--


/*!40000 ALTER TABLE `policy` DISABLE KEYS */;
LOCK TABLES `policy` WRITE;
INSERT INTO `policy` VALUES (1,'2004-01-22','2006-01-21',114450),(2,'2004-11-07','2007-11-06',114600),(3,'2004-11-07','2008-11-06',114800),(4,'2004-11-07','2009-11-06',114850),(5,'2004-05-27','2006-05-26',114450),(6,'2004-05-27','2007-05-26',114600),(7,'2004-06-30','2008-06-29',114800),(8,'2004-11-07','2009-11-06',114850),(9,'2005-01-20','2007-01-19',114900),(10,'2005-11-22','2008-11-21',115000),(11,'2005-11-22','2009-11-21',115100),(12,'2005-03-14','2010-03-13',115200),(13,'2005-09-27','2007-09-26',114450),(14,'2005-09-13','2008-09-12',114600),(15,'2005-10-25','2009-10-24',114800),(16,'2005-10-28','2010-10-27',114850),(17,'2005-02-20','2007-02-19',114450),(18,'2005-11-07','2008-11-06',114600),(19,'2005-02-28','2009-02-27',114800),(20,'2005-06-20','2010-06-19',114850),(21,'2005-01-31','2006-01-30',114800),(22,'2005-04-06','2007-04-05',114850);
UNLOCK TABLES;
/*!40000 ALTER TABLE `policy` ENABLE KEYS */;

--
-- Table structure for table `policy_category`
--

DROP TABLE IF EXISTS `policy_category`;
CREATE TABLE `policy_category` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK19DC016BD9BE9114` (`id`),
  CONSTRAINT `FK19DC016BD9BE9114` FOREIGN KEY (`id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `policy_category`
--


/*!40000 ALTER TABLE `policy_category` DISABLE KEYS */;
LOCK TABLES `policy_category` WRITE;
INSERT INTO `policy_category` VALUES (3),(4),(5);
UNLOCK TABLES;
/*!40000 ALTER TABLE `policy_category` ENABLE KEYS */;

--
-- Table structure for table `policy_category_policy`
--

DROP TABLE IF EXISTS `policy_category_policy`;
CREATE TABLE `policy_category_policy` (
  `policy_category` bigint(20) NOT NULL,
  `policy` bigint(20) NOT NULL,
  PRIMARY KEY  (`policy_category`,`policy`),
  KEY `FK9CC4286547FE65E` (`policy`),
  KEY `FK9CC428618530435` (`policy_category`),
  CONSTRAINT `FK9CC428618530435` FOREIGN KEY (`policy_category`) REFERENCES `policy_category` (`id`),
  CONSTRAINT `FK9CC4286547FE65E` FOREIGN KEY (`policy`) REFERENCES `policy_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `policy_category_policy`
--


/*!40000 ALTER TABLE `policy_category_policy` DISABLE KEYS */;
LOCK TABLES `policy_category_policy` WRITE;
INSERT INTO `policy_category_policy` VALUES (5,114450),(5,114600),(5,114800),(5,114850);
UNLOCK TABLES;
/*!40000 ALTER TABLE `policy_category_policy` ENABLE KEYS */;

--
-- Table structure for table `policy_criteria`
--

DROP TABLE IF EXISTS `policy_criteria`;
CREATE TABLE `policy_criteria` (
  `id` bigint(20) NOT NULL auto_increment,
  `claim_type` varchar(255) default NULL,
  `relevance_score` bigint(20) NOT NULL,
  `warranty_type` varchar(255) default NULL,
  `dealer_criterion_dealer_group` bigint(20) default NULL,
  `policy_definition` bigint(20) default NULL,
  `policy_category` bigint(20) default NULL,
  `product_type` bigint(20) default NULL,
  `dealer_criterion_dealer` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8B3323CC887886C6` (`dealer_criterion_dealer`),
  KEY `FK8B3323CC93FC4CEF` (`dealer_criterion_dealer_group`),
  KEY `FK8B3323CC18530435` (`policy_category`),
  KEY `FK8B3323CC183553AC` (`policy_definition`),
  KEY `FK8B3323CC8B4E6FEC` (`product_type`),
  CONSTRAINT `FK8B3323CC8B4E6FEC` FOREIGN KEY (`product_type`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FK8B3323CC183553AC` FOREIGN KEY (`policy_definition`) REFERENCES `policy_definition` (`id`),
  CONSTRAINT `FK8B3323CC18530435` FOREIGN KEY (`policy_category`) REFERENCES `policy_category` (`id`),
  CONSTRAINT `FK8B3323CC887886C6` FOREIGN KEY (`dealer_criterion_dealer`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FK8B3323CC93FC4CEF` FOREIGN KEY (`dealer_criterion_dealer_group`) REFERENCES `dealer_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `policy_criteria`
--


/*!40000 ALTER TABLE `policy_criteria` DISABLE KEYS */;
LOCK TABLES `policy_criteria` WRITE;
INSERT INTO `policy_criteria` VALUES (1,NULL,1,NULL,NULL,NULL,3,NULL,NULL),(2,NULL,2,NULL,NULL,NULL,5,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `policy_criteria` ENABLE KEYS */;

--
-- Table structure for table `policy_definition`
--

DROP TABLE IF EXISTS `policy_definition`;
CREATE TABLE `policy_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `active_from` date NOT NULL,
  `active_till` date NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `currency` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `comments` text,
  `months_frm_delivery` int(11) default NULL,
  `months_frm_shipment` int(11) default NULL,
  `service_hrs_covered` int(11) default NULL,
  `currently_inactive` bit(1) NOT NULL,
  `description` varchar(255) default NULL,
  `opt_lock_version` bigint(20) default NULL,
  `priority` int(11) NOT NULL,
  `transfer_fee_amt` decimal(19,2) default NULL,
  `transfer_fee_curr` varchar(255) default NULL,
  `transferable` bit(1) default NULL,
  `availability_item_condition` varchar(255) default NULL,
  `availability_ownership_state` bigint(20) default NULL,
  `warranty_type` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_policy_defn_code` (`code`),
  UNIQUE KEY `unique_priority` (`priority`),
  KEY `FK892312806D560606` (`availability_ownership_state`),
  KEY `FK89231280A586B346` (`availability_item_condition`),
  KEY `FK89231280B6F6277A` (`warranty_type`),
  CONSTRAINT `FK892312806D560606` FOREIGN KEY (`availability_ownership_state`) REFERENCES `ownership_state` (`id`),
  CONSTRAINT `FK89231280A586B346` FOREIGN KEY (`availability_item_condition`) REFERENCES `inventory_item_condition` (`item_condition`),
  CONSTRAINT `FK89231280B6F6277A` FOREIGN KEY (`warranty_type`) REFERENCES `warranty_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `policy_definition`
--


/*!40000 ALTER TABLE `policy_definition` DISABLE KEYS */;
LOCK TABLES `policy_definition` WRITE;
INSERT INTO `policy_definition` VALUES (114450,'2004-10-10','2010-01-01','100.00','USD','STD 24 / 10000',NULL,24,26,10000,'\0','STD 24 / 10000',1,1,'20.00','USD','',NULL,1,'STANDARD'),(114600,'2004-10-10','2010-01-01','100.00','USD','STD 36 / 15000',NULL,36,38,15000,'\0','STD 36 / 15000',1,2,'20.00','USD','\0',NULL,3,'STANDARD'),(114800,'2004-10-10','2010-01-01','100.00','USD','STD 48 / 20000',NULL,48,50,20000,'\0','STD 48 / 20000',1,3,'20.00','USD','',NULL,3,'STANDARD'),(114850,'2004-10-10','2010-01-01','100.00','USD','STD 60 / 30000',NULL,60,62,30000,'\0','STD 60 / 30000',1,4,'20.00','USD','\0',NULL,3,'STANDARD'),(114900,'2004-10-10','2010-01-01','100.00','USD','STD SPECIAL 24 / 12500',NULL,24,26,12500,'\0','STD SPECIAL 24 / 12500',1,5,'20.00','USD','\0',NULL,2,'STANDARD'),(115000,'2004-10-10','2010-01-01','100.00','USD','STD SPECIAL 36 / 17500',NULL,36,38,17500,'\0','STD SPECIAL 36 / 17500',1,6,'20.00','USD','\0',NULL,3,'STANDARD'),(115100,'2004-10-10','2010-01-01','100.00','USD','STD SPECIAL 48 / 22500',NULL,48,50,22500,'\0','STD SPECIAL 48 / 22500',1,7,'20.00','USD','',NULL,3,'STANDARD'),(115200,'2004-10-10','2010-01-01','100.00','USD','STD SPECIAL 60 / 32500',NULL,60,62,32500,'\0','STD SPECIAL 60 / 32500',1,8,'20.00','USD','',NULL,2,'STANDARD'),(115300,'2004-10-10','2010-01-01','100.00','USD','STD WITH GOODWILL 24 / 11000',NULL,25,26,11000,'\0','STD WITH GOODWILL 24 / 11000',1,9,'20.00','USD','',NULL,1,'STANDARD');
UNLOCK TABLES;
/*!40000 ALTER TABLE `policy_definition` ENABLE KEYS */;

--
-- Table structure for table `policy_definition_applicability_terms`
--

DROP TABLE IF EXISTS `policy_definition_applicability_terms`;
CREATE TABLE `policy_definition_applicability_terms` (
  `policy_definition` bigint(20) NOT NULL,
  `applicability_terms` bigint(20) NOT NULL,
  PRIMARY KEY  (`policy_definition`,`applicability_terms`),
  UNIQUE KEY `applicability_terms` (`applicability_terms`),
  KEY `FK2F79F94EEA821F99` (`applicability_terms`),
  KEY `FK2F79F94E183553AC` (`policy_definition`),
  CONSTRAINT `FK2F79F94E183553AC` FOREIGN KEY (`policy_definition`) REFERENCES `policy_definition` (`id`),
  CONSTRAINT `FK2F79F94EEA821F99` FOREIGN KEY (`applicability_terms`) REFERENCES `domain_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `policy_definition_applicability_terms`
--


/*!40000 ALTER TABLE `policy_definition_applicability_terms` DISABLE KEYS */;
LOCK TABLES `policy_definition_applicability_terms` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `policy_definition_applicability_terms` ENABLE KEYS */;

--
-- Table structure for table `policy_for_products`
--

DROP TABLE IF EXISTS `policy_for_products`;
CREATE TABLE `policy_for_products` (
  `policy_defn` bigint(20) NOT NULL,
  `for_product` bigint(20) NOT NULL,
  PRIMARY KEY  (`policy_defn`,`for_product`),
  KEY `FKF8CE0E07101FC122` (`policy_defn`),
  KEY `FKF8CE0E07E635EDB` (`for_product`),
  CONSTRAINT `FKF8CE0E07E635EDB` FOREIGN KEY (`for_product`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FKF8CE0E07101FC122` FOREIGN KEY (`policy_defn`) REFERENCES `policy_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `policy_for_products`
--


/*!40000 ALTER TABLE `policy_for_products` DISABLE KEYS */;
LOCK TABLES `policy_for_products` WRITE;
INSERT INTO `policy_for_products` VALUES (114450,5),(114450,6),(114600,5),(114600,6),(114800,5),(114800,6),(114850,5),(114850,6),(114900,5),(114900,7),(115000,5),(115000,7),(115100,5),(115100,7),(115200,5),(115200,7),(115300,5),(115300,7);
UNLOCK TABLES;
/*!40000 ALTER TABLE `policy_for_products` ENABLE KEYS */;

--
-- Table structure for table `purpose`
--

DROP TABLE IF EXISTS `purpose`;
CREATE TABLE `purpose` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `purpose`
--


/*!40000 ALTER TABLE `purpose` DISABLE KEYS */;
LOCK TABLES `purpose` WRITE;
INSERT INTO `purpose` VALUES (1,'Item Pricing'),(2,'Part Returns'),(3,'Claim Payment Modifiers'),(4,'Item Return Watchlist'),(5,'Item Review Watchlist'),(6,'PRODUCT STRUCTURE'),(7,'Dealer Rates'),(8,'Organisation Hierarchy'),(9,'Claim Assignment');
UNLOCK TABLES;
/*!40000 ALTER TABLE `purpose` ENABLE KEYS */;

--
-- Table structure for table `recovery_formula`
--

DROP TABLE IF EXISTS `recovery_formula`;
CREATE TABLE `recovery_formula` (
  `id` bigint(20) NOT NULL auto_increment,
  `constant_amt` decimal(19,2) default NULL,
  `constant_curr` varchar(255) default NULL,
  `max_amount_amt` decimal(19,2) default NULL,
  `max_amount_curr` varchar(255) default NULL,
  `minimum_amount_amt` decimal(19,2) default NULL,
  `minimum_amount_curr` varchar(255) default NULL,
  `percentage_of_cost` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `recovery_formula`
--


/*!40000 ALTER TABLE `recovery_formula` DISABLE KEYS */;
LOCK TABLES `recovery_formula` WRITE;
INSERT INTO `recovery_formula` VALUES (1,'0.00','USD','10000.00','USD','0.00','USD',100),(2,'20.00','USD','500.00','USD','0.00','USD',50),(3,'20.00','USD','100.00','USD','0.00','USD',100),(4,'0.00','USD','100.00','USD','0.00','USD',75),(5,'0.00','USD','10000.00','USD','0.00','USD',100),(6,'20.00','USD','500.00','USD','0.00','USD',50),(7,'20.00','USD','100.00','USD','0.00','USD',100),(8,'0.00','USD','100.00','USD','0.00','USD',75);
UNLOCK TABLES;
/*!40000 ALTER TABLE `recovery_formula` ENABLE KEYS */;

--
-- Table structure for table `rejection_reason`
--

DROP TABLE IF EXISTS `rejection_reason`;
CREATE TABLE `rejection_reason` (
  `code` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `state` varchar(255) default NULL,
  PRIMARY KEY  (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `rejection_reason`
--


/*!40000 ALTER TABLE `rejection_reason` DISABLE KEYS */;
LOCK TABLES `rejection_reason` WRITE;
INSERT INTO `rejection_reason` VALUES ('R1','Wrong Part(s) Returned','active'),('R10','Repair Not Completed','active'),('R11','Product Quality Report Not Submitted','active'),('R12','Part(s) Returned Late','active'),('R13','Part(s) Not Returned','active'),('R14','Out of Warranty','active'),('R15','Not a Warranty Issue - Maintenance','active'),('R16','Not a Warranty Issue - Consumables','active'),('R17','Not Covered Under Extended Warranty Plan','active'),('R18','No Defect Found','active'),('R19','Lack of Maintenance','active'),('R2','Unauthorized Repair/Modification','active'),('R20','Lack of Information to Support Claim','active'),('R21','Filed Late','active'),('R22','Duplicate Claim','active'),('R23','Cancelled by Air Center/Distributor','active'),('R24','Appeal - Denied','active'),('R3','Startup/Commissioning Responsibility','active'),('R4','Special Warr Consid - Denied','active'),('R5','Site Related Failure','active'),('R6','Shipping Damage','active'),('R7','Serviceman Error','active'),('R8','Requested Information Not Received','active'),('R9','Repair Not Done by IR Technician','active');
UNLOCK TABLES;
/*!40000 ALTER TABLE `rejection_reason` ENABLE KEYS */;

--
-- Table structure for table `remote_interaction`
--

DROP TABLE IF EXISTS `remote_interaction`;
CREATE TABLE `remote_interaction` (
  `id` bigint(20) NOT NULL auto_increment,
  `bod_type` varchar(255) default NULL,
  `exception` text,
  `interaction_date` datetime default NULL,
  `payload` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `remote_interaction`
--


/*!40000 ALTER TABLE `remote_interaction` DISABLE KEYS */;
LOCK TABLES `remote_interaction` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `remote_interaction` ENABLE KEYS */;

--
-- Table structure for table `repeatable_transition`
--

DROP TABLE IF EXISTS `repeatable_transition`;
CREATE TABLE `repeatable_transition` (
  `fork_node` bigint(20) NOT NULL,
  `transition_name` varchar(255) default NULL,
  `repetition_criteria_class` varchar(255) default NULL,
  `input_expression` varchar(255) default NULL,
  `output_expression` varchar(255) default NULL,
  `list_index` int(11) NOT NULL,
  PRIMARY KEY  (`fork_node`,`list_index`),
  KEY `FK5306BA5FDC3270EE` (`fork_node`),
  CONSTRAINT `FK5306BA5FDC3270EE` FOREIGN KEY (`fork_node`) REFERENCES `jbpm_node` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `repeatable_transition`
--


/*!40000 ALTER TABLE `repeatable_transition` DISABLE KEYS */;
LOCK TABLES `repeatable_transition` WRITE;
INSERT INTO `repeatable_transition` VALUES (78,'ForkPaths','tavant.twms.jbpm.nodes.fork.PartsReturnBasedRepetitionCriteria','claim.serviceInformation.serviceDetail.oEMPartsReplaced','part',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `repeatable_transition` ENABLE KEYS */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `role`
--


/*!40000 ALTER TABLE `role` DISABLE KEYS */;
LOCK TABLES `role` WRITE;
INSERT INTO `role` VALUES (1,'dealer'),(2,'processor'),(3,'admin'),(4,'dsm'),(5,'receiver'),(6,'inspector'),(7,'supplier'),(8,'sra'),(9,'partshipper'),(10,'customer');
UNLOCK TABLES;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;

--
-- Table structure for table `rule_failure`
--

DROP TABLE IF EXISTS `rule_failure`;
CREATE TABLE `rule_failure` (
  `id` bigint(20) NOT NULL auto_increment,
  `failed_rule_set` varchar(255) default NULL,
  `recorded_date` datetime default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `rule_failure`
--


/*!40000 ALTER TABLE `rule_failure` DISABLE KEYS */;
LOCK TABLES `rule_failure` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `rule_failure` ENABLE KEYS */;

--
-- Table structure for table `rule_failure_failed_rules`
--

DROP TABLE IF EXISTS `rule_failure_failed_rules`;
CREATE TABLE `rule_failure_failed_rules` (
  `rule_failure` bigint(20) NOT NULL,
  `failed_rules_element` varchar(255) default NULL,
  KEY `FK3981E06DEFD64F4E` (`rule_failure`),
  CONSTRAINT `FK3981E06DEFD64F4E` FOREIGN KEY (`rule_failure`) REFERENCES `rule_failure` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `rule_failure_failed_rules`
--


/*!40000 ALTER TABLE `rule_failure_failed_rules` DISABLE KEYS */;
LOCK TABLES `rule_failure_failed_rules` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `rule_failure_failed_rules` ENABLE KEYS */;

--
-- Table structure for table `saved_criteria_search`
--

DROP TABLE IF EXISTS `saved_criteria_search`;
CREATE TABLE `saved_criteria_search` (
  `id` bigint(20) NOT NULL,
  `search_criteria_asxml` text,
  PRIMARY KEY  (`id`),
  KEY `FKC23320D0F084A239` (`id`),
  CONSTRAINT `FKC23320D0F084A239` FOREIGN KEY (`id`) REFERENCES `saved_search` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `saved_criteria_search`
--


/*!40000 ALTER TABLE `saved_criteria_search` DISABLE KEYS */;
LOCK TABLES `saved_criteria_search` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `saved_criteria_search` ENABLE KEYS */;

--
-- Table structure for table `saved_query_search`
--

DROP TABLE IF EXISTS `saved_query_search`;
CREATE TABLE `saved_query_search` (
  `id` bigint(20) NOT NULL,
  `search_query` text,
  PRIMARY KEY  (`id`),
  KEY `FK663067B7F084A239` (`id`),
  CONSTRAINT `FK663067B7F084A239` FOREIGN KEY (`id`) REFERENCES `saved_search` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `saved_query_search`
--


/*!40000 ALTER TABLE `saved_query_search` DISABLE KEYS */;
LOCK TABLES `saved_query_search` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `saved_query_search` ENABLE KEYS */;

--
-- Table structure for table `saved_search`
--

DROP TABLE IF EXISTS `saved_search`;
CREATE TABLE `saved_search` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` text,
  `name` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `saved_search`
--


/*!40000 ALTER TABLE `saved_search` DISABLE KEYS */;
LOCK TABLES `saved_search` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `saved_search` ENABLE KEYS */;

--
-- Table structure for table `section`
--

DROP TABLE IF EXISTS `section`;
CREATE TABLE `section` (
  `id` bigint(20) NOT NULL auto_increment,
  `display_position` int(11) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `section`
--


/*!40000 ALTER TABLE `section` DISABLE KEYS */;
LOCK TABLES `section` WRITE;
INSERT INTO `section` VALUES (1,0,'OEM Parts'),(2,1,'Non OEM Parts'),(3,2,'Labor'),(4,3,'Travel'),(5,4,'Item Freight Duty'),(6,5,'Meals'),(7,6,'Parking'),(8,7,'Claim Amount');
UNLOCK TABLES;
/*!40000 ALTER TABLE `section` ENABLE KEYS */;

--
-- Table structure for table `section_cost_categories`
--

DROP TABLE IF EXISTS `section_cost_categories`;
CREATE TABLE `section_cost_categories` (
  `section` bigint(20) NOT NULL,
  `cost_category` bigint(20) NOT NULL,
  `display_position` int(11) NOT NULL,
  PRIMARY KEY  (`section`,`display_position`),
  UNIQUE KEY `cost_category` (`cost_category`),
  KEY `FKE4D64741D816E9E` (`section`),
  KEY `FKE4D647464BE2B0A` (`cost_category`),
  CONSTRAINT `FKE4D647464BE2B0A` FOREIGN KEY (`cost_category`) REFERENCES `cost_category` (`id`),
  CONSTRAINT `FKE4D64741D816E9E` FOREIGN KEY (`section`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `section_cost_categories`
--


/*!40000 ALTER TABLE `section_cost_categories` DISABLE KEYS */;
LOCK TABLES `section_cost_categories` WRITE;
INSERT INTO `section_cost_categories` VALUES (1,1,0),(2,2,0),(3,3,0),(4,4,0),(5,5,0),(6,6,0),(7,7,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `section_cost_categories` ENABLE KEYS */;

--
-- Table structure for table `sections_in_pymt_defn`
--

DROP TABLE IF EXISTS `sections_in_pymt_defn`;
CREATE TABLE `sections_in_pymt_defn` (
  `pymt_defn` bigint(20) NOT NULL,
  `pymt_section` bigint(20) NOT NULL,
  `display_position` int(11) NOT NULL,
  PRIMARY KEY  (`pymt_defn`,`display_position`),
  UNIQUE KEY `pymt_section` (`pymt_section`),
  KEY `FK3226824FF5394A45` (`pymt_defn`),
  KEY `FK3226824FA966DDC1` (`pymt_section`),
  CONSTRAINT `FK3226824FA966DDC1` FOREIGN KEY (`pymt_section`) REFERENCES `payment_section` (`id`),
  CONSTRAINT `FK3226824FF5394A45` FOREIGN KEY (`pymt_defn`) REFERENCES `payment_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `sections_in_pymt_defn`
--


/*!40000 ALTER TABLE `sections_in_pymt_defn` DISABLE KEYS */;
LOCK TABLES `sections_in_pymt_defn` WRITE;
INSERT INTO `sections_in_pymt_defn` VALUES (2,6,0),(2,7,1),(2,8,2),(2,9,3),(2,10,4),(3,11,0),(3,12,1),(3,13,2),(3,14,3),(3,15,4),(2,16,5),(2,17,6),(3,18,5),(3,19,6),(2,20,7),(3,21,7),(4,22,0),(4,23,1),(4,24,2),(4,25,3),(4,26,4),(4,27,5),(4,28,6),(4,29,7);
UNLOCK TABLES;
/*!40000 ALTER TABLE `sections_in_pymt_defn` ENABLE KEYS */;

--
-- Table structure for table `seq_document`
--

DROP TABLE IF EXISTS `seq_document`;
CREATE TABLE `seq_document` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_document`
--


/*!40000 ALTER TABLE `seq_document` DISABLE KEYS */;
LOCK TABLES `seq_document` WRITE;
INSERT INTO `seq_document` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_document` ENABLE KEYS */;

--
-- Table structure for table `seq_inventoryitem`
--

DROP TABLE IF EXISTS `seq_inventoryitem`;
CREATE TABLE `seq_inventoryitem` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_inventoryitem`
--


/*!40000 ALTER TABLE `seq_inventoryitem` DISABLE KEYS */;
LOCK TABLES `seq_inventoryitem` WRITE;
INSERT INTO `seq_inventoryitem` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_inventoryitem` ENABLE KEYS */;

--
-- Table structure for table `seq_inventoryitemcomposition`
--

DROP TABLE IF EXISTS `seq_inventoryitemcomposition`;
CREATE TABLE `seq_inventoryitemcomposition` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_inventoryitemcomposition`
--


/*!40000 ALTER TABLE `seq_inventoryitemcomposition` DISABLE KEYS */;
LOCK TABLES `seq_inventoryitemcomposition` WRITE;
INSERT INTO `seq_inventoryitemcomposition` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_inventoryitemcomposition` ENABLE KEYS */;

--
-- Table structure for table `seq_itemcomposition`
--

DROP TABLE IF EXISTS `seq_itemcomposition`;
CREATE TABLE `seq_itemcomposition` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_itemcomposition`
--


/*!40000 ALTER TABLE `seq_itemcomposition` DISABLE KEYS */;
LOCK TABLES `seq_itemcomposition` WRITE;
INSERT INTO `seq_itemcomposition` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_itemcomposition` ENABLE KEYS */;

--
-- Table structure for table `seq_lineitem`
--

DROP TABLE IF EXISTS `seq_lineitem`;
CREATE TABLE `seq_lineitem` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_lineitem`
--


/*!40000 ALTER TABLE `seq_lineitem` DISABLE KEYS */;
LOCK TABLES `seq_lineitem` WRITE;
INSERT INTO `seq_lineitem` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_lineitem` ENABLE KEYS */;

--
-- Table structure for table `seq_lineitemgroup`
--

DROP TABLE IF EXISTS `seq_lineitemgroup`;
CREATE TABLE `seq_lineitemgroup` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_lineitemgroup`
--


/*!40000 ALTER TABLE `seq_lineitemgroup` DISABLE KEYS */;
LOCK TABLES `seq_lineitemgroup` WRITE;
INSERT INTO `seq_lineitemgroup` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_lineitemgroup` ENABLE KEYS */;

--
-- Table structure for table `seq_partreplaced`
--

DROP TABLE IF EXISTS `seq_partreplaced`;
CREATE TABLE `seq_partreplaced` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_partreplaced`
--


/*!40000 ALTER TABLE `seq_partreplaced` DISABLE KEYS */;
LOCK TABLES `seq_partreplaced` WRITE;
INSERT INTO `seq_partreplaced` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_partreplaced` ENABLE KEYS */;

--
-- Table structure for table `seq_payment`
--

DROP TABLE IF EXISTS `seq_payment`;
CREATE TABLE `seq_payment` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_payment`
--


/*!40000 ALTER TABLE `seq_payment` DISABLE KEYS */;
LOCK TABLES `seq_payment` WRITE;
INSERT INTO `seq_payment` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_payment` ENABLE KEYS */;

--
-- Table structure for table `seq_paymentcomponent`
--

DROP TABLE IF EXISTS `seq_paymentcomponent`;
CREATE TABLE `seq_paymentcomponent` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_paymentcomponent`
--


/*!40000 ALTER TABLE `seq_paymentcomponent` DISABLE KEYS */;
LOCK TABLES `seq_paymentcomponent` WRITE;
INSERT INTO `seq_paymentcomponent` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_paymentcomponent` ENABLE KEYS */;

--
-- Table structure for table `seq_serializeditemreplacement`
--

DROP TABLE IF EXISTS `seq_serializeditemreplacement`;
CREATE TABLE `seq_serializeditemreplacement` (
  `next_val` bigint(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seq_serializeditemreplacement`
--


/*!40000 ALTER TABLE `seq_serializeditemreplacement` DISABLE KEYS */;
LOCK TABLES `seq_serializeditemreplacement` WRITE;
INSERT INTO `seq_serializeditemreplacement` VALUES (200);
UNLOCK TABLES;
/*!40000 ALTER TABLE `seq_serializeditemreplacement` ENABLE KEYS */;

--
-- Table structure for table `serialized_item_replacement`
--

DROP TABLE IF EXISTS `serialized_item_replacement`;
CREATE TABLE `serialized_item_replacement` (
  `id` bigint(20) NOT NULL,
  `old_part` bigint(20) default NULL,
  `due_to_claim` bigint(20) default NULL,
  `new_part` bigint(20) default NULL,
  `for_composition` bigint(20) NOT NULL,
  `in_order` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKD6C54841DE58F83A` (`new_part`),
  KEY `FKD6C548419819B013` (`old_part`),
  KEY `FKD6C54841EC4DDB78` (`due_to_claim`),
  KEY `FKD6C5484125A3CAB6` (`for_composition`),
  CONSTRAINT `FKD6C5484125A3CAB6` FOREIGN KEY (`for_composition`) REFERENCES `inventory_item_composition` (`id`),
  CONSTRAINT `FKD6C548419819B013` FOREIGN KEY (`old_part`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FKD6C54841DE58F83A` FOREIGN KEY (`new_part`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FKD6C54841EC4DDB78` FOREIGN KEY (`due_to_claim`) REFERENCES `claim` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `serialized_item_replacement`
--


/*!40000 ALTER TABLE `serialized_item_replacement` DISABLE KEYS */;
LOCK TABLES `serialized_item_replacement` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `serialized_item_replacement` ENABLE KEYS */;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `id` bigint(20) NOT NULL auto_increment,
  `freight_duty_amt` decimal(19,2) default NULL,
  `freight_duty_curr` varchar(255) default NULL,
  `meals_amt` decimal(19,2) default NULL,
  `meals_curr` varchar(255) default NULL,
  `parking_and_toll_expense_amt` decimal(19,2) default NULL,
  `parking_and_toll_expense_curr` varchar(255) default NULL,
  `travel_details` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK7643C6B527D44C2F` (`travel_details`),
  CONSTRAINT `FK7643C6B527D44C2F` FOREIGN KEY (`travel_details`) REFERENCES `travel_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service`
--


/*!40000 ALTER TABLE `service` DISABLE KEYS */;
LOCK TABLES `service` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `service` ENABLE KEYS */;

--
-- Table structure for table `service_information`
--

DROP TABLE IF EXISTS `service_information`;
CREATE TABLE `service_information` (
  `id` bigint(20) NOT NULL auto_increment,
  `caused_by` varchar(255) default NULL,
  `fault_code` varchar(255) default NULL,
  `fault_found` varchar(255) default NULL,
  `service_detail` bigint(20) default NULL,
  `causal_part` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK73B132A2D29E935A` (`service_detail`),
  KEY `FK73B132A2B322CDA8` (`causal_part`),
  CONSTRAINT `FK73B132A2B322CDA8` FOREIGN KEY (`causal_part`) REFERENCES `item` (`id`),
  CONSTRAINT `FK73B132A2D29E935A` FOREIGN KEY (`service_detail`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service_information`
--


/*!40000 ALTER TABLE `service_information` DISABLE KEYS */;
LOCK TABLES `service_information` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `service_information` ENABLE KEYS */;

--
-- Table structure for table `service_labor_performed`
--

DROP TABLE IF EXISTS `service_labor_performed`;
CREATE TABLE `service_labor_performed` (
  `service` bigint(20) NOT NULL,
  `labor_performed` bigint(20) NOT NULL,
  UNIQUE KEY `labor_performed` (`labor_performed`),
  KEY `FKE2C2B0E7D3C210F4` (`service`),
  KEY `FKE2C2B0E766E43C2B` (`labor_performed`),
  CONSTRAINT `FKE2C2B0E766E43C2B` FOREIGN KEY (`labor_performed`) REFERENCES `labor_detail` (`id`),
  CONSTRAINT `FKE2C2B0E7D3C210F4` FOREIGN KEY (`service`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service_labor_performed`
--


/*!40000 ALTER TABLE `service_labor_performed` DISABLE KEYS */;
LOCK TABLES `service_labor_performed` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `service_labor_performed` ENABLE KEYS */;

--
-- Table structure for table `service_nonoemparts_replaced`
--

DROP TABLE IF EXISTS `service_nonoemparts_replaced`;
CREATE TABLE `service_nonoemparts_replaced` (
  `service` bigint(20) NOT NULL,
  `nonoemparts_replaced` bigint(20) NOT NULL,
  UNIQUE KEY `nonoemparts_replaced` (`nonoemparts_replaced`),
  KEY `FK7D24AFE3D3C210F4` (`service`),
  KEY `FK7D24AFE3D51C630D` (`nonoemparts_replaced`),
  CONSTRAINT `FK7D24AFE3D51C630D` FOREIGN KEY (`nonoemparts_replaced`) REFERENCES `non_oem_part_replaced` (`id`),
  CONSTRAINT `FK7D24AFE3D3C210F4` FOREIGN KEY (`service`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service_nonoemparts_replaced`
--


/*!40000 ALTER TABLE `service_nonoemparts_replaced` DISABLE KEYS */;
LOCK TABLES `service_nonoemparts_replaced` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `service_nonoemparts_replaced` ENABLE KEYS */;

--
-- Table structure for table `service_oemparts_replaced`
--

DROP TABLE IF EXISTS `service_oemparts_replaced`;
CREATE TABLE `service_oemparts_replaced` (
  `service` bigint(20) NOT NULL,
  `oemparts_replaced` bigint(20) NOT NULL,
  UNIQUE KEY `oemparts_replaced` (`oemparts_replaced`),
  KEY `FKA77F4CBCD3C210F4` (`service`),
  KEY `FKA77F4CBCB0A54499` (`oemparts_replaced`),
  CONSTRAINT `FKA77F4CBCB0A54499` FOREIGN KEY (`oemparts_replaced`) REFERENCES `oem_part_replaced` (`id`),
  CONSTRAINT `FKA77F4CBCD3C210F4` FOREIGN KEY (`service`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service_oemparts_replaced`
--


/*!40000 ALTER TABLE `service_oemparts_replaced` DISABLE KEYS */;
LOCK TABLES `service_oemparts_replaced` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `service_oemparts_replaced` ENABLE KEYS */;

--
-- Table structure for table `service_procedure`
--

DROP TABLE IF EXISTS `service_procedure`;
CREATE TABLE `service_procedure` (
  `id` bigint(20) NOT NULL auto_increment,
  `for_campaigns` bit(1) default NULL,
  `suggested_labour_hours` double default NULL,
  `definition` bigint(20) default NULL,
  `defined_for` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK4C7025C9DEC189BD` (`defined_for`),
  KEY `FK4C7025C949324628` (`definition`),
  CONSTRAINT `FK4C7025C949324628` FOREIGN KEY (`definition`) REFERENCES `service_procedure_definition` (`id`),
  CONSTRAINT `FK4C7025C9DEC189BD` FOREIGN KEY (`defined_for`) REFERENCES `assembly` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service_procedure`
--


/*!40000 ALTER TABLE `service_procedure` DISABLE KEYS */;
LOCK TABLES `service_procedure` WRITE;
INSERT INTO `service_procedure` VALUES (1,'\0',5,7,2),(2,'',3,9,2),(3,'\0',10,4,1),(4,'',30,8,4),(5,'\0',11,7,4),(6,'',7,6,6),(7,'\0',12,11,6),(8,'\0',9,3,8),(9,'\0',23,14,8),(10,'\0',8,4,9);
UNLOCK TABLES;
/*!40000 ALTER TABLE `service_procedure` ENABLE KEYS */;

--
-- Table structure for table `service_procedure_definition`
--

DROP TABLE IF EXISTS `service_procedure_definition`;
CREATE TABLE `service_procedure_definition` (
  `id` bigint(20) NOT NULL auto_increment,
  `code` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service_procedure_definition`
--


/*!40000 ALTER TABLE `service_procedure_definition` DISABLE KEYS */;
LOCK TABLES `service_procedure_definition` WRITE;
INSERT INTO `service_procedure_definition` VALUES (1,'AA','REPLACE'),(2,'AB','CHECK'),(3,'AC','GRIND'),(4,'AD','DEBURR'),(5,'AE','ALIGN'),(6,'AF','INSPECT/TEST'),(7,'AG','LOOSEN'),(8,'AH','MANUFACTURE/FABRICATE'),(9,'AI','DIAGNOSTICS/FAULT FINDING'),(10,'AJ','REPAIR'),(11,'AK','ADJUST'),(12,'AL','OVERHAUL'),(13,'AM','RECHARGE'),(14,'AN','CLEAN'),(15,'AO','REATTACH');
UNLOCK TABLES;
/*!40000 ALTER TABLE `service_procedure_definition` ENABLE KEYS */;

--
-- Table structure for table `service_procedure_level`
--

DROP TABLE IF EXISTS `service_procedure_level`;
CREATE TABLE `service_procedure_level` (
  `id` bigint(20) NOT NULL auto_increment,
  `generator_type` int(11) default NULL,
  `next_code_value` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service_procedure_level`
--


/*!40000 ALTER TABLE `service_procedure_level` DISABLE KEYS */;
LOCK TABLES `service_procedure_level` WRITE;
INSERT INTO `service_procedure_level` VALUES (1,0,'AO');
UNLOCK TABLES;
/*!40000 ALTER TABLE `service_procedure_level` ENABLE KEYS */;

--
-- Table structure for table `shipment`
--

DROP TABLE IF EXISTS `shipment`;
CREATE TABLE `shipment` (
  `id` bigint(20) NOT NULL auto_increment,
  `comments` text,
  `logical_shipment` bit(1) default NULL,
  `shipment_date` date default NULL,
  `tracking_id` varchar(255) default NULL,
  `destination` bigint(20) default NULL,
  `carrier` bigint(20) default NULL,
  `shipped_by` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKE139719AB880CBB9` (`destination`),
  KEY `FKE139719AD52FF3A` (`carrier`),
  KEY `FKE139719A1FDE16AC` (`shipped_by`),
  CONSTRAINT `FKE139719A1FDE16AC` FOREIGN KEY (`shipped_by`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FKE139719AB880CBB9` FOREIGN KEY (`destination`) REFERENCES `location` (`id`),
  CONSTRAINT `FKE139719AD52FF3A` FOREIGN KEY (`carrier`) REFERENCES `carrier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `shipment`
--


/*!40000 ALTER TABLE `shipment` DISABLE KEYS */;
LOCK TABLES `shipment` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `shipment` ENABLE KEYS */;

--
-- Table structure for table `smr_reason`
--

DROP TABLE IF EXISTS `smr_reason`;
CREATE TABLE `smr_reason` (
  `code` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `state` varchar(255) default NULL,
  PRIMARY KEY  (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `smr_reason`
--


/*!40000 ALTER TABLE `smr_reason` DISABLE KEYS */;
LOCK TABLES `smr_reason` WRITE;
INSERT INTO `smr_reason` VALUES ('smr1','Unit Return Request','active'),('smr2','Rental Request','active'),('smr3','Part(s) Discarded in Error','active'),('smr4','Out Of Warranty','active'),('smr5','Other','active'),('smr6','Late Claim Submission','active'),('smr7','Extra Labor/Travel Requested','active'),('smr8','Customer Settlement (Legal Purposes Only)','active');
UNLOCK TABLES;
/*!40000 ALTER TABLE `smr_reason` ENABLE KEYS */;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` bigint(20) NOT NULL,
  `preferred_location_type` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK9CDBF9CCB2F46962` (`id`),
  CONSTRAINT `FK9CDBF9CCB2F46962` FOREIGN KEY (`id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `supplier`
--


/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
LOCK TABLES `supplier` WRITE;
INSERT INTO `supplier` VALUES (31,'RETAIL'),(34,'BUSINESS'),(35,'RETAIL'),(36,'BUSINESS');
UNLOCK TABLES;
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;

--
-- Table structure for table `supplier_locations`
--

DROP TABLE IF EXISTS `supplier_locations`;
CREATE TABLE `supplier_locations` (
  `supplier` bigint(20) NOT NULL,
  `locations` bigint(20) NOT NULL,
  `locations_mapkey` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`supplier`,`locations_mapkey`),
  KEY `FKFF1C7CAB7A020CAC` (`supplier`),
  KEY `FKFF1C7CABC65EDF09` (`locations`),
  CONSTRAINT `FKFF1C7CABC65EDF09` FOREIGN KEY (`locations`) REFERENCES `location` (`id`),
  CONSTRAINT `FKFF1C7CAB7A020CAC` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `supplier_locations`
--


/*!40000 ALTER TABLE `supplier_locations` DISABLE KEYS */;
LOCK TABLES `supplier_locations` WRITE;
INSERT INTO `supplier_locations` VALUES (31,4,'BUSINESS'),(31,5,'RETAIL'),(34,6,'BUSINESS'),(34,7,'RETAIL'),(35,8,'BUSINESS'),(35,9,'RETAIL'),(36,10,'BUSINESS'),(36,11,'RETAIL');
UNLOCK TABLES;
/*!40000 ALTER TABLE `supplier_locations` ENABLE KEYS */;

--
-- Table structure for table `supplier_part_return`
--

DROP TABLE IF EXISTS `supplier_part_return`;
CREATE TABLE `supplier_part_return` (
  `id` bigint(20) NOT NULL,
  `sra_comment` varchar(255) default NULL,
  `supplier_comment` varchar(255) default NULL,
  `supplier` bigint(20) default NULL,
  `supplier_item` bigint(20) default NULL,
  `contract` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKBF26150955A60473` (`contract`),
  KEY `FKBF2615097A020CAC` (`supplier`),
  KEY `FKBF261509D4B972C3` (`supplier_item`),
  KEY `FKBF26150914608145` (`id`),
  CONSTRAINT `FKBF26150914608145` FOREIGN KEY (`id`) REFERENCES `base_part_return` (`id`),
  CONSTRAINT `FKBF26150955A60473` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FKBF2615097A020CAC` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FKBF261509D4B972C3` FOREIGN KEY (`supplier_item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `supplier_part_return`
--


/*!40000 ALTER TABLE `supplier_part_return` DISABLE KEYS */;
LOCK TABLES `supplier_part_return` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `supplier_part_return` ENABLE KEYS */;

--
-- Table structure for table `supplier_part_return_cost_line_items`
--

DROP TABLE IF EXISTS `supplier_part_return_cost_line_items`;
CREATE TABLE `supplier_part_return_cost_line_items` (
  `supplier_part_return` bigint(20) NOT NULL,
  `cost_line_items` bigint(20) NOT NULL,
  UNIQUE KEY `cost_line_items` (`cost_line_items`),
  KEY `FKC7722031F941B6E8` (`cost_line_items`),
  KEY `FKC77220314701F4A5` (`supplier_part_return`),
  CONSTRAINT `FKC77220314701F4A5` FOREIGN KEY (`supplier_part_return`) REFERENCES `supplier_part_return` (`id`),
  CONSTRAINT `FKC7722031F941B6E8` FOREIGN KEY (`cost_line_items`) REFERENCES `cost_line_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `supplier_part_return_cost_line_items`
--


/*!40000 ALTER TABLE `supplier_part_return_cost_line_items` DISABLE KEYS */;
LOCK TABLES `supplier_part_return_cost_line_items` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `supplier_part_return_cost_line_items` ENABLE KEYS */;

--
-- Table structure for table `transaction_type`
--

DROP TABLE IF EXISTS `transaction_type`;
CREATE TABLE `transaction_type` (
  `type` varchar(255) NOT NULL,
  PRIMARY KEY  (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaction_type`
--


/*!40000 ALTER TABLE `transaction_type` DISABLE KEYS */;
LOCK TABLES `transaction_type` WRITE;
INSERT INTO `transaction_type` VALUES ('Cash Sales'),('Installment Sale'),('Lease'),('Long Term Rental'),('Short Term Rental');
UNLOCK TABLES;
/*!40000 ALTER TABLE `transaction_type` ENABLE KEYS */;

--
-- Table structure for table `transition_conditions`
--

DROP TABLE IF EXISTS `transition_conditions`;
CREATE TABLE `transition_conditions` (
  `task_node` bigint(20) NOT NULL,
  `transition_name` varchar(255) default NULL,
  `expression` varchar(255) default NULL,
  `list_index` int(11) NOT NULL,
  PRIMARY KEY  (`task_node`,`list_index`),
  KEY `FK1EACE7E2787B46E8` (`task_node`),
  CONSTRAINT `FK1EACE7E2787B46E8` FOREIGN KEY (`task_node`) REFERENCES `jbpm_node` (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transition_conditions`
--


/*!40000 ALTER TABLE `transition_conditions` DISABLE KEYS */;
LOCK TABLES `transition_conditions` WRITE;
INSERT INTO `transition_conditions` VALUES (74,'Re-requests for SMR','#{claim.noOfResubmits < 3}',0),(74,'Submit','#{claim.isServiceManagerAccepted}',1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `transition_conditions` ENABLE KEYS */;

--
-- Table structure for table `travel_detail`
--

DROP TABLE IF EXISTS `travel_detail`;
CREATE TABLE `travel_detail` (
  `id` bigint(20) NOT NULL auto_increment,
  `distance` int(11) NOT NULL,
  `distance_charge_amt` decimal(19,2) default NULL,
  `distance_charge_curr` varchar(255) default NULL,
  `hours` int(11) NOT NULL,
  `location` varchar(255) default NULL,
  `time_charge_amt` decimal(19,2) default NULL,
  `time_charge_curr` varchar(255) default NULL,
  `trip_charge_amt` decimal(19,2) default NULL,
  `trip_charge_curr` varchar(255) default NULL,
  `trips` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `travel_detail`
--


/*!40000 ALTER TABLE `travel_detail` DISABLE KEYS */;
LOCK TABLES `travel_detail` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `travel_detail` ENABLE KEYS */;

--
-- Table structure for table `travel_rate`
--

DROP TABLE IF EXISTS `travel_rate`;
CREATE TABLE `travel_rate` (
  `id` bigint(20) NOT NULL auto_increment,
  `from_date` date NOT NULL,
  `till_date` date NOT NULL,
  `distance_rate_amt` decimal(19,2) default NULL,
  `distance_rate_curr` varchar(255) default NULL,
  `hourly_rate_amt` decimal(19,2) default NULL,
  `hourly_rate_curr` varchar(255) default NULL,
  `trip_rate_amt` decimal(19,2) default NULL,
  `trip_rate_curr` varchar(255) default NULL,
  `travel_rates` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKEC9EE6E53F61FC5D` (`travel_rates`),
  CONSTRAINT `FKEC9EE6E53F61FC5D` FOREIGN KEY (`travel_rates`) REFERENCES `travel_rates` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `travel_rate`
--


/*!40000 ALTER TABLE `travel_rate` DISABLE KEYS */;
LOCK TABLES `travel_rate` WRITE;
INSERT INTO `travel_rate` VALUES (1,'2001-01-01','2010-01-01','1.00','USD','1.00','USD','1.00','USD',1),(2,'2001-01-01','2010-01-01','2.00','USD','2.00','USD','2.00','USD',2),(3,'2001-01-01','2010-01-01','3.00','USD','3.00','USD','3.00','USD',3),(4,'2001-01-01','2010-01-01','4.00','USD','4.00','USD','4.00','USD',4),(5,'2001-01-01','2010-01-01','5.00','USD','5.00','USD','5.00','USD',5),(6,'2001-01-01','2010-01-01','6.00','USD','6.00','USD','6.00','USD',6),(7,'2001-01-01','2010-01-01','7.00','USD','7.00','USD','7.00','USD',7),(8,'2001-01-01','2010-01-01','8.00','USD','8.00','USD','8.00','USD',8),(9,'2001-01-01','2010-01-01','9.00','USD','9.00','USD','9.00','USD',9),(10,'2001-01-01','2010-01-01','10.00','USD','10.00','USD','10.00','USD',10),(11,'2001-01-01','2010-01-01','11.00','USD','11.00','USD','11.00','USD',11),(12,'2001-01-01','2010-01-01','12.00','USD','12.00','USD','12.00','USD',12),(13,'2001-01-01','2010-01-01','13.00','USD','13.00','USD','13.00','USD',13),(14,'2001-01-01','2010-01-01','14.00','USD','14.00','USD','14.00','USD',14),(15,'2001-01-01','2010-01-01','15.00','USD','15.00','USD','15.00','USD',15),(16,'2001-01-01','2010-01-01','16.00','USD','16.00','USD','16.00','USD',16),(17,'2001-01-01','2010-01-01','17.00','USD','17.00','USD','17.00','USD',17),(18,'2001-01-01','2010-01-01','18.00','USD','18.00','USD','18.00','USD',18),(19,'2001-01-01','2010-01-01','19.00','USD','19.00','USD','19.00','USD',19),(20,'2001-01-01','2010-01-01','20.00','USD','20.00','USD','20.00','USD',20),(21,'2001-01-01','2010-01-01','21.00','USD','21.00','USD','21.00','USD',21),(22,'2001-01-01','2010-01-01','22.00','USD','22.00','USD','22.00','USD',22),(23,'2001-01-01','2010-01-01','23.00','USD','23.00','USD','23.00','USD',23),(24,'2001-01-01','2010-01-01','24.00','USD','24.00','USD','24.00','USD',24),(25,'2001-01-01','2010-01-01','25.00','USD','25.00','USD','25.00','USD',25),(26,'2001-01-01','2010-01-01','26.00','USD','26.00','USD','26.00','USD',26),(27,'2001-01-01','2010-01-01','27.00','USD','27.00','USD','27.00','USD',27),(28,'2001-01-01','2010-01-01','28.00','USD','28.00','USD','28.00','USD',28),(29,'2001-01-01','2010-01-01','29.00','USD','29.00','USD','29.00','USD',29),(30,'2001-01-01','2010-01-01','30.00','USD','30.00','USD','30.00','USD',30),(31,'2001-01-01','2010-01-01','31.00','USD','31.00','USD','31.00','USD',31),(32,'2001-01-01','2010-01-01','32.00','USD','32.00','USD','32.00','USD',32);
UNLOCK TABLES;
/*!40000 ALTER TABLE `travel_rate` ENABLE KEYS */;

--
-- Table structure for table `travel_rates`
--

DROP TABLE IF EXISTS `travel_rates`;
CREATE TABLE `travel_rates` (
  `id` bigint(20) NOT NULL auto_increment,
  `claim_type` varchar(255) default NULL,
  `relevance_score` bigint(20) default NULL,
  `warranty_type` varchar(255) default NULL,
  `for_criteria_dealer_criterion_dealer_group` bigint(20) default NULL,
  `for_criteria_dealer_criterion_dealer` bigint(20) default NULL,
  `for_criteria_product_type` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKA73DF62E1BC64DD6` (`for_criteria_product_type`),
  KEY `FKA73DF62EBDF2C045` (`for_criteria_dealer_criterion_dealer_group`),
  KEY `FKA73DF62E3BC5B29C` (`for_criteria_dealer_criterion_dealer`),
  CONSTRAINT `FKA73DF62E3BC5B29C` FOREIGN KEY (`for_criteria_dealer_criterion_dealer`) REFERENCES `dealership` (`id`),
  CONSTRAINT `FKA73DF62E1BC64DD6` FOREIGN KEY (`for_criteria_product_type`) REFERENCES `item_group` (`id`),
  CONSTRAINT `FKA73DF62EBDF2C045` FOREIGN KEY (`for_criteria_dealer_criterion_dealer_group`) REFERENCES `dealer_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `travel_rates`
--


/*!40000 ALTER TABLE `travel_rates` DISABLE KEYS */;
LOCK TABLES `travel_rates` WRITE;
INSERT INTO `travel_rates` VALUES (1,NULL,0,NULL,NULL,NULL,NULL),(2,NULL,1,NULL,NULL,NULL,5),(3,NULL,2,'STANDARD',NULL,NULL,NULL),(4,NULL,3,'STANDARD',NULL,NULL,5),(5,'Machine',4,NULL,NULL,NULL,NULL),(6,'Machine',5,NULL,NULL,NULL,5),(7,'Machine',6,'STANDARD',NULL,NULL,NULL),(8,'Machine',7,'STANDARD',NULL,NULL,5),(9,NULL,8,NULL,2,NULL,NULL),(10,NULL,9,NULL,2,NULL,5),(11,NULL,10,'STANDARD',2,NULL,NULL),(12,NULL,11,'STANDARD',2,NULL,5),(13,'Machine',12,NULL,2,NULL,NULL),(14,'Machine',13,NULL,2,NULL,5),(15,'Machine',14,'STANDARD',2,NULL,NULL),(16,'Machine',15,'STANDARD',2,NULL,5),(17,NULL,8,NULL,1,NULL,NULL),(18,NULL,9,NULL,1,NULL,5),(19,NULL,10,'STANDARD',1,NULL,NULL),(20,NULL,11,'STANDARD',1,NULL,5),(21,'Machine',12,NULL,1,NULL,NULL),(22,'Machine',13,NULL,1,NULL,5),(23,'Machine',14,'STANDARD',1,NULL,NULL),(24,'Machine',15,'STANDARD',1,NULL,5),(25,NULL,16,NULL,NULL,7,NULL),(26,NULL,17,NULL,NULL,7,5),(27,NULL,18,'STANDARD',NULL,7,NULL),(28,NULL,19,'STANDARD',NULL,7,5),(29,'Machine',20,NULL,NULL,7,NULL),(30,'Machine',21,NULL,NULL,7,5),(31,'Machine',22,'STANDARD',NULL,7,NULL),(32,'Machine',23,'STANDARD',NULL,7,5);
UNLOCK TABLES;
/*!40000 ALTER TABLE `travel_rates` ENABLE KEYS */;

--
-- Table structure for table `tread_bucket`
--

DROP TABLE IF EXISTS `tread_bucket`;
CREATE TABLE `tread_bucket` (
  `code` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tread_bucket`
--


/*!40000 ALTER TABLE `tread_bucket` DISABLE KEYS */;
LOCK TABLES `tread_bucket` WRITE;
INSERT INTO `tread_bucket` VALUES ('01','Steering'),('02','Suspension'),('03','ServiceBrake'),('04','ServiceBrakeAir'),('05','ParkingBrake'),('06','EngAndEngCooling'),('07','FuelSys'),('08','FuelSysDiesel'),('09','FuelSysOther'),('10','PowerTrain'),('11','Electrical'),('12','ExtLighting'),('13','Visibility'),('14','AirBags'),('15','SeatBelts'),('16','Structure'),('17','Latch'),('18','SpeedControl'),('19','TiresRelated'),('20','Wheels'),('21','TrailerHitch'),('22','Seats'),('23','FireRelated'),('24','Rollover');
UNLOCK TABLES;
/*!40000 ALTER TABLE `tread_bucket` ENABLE KEYS */;

--
-- Table structure for table `upload_claim_history`
--

DROP TABLE IF EXISTS `upload_claim_history`;
CREATE TABLE `upload_claim_history` (
  `id` bigint(20) NOT NULL auto_increment,
  `date_of_upload` datetime default NULL,
  `error_file` blob,
  `input_file` blob,
  `number_of_error_claims` int(11) default NULL,
  `number_of_successful_claims` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `upload_claim_history`
--


/*!40000 ALTER TABLE `upload_claim_history` DISABLE KEYS */;
LOCK TABLES `upload_claim_history` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `upload_claim_history` ENABLE KEYS */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) default NULL,
  `locale` varchar(255) default NULL,
  `password` varchar(255) default NULL,
  `user_id` varchar(255) default NULL,
  `supervisor` bigint(20) default NULL,
  `belongs_to_organization` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK36EBCBC33A2193` (`belongs_to_organization`),
  KEY `FK36EBCB2AD70247` (`supervisor`),
  KEY `FK36EBCB6FC6C76D` (`id`),
  CONSTRAINT `FK36EBCB6FC6C76D` FOREIGN KEY (`id`) REFERENCES `party` (`id`),
  CONSTRAINT `FK36EBCB2AD70247` FOREIGN KEY (`supervisor`) REFERENCES `user` (`id`),
  CONSTRAINT `FK36EBCBC33A2193` FOREIGN KEY (`belongs_to_organization`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--


/*!40000 ALTER TABLE `user` DISABLE KEYS */;
LOCK TABLES `user` WRITE;
INSERT INTO `user` VALUES (1,'bishop@superdealers.com','en_US','tavant',NULL,NULL,7),(2,'sandy@atozdealers.com','fr_FR','tavant',NULL,NULL,10),(3,'ann@ir.com','en_US','tavant',NULL,4,8),(4,'alissa@ir.com','en_US','tavant',NULL,NULL,8),(5,'phil@ir.com','en_US','tavant',NULL,4,8),(6,'lynn@ir.com','en_US','tavant',NULL,4,8),(9,'jack@gmail.com','en_US','tavant',NULL,NULL,8),(11,'michael@gmail.com','en_US','tavant',NULL,NULL,8),(12,'alonso@gmail.com','en_US','tavant',NULL,NULL,8),(13,'kimi@gmail.com','en_US','tavant',NULL,NULL,8),(14,'webber@gmail.com','en_US','tavant',NULL,NULL,8),(15,'senna@gmail.com','en_US','tavant',NULL,NULL,8),(16,'prost@gmail.com','en_US','tavant',NULL,NULL,8),(17,'massa@gmail.com','en_US','tavant',NULL,NULL,8),(18,'button@gmail.com','en_US','tavant',NULL,NULL,8),(19,'rubens@gmail.com','en_US','tavant',NULL,NULL,8),(20,'dealer1@gmail.com','en_US','tavant',NULL,NULL,NULL),(21,'dealer2@gmail.com','en_US','tavant',NULL,NULL,NULL),(22,'dealer3@gmail.com','en_US','tavant',NULL,NULL,NULL),(23,'dealer4@gmail.com','en_US','tavant',NULL,NULL,NULL),(24,'dealer5@gmail.com','en_US','tavant',NULL,NULL,NULL),(25,'dealer6@gmail.com','en_US','tavant',NULL,NULL,NULL),(26,'Customer1@gmail.com','en_US','tavant',NULL,NULL,NULL),(27,'Customer2@gmail.com','en_US','tavant',NULL,NULL,NULL),(28,'Customer3@gmail.com','en_US','tavant',NULL,NULL,NULL),(29,'Customer4@gmail.com','en_US','tavant',NULL,NULL,NULL),(30,'Customer5@gmail.com','en_US','tavant',NULL,NULL,NULL),(32,'northwind@gmail.com','en_US','tavant',NULL,NULL,31),(33,'sra@gmail.com','en_US','tavant',NULL,NULL,8),(37,'partshipper@gmail.com','en_US','tavant',NULL,NULL,8),(38,'truckdrove@gmail.com','en_US','tavant',NULL,NULL,34),(39,'backyard@gmail.com','en_US','tavant',NULL,NULL,35),(40,'asg@gmail.com','en_US','tavant',NULL,NULL,36),(41,'a@gmail.com','en_US','tavant',NULL,NULL,NULL),(42,'b@gmail.com','en_US','tavant',NULL,NULL,NULL),(43,'c@gmail.com','en_US','tavant',NULL,NULL,NULL),(44,'d@gmail.com','en_US','tavant',NULL,NULL,NULL),(45,'e@gmail.com','en_US','tavant',NULL,NULL,NULL),(46,'f@gmail.com','en_US','tavant',NULL,NULL,NULL),(47,'g@gmail.com','en_US','tavant',NULL,NULL,NULL),(48,'h@gmail.com','en_US','tavant',NULL,NULL,NULL),(49,'i@gmail.com','en_US','tavant',NULL,NULL,NULL),(50,'j@gmail.com','en_US','tavant',NULL,NULL,NULL),(51,'k@gmail.com','en_US','tavant',NULL,NULL,NULL),(52,'l@gmail.com','en_US','tavant',NULL,NULL,NULL),(53,'m@gmail.com','en_US','tavant',NULL,NULL,NULL),(54,'n@gmail.com','en_US','tavant',NULL,NULL,NULL),(55,'o@gmail.com','en_US','tavant',NULL,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

--
-- Table structure for table `user_attrs`
--

DROP TABLE IF EXISTS `user_attrs`;
CREATE TABLE `user_attrs` (
  `user` bigint(20) NOT NULL,
  `attrs` bigint(20) NOT NULL,
  PRIMARY KEY  (`user`,`attrs`),
  KEY `FK72556F2E90379AAA` (`user`),
  KEY `FK72556F2ECF4E18AA` (`attrs`),
  CONSTRAINT `FK72556F2ECF4E18AA` FOREIGN KEY (`attrs`) REFERENCES `attribute` (`id`),
  CONSTRAINT `FK72556F2E90379AAA` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_attrs`
--


/*!40000 ALTER TABLE `user_attrs` DISABLE KEYS */;
LOCK TABLES `user_attrs` WRITE;
INSERT INTO `user_attrs` VALUES (1,2),(3,1),(3,3),(4,1),(4,2),(4,9);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_attrs` ENABLE KEYS */;

--
-- Table structure for table `user_cluster`
--

DROP TABLE IF EXISTS `user_cluster`;
CREATE TABLE `user_cluster` (
  `id` bigint(20) NOT NULL auto_increment,
  `description` varchar(255) default NULL,
  `name` varchar(255) NOT NULL,
  `scheme` bigint(20) default NULL,
  `is_part_of` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8EEAA986BB02E1A9` (`is_part_of`),
  KEY `FK8EEAA9867DFB2309` (`scheme`),
  CONSTRAINT `FK8EEAA9867DFB2309` FOREIGN KEY (`scheme`) REFERENCES `user_scheme` (`id`),
  CONSTRAINT `FK8EEAA986BB02E1A9` FOREIGN KEY (`is_part_of`) REFERENCES `user_cluster` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_cluster`
--


/*!40000 ALTER TABLE `user_cluster` DISABLE KEYS */;
LOCK TABLES `user_cluster` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_cluster` ENABLE KEYS */;

--
-- Table structure for table `user_cluster_user`
--

DROP TABLE IF EXISTS `user_cluster_user`;
CREATE TABLE `user_cluster_user` (
  `user_cluster` bigint(20) NOT NULL,
  `user` bigint(20) NOT NULL,
  PRIMARY KEY  (`user_cluster`,`user`),
  KEY `FK8071FEE490379AAA` (`user`),
  KEY `FK8071FEE41FC3FBA1` (`user_cluster`),
  CONSTRAINT `FK8071FEE41FC3FBA1` FOREIGN KEY (`user_cluster`) REFERENCES `user_cluster` (`id`),
  CONSTRAINT `FK8071FEE490379AAA` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_cluster_user`
--


/*!40000 ALTER TABLE `user_cluster_user` DISABLE KEYS */;
LOCK TABLES `user_cluster_user` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_cluster_user` ENABLE KEYS */;

--
-- Table structure for table `user_comment`
--

DROP TABLE IF EXISTS `user_comment`;
CREATE TABLE `user_comment` (
  `id` bigint(20) NOT NULL auto_increment,
  `comment` varchar(255) default NULL,
  `internal_comment` bit(1) NOT NULL,
  `made_on` bigint(20) default NULL,
  `made_by` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK939585EBC13E86A0` (`made_by`),
  CONSTRAINT `FK939585EBC13E86A0` FOREIGN KEY (`made_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_comment`
--


/*!40000 ALTER TABLE `user_comment` DISABLE KEYS */;
LOCK TABLES `user_comment` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_comment` ENABLE KEYS */;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_group`
--


/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
LOCK TABLES `user_group` WRITE;
INSERT INTO `user_group` VALUES (1,'French Processors'),(2,'Excavator Processors'),(3,'US Processors'),(4,'Machine Processors'),(5,'US Dsm'),(6,'Machine Dsm'),(7,'Parts Processor'),(8,'Parts Dsm');
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;

--
-- Table structure for table `user_group_attrs`
--

DROP TABLE IF EXISTS `user_group_attrs`;
CREATE TABLE `user_group_attrs` (
  `user_group` bigint(20) NOT NULL,
  `attrs` bigint(20) NOT NULL,
  PRIMARY KEY  (`user_group`,`attrs`),
  KEY `FKE3B2946ECF4E18AA` (`attrs`),
  KEY `FKE3B2946E4E8BCBEB` (`user_group`),
  CONSTRAINT `FKE3B2946E4E8BCBEB` FOREIGN KEY (`user_group`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FKE3B2946ECF4E18AA` FOREIGN KEY (`attrs`) REFERENCES `attribute` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_group_attrs`
--


/*!40000 ALTER TABLE `user_group_attrs` DISABLE KEYS */;
LOCK TABLES `user_group_attrs` WRITE;
INSERT INTO `user_group_attrs` VALUES (1,4),(2,5),(3,6),(5,6),(4,7),(6,7),(1,10),(2,10),(3,10),(4,10),(7,10),(5,11),(6,11),(8,11),(7,12),(8,12);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_group_attrs` ENABLE KEYS */;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `user` bigint(20) NOT NULL,
  `roles` bigint(20) NOT NULL,
  PRIMARY KEY  (`user`,`roles`),
  KEY `FK7342994990379AAA` (`user`),
  KEY `FK734299499679D247` (`roles`),
  CONSTRAINT `FK734299499679D247` FOREIGN KEY (`roles`) REFERENCES `role` (`id`),
  CONSTRAINT `FK7342994990379AAA` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_roles`
--


/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
LOCK TABLES `user_roles` WRITE;
INSERT INTO `user_roles` VALUES (1,1),(2,1),(3,2),(4,2),(4,3),(5,4),(11,1),(11,2),(12,4),(13,2),(13,6),(14,4),(15,4),(16,4),(17,2),(17,5),(18,4),(19,2),(20,1),(21,1),(22,1),(23,1),(24,1),(25,1),(26,10),(27,10),(28,10),(29,10),(30,10),(32,7),(33,8),(37,9),(38,7),(39,7),(40,7),(41,5),(42,6),(43,9),(44,5),(45,6),(46,9),(47,5),(48,6),(49,9),(50,5),(51,5),(52,6),(53,6),(54,9),(55,9);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;

--
-- Table structure for table `user_scheme`
--

DROP TABLE IF EXISTS `user_scheme`;
CREATE TABLE `user_scheme` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_scheme`
--


/*!40000 ALTER TABLE `user_scheme` DISABLE KEYS */;
LOCK TABLES `user_scheme` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_scheme` ENABLE KEYS */;

--
-- Table structure for table `user_scheme_purposes`
--

DROP TABLE IF EXISTS `user_scheme_purposes`;
CREATE TABLE `user_scheme_purposes` (
  `user_scheme` bigint(20) NOT NULL,
  `purposes` bigint(20) NOT NULL,
  PRIMARY KEY  (`user_scheme`,`purposes`),
  UNIQUE KEY `purposes` (`purposes`),
  KEY `FK47DE77FBCFD0EB05` (`purposes`),
  KEY `FK47DE77FBAA3469FD` (`user_scheme`),
  CONSTRAINT `FK47DE77FBAA3469FD` FOREIGN KEY (`user_scheme`) REFERENCES `user_scheme` (`id`),
  CONSTRAINT `FK47DE77FBCFD0EB05` FOREIGN KEY (`purposes`) REFERENCES `purpose` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_scheme_purposes`
--


/*!40000 ALTER TABLE `user_scheme_purposes` DISABLE KEYS */;
LOCK TABLES `user_scheme_purposes` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_scheme_purposes` ENABLE KEYS */;

--
-- Table structure for table `user_user_groups`
--

DROP TABLE IF EXISTS `user_user_groups`;
CREATE TABLE `user_user_groups` (
  `users` bigint(20) NOT NULL,
  `user_groups` bigint(20) NOT NULL,
  PRIMARY KEY  (`users`,`user_groups`),
  KEY `FK8578125496A73CE7` (`users`),
  KEY `FK85781254BE59EBA8` (`user_groups`),
  CONSTRAINT `FK85781254BE59EBA8` FOREIGN KEY (`user_groups`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FK8578125496A73CE7` FOREIGN KEY (`users`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_user_groups`
--


/*!40000 ALTER TABLE `user_user_groups` DISABLE KEYS */;
LOCK TABLES `user_user_groups` WRITE;
INSERT INTO `user_user_groups` VALUES (3,2),(4,1),(4,3),(11,3),(11,4),(11,7),(12,6),(12,8),(13,3),(14,6),(14,8),(15,5),(15,6),(15,8),(16,5),(17,3),(18,5),(19,4),(19,7);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user_user_groups` ENABLE KEYS */;

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
  `id` bigint(20) NOT NULL auto_increment,
  `location` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK88EF3AC37F0A2760` (`location`),
  CONSTRAINT `FK88EF3AC37F0A2760` FOREIGN KEY (`location`) REFERENCES `location` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `warehouse`
--


/*!40000 ALTER TABLE `warehouse` DISABLE KEYS */;
LOCK TABLES `warehouse` WRITE;
INSERT INTO `warehouse` VALUES (1,1),(2,2),(3,3);
UNLOCK TABLES;
/*!40000 ALTER TABLE `warehouse` ENABLE KEYS */;

--
-- Table structure for table `warehouse_inspectors`
--

DROP TABLE IF EXISTS `warehouse_inspectors`;
CREATE TABLE `warehouse_inspectors` (
  `warehouse` bigint(20) NOT NULL,
  `inspectors` bigint(20) NOT NULL,
  UNIQUE KEY `inspectors` (`inspectors`),
  KEY `FKA0C2F758F86BD33B` (`inspectors`),
  KEY `FKA0C2F7582D08C50` (`warehouse`),
  CONSTRAINT `FKA0C2F7582D08C50` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`),
  CONSTRAINT `FKA0C2F758F86BD33B` FOREIGN KEY (`inspectors`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `warehouse_inspectors`
--


/*!40000 ALTER TABLE `warehouse_inspectors` DISABLE KEYS */;
LOCK TABLES `warehouse_inspectors` WRITE;
INSERT INTO `warehouse_inspectors` VALUES (1,42),(2,45),(3,48);
UNLOCK TABLES;
/*!40000 ALTER TABLE `warehouse_inspectors` ENABLE KEYS */;

--
-- Table structure for table `warehouse_part_shippers`
--

DROP TABLE IF EXISTS `warehouse_part_shippers`;
CREATE TABLE `warehouse_part_shippers` (
  `warehouse` bigint(20) NOT NULL,
  `part_shippers` bigint(20) NOT NULL,
  UNIQUE KEY `part_shippers` (`part_shippers`),
  KEY `FKDD92D9822D08C50` (`warehouse`),
  KEY `FKDD92D982B9620CDD` (`part_shippers`),
  CONSTRAINT `FKDD92D982B9620CDD` FOREIGN KEY (`part_shippers`) REFERENCES `user` (`id`),
  CONSTRAINT `FKDD92D9822D08C50` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `warehouse_part_shippers`
--


/*!40000 ALTER TABLE `warehouse_part_shippers` DISABLE KEYS */;
LOCK TABLES `warehouse_part_shippers` WRITE;
INSERT INTO `warehouse_part_shippers` VALUES (1,43),(2,46),(3,49);
UNLOCK TABLES;
/*!40000 ALTER TABLE `warehouse_part_shippers` ENABLE KEYS */;

--
-- Table structure for table `warehouse_recievers`
--

DROP TABLE IF EXISTS `warehouse_recievers`;
CREATE TABLE `warehouse_recievers` (
  `warehouse` bigint(20) NOT NULL,
  `recievers` bigint(20) NOT NULL,
  UNIQUE KEY `recievers` (`recievers`),
  KEY `FK7C7036602D08C50` (`warehouse`),
  KEY `FK7C703660C04D07BB` (`recievers`),
  CONSTRAINT `FK7C703660C04D07BB` FOREIGN KEY (`recievers`) REFERENCES `user` (`id`),
  CONSTRAINT `FK7C7036602D08C50` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `warehouse_recievers`
--


/*!40000 ALTER TABLE `warehouse_recievers` DISABLE KEYS */;
LOCK TABLES `warehouse_recievers` WRITE;
INSERT INTO `warehouse_recievers` VALUES (1,41),(2,44),(3,47);
UNLOCK TABLES;
/*!40000 ALTER TABLE `warehouse_recievers` ENABLE KEYS */;

--
-- Table structure for table `warehouse_warehouse_bins`
--

DROP TABLE IF EXISTS `warehouse_warehouse_bins`;
CREATE TABLE `warehouse_warehouse_bins` (
  `warehouse` bigint(20) NOT NULL,
  `warehouse_bins_element` varchar(255) default NULL,
  KEY `FKD1ACD9442D08C50` (`warehouse`),
  CONSTRAINT `FKD1ACD9442D08C50` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `warehouse_warehouse_bins`
--


/*!40000 ALTER TABLE `warehouse_warehouse_bins` DISABLE KEYS */;
LOCK TABLES `warehouse_warehouse_bins` WRITE;
INSERT INTO `warehouse_warehouse_bins` VALUES (1,'Aisle 1'),(1,'Aisle 2'),(1,'Aisle 3'),(1,'Aisle 4'),(2,'Aisle 1'),(2,'Aisle 2'),(2,'Aisle 3'),(2,'Aisle 4'),(3,'Aisle 1'),(3,'Aisle 2'),(3,'Aisle 3'),(3,'Aisle 4');
UNLOCK TABLES;
/*!40000 ALTER TABLE `warehouse_warehouse_bins` ENABLE KEYS */;

--
-- Table structure for table `warranty`
--

DROP TABLE IF EXISTS `warranty`;
CREATE TABLE `warranty` (
  `id` bigint(20) NOT NULL auto_increment,
  `marketing_information` bigint(20) default NULL,
  `customer` bigint(20) NOT NULL,
  `for_item` bigint(20) NOT NULL,
  `customer_address` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `for_item` (`for_item`),
  KEY `FK1DE500FCA7C2BDD1` (`for_item`),
  KEY `FK1DE500FCC58E14F3` (`customer_address`),
  KEY `FK1DE500FC6564D7C3` (`customer`),
  KEY `FK1DE500FC34463940` (`marketing_information`),
  CONSTRAINT `FK1DE500FC34463940` FOREIGN KEY (`marketing_information`) REFERENCES `marketing_information` (`id`),
  CONSTRAINT `FK1DE500FC6564D7C3` FOREIGN KEY (`customer`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK1DE500FCA7C2BDD1` FOREIGN KEY (`for_item`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FK1DE500FCC58E14F3` FOREIGN KEY (`customer_address`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `warranty`
--


/*!40000 ALTER TABLE `warranty` DISABLE KEYS */;
LOCK TABLES `warranty` WRITE;
INSERT INTO `warranty` VALUES (1,NULL,29,14675556,20),(2,NULL,29,14937806,20),(3,NULL,29,15009556,20),(4,NULL,29,15009606,20),(5,NULL,29,15081406,20),(6,NULL,29,15081456,20),(7,NULL,29,15390206,20),(8,NULL,29,15517006,20),(9,NULL,29,15639806,20),(10,NULL,29,17797106,20),(11,NULL,29,17797156,20),(12,NULL,30,17808406,21),(13,NULL,30,17816256,21),(14,NULL,30,17816806,21),(15,NULL,30,17874156,21),(16,NULL,30,17888856,21),(17,NULL,30,17916706,21),(18,NULL,30,17935306,21),(19,NULL,30,17937106,21),(20,NULL,30,17975956,21),(21,NULL,30,17978706,21),(22,NULL,30,17989706,21);
UNLOCK TABLES;
/*!40000 ALTER TABLE `warranty` ENABLE KEYS */;

--
-- Table structure for table `warranty_policies`
--

DROP TABLE IF EXISTS `warranty_policies`;
CREATE TABLE `warranty_policies` (
  `warranty` bigint(20) NOT NULL,
  `policies` bigint(20) NOT NULL,
  PRIMARY KEY  (`warranty`,`policies`),
  UNIQUE KEY `policies` (`policies`),
  KEY `FK52047D337B3B18AB` (`policies`),
  KEY `FK52047D3358EBD9FF` (`warranty`),
  CONSTRAINT `FK52047D3358EBD9FF` FOREIGN KEY (`warranty`) REFERENCES `warranty` (`id`),
  CONSTRAINT `FK52047D337B3B18AB` FOREIGN KEY (`policies`) REFERENCES `policy` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `warranty_policies`
--


/*!40000 ALTER TABLE `warranty_policies` DISABLE KEYS */;
LOCK TABLES `warranty_policies` WRITE;
INSERT INTO `warranty_policies` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10),(11,11),(12,12),(13,13),(14,14),(15,15),(16,16),(17,17),(18,18),(19,19),(20,20),(21,21),(22,22);
UNLOCK TABLES;
/*!40000 ALTER TABLE `warranty_policies` ENABLE KEYS */;

--
-- Table structure for table `warranty_type`
--

DROP TABLE IF EXISTS `warranty_type`;
CREATE TABLE `warranty_type` (
  `type` varchar(255) NOT NULL,
  PRIMARY KEY  (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `warranty_type`
--


/*!40000 ALTER TABLE `warranty_type` DISABLE KEYS */;
LOCK TABLES `warranty_type` WRITE;
INSERT INTO `warranty_type` VALUES ('EXTENDED'),('GOODWILL'),('STANDARD');
UNLOCK TABLES;
/*!40000 ALTER TABLE `warranty_type` ENABLE KEYS */;

--
-- Table structure for table `watched_dealership`
--

DROP TABLE IF EXISTS `watched_dealership`;
CREATE TABLE `watched_dealership` (
  `id` bigint(20) NOT NULL auto_increment,
  `dealer` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK5027070652828602` (`dealer`),
  CONSTRAINT `FK5027070652828602` FOREIGN KEY (`dealer`) REFERENCES `dealership` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `watched_dealership`
--


/*!40000 ALTER TABLE `watched_dealership` DISABLE KEYS */;
LOCK TABLES `watched_dealership` WRITE;
INSERT INTO `watched_dealership` VALUES (1,10);
UNLOCK TABLES;
/*!40000 ALTER TABLE `watched_dealership` ENABLE KEYS */;

--
-- Table structure for table `watched_part`
--

DROP TABLE IF EXISTS `watched_part`;
CREATE TABLE `watched_part` (
  `id` bigint(20) NOT NULL auto_increment,
  `item` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKC85D1FE458AA7430` (`item`),
  CONSTRAINT `FKC85D1FE458AA7430` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `watched_part`
--


/*!40000 ALTER TABLE `watched_part` DISABLE KEYS */;
LOCK TABLES `watched_part` WRITE;
INSERT INTO `watched_part` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10),(11,11),(12,12),(13,13),(14,14),(15,15),(16,16),(17,17),(18,18),(19,19),(20,20),(21,21),(22,22),(23,23),(34,24);
UNLOCK TABLES;
/*!40000 ALTER TABLE `watched_part` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

