package com.start.helm.cli;

import com.start.helm.cli.arg.ArgumentValidator;
import org.junit.jupiter.api.Test;

class ArgumentValidatorTest {

    @Test
    void isValidArgs() {

        ArgumentValidator.isValidArgs(new String[]{"--build-file", "build.gradle"});


    }
}
