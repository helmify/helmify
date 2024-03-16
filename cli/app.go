package main

import (
	"flag"
	"fmt"
	"io"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"path/filepath"
	s "strings"
)

/*
* - Read build file (pom.xml / build.gradle)
* - Extract app name
* - Extract app version
* - Send request to API
* - Store Helm Chart
 */
func main() {

	// content, path to build file (pom.xml / build.gradle)
	content, p := readBuildFile()
	// extract app name
	appName := getAppName(content, p)
	// extract app version
	appVersion := getAppVersion(content, p)

	fmt.Println("App Name: ", appName)
	fmt.Println("App Version: ", appVersion)

	// send request
	invokeApi(appName, appVersion, content, p)

}

func invokeApi(appName string, appVersion string, buildFileContent string, path string) {
	url := fmt.Sprintf("https://helmify.me/api/cli?name=%s&version=%s", appName, appVersion)

	resp, err := http.Post(url, "application/json", s.NewReader(buildFileContent))
	if err != nil {
		log.Fatal(err)
	}
	// store file from response
	defer resp.Body.Close()

	// create new file in path
	newFilePath := filepath.Join(filepath.Dir(path), "helm.zip")
	file, err := os.Create(newFilePath)
	if err != nil {
		log.Fatal(err)
	}

	// write response to file
	_, err = io.Copy(file, resp.Body)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()
	fmt.Println("Find your Helm Chart at: ", newFilePath)

}

func getAppName(content string, path string) string {

	appName := "app"
	isXml := isXml(content)

	if isXml {
		appName = getAppNameMaven(content)
	} else {
		appName = getAppNameGradle(path)
	}

	return s.TrimSpace(appName)
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
		return s.TrimSpace(getAppVersionMaven(content))
	} else {
		return s.TrimSpace(getAppVersionGradle(content))
	}
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

	var buildFile = *buildFilePtr

	fmt.Println("Reading ", buildFile)

	content, error := ioutil.ReadFile(buildFile)
	if error != nil {
		log.Fatal(error)
	}

	str := string(content)
	return str, buildFile
}
