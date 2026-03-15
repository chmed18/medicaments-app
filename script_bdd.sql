-- =========================================
-- EXTENSION POUR RECHERCHE PARTIELLE
-- =========================================
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- =========================
-- TABLES
-- =========================
CREATE TABLE forme (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE molecule (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE unite_dosage (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(100)
);


CREATE TABLE titulaire_amm (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE groupe_equivalence (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    signature VARCHAR(500) NOT NULL,
    libelle VARCHAR(255),

    CONSTRAINT uq_groupe_equivalence
        UNIQUE (signature)
);

CREATE TABLE medicament (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    libelle VARCHAR(255) NOT NULL,
    libelle_complet VARCHAR(500) NOT NULL,

    forme_id BIGINT NOT NULL,
    titulaire_amm_id BIGINT,
    groupe_equivalence_id BIGINT,

    source VARCHAR(255),

    CONSTRAINT fk_medicament_forme
        FOREIGN KEY (forme_id)
        REFERENCES forme(id),

    CONSTRAINT fk_medicament_titulaire
        FOREIGN KEY (titulaire_amm_id)
        REFERENCES titulaire_amm(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_medicament_groupe
        FOREIGN KEY (groupe_equivalence_id)
        REFERENCES groupe_equivalence(id)
        ON DELETE SET NULL
);

CREATE TABLE medicament_composition (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    medicament_id BIGINT NOT NULL,
    molecule_id BIGINT NOT NULL,
    dosage_valeur NUMERIC(10,2) NOT NULL CHECK (dosage_valeur > 0),
    unite_dosage_id BIGINT NOT NULL,

    ordre_affichage INT CHECK (ordre_affichage IS NULL OR ordre_affichage >= 1),

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

CREATE TABLE medicament_presentation (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    medicament_id BIGINT NOT NULL,
    presentation VARCHAR(255) NOT NULL,

    prix_pharmacie NUMERIC(10,2) CHECK (prix_pharmacie IS NULL OR prix_pharmacie >= 0),
    prix_grossiste NUMERIC(10,2) CHECK (prix_grossiste IS NULL OR prix_grossiste >= 0),
    prix_camec NUMERIC(10,2) CHECK (prix_camec IS NULL OR prix_camec >= 0),

    code_barre VARCHAR(100),

    CONSTRAINT fk_presentation_medicament
        FOREIGN KEY (medicament_id)
        REFERENCES medicament(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_medicament_presentation_code_barre
        UNIQUE (code_barre)
);

-- =========================
-- INDEX CLASSIQUES
-- =========================

CREATE INDEX idx_medicament_forme
ON medicament(forme_id);

CREATE INDEX idx_medicament_groupe
ON medicament(groupe_equivalence_id);

CREATE INDEX idx_medicament_titulaire
ON medicament(titulaire_amm_id);

CREATE INDEX idx_mc_medicament
ON medicament_composition(medicament_id);

CREATE INDEX idx_mc_molecule_dosage
ON medicament_composition(molecule_id, dosage_valeur, unite_dosage_id);

CREATE INDEX idx_presentation_medicament
ON medicament_presentation(medicament_id);

CREATE INDEX idx_presentation_code_barre
ON medicament_presentation(code_barre);

-- =========================
-- INDEX TRIGRAM POUR RECHERCHE PARTIELLE
-- =========================

CREATE INDEX idx_medicament_libelle_trgm
ON medicament
USING gin (libelle gin_trgm_ops);

CREATE INDEX idx_medicament_libelle_complet_trgm
ON medicament
USING gin (libelle_complet gin_trgm_ops);