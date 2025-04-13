-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.23 - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for medusa
CREATE DATABASE IF NOT EXISTS `medusa` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `medusa`;

-- Dumping structure for table medusa.npc_template
CREATE TABLE IF NOT EXISTS `npc_template` (
  `id` int(11) NOT NULL,
  `NAME` varchar(50) NOT NULL,
  `head` int(11) NOT NULL,
  `body` int(11) NOT NULL,
  `leg` int(11) NOT NULL,
  `avatar` int(11) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- Dumping data for table medusa.npc_template: ~124 rows (approximately)
DELETE FROM `npc_template`;
INSERT INTO `npc_template` (`id`, `NAME`, `head`, `body`, `leg`, `avatar`) VALUES
	(0, 'Ông Gôhan', 18, 19, 20, 349),
	(1, 'Ông Paragus', 24, 25, 26, 348),
	(2, 'Ông Moori', 21, 22, 23, 347),
	(3, 'Rương đồ', 74, 75, 265, 0),
	(4, 'Đậu thần', 84, 51, 84, 0),
	(5, 'Con mèo', 75, -1, -1, 0),
	(6, 'Khu vực', -1, -1, -1, 0),
	(7, 'Bunma', 42, 43, 44, 562),
	(8, 'Dende', 45, 46, 47, 350),
	(9, 'Appule', 3, 4, 5, 565),
	(10, 'Dr. Brief', 784, 785, 786, 7184),
	(11, 'Cargo', 54, 55, 56, 641),
	(12, 'Cui', 48, 49, 50, 639),
	(13, 'Quy Lão Kame', 33, 34, 35, 564),
	(14, 'Trưởng lão Guru', 39, 40, 41, 566),
	(15, 'Vua Vegeta', 36, 37, 38, 563),
	(16, 'Uron', 61, 62, 63, 728),
	(17, 'Bò Mộng', 80, 81, 82, 1142),
	(18, 'Thần mèo Karin', 89, 90, 91, 1209),
	(19, 'Thượng Đế', 86, 87, 88, 1356),
	(20, 'Thần Vũ Trụ', 98, 99, 100, 1357),
	(21, 'Bà Hạt Mít', 117, 118, 119, 1410),
	(22, 'Trọng tài', 114, 115, 116, 1411),
	(23, 'Ghi danh', 120, 121, 122, 1415),
	(24, 'Rồng Thiêng', 103, 104, 105, 0),
	(25, 'Lính canh', 132, 133, 134, 1468),
	(26, 'Độc Nhãn', 144, 145, 146, 1571),
	(27, 'Rồng Thần Namec', 0, 0, 0, 0),
	(28, 'Cửa hàng ký gửi', 120, 121, 122, 1415),
	(29, 'Rồng Thần Sao Đen', 225, 226, 227, 2344),
	(30, 'Rồng 2 sao', 207, 208, 209, 2333),
	(31, 'Rồng 3 sao', 210, 211, 212, 2334),
	(32, 'Rồng 4 sao', 213, 214, 215, 2335),
	(33, 'Rồng 5 sao', 216, 217, 218, 2336),
	(34, 'Rồng 6 sao', 219, 220, 221, 2337),
	(35, 'Rồng 7 sao', 222, 223, 224, 2338),
	(36, 'Rồng Thần Tà Ác', 225, 226, 227, 2344),
	(37, 'Bunma', 267, 268, 269, 2752),
	(38, 'Ca Lích', 270, 271, 272, 1364),
	(39, 'Santa', 300, 301, 302, 2993),
	(40, 'Mabư mập', 297, 298, 299, 0),
	(41, 'Trung thu', 120, 121, 122, 0),
	(42, 'Quốc Vương', 442, 443, 444, 4335),
	(43, 'Tổ Sư Kaio', 448, 449, 450, 4389),
	(44, 'Ôsin', 433, 434, 435, 4390),
	(45, 'Kibit', 436, 437, 438, 4391),
	(46, 'Babiđây', 430, 431, 432, 4388),
	(47, 'Giu-ma Đầu Bò', 445, 446, 447, 0),
	(48, 'Ngộ Không', 462, 470, 471, 0),
	(49, 'Đường Tăng', 467, 468, 469, 4544),
	(50, 'Quả trứng', -1, -1, -1, 0),
	(51, 'Dưa hấu', -1, -1, -1, 0),
	(52, 'Hùng Vương', 484, 485, 486, 0),
	(53, 'Tapion', 481, 482, 483, 4668),
	(54, 'Lý Tiểu Nương', 487, 488, 489, 3049),
	(55, 'Berrus', 508, 509, 510, 5067),
	(56, 'Whis', 505, 506, 507, 5073),
	(57, 'Champa', 511, 512, 513, 0),
	(58, 'Vados', 530, 531, 532, 5074),
	(59, 'Trọng tài', 533, 534, 535, 0),
	(60, 'Goku SSJ', 101, 57, 66, 1359),
	(61, 'Goku SSJ', 0, 523, 524, 516),
	(62, 'Potage', 621, 622, 623, 5828),
	(63, 'Jaco', 624, 625, 626, 5833),
	(64, 'Thiên Sứ Whis', 505, 506, 507, 5073),
	(65, 'Yarirobe', 77, 78, 79, 2119),
	(66, 'Nồi bánh', 766, 767, 768, 7084),
	(67, 'Mr Popo', 83, 84, 85, 2132),
	(68, 'Panchy', 787, 788, 789, 0),
	(69, 'Thỏ Đại Ca', 403, 404, 405, 0),
	(70, 'Bardock', 1012, 1013, 1014, 9075),
	(71, 'zeno', 1015, 1016, 1017, 9076),
	(72, 'Unknown', 1143, 1144, 1145, 10477),
	(73, 'Fide', 1062, 1063, 1064, 9493),
	(74, 'Tori-Bot', 1143, 1144, 1145, 10477),
	(75, 'Jiren Sự kiện', 1234, 1235, 1236, 11313),
	(76, 'Chuyển Sinh', 793, 794, 795, 7270),
	(77, 'Nhận PET', 946, 947, 948, 8617),
	(78, 'SHOP VIP', 709, 710, 711, 8058),
	(79, 'Zamasu', 1312, 1313, 1314, 11840),
	(80, 'Mèo Thần Tài', 1198, 1199, 1200, 11039),
	(81, 'SHOP VIP', 709, 710, 711, 8058),
	(82, 'Mị Nương (Hộ tống)', 841, 842, 843, 7742),
	(83, 'NPC Chiến Thần', 1354, 1355, 1356, 12713),
	(84, 'Rồng Xương', 103, 104, 105, 0),
	(85, 'ZENOSAMA', 1427, 1428, 1429, 16405),
	(86, 'Câu cá', 1068, 1069, 1070, 9588),
	(87, 'Gắp thú Noel', 1994, 1995, 1996, 21895),
	(88, 'Chiến trường PK', 1436, 1437, 1438, 22196),
	(89, 'Nữ Vương Medusa', 1475, 1476, 1477, 21517),
	(90, 'Kết hôn', 1396, 1397, 1398, 18328),
	(91, 'Rồng Siêu Cấp', 103, 104, 105, 0),
	(92, 'Goku Sự Kiện', 542, 1449, 1450, 5208),
	(93, 'Cây Nêu', 1481, 1482, 1483, 21500),
	(94, 'Goku áo dài', 1484, 1485, 1486, 12590),
	(95, 'Tết Thiếu Nhi', 409, 410, 411, 4119),
	(96, 'Tết Thiếu Nhi', 409, 410, 411, 4119),
	(97, 'Quả trứng linh thú', 1997, 1998, 1999, 15072),
	(98, 'Ông già Noel', 657, 658, 659, 6126),
	(99, 'Cây thông Noel', 2003, 2004, 2005, 0),
	(100, 'Girlkun', 391, 392, 393, 0),
	(101, 'Rồng Băng', 0, 0, 0, 0),
	(102, 'Monaito', 2021, 2022, 2023, 0),
	(103, '', 0, 0, 0, 0),
	(104, '', 0, 0, 0, 0),
	(105, '', 0, 0, 0, 0),
	(106, '', 0, 0, 0, 0),
	(107, '', 0, 0, 0, 0),
	(108, '', 0, 0, 0, 0),
	(109, '', 0, 0, 0, 0),
	(110, '', 0, 0, 0, 0),
	(111, '', 0, 0, 0, 0),
	(112, '', 0, 0, 0, 0),
	(113, '', 0, 0, 0, 0),
	(114, 'Monkey D. Luffy', 582, 583, 584, 5717),
	(115, 'Roronoa Zoro', 585, 586, 587, 5718),
	(116, 'Vinsmoke Sanji', 588, 589, 590, 5715),
	(117, 'Nami', 600, 601, 602, 5713),
	(118, 'Nico Robin', 603, 604, 605, 5712),
	(119, 'Usopp', 597, 598, 599, 5714),
	(120, 'Chopper', 606, 607, 608, 5716),
	(121, 'Franky', 594, 595, 596, 5720),
	(122, 'Brook', 591, 592, 593, 5719),
	(123, 'Thần Cấp Luyện Khí Sư', 1246, 1247, 1248, 13507);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
