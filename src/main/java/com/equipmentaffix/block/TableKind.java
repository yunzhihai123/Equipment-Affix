package com.equipmentaffix.block;

/**
 * The two table blocks share one block entity and one menu implementation; only
 * the action rules differ.
 */
public enum TableKind {
    UPGRADE("upgrade_table"),
    RESET("reset_table");

    private final String id;

    TableKind(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public String translationKey() {
        return "container.equipment_affix." + this.id;
    }
}
