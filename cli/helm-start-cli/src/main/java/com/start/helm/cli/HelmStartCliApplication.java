package com.start.helm.cli;

import com.start.helm.cli.arg.ArgumentListParser;
import com.start.helm.cli.arg.ArgumentValidator;
import com.start.helm.cli.config.AppConfig;
import com.start.helm.cli.config.AppNameProvider;
import com.start.helm.cli.config.AppVersionProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HelmStartCliApplication {

	public static void main(String[] args) {
		if (ArgumentValidator.isValidArgs(args)) {
			ConfigurableApplicationContext ctx = SpringApplication.run(HelmStartCliApplication.class, args);
			String buildFile = ArgumentListParser.getBuildFile(args);

			if (buildFile != null) {

				final String appName = new AppNameProvider().getAppName(buildFile);
				final String appVersion = new AppVersionProvider().getAppVersion(buildFile);

				AppConfig appConfig = new AppConfig(buildFile, appName, appVersion);
				ctx.publishEvent(appConfig);
			}

		}
	}

}
