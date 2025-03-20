-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : database
-- Généré le : jeu. 20 mars 2025 à 17:18
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
  `numEtat` int(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `tickets`
--

INSERT INTO `tickets` (`idTicket`, `nomTicket`, `dateTicket`, `numUser`, `numEtat`) VALUES
(1, 'Corriger bug JavaScript', '2024-02-15', 1, 1),
(2, 'Corriger balises HTML sur la page TICKET', '2024-02-13', 2, 4),
(3, 'Améliorer bootstrap', '2024-02-06', 4, 1),
(4, 'Modifier la table client', '2024-09-13', 3, 2),
(5, 'Paramétrer serveur Linux', '2024-12-14', 2, 4),
(6, 'Ecrire les tests unitaires', '2024-12-13', 3, 2),
(7, 'Ecrire scripts PHP', '2024-12-01', 4, 1),
(8, 'Ajouter la méthode UpDateTicket ', '2024-12-13', 4, 3),
(25, 'Réparer la machine en salle BJ48', '2024-12-10', 6, 2),
(26, 'Verifier que la Maj dans tous les PC de la salle BJ15', '2024-12-10', 7, 3),
(27, 'Installer un routeur ', '2024-12-10', 5, 4),
(28, 'PC salle bj45', '2024-12-10', 2, 4),
(29, 'ajouter un switch', '2024-12-10', 3, 2),
(30, 'changer un pc', '2024-12-10', 3, 2),
(31, '5', '2025-03-19', 2, 3);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `idUser` int(11) NOT NULL,
  `nomUser` varchar(20) NOT NULL,
  `prenomUser` varchar(20) NOT NULL,
  `loginUser` varchar(20) NOT NULL,
  `pwdUser` varchar(20) NOT NULL,
  `statutUser` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`idUser`, `nomUser`, `prenomUser`, `loginUser`, `pwdUser`, `statutUser`) VALUES
(1, 'D\'amico', 'Mr', 'damico', 'damico', 'admin'),
(2, 'Lukas', 'Osorio', 'lukas', 'lukas', 'user'),
(3, 'Luc', 'Sarrazin', 'luc', 'luc', 'user'),
(4, 'Alban', 'Chesnel', 'alban', 'alban', 'user'),
(5, 'Nassim', 'Chabulaine', 'nassim', 'nassim', 'user'),
(6, 'Mohamed', ' El Nagar', 'mohamed', 'mohamed', 'user'),
(7, 'Thibault', 'Cognet', 'thibault', 'thibault', 'user');

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
  MODIFY `idTicket` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `idUser` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

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
