package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"regexp"
	"strings"
)

func main() {
	if len(os.Args) < 3 {
		fmt.Println("Usage: go run update_initializr_yaml.go <file_path> <version> [<boot_max>]")
		os.Exit(1)
	}

	filePath := os.Args[1]
	version := os.Args[2]
	var bootMax string
	if len(os.Args) > 3 {
		bootMax = os.Args[3]
	}

	// Extract major version (e.g., "7" from "7.4.10")
	parts := strings.Split(version, ".")
	if len(parts) < 1 {
		fmt.Println("Error: Invalid version format:", version)
		os.Exit(1)
	}
	majorVersion := parts[0]
	versionPrefix := majorVersion + "."

	content, err := ioutil.ReadFile(filePath)
	if err != nil {
		fmt.Printf("Error reading file: %v\n", err)
		os.Exit(1)
	}

	contentStr := string(content)

	// Regex to find spring-cloud-gcp block (6 spaces indentation)
	blockRegex := regexp.MustCompile(`(?m)(^      spring-cloud-gcp:\n([ ]{8}.*\n)+)`)
	match := blockRegex.FindStringSubmatchIndex(contentStr)
	if match == nil {
		fmt.Println("Error: spring-cloud-gcp block not found in YAML.")
		os.Exit(1)
	}

	blockStart, blockEnd := match[0], match[1]
	bomBlock := contentStr[blockStart:blockEnd]

	// Find all mapping entries in the bomBlock
	// Format:
	//           - compatibilityRange: "[3.5.0,4.0.0)"
	//             version: 7.4.8
	// Group 1: prefix up to version
	// Group 2: version value
	versionRegex := regexp.MustCompile(`(- compatibilityRange:\s*"\[[^,]+,([^)]+)\)"\n\s+version:\s*)([^\s\n]+)`)
	matches := versionRegex.FindAllStringSubmatchIndex(bomBlock, -1)

	if len(matches) == 0 {
		fmt.Println("Error: No compatibilityRange/version mappings found in spring-cloud-gcp block.")
		os.Exit(1)
	}

	targetMatchIndex := -1
	for i, m := range matches {
		// m[6]:m[7] is the version string (Group 3)
		vStr := bomBlock[m[6]:m[7]]
		if strings.HasPrefix(vStr, versionPrefix) {
			targetMatchIndex = i
			break
		}
	}

	if targetMatchIndex == -1 {
		fmt.Printf("Error: Could not find a mapping matching major version %s (prefix: %s) in the block.\n", majorVersion, versionPrefix)
		os.Exit(1)
	}

	targetMatch := matches[targetMatchIndex]
	var replacedStr string

	if bootMax != "" {
		// Update both compatibilityRange upper bound and version
		// Reconstruct Group 1 with new bootMax
		replacedStr = bomBlock[targetMatch[2]:targetMatch[4]] + bootMax + bomBlock[targetMatch[5]:targetMatch[3]] + version
	} else {
		// Only update version
		replacedStr = bomBlock[targetMatch[2]:targetMatch[3]] + version
	}

	newBomBlock := bomBlock[:targetMatch[0]] + replacedStr + bomBlock[targetMatch[1]:]
	newContent := contentStr[:blockStart] + newBomBlock + contentStr[blockEnd:]

	err = ioutil.WriteFile(filePath, []byte(newContent), 0644)
	if err != nil {
		fmt.Printf("Error writing file: %v\n", err)
		os.Exit(1)
	}

	fmt.Println("Successfully updated application.yml")
}
