package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"regexp"
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
	newBomBlock := bomBlock

	if bootMax != "" {
		// Update compatibility range upper bound and version of the last mapping
		// Target format:
		//           - compatibilityRange: "[4.0.0,4.1.0-M1)"
		//             version: 8.0.3
		// Submatch groups:
		// Group 1: (- compatibilityRange:\s*"\[[^,]+,)([^)]+)(\)"\n\s+version:\s*)([^\s\n]+)
		rangeRegex := regexp.MustCompile(`(- compatibilityRange:\s*"\[[^,]+,)([^)]+)(\)"\n\s+version:\s*)([^\s\n]+)`)
		matches := rangeRegex.FindAllStringSubmatchIndex(bomBlock, -1)
		if len(matches) > 0 {
			lastMatch := matches[len(matches)-1]
			// lastMatch indexes: [start, end, g1_start, g1_end, g2_start, g2_end, g3_start, g3_end, g4_start, g4_end]
			g1 := bomBlock[lastMatch[2]:lastMatch[3]]
			g3 := bomBlock[lastMatch[6]:lastMatch[7]]
			replacedStr := g1 + bootMax + g3 + version
			
			matchStart, matchEnd := lastMatch[0], lastMatch[1]
			newBomBlock = bomBlock[:matchStart] + replacedStr + bomBlock[matchEnd:]
		} else {
			fmt.Println("Error: Could not find compatibilityRange mappings in spring-cloud-gcp block.")
			os.Exit(1)
		}
	} else {
		// Just update version of the last mapping
		// Target:
		//           - compatibilityRange: "[4.0.0,4.1.0-M1)"
		//             version: 8.0.3
		versionRegex := regexp.MustCompile(`(- compatibilityRange:\s*"\[[^,]+,[^)]+\)"\n\s+version:\s*)([^\s\n]+)`)
		matches := versionRegex.FindAllStringSubmatchIndex(bomBlock, -1)
		if len(matches) > 0 {
			lastMatch := matches[len(matches)-1]
			g1 := bomBlock[lastMatch[2]:lastMatch[3]]
			replacedStr := g1 + version
			
			matchStart, matchEnd := lastMatch[0], lastMatch[1]
			newBomBlock = bomBlock[:matchStart] + replacedStr + bomBlock[matchEnd:]
		} else {
			fmt.Println("Error: Could not find version mappings in spring-cloud-gcp block.")
			os.Exit(1)
		}
	}

	newContent := contentStr[:blockStart] + newBomBlock + contentStr[blockEnd:]

	err = ioutil.WriteFile(filePath, []byte(newContent), 0644)
	if err != nil {
		fmt.Printf("Error writing file: %v\n", err)
		os.Exit(1)
	}

	fmt.Println("Successfully updated application.yml")
}
