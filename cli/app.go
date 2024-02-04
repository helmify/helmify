package main

import (
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"path/filepath"
	s "strings"
)

func main() {

	content, p := readBuildFile()
	appName := getAppName(content, p)
	appVersion := getAppVersion(content, p)

	fmt.Println("App Name: ", appName)
	fmt.Println("App Version: ", appVersion)

}

func getAppName(content string, path string) string {

	appName := "app"
	isXml := isXml(content)

	if isXml {
		appName = getAppNameMaven(content)
	} else {
		appName = getAppNameGradle(path)
	}

	return appName
}

func getAppNameGradle(path string) string {
	buildFileDir := filepath.Dir(path)
	settingsGradlePath := filepath.Join(buildFileDir, "settings.gradle")
	content, error := ioutil.ReadFile(settingsGradlePath)
	if error != nil {
		log.Fatal(error)
	}

	lines := s.Split(string(content), "\n")
	for _, line := range lines {
		if s.Contains(line, "rootProject.name") {
			return s.Replace(s.Replace(s.Replace(line, "rootProject.name", "", -1), "=", "", -1), "'", "", -1)
		}
	}

	return "app"
}

func getAppNameMaven(content string) string {
	return extractFromPom(content, "artifactId")
}

func extractFromPom(content string, tagName string) string {
	hasParent := s.Contains(content, "<parent>")
	lines := s.Split(content, "\n")
	parentFound := false

	// iterate lines
	for _, line := range lines {
		if !parentFound && hasParent && s.Contains(line, "<"+tagName+">") {
			parentFound = true
			continue
		}

		if s.Contains(line, "<"+tagName+">") {
			fmt.Println("ArtifactId: ", line)
			return s.Replace(s.Replace(line, "<"+tagName+">", "", -1), "</"+tagName+">", "", -1)
		}
	}
	return "app"
}

func getAppVersion(content string, path string) string {

	if s.Contains(path, "pom.xml") {
		return getAppVersionMaven(content)
	} else {
		return getAppVersionGradle(content)
	}

	return "1.0.0"
}

func getAppVersionMaven(content string) string {
	return extractFromPom(content, "version")
}

func getAppVersionGradle(content string) string {
	// extract version from build.gradle
	lines := s.Split(content, "\n")
	for _, line := range lines {
		if s.HasPrefix(line, "version") {
			return s.Replace(s.Replace(s.Replace(line, "version", "", -1), "=", "", -1), "'", "", -1)
		}
	}
	return "1.0.0"
}

func isXml(content string) bool {
	return s.Contains(content, "<?xml")
}

func readBuildFile() (string, string) {
	buildFilePtr := flag.String("build-file", "", "Absolute Path to pom.xml / build.gradle / build.gradle.kts")

	flag.Parse()

	fmt.Println("tail:", flag.Args())

	var buildFile = *buildFilePtr

	fmt.Println("Build File: ", buildFile)

	content, error := ioutil.ReadFile(buildFile)
	if error != nil {
		log.Fatal(error)
	}

	str := string(content)
	return str, buildFile
}
