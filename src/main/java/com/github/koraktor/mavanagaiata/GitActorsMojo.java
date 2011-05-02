/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * This goal provides the name and email address of the author of the current
 * Git commit in the "mavanagaiata.author.name", "mavanagaiata.author.email",
 * "mvngit.auhtor.name" and "mvngit.author.email" properties. The name and
 * email address of the committer of the current commit is stored in the
 * "mavanagaiata.committer.name", "mavanagaiata.committer.email",
 * "mvngit.commiter.name" and "mvngit.committer.email" properties respectively.
 *
 * @author Sebastian Staudt
 * @goal actors
 * @phase initialize
 * @requiresProject
 */
public class GitActorsMojo extends AbstractGitMojo {

    /**
     * Information about the author and commiter of the currently checked out
     * Git branch is retrieved using a JGit Repository instance
     *
     * @see PersonIdent
     * @see RevCommit#getAuthorIdent()
     * @see RevCommit#getCommitterIdent()
     * @throws MojoExecutionException if retrieving information from the Git
     *         repository fails
     */
    @Override
    public void execute() throws MojoExecutionException {
        super.execute();

        try {
            RevCommit commit = this.getHead();
            PersonIdent author = commit.getAuthorIdent();
            PersonIdent committer = commit.getCommitterIdent();

            project.getProperties().put("mavanagaiata.author.name", author.getName());
            project.getProperties().put("mavanagaiata.author.email", author.getEmailAddress());
            project.getProperties().put("mavanagaiata.committer.name", committer.getName());
            project.getProperties().put("mavanagaiata.committer.email", committer.getEmailAddress());
            project.getProperties().put("mvngit.author.name", author.getName());
            project.getProperties().put("mvngit.author.email", author.getEmailAddress());
            project.getProperties().put("mvngit.committer.name", committer.getName());
            project.getProperties().put("mvngit.committer.email", committer.getEmailAddress());
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read Git actor information", e);
        }
    }
}
