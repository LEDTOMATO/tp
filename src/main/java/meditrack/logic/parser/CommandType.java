package meditrack.logic.parser;

/**
 * All the different command types the Parser knows how to validate.
 */
public enum CommandType {
    ADD_SUPPLY,
    EDIT_SUPPLY,
    DELETE_SUPPLY,
    GENERATE_RESUPPLY_REPORT,
    ADD_PERSONNEL,
    REMOVE_PERSONNEL,
    UPDATE_STATUS,
    GENERATE_ROSTER
}
