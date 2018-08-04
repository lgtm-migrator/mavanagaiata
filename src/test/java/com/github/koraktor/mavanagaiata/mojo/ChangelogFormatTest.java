/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2018, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata.mojo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.koraktor.mavanagaiata.git.GitCommit;
import com.github.koraktor.mavanagaiata.git.GitTag;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;

public class ChangelogFormatTest {

    private ChangelogFormat format;
    private ByteArrayOutputStream outputStream;
    private BufferedReader reader;

    @Before
    public void setup() {
        outputStream = new ByteArrayOutputStream();

        format = new ChangelogFormat();

        format.baseUrl = "https://git.example.com";
        format.branch = "Branch\\n%s";
        format.branchLink = "Compare branch\\n%s\\n%s\\n%s";
        format.branchOnlyLink = "Compare branch only\\n%s\\n%s";
        format.dateFormat = "MM/dd/yyyy";
        format.commitPrefix = "\\n- ";
        format.header = "A\\nheader";
        format.separator = "\\n\\n";
        format.tag = "Tag\\n%s\\n%s";
        format.tagLink = "Compare tag\\n%s\\n%s";
        format.prepare();

        format.printStream = new PrintStream(outputStream);
    }

    private Date getDate() {
        return new GregorianCalendar(2011, 3, 29).getTime();
    }

    @Test
    public void testPrepare() {
        assertThat(format.branch, is(equalTo("Branch\n%s")));
        assertThat(format.branchLink, is(equalTo("Compare branch\n%s\n%s\n%s")));
        assertThat(format.branchOnlyLink, is(equalTo("Compare branch only\n%s\n%s")));
        assertThat(format.dateFormatter.format(getDate()), is(equalTo("04/29/2011")));
        assertThat(format.commitPrefix, is(equalTo("\n- ")));
        assertThat(format.header, is(equalTo("A\nheader")));
        assertThat(format.separator, is(equalTo("\n\n")));
        assertThat(format.tag, is(equalTo("Tag\n%s\n%s")));
        assertThat(format.tagLink, is(equalTo("Compare tag\n%s\n%s")));
    }

    @Test
    public void testPrintBranch() throws IOException {
        format.printBranch("master");

        assertOutputLine("");
        assertOutputLine("");
        assertOutputLine("Branch");
        assertOutputLine("master");
        assertOutputLine("");
        assertOutputLine("");
        assertOutputLine(null);
    }

    @Test
    public void testPrintHeader() throws IOException {
        format.printHeader();

        assertOutputLine("A");
        assertOutputLine("header");
        assertOutputLine(null);
    }

    @Test
    public void testPrintTag() throws IOException {
        GitTag tag = new GitTag() {
            @Override
            public Date getDate() {
                return ChangelogFormatTest.this.getDate();
            }

            @Override
            public String getName() {
                return "1.0.0";
            }

            @Override
            public TimeZone getTimeZone() {
                return TimeZone.getDefault();
            }

            @Override
            public boolean isLoaded() {
                return false;
            }
        };
        format.printTag(tag);

        assertOutputLine("Tag");
        assertOutputLine("1.0.0");
        assertOutputLine("04/29/2011");
        assertOutputLine("");
        assertOutputLine("");
        assertOutputLine(null);
    }

    @Test
    public void testPrintCommit() throws IOException {
        format.printCommit(commit());

        assertOutputLine("");
        assertOutputLine("- Commit > message");
        assertOutputLine(null);
    }

    @Test
    public void testPrintCommitEscapeHtml() throws IOException {
        format.escapeHtml = true;
        format.printCommit(commit());

        assertOutputLine("");
        assertOutputLine("- Commit &gt; message");
        assertOutputLine(null);
    }

    @Test
    public void testPrintCompareLink() throws IOException {
        format.printCompareLink("2.0.0", "master", true);
        format.printCompareLink("1.0.0", "2.0.0", false);
        format.printCompareLink("1.0.0", null, false);
        format.printCompareLink("master", null, true);

        assertOutputLine("Compare branch");
        assertOutputLine("master");
        assertOutputLine("2.0.0");
        assertOutputLine("https://git.example.com/compare/2.0.0...master");
        assertOutputLine("");
        assertOutputLine("");
        assertOutputLine("Compare tag");
        assertOutputLine("2.0.0");
        assertOutputLine("https://git.example.com/compare/1.0.0...2.0.0");
        assertOutputLine("");
        assertOutputLine("");
        assertOutputLine("Compare tag");
        assertOutputLine("1.0.0");
        assertOutputLine("https://git.example.com/commits/1.0.0");
        assertOutputLine("");
        assertOutputLine("");
        assertOutputLine("Compare branch only");
        assertOutputLine("master");
        assertOutputLine("https://git.example.com/commits/master");
        assertOutputLine("");
        assertOutputLine("");
        assertOutputLine(null);
    }

    @Test
    public void testPrintSeparator() throws IOException {
        format.separator = "---";
        format.printSeparator();

        assertOutputLine("---");
        assertOutputLine(null);
    }

    @After
    public void tearDown() {
        reader = null;
    }

    private void assertOutputLine(String line) throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new StringReader(outputStream.toString()));
        }

        assertThat(reader.readLine(), is(equalTo(line)));
    }

    private GitCommit commit() {
        return new GitCommit() {
            @Override
            public Date getAuthorDate() {
                return null;
            }

            @Override
            public String getAuthorEmailAddress() {
                return null;
            }

            @Override
            public String getAuthorName() {
                return null;
            }

            @Override
            public TimeZone getAuthorTimeZone() {
                return null;
            }

            @Override
            public Date getCommitterDate() {
                return null;
            }

            @Override
            public String getCommitterEmailAddress() {
                return null;
            }

            @Override
            public String getCommitterName() {
                return null;
            }

            @Override
            public TimeZone getCommitterTimeZone() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getMessage() {
                return null;
            }

            @Override
            public String getMessageSubject() {
                return "Commit > message";
            }

            @Override
            public boolean isMergeCommit() {
                return false;
            }
        };
    }
}
