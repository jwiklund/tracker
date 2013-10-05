package source

import (
	"os"
	"testing"
)

func TestParseLatestHorrible(t *testing.T) {
	file, err := os.Open("test/horriblesubs.info_lib_latest.php")
	if err != nil {
		t.Fatal("Could not open example file", err)
	}
	defer file.Close()
	_, err = parseLatestHorrible(file)
	if err != nil {
		t.Fatal("Could not parse latest horrible", err)
	}
}
