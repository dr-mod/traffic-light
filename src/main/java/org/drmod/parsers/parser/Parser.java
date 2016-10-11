package org.drmod.parsers.parser;

import org.drmod.parsers.ProjectStatus;

import java.util.concurrent.Callable;

public interface Parser extends Callable<ProjectStatus> {
}
