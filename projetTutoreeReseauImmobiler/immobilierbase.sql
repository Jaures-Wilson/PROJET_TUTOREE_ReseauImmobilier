-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : sam. 31 mai 2025 à 18:38
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `immobilierbase`
--

-- --------------------------------------------------------

--
-- Structure de la table `acquereur`
--

CREATE TABLE `acquereur` (
  `id_acquereur` int(11) NOT NULL,
  `historique_recherches` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`historique_recherches`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `acquereur`
--

INSERT INTO `acquereur` (`id_acquereur`, `historique_recherches`) VALUES
(1, '[\"studio\", \"Douala\"]'),
(2, '[\"appartement\", \"Yaoundé\"]'),
(3, '[\"villa\", \"Bafoussam\"]');

-- --------------------------------------------------------

--
-- Structure de la table `administrateur`
--

CREATE TABLE `administrateur` (
  `id_admin` int(11) NOT NULL,
  `numero_mtn_momo` int(11) DEFAULT NULL,
  `numero_orange_money` int(11) DEFAULT NULL,
  `numero_uba` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `administrateur`
--

INSERT INTO `administrateur` (`id_admin`, `numero_mtn_momo`, `numero_orange_money`, `numero_uba`) VALUES
(1, 677000030, 688000040, 123789456),
(2, 676000050, 689000060, 321654987),
(3, 699000010, 690000020, 456123789);

-- --------------------------------------------------------

--
-- Structure de la table `annonce`
--

CREATE TABLE `annonce` (
  `id_annonce` int(11) NOT NULL,
  `titre` text NOT NULL,
  `description` text NOT NULL,
  `prix_mensuel` int(11) NOT NULL,
  `localisation` text NOT NULL,
  `status` enum('DISPONIBLE','RÉSERVÉ','VENDU','REFUSE') NOT NULL,
  `date_publication` datetime NOT NULL DEFAULT current_timestamp(),
  `tarif_reservation` int(11) DEFAULT NULL,
  `nombre_vue` int(11) DEFAULT 0,
  `nombre_favori` int(11) DEFAULT 0,
  `nombre_visite` int(11) DEFAULT 0,
  `id_annonceur` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `annonce`
--

INSERT INTO `annonce` (`id_annonce`, `titre`, `description`, `prix_mensuel`, `localisation`, `status`, `date_publication`, `tarif_reservation`, `nombre_vue`, `nombre_favori`, `nombre_visite`, `id_annonceur`) VALUES
(1, 'Studio à Bonamoussadi', 'Studio moderne et bien équipé', 100000, 'Douala', 'DISPONIBLE', '2025-05-29 02:55:19', 10000, 0, 0, 0, 2),
(2, 'Appartement à Bastos', 'Grand appartement 3 chambres', 250000, 'Yaoundé', 'RÉSERVÉ', '2025-05-29 02:55:19', 15000, 0, 0, 0, 2),
(3, 'Chambre à Makepe', 'Petite chambre pas chère', 50000, 'Douala', 'VENDU', '2025-05-29 02:55:19', 5000, 0, 0, 0, 2);

-- --------------------------------------------------------

--
-- Structure de la table `annonceur`
--

CREATE TABLE `annonceur` (
  `id_annonceur` int(11) NOT NULL,
  `numero_mtn_momo` int(11) DEFAULT NULL,
  `numero_orange_money` int(11) DEFAULT NULL,
  `numero_uba` int(11) DEFAULT NULL,
  `id_abonnement_actif` int(11) DEFAULT NULL,
  `id_abonnement` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `annonceur`
--

INSERT INTO `annonceur` (`id_annonceur`, `numero_mtn_momo`, `numero_orange_money`, `numero_uba`, `id_abonnement_actif`, `id_abonnement`) VALUES
(1, 699000003, 678000004, 987654321, NULL, NULL),
(2, 675000001, 690000002, 123456789, NULL, NULL),
(3, 670000005, 673000006, 111222333, NULL, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `contrat`
--

CREATE TABLE `contrat` (
  `id_contrat` int(11) NOT NULL,
  `date_signature` datetime NOT NULL DEFAULT current_timestamp(),
  `type` enum('PROMESSE_VENTE','VENTE') NOT NULL,
  `date_debut_contrat` datetime DEFAULT NULL,
  `date_fin_contrat` datetime DEFAULT NULL,
  `precision_en_surplus` text DEFAULT NULL,
  `decision_acquereur` tinyint(1) DEFAULT 0,
  `id_annonce` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `contrat`
--

INSERT INTO `contrat` (`id_contrat`, `date_signature`, `type`, `date_debut_contrat`, `date_fin_contrat`, `precision_en_surplus`, `decision_acquereur`, `id_annonce`) VALUES
(1, '2025-05-29 02:55:20', 'VENTE', '2025-05-29 02:55:20', '2025-11-29 02:55:20', 'Paiement complet', 1, 1),
(2, '2025-05-29 02:55:20', 'PROMESSE_VENTE', '2025-05-29 02:55:20', '2025-08-29 02:55:20', 'Acompte versé', 0, 2),
(3, '2025-05-29 02:55:20', 'VENTE', '2025-05-29 02:55:20', '2026-05-29 02:55:20', 'Contrat notarié', 1, 3);

-- --------------------------------------------------------

--
-- Structure de la table `favoris`
--

CREATE TABLE `favoris` (
  `id_acquereur` int(11) NOT NULL,
  `id_annonce` int(11) NOT NULL,
  `date_ajout` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `favoris`
--

INSERT INTO `favoris` (`id_acquereur`, `id_annonce`, `date_ajout`) VALUES
(1, 1, '2025-05-29 02:55:20'),
(1, 2, '2025-05-29 02:55:20'),
(1, 3, '2025-05-29 02:55:20');

-- --------------------------------------------------------

--
-- Structure de la table `message`
--

CREATE TABLE `message` (
  `id_message` int(11) NOT NULL,
  `contenu` text NOT NULL,
  `date_envoi` datetime NOT NULL DEFAULT current_timestamp(),
  `lu` tinyint(1) DEFAULT 0,
  `id_expediteur` int(11) NOT NULL,
  `id_destinataire` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `message`
--

INSERT INTO `message` (`id_message`, `contenu`, `date_envoi`, `lu`, `id_expediteur`, `id_destinataire`) VALUES
(1, 'Bonjour, je suis intéressé par votre annonce.', '2025-05-29 02:55:20', 0, 1, 2),
(2, 'Merci pour votre message.', '2025-05-29 02:55:20', 1, 2, 1),
(3, 'Quand puis-je visiter ?', '2025-05-29 02:55:20', 0, 1, 2);

-- --------------------------------------------------------

--
-- Structure de la table `notification`
--

CREATE TABLE `notification` (
  `id_notification` int(11) NOT NULL,
  `date_creation` datetime NOT NULL DEFAULT current_timestamp(),
  `type` enum('CONTRAT','PAIEMENT','VISITE','CONVOITISE','MESSAGE') NOT NULL,
  `lu` tinyint(1) DEFAULT 0,
  `id_expediteur` int(11) NOT NULL,
  `id_destinataire` int(11) NOT NULL,
  `contenu` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `notification`
--

INSERT INTO `notification` (`id_notification`, `date_creation`, `type`, `lu`, `id_expediteur`, `id_destinataire`, `contenu`) VALUES
(1, '2025-05-29 02:55:20', 'PAIEMENT', 0, 2, 1, ''),
(2, '2025-05-29 02:55:20', 'VISITE', 1, 3, 1, ''),
(3, '2025-05-29 02:55:20', 'CONTRAT', 0, 1, 2, '');

-- --------------------------------------------------------

--
-- Structure de la table `paiement`
--

CREATE TABLE `paiement` (
  `id_paiement` int(11) NOT NULL,
  `montant` int(11) NOT NULL,
  `date_paiement` datetime NOT NULL DEFAULT current_timestamp(),
  `moyen_paiement` enum('CARTE_UBA','OM','MoMo') NOT NULL,
  `statut` enum('CONFIRMEE','EN_ATTENTE','REFUSE') NOT NULL,
  `type_contrat` enum('PROMESSE_VENTE','VENTE') NOT NULL,
  `capture_photo_preuve_paiement` tinyblob NOT NULL,
  `lu` tinyint(1) DEFAULT 0,
  `id_annonce` int(11) NOT NULL,
  `id_acquereur` int(11) NOT NULL,
  `id_annonceur` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `photo`
--

CREATE TABLE `photo` (
  `id_photo` int(11) NOT NULL,
  `id_annonce` int(11) NOT NULL,
  `photo` longblob NOT NULL,
  `est_video` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `plainte`
--

CREATE TABLE `plainte` (
  `id_plainte` int(11) NOT NULL,
  `contenu` text NOT NULL,
  `date_plainte` datetime NOT NULL DEFAULT current_timestamp(),
  `statut` text NOT NULL CHECK (`statut` in ('NOUVELLE','LU')),
  `id_acquereur` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `plainte`
--

INSERT INTO `plainte` (`id_plainte`, `contenu`, `date_plainte`, `statut`, `id_acquereur`) VALUES
(1, 'Le vendeur ne répond pas.', '2025-05-29 02:55:20', 'NOUVELLE', 1),
(2, 'Annonce frauduleuse.', '2025-05-29 02:55:20', 'LU', 1),
(3, 'Le bien est déjà vendu mais encore affiché.', '2025-05-29 02:55:20', 'NOUVELLE', 1);

-- --------------------------------------------------------

--
-- Structure de la table `signataire`
--

CREATE TABLE `signataire` (
  `id_contrat` int(11) NOT NULL,
  `id_utilisateur` int(11) NOT NULL,
  `date_signature` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `signataire`
--

INSERT INTO `signataire` (`id_contrat`, `id_utilisateur`, `date_signature`) VALUES
(1, 1, '2025-05-29 02:55:20'),
(2, 2, '2025-05-29 02:55:20'),
(3, 1, '2025-05-29 02:55:20');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id_utilisateur` int(11) NOT NULL,
  `nom` varchar(40) NOT NULL,
  `email` varchar(40) NOT NULL,
  `mot_de_passe` varchar(40) NOT NULL,
  `role` enum('ADMIN','ANNONCEUR','ACQUEREUR') NOT NULL,
  `num_phone` int(11) NOT NULL,
  `format_num_pays` int(11) NOT NULL,
  `photo_profil` tinyblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id_utilisateur`, `nom`, `email`, `mot_de_passe`, `role`, `num_phone`, `format_num_pays`, `photo_profil`) VALUES
(1, 'Alice Mbarga', 'alice@example.com', 'pass123', 'ACQUEREUR', 690112233, 237, NULL),
(2, 'Bruno Nji', 'bruno@example.com', 'pass456', 'ANNONCEUR', 677445566, 237, NULL),
(3, 'Clara Douala', 'clara@example.com', 'adminpass', 'ADMIN', 699998877, 237, NULL),
(4, 'Jean Dupont', 'jean@test.com', 'password123', 'ACQUEREUR', 690123456, 237, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `visite`
--

CREATE TABLE `visite` (
  `id_visite` int(11) NOT NULL,
  `date_visite` date NOT NULL,
  `statut` enum('DEMANDEE','CONFIRMEE','ANNULEE','EFFECTUEE') NOT NULL,
  `id_annonce` int(11) NOT NULL,
  `id_acquereur` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `visite`
--

INSERT INTO `visite` (`id_visite`, `date_visite`, `statut`, `id_annonce`, `id_acquereur`) VALUES
(1, '2025-05-29', 'DEMANDEE', 1, 1),
(2, '2025-05-29', 'CONFIRMEE', 2, 1),
(3, '2025-05-29', 'ANNULEE', 3, 1);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `acquereur`
--
ALTER TABLE `acquereur`
  ADD PRIMARY KEY (`id_acquereur`);

--
-- Index pour la table `administrateur`
--
ALTER TABLE `administrateur`
  ADD PRIMARY KEY (`id_admin`);

--
-- Index pour la table `annonce`
--
ALTER TABLE `annonce`
  ADD PRIMARY KEY (`id_annonce`),
  ADD KEY `FK1wj57nqhxiwgpqdxhv400gi5t` (`id_annonceur`);

--
-- Index pour la table `annonceur`
--
ALTER TABLE `annonceur`
  ADD PRIMARY KEY (`id_annonceur`),
  ADD KEY `id_abonnement_actif` (`id_abonnement_actif`);

--
-- Index pour la table `contrat`
--
ALTER TABLE `contrat`
  ADD PRIMARY KEY (`id_contrat`),
  ADD KEY `id_annonce` (`id_annonce`);

--
-- Index pour la table `favoris`
--
ALTER TABLE `favoris`
  ADD PRIMARY KEY (`id_acquereur`,`id_annonce`),
  ADD KEY `id_annonce` (`id_annonce`);

--
-- Index pour la table `message`
--
ALTER TABLE `message`
  ADD PRIMARY KEY (`id_message`),
  ADD KEY `id_expediteur` (`id_expediteur`),
  ADD KEY `id_destinataire` (`id_destinataire`);

--
-- Index pour la table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`id_notification`),
  ADD KEY `id_expediteur` (`id_expediteur`),
  ADD KEY `id_destinataire` (`id_destinataire`);

--
-- Index pour la table `paiement`
--
ALTER TABLE `paiement`
  ADD PRIMARY KEY (`id_paiement`),
  ADD KEY `id_annonce` (`id_annonce`),
  ADD KEY `id_acquereur` (`id_acquereur`),
  ADD KEY `id_annonceur` (`id_annonceur`);

--
-- Index pour la table `photo`
--
ALTER TABLE `photo`
  ADD PRIMARY KEY (`id_photo`),
  ADD KEY `id_annonce` (`id_annonce`);

--
-- Index pour la table `plainte`
--
ALTER TABLE `plainte`
  ADD PRIMARY KEY (`id_plainte`),
  ADD KEY `id_acquereur` (`id_acquereur`);

--
-- Index pour la table `signataire`
--
ALTER TABLE `signataire`
  ADD PRIMARY KEY (`id_contrat`,`id_utilisateur`),
  ADD KEY `id_utilisateur` (`id_utilisateur`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id_utilisateur`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Index pour la table `visite`
--
ALTER TABLE `visite`
  ADD PRIMARY KEY (`id_visite`),
  ADD KEY `id_annonce` (`id_annonce`),
  ADD KEY `id_acquereur` (`id_acquereur`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `annonce`
--
ALTER TABLE `annonce`
  MODIFY `id_annonce` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `contrat`
--
ALTER TABLE `contrat`
  MODIFY `id_contrat` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `message`
--
ALTER TABLE `message`
  MODIFY `id_message` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `notification`
--
ALTER TABLE `notification`
  MODIFY `id_notification` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `paiement`
--
ALTER TABLE `paiement`
  MODIFY `id_paiement` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `photo`
--
ALTER TABLE `photo`
  MODIFY `id_photo` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `plainte`
--
ALTER TABLE `plainte`
  MODIFY `id_plainte` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id_utilisateur` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `visite`
--
ALTER TABLE `visite`
  MODIFY `id_visite` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `acquereur`
--
ALTER TABLE `acquereur`
  ADD CONSTRAINT `acquereur_ibfk_1` FOREIGN KEY (`id_acquereur`) REFERENCES `utilisateur` (`id_utilisateur`) ON DELETE CASCADE;

--
-- Contraintes pour la table `administrateur`
--
ALTER TABLE `administrateur`
  ADD CONSTRAINT `administrateur_ibfk_1` FOREIGN KEY (`id_admin`) REFERENCES `utilisateur` (`id_utilisateur`) ON DELETE CASCADE;

--
-- Contraintes pour la table `annonce`
--
ALTER TABLE `annonce`
  ADD CONSTRAINT `FK1wj57nqhxiwgpqdxhv400gi5t` FOREIGN KEY (`id_annonceur`) REFERENCES `utilisateur` (`id_utilisateur`),
  ADD CONSTRAINT `annonce_ibfk_1` FOREIGN KEY (`id_annonceur`) REFERENCES `annonceur` (`id_annonceur`) ON DELETE CASCADE;

--
-- Contraintes pour la table `annonceur`
--
ALTER TABLE `annonceur`
  ADD CONSTRAINT `annonceur_ibfk_1` FOREIGN KEY (`id_annonceur`) REFERENCES `utilisateur` (`id_utilisateur`) ON DELETE CASCADE,
  ADD CONSTRAINT `annonceur_ibfk_2` FOREIGN KEY (`id_abonnement_actif`) REFERENCES `abonnement` (`id_abonnement`) ON DELETE SET NULL;

--
-- Contraintes pour la table `contrat`
--
ALTER TABLE `contrat`
  ADD CONSTRAINT `contrat_ibfk_1` FOREIGN KEY (`id_annonce`) REFERENCES `annonce` (`id_annonce`) ON DELETE CASCADE;

--
-- Contraintes pour la table `favoris`
--
ALTER TABLE `favoris`
  ADD CONSTRAINT `favoris_ibfk_1` FOREIGN KEY (`id_acquereur`) REFERENCES `acquereur` (`id_acquereur`) ON DELETE CASCADE,
  ADD CONSTRAINT `favoris_ibfk_2` FOREIGN KEY (`id_annonce`) REFERENCES `annonce` (`id_annonce`) ON DELETE CASCADE;

--
-- Contraintes pour la table `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `message_ibfk_1` FOREIGN KEY (`id_expediteur`) REFERENCES `utilisateur` (`id_utilisateur`) ON DELETE CASCADE,
  ADD CONSTRAINT `message_ibfk_2` FOREIGN KEY (`id_destinataire`) REFERENCES `utilisateur` (`id_utilisateur`) ON DELETE CASCADE;

--
-- Contraintes pour la table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`id_expediteur`) REFERENCES `utilisateur` (`id_utilisateur`) ON DELETE CASCADE,
  ADD CONSTRAINT `notification_ibfk_2` FOREIGN KEY (`id_destinataire`) REFERENCES `utilisateur` (`id_utilisateur`) ON DELETE CASCADE;

--
-- Contraintes pour la table `paiement`
--
ALTER TABLE `paiement`
  ADD CONSTRAINT `paiement_ibfk_1` FOREIGN KEY (`id_annonce`) REFERENCES `annonce` (`id_annonce`) ON DELETE CASCADE,
  ADD CONSTRAINT `paiement_ibfk_2` FOREIGN KEY (`id_acquereur`) REFERENCES `acquereur` (`id_acquereur`) ON DELETE CASCADE,
  ADD CONSTRAINT `paiement_ibfk_3` FOREIGN KEY (`id_annonceur`) REFERENCES `annonceur` (`id_annonceur`) ON DELETE CASCADE;

--
-- Contraintes pour la table `photo`
--
ALTER TABLE `photo`
  ADD CONSTRAINT `photo_ibfk_1` FOREIGN KEY (`id_annonce`) REFERENCES `annonce` (`id_annonce`) ON DELETE CASCADE;

--
-- Contraintes pour la table `plainte`
--
ALTER TABLE `plainte`
  ADD CONSTRAINT `plainte_ibfk_1` FOREIGN KEY (`id_acquereur`) REFERENCES `acquereur` (`id_acquereur`) ON DELETE CASCADE;

--
-- Contraintes pour la table `signataire`
--
ALTER TABLE `signataire`
  ADD CONSTRAINT `signataire_ibfk_1` FOREIGN KEY (`id_contrat`) REFERENCES `contrat` (`id_contrat`) ON DELETE CASCADE,
  ADD CONSTRAINT `signataire_ibfk_2` FOREIGN KEY (`id_utilisateur`) REFERENCES `utilisateur` (`id_utilisateur`) ON DELETE CASCADE;

--
-- Contraintes pour la table `visite`
--
ALTER TABLE `visite`
  ADD CONSTRAINT `visite_ibfk_1` FOREIGN KEY (`id_annonce`) REFERENCES `annonce` (`id_annonce`) ON DELETE CASCADE,
  ADD CONSTRAINT `visite_ibfk_2` FOREIGN KEY (`id_acquereur`) REFERENCES `acquereur` (`id_acquereur`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
