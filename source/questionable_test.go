package source

import (
	"os"
	"testing"
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
	if items[0].Date != "2013-10-02 01:01:01" {
		t.Fatal("Wrong date of first")
	}
	if items[1].Date != "2013-10-01 01:01:01" {
		t.Fatal("Wrong date of second")
	}
}
