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
	if items[0].Title != "2546" {
		t.Fatal("Expected title of first one to be 2546")
	}
	if items[1].Title != "2545" {
		t.Fatal("Expected title of second one to be 2545")
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
	first, err := time.Parse("2006-1-2 03:04:05", items[0].Date)
	if err != nil {
		t.Fatalf("Could not parse first date, %s: %s", items[0].Date, err.Error())
	}
	second, err := time.Parse("2006-1-2 03:04:05", items[1].Date)
	if err != nil {
		t.Fatalf("Could not parse second date, %s: %s", items[1].Date, err.Error())
	}
	t.Logf("First %s, Second %s", first.Format(time.ANSIC), second.Format(time.ANSIC))
	if !second.Before(first) {
		t.Fatal("Expected first date to be one day after second date")
	}
}
