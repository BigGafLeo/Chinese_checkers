-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: lab5tp
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `gra`
--

DROP TABLE IF EXISTS `gra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gra` (
  `id_gry` int NOT NULL AUTO_INCREMENT,
  `liczba_ruchów` int NOT NULL DEFAULT '0',
  `liczba_graczy` int DEFAULT '0',
  `data` date NOT NULL,
  `is_started` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_gry`),
  CONSTRAINT `chk` CHECK ((((`liczba_graczy` >= 2) and (`liczba_graczy` <> 5) and (`liczba_graczy` <= 6)) or (`is_started` = false)))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gra`
--

LOCK TABLES `gra` WRITE;
/*!40000 ALTER TABLE `gra` DISABLE KEYS */;
INSERT INTO `gra` VALUES (1,2,2,'2022-01-28',1),(2,0,0,'2022-01-28',0),(3,0,NULL,'2022-01-29',NULL),(4,0,0,'2022-01-29',0),(5,0,0,'2022-01-29',0),(6,2,2,'2022-01-29',0),(7,1,2,'2022-01-29',1);
/*!40000 ALTER TABLE `gra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gracze`
--

DROP TABLE IF EXISTS `gracze`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gracze` (
  `id_gry` int NOT NULL,
  `id_gracza` int NOT NULL,
  `nazwa` varchar(30) NOT NULL,
  PRIMARY KEY (`id_gry`,`id_gracza`),
  CONSTRAINT `gracze_ibfk_1` FOREIGN KEY (`id_gry`) REFERENCES `gra` (`id_gry`) ON DELETE CASCADE,
  CONSTRAINT `chk2` CHECK (((`id_gracza` >= 1) and (`id_gracza` <= 6)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gracze`
--

LOCK TABLES `gracze` WRITE;
/*!40000 ALTER TABLE `gracze` DISABLE KEYS */;
INSERT INTO `gracze` VALUES (1,1,'Mateusz'),(1,2,'222'),(6,1,'xd'),(6,2,'dx'),(7,1,'abc'),(7,2,'de');
/*!40000 ALTER TABLE `gracze` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `dodajLiczbęGraczy` AFTER INSERT ON `gracze` FOR EACH ROW begin
        update gra set liczba_graczy = liczba_graczy + 1 where id_gry = new.id_gry;
    end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `ruch`
--

DROP TABLE IF EXISTS `ruch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ruch` (
  `id_ruchu` int NOT NULL AUTO_INCREMENT,
  `id_gry` int NOT NULL,
  `id_gracza` int NOT NULL,
  `start_x` int NOT NULL,
  `start_y` int NOT NULL,
  `end_x` int NOT NULL,
  `end_y` int NOT NULL,
  PRIMARY KEY (`id_ruchu`),
  KEY `fk` (`id_gry`,`id_gracza`),
  CONSTRAINT `fk` FOREIGN KEY (`id_gry`, `id_gracza`) REFERENCES `gracze` (`id_gry`, `id_gracza`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ruch`
--

LOCK TABLES `ruch` WRITE;
/*!40000 ALTER TABLE `ruch` DISABLE KEYS */;
INSERT INTO `ruch` VALUES (3,1,1,1,1,2,2),(4,1,2,1,1,2,2),(5,6,2,15,3,16,4),(6,6,1,15,13,16,12),(7,7,1,15,13,16,12);
/*!40000 ALTER TABLE `ruch` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `dodajLiczbęRuchów` AFTER INSERT ON `ruch` FOR EACH ROW begin
        update gra set liczba_ruchów = liczba_ruchów + 1 where id_gry = NEW.id_gry;
    end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping events for database 'lab5tp'
--

--
-- Dumping routines for database 'lab5tp'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-01-29 17:32:44
