/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2018, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata.git;

/**
 * Represents information about a Git commit like supplied by
 * {@code git describe}
 *
 * @author Sebastian Staudt
 */
public class GitTagDescription {

    private static final String DESCRIBE_FORMAT = "%s-%d-g%s";

    private String abbrevCommitId;

    private int distance;

    private GitTag nextTag;

    /**
     * Create a new description for the given information
     *
     * @param abbrevCommitId The abbreviated commit ID
     * @param nextTag The next tag reachable from the commit
     * @param distance The distance to the next tag
     */
    public GitTagDescription(String abbrevCommitId, GitTag nextTag, int distance) {
        this.abbrevCommitId = abbrevCommitId;
        this.distance = distance;
        this.nextTag = nextTag;
    }

    /**
     * Returns the name of the next reachable tag
     *
     * @return The name of the next tag
     */
    public String getNextTagName() {
        return (nextTag == null) ? "" : nextTag.getName();
    }

    /**
     * Returns whether the commit is tagged
     *
     * @return {@code true} if the commit is tagged
     */
    public boolean isTagged() {
        return distance == 0;
    }

    /**
     * Returns the string representation of this description
     * <p>
     * This includes the abbreviated commit ID and (if available) the distance
     * to and the name of the next tag.
     *
     * @return The string representation of this description
     */
    @Override
    public String toString() {
        if (nextTag == null) {
            return abbrevCommitId;
        }

        if (distance == 0) {
            return nextTag.getName();
        }

        return String.format(DESCRIBE_FORMAT,
            nextTag.getName(),
            distance,
            abbrevCommitId);
    }

}
