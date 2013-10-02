package source

import (
	"fmt"
	"os"
	"testing"
)

func TestParseQuestionable(t *testing.T) {
	file, err := os.Open("test/questionablecontent.net")
	if err != nil {
		t.Fatal("Could not open example file", err)
	}
	defer file.Close()
	items, err := parseQuestionable(file)
	if err != nil {
		t.Fatal("Could not parse example file", err)
	}
	base := "http://www.questionablecontent.net/comics/"
	expect := []string{base + "2545.png", base + "2546.png"}
	if fmt.Sprintf("%v", items) != fmt.Sprintf("%v", expect) {
		t.Fatalf("Got %v not %v", items, expect)
	}
}
