-- =========================================
-- RESET COMPLET DU SCHEMA
-- =========================================

-- Extension utile pour recherche partielle
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- =========================
-- DROP TABLES
-- =========================

DROP TABLE IF EXISTS medicament_composition CASCADE;
DROP TABLE IF EXISTS medicament CASCADE;
DROP TABLE IF EXISTS groupe_equivalence CASCADE;
DROP TABLE IF EXISTS laboratoire CASCADE;
DROP TABLE IF EXISTS unite_dosage CASCADE;
DROP TABLE IF EXISTS molecule CASCADE;
DROP TABLE IF EXISTS forme CASCADE;

-- =========================
-- TABLE forme
-- =========================
CREATE TABLE forme (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       libelle VARCHAR(100) NOT NULL UNIQUE,
                       libelle_complet VARCHAR(255)
);

-- =========================
-- TABLE molecule
-- =========================
CREATE TABLE molecule (
                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          nom VARCHAR(255) NOT NULL UNIQUE
);

-- =========================
-- TABLE unite_dosage
-- =========================
CREATE TABLE unite_dosage (
                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              libelle VARCHAR(100),
                              libelle_complet VARCHAR(255)
);

-- =========================
-- TABLE laboratoire
-- =========================
CREATE TABLE laboratoire (
                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             nom VARCHAR(255) NOT NULL UNIQUE,
                             adresse VARCHAR(500)
);

-- =========================
-- TABLE groupe_equivalence
-- =========================
CREATE TABLE groupe_equivalence (
                                    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    signature VARCHAR(500) NOT NULL,
                                    libelle VARCHAR(1000),

                                    CONSTRAINT uq_groupe_equivalence
                                        UNIQUE (signature)
);

-- =========================
-- TABLE medicament
-- =========================
CREATE TABLE medicament (
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                            libelle VARCHAR(255) NOT NULL,
                            libelle_complet VARCHAR(500) NOT NULL,

                            forme_id BIGINT,
                            laboratoire_id BIGINT,
                            groupe_equivalence_id BIGINT,

                            presentation VARCHAR(255),
                            prix_pharmacie DECIMAL(10,2) CHECK (prix_pharmacie >= 0),
                            prix_grossiste DECIMAL(10,2) CHECK (prix_grossiste >= 0),
                            prix_camec DECIMAL(10,2) CHECK (prix_camec >= 0),

                            source VARCHAR(255),

                            CONSTRAINT fk_medicament_forme
                                FOREIGN KEY (forme_id)
                                    REFERENCES forme(id),

                            CONSTRAINT fk_medicament_laboratoire
                                FOREIGN KEY (laboratoire_id)
                                    REFERENCES laboratoire(id),

                            CONSTRAINT fk_medicament_groupe
                                FOREIGN KEY (groupe_equivalence_id)
                                    REFERENCES groupe_equivalence(id)
);

-- =========================
-- TABLE medicament_composition
-- =========================
CREATE TABLE medicament_composition (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                        medicament_id BIGINT NOT NULL,
                                        molecule_id BIGINT NOT NULL,
                                        dosage_valeur DECIMAL(10,2) ,
                                        unite_dosage_id BIGINT,

                                        ordre_affichage INT,

                                        CONSTRAINT fk_mc_medicament
                                            FOREIGN KEY (medicament_id)
                                                REFERENCES medicament(id)
                                                ON DELETE CASCADE,

                                        CONSTRAINT fk_mc_molecule
                                            FOREIGN KEY (molecule_id)
                                                REFERENCES molecule(id),

                                        CONSTRAINT fk_mc_unite
                                            FOREIGN KEY (unite_dosage_id)
                                                REFERENCES unite_dosage(id),

                                        CONSTRAINT uq_mc
                                            UNIQUE (medicament_id, molecule_id, dosage_valeur, unite_dosage_id)
);

-- =========================
-- INDEX CLASSIQUES
-- =========================

CREATE INDEX idx_medicament_forme
    ON medicament(forme_id);

CREATE INDEX idx_medicament_laboratoire
    ON medicament(laboratoire_id);

CREATE INDEX idx_medicament_groupe
    ON medicament(groupe_equivalence_id);

CREATE INDEX idx_mc_medicament
    ON medicament_composition(medicament_id);

CREATE INDEX idx_mc_molecule_dosage
    ON medicament_composition(molecule_id, dosage_valeur, unite_dosage_id);

-- =========================
-- INDEX TRIGRAM POUR RECHERCHE PARTIELLE
-- =========================

-- Index préfixe insensible à la casse pour la recherche principale (LOWER(col) LIKE 'query%')
CREATE INDEX idx_medicament_libelle_prefix
    ON medicament (LOWER(libelle) text_pattern_ops);

-- Index trigram insensible à la casse pour recherche libre (LOWER(col) LIKE '%query%')
CREATE INDEX idx_medicament_libelle_trgm
    ON medicament USING gin (LOWER(libelle) gin_trgm_ops);

-- Index préfixe insensible à la casse pour l'autocomplétion (LOWER(col) LIKE 'query%')
CREATE INDEX idx_medicament_libelle_complet_prefix
    ON medicament (LOWER(libelle_complet) text_pattern_ops);

-- Index trigram insensible à la casse pour recherche libre (LOWER(col) LIKE '%query%')
CREATE INDEX idx_medicament_libelle_complet_trgm
    ON medicament USING gin (LOWER(libelle_complet) gin_trgm_ops);
