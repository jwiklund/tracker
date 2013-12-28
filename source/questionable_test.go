package source

import (
	"os"
	"testing"
	"time"
)

func TestParseQuestionable(t *testing.T) {
	file, err := os.Open("test/questionablecontent.net")
	if err != nil {
		t.Fatal("Could not open example file", err)
	}
	defer file.Close()
	items, err := parseSimpleItems(file, questionableUrl(), questionablePatterns())
	if err != nil {
		t.Fatal("Could not parse example file", err)
	}
	t.Logf("Result %v", items)
	if items[0].Title != "2607" {
		t.Fatal("Expected title of first one to be 2607")
	}
}

func TestQuestionableDates(t *testing.T) {
	file, err := os.Open("test/questionablecontent.net")
	if err != nil {
		t.Fatal("Could not open example file", err)
	}
	defer file.Close()
	items, err := parseSimpleItems(file, questionableUrl(), questionablePatterns())
	if err != nil {
		t.Fatal("Could not parse example file", err)
	}
	t.Logf("Result %v", items)
	_, err = time.Parse("2006-1-2 03:04:05", items[0].Date)
	if err != nil {
		t.Fatalf("Could not parse first date, %s: %s", items[0].Date, err.Error())
	}
}
