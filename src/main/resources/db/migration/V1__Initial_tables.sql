-- MySQL dump 10.13  Distrib 8.0.27, for macos11.6 (x86_64)
--
-- Host: 142.132.162.41    Database: comppress
-- ------------------------------------------------------
-- Server version	8.0.30-0ubuntu0.20.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `article`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) DEFAULT NULL,
  `date_modified` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `content` longtext,
  `count_comment` int NOT NULL DEFAULT '0',
  `count_ratings` int NOT NULL DEFAULT '0',
  `description` longtext,
  `guid` varchar(255) DEFAULT NULL,
  `paywall_article` bit(1) NOT NULL,
  `paywall_article_updated` bit(1) NOT NULL,
  `published_at` datetime(6) DEFAULT NULL,
  `rss_feed_id` bigint DEFAULT NULL,
  `scale_image` bit(1) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `url` text,
  `url_to_image` text,
  PRIMARY KEY (`id`),
  KEY `IDXdmog9ruh5ru91tf8iua2wo8l0` (`published_at`),
  KEY `IDX8eax1yunp5qpjvgyltlyitkas` (`paywall_article`),
  KEY `IDXrhc6r4ydd18r7ll366snlvnt7` (`rss_feed_id`),
  KEY `IDXmjaeli3f9i815dujwml0vetl4` (`count_ratings`)
) ENGINE=InnoDB AUTO_INCREMENT=41765 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) DEFAULT NULL,
  `date_modified` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url_to_image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `criteria`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `criteria` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) DEFAULT NULL,
  `date_modified` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `publisher`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publisher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) DEFAULT NULL,
  `date_modified` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url_to_image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h9trv4xhmh6s68vbw9ba6to70` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rating`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rating` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) DEFAULT NULL,
  `date_modified` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `article_id` bigint DEFAULT NULL,
  `criteria_id` bigint DEFAULT NULL,
  `guid` varchar(255) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDXlpole2w3ag1ab3b7ymlcp90ch` (`article_id`,`criteria_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1501 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rss_feed`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rss_feed` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) DEFAULT NULL,
  `date_modified` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  `publisher_id` bigint DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_qh62gadubho9fur1b9o6gdjfn` (`url`),
  KEY `IDX4fd85ss1j6i9ul9ik5rcx9oj3` (`lang`)
) ENGINE=InnoDB AUTO_INCREMENT=386 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `twitter_tweet`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `twitter_tweet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) DEFAULT NULL,
  `date_modified` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `article_id` bigint DEFAULT NULL,
  `reply_count` int DEFAULT '0',
  `twitter_article_url` varchar(255) DEFAULT NULL,
  `twitter_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_created` datetime(6) DEFAULT NULL,
  `date_modified` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_otp_used` tinyint(1) DEFAULT '0',
  `list_category_ids` varchar(255) DEFAULT NULL,
  `list_publisher_ids` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `otp` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'comppress'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-09-18 19:23:15
