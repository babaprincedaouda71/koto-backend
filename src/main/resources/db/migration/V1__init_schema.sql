CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       nom VARCHAR(100) NOT NULL,
                       prenom VARCHAR(100) NOT NULL,
                       telephone VARCHAR(20) UNIQUE NOT NULL,
                       email VARCHAR(150) UNIQUE,
                       mot_de_passe_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'MEMBRE',
                       cree_le TIMESTAMP DEFAULT NOW()
);

CREATE TABLE groupes (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         admin_id UUID NOT NULL REFERENCES users(id),
                         nom VARCHAR(150) NOT NULL,
                         montant_cotisation INTEGER NOT NULL,
                         devise VARCHAR(10) NOT NULL DEFAULT 'XOF',
                         nombre_membres INTEGER NOT NULL,
                         date_debut DATE NOT NULL,
                         statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF',
                         token_invitation UUID DEFAULT uuid_generate_v4(),
                         cree_le TIMESTAMP DEFAULT NOW()
);

CREATE TABLE membres (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         groupe_id UUID NOT NULL REFERENCES groupes(id),
                         user_id UUID NOT NULL REFERENCES users(id),
                         ordre_reception INTEGER NOT NULL,
                         statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF',
                         rejoint_le TIMESTAMP DEFAULT NOW(),
                         UNIQUE(groupe_id, user_id),
                         UNIQUE(groupe_id, ordre_reception)
);

CREATE TABLE cycles (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        groupe_id UUID NOT NULL REFERENCES groupes(id),
                        beneficiaire_id UUID NOT NULL REFERENCES membres(id),
                        numero_cycle INTEGER NOT NULL,
                        date_debut DATE NOT NULL,
                        date_fin DATE NOT NULL,
                        statut VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
                        UNIQUE(groupe_id, numero_cycle)
);

CREATE TABLE paiements (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           cycle_id UUID NOT NULL REFERENCES cycles(id),
                           membre_id UUID NOT NULL REFERENCES membres(id),
                           montant INTEGER NOT NULL,
                           statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
                           date_echeance DATE NOT NULL,
                           date_paiement DATE,
                           note TEXT,
                           UNIQUE(cycle_id, membre_id)
);

CREATE TABLE rappels (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         paiement_id UUID NOT NULL REFERENCES paiements(id),
                         canal VARCHAR(20) NOT NULL DEFAULT 'WHATSAPP',
                         envoye_le TIMESTAMP DEFAULT NOW(),
                         statut VARCHAR(20) NOT NULL DEFAULT 'ENVOYE'
);