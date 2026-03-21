package mr.anetat.medicamentsapp.controller;

import java.util.Arrays;

public enum ReferenceDataType {
    FORMES("formes", "Formes", "Forme", "Libellé", "Libellé complet"),
    LABORATOIRES("laboratoires", "Laboratoires", "Laboratoire", "Nom", "Adresse"),
    MOLECULES("molecules", "Molécules", "Molécule", "Nom", null),
    UNITES_DOSAGE("unites-dosage", "Unités de dosage", "Unité de dosage", "Libellé", "Libellé complet");

    private final String slug;
    private final String pluralLabel;
    private final String singularLabel;
    private final String primaryFieldLabel;
    private final String secondaryFieldLabel;

    ReferenceDataType(
            String slug,
            String pluralLabel,
            String singularLabel,
            String primaryFieldLabel,
            String secondaryFieldLabel) {
        this.slug = slug;
        this.pluralLabel = pluralLabel;
        this.singularLabel = singularLabel;
        this.primaryFieldLabel = primaryFieldLabel;
        this.secondaryFieldLabel = secondaryFieldLabel;
    }

    public String getSlug() {
        return slug;
    }

    public String getPluralLabel() {
        return pluralLabel;
    }

    public String getSingularLabel() {
        return singularLabel;
    }

    public String getPrimaryFieldLabel() {
        return primaryFieldLabel;
    }

    public String getSecondaryFieldLabel() {
        return secondaryFieldLabel;
    }

    public boolean hasSecondaryField() {
        return secondaryFieldLabel != null;
    }

    public static ReferenceDataType fromSlug(String slug) {
        return Arrays.stream(values())
                .filter(type -> type.slug.equalsIgnoreCase(slug))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type de référentiel inconnu: " + slug));
    }
}

