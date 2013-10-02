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
	expect := []string{"2545", "2546"}
	if fmt.Sprintf("%v", items) != fmt.Sprintf("%v", expect) {
		t.Fatalf("Got %v not %v", items, expect)
	}
}
