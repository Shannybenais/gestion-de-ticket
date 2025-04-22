-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : database
-- Généré le : mar. 22 avr. 2025 à 10:40
-- Version du serveur : 5.7.44
-- Version de PHP : 8.2.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bddTicket`
--

-- --------------------------------------------------------

--
-- Structure de la table `etats`
--

CREATE TABLE `etats` (
  `idEtat` int(3) NOT NULL,
  `nomEtat` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `etats`
--

INSERT INTO `etats` (`idEtat`, `nomEtat`) VALUES
(1, 'Bloquant'),
(2, 'Majeur'),
(3, 'Mineur'),
(4, 'Terminé');

-- --------------------------------------------------------

--
-- Structure de la table `tickets`
--

CREATE TABLE `tickets` (
  `idTicket` int(11) NOT NULL,
  `nomTicket` varchar(250) NOT NULL,
  `dateTicket` date NOT NULL,
  `numUser` int(11) NOT NULL,
  `numEtat` int(3) NOT NULL,
  `dateFinSouhaitee` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `tickets`
--

INSERT INTO `tickets` (`idTicket`, `nomTicket`, `dateTicket`, `numUser`, `numEtat`, `dateFinSouhaitee`) VALUES
(1, 'Corriger bug JavaScript', '2024-02-15', 1, 1, NULL),
(3, 'Améliorer bootstrap', '2024-02-06', 4, 1, NULL),
(4, 'Modifier la table client', '2024-09-13', 3, 2, NULL),
(5, 'Paramétrer serveur Linux', '2024-12-14', 2, 4, NULL),
(6, 'Ecrire les tests unitaires', '2024-12-13', 3, 2, NULL),
(7, 'Ecrire scripts PHP', '2024-12-01', 4, 1, NULL),
(8, 'Ajouter la méthode UpDateTicket ', '2024-12-13', 4, 3, NULL),
(25, 'Réparer la machine en salle BJ48', '2024-12-10', 6, 2, NULL),
(26, 'Verifier que la Maj dans tous les PC de la salle BJ15', '2024-12-10', 7, 3, NULL),
(28, 'PC salle bj45', '2024-12-10', 2, 2, NULL),
(29, 'ajouter un switch', '2024-12-10', 3, 2, NULL),
(30, 'changer un pc', '2024-12-10', 3, 2, NULL),
(31, '5', '2025-03-19', 2, 3, NULL),
(32, '25', '2025-04-21', 6, 4, '2025-04-29'),
(33, 'changer un pc', '2025-04-21', 3, 1, '2025-04-11');

-- --------------------------------------------------------

--
-- Structure de la table `tickets_archives`
--

CREATE TABLE `tickets_archives` (
  `idArchive` int(11) NOT NULL,
  `idTicket` int(11) NOT NULL,
  `nomTicket` varchar(250) NOT NULL,
  `dateTicket` date NOT NULL,
  `dateFinReelle` date NOT NULL,
  `numUser` int(11) NOT NULL,
  `numEtat` int(3) NOT NULL,
  `dateFinSouhaitee` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `tickets_archives`
--

INSERT INTO `tickets_archives` (`idArchive`, `idTicket`, `nomTicket`, `dateTicket`, `dateFinReelle`, `numUser`, `numEtat`, `dateFinSouhaitee`) VALUES
(1, 2, 'Corriger balises HTML sur la page TICKET', '2024-02-13', '2025-04-21', 2, 4, NULL),
(2, 27, 'Installer un routeur ', '2024-12-10', '2025-04-21', 5, 4, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `idUser` int(11) NOT NULL,
  `nomUser` varchar(20) NOT NULL,
  `prenomUser` varchar(20) NOT NULL,
  `loginUser` varchar(20) NOT NULL,
  `pwdUser` varchar(255) NOT NULL,
  `statutUser` varchar(20) NOT NULL,
  `derniereTentative` datetime DEFAULT NULL,
  `nbTentatives` int(11) DEFAULT '0',
  `compteBloque` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`idUser`, `nomUser`, `prenomUser`, `loginUser`, `pwdUser`, `statutUser`, `derniereTentative`, `nbTentatives`, `compteBloque`) VALUES
(1, 'D\'amico', 'Mr', 'damico', 'Rqt5NJ3dnjuquc1C1O31dA==:MlJk3olBm73fQEp3UTnTtG3JLeLybVkyr+9out8R4+4WHxydrDRYojwg5IA0WONFpG09Ysgl6d7WN/z1LxHMPw==', 'admin', NULL, 0, 0),
(2, 'Lukas', 'Osorio', 'lukas', '1vAIq2epO60vm4uj9Fubuw==:4lBnBMboV3846f46TT6vu1i9Igw7pTV/IBBLSIMFITN0qtg5LOqBZgrhF1LS3kEziO0YKKHY+xoG3+rWc/uRyg==', 'user', NULL, 0, 0),
(3, 'Luc', 'Sarrazin', 'luc', 'TqcyejKS4cayahMfbc1UzA==:VB/GUD1wdHB70WaKAXsJ2RwJCUEpoqzc3y0iNaDWNS+5pemuqcx1qM41YYA5/c9Yf1zmWL0b82Gc5jyFeByjMA==', 'user', NULL, 0, 0),
(4, 'Alban', 'Chesnel', 'alban', 'oVf6VIUWhYskFZY/EGHUmw==:/dKpJl1OHyPNlOhymGrjVs9nWJZg5zEvq70aZO0VG0lKjPiKx93mbsFrAptYq/VeZUbs2N/isURaQjeunY8WjA==', 'user', NULL, 0, 0),
(5, 'Nassim', 'Chabulaine', 'nassim', 'aAsiD7+KKmL71SU1E4w/CA==:Mw+xTMU/8q48fWoup7kZ78doucQGkoq3FkLI7hQSyO33dj82TmY3QJkgwXZA98UYFvthHh8jqcr2QxIvcI5MhQ==', 'user', NULL, 0, 0),
(6, 'Mohamed', ' El Nagar', 'mohamed', 'b5DK0VHsahCOMzF3gCtAsQ==:7xe7OFeTz++mDCii8Jyo73IyB4+Peyo6g5c2p43WrJ/Nw//BJhuaESS9GREkLc0yID5r0v8hmVfPPOsZy6O2Gg==', 'user', NULL, 0, 0),
(7, 'Thibault', 'Cognet', 'thibault', 'nhOyrx1ZNoqz6dqT8Y2mMA==:wiZ3da+b8VQOTZjbWJNPiNAKWfaZIKJ8QHw9dP/405Y2AdUbPzZTjbP5Ajh2pFyR8nLlMln6WxaR1ysAMhEilQ==', 'user', NULL, 0, 0);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `etats`
--
ALTER TABLE `etats`
  ADD PRIMARY KEY (`idEtat`);

--
-- Index pour la table `tickets`
--
ALTER TABLE `tickets`
  ADD PRIMARY KEY (`idTicket`),
  ADD KEY `numEtat` (`numEtat`),
  ADD KEY `idUser` (`numUser`);

--
-- Index pour la table `tickets_archives`
--
ALTER TABLE `tickets_archives`
  ADD PRIMARY KEY (`idArchive`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`idUser`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `tickets`
--
ALTER TABLE `tickets`
  MODIFY `idTicket` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT pour la table `tickets_archives`
--
ALTER TABLE `tickets_archives`
  MODIFY `idArchive` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `idUser` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `tickets`
--
ALTER TABLE `tickets`
  ADD CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`numEtat`) REFERENCES `etats` (`idEtat`),
  ADD CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`numUser`) REFERENCES `users` (`idUser`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
