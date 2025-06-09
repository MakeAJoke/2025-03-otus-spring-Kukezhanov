package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class TestRunnerCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Run test", key = {"run", "rt"})
    public void runTest() {
        testRunnerService.run();
    }
}
